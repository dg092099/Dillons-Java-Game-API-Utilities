package dillon.gameAPI.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class InformationHolder {
	private static BufferedImage background;
	private static File backgroundMusic;
	private static ArrayList<Tilesheet> tilesheets = new ArrayList<Tilesheet>();
	private static Tilesheet inUse;
	private static ArrayList<Tile> tiles = new ArrayList<Tile>();
	private static Tile inUseTile;
	private static ArrayList<TileEvent> events = new ArrayList<TileEvent>();
	private static ArrayList<ScriptObj> scripts = new ArrayList<ScriptObj>();

	public static void setBackground(BufferedImage img) {
		background = img;
	}

	public static BufferedImage getBackground() {
		return background;
	}

	public static void setBackgroundMusic(File f) {
		backgroundMusic = f;
	}

	public static File getBackgroundMusic() {
		return backgroundMusic;
	}

	public static void addTilesheet(Tilesheet t) {
		tilesheets.add(t);
	}

	public static void removeTilesheet(String id) {
		if (existsTilesheet(id)) {
			Tilesheet t = null;
			for (Tilesheet tile : tilesheets) {
				if (tile.getId().equals(id)) {
					t = tile;
					break;
				}
			}
			if (t != null) {
				tilesheets.remove(t);
			}
		}
	}

	public static boolean existsTilesheet(String id) {
		for (Tilesheet tile : tilesheets) {
			if (tile.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public static void setInUseTilesheet(String id) {
		for (Tilesheet tile : tilesheets) {
			if (tile.getId().equals(id)) {
				inUse = tile;
				return;
			}
		}
	}

	public static boolean tilesheetsEmpty() {
		return tilesheets.isEmpty();
	}

	public static ArrayList<Tilesheet> getTilesheets() {
		return tilesheets;
	}

	public static Tilesheet getTilesheet(String tilesheetId) {
		for (Tilesheet tile : tilesheets) {
			if (tile.getId().equals(tilesheetId)) {
				return tile;
			}
		}
		return null;
	}

	public static Tile getInUseTile() {
		return inUseTile;
	}

	public static void setInUseTile(Tile inUseTile) {
		InformationHolder.inUseTile = inUseTile;
	}

	public static void addTile(Tile t) {
		tiles.add(t);
	}

	public static void removeTile(int x, int y) {
		Tile t = null;
		for (Tile t2 : tiles) {
			if (t2.getxPos() == x && t2.getyPos() == y) {
				t = t2;
				break;
			}
		}
		tiles.remove(t);
	}

	public static ArrayList<Tile> getTiles() {
		return tiles;
	}

	public static ArrayList<TileEvent> getTileEvents() {
		return events;
	}

	public static void addTileEvent(TileEvent evt) {
		events.add(evt);
	}

	public static int getTileEventsLength() {
		return events.size();
	}

	public static void deleteTileEvent(int index) {
		events.remove(index);
	}

	public static void reset() {
		background = null;
		backgroundMusic = null;
		tilesheets.clear();
		inUse = null;
		tiles.clear();
		inUseTile = null;
		events.clear();
		scripts.clear();
	}

	public static Tile getTileAtPosition(int x, int y) {
		for (Tile t : tiles) {
			if (t.getxPos() == x && t.getyPos() == y && inUse.getId().equals(t.getTilesheetId())) {
				return t;
			}
		}
		return null;
	}

	public static void addScript(ScriptObj o) {
		scripts.add(o);
	}

	public static void removeScript(ScriptObj o) {
		scripts.remove(o);
	}

	public static ArrayList<ScriptObj> getScripts() {
		return scripts;
	}
}
