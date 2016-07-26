package dillon.gameAPI.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.PromptEvent;
import dillon.gameAPI.gui.GuiComponent;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.gui.Prompt;
import dillon.gameAPI.utils.MainUtilities;

public class TileChooser implements GuiComponent {
	String tilesheetId = "";
	boolean visible = true;
	boolean solid = true;
	String xPosVar = "";
	String yPosVar = "";
	String visibleVar = "";
	String solidityVar = "";

	public void updateMenu() {
		try {
			img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/tileChooser.png"));
			Graphics2D g = img.createGraphics();
			g.setFont(new Font("Calibri", Font.BOLD, 18));
			ArrayList<Tilesheet> tilesheets = InformationHolder.getTilesheets();
			int counter = 0;
			// X = 5
			// Y = counter * 20 + 70
			for (Tilesheet t : tilesheets) {
				if (tilesheetId.equals(t.getId())) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.black);
				}
				g.drawString(t.getId(), 5, counter * 20 + 70);
				counter++;
			}
			if (visible) {
				g.setColor(Color.black);
				g.drawLine(223, 525, 242, 543);
				g.drawLine(222, 543, 242, 524);
			}
			if (solid) {
				g.setColor(Color.black);
				g.drawLine(340, 527, 357, 542);
				g.drawLine(358, 527, 338, 543);
			}
			if (!xPosVar.isEmpty()) {
				g.setFont(new Font("Calibri", Font.BOLD, 18));
				int width = g.getFontMetrics().stringWidth(xPosVar);
				String test = xPosVar;
				while (width > 68) {
					test = test.substring(0, test.length() - 1);
					width = g.getFontMetrics().stringWidth(test);
				}
				g.setColor(Color.black);
				g.drawString(test, 459, 535);
			}
			if (!yPosVar.isEmpty()) {
				g.setFont(new Font("Calibri", Font.BOLD, 18));
				int width = g.getFontMetrics().stringWidth(yPosVar);
				String test = yPosVar;
				while (width > 48) {
					test = test.substring(0, test.length() - 1);
					width = g.getFontMetrics().stringWidth(test);
				}
				g.setColor(Color.black);
				g.drawString(test, 232, 568);
			}
			if (!visibleVar.isEmpty()) {
				g.setFont(new Font("Calibri", Font.BOLD, 18));
				int width = g.getFontMetrics().stringWidth(visibleVar);
				String test = visibleVar;
				while (width > 71) {
					test = test.substring(0, test.length() - 1);
					width = g.getFontMetrics().stringWidth(test);
				}
				g.setColor(Color.black);
				g.drawString(test, 363, 566);
			}
			if (!solidityVar.isEmpty()) {
				g.setFont(new Font("Calibri", Font.BOLD, 18));
				int width = g.getFontMetrics().stringWidth(solidityVar);
				String test = solidityVar;
				while (width > 40) {
					test = test.substring(0, test.length() - 1);
					width = g.getFontMetrics().stringWidth(test);
				}
				g.setColor(Color.black);
				g.drawString(test, 546, 569);
			}
			if (!tilesheetId.isEmpty()) {
				Tilesheet tilesheet = InformationHolder.getTilesheet(tilesheetId);
				BufferedImage img = new BufferedImage(tilesheet.getImg().getWidth(), tilesheet.getImg().getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = img.createGraphics();
				g2.drawImage(tilesheet.getImg(), 0, 0, null);
				g2.dispose();
				if (tilesheet.getImg().getWidth() > 557 || tilesheet.getImg().getHeight() > 460) {
					// Too big for viewport
					if (img.getWidth() > 557) {
						img = img.getSubimage(0, 0, 557, img.getHeight());
					}
					if (img.getHeight() > 460) {
						img = img.getSubimage(0, 0, img.getWidth(), 460);
					}
				}
				g.drawImage(img, 216, 51, null);

				if (selectedTileX != -1 && selectedTileY != -1) { // Tile
																	// selected
					BufferedImage sample = tilesheet.getImg().getSubimage(tilesheet.getTileWidth() * selectedTileX,
							tilesheet.getTileHeight() * selectedTileY, tilesheet.getTileWidth(),
							tilesheet.getTileHeight());
					Color inverse = MainUtilities.getInverseColor(MainUtilities.getAverageColorInImage(sample));
					g.setColor(inverse);
					g.drawRect(tilesheet.getTileWidth() * selectedTileX + 216,
							tilesheet.getTileHeight() * selectedTileY + 51, tilesheet.getTileWidth(),
							tilesheet.getTileHeight());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static boolean listenerRegistered = false;

	public TileChooser() {
		updateMenu();
		if (listenerRegistered) {
			return;
		}
		EventSystem.addHandler(new EEHandler<PromptEvent>() {
			@Override
			public void handle(PromptEvent evt) {
				if (evt.getId() == -9911510L) { // X Pos box 68 pixels wide
					xPosVar = evt.getMsg();
					updateMenu();
				} else if (evt.getId() == 7718184161L) { // Y pos box 48 pixels
															// wide
					yPosVar = evt.getMsg();
					updateMenu();
				} else if (evt.getId() == 330005600L) { // Visible variable
					visibleVar = evt.getMsg();
					updateMenu();
				} else if (evt.getId() == 7891561321651L) { // Solidity
					solidityVar = evt.getMsg();
					updateMenu();
				}
			}
		}, null);
		listenerRegistered = true;
	}

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

	@Override
	public void render(Graphics2D g) {
		g.drawImage(img, 20, 20, null);
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	@Override
	public void onMouseClickLeft(double x, double y) {
		if (x >= 735 && x <= 780 && y >= 28 && y <= 63) {
			GuiSystem.removeGui(this, null);
			MainUtilities.executeWithEngine(new Runnable() {
				@Override
				public void run() {
					Core.stopChoosing();
				}
			}, null);
			return;
		}
		if (x >= 23 && x <= 229 && y >= 72 && y <= 601) { // Choose tilesheet
			double difference = y - 72;
			int selection = (int) difference / 20;
			if (selection < InformationHolder.getTilesheets().size()) {
				tilesheetId = InformationHolder.getTilesheets().get(selection).getId();
				updateMenu();
			}
			return;
		}
		if (x >= 243 && x <= 263 && y >= 544 && y <= 563) { // Visible
			visible = !visible;
			updateMenu();
			return;
		}
		if (x >= 359 && x <= 376 && y >= 547 && y <= 563) { // Walkthrough
			solid = !solid;
			updateMenu();
			return;
		}
		if (x >= 469 && x <= 557 && y >= 543 && y <= 568) { // X pos variable
			Prompt p = new Prompt("Enter the name of the variable for the x position.",
					new Font("Calibri", Font.PLAIN, 18), Color.blue, Color.white, Color.black, true, -9911510L,
					Color.black, null);
			GuiSystem.startGui(p, null);
			return;
		}
		if (x >= 239 && x <= 313 && y >= 573 && y <= 599) { // Y pos variable
			Prompt p = new Prompt("Enter the name of the variable for the y position.",
					new Font("Calibri", Font.PLAIN, 18), Color.blue, Color.white, Color.black, true, 7718184161L,
					Color.black, null);
			GuiSystem.startGui(p, null);
			return;
		}
		if (x >= 373 && x <= 466 && y >= 571 && y <= 601) { // Visible variable
			Prompt p = new Prompt("Enter the name of the variable for the visibility.",
					new Font("Calibri", Font.PLAIN, 18), Color.blue, Color.white, Color.black, true, 330005600L,
					Color.black, null);
			GuiSystem.startGui(p, null);
		}
		if (x >= 551 && x <= 618 && y >= 573 && y <= 602) { // Solid variable
			Prompt p = new Prompt("Enter the name of the variable for the solidity.",
					new Font("Calibri", Font.PLAIN, 18), Color.blue, Color.white, Color.black, true, 7891561321651L,
					Color.black, null);
			GuiSystem.startGui(p, null);
		}
		if (x >= 236 && x <= 794 && y >= 71 && y <= 531) { // Inside viewport
			int xDiff = (int) x - 236;
			int yDiff = (int) y - 71;
			int tileWidth = InformationHolder.getTilesheet(tilesheetId).getTileWidth();
			int tileHeight = InformationHolder.getTilesheet(tilesheetId).getTileHeight();
			selectedTileX = (int) Math.floor(xDiff / (double) tileWidth);
			selectedTileY = (int) Math.floor(yDiff / (double) tileHeight);
			updateMenu();
		}
		if (x >= 699 && x <= 788 && y >= 536 && y <= 599) { // Done button
			Tile t = new Tile();
			t.setSheetPosX(selectedTileX);
			t.setSheetPosY(selectedTileY);
			t.setSolid(solid);
			t.setSolidVar(solidityVar);
			t.setTilesheetId(tilesheetId);
			t.setVarLocationX(xPosVar);
			t.setVarLocationY(yPosVar);
			t.setVisible(visible);
			t.setVisibleVar(visibleVar);
			InformationHolder.setInUseTile(t);
			GuiSystem.removeGui(this, null);
			MainUtilities.executeWithEngine(new Runnable() {
				@Override
				public void run() {
					Core.stopChoosing();
				}
			}, null);
		}
	}

	private int selectedTileX = -1, selectedTileY = -1;

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
