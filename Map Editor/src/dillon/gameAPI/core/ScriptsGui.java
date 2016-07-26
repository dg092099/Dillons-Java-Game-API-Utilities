package dillon.gameAPI.core;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import dillon.gameAPI.gui.BasicDialog;
import dillon.gameAPI.gui.GuiComponent;
import dillon.gameAPI.gui.GuiSystem;

public class ScriptsGui implements GuiComponent {
	private BufferedImage img;
	private ArrayList<String> scriptNames = new ArrayList<String>();
	private int selected = -1;

	public ScriptsGui() {
		try {
			img = ImageIO
					.read(ScriptsGui.class.getClassLoader().getResourceAsStream("dillon/gameAPI/res/scriptsMenu.png"));
			updateList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateList() {
		scriptNames.clear();
		for (ScriptObj s : InformationHolder.getScripts()) {
			scriptNames.add(s.getId());
		}
		selected = -1;
	}

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
		g.drawImage(img, Core.getWidth() / 2 - img.getWidth() / 2, Core.getHeight() / 2 - img.getHeight() / 2, null);
		g.setColor(Color.black);
		g.setFont(new Font("Calibri", Font.BOLD, 18));
		for (int i = 0; i < scriptNames.size(); i++) {
			g.setColor(i == selected ? Color.red : Color.black);
			String name = scriptNames.get(i);
			g.drawString(name, 260, i * 25 + 100 + g.getFontMetrics().getHeight());
		}
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	@Override
	public void onMouseClickLeft(double x, double y) {
		if (x >= 258 && y >= 514 && x <= 319 && y <= 534) { // New
			File f;
			try {
				f = File.createTempFile(Long.toString(new SecureRandom().nextLong()), ".txt");
				PrintWriter pw = new PrintWriter(f);
				pw.println("//Name:");
				pw.close();
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().edit(f);
					JOptionPane.showMessageDialog(null, "Click OK when finished.");
					Scanner s = new Scanner(f);
					String script = "";
					boolean firstLine = true;
					String name = "";
					while (s.hasNextLine()) {
						if (firstLine) {
							String nameLine = s.nextLine();
							if (!nameLine.matches("\\/\\/Name:\\s?(\\w+)")) {
								JOptionPane.showMessageDialog(null, "You failed to enter a name.");
								s.close();
								if (!f.delete()) {
									f.deleteOnExit();
								}
								return;
							}
							name = nameLine.split(":\\s?")[1];
							firstLine = false;
						} else {
							script += s.nextLine();
							script += "\n";
						}
					}
					s.close();
					if (!f.delete()) {
						f.deleteOnExit();
					}
					InformationHolder.addScript(new ScriptObj(name, script));
					GuiSystem.startGui(new BasicDialog("The script has been added successfully.",
							new Font("Calibri", Font.BOLD, 18), Color.green, Color.white, Color.black, true, null),
							null);
					updateList();
				} else if (!f.delete()) {
					f.deleteOnExit();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (x >= 256 && y >= 100 && x <= 553 && y <= 502) { // Viewport
			int diffY = (int) (y - 100);
			double nearestMultiple = 25 * Math.round(diffY / 25);
			selected = (int) nearestMultiple / 25;
			System.out.println(selected);
		} else if (x >= 330 && y >= 512 && x <= 394 && y <= 535) { // Edit
			if (selected >= 0 && selected < InformationHolder.getScripts().size()) {
				ScriptObj obj = InformationHolder.getScripts().get(selected);
				try {
					File f = File.createTempFile(Long.toString(new SecureRandom().nextLong()), ".txt");
					PrintWriter pw = new PrintWriter(f);
					pw.println("//Name: " + obj.getId());
					for (String s : obj.getScript().split("\n")) {
						pw.println(s);
					}
					pw.close();
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().edit(f);
						JOptionPane.showMessageDialog(null, "Press OK when finished editing.");
						String script = "";
						Scanner s = new Scanner(f);
						boolean findingTitle = true;
						String title = "";
						while (s.hasNextLine()) {
							if (findingTitle) {
								String nameLine = s.nextLine();
								if (!nameLine.matches("\\/\\/Name:\\s?(\\w+)")) {
									JOptionPane.showMessageDialog(null, "The header is missing.");
									if (!f.delete()) {
										f.deleteOnExit();
									}
									return;
								}
								title = nameLine.split(":\\s?")[1];
								findingTitle = false;
							} else {
								script += s.nextLine();
								script += "\n";
							}
						}
						s.close();
						obj.setId(title);
						obj.setScript(script);
						JOptionPane.showMessageDialog(null, "The script has been edited successfully.");
					}
					if (!f.delete()) {
						f.deleteOnExit();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateList();
			}
		} else if (x >= 405 && y >= 515 && x <= 470 && y <= 535) { // Delete
			if (selected >= 0 && selected < InformationHolder.getScripts().size()) {
				InformationHolder.removeScript(InformationHolder.getScripts().get(selected));
				JOptionPane.showMessageDialog(null, "The script has been deleted.");
				updateList();
			}
		} else if (x >= 487 && y >= 514 && x <= 548 && y <= 539) { // Close
			GuiSystem.removeGui(this, null);
			Core.stopChoosing();
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
		return new int[] { Core.getWidth() / 2 - img.getWidth() / 2, Core.getHeight() / 2 - img.getHeight() / 2 };
	}

	@Override
	public int[] getSize() {
		return new int[] { img.getWidth(), img.getHeight() };
	}

	@Override
	public void slide(int x, int y) {
	}

}
