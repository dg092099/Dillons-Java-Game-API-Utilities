package dillon.creator;

import java.net.URL;
import java.util.ResourceBundle;

import dillon.creator.sheetCreator.shMain;
import dillon.creator.textureFilesCreator.texMain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainMenuHandler implements Initializable{
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	@FXML
	public void createIndividualFiles() {
		new texMain();
	}
	@FXML
	public void convert() {
		new shMain();
	}
}
