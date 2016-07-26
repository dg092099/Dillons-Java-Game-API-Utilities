package dillon.gameAPI.core.saving;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.InformationHolder;
import dillon.gameAPI.core.ScriptObj;
import dillon.gameAPI.core.Tile;
import dillon.gameAPI.core.TileEvent;
import dillon.gameAPI.core.Tilesheet;
import dillon.gameAPI.gui.BasicDialog;
import dillon.gameAPI.gui.GuiSystem;

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
			GuiSystem.startGui(new BasicDialog("Unable to create file.", new Font("Calibri", Font.BOLD, 18), Color.red,
					Color.white, Color.black, true, null), null);
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
			GuiSystem.startGui(new BasicDialog("Unable to push version number.", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
		}
	}

	private static void writeMusic() {
		if (InformationHolder.getBackgroundMusic() != null) {
			ZipEntry ze = new ZipEntry("music.au");
			try {
				zos.putNextEntry(ze);
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(InformationHolder.getBackgroundMusic());
				int len = 0;
				while ((len = fis.read(buffer, 0, 1024)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.flush();
				fis.close();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
				GuiSystem.startGui(new BasicDialog("Unable to write music to map file.",
						new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static void writeBackImage() {
		if (InformationHolder.getBackground() != null) {
			ZipEntry ze = new ZipEntry("background.png");
			try {
				zos.putNextEntry(ze);
				ImageIO.write(InformationHolder.getBackground(), "PNG", zos);
				zos.flush();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
				GuiSystem.startGui(new BasicDialog("Unable to write background image to map.",
						new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null), null);
			}
		}
	}

	private static void writeTilesheets() {
		for (Tilesheet t : InformationHolder.getTilesheets()) {
			ZipEntry ze = new ZipEntry("tilesheets/" + t.getId() + ".png");
			try {
				zos.putNextEntry(ze);
				ImageIO.write(t.getImg(), "PNG", zos);
				zos.flush();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
				GuiSystem.startGui(
						new BasicDialog("Unable to write tilesheet: " + t.getId() + " to file.",
								new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null),
						null);
			}
		}
	}

	private static void writeScripts() {
		for (ScriptObj obj : InformationHolder.getScripts()) {
			ZipEntry ze = new ZipEntry("scripts/" + obj.getId() + ".js");
			try {
				zos.putNextEntry(ze);
				pw.print(obj.getScript());
				pw.flush();
				zos.flush();
				zos.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
				GuiSystem.startGui(
						new BasicDialog("Unable to write script: " + obj.getId() + " to file.",
								new Font("Calibri", Font.BOLD, 18), Color.red, Color.white, Color.black, true, null),
						null);
			}
		}
	}

	private static void writeTilesheetMeta() {
		ZipEntry ze = new ZipEntry("tilesheets.info");
		try {
			zos.putNextEntry(ze);
			pw.println(InformationHolder.getTilesheets().size());
			for (Tilesheet t : InformationHolder.getTilesheets()) {
				pw.print(t.getId() + ":");
				pw.print(t.getTileWidth() + ":");
				pw.println(t.getTileHeight());
			}
			pw.flush();
			zos.flush();
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
			GuiSystem.startGui(new BasicDialog("Unable to write tilesheets.info", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
		}
	}

	private static void writeTilesMeta() {
		ZipEntry ze = new ZipEntry("tiles.info");
		try {
			zos.putNextEntry(ze);
			pw.println(InformationHolder.getTiles().size());
			for (Tile t : InformationHolder.getTiles()) {
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
			GuiSystem.startGui(new BasicDialog("Unable to write tiles.info", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
		}
	}

	private static void writeEventsMeta() {
		ZipEntry ze = new ZipEntry("events.info");
		try {
			zos.putNextEntry(ze);
			pw.println(InformationHolder.getTileEvents().size());
			for (TileEvent evt : InformationHolder.getTileEvents()) {
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
			GuiSystem.startGui(new BasicDialog("Unable to write events.info", new Font("Calibri", Font.BOLD, 18),
					Color.red, Color.white, Color.black, true, null), null);
		}
	}
}
