package dk.easv.mytunes.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    //@FXML
    //private AnchorPane anchorPopUp;
    @FXML
    private StackPane rootPane;
    @FXML
    private Button btnNewPlayList;
    @FXML
    private Button btnEditPlayList;
    @FXML
    private Button btnDeletePlayList;
    @FXML
    private Button btnAddSongToPlayList;
    @FXML
    private Button btnMoveSongUp;
    @FXML
    private Button btnMoveSongDown;
    @FXML
    private Button btnDeleteFromPlaylist;
    @FXML
    private Button btnNewSong;
    @FXML
    private Button btnEditSong;
    @FXML
    private Button btnDeleteSong;
    @FXML
    private Button btnClose;
    @FXML
    private TextField txtNewPlaylist;
    @FXML
    private VBox popupVBox;
    @FXML
    private VBox popupBg;
    @FXML
    private VBox popupNewSong;
    @FXML
    private TextField txtSongTitle;
    @FXML
    private TextField txtSongArtist;
    @FXML
    private ChoiceBox<String> choiceCategory;
    @FXML
    private Button btnChooseCategory;
    @FXML
    private Button btnAddCategory;
    @FXML
    private TextField txtTime;
    @FXML
    private TextField txtFilePath;
    @FXML
    private Button btnChoose;
    @FXML
    private Button btnSaveSong;
    @FXML
    private Button btnCancelSong;
    @FXML
    private TextField txtNewCategory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupBg.setPrefWidth(rootPane.getWidth());
        popupBg.setPrefHeight(rootPane.getHeight());
    }


    @FXML
    private void newPlaylistClicked(ActionEvent event) {
        popupBg.setVisible(true);
        popupVBox.setVisible(true);
        popupVBox.toFront();
    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        txtNewPlaylist.setText("");
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
    }
    @FXML
    private void saveButtonClicked(ActionEvent event) {
        txtNewPlaylist.setText("");
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
        //TODO: save to DB
    }
    @FXML
    private void btnNewSongClicked(ActionEvent event) {
        popupBg.setVisible(true);
        popupNewSong.setVisible(true);
        popupNewSong.toFront();
    }
    @FXML
    private void btnChooseCategoryClicked(ActionEvent event) {
        btnChoose.setVisible(false);
        txtNewCategory.setVisible(true);
        btnChooseCategory.setVisible(false);
        btnAddCategory.setVisible(true);
    }
    @FXML
    private void btnCancelSongClicked(ActionEvent event) {
        txtFilePath.setText("");
        txtSongTitle.setText("");
        txtSongArtist.setText("");
        txtTime.setText("");
        popupBg.setVisible(false);
        popupNewSong.setVisible(false);
    }
}