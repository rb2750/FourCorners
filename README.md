# FourCorners
FourCorners is a simple platformer made in LWJGL, initially made to test a steam controller library. It was gradually adapted into a more advanced game.
The game now supports all forms of controllers including Xbox and DualShock.

## Setup
Simply import the pom.xml as a maven project. The required dependencies should then be installed and the project should be ready to run.

## Block Types
### Tile
This is a standard building tile, nothing particularly special about it.

### Bouncy Tile
Jumping on this gives a bouncy effect like a trampoline. Holding jump makes you gradually gain height.

### Starting Point
This is where you will spawn when the map is loaded. If there are multiple, your spawn point will be chosen at random.

### One Way Tile
You can only travel from left to right through this tile.
