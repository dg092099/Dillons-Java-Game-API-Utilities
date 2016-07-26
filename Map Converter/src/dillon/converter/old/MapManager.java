package dillon.converter.old;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

/**
 * The class that manages all of the mapping package.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 *
 */
public class MapManager {
	public static final int VERSION = 1;

	/**
	 * Creates a map object from the file input stream.
	 *
	 * @param is
	 *            The input stream.
	 * @return The map
	 */
	public static Map derriveMapFromFile(InputStream is) {
		try {
			if (is == null || is.available() <= 0) {
				throw new IllegalArgumentException("Invalid input stream.");
			}
		} catch (IOException e) {
		}
		ZipInputStream zis = null;
		Scanner input = null;
		Map m = new Map();
		try {
			zis = new ZipInputStream(is);
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
					m.setBackgroundMusic(temp);
				} else if (ze.getName().equalsIgnoreCase("background.png")) {
					BufferedImage img = ImageIO.read(zis);
					m.setBackground(img);
				} else if (ze.getName().startsWith("tilesheets")) {
					String id = ze.getName().split("/")[1].split("\\Q.\\E")[0];
					BufferedImage img = ImageIO.read(zis);
					tilesheetsMaps.put(id, img);
				} else if (ze.getName().equals("data.txt")) {
					int numOfTilesheets = input.nextInt();
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
						t.setParentMap(m);
						m.addTilesheet(t);
					}

					int numTiles = input.nextInt();
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
						if (varSolid.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setSolidVar(id);
							m.setGlobalFlagVariable(id, solid, false);
						} else {
							t.setSolidVar(varSolid);
							m.setGlobalFlagVariable(varLocX, solid, false);
						}
						t.setTilesheetId(tilesheetId);
						if (varLocX.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVarLocationX(id);
							m.setGlobalPositionVariable(id, worldX, false);
						} else {
							t.setVarLocationX(varLocX);
							m.setGlobalPositionVariable(varLocX, worldX, false);
						}
						if (varLocY.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVarLocationY(id);
							m.setGlobalPositionVariable(id, worldY, false);
						} else {
							t.setVarLocationY(varLocY);
							m.setGlobalPositionVariable(varLocY, worldY, false);
						}
						t.setVisible(visible);
						if (varVisible.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVisibleVar(id);
							m.setGlobalFlagVariable(id, visible, false);
						} else {
							t.setVisibleVar(varVisible);
							m.setGlobalFlagVariable(varVisible, visible, false);
						}
						t.setxPos(worldX);
						t.setyPos(worldY);
						m.addTile(t);
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
						m.setInUseTilesheet(id);
						evt.setAffectedTile(m.getTileAtPosition(x, y));
						m.addTileEvent(evt);
					}
				}
				zis.closeEntry();
			}
			for (Tilesheet t : m.getTilesheets()) {
				if (!tilesheetsMaps.containsKey(t.getId())) {
					System.out.println("Broken tilesheet: " + t.getId());
				} else {
					t.setImg(tilesheetsMaps.get(t.getId()));
				}
			}
			for (Tile t : m.getTiles()) {
				t.setParentTilesheet(m.getTilesheetByName(t.getTilesheetId()));
				t.updateImage();
			}
			m.render();
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
		}
		return m;
	}

}
