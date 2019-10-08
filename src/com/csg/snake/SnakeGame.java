package com.csg.snake;
import java.io.File;
import java.util.Random;

import com.csg.snake.entities.Snake;
import com.csg.snake.physics.Direction;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeGame extends Application {

	Stage stage;
	Pane theGrid;
	private BorderPane bg;
	BorderPane stats;
	Text scoreCounter;

	private Rectangle fruit;

	Snake snake;

	Media menuButtonClick = new Media(new File("smw_map_move_to_spot.wav").toURI().toString());
	public MediaPlayer menuButtonClickPlayer = new MediaPlayer(menuButtonClick);

	public static final int WINDOW_SIZE = 750;
	public static final int GRID_WIDTH = 32;
	public static final int GRID_RECT_SIZE = WINDOW_SIZE / GRID_WIDTH;
	
	public static final String FONT_FAMILY = "TIMES";
	
	public Button onePlayer;
	
	boolean listenerActive = false;
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;

		bg = new BorderPane();
		bg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

		Scene scene = new Scene(bg, WINDOW_SIZE, WINDOW_SIZE);

		showMenuScreen();

		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Nick's Snake Game");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void showMenuScreen() {
		onePlayer = new Button("1 Player Game");
		bg.setTop(onePlayer);
		onePlayer.setOnAction(e -> {
			menuButtonClickPlayer.seek(Duration.ZERO);
			menuButtonClickPlayer.play();
			
			stats = new BorderPane();
			stats.setMaxHeight(200);

			Rectangle bounds = new Rectangle(0, 0, stats.getWidth(), stats.getHeight());
			bounds.setFill(Color.BLACK);
			bounds.setStrokeType(StrokeType.INSIDE);
			bounds.setStrokeWidth(2);
			bounds.setStroke(Color.LIME);

			theGrid = new BorderPane();

			bg.setTop(theGrid);
			bg.setBottom(stats);

			snake = new Snake(0, 0, this);

			scoreCounter = new Text("Score: " + snake.getScore());
			scoreCounter.setFill(Color.LIME);
			scoreCounter.setFont(new Font(FONT_FAMILY, WINDOW_SIZE / 50));

			stats.setLeft(scoreCounter);

			onePlayer.setVisible(false);

			playGame();
		});
	}

	public void playGame() {
		fruit = addNewFruit();

		listenerActive = true;
		
		EventHandler<KeyEvent> eh = new EventHandler<javafx.scene.input.KeyEvent>() {
			@Override
			public void handle(KeyEvent key) {
				// Remove the event handler if there's no need for it, as determined in Snake.java cleanUp()
				if(!listenerActive)
					stage.removeEventHandler(KeyEvent.KEY_PRESSED, this);
					
				// Each if statement verifies that the proper key has been pressed and ensures the player cannot
				// double back on himself

				// CONTROLS
				if(key.getCode() == KeyCode.W && 
						snake.getCurrentDirection() != Direction.DOWN) {
					snake.setCurrentDirection(Direction.UP);
				}
				else if(key.getCode() == KeyCode.A && 
						snake.getCurrentDirection() != Direction.RIGHT) {
					snake.setCurrentDirection(Direction.LEFT);
				}
				else if(key.getCode() == KeyCode.S && 
						snake.getCurrentDirection() != Direction.UP) {
					snake.setCurrentDirection(Direction.DOWN);
				}
				else if(key.getCode() == KeyCode.D && 
						snake.getCurrentDirection() != Direction.LEFT) {
					snake.setCurrentDirection(Direction.RIGHT);
				}
			}
		};
		
		// Control scheme -
		//    Player 1: WASD
		stage.addEventHandler(KeyEvent.KEY_PRESSED, eh);
	}

	public Rectangle addNewFruit() {
		if(fruit == null) {
			fruit = new Rectangle(GRID_RECT_SIZE, GRID_RECT_SIZE);
			fruit.setFill(Color.RED);
			
			DropShadow fruitGlow = new DropShadow();
			fruitGlow.setColor(Color.RED);
			fruitGlow.setOffsetX(0);
			fruitGlow.setOffsetY(0);
			fruitGlow.setWidth(GRID_RECT_SIZE + 5);
			fruitGlow.setHeight(GRID_RECT_SIZE + 5);
			
			fruit.setEffect(fruitGlow);
			
			theGrid.getChildren().add(fruit);
		}

		boolean generate = true;

		while(generate) {
			// Loop over every piece of the snake
			for(Rectangle r : snake.getSnake()) {
				// If generated inside the snake, regenerate
				if(r.getX() == fruit.getX() && r.getY() == fruit.getY()) {
					fruit.setX((GRID_RECT_SIZE) * new Random().nextInt(GRID_WIDTH - 1));
					fruit.setY((GRID_RECT_SIZE) * new Random().nextInt(GRID_WIDTH - 1));
				}
				// Otherwise stop regenerating
				else {
					generate = false;
				}
			}
		}

		return fruit;
	}

	public Pane getTheGrid() {
		return theGrid;
	}

	public Snake getPlayer1Snake() {
		return snake;
	}

	public Rectangle getFruit() {
		return fruit;
	}

	public void setFruit(Rectangle fruit) {
		this.fruit = fruit;
	}

	public Text getScoreCounter1() {
		return scoreCounter;
	}

	public BorderPane getBG() {
		return bg;
	}

	public void setBG(BorderPane bg) {
		this.bg = bg;
	}

	public Stage getStage() {
		return stage;
	}

	public void setListenerActive(boolean listenerActive) {
		this.listenerActive = listenerActive;
	}

}