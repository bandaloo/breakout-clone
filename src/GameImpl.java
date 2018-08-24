import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.event.*;

import javafx.scene.shape.Rectangle;


public class GameImpl extends Pane implements Game {
	/**
	 * Defines different states of the game.
	 */
	public enum GameState {
		WON, LOST, ACTIVE, NEW
	}
	
	boolean notStarted = true;
	boolean awaitingClick;

	// Constants
	/**
	 * The width of the game board.
	 */

	public static final int WIDTH = 400;
	/**
	 * The height of the game board.
	 */
	public static final int HEIGHT = 600;
	
	public static final double SPEED_FACTOR = 1.05;

	// Instance variables
	private Ball ball;
	private Paddle paddle;
	private BlockField blockField;	
	int lives;

	/**
	 * Constructs a new GamePane.
	 */
	public GameImpl () {
		setStyle("-fx-background-color: white;");

		restartGame(GameState.NEW);
	}

	public String getName () {
		return "Zutopia";
	}

	public Pane getPane () {
		return this;
	}

	private void restartGame (GameState state) {
		awaitingClick = true;
		getChildren().clear();  // remove all components from the game

		// Create and add ball
		ball = new Ball();
		getChildren().add(ball.getCircle());  // Add the ball to the game board

		// Create and add blockField
		blockField = new BlockField(8, 6, 0.6);
		
		// Add the rectangles from blockField
		for (int i = 0; i < blockField.getHorizontalNum(); i++) {
			for (int j = 1; j < blockField.getVerticalNum() - 1; j++) {
				getChildren().add(blockField.getRectangleFromPosition(i, j));
			}
		}
		
		// Add the animals from blockField
		for (int i = 0; i < blockField.getAnimalList().size(); i++) {
			getChildren().add(blockField.getAnimalList().get(i));
		}
		
		lives = blockField.getAnimalList().size();

		
		// Create and add paddle
		paddle = new Paddle();
		getChildren().add(paddle.getRectangle());  // Add the paddle to the game board
		
		// Add start message
		final String message;
		if (state == GameState.LOST) {
			message = "Game Over\n";
		} else if (state == GameState.WON) {
			message = "You won!\n";
		} else {
			message = "";
		}
		final Label startLabel = new Label(message + "Click mouse to start");
		state = GameState.NEW;
		startLabel.setLayoutX(WIDTH / 2 - 50);
		startLabel.setLayoutY(HEIGHT / 2 + 100);
		getChildren().add(startLabel);
		
		// Add event handler to start the game
		setOnMouseClicked(new EventHandler<MouseEvent> () {
			@Override
			public void handle (MouseEvent e) {
				// As soon as the mouse is clicked, remove the startLabel from the game board
				getChildren().remove(startLabel);
				run();
				awaitingClick = false;
			}
		});
		
		// Add another event handler to steer paddle...
		setOnMouseMoved(new EventHandler<MouseEvent> () {
			public void handle (MouseEvent e) {
				paddle.moveTo(e.getSceneX(), paddle.getY());
			}
		});};

	/**
	 * Begins the game-play by creating and starting an AnimationTimer.
	 */
	public void run () {
		// This if statement fixes the bug that was in the starter code causing the ball to speed up when the mouse was clicked.
		if (notStarted) {
			// Instantiate and start an AnimationTimer to update the component of the game.
			new AnimationTimer () {
				private long lastNanoTime = -1;
				public void handle (long currentNanoTime) {
					if (lastNanoTime >= 0) {  // Necessary for first clock-tick.
						GameState state;
						if ((state = runOneTimestep(currentNanoTime - lastNanoTime)) != GameState.ACTIVE) {
							// Once the game is no longer ACTIVE, stop the AnimationTimer.
							stop();
							// Restart the game, with a message that depends on whether
							// the user won or lost the game.
							restartGame(state);
						}
					}
					// Keep track of how much time actually transpired since the last clock-tick.
					lastNanoTime = currentNanoTime;
				}
			}.start();}
		notStarted = false;
	}

	/**
	 * Updates the state of the game at each timestep. In particular, this method should
	 * move the ball, check if the ball collided with any of the animals, walls, or the paddle, etc.
	 * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
	 * @return the current game state
	 */
	public GameState runOneTimestep (long deltaNanoTime) {
		// This if statement pauses the game even if there is an animation timer present.
		if (!awaitingClick) {
			ball.updatePosition(deltaNanoTime);
			Rectangle removalRectangle = blockField.fieldCollision(ball);
			if (removalRectangle != null) {
				getChildren().remove(removalRectangle);
			}
			
			final double paddleDifference = ball.getX() - paddle.getX();
			
			// If the ball is above the paddle and between the paddle's end points, the ball will bounce.
			if (Math.abs(paddleDifference) < Paddle.PADDLE_WIDTH / 2 && ball.getY() + Ball.BALL_RADIUS >= paddle.getY() && ball.getY() < paddle.getY()) {
				ball.verticalBounce();
				ball.veerDirection(paddleDifference > 0 ? -1 : 1);
				ball.setY(paddle.getY() - Ball.BALL_RADIUS);
			}
			
			// If the ball falls below the screen, your lives are reduced and the ball's position is reset.
			if (ball.getY() > HEIGHT) {
				getChildren().remove(blockField.getAnimalList().get(lives - 1));
				ball.resetBall();
				lives--;
				if (lives == 0) {
					restartGame(GameState.LOST);
				}
			}
			
			// If all the blocks are cleared, the game is won.
			if (blockField.isEmpty()) {
				restartGame(GameState.WON);
			}
		}
		return GameState.ACTIVE;
	}
}
