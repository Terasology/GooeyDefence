# The Gooeys

Gooeys are the malicious intruders in this gameplay.
They spawn in the outer areas of the gameplay arena and try to traverse it to reach the shrine in the arena center.

Currently, Gooeys do not spawn automatically.
A wave of Gooeys can be triggered by interacting with the shrine in the center of the arena (`E`) or by using a held item (right-click).
In the future, gooey waves are expected to change with respect to number, strength, and speed of the Gooeys the longer the player survives.

When a Gooey reaches the shrine, it will be destroyed and make the shrine flash red briefly.
As a result, the game will end and show a restart button allowing a player to retry defending the shrine. 

The main properties of Gooeys are movement speed and health.
Currently, there are the following Gooey types, varying in these properties: `BasicEnemy`, `FastEnemy`, and `StrongEnemy`.
