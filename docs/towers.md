# Towers

Towers are the multi-block structures that attack the enemies either by damaging them or affecting their stats.
A tower is made up of at least three blocks:
* a core block
* an effector block
* a targeter block

## Core

The core of a tower defines the power level.
A tower needs power to run effectors and targeters that will allow the tower to affect enemies.
Upgrades to a tower core increase the amount of power it can provide. 

## Effectors

Effectors define how a tower will affect targets.
They drain the tower's power to realize the tower's effect on targeted enemies.

Currently, there are the following effectors:

| Effector       | Effect       |  Notes                                |
-------------------------------------------------------------------------
| DamageEffector | Plain Damage | direct damage only                    |
| FireEffector   | Burn Damage  | damage over time, chance of spreading |
| IceEffector    | Slow         |                                       |
| PoisonEffector | Poison       | damage both direct and over time      |
| StunEffector   | Stun         |                                       |
| VisualEffector | Enlarge      | increases enemy visibility            | 

Multiple types of effectors can be stacked onto a single tower to apply multiple different effects.
This does mean that, technically, an enemy can be slowed and burnt at the same time.

Most effectors can be upgraded via the upgrade system.

## Targeters

Targeters define which enemies are targeted by a tower and as a result will be affected by the tower's effect.
They drain the tower's power to detect possible targets and initiate the attack on them.
Targeters don't need to actually see a target to attack it as their attacks phase through blocks.

A targeter has a maximum range, an attack speed, and a selection strategy for targets.
Currently, there are the following targeters:

| Targeter        | Selection Strategy                                                                            |
-------------------------------------------------------------------------------------------------------------------
| AoeTargeter     | All targets in range                                                                          |
| ChainTargeter   | Single base target, attack chaining to additional targets depending on chain range and length |
| MissileTargeter | Single, far away base target, small splash radius                                             |
| SingleTargeter  | Single target within range                                                                    |
| SniperTargeter  | Far away target, high effect multiplier                                                       |
| SplashTargeter  | Single base target, small splash radius                                                       |

Same as for effectors, each different targeter type has strengths and weaknesses compared to other targeters.
The multipliers are used to keep the balance across the different targeter types, for instance to reduce an effector's effect with more powerful targeters, such as the `AoeTargeter`.
If, for instance `AoeTargeter` and `SingleTargeter` had the same multiplier, the former would damage multiple targets for the same amount as the later would damage only a single target for.
This would result in the `AoeTargeter` being objectively a better choice, resulting in an imbalance across the different targeters.

Multiple types of targeters can be stacked onto a single tower to leverage multiple different selection strategies.