package dillon.gameAPI.core;

import java.awt.image.BufferedImage;

public class Tile {
	private int xPos = 0, yPos = 0; // World
	private String tilesheetId;
	private boolean visible = true, solid = true;
	private String varLocationX = "", varLocationY = ""; // World variables
	private String visibleVar = "", solidVar = "";
	private int sheetPosX = -1, sheetPosY = -1; // Tilesheet
	private BufferedImage img;

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public String getTilesheetId() {
		return tilesheetId;
	}

	public void setTilesheetId(String tilesheetId) {
		this.tilesheetId = tilesheetId;
		updateImage();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public String getVarLocationX() {
		return varLocationX;
	}

	public void setVarLocationX(String varLocationX) {
		this.varLocationX = varLocationX;
	}

	public String getVarLocationY() {
		return varLocationY;
	}

	public void setVarLocationY(String varLocationY) {
		this.varLocationY = varLocationY;
	}

	public String getVisibleVar() {
		return visibleVar;
	}

	public void setVisibleVar(String visibleVar) {
		this.visibleVar = visibleVar;
	}

	public String getSolidVar() {
		return solidVar;
	}

	public void setSolidVar(String solidVar) {
		this.solidVar = solidVar;
	}

	public int getSheetPosX() {
		return sheetPosX;
	}

	public void setSheetPosX(int sheetPosX) {
		this.sheetPosX = sheetPosX;
		updateImage();
	}

	public int getSheetPosY() {
		return sheetPosY;
	}

	public void setSheetPosY(int sheetPosY) {
		this.sheetPosY = sheetPosY;
		updateImage();
	}

	public Tile copy(int xPos, int yPos) {
		Tile copy = new Tile();
		copy.sheetPosX = this.sheetPosX;
		copy.sheetPosY = this.sheetPosY;
		copy.solid = this.solid;
		copy.solidVar = this.solidVar;
		copy.tilesheetId = this.tilesheetId;
		copy.varLocationX = this.varLocationX;
		copy.varLocationY = this.varLocationY;
		copy.visible = this.visible;
		copy.visibleVar = this.visibleVar;
		copy.xPos = xPos;
		copy.yPos = yPos;
		copy.img = img;
		return copy;
	}

	public BufferedImage getTile() {
		if (img == null) {
			System.out.println("Image null");
		}
		return img;
	}

	public void updateImage() {
		try {
			if (sheetPosX > -1 && sheetPosY > -1 && tilesheetId != null) {
				Tilesheet t = InformationHolder.getTilesheet(tilesheetId);
				BufferedImage fullImg = t.getImg();
				if (fullImg == null) {
					return;
				}
				BufferedImage tileImg = fullImg.getSubimage(sheetPosX * t.getTileWidth(), sheetPosY * t.getTileHeight(),
						t.getTileWidth(), t.getTileHeight());
				img = tileImg;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
