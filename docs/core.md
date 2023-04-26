## Cores

Cores are part of a [Tower](towers.md) structure.

Cores comprise two main parts, the block and the component. The component section is the only section that provides functionality and it simply needs to be on a [valid tower block](tower-block-base.md) to function.

See [Tower Block Base](tower-block-base.md) for details on how to make the tower block and corresponding entity. This guide will assume you have completed those steps first.

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
