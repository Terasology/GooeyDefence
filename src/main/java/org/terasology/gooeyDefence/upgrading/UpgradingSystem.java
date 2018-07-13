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
     * @return The component that this component should be applied to.
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
        logger.info((field.getType() == int.class) + "");
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
                //TODO: work out what to throw here
                throw new Error("Can't set field of type: "
                        + field.getField().getGenericType().getTypeName()
                        + ". Type must be a Number primitive");
        }
    }


    public <T extends Component> List<String> getComponentFields(T component) {
        return getComponentFields(component, true);
    }

    private <T extends Component> List<String> getComponentFields(T component, boolean formatted) {
        if (component == null) {
            return Collections.emptyList();
        }
        BaseParser parser = parserMap.getOrDefault(component.getClass(), new DefaultParser(component));
        return (formatted ? parser.getFields().values()
                : parser.getFields().keySet())
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get a mapping between all the fields and their values for a component.
     *
     * @param component The component to get all fields on
     * @return All the fields and their values, as strings.
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
            values.add(parser.handleField(field, fieldMetadata.getValue(component)));
        }

        return values;
    }

    public <T extends Component> List<String> getComponentUpgrades(T component, UpgradeInfo upgradeInfo) {
        if (component == null) {
            return Collections.emptyList();
        }

        List<String> fields = getComponentFields(component, false);
        List<String> upgrades = new ArrayList<>(fields.size());
        BaseParser parser = parserMap.getOrDefault(component.getClass(), new DefaultParser());

        if (upgradeInfo == null) {
            return Collections.nCopies(fields.size(), "");
        }

        for (String field : fields) {
            Number upgradeValue = upgradeInfo.getValues().getOrDefault(field, null);
            if (upgradeValue == null) {
                upgrades.add("");
            } else {
                upgrades.add(parser.handleUpgrade(field, upgradeValue));
            }
        }

        return upgrades;
    }

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
