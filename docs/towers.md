# The Towers
Gooey Defence is a tower defence gameplay template. This means that one of the most important features of the module is the towers that give the genre its name. In Gooey Defence, these are freeform multiblock structures, with a minimum of three parts. Together these parts allow players to build a wide variety of towers.

The first part of a tower is the [core](core.md). These add power to the other two block types.
Next are [targeters](targeter.md), which select and attack the enemies. They don't actually apply any damage or effects however. The last are [effectors](effector.md) which complement the targeters by applying effects and damage to the targeted enemies.

All three types are needed for a tower to work. Lack a core, and the other two won't work. Lack a targeter and the effector won't have any enemies to affect. Don't have an effector, and the attacks won't do anything.
Additionally you can, and indeed are encouraged to, have multiple variants of both Targeters and Effectors on a single tower. For instance, adding an Ice Effector and Damage Effector along with a Chain Targeter and Aoe Targeter will result in each attack dealing damage and applying a slowing effect. Additionally all enemies in range will be attacked, with some being attacked again by the Chain Targeter.
In this way complex towers can be built with multiple different focuses.

For a block to be considered part of a tower, all it needs to be doing is touch other tower blocks. In order to allow players to spread out towers there is a plain block which provides no effect, but counts as part of the tower.

More detailed information on how towers work in game is found in the player guide, which is located in the [readme](../README.md).

### Extending the system

The main way to extend the tower system is to add additional [effector](effector.md), [core](core.md) or [targeter](targeter.md) blocks. At present a block cannot implement more than one of these types, with the behaviour being undefined if you try. You could add additional block types, however they would end up being considered as 'plain' blocks and not treated specially. There is currently no plans to add support for custom block types, however we welcome PR's adding this.

Additionally, all tower blocks share the same base, which is detailed on the [Tower Block Base](tower-block-base.md) page.

## Connectors

Connectors are plain blocks that do not provide any function to the tower other than connecting functional blocks.
Functional blocks can also be placed adjacent to each other, but with connectors they don't have to, allowing for a cost-effective yet more strategic placement while still leveraging the other components of a tower.

## Upgrades

The effectiveness of a tower's components can be improved by upgrading them using the "Tower Screen" (`E`).
The "Tower Screen" lists all functional components of a tower.
Selecting any of those displays details of the respective component and means to upgrade it.

Available tower component upgrades depend on the component type. For instance, for examples targeter upgrades include increasing range and attack speed.
Breaking an upgraded tower component block will reset all applied upgrades.