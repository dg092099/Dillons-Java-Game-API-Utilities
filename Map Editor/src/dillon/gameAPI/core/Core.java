package dillon.gameAPI.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import dillon.gameAPI.core.saving.MapLoader;
import dillon.gameAPI.core.saving.MapSaver;
import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.PromptEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.ShutdownEvent;
import dillon.gameAPI.gui.BasicDialog;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.gui.Prompt;
import dillon.gameAPI.modding.ModdingCore;
import dillon.gameAPI.networking.NetworkConnection;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.scripting.bridges.GuiFactory;
import dillon.gameAPI.scripting.bridges.RemoteCallBridge;
import dillon.gameAPI.scroller.Camera;
import dillon.gameAPI.scroller.ScrollManager;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;
import dillon.gameAPI.sound.PlayableSound;
import dillon.gameAPI.sound.SoundSystem;
import dillon.gameAPI.utils.MainUtilities;
import dillon.gameAPI.utils.Scheduling;

/**
 * Core file that has starting and stopping methods. A policy file should be
 * used to isolate the game to a certain directory.
 *
 * @author Dillon - Github dg092099
 */
public class Core {
	private static String TITLE; // The game's title.
	private static Image ICON; // The icon for the game.
	private static JFrame frame; // The JFrame window.
	public static final String ENGINE_VERSION = "MapEditor"; // The engine's
																// version.
	public static final int TILES = 1; // Constant: Render method, tile.
	public static final int SIDESCROLLER = 2; // Constant: render method,
												// sidescroller.

	private static BasicDialog helpMenu;
	private static ArrayList<BufferedImage> tileImages = new ArrayList<BufferedImage>();
	private static boolean choosingTile = false;

	public static void updateScreen() {
		tileImages.clear();
		for (Tilesheet t : InformationHolder.getTilesheets()) {
			int farthestRight = 0;
			int farthestDown = 0;
			for (Tile t2 : InformationHolder.getTiles()) {
				if (t2.getTilesheetId().equals(t.getId())) {
					if (t2.getxPos() > farthestRight) {
						farthestRight = t2.getxPos();
					}
					if (t2.getyPos() > farthestDown) {
						farthestDown = t2.getyPos();
					}
				}
			}
			BufferedImage img = new BufferedImage((farthestRight + 1) * t.getTileWidth(),
					(farthestDown + 1) * t.getTileHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			for (Tile t2 : InformationHolder.getTiles()) {
				if (t2.getTilesheetId().equals(t.getId())) {
					BufferedImage tile = t2.getTile();
					g.drawImage(tile, t.getTileWidth() * t2.getxPos(), t.getTileHeight() * t2.getyPos(), null);
				}
			}
			g.dispose();
			tileImages.add(img);
		}
	}

	public static void main(String[] args) {
		try {
			setup(813, 625, "Map Editor", null, null);
			startGame(60, null, null);
			helpMenu = new BasicDialog(
					"Welcome to the map editor V4 Map format V2! Shortcuts:\nCtrl+H: Brings help menu.\nCtrl+S: Save\nCtrl+L: Load\nCtrl+B: Set background\nCtrl+T: Tilesheets\nCtrl+M: Background music\nCtrl+E: Events\nCtrl+R: Scripts\nEsc: Exit without saving",
					new Font("Calibri", Font.PLAIN, 28), Color.blue, Color.white, Color.black, true, null);
			GuiSystem.startGui(helpMenu, null);
			EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {
				@Override
				public void handle(KeyEngineEvent evt) {
					if (evt.getMode().equals(KeyEngineEvent.KeyMode.KEY_PRESS)) {
						if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_ESCAPE) {
							choosingTile = true;
							Prompt p = new Prompt("Are you sure you want to exit?(Y/N)",
									new Font("Calibri", Font.BOLD, 28), Color.red, Color.white, Color.black, true,
									894484L, Color.green, null);
							GuiSystem.startGui(p, null);
						}
						if (!evt.getKeyEvent().isControlDown()) {
							return;
						}
						if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_H) {
							GuiSystem.startGui(helpMenu, null);
							choosingTile = true;
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_B) {
							MainUtilities.executeWithEngine(new Runnable() {
								@Override
								public void run() {
									choosingTile = true;
									FileGetter.getFile(new Runnable() {
										@Override
										public void run() {
											try {
												BufferedImage img = ImageIO.read(FileGetter.getFile());
												Core.setBackgroundImage(img, null);
												InformationHolder.setBackground(img);
												choosingTile = false;
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}, "Background");
								}
							}, null);
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_M) {
							MainUtilities.executeWithEngine(new Runnable() {
								@Override
								public void run() {
									choosingTile = true;
									FileGetter.getFile(new Runnable() {
										@Override
										public void run() {
											try {
												choosingTile = false;
												File f = FileGetter.getFile();
												InformationHolder.setBackgroundMusic(f);
												PlayableSound back = new PlayableSound(
														new BufferedInputStream(new FileInputStream(f)));
												SoundSystem.playSound(back, null);
												Scheduling.scheduleWaiting(TimeUnit.SECONDS, 3, new Runnable() {
													@Override
													public void run() {
														SoundSystem.stopSound(back, null);
													}
												}, null);
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}, "Map Music");
								}
							}, null);
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_T) {
							GuiSystem.startGui(new TilesheetMenu(), null);
							choosingTile = true;
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_E) {
							GuiSystem.startGui(new TileEventsMenu(), null);
							choosingTile = true;
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_S) {
							// Save
							choosingTile = true;
							JFileChooser fc = new JFileChooser();
							fc.setDialogTitle("Choose where to save the file.");
							fc.setMultiSelectionEnabled(false);
							fc.setFileFilter(new FileNameExtensionFilter("Map file", "*.map"));
							if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
								MapSaver.save(fc.getSelectedFile());
								BasicDialog bd = new BasicDialog("The file has been saved.",
										new Font("Calibri", Font.BOLD, 18), Color.green, Color.white, Color.black, true,
										null);
								GuiSystem.startGui(bd, null);
								Core.stopChoosing();
							}
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_L) {
							choosingTile = true;
							JFileChooser fc = new JFileChooser();
							fc.setDialogTitle("Choose the file to load.");
							fc.setMultiSelectionEnabled(false);
							fc.setFileFilter(new FileNameExtensionFilter("Map files", "map"));
							if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								MapLoader.load(fc.getSelectedFile());
								BasicDialog bd = new BasicDialog("The file has been loaded.",
										new Font("Calibri", Font.BOLD, 18), Color.green, Color.white, Color.black, true,
										null);
								GuiSystem.startGui(bd, null);
								Core.stopChoosing();
							}
						} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_R) { // Scripts
							choosingTile = true;
							GuiSystem.startGui(new ScriptsGui(), null);
						}
					}
				}
			}, null);
			EventSystem.addHandler(new EEHandler<PromptEvent>() {

				@Override
				public void handle(PromptEvent evt) { // Exit prompt
					if (evt.getId() == 894484L) {
						if (evt.getMsg().toLowerCase().contains("y")) {
							Core.shutdown(false, null);
						}
					}
				}
			}, null);
			EventSystem.addHandler(new EEHandler<MouseEngineEvent>() {
				@Override
				public void handle(MouseEngineEvent evt) {
					if (choosingTile) {
						return;
					}
					if (evt.getMouseMode() == MouseEngineEvent.MouseMode.HOLD) {
						if (evt.getMouseButton() == MouseEngineEvent.MouseButton.LEFT) { // Create
																							// tile.
							Tile tile = InformationHolder.getInUseTile();
							Tilesheet tilesheet = InformationHolder.getTilesheet(tile.getTilesheetId());
							int tileWidth = tilesheet.getTileWidth();
							int tileHeight = tilesheet.getTileHeight();
							int posX = (int) Math.floor((evt.getLocation().getX() + Camera.getXPos()) / tileWidth);
							int posY = (int) Math.floor((evt.getLocation().getY() + Camera.getYPos()) / tileHeight);
							Tile finalTile = tile.copy(posX, posY);
							InformationHolder.addTile(finalTile);
							if (TileEventsMenu.currentEvent != null) { // Event
																		// to
								// process
								TileEventsMenu.currentEvent.setAffectedTile(finalTile);
								InformationHolder.addTileEvent(TileEventsMenu.currentEvent.copy());
								TileEventsMenu.currentEvent = null;
								GuiSystem.startGui(
										new BasicDialog("Tile event bound.", new Font("Calibri", Font.BOLD, 18),
												Color.green, Color.white, Color.black, true, null),
										null);
							}
						}
						updateScreen();
					} else if (evt.getMouseMode().equals(MouseEngineEvent.MouseMode.CLICK)) {
						if (evt.getMouseButton() == MouseEngineEvent.MouseButton.RIGHT) { // Remove
							// tile
							Tile tile = InformationHolder.getInUseTile();
							Tilesheet tilesheet = InformationHolder.getTilesheet(tile.getTilesheetId());
							int tileWidth = tilesheet.getTileWidth();
							int tileHeight = tilesheet.getTileHeight();
							int posX = (int) Math.floor((evt.getLocation().getX() + Camera.getXPos()) / tileWidth);
							int posY = (int) Math.floor((evt.getLocation().getY() + Camera.getYPos()) / tileHeight);
							InformationHolder.removeTile(posX, posY);
						}
					}
					updateScreen();
				}
			}, null);
			EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {

				@Override
				public void handle(KeyEngineEvent evt) {
					if (choosingTile) {
						return;
					}
					if (!evt.getMode().equals(KeyEngineEvent.KeyMode.KEY_PRESS)) {
						return;
					}
					if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_DOWN) {
						Camera.setY(Camera.getYPos() + 10, null);
					} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_UP) {
						Camera.setY(Camera.getYPos() - 10, null);
					} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_LEFT) {
						Camera.setX(Camera.getXPos() - 10, null);
					} else if (evt.getKeyEvent().getKeyCode() == KeyEvent.VK_RIGHT) {
						Camera.setX(Camera.getXPos() + 10, null);
					}
				}
			}, null);
			EventSystem.addHandler(new EEHandler<RenderEvent>() {
				@Override
				public void handle(RenderEvent evt) {
					if (choosingTile) {
						return;
					}
					Graphics2D g = evt.getGraphics();
					for (BufferedImage img : tileImages) {
						g.drawImage(img, 0 - Camera.getXPos(), 0 - Camera.getYPos(), null);
					}
				}
			}, null);
		} catch (

		Exception ex)

		{
			ex.printStackTrace();
		}

	}

	public static void stopChoosing() {
		choosingTile = false;
	}

	/**
	 * This method starts the game with the specified background and fps. <b>Use
	 * setup method before this method.</b>
	 *
	 * @param FPS
	 *            The maximum frames per second to use.
	 * @param background
	 *            The default background image. to change it.
	 * @param key
	 *            The security key for the security module.
	 */
	public static void startGame(final int FPS, final BufferedImage background, final SecurityKey key) {
		if (frame == null) { // Frame must exist to use it.
			throw new GeneralRuntimeException("Invalid state: Use setup method first.");
		}
		SecuritySystem.checkPermission(key, RequestedAction.START_GAME); // Security
																			// check.
		Logger.getLogger("Core").info("Starting game.");
		controller.start();
		controller.setFps(FPS);
		final Thread t = new Thread(controller); // So that the game loop can't
		// interfere with other programming.
		t.setName("Canvas Controller");
		t.start();
		if (background != null) {
			// Set background image.
			CanvasController.setBackgroundImage(background);
		}
		// Setup auxiliary systems.
		new ScrollManager(engineKey);
		new Camera();
		guiSystem = new GuiSystem(engineKey);
		ModdingCore.sendPostStart();
	}

	/**
	 * Pauses the game.
	 *
	 * @param k
	 *            The security key.
	 */
	public static void pauseUpdate(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.PAUSE);
		controller.pauseUpdate();
	}

	/**
	 * Unpauses the game.
	 *
	 * @param k
	 *            The security key.
	 */
	public static void unpauseUpdate(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.UNPAUSE);
		controller.unpauseUpdate();
	}

	/**
	 * The background color.
	 *
	 * @return The background color.
	 */
	public static Color getBackColor() {
		return controller.getBackground();
	}

	/**
	 * This returns the title you specified for the game.
	 *
	 * @return Name
	 */
	public String getTitle() {
		return TITLE;
	}

	/**
	 * This returns the icon on the taskbar that you requested to be used.
	 *
	 * @return taskbar icon
	 */
	public Image getIcon() {
		return ICON;
	}

	private static SecurityKey engineKey;

	/**
	 * This method sets up the jframe.
	 *
	 * @param width
	 *            width of jframe.
	 * @param height
	 *            height of jframe.
	 * @param title
	 *            title of game.
	 * @param icon
	 *            Icon to use in the corner of the program - optional, the
	 *            engine has its own, but it's ugly so use your own.
	 * @param k
	 *            The security key.
	 * @throws IOException
	 *             Results because the icon that you specified couldn't be
	 *             found.
	 */
	public static void setup(final int width, final int height, final String title, final Image icon, SecurityKey k)
			throws IOException {
		SecuritySystem.checkPermission(k, RequestedAction.SETUP_GAME); // Security
																		// check.
		engineKey = SecuritySystem.init(); // Obtain engine key
		TITLE = title;
		ICON = icon;
		Logger.getLogger("Core").info("Setting up...");
		frame = new JFrame(title);
		// Setup canvas controller and window.
		controller = new CanvasController(engineKey);
		frame.getContentPane().setSize(new Dimension(width, height));
		frame.setTitle(title);
		frame.setResizable(false);
		if (icon != null) {
			frame.setIconImage(icon);
		} else {
			frame.setIconImage(
					ImageIO.read(Core.class.getClassLoader().getResourceAsStream("dillon/gameAPI/res/logo.png")));
		}
		// Add listeners
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(final WindowEvent arg0) {
			}

			@Override
			public void windowClosed(final WindowEvent arg0) {
			}

			@Override
			public void windowClosing(final WindowEvent arg0) { // Shutdown
				shutdown(false, engineKey);
			}

			@Override
			public void windowDeactivated(final WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(final WindowEvent arg0) {
			}

			@Override
			public void windowIconified(final WindowEvent arg0) {
			}

			@Override
			public void windowOpened(final WindowEvent arg0) {
			}
		});
		controller = new CanvasController(engineKey);
		// Touch up on window.
		frame.add(controller);
		frame.pack();
		frame.setLocationRelativeTo(null);
		ModdingCore.sendInit();
		frame.setVisible(true); // Show window
		guiFactory = new GuiFactory(engineKey);
		scriptRemote = new RemoteCallBridge(engineKey);
	}

	public static CanvasController getController() {
		return controller;
	}

	private static GuiFactory guiFactory;
	private static boolean fullscreen = false;
	private static RemoteCallBridge scriptRemote;

	public static RemoteCallBridge getRemoteBridge() {
		return scriptRemote;
	}

	public static GuiFactory getGuiFactory() {
		return guiFactory;
	}

	/**
	 * Sets if the screen should be fullscreen.
	 *
	 * @param b
	 *            Weather or not it should be fullscreen.
	 * @param k
	 *            The security key.
	 */
	public static void setFullScreen(final boolean b, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_FULLSCREEN);
		final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices(); // Gets
																												// devices
		if (devices[0].isFullScreenSupported()) {
			fullscreen = b;
			devices[0].setFullScreenWindow(fullscreen ? frame : null);
		}

	}

	/**
	 * Returns jframe width
	 *
	 * @return width
	 */
	public static int getWidth() {
		return frame.getContentPane().getWidth();
	}

	/**
	 * Returns jframe height
	 *
	 * @return height
	 */
	public static int getHeight() {
		return frame.getContentPane().getHeight();
	}

	static CanvasController controller;

	/**
	 * This method shuts off the event system and activates CanvasController's
	 * Crash to stop the engine.
	 *
	 * @param e
	 *            The exception that was thrown.
	 * @param k
	 *            The security key.
	 */
	public static void crash(final Exception e, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.CRASH_GAME);
		try {
			// Crash game.
			NetworkServer.stopServer(engineKey);
			NetworkConnection.disconnect(engineKey);
			EventSystem.override();
			controller.crash(e);
		} catch (final Exception e2) {
			e2.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This method will shutdown the game. This occurs when the x is clicked,
	 * the key combination shift + escape is used, or the game crashes.
	 *
	 * @param hard
	 *            Determines if the api would yield to the event system for
	 *            shutdown, or if it will shutdown directly.
	 * @param k
	 *            The security key.
	 */
	public static void shutdown(final boolean hard, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SHUTDOWN);
		if (hard) { // If the game should immediately shutdown.
			Logger.getLogger("Core").severe("Engine Shutting down...");
			NetworkServer.stopServer(engineKey);
			NetworkConnection.disconnect(engineKey);
			NetworkServer.disableDiscovery(engineKey);
			System.exit(0);
		} else {
			Logger.getLogger("Core").severe("Engine Shutting down...");
			EventSystem.broadcastMessage(new ShutdownEvent(), ShutdownEvent.class, engineKey);
			controller.stop();
			Logger.getLogger("Core").severe("Stopping server...");
			NetworkServer.stopServer(engineKey);
			NetworkConnection.disconnect(engineKey);
			NetworkServer.disableDiscovery(engineKey);
			System.exit(0);
		}
	}

	/**
	 * This method sets the maximum frames per second that this game should run
	 * on.
	 *
	 * @param fps
	 *            The new fps limit.
	 * @param k
	 *            The security key.
	 */
	public static void setFPS(final int fps, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_FPS);
		controller.setFps(fps);
	}

	/**
	 * This method will change the color of the background on the game.
	 *
	 * @param c
	 *            The new color.
	 * @param k
	 *            The security key
	 */
	public static synchronized void setBackColor(final Color c, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_BACKGROUND_COLOR);
		controller.setBackground(c);
	}

	/**
	 * This function will return the current version of the engine.
	 *
	 * @return Version string
	 */
	public static String getVersion() {
		return ENGINE_VERSION;
	}

	/**
	 * Gets the background image.
	 *
	 * @return The background image.
	 */
	public static BufferedImage getBackgroundImage() {
		return CanvasController.getBackgroundImage();
	}

	/**
	 * Gets the current fps.
	 *
	 * @return FPS
	 */
	public static int getFPS() {
		return CanvasController.getFPS();
	}

	/**
	 * Sets the background image
	 *
	 * @param background
	 *            The image
	 * @param k
	 *            The security key.
	 */
	public static void setBackgroundImage(final BufferedImage background, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_BACKGROUND_IMAGE);
		CanvasController.setBackgroundImage(background);
	}

	/**
	 * Used for debugging a crash.
	 *
	 * @return Debugging string
	 */
	public static String getDebug() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\ndillon.gameAPI.core.Core Dump:\n");
		String data = "";
		data += String.format("%-13s %-15s\n", "Key", "Value");
		data += String.format("%-13s %-15s\n", "---", "-----");
		data += String.format("%-13s %-15s\n", "Title:", TITLE);
		data += String.format("%-13s %-15s\n", "Icon:", ICON != null ? ICON.toString() : "None");
		data += String.format("%-13s %-15s\n", "Frame:", frame.toString());
		data += String.format("%-13s %-15s\n", "Fullscreen", fullscreen ? "Yes" : "No");
		sb.append(data);
		return sb.toString();
	}

	private static GuiSystem guiSystem;

	public static GuiSystem getGuiSystem() {
		return guiSystem;
	}
}
