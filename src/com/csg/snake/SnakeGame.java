package com.csg.snake;
import java.io.File;
import java.util.Random;

import com.csg.snake.entities.Snake;
import com.csg.snake.physics.Direction;
import com.csg.snake.storage.Config;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeGame extends Application {

	private Stage stage;
	private Pane theGrid;
	private BorderPane bg;
	private BorderPane stats;
	private Text scoreDisplay, highScoreDisplay;

	private int score = 0;
	private int highScore = Integer.parseInt(SnakeGame.config.get("highScore"));

	private Rectangle fruit;

	private Snake snake;

	private Media menuButtonClick = new Media(new File("audio/smw_map_move_to_spot.wav").toURI().toString());
	private MediaPlayer menuButtonClickPlayer = new MediaPlayer(menuButtonClick);

	public static final String FONT_FAMILY = "TIMES";

	public static final int WINDOW_SIZE = 750;
	public static final int GRID_WIDTH = 32;
	public static final int GRID_RECT_SIZE = WINDOW_SIZE / GRID_WIDTH;

	private Button playButton;

	private boolean listenerActive = false;

	private static Config config = new Config();

	@Override
	public void start(Stage stage) throws Exception {
		// If config is empty, populate it
		if(config.isEmpty()) {
			config.set("snakeColorR", Color.LIME.getRed());
			config.set("snakeColorG", Color.LIME.getGreen());
			config.set("snakeColorB", Color.LIME.getBlue());
			config.set("snakeColorA", Color.LIME.getOpacity());
			config.set("rainbowSnake", false);
			config.set("highScore", 0);
		}

		this.stage = stage;

		bg = new BorderPane();
		bg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

		Scene scene = new Scene(bg, WINDOW_SIZE, WINDOW_SIZE);

		showMenuScreen();

		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Java Grid Snake");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void showMenuScreen() {
		playButton = new Button("Play");

		playButton.setOnAction(e -> {
			menuButtonClickPlayer.seek(Duration.ZERO);
			menuButtonClickPlayer.play();

			stats = new BorderPane();
			stats.setMaxHeight(200);

			theGrid = new BorderPane();

			bg.setTop(theGrid);
			bg.setBottom(stats);

			snake = new Snake(0, 0, this);

			scoreDisplay = new Text("Score: " + score);
			scoreDisplay.setFill(Color.WHITE);
			scoreDisplay.setFont(new Font(FONT_FAMILY, WINDOW_SIZE / 50));
			
			highScoreDisplay = new Text("High Score: " + highScore);
			highScoreDisplay.setFill(Color.WHITE);
			highScoreDisplay.setFont(new Font(FONT_FAMILY, WINDOW_SIZE / 50));

			stats.setLeft(scoreDisplay);
			stats.setRight(highScoreDisplay);

			playButton.setVisible(false);

			playGame();
		});
		bg.setCenter(playButton);

		BorderPane options = new BorderPane();

		Text snakeColorPickerText = new Text("Snake Color: ");
		snakeColorPickerText.setFill(Color.WHITE);
		
		ColorPicker snakeColorPicker = new ColorPicker(Snake.snakeColor);
		
		snakeColorPicker.setOnAction(e -> {
			Color chosenColor = snakeColorPicker.getValue();
			Snake.snakeColor = chosenColor;
			
			config.set("snakeColorR", Snake.snakeColor.getRed());
			config.set("snakeColorG", Snake.snakeColor.getGreen());
			config.set("snakeColorB", Snake.snakeColor.getBlue());
			config.set("snakeColorA", Snake.snakeColor.getOpacity());
		});
		
		BorderPane snakeColorPickerMenu = new BorderPane();
		snakeColorPickerMenu.setLeft(snakeColorPickerText);
		snakeColorPickerMenu.setRight(snakeColorPicker);

		options.setLeft(snakeColorPickerMenu);
		
		// Rainbow snake feature toggle
		// Will disable snake color upon enabling
		CheckBox rainbowSnakeToggle = new CheckBox("Rainbow Snake");
		rainbowSnakeToggle.setIndeterminate(Boolean.parseBoolean(config.get("rainbowSnake")));
		rainbowSnakeToggle.setTextFill(Color.WHITE);
		
		rainbowSnakeToggle.setOnAction(e -> {
			Snake.rainbowSnake = rainbowSnakeToggle.isSelected();
			config.set("rainbowSnake", Snake.rainbowSnake);
		});
		
		options.setRight(rainbowSnakeToggle);
		bg.setBottom(options);
	}

	public void playGame() {
		fruit = addNewFruit();

		listenerActive = true;

		EventHandler<KeyEvent> keyEventHandler = new EventHandler<javafx.scene.input.KeyEvent>() {
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

		// Control scheme - WASD
		stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
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

	public Snake getSnake() {
		return snake;
	}

	public Rectangle getFruit() {
		return fruit;
	}

	public void setFruit(Rectangle fruit) {
		this.fruit = fruit;
	}

	public Text getScoreCounter() {
		return scoreDisplay;
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHighScore() {
		return highScore;
	}
	
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public static Config getConfig() {
		return config;
	}

	public MediaPlayer getMenuButtonClickPlayer() {
		return menuButtonClickPlayer;
	}

}