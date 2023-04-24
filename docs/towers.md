# The Towers
Gooey Defence is a tower defence gameplay template. This means that one of the most important features of the module is the towers that give the genre its name. In Gooey Defence, these are freeform multiblock structures, with a minimum of three parts. Together these parts allow players to build a wide variety of towers.

The first part of a tower is the core. These add power to the other two block types.
Next are targeters, which select and attack the enemies. They don't actually apply any damage or effects however. The last are effectors which complement the targeters by applying effects and damage to the targeted enemies.

All three types are needed for a tower to work. Lack a core, and the other two won't work. Lack a targeter and the effector won't have any enemies to affect. Don't have an effector, and the attacks won't do anything.
Additionally you can, and indeed are encouraged to, have multiple variants of both Targeters and Effectors on a single tower. For instance, adding an Ice Effector and Damage Effector along with a Chain Targeter and Aoe Targeter will result in each attack dealing damage and applying a slowing effect. Additionally all enemies in range will be attacked, with some being attacked again by the Chain Targeter.
In this way complex towers can be built with multiple different focuses.

For a block to be considered part of a tower, all it needs to be doing is touch other tower blocks. In order to allow players to spread out towers there is a plain block which provides no effect, but counts as part of the tower.

More detailed information on how towers work in game is found in the player guide, which is located in the readme

### Extending the system

The main way to extend the tower system is to add additional effector, core or targeter blocks. At present a block cannot implement more than one of these types, with the behaviour being undefined if you try. You could add additional block types, however they would end up being considered as 'plain' blocks and not treated specially. There is currently no plans to add support for custom block types, however we welcome PR's adding this.

Additionally, all tower blocks share the same base, which is detailed on the Tower Block Base page

## Tower Block Base

All of the 4 tower block types, Core, Effector, Targeter or Plain, have the exact same base structure to creating a new block. The two parts to a new tower block are the block and the prefab.
It is worth noting that you can add to the base outlined on this page without affecting the functionality of the block. For instance, although the guide doesn't mention it you can use block families, and any other feature of the `.block` format. Likewise, your prefab can contain components beyond what is listed here, allowing you to augment the blocks with features from other modules or the engine.

### The Prefab

(`.prefab`)

This is the section of the block that has the more stringent requirements. The first and most important requirement is that the `alwaysRelevant` flag is set to `true`. This ensures that the entity will not be destroyed at any point allowing the changes made to it to 'stick'.
Next is the component that allows it to be added to a tower, `TowerMultiBlock`. This component has no fields, it just needs to be present.
Similar to this `DestructibleBlock` needs to be present. It allows for the block to be destroyed and picked up by the player. If this is not present, trying to break the block will do nothing.

The last two components are `Purchasable` and `Value`. The first of these makes the block appear in the shop, with the cost being given by the `cost` field. If the field is left blank then the `Value` component is used as a fallback. The second component determines the amount of money given when the tower block is broken as well as a fallback for the cost when buying it from the shop.
Neither of these two blocks is strictly needed. If you intend to provide an alternative method of obtaining your block then feel free to drop `Purchasable`. Likewise if you intend to allow players to break and pick up your block then leave off the `Value` component.

A prefab for a block called "`TowerBlock`" with all the above components would look something like this:
   
     ...
    "alwaysRelevant": true,
    "DisplayName": {
        "name": "Tower Block",
        "description": "A brief, few sentence description of the block goes here.\nIt will be displayed in the UI where possible."
    },
    "TowerMultiBlock": {},
    "DestructibleBlock": {},
    "Purchasable": {
        // No value set. The cost will be determined by the Value component
    },
    "Value": {
        "value": 0
    },
    ...

### The Block

(`.block`)

The requirements for the block are very simple. All it needs to have is the `prefab` field set to the prefab detailed above. Assuming the prefab was called "`TowerBlock`" like above and the module was called "`ModuleName`" then it would look like this:

    ...
    "entity": {
        "prefab":"ModuleName:TowerBlock"
    },
    ...

It is not strictly required, but strongly recommended, to add a display name to the block as well. This is the name that will be used in all UI to refer to the block. The default is the full name of the block, ie for a block called "`TowerBlock.block`" in module "`ModuleName`" then the displayed name will be "`ModuleName:TowerBlock`". That would look something like this:

    ...
    "displayName": "Tower Block",
    ...

### Template

Both a template bock and a template prefab are provided. These don't need to be used and can be used by either extending them using the `parent` field or by simply copy pasting their contents. They are both linked and laid out full here for you.

[blocks/towerBlocks/Plain.block](https://github.com/Terasology/GooeyDefence/tree/master/assets/blocks/towerBlocks/Plain.block)

    {
        "tile": "GooeyDefence:Plain",
        "entity": {
            "prefab":"GooeyDefence:PlainBlock"
        },
        "translucent":true
    }

[prefabs/blocks/PlainBlockblock.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/PlainBlock.prefab)

    {
        "alwaysRelevant": true,
        //This does lack a DisplayName component but it would ideally be included.
        "TowerMultiBlock": {},
        "DestructibleBlock": {},
        "Purchasable": {
        },
        "Value": {
            "value": 0
        }
    }

## Cores

Cores comprise two main parts, the block and the component. The component section is the only section that provides functionality and it simply needs to be on a valid tower block to function.

See Tower Block Base for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

### Core Component

To add a new Core, you firstly need to add a new Core Component. This is a Component that extends the `TowerCore` class. As this class already implements the `Component` interface, yours does not need to (but will still function if it is included).

The `TowerCore` class only has one field, `power`. This is the amount of power your core will produce. It can be altered by other systems, such as the Upgrade System to change the amount of power provided based on outside factors.

This means that a new core component called `NewCore` would look like this:

    public class NewCore extends TowerCore {
    }

As you can see, there are no required fields to be added. If you intend to overwrite the default value however, either do this via prefabs or by setting it in the default constructor like so:

    public NewCore() {
        power = 10;
    }

### Current Implementations
* Core: the most basic implementation, adding no additional features.
  * Files: [prefabs/blocks/Core.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/Core.prefab), [CoreComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/cores/CoreComponent.html)

## Effectors

Effectors are much like Targeters in that they have both a component and a system part. Each of these has a few more nuances to how they are set up however, that makes an effector the hardest of the three to extend.

See Tower Block Base for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

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

## Targeters

Targeters have two components on top of the standard block and prefab requirements. These are the component that will contain the configurable values, and the system used to implement the logic. Both of these are vital for a targeter to function properly.

See Tower Block Base for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

### Component

First is the component. This should be a class that extends the [TowerTargeter](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/components/TowerTargeter.html) class. It can either do this directly, like `SingleTargeter` and `AoeTargeter` or it can extend a class that in turn extends `TowerTargeter`. This is what classes like `MissileTargeter` do as it allows you to use fields from the other subclasses.

TowerTargeter is the base class for all Targeters and has a number of default fields on it. Namely these are:

* `drain`: How much power this Targeter will take.
* `attackSpeed`: How long between attacks. Given in milliseconds, eg 500ms means it will attack twice per second.
* `range`: The radius of this Targeter's range. Given in blocks.

Additionally there is an abstract method called `getMultiplier` that the component will need to implement. This method returns a float that is passed to the Effectors each attack. It should be used as a balancing tool to make sure that the different targeters are all correctly balanced.

Of particular note is the `SingleTargeter` as this is a base class for all bar one implementations. It has an additional field called `selectionMethod`. This is a value from the [`SelectionMethod` enum](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/SelectionMethod.html) that provides the player with control over how the targeter selects its targets.

Here example of what a targeter called `HeightTargeter` that added a float field `minHeight`, and extended SingleTargeter might look:

    public class SingleTargeterComponent extends SingleTargeterComponent {
        public float minHeight = 0.5f;
        @Override
        public float getMultiplier() {
            return 1.2f;
        }
    }

### System

The second half of this is the system. This will implement the actual selection of enemies using the component detailed in the prior section. Unlike the component, this system does not need to extend any special classes and can be a plain system. It can even have other responsibilities and functionality just like all systems. It is recommended to extend the [`BaseTargeterSystem`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/BaseTargeterSystem.html) however, for reasons explained below. When a targeter is called to select its targets, the [`SelectEnemiesEvent`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/events/SelectEnemiesEvent.html) is sent. The event handler should simply use the `addToList` methods to add enemies to the event. Managers such as [`EnemyManager`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/EnemyManager.html) have some useful methods which will help in selecting enemies. Additionally the system, `BaseTargeterSystem`, has a number of useful helper methods which can simplify the process.
Any visual effects should also be triggered in this event, helper methods for which are located in [`InWorldRenderer`](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/visuals/InWorldRenderer.html).

As an example here is the entirety of `AoeTargeterSystem`, minus javadoc, which serves as one of the simpler implementations:

    @RegisterSystem
    public class AoeTargeterSystem extends BaseTargeterSystem {
    
        @In
        private EnemyManager enemyManager;
        @In
        private InWorldRenderer inWorldRenderer;
    
        @ReceiveEvent
        public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, AoeTargeterComponent targeterComponent) {
            Set<EntityRef> targets = enemyManager.getEnemiesInRange(locationComponent.getWorldPosition(), targeterComponent.range);
            event.addToList(targets);
            if (!targets.isEmpty()) {
                inWorldRenderer.displayExpandingSphere(locationComponent.getWorldPosition(), (float) targeterComponent.attackSpeed / 1000, targeterComponent.range * 2 + 1);
            }
        }
    } 

### Current Implementations

| Targeter        | Selection Strategy                                                                                                                                                       | Files                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | Tile                                                                                                                                                  |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| AoeTargeter     | This targeter attacks every single enemy within range. It has greatly reduced power in return. Useful for applying an effect over a large area.                          | [blocks/targeters/AoeTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/AoeTargeter.prefab), [AoeTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/AoeTargeterComponent.html), [AoeTargeterSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/AoeTargeterSystem.html)                        | ![AoeTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/AoeTargeter.png)         |
| ChainTargeter   | Targets a enemy and chains the effect to nearby enemies.                                                                                                                 | [blocks/targeters/ChainTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/ChainTargeter.prefab), [ChainTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/ChainTargeterComponent.html), [ChainTargeterSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/ChainTargeterSystem.html)            | ![ChainTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/ChainTargeter.png)     |
| MissileTargeter | Targets an enemy far away and affects all enemies nearby the target                                                                                                      | [blocks/targeters/MissileTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/MissileTargeter.prefab),[MissileTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/MissileTargeterComponent.html), [MissileTargeterSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/MissileTargeterSystem.html) | ![MissileTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/MissileTargeter.png) |
| SingleTargeter  | Targets a single enemy within range. The basic targeter block.                                                                                                           | [blocks/targeters/SingleTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/SingleTargeter.prefab), [SingleTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence//towers/targeters/SingleTargeterComponent.html), [SingleTargeterCSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence//towers/targeters/SingleTargeterSystem.html)   | ![SingleTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/SingleTargeter.png)   |
| SniperTargeter  | This targeter has high range and power, at the cost of a slow attack speed and an inability to hit close by targets. It is ideal for taking out strong units from range  | [blocks/targeters/SniperTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/SniperTargeter.prefab), [SniperTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/SniperTargeterComponent.html), [SniperTargeterSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/SniperTargeterSystem.html)      | ![SniperTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/SniperTargeter.png)   |
| SplashTargeter  | Targets a single enemy and applies the effect to all enemies close by the target.                                                                                        | [blocks/targeters/SplashTargeter.prefab](https://github.com/Terasology/GooeyDefence/tree/master/assets/prefabs/blocks/targeters/SplashTargeter.prefab), [SplashTargeterComponent.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/SplashTargeterComponent.html), [SplashTargeterSystem.java](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/targeters/SplashTargeterSystem.html)      | ![SplashTargeter Tile](https://raw.githubusercontent.com/Terasology/GooeyDefence/master/assets/blockTiles/towerBlocks/targeters/SplashTargeter.png)   |

## Connectors

Connectors are plain blocks that do not provide any function to the tower other than connecting functional blocks.
Functional blocks can also be placed adjacent to each other, but with connectors they don't have to, allowing for a cost-effective yet more strategic placement while still leveraging the other components of a tower.

## Upgrades

The effectiveness of a tower's components can be improved by upgrading them using the "Tower Screen" (`E`).
The "Tower Screen" lists all functional components of a tower.
Selecting any of those displays details of the respective component and means to upgrade it.

Available tower component upgrades depend on the component type. For instance, for examples targeter upgrades include increasing range and attack speed.
Breaking an upgraded tower component block will reset all applied upgrades.