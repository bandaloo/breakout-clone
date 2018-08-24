import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.*;


public class BlockField {
	byte[][] blockArray;
	Rectangle[][] rectangleArray;
	double blockWidth;
	double blockHeight;
	double fieldHeight;
	int currentBlockNum;
	int initialBlockNum;
	ArrayList<Image> imageList = new ArrayList<Image>();
	ArrayList<Label> animalList = new ArrayList<Label>();
	ArrayList<Color> colorList = new ArrayList<Color>();
	
	/**
	 * Constructs a new BlockField with specifications for the amount of vertical and horizontal blocks
	 * with the given height over width ratio, allowing for many kinds of rectangles
	 * @param hBlockNum the amount of horizontal blocks
	 * @param vBlockNum the amount of vertical blocks
	 * @param heightWidthRatio determines the dimensions of the rectangle representing a block.
	 */
	public BlockField (int hBlockNum, int vBlockNum, double heightWidthRatio) {
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLUE);
		
		imageList.add(new Image(getClass().getResourceAsStream("duck.jpg")));
		imageList.add(new Image(getClass().getResourceAsStream("goat.jpg")));
		imageList.add(new Image(getClass().getResourceAsStream("horse.jpg")));
		imageList.add(new Image(getClass().getResourceAsStream("duck.jpg")));
		imageList.add(new Image(getClass().getResourceAsStream("goat.jpg")));
		
		animalList = makeLabelList(imageList);
		
		blockArray = new byte[hBlockNum][vBlockNum + 2];
		rectangleArray = new Rectangle[hBlockNum][vBlockNum + 2];
		blockWidth = GameImpl.WIDTH/hBlockNum;
		blockHeight = blockWidth * heightWidthRatio;
		fieldHeight = blockHeight * getVerticalNum();
		
		currentBlockNum = getHorizontalNum() * (getVerticalNum() - 2);
		initialBlockNum = currentBlockNum;
				
		for (int i = 0; i < getHorizontalNum(); i++) {
			for (int j = 1; j < getVerticalNum() - 1; j++) {
				blockArray[i][j] = 1;
				rectangleArray[i][j] = makeRectangleFromPosition(i, j);
			}
		}
	}
	
	/**
	 * @param m horizontal array location of the block
	 * @param n vertical array location of the block
	 * @return a new Rectangle based on its position to get a diagonal striped pattern
	 */
	public Rectangle makeRectangleFromPosition(int m, int n) {
		Color blockColor = colorList.get((m + n) % colorList.size());
		Rectangle blockRectangle = new Rectangle(0, 0, blockWidth, blockHeight);
		blockRectangle.setLayoutX(m * blockWidth);
		blockRectangle.setLayoutY(n * blockHeight);
		blockRectangle.setFill(blockColor);
		return blockRectangle;
	}
	
	/**
	 * @param i position of the block in the array
	 * @param dimension blockWidth or blockHeight depending if you want to find a horizontal or vertical line
	 * @return the location of a line running through the center of that block
	 */
	public double blockCenter (double i, double dimension) {
		return (Math.floor(i / dimension)) * dimension + dimension / 2;
	}
	
	/**
	 * @param x position on x axis
	 * @param y position on y axis
	 * @return the array coordinates of a block given its spatial position
	 */
	int[] blockValue (double x, double y) {
		int[] coordinates = new int[2];
		coordinates[0] = (int) Math.floor(x / blockWidth);
		coordinates[1] = (int) Math.floor(y / blockHeight);
		return coordinates;
	}
	
	/**
	 * Bounces the ball of blocks and calls destroyBlock() when ball hits a block
	 * @param ball the Ball that is interacting with the blockField
	 * @return the Rectangle so instance of GameImpl may remove it from its children
	 */
	public Rectangle fieldCollision (Ball ball) {
		// bouncing off left and right walls
		if (ball.getX() < 0 + Ball.BALL_RADIUS) {
			ball.horizontalBounce();
			ball.setX(Ball.BALL_RADIUS);
		}
		else if (ball.getX() > GameImpl.WIDTH - Ball.BALL_RADIUS) {
			ball.horizontalBounce();
			ball.setX(GameImpl.WIDTH - Ball.BALL_RADIUS);
		}
		
		// bouncing of the ceiling
		if (ball.getY() < 0 + Ball.BALL_RADIUS) {
			ball.verticalBounce();
			ball.setY(Ball.BALL_RADIUS);
		}
		
		// checking for collisions with adjacent block areas
		if (ball.getY() <= fieldHeight) {
			byte leftRight = 0, upDown = 0;
			double xCenter = blockCenter(ball.getX(), blockWidth);
			double yCenter = blockCenter(ball.getY(), blockHeight);
			if (ball.getY() - Ball.BALL_RADIUS < yCenter - blockHeight / 2) {
				upDown = -1;
			}
			else if (ball.getY() + Ball.BALL_RADIUS > yCenter + blockHeight / 2) {
				upDown = 1;
			}
			if (ball.getX() - Ball.BALL_RADIUS < xCenter - blockWidth / 2) {
				leftRight = -1;
			}
			else if (ball.getX() + Ball.BALL_RADIUS > xCenter + blockWidth / 2) {
				leftRight = 1;
			}
			
			int[] coordinates = blockValue(ball.getX(), ball.getY());
			
			// bounce and destroy blocks if there is a collision at the block area the ball is overlapping
			if (solidAt(coordinates[0] + leftRight, coordinates[1])) {
				ball.horizontalBounce();
				destroyBlock(coordinates[0] + leftRight, coordinates[1], ball);
				return rectangleArray[coordinates[0] + leftRight][coordinates[1]];
			}
			else if (solidAt(coordinates[0],coordinates[1] + upDown)) {
				ball.verticalBounce();
				destroyBlock(coordinates[0], coordinates[1] + upDown, ball);
				return rectangleArray[coordinates[0]][coordinates[1] + upDown];
			}
		}
		return null;
	}
	
	/**
	 * Updates blockArray to reflect destroyed blocks; only called when there is a block in that position
	 * @param m horizontal array position of block
	 * @param n vertical array position of block
	 * @param ball the Ball that is sped up by destroying a block
	 */
	public void destroyBlock (int m, int n, Ball ball) {
		blockArray[m][n] = 0;
		ball.speedUp(GameImpl.SPEED_FACTOR);
		currentBlockNum--;
	}
	
	/**
	 * @param m horizontal array position of block
	 * @param n	vertical array position of block
	 * @return boolean representing whether there is a block at that position
	 */
	private boolean solidAt (int m, int n) {
		if (n >= getVerticalNum()) {
			return false;
		}
		else return blockArray[m][n] != 0;
	}
	
	/**
	 * @param m horizontal array position of block
	 * @param n vertical array position of block
	 * @return the Rectangle stored in rectangleArray
	 */
	public Rectangle getRectangleFromPosition(int m, int n) {
		return rectangleArray[m][n];
	}
	
	/**
	 * @return the width of blockArray, which is equal to the amount of horizontal blocks
	 */
	public int getHorizontalNum () {
		return blockArray.length;
	}
	
	/**
	 * @return the height of blockArray, which is two larger than the amount of vertical blocks, since the bottom and top rows don't have blocks
	 */
	public int getVerticalNum () {
		return blockArray[0].length;
	}
	
	/**
	 * @return boolean representing if the field of blocks has been cleared
	 */
	public boolean isEmpty () {
		return currentBlockNum == 0;
	}
	
	/**
	 * @return an ArrayList of Labels for the animals, indicating lives in the bottom left corner of the screen
	 */
	public ArrayList<Label> getAnimalList () {
		return animalList;
	}
	
	/**
	 * @param imageList a list of images to be made into Labels
	 * @return a new ArrayList of labels based on the given ArrayList of images
	 */
	private ArrayList<Label> makeLabelList (ArrayList<Image> imageList) {
		ArrayList<Label> labelList = new ArrayList<Label>();
		int currentPosition = 0;
		for (int i = 0; i < imageList.size(); i++) {
			Image image = imageList.get(i);
			Label imageLabel = new Label("", new ImageView(image));
			currentPosition += image.getWidth();
			imageLabel.setLayoutX(GameImpl.WIDTH - currentPosition);
			imageLabel.setLayoutY(GameImpl.HEIGHT - image.getHeight());
			labelList.add(imageLabel);
		}
		return labelList;
	}
}







