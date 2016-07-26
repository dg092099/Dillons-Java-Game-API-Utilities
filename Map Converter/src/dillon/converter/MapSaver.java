package dillon.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import dillon.converter.newStuff.Tile;
import dillon.converter.newStuff.TileEvent;
import dillon.converter.newStuff.Tilesheet;

public class MapSaver {
	private static final int VERSION = 2;
	private static ZipOutputStream zos;
	private static PrintWriter pw;

	public static void save(File f) {
		try {
			zos = new ZipOutputStream(new FileOutputStream(f));
			zos.setLevel(9);
			pw = new PrintWriter(zos);
			writeVersion();
			writeMusic();
			writeBackImage();
			writeTilesheets();
			writeScripts();
			writeTilesheetMeta();
			writeTilesMeta();
			writeEventsMeta();
			pw.flush();
			pw.close();
			zos.flush();
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeVersion() {
		ZipEntry ze = new ZipEntry("version.txt");
		try {
			zos.putNextEntry(ze);
			pw.println(VERSION);
			pw.flush();
			zos.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeMusic() {
		if (Main.finalMap.getBackgroundMusicFile() != null) {
			ZipEntry ze = new ZipEntry("music.au");
			try {
				zos.putNextEntry(ze);
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(Main.finalMap.getBackgroundMusicFile());
				int len = 0;
				while ((len = fis.read(buffer, 0, 1024)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.flush();
				fis.close();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void writeBackImage() {
		if (Main.finalMap.getBackgroundImage() != null) {
			ZipEntry ze = new ZipEntry("background.png");
			try {
				zos.putNextEntry(ze);
				ImageIO.write(Main.finalMap.getBackgroundImage(), "PNG", zos);
				zos.flush();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void writeTilesheets() {
		for (Tilesheet t : Main.finalMap.getTilesheets()) {
			ZipEntry ze = new ZipEntry("tilesheets/" + t.getId() + ".png");
			try {
				zos.putNextEntry(ze);
				ImageIO.write(t.getImg(), "PNG", zos);
				zos.flush();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void writeScripts() {
		ZipEntry ze = new ZipEntry("scripts/legacy.js");
		try {
			zos.putNextEntry(ze);
			pw.print(Main.convertedScript);
			pw.flush();
			zos.flush();
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void writeTilesheetMeta() {
		ZipEntry ze = new ZipEntry("tilesheets.info");
		try {
			zos.putNextEntry(ze);
			pw.println(Main.finalMap.getTilesheets().size());
			for (Tilesheet t : Main.finalMap.getTilesheets()) {
				pw.print(t.getId() + ":");
				pw.print(t.getTileWidth() + ":");
				pw.println(t.getTileHeight());
			}
			pw.flush();
			zos.flush();
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void writeTilesMeta() {
		ZipEntry ze = new ZipEntry("tiles.info");
		try {
			zos.putNextEntry(ze);
			pw.println(Main.finalMap.getTiles().size());
			for (Tile t : Main.finalMap.getTiles()) {
				pw.print(t.getTilesheetId() + ":");
				pw.print(t.getxPos() + ":" + t.getyPos() + ":");
				pw.print(t.isVisible() + ":" + t.isSolid() + ":");
				pw.println(t.getSheetPosX() + ":" + t.getSheetPosY());
			}
			pw.flush();
			zos.flush();
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void writeEventsMeta() {
		ZipEntry ze = new ZipEntry("events.info");
		try {
			zos.putNextEntry(ze);
			pw.println(Main.finalMap.getTileEvents().size());
			for (TileEvent evt : Main.finalMap.getTileEvents()) {
				pw.print(evt.getEventType().toString() + ":");
				pw.print(evt.getAffectedTile().getxPos() + ":" + evt.getAffectedTile().getyPos() + ":");
				pw.print(evt.getAffectedTile().getTilesheetId() + ":");
				pw.print(evt.getEntityType() + ":");
				pw.println(evt.getMethod());
			}
			pw.flush();
			zos.flush();
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
