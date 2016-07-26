package dillon.gameAPI.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import dillon.gameAPI.errors.EngineSecurityError;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This class contains miscellaneous methods that are more or less utilities for
 * the engine.
 *
 * @author Dillon - Github dg092099
 *
 */
public class MainUtilities {
	/**
	 * This method will get the inverse of the coordinate according to the
	 * image's height. This is used to invert the x position of the camera
	 * because the image is rendered from the top left corner. This makes it so
	 * that when it is rendered, it is in the correct position.
	 *
	 * @param x
	 *            The value to invert
	 * @param height
	 *            The height of the image to invert around.
	 * @return The inverted number.
	 */
	public static int getInverse(int x, int height) {
		int half = height / 2;
		int diff = half - x;
		return half + diff;
	}

	private static final ArrayList<Runnable> queue = new ArrayList<Runnable>(); // Things
																				// to
																				// run
																				// in
																				// sync
																				// with
																				// the
																				// engine.

	/**
	 * Sets the action to run on time with the game engine.
	 *
	 * @param r
	 *            The action
	 * @param k
	 *            The security key
	 */
	public static synchronized void executeWithEngine(Runnable r, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.EXECUTE_WITH_ENGINE);
		queue.add(r);
	}

	/**
	 * Executes the queue. Only works when invoked by the canvas controller.
	 *
	 * @param k
	 *            The security key.
	 */
	public static synchronized void executeQueue(SecurityKey k) {
		if (!SecuritySystem.isEngineKey(k)) {
			throw new EngineSecurityError("Invalid key for operation.");
		}
		if (queue.size() == 0) {
			return;
		}
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).run();
		}
		queue.clear();
	}

	/**
	 * Returns the blue in a rgb value
	 *
	 * @param rgb
	 *            The rgb value
	 * @return the blue.
	 */
	public static int getBlue(int rgb) {
		return rgb & 0xFF;
	}

	/**
	 * Returns the red in a rgb value
	 *
	 * @param rgb
	 *            The rgb value
	 * @return the red.
	 */
	public static int getRed(int rgb) {
		return rgb >> 16 & 0xFF;
	}

	/**
	 * Returns the green in a rgb value
	 *
	 * @param rgb
	 *            The rgb value
	 * @return the green.
	 */
	public static int getGreen(int rgb) {
		return rgb >> 8 & 0xFF;
	}

	/**
	 * This method gets the average color in an image, useful for gausian blurs.
	 *
	 * @param img
	 *            The image
	 * @return The average color.
	 */
	public static Color getAverageColorInImage(BufferedImage img) {
		int red = 0;
		int green = 0;
		int blue = 0;
		double counter = 0;
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int color = img.getRGB(x, y);
				Color c = new Color(color);
				red += c.getRed();
				green += c.getGreen();
				blue += c.getBlue();
				counter++;
			}
		}
		return new Color((int) Math.floor(red / counter), (int) Math.floor(green / counter),
				(int) Math.floor(blue / counter));
	}

	/**
	 * This method gets the inverted color of another color.
	 *
	 * @param c
	 *            The color
	 * @return The inverted color.
	 */
	public static Color getInverseColor(Color c) {
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}

	public static boolean fileExistsWithinDirectory(File dir, String fileName) {
		if (!dir.isDirectory()) {
			return false;
		}
		File f = new File(dir.getAbsolutePath() + File.separator + fileName);
		return f.exists();
	}
}
