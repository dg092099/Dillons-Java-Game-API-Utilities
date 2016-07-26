package dillon.creator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
		arg0.setScene(new Scene(root));
		arg0.setResizable(false);
		arg0.setTitle("Dillon's Texture Creator");
		arg0.show();
	}
}
