/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.upgrading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.bootstrap.ClassMetaLibrary;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.metadata.ComponentFieldMetadata;
import org.terasology.entitySystem.metadata.ComponentLibrary;
import org.terasology.entitySystem.metadata.ComponentMetadata;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles applying an upgrade to a component
 */
@Share(UpgradingSystem.class)
@RegisterSystem
public class UpgradingSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(UpgradingSystem.class);
    @In
    private EntityManager entityManager;
    @In
    private ClassMetaLibrary classMetaLibrary;
    private ComponentLibrary componentLibrary;
    private Map<Class, BaseParser> parserMap = new HashMap<>();

    @Override
    public void postBegin() {
        componentLibrary = entityManager.getComponentLibrary();

        /* Build a list of all the parsers */
        for (Class<? extends BaseParser> parserClass : classMetaLibrary.getSubtypesOf(BaseParser.class)) {
            try {
                BaseParser parser = parserClass.newInstance();
                parserMap.put(parser.getComponentClass(), parser);
            } catch (IllegalAccessException | InstantiationException e) {
                if (parserClass != DefaultParser.class) {
                    throw new IllegalArgumentException("Could not create parser of type " + parserClass.getSimpleName());
                }
            }
        }
    }

    /**
     * Applies a given upgrade to the entity.
     *
     * @param component The component to apply the upgrade onto
     * @param upgrade   The upgrade to apply
     * @see UpgradeInfo
     */
    public void applyUpgrade(Component component, UpgradeInfo upgrade) {
        if (component == null || upgrade == null) {
            return;
        }
        ComponentMetadata<? extends Component> componentMeta = componentLibrary.getMetadata(component);

        /* Apply upgrade for each field */
        for (Map.Entry<String, Number> entry : upgrade.getValues().entrySet()) {
            ComponentFieldMetadata<?, ?> fieldMeta = componentMeta.getField(entry.getKey());
            setField(fieldMeta, component, entry.getValue());
        }
    }

    /**
     * Gets the instance of the component to upgrade
     *
     * @param entity            The entity to get the component from
     * @param upgradesComponent The component containing the upgrade data
     * @return The component that the upgrades should be applied to.
     */
    public Component getComponentToUpgrade(EntityRef entity, BlockUpgradesComponent upgradesComponent) {
        if (upgradesComponent == null || entity == EntityRef.NULL) {
            return null;
        }
        /* Get needed data */
        ComponentMetadata<?> componentMeta = componentLibrary.resolve(upgradesComponent.getComponentName());
        if (componentMeta == null) {
            throw new IllegalArgumentException("Cannot upgrade entity as "
                    + upgradesComponent.getComponentName()
                    + " is not a valid component.");
        }
        Component component = entity.getComponent(componentMeta.getType());
        if (component == null) {
            throw new IllegalArgumentException("Cannot upgrade entity as it lacks any "
                    + upgradesComponent.getComponentName()
                    + " to upgrade");
        }
        return component;
    }

    /**
     * Sets a Number field to the given value.
     * Requires the field to be of a primitive number type.
     *
     * @param field     The field to set
     * @param component The component containing the field to set
     * @param value     The value to set the field to
     */
    private void setField(ComponentFieldMetadata field, Component component, Number value) {
        switch (field.getType().getSimpleName()) {
            case "int":
                field.setValue(component, (int) field.getValue(component) + value.intValue());
                break;
            case "double":
                field.setValue(component, (double) field.getValue(component) + value.doubleValue());
                break;
            case "float":
                field.setValue(component, (float) field.getValue(component) + value.floatValue());
                break;
            case "long":
                field.setValue(component, (long) field.getValue(component) + value.longValue());
                break;
            case "short":
                field.setValue(component, (short) field.getValue(component) + value.shortValue());
                break;
            case "byte":
                field.setValue(component, (byte) field.getValue(component) + value.byteValue());
                break;
            default:
                throw new IllegalArgumentException("Can't set field of type: "
                        + field.getField().getGenericType().getTypeName()
                        + ". Type must be a Number primitive");
        }
    }


    public <T extends Component> List<String> getComponentFields(T component) {
        return getComponentFields(component, true);
    }

    /**
     * Get a list of all the fields on the component to display in UI.
     * This list is sorted alphabetically.
     *
     * @param component The component to get the fields from
     * @param formatted Flag indicating if the returned fields should use the ui name or the field name
     * @param <T>       The type of the component
     * @return A list of all the fields to display in the component.
     */
    private <T extends Component> List<String> getComponentFields(T component, boolean formatted) {
        if (component == null) {
            return Collections.emptyList();
        }
        BaseParser parser = parserMap.getOrDefault(component.getClass(), new DefaultParser(component));
        Map<String, String> fieldMap = parser.getFields();
        List<String> keys = fieldMap.keySet().stream().sorted().collect(Collectors.toList());
        if (formatted) {
            List<String> values = new ArrayList<>();
            keys.forEach(value -> values.add(fieldMap.get(value)));
            return values;
        } else {
            return keys;
        }
    }

    /**
     * Get a list of all the values of the component to display in the UI.
     * The values in this list are ordered the same as those in {@link #getComponentFields(Component, boolean)}.
     *
     * @param component The component to get the values from
     * @param <T>       The type of the component
     * @return An ordered list of all the values to display from the component.
     */
    public <T extends Component> List<String> getComponentValues(T component) {
        if (component == null) {
            return Collections.emptyList();
        }

        List<String> fields = getComponentFields(component, false);
        List<String> values = new ArrayList<>(fields.size());
        ComponentMetadata<T> metadata = componentLibrary.getMetadata(component);
        BaseParser parser = parserMap.getOrDefault(component.getClass(), new DefaultParser());

        for (String field : fields) {
            ComponentFieldMetadata<T, ?> fieldMetadata = metadata.getField(field);
            values.add(
                    tryParseValue(
                            parser,
                            (Number) fieldMetadata.getValue(component),
                            field,
                            fieldMetadata.getType(),
                            false));
        }

        return values;
    }

    /**
     * Returns a list of all the values from an upgrade to display.
     * This list is ordered the same as the values from {@link #getComponentFields(Component, boolean)}.
     * If a field does not have an upgrade value, then a blank string is used.
     *
     * @param component   The component the upgrade will be applied to
     * @param upgradeInfo The upgrade to display
     * @param <T>         The type of the component
     * @return An ordered list of all the values to display.
     */
    public <T extends Component> List<String> getComponentUpgrades(T component, UpgradeInfo upgradeInfo) {
        if (component == null) {
            return Collections.emptyList();
        }

        List<String> fields = getComponentFields(component, false);
        List<String> upgrades = new ArrayList<>(fields.size());

        if (upgradeInfo == null) {
            return Collections.nCopies(fields.size(), "");
        }

        ComponentMetadata<T> metadata = componentLibrary.getMetadata(component);
        BaseParser parser = parserMap.getOrDefault(component.getClass(), new DefaultParser());
        for (String field : fields) {
            ComponentFieldMetadata<T, ?> fieldMetadata = metadata.getField(field);
            Number upgradeValue = upgradeInfo.getValues().getOrDefault(field, null);
            if (upgradeValue == null) {
                upgrades.add("");
            } else {
                upgrades.add(
                        tryParseValue(
                                parser,
                                upgradeValue,
                                field,
                                fieldMetadata.getType(),
                                true));
            }
        }

        return upgrades;
    }

    /**
     * Attempts to parse a value into a human readable format.
     * <p>
     * First tries to call a method on the parser with the following properties:
     * <p>
     * 1. Same name as the field
     * 2. Return type of string
     * 3. First parameter is a boolean
     * 4. Second parameter is the same type as the field.
     * <p>
     * If it cannot find an appropriate method, it will instead use the
     * {@link BaseParser#handleUpgrade(String, Object)} or {@link BaseParser#handleField(String, Object)} methods.
     * By default these simply call {@code String.valueOf()} on the value.
     *
     * @param parser    The parser to use
     * @param value     The value to convert
     * @param fieldName The name of the field being converted
     * @param fieldType The type of the field being converted
     * @param isUpgrade True if the value is an upgrade value, false otherwise
     * @return The human readable version of the value
     */
    private String tryParseValue(BaseParser parser, Number value, String fieldName, Class<?> fieldType, boolean isUpgrade) {
        MethodHandle method = getHandleForMethod(parser, fieldName, fieldType);
        if (method == null) {
            return parseWithBackup(parser, fieldName, value, isUpgrade);
        } else {
            try {
                return invokeWithType(method, isUpgrade, value, fieldType).toString();
            } catch (Throwable throwable) {
                logger.error(String.format("Unable to call method for %s on %s. It threw %s", fieldName, parser.getClass().getSimpleName(), throwable.toString()));
                return parseWithBackup(parser, fieldName, value, isUpgrade);
            }
        }
    }

    /**
     * Wrapper handler to invoke a method with the correct primitive number type.
     *
     * @param method    The method to invoke
     * @param isUpgrade True if the value is an upgrade value, false otherwise
     * @param value     The value to pass to the method being invoked
     * @param type      The type to convert the value to. Must be a primitive number.
     * @return The value returned by the invocation
     * @throws Throwable Any error returned by the invocation
     */
    private Object invokeWithType(MethodHandle method, boolean isUpgrade, Number value, Class<?> type) throws Throwable {
        switch (type.getSimpleName()) {
            case "int":
                return method.invoke(isUpgrade, value.intValue());
            case "float":
                return method.invoke(isUpgrade, value.floatValue());
            case "long":
                return method.invoke(isUpgrade, value.longValue());
            case "double":
                return method.invoke(isUpgrade, value.doubleValue());
            case "short":
                return method.invoke(isUpgrade, value.shortValue());
            case "byte":
                return method.invoke(isUpgrade, value.byteValue());
            default:
                throw new IllegalArgumentException("Cannot convert " + value + " as it is of the type " + type.getSimpleName());
        }
    }

    /**
     * The backup parser. Calls either of
     * {@link BaseParser#handleUpgrade(String, Object)} or {@link BaseParser#handleField(String, Object)}
     * on the target parser.
     *
     * @param parser    The parser to use to do the converting..
     * @param fieldName The name of the field being converted.
     * @param value     The value to convert
     * @param isUpgrade True if the value is an upgrade value, false otherwise.
     * @return The converted string.
     */
    private String parseWithBackup(BaseParser parser, String fieldName, Object value, boolean isUpgrade) {
        if (!isUpgrade) {
            return parser.handleField(fieldName, value);
        } else {
            return parser.handleUpgrade(fieldName, value);
        }
    }

    /**
     * Gets the method handler to call on the parser in order to convert the value.
     *
     * @param parser    The parser to search on
     * @param name      The name of the field
     * @param parameter The type of the value to convert
     * @return The method handler if there is one, null otherwise.
     */
    private MethodHandle getHandleForMethod(BaseParser parser, String name, Class<?> parameter) {
        MethodType methodType = MethodType.methodType(String.class, boolean.class, parameter);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.bind(parser, name, methodType);
        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            /* We don't do anything. Instead we will try the default parser. */
            return null;
        }
    }


    /**
     * A default implementation of the parsers.
     * Returns all the fields on a component, and does not apply any formatting to values.
     *
     * @see BaseParser
     */
    public class DefaultParser implements BaseParser {
        private Component component;

        public DefaultParser() {

        }

        public DefaultParser(Component component) {
            this.component = component;
        }

        @Override
        public Class<? extends Component> getComponentClass() {
            return Component.class;
        }

        @Override
        public Map<String, String> getFields() {
            Map<String, String> result = new HashMap<>();
            ComponentMetadata<?> metadata = componentLibrary.getMetadata(component);
            for (ComponentFieldMetadata<?, ?> field : metadata.getFields()) {
                result.put(field.getName(), field.getName());
            }
            return result;
        }

        @Override
        public String handleField(String field, Object value) {
            return String.valueOf(value);
        }
    }
}
