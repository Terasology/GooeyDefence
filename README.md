# Gooey's Defence
This module serves as the main module for the Gooey's Defence Gameplay Template.  
It currently is undergoing active development and thus is not at a public release stage.

## Testing Instructions
If you want to test this module then these are the current steps.

Firstly, upon loading the world you should interact with any block via `E`.  
This will activate the world, setting up the Enemy, Tower and Pathfinding systems.  

Once that has been done there are a few other things you can do:  

To spawn in an enemy interact with any block again using `E`.  
To test towers use the blocks `CoreOne`, `EffectOne` and `EmitterOne` as well as the `Plain` block.
The functions of each of these blocks is as follows:

* *Effect* blocks deal damage & apply effects to the enemies. For the testing, it simply places a visual marker above the enemy.
* *Emitter* blocks determine which enemies are to be attacked. For the testing, it simply selects the first enemy in range.
* *Core* blocks provide power to the above two blocks. For testing one core block can power 2 other blocks.
* *Plain* blocks simply serve as an additional block to be used for connecting the above three types.

For the tower to function, at least one of each of the above must be connected to each other. They can be connected via any of the four block types.
