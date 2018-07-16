# Module Contributor Guide
<special git conventions for the module?>

The module with all dependencies can be fetched via `groovyw module recurse GooeyDefence`.

## Concepts
<more concepts missing?>

### World Generation
The world generator creates a world with the following properties:
- Base height is always zero
- Defence field with four entries and a shrine in the center
- Field is filled with a random block pattern on the floor

<can the world generator be extended?>

### Towers
Towers are block entities and contain a core, an effector and a targeter.
<How are different towers defined and how can new towers be added?>

#### Core
The core defines the power level of a tower which can be affected with upgrades.

#### Effectors
Effectors represent the tower effect and cause a drain on the tower power.
Most effectors can be upgraded via the upgrade system.
New effectors can be created via prefabs and by extending the `TowerEffector` component.

#### Targeters
Targeters define which enemies are affected by one tower effect.
A targeter drains tower power and has a maximum range, an attack speed,
a selection strategy for enemies (e.g. always the first enemy).

<more to targeters, e.g. what is the multiplicator?>

### Upgrade System
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

<limitations of the system like special component types or property types?>

### Enemies
The main properties for the enemies are the movement speed and the health.
Movement and animation are handled by the FlexiblePathfinding module and skeletal mesh animations.
The health system manages the enemy health which is affected by tower effectors.
If an enemy dies (health equals zero), an `EntityDeathEvent` is sent to the enemy entity.
Enemies spawn on a periodic action at each entrance.

<can health be negative? check is == 0 >

<how are enemy waves defined?>

### User Interface
<all the nui stuff ;)>

### Pathfinding
The module depends on the FlexiblePathfinding which is used for the path calculations.
Paths are always calculated from each entrance to the field center where the shrine is located.
Path events are then sent to the shrine entity.
Initial paths are calculated when the game starts. All paths are recalculated when a block is changed.

# Extension Modules
<how to add extra features with new modules>

# Open Points
- World size and properties are defined hardcoded in DefenceField, can be moved to a configuration prefab
- Shrine template could be extracted to something else than hardcoded block positions
- Paths all entrances in PathfindingManager can be stored in something more readable than a List of Lists
- EnemyWalkingPlugin.areAllBlocksPenetrable is a code duplication with a part of the WalkingPlugin from FlexiblePathfinding.
The method could be extracted in flexible pathfinding and then reused in GooeyDefence.