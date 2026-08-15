## Targeters

Targeters are part of a [Tower](towers.md) structure.

Targeters have two components on top of the standard block and prefab requirements. These are the component that will contain the configurable values, and the system used to implement the logic. Both of these are vital for a targeter to function properly.

See [Tower Block Base](tower-block-base.md) for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

### Component

First is the component. This should be a class that extends the [TowerTargeter](http://jenkins.terasology.org/view/Modules/job/GooeyDefence/javadoc/org/terasology/gooeyDefence/towers/components/TowerTargeter.html) class. It can either do this directly, like `SingleTargeter` and `AoeTargeter` or it can extend a class that in turn extends `TowerTargeter`. This is what classes like `MissileTargeter` do as it allows you to use fields from the other subclasses.

TowerTargeter is the base class for all Targeters and has a number of default fields on it. Namely these are:

* `drain`: How much power this Targeter will take.
* `attackSpeed`: How long between attacks. Given in milliseconds, eg 500ms means it will attack twice per second.
* `range`: The radius of this Targeter's range. Given in blocks.

Additionally there is an abstract method called `getMultiplier` that the component will need to implement. This method returns a float that is passed to the [Effectors](effector.md) each attack. It should be used as a balancing tool to make sure that the different targeters are all correctly balanced.

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
