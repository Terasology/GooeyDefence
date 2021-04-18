# TowerEffector Component

New effectors can be created via prefabs and by extending the `TowerEffector` component.
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