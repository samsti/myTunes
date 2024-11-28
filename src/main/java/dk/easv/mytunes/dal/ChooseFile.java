package dk.easv.mytunes.dal;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ChooseFile {
    File chosenFile;
    public ChooseFile(Window window) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        //Sets the window title
        fileChooser.setTitle("Open Music File");

        //Sets what kind of files the user can open
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Music mp3", "*.mp3");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("Music wav", "*.wav");
        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("Music aifff","*.aifff");
        //Sets the filter for the browser
        fileChooser.getExtensionFilters().addAll(extFilter, extFilter2, extFilter3);
        //Open dialogs, when finishes the File will be stored in chosenFile
        chosenFile = fileChooser.showOpenDialog(window);
    }

    public String getSelectedFilePath() {
        if (chosenFile != null) {
            return chosenFile.getAbsolutePath();
        }
        else
            throw new RuntimeException("File is null, can not return filePath");
    }
}
