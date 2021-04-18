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
Effectors drain the tower's power to realize the tower's effect on targeted enemies.

Most effectors can be upgraded via the upgrade system.
Currently, there are the following effectors:

| Effector | Effect |  Notes |
------------------------------
| DamageEffector | Plain Damage | direct damage only |
| FireEffector | Burn Damage | damage over time, chance of spreading |
| IceEffector | Slow | |
| PoisonEffector | Poison | damage both direct and over time |
| StunEffector | Stun | |
| VisualEffector | Enlarge | increases enemy visibility | 

Multiple types of effectors can be stacked onto a single tower to apply multiple different effects.
This does mean that, technically, an enemy can be slowed and burnt at the same time.



## Targeters

