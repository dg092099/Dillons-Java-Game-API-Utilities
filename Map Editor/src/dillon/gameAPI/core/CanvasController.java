package dillon.gameAPI.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TickEvent;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.utils.MainUtilities;

/**
 * This class is in control of the game's canvas.
 *
 * @author Dillon - Github dg092099
 */
class CanvasController extends Canvas implements Runnable {
	private static final long serialVersionUID = -3207927320425492600L;
	private static SecurityKey key;

	public CanvasController(SecurityKey k) {
		// Initially sets up the canvas and loads security key.
		this.setSize(new Dimension(Core.getWidth(), Core.getHeight()));
		this.setBackground(Color.BLACK);
		key = k;
	}

	private long startTime, endTime; // Time when loop starts, ends.
	private static int FPS = 30; // The target fps, defaults to 30.
	private boolean running = false; // Tells if the loop should keep running.
	private volatile boolean paused = false; // Tells if the update method
												// should occur.

	/**
	 * Preps the loop.
	 */
	public synchronized void start() {
		running = true;
	}

	/**
	 * Terminates the loop
	 */
	public synchronized void stop() {
		running = false;
	}

	/**
	 * This adjusts the current fps limit on the game.
	 *
	 * @param newFps
	 *            The new fps to set it to.
	 */
	public synchronized void setFps(final int newFps) {
		FPS = newFps;
	}

	/**
	 * This returns the current fps limit.
	 *
	 * @return current fps limit
	 */
	public int getFps() {
		return FPS;
	}

	/**
	 * Gives back the canvas to draw on.
	 *
	 * @return The canvas
	 */
	public Graphics2D getDrawingCanvas() {
		return graphics;
	}

	private boolean showingSplash = true; // Determines if a splash screen
											// should still be displayed.

	@Override
	public void run() {
		this.addMouseListener(new MouseListener() { // Mouse listener
			@Override
			public void mouseClicked(final MouseEvent evt) {
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseEntered(final MouseEvent evt) {
				// When a mouse enters the window.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseExited(final MouseEvent evt) {
				// When the mouse leaves the window.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mousePressed(final MouseEvent evt) {
				// When someone holds the mouse
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent evt) {
				// When someone releases the button.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem
							.broadcastMessage(
									new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
											MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
									MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem
							.broadcastMessage(
									new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
											MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
									MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(
							new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
									MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
							MouseEngineEvent.class, key);
					break;
				}
			}
		});
		this.addKeyListener(new KeyListener() { // Key listener

			@Override
			public void keyPressed(final KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (arg0.isShiftDown()) {
						// Shutdown key combination: Shift+escape
						Core.shutdown(true, key);
					}
				}
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_PRESS),
						KeyEngineEvent.class, key);
			}

			@Override
			public void keyReleased(final KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_RELEASE),
						KeyEngineEvent.class, key);
			}

			@Override
			public void keyTyped(final KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_TYPED),
						KeyEngineEvent.class, key);
			}
		});
		this.requestFocus();
		while (running) {
			final int framesInSecond = 1000 / FPS; // The amount of frames in a
													// second.
			startTime = System.currentTimeMillis(); // The starting time in the
													// loop.
			sendTick();
			sendRender();
			endTime = System.currentTimeMillis(); // The ending time in the loop
			final long diff = endTime - startTime;
			final long delta = framesInSecond - diff; // The calculated delta in
														// the
			// time.
			if (delta < -50) {
				Logger.getLogger("Core").warning("The game is behind by " + Math.abs(delta) + " ticks.");
			}
			try {
				Thread.sleep(delta);
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * This ticks everything that happens.
	 */
	public void sendTick() {
		// Sends the updates to the objects.
		if (paused) {
			return;
		}
		EventSystem.broadcastMessage(new TickEvent(), TickEvent.class, key);
		MainUtilities.executeQueue(key);
	}

	/**
	 * This pauses the update on the engine.
	 */
	public void pauseUpdate() {
		paused = true;
	}

	/**
	 * This unpauses the game.
	 */
	public void unpauseUpdate() {
		paused = false;
	}

	Graphics2D graphics; // The graphics for the canvas.
	private int splashCounter; // The counter to determine how long the splash
								// was on.
	private Image Splash; // The splash itself.
	private static Image background; // The background image.

	/**
	 * This function renders everything.
	 */
	public void sendRender() {
		// Causes the render process.
		final BufferStrategy buffer = getBufferStrategy(); // The buffer system
															// in the
															// rendering system.
		if (buffer == null) {
			// Then create the buffer strategy.
			createBufferStrategy(2);
			return;
		}
		graphics = (Graphics2D) getBufferStrategy().getDrawGraphics();
		graphics.setColor(graphics.getBackground());
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		// Start Draw
		if (background != null) {
			graphics.drawImage(background, 0, 0, null);
		}
		EventSystem.broadcastMessage(new RenderEvent(graphics), RenderEvent.class, key); // Render

		if (showingSplash) {
			splashCounter++; // To stop displaying splash after a while.
			if (splashCounter >= FPS * 2) {
				showingSplash = false;
			}
			try {
				if (Splash == null) {
					Splash = ImageIO
							.read(getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/splash.png"));
				}
				graphics.drawImage(Splash, Core.getWidth() - 100, Core.getHeight() - 50, null);
			} catch (final Exception e) {
			}
		}
		if (NetworkServer.getServerRunning()) {
			try {
				// Show networking icon if on.
				graphics.drawImage(
						ImageIO.read(
								getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/ServerImage.png")),
						Core.getWidth() - 30, 5, null);
			} catch (final Exception e) {
				e.printStackTrace();
				Core.crash(e, key);
			}
		}
		// End draw
		getBufferStrategy().show();
		graphics.dispose();
	}

	/**
	 * This sets the new background image.
	 *
	 * @param img
	 *            The new image to put in the background.
	 */
	public static void setBackgroundImage(final BufferedImage img) {
		background = img;
	}

	/**
	 * Gets the current background image.
	 *
	 * @return The background image.
	 */
	public static BufferedImage getBackgroundImage() {
		return (BufferedImage) background;
	}

	/**
	 * This method crashes the game and produces a stacktrace onto the screen.
	 *
	 * @param e
	 *            This is the exception that will be displayed.
	 */
	public void crash(final Exception e) {
		stop(); // Halts the game loop
		Logger.getLogger("Core").severe("Crashing...");
		final Font f = new Font("Courier", Font.BOLD, 18);
		this.setFont(f);
		this.setBackground(Color.WHITE);
		setBackgroundImage(null);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		graphics.setColor(Color.RED);
		this.getGraphics().drawString("An error has occured.", 15, 15);
		this.getGraphics().drawString(e.getMessage(), 15, 30);
		final StackTraceElement[] lines = e.getStackTrace();
		String formatted; // The formatted version of the stacktrace.
		for (int i = 0; i < lines.length; i++) {// Tries to display the crash
												// details on the screen.
			formatted = lines[i].getClassName() + "#" + lines[i].getMethodName() + " Line: " + lines[i].getLineNumber();
			this.getGraphics().drawString(formatted, 15, i * 15 + 45);
		}
	}

	/**
	 * Gets the current FPS
	 *
	 * @return FPS
	 */
	public static int getFPS() {
		return FPS;
	}

	/**
	 * Used for debugging a crash.
	 *
	 * @return Debug string.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\ndillon.gameAPI.core.CanvasController Dump:\n");
		String data = "";
		data = String.format("%-20s %-20s\n", "Key", "Value");
		data += String.format("%-20s %-20s\n", "---", "-----");
		data += String.format("%-20s %-20d\n", "Start Time: ", startTime);
		data += String.format("%-20s %-20d\n", "End Time:", endTime);
		data += String.format("%-20s %-20d\n", "FPS:", FPS);
		data += String.format("%-20s %-20s\n", "Running", running ? "Yes" : "No");
		data += String.format("%-20s %-20s\n", "Paused", paused ? "Yes" : "No");
		data += String.format("%-20s %-20s\n", "Showing Splash:", showingSplash ? "Yes" : "No");
		data += String.format("%-20s %-20d\n", "Splash counter:", splashCounter);
		data += String.format("%-20s %-20s\n", "Splash:", Splash != null ? Splash.toString() : "Not set.");
		data += String.format("%-20s %-20s\n", "Background:", background != null ? background.toString() : "Not set.");
		sb.append(data);
		return sb.toString();
	}
}
