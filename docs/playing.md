# How To Play "Gooey's Defence"

"Gooey's Defence" is a tower-defense style gameplay.
The goal is to defend the center of the arena against the malicious Gooeys.
The Gooeys spawn in waves in multiple outer areas of the arena and traverse it to get to the center.
The player can buy tower parts to build tower with various effects to keep the Gooeys from reaching the center.

### World Generation
The world generator creates a world with the following properties:
- Base height is always zero
- Defence field with four entries and a shrine in the center
- Field is filled with a random block pattern on the floor

### Activation
Firstly, please start a new world after updating this module.
During development I shall make no guarantees that the module will be backwards compatible.  
Once your new world has loaded you will need to activate it.
That is currently done by simply ~~interacting with any shrine block via `E`~~ entering the game now.

If the world loaded correctly you should see translucent blue lines crossing the field from the entrances to the shrine.
These are the paths the enemies will take in order to reach the spawn. As you alter the world, the paths will also change.
If no path is found then the blue line will disappear and your game will probably crash at the moment.  
Likewise be careful using the debugger, if the pathfinding times out (and it does count time paused in the debugger) then your game will crash.  
Probably.


### Enemies
In order to spawn in some enemies just interact with the world again.
This can be done by interacting with a shrine block with `E` key or by using an item you are holding with `right click`.
~~This does include placing blocks, so do with that as you will~~.

There are three enemy types at the moment, `BasicEnemy`, `FastEnemy` and `StrongEnemy`.
However in order to spawn in different types you will need to edit the `EnemyManager#spawnEnemy()` method.
Simply change the prefab referenced to one of the other types and recompile.

When an enemy reaches the shrine, it will be destroyed and make the shrine flash red briefly.
The game will not end for the release, but the game will end for the master branch.
The restart button is there to help, so don't worry about having your testing constantly interrupted.

The main properties for the enemies are the movement speed and the health.
Movement and animation are handled by the FlexiblePathfinding module and skeletal mesh animations.
The health system manages the enemy health which is affected by tower effectors.
If an enemy dies (health equals zero), an `EntityDeathEvent` is sent to the enemy entity.
Enemies spawn on a periodic action at each entrance.

As of present, waves are not implement.
When they are implemented it will most likely be through either a class or component.
The possible composition of waves changes the longer one survives, and as such anything that defines them must be capable of incorporating this. Also, the wave composition should be slightly random, when it comes to exact nunber and entrance used. This is doable with either a component/prefab or a code class. I am open to suggestions as to which.

### Towers

The second block type is **Targeters**.
Targeters define which enemies are affected by one tower effect.
A targeter drains tower power and has a maximum range, an attack speed, a selection strategy for enemies (e.g. always the first enemy).
These are the blocks that actually attack the enemies.
There are a number of different Targeter blocks, all with different strengths and weaknesses.
They are listed below.  
Crucially, Targeters don't need to actually see the enemy to shoot it, with their shots phasing through blocks.
You can also stack multiple different types of Targeters onto a single tower. For now...

The multiplier is one of the balancing tools I've added. It is sent to each effector in order to moderate the effects. For instance, the Aoe Targeter has a much lower multiplier than the Single Targeter. This is designed to balance for the fact that the Aoe applies to /all/ of the enemies in it's radius whilst Single only applies to one. If this multiplier was not present then it would mean that the attacks from the Aoe each hit as hard as the Single. Obviously this is unbalanced and the Aoe is objectively better.
When it comes to actually balancing all of this, I may split it out into two components, one for the status effects and the other for the damage applied. This allows for instance, for the Aoe to deal zero damage, but still a relatively standard status strength. This would mean you would have to pair it with a tower that deals more damage in order to kill the enemies.

The method for extending this is similar to the effector with a few key differences, which will be outlined here.
Firstly, the component must extend TowerTargeter. Likewise with effectors, the targeter can instead extend an existing targeter component. TowerTargeter also includes some fields by default which are as follows:
drain: The amount of power the targeter will consume
range: The range of the targeter in blocks
attackSpeed: How often the targeter will attack, in milliseconds. Eg, 200 means it will attack every 0.2s, or 5 times a second
selectionMethod: The method used to select a target, more info below. This should not be set in the prefabs
lastTarget: The enemy targeted last attack, more info below. This should not be set in the prefabs
affectedEnemies: All enemies effected last attack, used internally. This should not be set in the prefabs
The component must also implement (or override if extending an existing targeter) the getMultiplier method. This returns a float which is passed to the effectors when they apply their effects. It's used as a balancing tool in order to strengthen or weaken effects. For instance, the Sniper Targeter has a high multiplier, but the Aoe Targeter has a low one.
The selectionMethod of a tower is an optional enum that indicates how the tower should choose a target from the enemies within range. It is not required to be used. There are four possible options:
FIRST: The enemy closest to shrine
WEAK: The enemy with the least health
STRONG: The enemy with the most health
RANDOM: A random enemy within range
Following on from this a new block must be made. This is the same as effectors, prefab must extend GooeyDefence:PlainBlock etc.
Lastly, a system must be created in order to actually select the targets to attack. The system should listen for the SelectEnemiesEvent which will be sent against the targeter entity. A number of helper methods are provided in the BaseTargeterSystem, such as getSingleTarget which selects a single entity from a list, given the selection method to use; canUseTarget which does a basic check to see if the enemy is within range and exists; and getTarget which gets a single entity, based on the range, selection method and location of the tower.
EnemyManager also contains a method to get all enemies within a radius of a position. It is not required to use any of these methods however.

Lastly you have **Effectors**.
.

Firstly you'll need to make the new component. This needs to be a subclass of TowerEffector and thus must either extend that, or some other effector. The benefits of extending an existing effector is that it allows you to build on fields defined within that. For instance, FireEffector and PoisonEffector both extend DamageEffector, as they apply damage to the target and thus can reuse the latter.
An effector also needs to implement (or override if extending from another effector) the following methods
getEffectCount()
This method is used to indicate how many times the effect should be applied onto the target. The options are defined in the EffectCount enum and are as follows:
PER_SHOT: Every single time the tower attacks, this effector is applied.
CONTINUOUS: The effect is only applied when the enemy is first attacked.
getEffectDuration()
This method indicates how long the effect should last for. There are three options defined in the EffectDuration enum.
INSTANT: The effect has no duration.
LASTING: The effect has a duration for as long as the enemy is being attacked
PERMANENT: The effect has a duration, but it's not determined how long.
After that, the effector needs to be added to a block, this is the same as the method of adding a core block, and details are in that section.
Finally, a System needs to be created to apply the effect to the enemy. The system should listen for an ApplyEffectEvent. This event will be sent against the effector itself, to allow for the system to filter for the correct component. The event will always only be sent with a single enemy target, with multiple events being sent for each enemy targeted.
If the effector has EffectDuration.LASTING then a second event will be sent to remove the effect from enemies, RemoveEffectEvent. This is the same as the prior event and is sent against the effector with a single enemy target.
Of note, is that the event will contain a strength multiplier, this should be used to weaken or strengthen an effect, and is used as a balancing tool on a per-targeter basis. It is gauranteed that the value will be the same for both the ApplyEffectEvent and RemoveEffectEvent
The system may otherwise do whatever it needs to, in order to apply the effect. This includes listening for other events, injecting managers and other things common for a system.

**Plain** blocks are the odd type out. They provide no function to the tower, but they allow the blocks to connect without having to directly touch.
They are useful if you want to connect some of the above blocks but don't actually want to have to use a more expensive functional block to do so.

### Upgrading
If you want to upgrade the effectiveness of the tower's blocks you can.
By interacting with a tower via `E` you can bring up the tower screen.
This has a list of all the targeters and effectors in the tower.
By selecting one you can view the details of that block and upgrade it.
A block can be upgraded in multiple different ways. You can always upgrade the range and attack speed but some other towers have other upgrades you can buy.  
If you break an upgraded block you will lose all your upgrades and have to re apply them.
This is only a minor inconvenience at the moment, but when upgrades start to cost money will become more serious.
The costs will also not be refunded. You break it, you pay for it.

The upgrade system can be defined via prefabs and depends on the `BlockUpgradesComponent`.
The component maps to another component and includes a list of upgrades
which will modify a property of the target component.
Example:
```
  "FireEffector": {
    "drain": 5,
    "damage": 2,
    "fireDuration": 2000
  },
  "BlockUpgrades": {
    "componentName": "FireEffector",
    "upgrades": [
      {
        "upgradeName": "Damage",
        "stages": [
          {
            "cost": 5,
            "values": {
              "damage": 2
            }
          },
          ...
```
The `FireEffectorComponent` has a damage property. The `BlockUpgradesComponent` refers to the fire effector component
at the `componentName` field. The first upgrade stage will increase the `damage` property value by two. Therefore the
overall damage after the first upgrade will be `2+2=4`.

An upgrade can only modifiy existing fields, it cannot add them.
In order to decrease a field simply use a negative number.
The values defined in each upgrade section have to be a number, that is they are limited to int, float, short, long, double & byte.
If the component has a parser (see UI section) then the fields being upgraded do not need to be defined within that or vice versa. They are independent.


### Tower Screen

This is by far the most complicated at present.
It's split into three main segments, the block lists, the block info and the tower info.

#### Block Lists

These are the three UILists that contain all the blocks of each type. In the top right corner is the list of all the Core blocks. In top left is the list of Targeters and in the bottom left, the Effectors.
These are simply populated from the tower entities internal lists.
The name for each entry in the list is first taken from the DisplayNameComponent and as a backup the prefab name.

#### Block Info

This is the complicated bit. This is the centre section inbetween the Core list on the right and the other two lists on the left. At the top it contains the name, again taken from the DisplayNameComponent with the prefab name as backup. This is followed by thr description, from the DisplayNameComponent, with no backup.

At the bottom of this column is an area for block specific settings. At present this is only used for selecting targeting mode for Targeters, however there are plans to allow for elements to be added to this section, depending on the block selected.

Inbetween these is a list of all the fields from the relevant TowerX component that are listed to display. As well as buttons for each of the upgrade paths. This is where the Parsers come in.

##### Parsers

A parser is any class that extends the BaseParser abstract class. It contains information about the component it applies to, the fields that should be displayed and methods to convert those field values into human readable forms.

Firstly, a parser is only applied onto the class that is returned by the getComponentClass() method.

As not all fields on a component should be displayed in UI, the getFields() method returns a map between a field name, and the display name. If a field is not present as a key in this map, then it is not displayed. The fields are also displayed in alphabetical order, according to the field name, not the display name. This is to allow field names, values and upgrade values to all line up.

Lastly, and this is where the reflection magic happens, the parser should contain methods to convert field values to readable values. These methods should have a specific structure.

A return type of string
The name of the method must be the same as the name of the field. Capitalisation matters.
The first parameter should be a boolean. It will indicate if the value is actually an upgrade values
The second parameter should have the same type as the field. This will be passed the value to convert.

If a method matching this is not found, then either the handleField or handleUpgrade methods are called. These are backup methods that take a String field name and an Object value. By default handleField simply calls String.valueOf on the value passed, and handleUpgrade calls handleField and prepends a + to the result.

As of present the value of a field being converted can be an Enum, String, or any of the 6 primitive numbers. Other values may be supported in the future or an Object default added.

Of note here is that both component field values and upgrade values are passed through the parser. The boolean flag, or differing handle methods allow for different conversion of upgrades vs field values.

#### Tower Info

This contains general information about the tower itself. At present this only contains the power production and drain, but likewise there are plans to extend it.

### Activate Screen

This screen is displayed when the loading screen is closed. It simply contains a welcome title, some info text and a begin button.
Closing the screen, either via the begin button or via esc will activate the field and cause the game to start, or resume.
The text displayed varies depending on if the game is new or being resumed from a save. At present only filler text is displayed, however likely that stats, flavour text and possibly options will be added here later in development.
The main purpose for the screen is actually to add a buffer between the world being created and the game starting. This is as the pathfinding systems require all chunks to be loaded before they can complete their pathfinding. A such an activation needs to be peformed once the player has loaded in and the chunks have loaded. There is no such event for this, instead this screen provides a useful way of ensuring the world cannot be activated to early.

### Pathfinding
The module depends on the FlexiblePathfinding which is used for the path calculations.
Paths are always calculated from each entrance to the field center where the shrine is located.
Path events are then sent to the shrine entity.
Initial paths are calculated when the game starts. All paths are recalculated when a block is changed.

### Open Points
- World size and properties are defined hardcoded in DefenceField, can be moved to a configuration prefab
- Shrine template could be extracted to something else than hardcoded block positions
- Paths all entrances in PathfindingManager can be stored in something more readable than a List of Lists
- EnemyWalkingPlugin.areAllBlocksPenetrable is a code duplication with a part of the WalkingPlugin from FlexiblePathfinding.
  The method could be extracted in flexible pathfinding and then reused in GooeyDefence.