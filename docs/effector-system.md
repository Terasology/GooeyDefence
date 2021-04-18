
The system should listen for an ApplyEffectEvent.
This event will be sent against the effector itself, to allow for the system to filter for the correct component.
The event will always only be sent with a single enemy target, with multiple events being sent for each enemy targeted.
If the effector has EffectDuration.LASTING then a second event will be sent to remove the effect from enemies, RemoveEffectEvent.
This is the same as the prior event and is sent against the effector with a single enemy target.
Of note is that the event will contain a strength multiplier, this should be used to weaken or strengthen an effect, and is used as a balancing tool on a per-targeter basis.
It is gauranteed that the value will be the same for both the ApplyEffectEvent and RemoveEffectEvent
The system may otherwise do whatever it needs to, in order to apply the effect.
This includes listening for other events, injecting managers and other things common for a system.