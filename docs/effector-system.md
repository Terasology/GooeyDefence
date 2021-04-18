# `EffectorSystem`

Every tower effector requires a respective system to apply the effector's effect to a target.
In order to do so, a system can inject managers, listen and react to events sent by other systems, and send out events itself.

## `ApplyEffectEvent`

`ApplyEffectEvent` is the trigger event any effector system should listen for.
This event is sent against the effector, allowing systems to filter for their associated `EffectorComponent`.
An effector system by default is not expected to react to `ApplyEffectEvent`s sent against other effectors.

Every `ApplyEffectEvent` only affects a single target.
If an effector's effect applies to multiple targets, multiple `ApplyEffectEvent`s will be sent.
The combination of multiple effectors can further result in multiple `ApplyEffectEvent`s bein sent for the same target.

## `RemoveEffectEvent`

If an effector's effect duration is `EffectDuration.LASTING`, then a second event called `RemoveEffectEvent` will be sent at some point in time.
This event is expected to remove the effect from a target.

Like `ApplyEffectEvent`, `RemoveEffectEvent` is sent against an effector and affects a single target.

## Effect Strength

Both, the `ApplyEffectEvent` and the `RemoveEffectEvent` contain a strength multiplier.
An effector system can weaken or strengthen an effect based on various conditions, including intervention by other systems.
The respective implementation is up to the system.
The strength multiplier can be used as a balancing tool on a per-targeter basis.
It is guaranteed that the multiplier is equal for both, the `ApplyEffectEvent` and `RemoveEffectEvent`.
