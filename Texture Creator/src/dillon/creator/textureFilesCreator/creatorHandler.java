package dillon.creator.textureFilesCreator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class creatorHandler implements Initializable{
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	@FXML Button submit;
	@FXML TextField tileHeightField;
	@FXML TextField tileWidthField;
	@FXML TextField tilesField;
	@FXML
	public void generate() {
		try{
			submit.setDisable(true);
			int tilesHeight = Integer.parseInt(tileHeightField.getText());
			int tilesWidth = Integer.parseInt(tileWidthField.getText());
			int tiles = Integer.parseInt(tilesField.getText());
			DirectoryChooser dc = new DirectoryChooser();
			dc.setTitle("Choose the file to send all of the files to.");
			dc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			File d = dc.showDialog(null);
			if(d == null) return;
			for(int i = 0; i < tiles; i++) {
				String fileName = (i+1) + ".png";
				BufferedImage img = new BufferedImage(tilesWidth, tilesHeight, BufferedImage.TYPE_INT_ARGB);
				for(int x = 0; x < img.getWidth(); x++) {
					for(int y = 0; y < img.getHeight(); y++) {
						img.setRGB(x, y, 16777215);
					}
				}
				ImageIO.write(img, "png", new File(d.getAbsolutePath() + "\\" + fileName));
			}
			texMain.stage.close();
		}catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "You must enter valid information.");
			submit.setDisable(false);
		}
	}
}
