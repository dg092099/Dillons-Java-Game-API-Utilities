package dillon.gameAPI.core.saving;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.core.InformationHolder;
import dillon.gameAPI.core.ScriptObj;
import dillon.gameAPI.core.Tile;
import dillon.gameAPI.core.TileEvent;
import dillon.gameAPI.core.Tilesheet;
import dillon.gameAPI.gui.BasicDialog;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.utils.MainUtilities;

public class MapLoader {
	private static File extracted;

	public static final int VERSION = 2;

	public static void load(File f) {
		InformationHolder.reset();
		extractFiles(f);
		if (!checkVersion()) {
			GuiSystem.startGui(new BasicDialog("The file you are using is out of date.",
					new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null), null);
			deleteDirectory(extracted);
		}
		loadMusic();
		loadBackground();
		loadTilesheetImages();
		loadScripts();
		loadTilesheetMeta();
		loadTilesMeta();
		loadEventsMeta();
		deleteDirectory(extracted);
		Core.updateScreen();
		Core.stopChoosing();
	}

	private static void extractFiles(File f) {
		try {
			extracted = Files.createTempDirectory("DGAPI-MAPEDIT-" + Long.toString(System.nanoTime())).toFile();
			ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				File extractTo = new File(extracted.getAbsolutePath() + File.separator + ze.getName());
				extractTo.getParentFile().mkdirs();
				extractTo.createNewFile();
				byte[] buffer = new byte[1024];
				int len = 0;
				FileOutputStream fos = new FileOutputStream(extractTo);
				while ((len = zis.read(buffer, 0, 1024)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zis.closeEntry();
			}
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
			GuiSystem.startGui(new BasicDialog("The files could not be extracted.", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
		}
	}

	private static void deleteDirectory(File f) {
		if (!f.isDirectory()) {
			f.delete();
		} else {
			for (File f2 : f.listFiles()) {
				deleteDirectory(f2);
			}
			f.delete();
		}
	}

	private static boolean checkVersion() {
		try {
			if (!MainUtilities.fileExistsWithinDirectory(extracted, "version.txt")) {
				GuiSystem.startGui(
						new BasicDialog("The map file you have provided is corrupt.",
								new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null),
						null);
				return false;
			}
			Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "version.txt"));
			if (s.hasNext()) {
				int ver = s.nextInt();
				s.close();
				return ver == VERSION;
			} else {
				s.close();
				return false;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			GuiSystem.startGui(new BasicDialog("The version could not be checked.", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
			return false;
		}
	}

	private static void loadMusic() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "music.au")) {
			File f;
			try {
				f = File.createTempFile("DGAPI-MAPEDIT-MUSIC-" + Long.toString(System.nanoTime()), ".au");
				f.deleteOnExit();
				byte[] buffer = new byte[1024];
				int len = 0;
				FileInputStream fis = new FileInputStream(
						new File(extracted.getAbsolutePath() + File.separator + "music.au"));
				FileOutputStream fos = new FileOutputStream(f);
				while ((len = fis.read(buffer, 0, 1024)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				fis.close();
				InformationHolder.setBackgroundMusic(f);
			} catch (IOException e) {
				e.printStackTrace();
				GuiSystem.startGui(new BasicDialog("The music could not be loaded.", new Font("Calibri", Font.BOLD, 18),
						Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static void loadBackground() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "background.png")) {
			try {
				BufferedImage img = ImageIO
						.read(new File(extracted.getAbsolutePath() + File.separator + "background.png"));
				InformationHolder.setBackground(img);
				Core.setBackgroundImage(img, null);
			} catch (IOException e) {
				e.printStackTrace();
				GuiSystem.startGui(new BasicDialog("The background image could not be loaded.",
						new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static HashMap<String, BufferedImage> tilesheets = new HashMap<String, BufferedImage>();

	private static void loadTilesheetImages() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "tilesheets")) {
			for (File f : new File(extracted.getAbsolutePath() + File.separator + "tilesheets").listFiles()) {
				try {
					String id = f.getName().split("\\Q.\\E")[0];
					BufferedImage img = ImageIO.read(f);
					tilesheets.put(id, img);
				} catch (IOException ex) {
					ex.printStackTrace();
					GuiSystem.startGui(new BasicDialog("Could not load tilesheets.", new Font("Calibri", Font.BOLD, 18),
							Color.red, Color.white, Color.black, true, null), null);
				}
			}
		}
	}

	private static void loadScripts() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "scripts")) {
			for (File f : new File(extracted.getAbsolutePath() + File.separator + "scripts").listFiles()) {
				try {
					String id = f.getName().split("\\Q.\\E")[0];
					String content = "";
					Scanner s = new Scanner(f);
					while (s.hasNextLine()) {
						content += s.nextLine();
						content += "\n";
					}
					s.close();
					InformationHolder.addScript(new ScriptObj(id, content));
				} catch (IOException ex) {
					ex.printStackTrace();
					GuiSystem.startGui(new BasicDialog("Could not load scripts.", new Font("Calibri", Font.BOLD, 18),
							Color.red, Color.white, Color.black, true, null), null);
				}
			}
		}
	}

	private static void loadTilesheetMeta() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "tilesheets.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "tilesheets.info"));
				int amt = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amt; i++) {
					String desc = s.nextLine();
					String id = desc.split(":")[0];
					int width = Integer.parseInt(desc.split(":")[1]);
					int height = Integer.parseInt(desc.split(":")[2]);
					Tilesheet t = new Tilesheet();
					t.setId(id);
					t.setTileWidth(width);
					t.setTileHeight(height);
					if (!tilesheets.containsKey(id)) {
						GuiSystem.startGui(new BasicDialog("Missing tilesheet: " + id,
								new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null),
								null);
					} else {
						t.setImg(tilesheets.get(id));
						InformationHolder.addTilesheet(t);
					}
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				GuiSystem.startGui(new BasicDialog("The tilesheets cannot be loaded.",
						new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static void loadTilesMeta() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "tiles.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "tiles.info"));
				int amtTiles = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amtTiles; i++) {
					String desc = s.nextLine();
					String[] parts = desc.split(":");
					String tilesheetID = parts[0];
					int xPos = Integer.parseInt(parts[1]);
					int yPos = Integer.parseInt(parts[2]);
					boolean visible = Boolean.parseBoolean(parts[3]);
					boolean solid = Boolean.parseBoolean(parts[4]);
					int sheetX = Integer.parseInt(parts[5]);
					int sheetY = Integer.parseInt(parts[6]);
					Tile t = new Tile();
					t.setTilesheetId(tilesheetID);
					t.setxPos(xPos);
					t.setyPos(yPos);
					t.setVisible(visible);
					t.setSolid(solid);
					t.setSheetPosX(sheetX);
					t.setSheetPosY(sheetY);
					t.updateImage();
					InformationHolder.addTile(t);
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				GuiSystem.startGui(new BasicDialog("Unable to load tiles.", new Font("Calibri", Font.BOLD, 18),
						Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static void loadEventsMeta() {
		if (MainUtilities.fileExistsWithinDirectory(extracted, "events.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "events.info"));
				int amt = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amt; i++) {
					String desc = s.nextLine();
					String[] parts = desc.split(":");
					TileEvent evt = new TileEvent();
					evt.setEventType(TileEvent.TileEventType.valueOf(parts[0]));
					int x = Integer.parseInt(parts[1]);
					int y = Integer.parseInt(parts[2]);
					String tilesheet = parts[3];
					InformationHolder.setInUseTilesheet(tilesheet);
					Tile t = InformationHolder.getTileAtPosition(x, y);
					evt.setAffectedTile(t);
					String entityType = parts[4];
					evt.setEntityType(entityType);
					String method = parts[5];
					evt.setMethod(method);
					InformationHolder.addTileEvent(evt);
				}
				s.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				GuiSystem.startGui(new BasicDialog("Unable to load tile events.", new Font("Calibri", Font.BOLD, 18),
						Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}
}
