package dillon.creator.sheetCreator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class shMain {
	static Stage stage;
	static int tileCount = 0;
	static ArrayList<BufferedImage> tiles;
	static Dimension dimensions = new Dimension(1, 10000);
	public shMain() {
		try{
			DirectoryChooser dc = new DirectoryChooser();
			dc.setTitle("Choose the directory with all of the sprites.");
			dc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			File d = dc.showDialog(stage);
			for(int i = 0; i < d.listFiles().length; i++) {
				if(d.listFiles()[i].getName().endsWith(".png")) {
					tileCount++;
				}
			}
			tiles = new ArrayList<BufferedImage>();
			System.out.println("There are " + d.listFiles().length + " files in the directory.");
			for(int i = 0; i < d.listFiles().length; i++) {
				if(d.listFiles()[i].getName().endsWith(".png")) {
					tiles.add(ImageIO.read(d.listFiles()[i]));
				}
			}
			ArrayList<Dimension> candidates = new ArrayList<Dimension>();
			for(int x = 0; x < tileCount; x++) {
				for(int y = 0; y < tileCount; y++) {
					if(x * y >= tileCount) {
						candidates.add(new Dimension(x, y));
					}
				}
			}
			for(int i = 0; i < candidates.size(); i++) {
				Dimension Target = candidates.get(i);
				if(Math.abs(Target.getHeight() - Target.getWidth()) < Math.abs(dimensions.getHeight() - dimensions.getWidth())) {
					dimensions = Target;
				}
			}
			tileCount++;
			stage = new Stage();
			stage.setTitle("Texture sheet creator");
			stage.setResizable(false);
			stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("converter.fxml"))));
			stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
