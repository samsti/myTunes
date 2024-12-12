package dk.easv.mytunes.dal;

import com.mpatric.mp3agic.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ChooseFile {
    File chosenFile;
    public ChooseFile(Window window) {
        FileChooser fileChooser = new FileChooser();
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

    public String[] getDuration() {
        try {
            if (chosenFile == null) {
                return null;
            }

            Mp3File mp3file = new Mp3File(getSelectedFilePath());
            int durationInSeconds = (int) mp3file.getLengthInSeconds();
            String title = "";
            String artist = "";
            String genre = "";
            if (mp3file.hasId3v1Tag() && mp3file.getId3v1Tag() != null) {
                title = mp3file.getId3v1Tag().getTitle() != null ? mp3file.getId3v1Tag().getTitle() : "";
                artist = mp3file.getId3v1Tag().getArtist() != null ? mp3file.getId3v1Tag().getArtist() : "";
                genre = mp3file.getId3v1Tag().getGenreDescription() != null ? mp3file.getId3v1Tag().getGenreDescription() : "";
            }
            else if (mp3file.hasId3v2Tag() && mp3file.getId3v2Tag() != null) {
                title = mp3file.getId3v2Tag().getTitle() != null ? mp3file.getId3v2Tag().getTitle() : "";
                artist = mp3file.getId3v2Tag().getArtist() !=null ? mp3file.getId3v2Tag().getArtist() : "";
                genre = mp3file.getId3v2Tag().getGenreDescription() != null ? mp3file.getId3v2Tag().getGenreDescription() : "";

            }
            int minutes = durationInSeconds / 60;
            int seconds = durationInSeconds % 60;
            //return String.format("%d:%02d", minutes, seconds);
            return new String[]{title, artist, String.format("%d:%02d", minutes, seconds), genre};

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSelectedFilePath() {
        if (chosenFile != null) {
            return chosenFile.getAbsolutePath();
        }
        else
            throw new RuntimeException("File is null, can not return filePath");
    }
}
