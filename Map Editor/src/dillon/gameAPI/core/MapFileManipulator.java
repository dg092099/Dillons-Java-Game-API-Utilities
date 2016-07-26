package dillon.gameAPI.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class MapFileManipulator {
	private static final int VERSION = 2;

	public static void save(File F) {
		PrintWriter pw = null;
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(F));
			zos.setLevel(9);
			pw = new PrintWriter(zos);
			ZipEntry ze = new ZipEntry("version.txt");
			zos.putNextEntry(ze);
			pw.println(VERSION);
			pw.flush();
			zos.closeEntry();
			if (InformationHolder.getBackgroundMusic() != null) {
				ze = new ZipEntry("music.au");
				zos.putNextEntry(ze);
				File f = InformationHolder.getBackgroundMusic();
				byte[] buffer = new byte[1024];
				int len = 0;
				FileInputStream fis = new FileInputStream(f);
				while ((len = fis.read(buffer, 0, 1024)) > 0) {
					zos.write(buffer, 0, len);
				}
				fis.close();
				zos.flush();
				zos.closeEntry();
			}
			if (InformationHolder.getBackground() != null) {
				ze = new ZipEntry("background.png");
				zos.putNextEntry(ze);
				ImageIO.write(InformationHolder.getBackground(), "png", zos);
				zos.flush();
				zos.closeEntry();
			}
			for (Tilesheet t : InformationHolder.getTilesheets()) {
				ze = new ZipEntry("tilesheets/" + t.getId() + ".png");
				zos.putNextEntry(ze);
				ImageIO.write(t.getImg(), "png", zos);
				zos.flush();
				zos.closeEntry();
			}
			ze = new ZipEntry("data.txt");
			zos.putNextEntry(ze);

			// Tilesheets
			ArrayList<Tilesheet> tilesheets = InformationHolder.getTilesheets();
			pw.println(tilesheets.size());
			for (Tilesheet t : tilesheets) {
				pw.println(t.getTileWidth());
				pw.println(t.getTileHeight());
				pw.println(t.getId());
			}
			pw.flush();

			// Tiles
			ArrayList<Tile> tiles = InformationHolder.getTiles();
			pw.println(tiles.size());
			for (Tile t : tiles) {
				pw.println(t.getTilesheetId());
				pw.println(t.getxPos());
				pw.println(t.getyPos());
				pw.println(t.isVisible());
				pw.println(t.isSolid());
				pw.println(t.getVarLocationX());
				pw.println(t.getVarLocationY());
				pw.println(t.getVisibleVar());
				pw.println(t.getSolidVar());
				pw.println(t.getSheetPosX());
				pw.println(t.getSheetPosY());
			}
			pw.flush();

			// Events
			ArrayList<TileEvent> tileEvents = InformationHolder.getTileEvents();
			pw.println(tileEvents.size());
			for (TileEvent evt : tileEvents) {
				pw.println(evt.getEventType().toString());
				pw.println(evt.getAffectedTile().getxPos());
				pw.println(evt.getAffectedTile().getyPos());
				pw.println(evt.getAffectedTile().getTilesheetId());
				pw.println(evt.getEntityType());
			}
			pw.flush();
			zos.closeEntry();
			Core.stopChoosing();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
				if (zos != null) {
					zos.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// Need to call in.nextLine() after using in.nextInt()
	public static void load(File F) {
		InformationHolder.reset();
		ZipInputStream zis = null;
		Scanner input = null;
		try {
			zis = new ZipInputStream(new FileInputStream(F));
			input = new Scanner(zis);
			ZipEntry ze = null;
			HashMap<String, BufferedImage> tilesheetsMaps = new HashMap<String, BufferedImage>();
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().equalsIgnoreCase("version.txt")) {
					int version = input.nextInt();
					if (version != VERSION) {
						input.close();
						throw new Exception("Invalid file.");
					}
				} else if (ze.getName().equalsIgnoreCase("music.au")) {
					File temp = File.createTempFile(Long.toString(new SecureRandom().nextLong()), ".au");
					temp.deleteOnExit();
					byte[] buffer = new byte[1024];
					int len = 0;
					FileOutputStream fos = new FileOutputStream(temp);
					while ((len = zis.read(buffer, 0, 1024)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					InformationHolder.setBackgroundMusic(temp);
				} else if (ze.getName().equalsIgnoreCase("background.png")) {
					BufferedImage img = ImageIO.read(zis);
					InformationHolder.setBackground(img);
					Core.setBackgroundImage(img, null);
				} else if (ze.getName().startsWith("tilesheets")) {
					String id = ze.getName().split("/")[1].split("\\Q.\\E")[0];
					BufferedImage img = ImageIO.read(zis);
					tilesheetsMaps.put(id, img);
				} else if (ze.getName().equals("data.txt")) {
					int numOfTilesheets = input.nextInt();
					System.out.println("Number of tilesheets " + numOfTilesheets);
					input.nextLine();
					for (int i = 0; i < numOfTilesheets; i++) {
						int width = input.nextInt();
						int height = input.nextInt();
						input.nextLine();
						String id = input.nextLine();
						Tilesheet t = new Tilesheet();
						t.setId(id);
						t.setTileHeight(height);
						t.setTileWidth(width);
						InformationHolder.addTilesheet(t);
					}

					int numTiles = input.nextInt();
					System.out.println("Number of Tiles: " + numTiles);
					input.nextLine();
					for (int i = 0; i < numTiles; i++) {
						String tilesheetId = input.nextLine();
						int worldX = input.nextInt(), worldY = input.nextInt();
						boolean visible = input.nextBoolean(), solid = input.nextBoolean();
						input.nextLine();
						String varLocX = input.nextLine();
						String varLocY = input.nextLine();
						String varVisible = input.nextLine();
						String varSolid = input.nextLine();
						int sheetX = input.nextInt(), sheetY = input.nextInt();
						input.nextLine();
						Tile t = new Tile();
						t.setSheetPosX(sheetX);
						t.setSheetPosY(sheetY);
						t.setSolid(solid);
						t.setSolidVar(varSolid);
						t.setTilesheetId(tilesheetId);
						t.setVarLocationX(varLocX);
						t.setVarLocationY(varLocY);
						t.setVisible(visible);
						t.setVisibleVar(varVisible);
						t.setxPos(worldX);
						t.setyPos(worldY);
						InformationHolder.addTile(t);
					}
					// Tile events
					int numEvents = input.nextInt();
					input.nextLine();
					for (int i = 0; i < numEvents; i++) {
						TileEvent.TileEventType type = TileEvent.TileEventType.valueOf(input.nextLine());
						int x = input.nextInt();
						int y = input.nextInt();
						input.nextLine();
						String id = input.nextLine();
						String entType = input.nextLine();
						TileEvent evt = new TileEvent();
						evt.setEntityType(entType);
						evt.setEventType(type);
						InformationHolder.setInUseTilesheet(id);
						evt.setAffectedTile(InformationHolder.getTileAtPosition(x, y));
						InformationHolder.addTileEvent(evt);
					}
				}
				zis.closeEntry();
			}
			for (Tilesheet t : InformationHolder.getTilesheets()) {
				if (!tilesheetsMaps.containsKey(t.getId())) {
					System.out.println("Broken tilesheet: " + t.getId());
				} else {
					t.setImg(tilesheetsMaps.get(t.getId()));
				}
			}
			for (Tile t : InformationHolder.getTiles()) {
				t.updateImage();
			}
			Core.updateScreen();
			Core.stopChoosing();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Core.updateScreen();
			Core.stopChoosing();
		}
	}
}