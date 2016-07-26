package dillon.gameAPI.core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import dillon.gameAPI.gui.GuiComponent;
import dillon.gameAPI.gui.GuiSystem;

public class TilesheetMenu implements GuiComponent {
	public TilesheetMenu() {
		try {
			menu = ImageIO
					.read(getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/tilesheetMenu.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BufferedImage menu;

	// Location 257, 213
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

	@Override
	public void render(Graphics2D g) {
		g.drawImage(menu, 257, 213, null);
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	@Override
	public void onMouseClickLeft(double x, double y) {
		if (x >= 262 && y >= 253 && x <= 548 && y <= 274) { // Import
			GuiSystem.removeGui(this, null);
			FileGetter.getFile(new Runnable() {
				@Override
				public void run() {
					try {
						int width = Integer.parseInt(JOptionPane.showInputDialog("Enter the width of each tile."));
						int height = Integer.parseInt(JOptionPane.showInputDialog("Enter the height of each tile."));
						String id = JOptionPane.showInputDialog("Enter an id for this tilesheet.");
						Tilesheet tilesheet = new Tilesheet();
						tilesheet.setId(id);
						tilesheet.setImg(ImageIO.read(FileGetter.getFile()));
						tilesheet.setTileHeight(height);
						tilesheet.setTileWidth(width);
						InformationHolder.addTilesheet(tilesheet);
					} catch (Exception ex) {
						ex.printStackTrace();
						System.exit(0);
					}
				}
			}, "Tilesheet");
		} else if (x >= 261 && x <= 550 && y >= 282 && y <= 306) { // Remove
			GuiSystem.removeGui(this, null);
			String id = JOptionPane.showInputDialog("Enter the id of the sheet.");
			InformationHolder.removeTilesheet(id);
		} else if (x >= 265 && x <= 548 && y >= 324 && y <= 347) { // Choose
			GuiSystem.removeGui(this, null);
			// Bring gui X: 20, Y: 20
			if (InformationHolder.tilesheetsEmpty()) {
				JOptionPane.showMessageDialog(null, "There are no tilesheets.");
			}
			GuiSystem.startGui(new TileChooser(), null);
		} else if (x >= 262 && x <= 552 && y >= 374 && y <= 397) { // Cancel
			GuiSystem.removeGui(this, null);
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
