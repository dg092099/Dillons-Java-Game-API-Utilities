package dillon.gameAPI.core;

import java.io.File;

import javax.swing.JFileChooser;

public class FileGetter {
	private static File f;

	public static void getFile(Runnable done, String reason) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(reason);
		fc.setMultiSelectionEnabled(false);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			f = fc.getSelectedFile();
			done.run();
		}
	}

	public static File getFile() {
		return f;
	}
}
