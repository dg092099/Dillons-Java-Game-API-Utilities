package dillon.creator.sheetCreator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class shMainController implements Initializable{
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tilesField.setText(Integer.toString(shMain.tileCount));
		tilesXField.setText(Integer.toString((int) shMain.dimensions.getWidth()));
		tilesYField.setText(Integer.toString((int) shMain.dimensions.getHeight()));
	}
	@FXML private Button submit;
	@FXML private TextField tilesField;
	@FXML private TextField tilesXField;
	@FXML private TextField tilesYField;
	@FXML
	public void generate() {
		try{
			submit.setDisable(true);
			int tilesX = (int) shMain.dimensions.getWidth();
			int tilesY = (int) shMain.dimensions.getHeight();
			int tileWidth = shMain.tiles.get(0).getWidth();
			int tileHeight = shMain.tiles.get(0).getHeight();
			if(tilesX * tilesY < shMain.tileCount) {
				int newValue = tilesX;
				while(true) {
					if(newValue * tilesY >= shMain.tileCount) {
						break;
					}
					newValue++;
				}
				int choice = JOptionPane.showConfirmDialog(null, "The dimensions you gave are too small,"
						+ " would you like to expand the width to: " + newValue + " tiles?");
				if(choice != JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "This operation couldn't be completed because of size constraints.");
					return;
				}
				tilesX = newValue;
				tilesXField.setText(Integer.toString(tilesX));
			}
			BufferedImage finished = new BufferedImage(tilesX * tileWidth, tilesY * tileHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) finished.getGraphics();
			int counter = 0;
			for(int x = 0; x < tilesX; x++) {
				for(int y = 0; y < tilesY; y++) {
					if(counter == 0) {
						BufferedImage calibrator = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
						calibrator.setRGB(0, 0, new Color(255, 0, 0).getRGB());
						calibrator.setRGB(0, calibrator.getHeight()-1, new Color(0, 255, 0).getRGB());
						calibrator.setRGB(calibrator.getWidth()-1, 0, new Color(0, 0, 255).getRGB());
						Graphics2D imgg = (Graphics2D) calibrator.getGraphics();
						imgg.setFont(new Font("Arial", Font.PLAIN, 10));
						imgg.setColor(Color.ORANGE);
						imgg.drawString("C", calibrator.getWidth() / 2, calibrator.getHeight() / 2);
						g2.drawImage(calibrator, 0, 0, null);
						counter++;
						continue;
					}
					try{
						g2.drawImage(shMain.tiles.get(counter - 1), x * tileWidth, y * tileHeight, null);
						counter++;
					}catch(Exception e) {}
				}
			}
			FileChooser fc = new FileChooser();
			fc.setTitle("Chose where to save the compiled sprite/tile sheet.");
			fc.getExtensionFilters().add(new ExtensionFilter("Portable Network Graphics", "*.png"));
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			File f = fc.showSaveDialog(null);
			ImageIO.write(finished, "png", f);
			shMain.stage.close();
		}catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "There was a problem when doing this.");
			submit.setDisable(false);
		}
	}
}
