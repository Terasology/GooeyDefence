## Tower Block Base

Tower block bases are part of a [Tower](towers.md) structure.

All of the 4 tower block types, [Core](core.md), [Effector](effector.md), [Targeter](targeter.md) or Plain, have the exact same base structure to creating a new block. The two parts to a new tower block are the block and the prefab.
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
