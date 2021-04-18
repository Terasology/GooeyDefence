# `TowerScreen`

The `TowerScreen` is a user interface element that lists all functional components of a tower and provides details about them and the general tower stats.
The UI is split into three main segments: the block lists, the block info and the tower info.

## Block Lists

The Block Lists are three `UIList`s that contain all tower components, separated into the three component types: Core, Effector, and Targeter.
The Cores are listed in the top right corner, while the Targeters are shown in the top left, and the Effectors in the bottom left.
These `UIList`s are populated based on the tower entity's internal list.
The name of each component is derived from the `DisplayNameComponent` or, if not present, the prefab name.

## Block Info

The Block Info is displayed in the center of the screen.
It shows the component details, including the name, the description and component-specific settings for a selected tower component.
Currently, the only customizable setting is the target selection strategy for Targeters.
Additionally, the part of the screen lists all fields related to a selected tower component as well as a buttons for upgrading it.

## Tower Info

The Tower Info contains general information about the tower itself.
Currently, this only includes the amount of power produced by the Cores and an accumulation of the power drain introduced by all Effectors and Targeters.
