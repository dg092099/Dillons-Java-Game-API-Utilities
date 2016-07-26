package dillon.creator.textureFilesCreator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class texMain {
	static Stage stage;
	public texMain() {
		try{
			stage = new Stage();
			stage.setTitle("Texture Creator");
			Parent root = FXMLLoader.load(getClass().getResource("creator.fxml"));
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
