package dillon.gameAPI.core;

import java.awt.image.BufferedImage;

public class Tilesheet {
	private String id;
	private int tileWidth;
	private int tileHeight;
	private BufferedImage img;

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage b) {
		img = b;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getTilesLeftToRight() {
		return img.getWidth() / tileWidth;
	}

	public int getTilesTopToBottom() {
		return img.getHeight() / tileHeight;
	}
}
