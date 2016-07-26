package dillon.gameAPI.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import dillon.gameAPI.gui.GuiComponent;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.utils.MainUtilities;

public class TileEventsMenu implements GuiComponent {
	private int selected = -1;

	@Override
	public int getZIndex() {
		return 0;
	}

	@Override
	public void bringToFront() {
	}

	@Override
	public void dropBehind() {
	}

	private BufferedImage img;

	public void updateMenu() {
		try {
			img = ImageIO
					.read(getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/tileEventsMenu.png"));
			ArrayList<TileEvent> events = InformationHolder.getTileEvents();
			Graphics2D graphics = img.createGraphics();
			graphics.setFont(new Font("Calibri", Font.BOLD, 18));
			int counter = 0;
			for (TileEvent evt : events) {
				if (selected == counter) {
					graphics.setColor(Color.red);
				} else {
					graphics.setColor(Color.black);
				}
				graphics.drawString(evt.getAffectedTile().getxPos() + ", " + evt.getAffectedTile().getyPos() + ": "
						+ evt.getEntityType() + ", " + evt.getEventType().toString(), 5, counter * 20 + 44);
				counter++;
			}
			graphics.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TileEventsMenu() {
		updateMenu();
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(img, 257, 120, null);
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	public static TileEvent currentEvent = null;

	@Override
	public void onMouseClickLeft(double x, double y) {
		if (x >= 261 && x <= 324 && y >= 487 && y <= 513) { // New
			TileEvent event = new TileEvent();
			int choice = JOptionPane.showConfirmDialog(null,
					"Do you want to specify an entity type that triggers this?");
			if (choice == JOptionPane.YES_OPTION) {
				String type = JOptionPane.showInputDialog("Enter the type of entity.");
				event.setEntityType(type);
			} else {
				event.setEntityType("");
			}
			String location = JOptionPane
					.showInputDialog("Do you want it to fire when touched or clicked on?\n1. Touched\n2. Clicked");
			if (location.equals("1")) {
				event.setEventType(TileEvent.TileEventType.TOUCH);
			} else {
				event.setEventType(TileEvent.TileEventType.CLICK);
			}
			final TileEventsMenu instance = this;
			MainUtilities.executeWithEngine(new Runnable() {
				@Override
				public void run() {
					GuiSystem.removeGui(instance, null);
				}
			}, null);
			String methodName = JOptionPane
					.showInputDialog("Enter the name of the method to invoke when this occures.");
			event.setMethod(methodName);
			JOptionPane.showMessageDialog(null, "Click on the tile to use.");
			currentEvent = event;
			MainUtilities.executeWithEngine(new Runnable() {
				@Override
				public void run() {
					Core.stopChoosing();
				}
			}, null);
		} else if (x >= 333 && x <= 388 && y >= 488 && y <= 513) { // Delete
			if (selected > -1 && selected < InformationHolder.getTileEventsLength()) {
				InformationHolder.deleteTileEvent(selected);
				selected = -1;
				updateMenu();
			}
		} else if (x >= 395 && x <= 455 && y >= 486 && y <= 510) { // Close
			final TileEventsMenu instance = this;
			MainUtilities.executeWithEngine(new Runnable() {
				@Override
				public void run() {
					Core.stopChoosing();
					GuiSystem.removeGui(instance, null);
				}
			}, null);
		} else if (x >= 258 && x <= 522 && y >= 153 && y <= 474) { // Viewport
			int pos = (int) (y - 153);
			selected = (int) Math.floor(pos / 15);
			updateMenu();
		}
	}

	@Override
	public void onKeyPress(KeyEvent evt) {
	}

	@Override
	public void onUpdate() {
	}

	@Override
	public int[] getTopLeftCorner() {
		return new int[] { 0, 0 };
	}

	@Override
	public int[] getSize() {
		return new int[] { 813, 625 };
	}

	@Override
	public void slide(int x, int y) {
	}

}
