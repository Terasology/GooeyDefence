## Effectors

Effectors are part of a [Tower](towers.md) structure.

Effectors are much like [Targeters](targeter.md) in that they have both a component and a system part. Each of these has a few more nuances to how they are set up however, that makes an effector the hardest of the three to extend.

See [Tower Block Base](tower-block-base.md) for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

### Component

The component of an effector is a class that extends [`TowerEffector`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/components/TowerEffector.html). The component class doesn't need to extend `TowerEffector` directly, so long as one of the superclasses do. This class only provides the single field, `drain`, which is the amount of power the effector needs.

The effector has two abstract methods that need to be implemented. The first of these is `getEffectCount`, which should return an [`EffectCount` enum](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/EffectCount.html). This will represent the number of times that an effect should be applied. Either every time the tower shoots or only on the initial attack per enemy.

The second method, `EffectDuration`, returns an [`EffectDuration` enum](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/EffectDuration.html). This controls when, if at all, an effect should be called to be removed. The options are for the effect to not have a duration, only last whilst the enemy is targeted, or to last for an unspecified amount of time.
The values of these will affect how often and when the events detailed in the next section are sent.

Here is an example of `DamageEffector`, minus javadoc, which is the simplest of the effectors:

    public class DamageEffectorComponent extends TowerEffector {
        public int damage;
        @Override
        public EffectCount getEffectCount() {
            return EffectCount.PER_SHOT;
        }
        @Override
        public EffectDuration getEffectDuration() {
            return EffectDuration.INSTANT;
        }
    }

### System

The system for an effector doesn't need to implement or extend anything beyond that of a normal system. All of the interaction is done via two events, [`ApplyEffectEvent`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/events/ApplyEffectEvent.html) and [`RemoveEffectEvent`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/events/RemoveEffectEvent.html).

The first event is sent to apply an effect to the single enemy. A key feature of the event is that it will also contain a multiplier value that the effector should use to moderate its effect. For instance, multiplying its damage by it. The event is sent once per enemy, so it will always only have a single target.

The second event is sent to remove an effect from the enemy. It is only sent if the duration of the effect is set to `EffectDuration.LASTING`. In the case of `EffectDuration.PERMANENT` the system is expected to remove the effect when appropriate itself. This event also has a multiplier, and it is guaranteed to be the same value used to apply the effect. This lets it be used like in `IceEffectorSystem` below to both apply and remove the effect.

The `IceEffectorSystem`, minus javadoc, is shown as it has simple examples for both events:

    @RegisterSystem
    public class IceEffectorSystem extends BaseComponentSystem {
        @In
        private InWorldRenderer inWorldRenderer;
        @ReceiveEvent
        public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, IceEffectorComponent component) {
            EntityRef enemy = event.getTarget();
            MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
            double reducedSpeed = movementComponent.speed * component.slow * event.getDamageMultiplier();
            movementComponent.speed = (float) reducedSpeed;
            inWorldRenderer.addParticleEffect(enemy, DefenceUris.ICE_PARTICLES);
        }
    
        @ReceiveEvent
        public void onRemoveEffect(RemoveEffectEvent event, EntityRef entity, IceEffectorComponent component) {
            EntityRef enemy = event.getTarget();
            MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
            movementComponent.speed = movementComponent.speed / (component.slow * event.getMultiplier());
            inWorldRenderer.removeParticleEffect(enemy, DefenceUris.ICE_PARTICLES);
        }
    }

### Current Implementations


| Effector       | Effect                                                                                                                                                                                                                    | Files                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Tile                                                                                                                                                 |
|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| DamageEffector | Simply directly deals damage to the target. Has no other side effect.                                                                                                                                                     | [blocks/effectors/DamageEffector.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/effectors/DamageEffector.prefab), [DamageEffectorComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/DamageEffectorComponent.html), [DamageEffectorSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/DamageEffectorSystem.html) | ![Damage Effector tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/effectors/DamageEffector.png) |
| FireEffector   | Ignites the target. Burning targets have a chance to set nearby targets on fire, thus spreading the effect.                                                                                                               | [blocks/effectors/FireEffector.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/effectors/FireEffector.prefab), [FireEffectorComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/FireEffectorComponent.html), [FireEffectorSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/FireEffectorSystem.html)             | ![Fire Effector tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/effectors/FireEffector.png)     |
| IceEffector    | Slows down the target by freezing them. The freezing doesn't do any damage                                                                                                                                                | [blocks/effectors/IceEffector.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/effectors/IceEffector.prefab), [IceEffectorComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/IceEffectorComponent.html), [IceEffectorSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/IceEffectorSystem.html)                   | ![Ice Effector tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/effectors/IceEffector.png)       |
| PoisonEffector | Deals an initial damage and then poisons the target, dealing more damage over time. Being hit again by the same effector will cause it to refresh the duration. An enemy can be poisoned by multiple different effectors. | [blocks/effectors/PoisonEffector.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/effectors/PoisonEffector.prefab), [PoisonEffectorComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/PoisonEffectorComponent.html), [PoisonEffectorSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/PoisonEffectorSystem.html) | ![Poison Effector tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/effectors/PoisonEffector.png) |
| StunEffector   | Briefly stuns the target. Stops them from moving for the duration of the stun. Does not deal damage to the target                                                                                                         | [blocks/effectors/StunEffector.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/effectors/StunEffector.prefab), [StunEffectorComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/StunEffectorComponent.html), [StunEffectorSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/effectors/StunEffectorSystem.html)             | ![Stun Effector tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/effectors/StunEffector.png)     |
