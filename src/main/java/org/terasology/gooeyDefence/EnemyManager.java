// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.gooeyDefence;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.economy.ValueComponent;
import org.terasology.gooeyDefence.events.OnEntrancePathCalculated;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.health.events.EntityDeathEvent;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.gooeyDefence.movement.components.BlankPathComponent;
import org.terasology.gooeyDefence.movement.components.EntrancePathComponent;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.movement.components.PathComponent;
import org.terasology.gooeyDefence.movement.events.ReachedGoalEvent;
import org.terasology.gooeyDefence.movement.events.RepathEnemyRequest;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles all enemy based actions.
 */
@Share(EnemyManager.class)
@RegisterSystem
public class EnemyManager extends BaseComponentSystem {

    private final Set<EntityRef> enemies = new HashSet<>();

    @In
    private EntityManager entityManager;
    @In
    private PathfindingManager pathfindingManager;
    @In
    private DelayManager delayManager;

    /**
     * Removes all the existing enemies.
     * <p>
     * Sent when the field is to be reset
     *
     * @see OnFieldReset
     */
    @ReceiveEvent
    public void onFieldReset(OnFieldReset event, EntityRef entity) {
        enemies.forEach(EntityRef::destroy);
        enemies.clear();
    }

    /**
     * Called when the field is activated. Clears the enemy store and re-scans for any enemies.
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        enemies.clear();
        entityManager.getEntitiesWith(GooeyComponent.class).forEach(enemies::add);
        enemies.stream().filter(enemy -> enemy.hasComponent(EntrancePathComponent.class))
                .forEach(enemy -> enemy.getComponent(EntrancePathComponent.class).setPathManager(pathfindingManager));
    }

    /**
     * Called when the a path is changed.
     *
     * @see OnEntrancePathCalculated
     */
    @ReceiveEvent
    public void onPathChanged(OnEntrancePathCalculated event, EntityRef shrineEntity) {
        if (DefenceField.fieldActivated) {
            for (EntityRef enemy : enemies) {
                /* Firstly check if the enemy is on an unchanged path */
                if (enemy.hasComponent(EntrancePathComponent.class)) {
                    if (enemy.getComponent(EntrancePathComponent.class).getEntranceId() != event.getPathId()) {
                        continue;
                    }
                }

                /* Check if the goal is on the new path */
                MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
                Vector3i goal = new Vector3i(movementComponent.goal);
                List<Vector3i> newPath = event.getNewPath();

                enemy.removeComponent(DefenceField.getComponentExtending(enemy, PathComponent.class).getClass());

                if (newPath.contains(goal)) {
                    /* Add a entrance component starting at the given position */
                    EntrancePathComponent entranceComponent = new EntrancePathComponent(
                            event.getPathId(),
                            pathfindingManager,
                            newPath.indexOf(goal));
                    enemy.addComponent(entranceComponent);
                } else {
                    /* Enemy isn't on the new path, so we have to calculate it's own path. */
                    enemy.addComponent(new BlankPathComponent(movementComponent.goal));
                    enemy.send(new RepathEnemyRequest());
                }
            }
        }
    }

    /**
     * Called when an entity reaches zero health. Filters on {@link GooeyComponent}
     *
     * @see EntityDeathEvent
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void onEntityDeath(EntityDeathEvent event, EntityRef entity, GooeyComponent component) {
        dropMoney(entity);
        destroyEnemy(entity);
    }

    /**
     * Called when an enemy reaches it's movement goal. Set the next goal and consumes the event.
     * <p>
     * Filters on {@link GooeyComponent}
     *
     * @see ReachedGoalEvent
     */
    @ReceiveEvent
    public void onReachedGoal(ReachedGoalEvent event, EntityRef entity, GooeyComponent gooeyComponent) {
        event.consume();
        PathComponent pathComponent = DefenceField.getComponentExtending(entity, PathComponent.class);
        if (pathComponent.atEnd()) {
            DefenceField.getShrineEntity().send(new DamageEntityEvent(gooeyComponent.damage));
            destroyEnemy(entity);
        } else {
            Vector3f prevGoal = pathComponent.getGoal();
            pathComponent.nextStep();
            Vector3f faceTowards = new Vector3f(pathComponent.getGoal());
            faceTowards.sub(prevGoal);
            if (faceTowards.y() == 0) {
                Vector3f perpendicular = new Vector3f();
                perpendicular.cross(Vector3f.south(), faceTowards);
                Quat4f entityRot;
                if (perpendicular.x() == 0.0f && perpendicular.y() == 0.0f && perpendicular.z() == 0.0f) {
                    if (faceTowards.z < 0)
                        entityRot = new Quat4f(Vector3f.up(), 0.0f);
                    else
                        entityRot = new Quat4f(Vector3f.up(), (float) Math.PI);
                } else {
                    double angle = Math.acos(faceTowards.normalize().dot(Vector3f.south()));
                    entityRot = new Quat4f(perpendicular, (float) angle);
                }
                LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
                locationComponent.setLocalRotation(entityRot);
                entity.saveComponent(locationComponent);
            }
            MovementComponent component = entity.getComponent(MovementComponent.class);
            component.goal = pathComponent.getGoal();
        }
    }

    /**
     * Spawns an enemy at the given entrance. Also begins it travelling down the path.
     *
     * @param entranceNumber The entrance to spawn at
     * @param prefab The prefab of the enemy to spawn in.
     */
    public void spawnEnemy(int entranceNumber, String prefab) {
        if (!DefenceField.fieldActivated) {
            return;
        }

        EntityRef entity = entityManager.create(prefab, DefenceField.entrancePos(entranceNumber).toVector3f());

        /* Setup pathfinding component */
        EntrancePathComponent component = new EntrancePathComponent(entranceNumber, pathfindingManager);
        entity.addComponent(component);
        /* Setup movement component */
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        movementComponent.goal = component.getGoal();

        enemies.add(entity);
    }

    /**
     * Destroys an enemy, ensuring that all references to it in the system are handled.
     *
     * @param enemy The enemy to destroy
     */
    private void destroyEnemy(EntityRef enemy) {
        enemies.remove(enemy);
        enemy.destroy();
    }

    /**
     * Drops the amount of money an enemy had. Drops money in 5 unit increments. If the enemy has no value, then nothing
     * is dropped
     *
     * @param enemy The enemy to drop money for
     */
    private void dropMoney(EntityRef enemy) {
        if (enemy.hasComponent(ValueComponent.class)) {
            Vector3f location = enemy.getComponent(LocationComponent.class).getWorldPosition();
            int value = enemy.getComponent(ValueComponent.class).value;

            /* Drop the money in instances of 5 */
            while (value >= 5) {
                EntityRef money = entityManager.create(DefenceUris.MONEY_ITEM);
                money.getComponent(ValueComponent.class).value = 5;
                money.send(new DropItemEvent(location));
                value -= 5;
            }
            /* Drop whatever is left, if any */
            if (value > 0) {
                EntityRef money = entityManager.create(DefenceUris.MONEY_ITEM);
                money.getComponent(ValueComponent.class).value = value;
                money.send(new DropItemEvent(location));
            }
        }
    }

    /**
     * Obtain all the enemies that are within range of the given position.
     *
     * @param pos The position to look for
     * @param range The range to search in.
     * @return A set of all enemies found within this range.
     */
    public Set<EntityRef> getEnemiesInRange(Vector3f pos, float range) {
        float rangeSqr = range * range;
        Set<EntityRef> result = new HashSet<>();
        for (EntityRef enemy : enemies) {
            Vector3f enemyPos = enemy.getComponent(LocationComponent.class).getWorldPosition();
            if (enemyPos.distanceSquared(pos) <= rangeSqr) {
                result.add(enemy);
            }
        }
        return result;
    }
}
