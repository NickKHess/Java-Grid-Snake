package com.csg.snake.entities;
import java.io.File;
import java.util.LinkedList;

import com.csg.snake.SnakeGame;
import com.csg.snake.physics.Direction;
import com.csg.snake.physics.Vector2D;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Snake extends ImageView {

	Media snakeEat = new Media(new File("smw_fireball.wav").toURI().toString());
	MediaPlayer snakeEatPlayer = new MediaPlayer(snakeEat);
	Media snakeDie = new Media(new File("smw_game_over.wav").toURI().toString());
	MediaPlayer snakeDeathPlayer = new MediaPlayer(snakeDie);
	Media snakeCrumble = new Media(new File("smw_lava_bubble.wav").toURI().toString());
	MediaPlayer snakeCrumblePlayer = new MediaPlayer(snakeCrumble);
	
	LinkedList<Rectangle> snake = new LinkedList<Rectangle>();
	int score = 0;
	int highScore = -1;
	Vector2D currentPosition;
	Timeline snakeAnimation, deathAnimation;

	private Direction currentDirection;

	SnakeGame game;

	boolean selfCollision = false;

	public static Color snakeColor;
	DropShadow snakeGlow = new DropShadow(0, 0, 0, snakeColor);

	public Snake(int x, int y, SnakeGame game) {
		
		this.game = game;
		currentPosition = new Vector2D(x, y);
		
		setCurrentDirection(Direction.RIGHT);

		Rectangle head = new Rectangle(SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH, 
				SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH);
		snakeColor = Color.LIMEGREEN;
		GridPane.setRowIndex(head, 0);
		GridPane.setColumnIndex(head, 0);
		head.setFill(snakeColor);
		snakeGlow.setColor(snakeColor);
		snakeGlow.setRadius(10);
		snakeGlow.setOffsetX(0);
		snakeGlow.setOffsetY(0);
		snakeGlow.setWidth(SnakeGame.GRID_RECT_SIZE + 5);
		snakeGlow.setHeight(SnakeGame.GRID_RECT_SIZE + 5);
		head.setEffect(snakeGlow);
		
		snake.add(head);
		game.getTheGrid().getChildren().addAll(head);

		snakeAnimation = new Timeline(new KeyFrame(Duration.seconds(.1), new EventHandler<ActionEvent>() {
			// Commented code here is for debugging only. Shows time between frames
			//long lastTimeMS = 0;
			@Override
			public void handle(ActionEvent e) {
				updateSnakePosition(getCurrentDirection());
				/*if(lastTimeMS != 0)
					System.out.println("Time btwn frames: " + (System.currentTimeMillis() - lastTimeMS));
				lastTimeMS = System.currentTimeMillis();*/
			}
		}));
		snakeAnimation.setCycleCount(Timeline.INDEFINITE);
		snakeAnimation.play();
	}


	public void updateSnakePosition(Direction direction) {
		Vector2D motion = new Vector2D(direction);

		currentPosition = new Vector2D(currentPosition.getX() + motion.getX(), 
				currentPosition.getY() + motion.getY());

		Rectangle snakeTail = snake.remove(snake.size() - 1);

		snakeTail.setX((SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH) * currentPosition.getX());
		snakeTail.setY((SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH) * currentPosition.getY());

		snake.add(0, snakeTail);

		selfCollision = false;

		// All but the snake's head collision detection
		if(snake.size() > 1) {
			for(int i = 1; i < snake.size(); i++) {
				if(snake.get(0).getX() == snake.get(i).getX() && snake.get(0).getY() == snake.get(i).getY()) {
					selfCollision = true;
					break;
				}
			}
		}

		// Kill snake if it will be outside of the bounds of the game or has collided with itself
		if(currentPosition.getX() < 0 
				|| currentPosition.getX() > SnakeGame.GRID_WIDTH 
				|| currentPosition.getY() < 0 
				|| currentPosition.getY() > SnakeGame.GRID_WIDTH
				|| selfCollision
				) {
			killSnake();
		}
		else if(score > snake.size() - 1) {
				Rectangle newPiece = new Rectangle(SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH,
						SnakeGame.WINDOW_SIZE / SnakeGame.GRID_WIDTH);
				newPiece.setFill(snakeColor);
				newPiece.setEffect(snakeGlow);
				newPiece.setX((SnakeGame.GRID_RECT_SIZE) * currentPosition.getX() - motion.getX() * (SnakeGame.GRID_RECT_SIZE)); 
				newPiece.setY((SnakeGame.GRID_RECT_SIZE) * currentPosition.getY() - motion.getY() * (SnakeGame.GRID_RECT_SIZE));
				snake.add(newPiece);
				game.getTheGrid().getChildren().add(newPiece);
			}

		if(currentPosition.getX() * (SnakeGame.GRID_RECT_SIZE) == game.getFruit().getX() &&
				currentPosition.getY() * (SnakeGame.GRID_RECT_SIZE) == game.getFruit().getY()) {
			score++;
			game.setFruit(game.addNewFruit());
			game.getScoreCounter1().setText("Score: " + score);

			snakeEatPlayer.seek(Duration.ZERO);
			snakeEatPlayer.play();
		}

	}

	public void killSnake() {
		snakeAnimation.stop();
		
		// Death animation speeds up as your snake gets longer. The animation is the same length for
		// every possible snake length.

		float animationLength = .25f;

		deathAnimation = new Timeline(new KeyFrame(Duration.seconds(animationLength/snake.size()), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				game.getTheGrid().getChildren().remove(snake.get(0));
				snake.remove(snake.get(0));
				snakeCrumblePlayer.seek(Duration.ZERO);
				snakeCrumblePlayer.play();
				if(snake.isEmpty()) {
					endDeathAnimation();
				}
			};
		}));
		deathAnimation.setCycleCount(Timeline.INDEFINITE);
		deathAnimation.play();
	}

	public void endDeathAnimation() {
		deathAnimation.stop();
		snakeDeathPlayer.seek(Duration.ZERO);
		snakeDeathPlayer.play();
		showGameOver();
	}

	public void showGameOver() {
		game.getTheGrid().getChildren().clear();
		game.setFruit(null);
		snake.clear();
		game.getBG().getChildren().clear();
		Text text = new Text("GAME OVER\nScore: " + score);

		text.setFill(Color.LIME);
		text.setFont(new Font(SnakeGame.FONT_FAMILY, 16));
		text.setTextAlignment(TextAlignment.CENTER);
		game.getBG().setCenter(text);
		
		Button restartButton = new Button("Restart");
		restartButton.setOnAction(e -> {
			game.menuButtonClickPlayer.seek(Duration.ZERO);
			game.menuButtonClickPlayer.play();
			
			// Enable main screen
			game.showMenuScreen();
			
			// Disable current screen (and its functionality)
			restartButton.setVisible(false);
			text.setVisible(false);
			game.setListenerActive(false);
			
			// Stop all game sounds
			snakeCrumblePlayer.stop();
			snakeDeathPlayer.stop();
			snakeEatPlayer.stop();
		});
		game.getBG().setBottom(restartButton);
		BorderPane.setAlignment(restartButton, Pos.TOP_CENTER);
		score = 0;
	}

	public int getScore() {
		return score;
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}


	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}


	public LinkedList<Rectangle> getSnake() {
		return snake;
	}


	public Color getSnakeColor() {
		return snakeColor;
	}

}