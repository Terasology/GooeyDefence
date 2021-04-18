# `TargeterSystem`

Every tower targeter requires a respective system to select targets and initiate the attack on them.
In order to do so, a system can inject managers, listen and react to events sent by other systems, and send out events itself.

## `SelectEnemiesEvent`

`SelectEnemiesEvent` is the target selection trigger event every targeter system should listen for.
This event is sent against the targeter, allowing systems to filter for their associated `TargeterComponent`.
A targeter system by default is not expected to react to `SelectEnemiesEvent`s sent against other targeters.

## Helper Methods

The `BaseTargeterSystem` provides a number of optional-to-use helper methods to simplify implementing targeter systems and avoid code duplication.

| Method          | Functionality                                                                          |
|-----------------|----------------------------------------------------------------------------------------|
| getSingleTarget | selecting a single target from a given input list based on a given selection strategy  |
| canUseTarget    | basic check to see if a target exists and is within range                              |
| getTarget       | selecting a single target based on range, selection method and location of a tower     |

Further, `EnemyManager` provides a method to get all targets within a radius of a given position.