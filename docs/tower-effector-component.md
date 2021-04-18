# `TowerEffector` Component

The `TowerEffector` component is the base class for all tower effectors.
New effectors can be created by extending the `TowerEffector` component and adding it to a block extending `GooeyDefence:PlainBlock`.
Additionally, a system is required including the implementation of the logic applying the effector's effect to a target.

New effectors can also extend other existing effector components to leverage fields defined in those and avoid code duplication.
For instance, `FireEffector` and `PoisonEffector` both reuse the `DamageEffector` by extending the class.
Thus, they can apply damage to a target without duplicating the logic implemented in `DamageEffector`.

Every new effector needs to implement the `getEffectCount()` and `getEffectDuration()` methods explained below.
In case a new effector extends another effector, it may need to override these methods.

## `getEffectCount()`

The `getEffectCount()` method is used to collect information on how many times an effector's effect should be applied to a target.
The following options are provided by the `EffectCount` enum:

| Option       | Effect Application                                            |
|--------------|---------------------------------------------------------------|
| `PER_SHOT`   | Every single time the tower attacks, this effector is applied |
| `CONTINUOUS` | The effect is only applied when the enemy is first attacked   |

## `getEffectDuration()`
The `getEffectDuration()` method is used to get information on how long an effector's effect applied on a target is expected to last.
The following options are provided by the `EffectDuration` enum.

| Option      | Effect Duration                                                      |
|-------------|----------------------------------------------------------------------|
| `INSTANT`   | The effect has no duration                                           |
| `LASTING`   | The effect has a duration for as long as the enemy is being attacked |
| `PERMANENT` | The effect has a duration, but it's not determined how long          |
