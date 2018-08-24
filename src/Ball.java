import java.awt.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class that implements a ball with a position and velocity.
 */
public class Ball {
	// Constants
	/**
	 * The radius of the ball.
	 */
	public static final int BALL_RADIUS = 8;
	/**
	 * The initial velocity of the ball in the x direction.
	 */
	public static final double INITIAL_VX = 1.5e-7;
	/**
	 * The initial velocity of the ball in the y direction.
	 */
	public static final double INITIAL_VY = -1.5e-7;

	// Instance variables
	// (x,y) is the position of the center of the ball.
	private double x, y;
	private double vx, vy;
	private Circle circle;

	/**
	 * @return the Circle object that represents the ball on the game board.
	 */
	public Circle getCircle () {
		return circle;
	}

	/**
	 * Constructs a new Ball object at the centroid of the game board
	 * with a default velocity that points down and right.
	 */
	public Ball () {
		resetBall();
		circle = new Circle(BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
		circle.setLayoutX(x - BALL_RADIUS);
		circle.setLayoutY(y - BALL_RADIUS);
		circle.setFill(Color.BLACK);
	}
	
	/**
	 * increases the speed of the ball by a given factor
	 * @param factor the factor to multiply the speed by
	 */
	public void speedUp (double factor) {
		vx *= factor;
		vy *= factor;
	}
	
	/**
	 * @return the x position of the ball
	 */
	public double getX () {
		return x;
	}
	
	/**
	 * @return the y position of the ball
	 */
	public double getY () {
		return y;
	}
	
	/**
	 * Sets the x position of the ball
	 * @param x the value to set the x position of the ball
	 */
	public void setX (double x) {
		this.x = x;
	}
	
	/**
	 * Sets the y position of the ball
	 * @param y the value to set the y position of the ball
	 */
	public void setY (double y) {
		this.y = y;
	}

	/**
	 * Updates the position of the ball, given its current position and velocity,
	 * based on the specified elapsed time since the last update.
	 * @param deltaNanoTime the number of nanoseconds that have transpired since the last update
	 */
	public void updatePosition (long deltaNanoTime) {
		double dx = vx * deltaNanoTime;
		double dy = vy * deltaNanoTime;
		x += dx;
		y += dy;

		circle.setTranslateX(x - (circle.getLayoutX() + BALL_RADIUS));
		circle.setTranslateY(y - (circle.getLayoutY() + BALL_RADIUS));
	}
	
	/**
	 * Multiplies the horizontal velocity of the ball by negative one, simulating a horizontal bounce
	 */
	public void horizontalBounce () {
		vx *= -1;
	}
	
	/**
	 * Multiplies the vertical velocity of the ball by negative one, simulating a vertical bounce
	 */
	public void verticalBounce () {
		vy *= -1;
	}
	
	/**
	 * Sends the ball of in a direction vertically; used to let the paddle control the direction of the ball's bounce
	 * @param direction an int to steer the ball; positive or negative one will send the ball to the right or left respectively
	 */
	public void veerDirection (int direction) {
		vx = -Math.abs(vx) * direction;
		
	}
	
	/**
	 * Resets the ball to its starting position
	 */
	public void resetBall () {
		x = GameImpl.WIDTH/2;
		y = GameImpl.HEIGHT/2;
		vx = INITIAL_VX;
		vy = INITIAL_VY;
	}
}
