# The Arena

The world generator creates an arena with the following properties:
* base height: 0
* circular defence field with four entries and a shrine in the center
* random block pattern on the floor of the defence field

## Attack Paths

If the world loaded correctly, there should be translucent blue lines crossing the field from the entrances to the shrine.
These are the paths the gppeys will take in order to reach the shrine.
As the player alters the world, these paths will change.
If no path is found, the blue lines will disappear and the gameplay will not work as expected.