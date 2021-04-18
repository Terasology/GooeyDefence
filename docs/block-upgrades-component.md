# `BlockUpgrades` Component

The `BlockUpgrades` component can be used to define new component upgrades in component prefabs.
It relates to a tower component using the `componentName` field and lists a number of upgrades modifying properties of the related tower component.

For example, the following prefab snippet shows a `FireEffector` and an upgrade to its `damage` property, increasing it by two:

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

The values defined in each upgrade section are limited to `int`, `float`, `short`, `long`, `double`, and `byte` types.
If the component has a parser, fields being upgraded do not need to be defined within that parser as they are independent.