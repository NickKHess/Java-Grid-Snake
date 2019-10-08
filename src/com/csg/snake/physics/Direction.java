package com.csg.snake.physics;

/**
 * The enum which specifies a cardinal direction on the screen
 * @author Nick
 */
public enum Direction {

	UP(0), LEFT(1), DOWN(2), RIGHT(3);

	int value;

	private Direction(int value) {
		this.value = value;
	}

	public Direction reverse() {
		switch(value) {
		case 0:
			return Direction.DOWN;
		case 1:
			return Direction.RIGHT;
		case 2:
			return Direction.UP;
		case 3:
			return Direction.LEFT;
		}
		return null;
	}

}