package dillon.converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import dillon.converter.newStuff.Tile;
import dillon.converter.newStuff.Tilesheet;
import dillon.converter.old.Map;
import dillon.converter.old.MapManager;
import dillon.converter.old.TileEvent;

public class Main {
	private static File dir;
	private static File sel;
	public static dillon.converter.newStuff.Map finalMap;

	public static void main(String[] args) {
		if (args.length > 0) {
			File temp = new File(args[0]);
			if (!temp.exists()) {
				System.out.println("The file must exist.");
			}
			if (temp.isDirectory()) {
				dir = temp;
			} else {
				sel = temp;
			}
		} else {
			String resp = JOptionPane.showInputDialog(
					"Enter a choice, directory converts all maps in a directory.\n1.Directory\n2.Single map");
			if (resp.matches("\\d+")) {
				int choice = Integer.parseInt(resp);
				switch (choice) {
				case 1:
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Choose the directory that contains the maps.");
					fc.setMultiSelectionEnabled(false);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						if (f.exists()) {
							dir = f;
						} else {
							JOptionPane.showMessageDialog(null, "The file must exist.");
							System.exit(-1);
						}
					} else {
						System.exit(0);
					}
					break;
				case 2:
					JFileChooser fc1 = new JFileChooser();
					fc1.setDialogTitle("Choose the file to convert.");
					fc1.setMultiSelectionEnabled(false);
					fc1.setAcceptAllFileFilterUsed(false);
					fc1.setFileFilter(new FileNameExtensionFilter("Map files", "map"));
					if (fc1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File f = fc1.getSelectedFile();
						if (f.exists()) {
							sel = f;
						} else {
							JOptionPane.showMessageDialog(null, "The file doesn't exist.");
							System.exit(-1);
						}
					} else {
						System.exit(0);
					}
					break;
				default:
					JOptionPane.showMessageDialog(null, "Invalid choice.");
					main(null);
					System.exit(0);
				}
			}
		}
		begin();
	}

	private static void begin() {
		if (dir != null) {
			int num = dir.listFiles().length;
			int counter = 1;
			for (File f : dir.listFiles()) {
				System.out.println("Converting file " + counter + "/" + num);
				convert(f);
				counter++;
			}
		} else if (sel != null) {
			convert(sel);
		}
		JOptionPane.showMessageDialog(null, "The files have been converted successfully.");
		System.exit(0);
	}

	private static void convert(File f) {
		try {
			if (f.getName().endsWith(".map")) {
				System.out.println("\n\n---------------\nMap: " + f.getName() + "\n---------------");
				Map m = MapManager.derriveMapFromFile(new BufferedInputStream(new FileInputStream(f)));
				System.out.println("Loaded legacy map.");
				dillon.converter.newStuff.Map converted = new dillon.converter.newStuff.Map();
				finalMap = converted;
				int numTilesheets = 0;
				for (dillon.converter.old.Tilesheet ts : m.getTilesheets()) {
					Tilesheet t = new Tilesheet();
					t.setId(ts.getId());
					t.setImg(ts.getImg());
					t.setParentMap(converted);
					t.setTileHeight(ts.getTileHeight());
					t.setTileWidth(ts.getTileWidth());
					converted.addTilesheet(t);
					numTilesheets++;
				}
				System.out.println("Converted " + numTilesheets + " legacy tilesheets.");
				int numTiles = 0;
				for (dillon.converter.old.Tile t : m.getTiles()) {
					Tile ct = new Tile();
					ct.setParentTilesheet(converted.getTilesheetByName(t.getTilesheetId()));
					ct.setTilesheetId(t.getTilesheetId());
					ct.setSheetPosX(t.getSheetPosX());
					ct.setSheetPosY(t.getSheetPosY());
					ct.setSolid(t.isSolid());
					ct.setSolidVar(t.getSolidVar());
					ct.setVarLocationX(t.getVarLocationX());
					ct.setVarLocationY(t.getVarLocationY());
					ct.setVisible(t.isVisible());
					ct.setVisibleVar(t.getVisibleVar());
					ct.setxPos(t.getxPos());
					ct.setyPos(t.getyPos());
					converted.addTile(ct);
					numTiles++;
				}
				System.out.println("Converted " + numTiles + " tiles.");
				createScripts(m, converted);
				if (m.getBackgroundImage() != null) {
					converted.setBackground(m.getBackgroundImage());
				}
				if (m.getBackgroundMusicFile() != null) {
					converted.setBackgroundMusic(m.getBackgroundMusicFile());
				}
				System.out.println("Optimizing...");
				optimize(converted);
				System.out.println("Saving...");
				saveMap(f);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unable to load map: " + f.getName());
		}
	}

	private static void optimize(dillon.converter.newStuff.Map converted) {
		ArrayList<dillon.converter.newStuff.Tile> copies = new ArrayList<>();
		int numCopies = 0;
		for (int i = 0; i < converted.getTiles().size(); i++) {
			for (int i2 = 0; i2 < converted.getTiles().size(); i2++) {
				if (i < 0 || i > converted.getTiles().size() || i2 < 0 || i2 > converted.getTiles().size()) {
					continue;
				}
				Tile t1 = converted.getTiles().get(i);
				Tile t2 = converted.getTiles().get(i2);
				if (i == i2) {
					continue;
				}
				if (t1.getTilesheetId().equals(t2.getTilesheetId())) { // Same
																		// tilesheet
					if (t1.getxPos() == t2.getxPos() && t1.getyPos() == t2.getyPos()) { // Same
																						// location
						if (t1.getSheetPosX() == t2.getSheetPosX() && t1.getSheetPosY() == t2.getSheetPosY()) { // Same
																												// tile
							converted.removeTile(t2);
							numCopies++;
						}
					}
				}
			}
		}
		System.out.println("Removed " + numCopies + " duplicate tiles.");
	}

	public static String convertedScript = "";

	private static void saveMap(File origin) {
		File output = new File(
				origin.getParentFile().getAbsolutePath() + File.separator + "CONVERTED-" + origin.getName());
		while (output.exists()) {
			output = new File(output.getParentFile().getAbsolutePath() + File.separator + new Random().nextInt()
					+ origin.getName());
		}
		System.out.println("Saving as: " + output.getName());
		MapSaver.save(output);
	}

	private static void createScripts(Map legacy, dillon.converter.newStuff.Map converting) {
		final char[] names = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		String script = "/* \n * Converted Script\n * Please use handling in place of this mechanism.\n */\n";
		SecureRandom sr = new SecureRandom();
		int numEvents = 0;
		for (TileEvent evt : legacy.getTileEvents()) {
			String name = "";
			for (int i = 0; i < 10; i++) {
				name += names[sr.nextInt(names.length)];
			}
			script += "function " + name + "(){\n";
			script += "\tMap.executeLegacyEvent(" + evt.getAffectedTile().getxPos() + ", "
					+ evt.getAffectedTile().getyPos();
			script += ", \"" + evt.getAffectedTile().getTilesheetId() + "\"";
			if (evt.getEventType().equals(TileEvent.TileEventType.TOUCH)) {
				script += ", 1";
			} else {
				script += ", 2";
			}
			script += ");\n";
			script += "}\n";
			numEvents++;
			dillon.converter.newStuff.TileEvent n = new dillon.converter.newStuff.TileEvent();
			dillon.converter.old.Tile t = evt.getAffectedTile();
			dillon.converter.newStuff.Tile t2 = new Tile();
			t2.setParentTilesheet(converting.getTilesheetByName(t.getTilesheetId()));
			t2.setSheetPosX(t.getSheetPosX());
			t2.setSheetPosY(t.getSheetPosY());
			t2.setSolid(t.isSolid());
			t2.setSolidVar(t.getSolidVar());
			t2.setTilesheetId(t.getTilesheetId());
			t2.setVarLocationX(t.getVarLocationX());
			t2.setVarLocationY(t.getVarLocationY());
			t2.setVisible(t.isVisible());
			t2.setVisibleVar(t.getVisibleVar());
			t2.setxPos(t.getxPos());
			t2.setyPos(t.getyPos());

			n.setAffectedTile(t2);
			n.setEntityType(evt.getEntityType());
			dillon.converter.newStuff.TileEvent.TileEventType ty = dillon.converter.newStuff.TileEvent.TileEventType
					.values()[evt.getEventType().ordinal()];
			n.setEventType(ty);
			n.setMethod(name);
			converting.addTileEvent(n);
		}
		convertedScript = script;
		System.out.println("Converted " + numEvents + " events.");
	}
}
