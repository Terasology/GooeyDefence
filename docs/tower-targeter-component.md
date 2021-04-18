# `TowerTargeter` Component

The `TowerTargeter` component is the base class for all tower targeters.
New targeters can be created by extending the `TowerTargeter` component and adding it to a block extending `GooeyDefence:PlainBlock`.
Additionally, a system is required including the implementation of the logic selecting targets and initiating the attack on them.

## Mandatory fields

The following fields are mandatory for every targeters to implement:

| drain           | The amount of power the targeter will consume       |
| range           | The range of the targeter in blocks                 |
| attackSpeed     | How often the targeter will attack, in milliseconds |
| selectionMethod | The method used to select a target                  |
| lastTarget      | The enemy targeted last attack                      |
| affectedEnemies | All targets affected last attack                    |

## `getMultiplier()`

The `getMultiplier()` method is called by effectors when applying their effects to weaken or strengthen it based on the targeter that initiated the attack including applying the effect.
Implementing this method is mandatory for every targeter.
It's expected to return a float value to the caller.

## Target Selection Strategy

The target selection strategy of a tower indicates how the targeter chooses targets to initiate attack on.
The strategy is a mandatory property of every targeter, stored in the `selectionMethod` field.
A targeter can only apply the selection strategy on targets within its range.
The following target selection strategies are available for use:

| FIRST  | The enemy closest to shrine     |
| WEAK   | The enemy with the least health |
| STRONG | The enemy with the most health  |
| RANDOM | A random enemy within range     |
