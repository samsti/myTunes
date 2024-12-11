package dk.easv.mytunes.ui;

import dk.easv.mytunes.be.Category;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.BLLManager;
import dk.easv.mytunes.exceptions.DBException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML private Button btnFilter;
    @FXML private StackPane rootPane;
    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    @FXML private Button btnNewPlayList;
    @FXML private Button btnEditPlayList;
    @FXML private Button btnDeletePlayList;
    @FXML private Button btnAddSongToPlayList;
    @FXML private Button btnMoveSongUp;
    @FXML private Button btnMoveSongDown;
    @FXML private Button btnDeleteFromPlaylist;
    @FXML private Button btnNewSong;
    @FXML private Button btnEditSong;
    @FXML private Button btnDeleteSong;
    @FXML private Button btnClose;
    @FXML private TextField txtFilter;
    @FXML private TextField txtNewPlaylist;
    @FXML private VBox popupVBox;
    @FXML private VBox popupBg;
    @FXML private VBox popupNewSong;
    @FXML private TextField txtSongTitle;
    @FXML private TextField txtSongArtist;
    @FXML private ChoiceBox<Category> choiceCategory;
    @FXML private Button btnChooseCategory;
    @FXML private Button btnAddCategory;
    @FXML private TextField txtTime;
    @FXML private TextField txtFilePath;
    @FXML private Button btnChoose;
    @FXML private Button btnSaveSong;
    @FXML private Button btnCancelSong;
    @FXML private TextField txtNewCategory;
    @FXML private TableView<Playlist> tblPlaylist;
    @FXML private TableColumn<Playlist, String> columnName;
    @FXML private TableColumn<Playlist, Integer> columnItem;
    @FXML private TableColumn<Playlist, Time> columnTotalDuration;
    @FXML private ListView<Song> lstSongsInPlaylist;
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    @FXML private TableColumn<Song, String> categoryColumn;
    @FXML private Slider volumeSlider;
    @FXML private Label lblVolume;
    private final MyTunesModel model = new MyTunesModel();
    @FXML private VBox popupDelete;
    @FXML private Label lblDeleting;
    @FXML private CheckBox cbDeleteFile;
    @FXML private Button btncancDelete;
    @FXML private Button btnYesDelete;
    @FXML private Slider sldVolume;
    @FXML private Button btnSavePlaylist;
    @FXML private Label lblPlaying;
    @FXML private Button btnNext;
    @FXML private Button btnBack;
    private final static String DELETING_DEFAULT_TEXT = "Are you sure you want to delete ";
    private BLLManager manager;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private Song nextSong;
    private boolean isFilterMode = true;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manager = new BLLManager();
        popupBg.setPrefWidth(rootPane.getWidth());
        popupBg.setPrefHeight(rootPane.getHeight());
        loadSongs();
        lblVolume.setText(String.valueOf(manager.getVolume()) + "%");

       // Adds a listener to any slider changes
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double volume = newValue.doubleValue();
                manager.setVolume(volume/100); //Passes the new volume to the Business Layer
                lblVolume.setVisible(true);
                lblVolume.setText(String.format("%.2f", volume) + "%");
            }
        });
        volumeSlider.setOnMouseReleased(event -> lblVolume.setVisible(false));
/*
        tblPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tblSongs.getSelectionModel().clearSelection();
            }
        });

        tblSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tblPlaylist.getSelectionModel().clearSelection();

            }
        });
 */
    }



    private void loadSongs() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnTotalDuration.setCellValueFactory(new PropertyValueFactory<>("totalDuration"));
        columnItem.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));

        // Load songs into TableView
        try {
            model.loadSongs();
            tblSongs.setItems(model.getSongs());
            model.loadPlaylists();
            tblPlaylist.setItems(model.getPlaylists());

            tblPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try {
                        model.loadSongsOnPlaylist(newValue.getId());
                        lstSongsInPlaylist.setItems(model.getSongsOnPlaylist());
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (DBException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void newPlaylistClicked(ActionEvent event) {
        openPlaylistPopUp();
        btnSavePlaylist.setOnAction(e -> savePlaylistButtonClicked(event));
    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        closePlaylistPopUp();

    }
    @FXML
    private void saveButtonClicked(ActionEvent event) {
        Playlist editPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
        editPlaylist.setName(txtNewPlaylist.getText().trim());
        if (manager.editPlaylistName(editPlaylist)) {
            tblPlaylist.getSelectionModel().getSelectedItem().setName(txtNewPlaylist.getText().trim());
            tblPlaylist.refresh();
        }
        closePlaylistPopUp();
    }
    @FXML
    private void savePlaylistButtonClicked(ActionEvent event) {
        String playlistName = txtNewPlaylist.getText();
        Alert a = new Alert(Alert.AlertType.NONE);

        if (!playlistName.isBlank()) {
            model.createPlaylist(playlistName);
        } else {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Please fill in all fields");
            a.show();
        }

        txtNewPlaylist.setText("");
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
    }
    @FXML
    private void btnNewSongClicked(ActionEvent event) {
        openSongsPopUp();
        btnSaveSong.setOnAction(e -> btnSaveSongClicked(event));
    }
    @FXML
    private void btnChooseCategoryClicked(ActionEvent event) {
        btnChooseCategory.setVisible(false);
        txtNewCategory.setVisible(true);
        btnAddCategory.setVisible(false);
    }
    @FXML
    private void btnCancelSongClicked(ActionEvent event) {
        closeSongsPopUp();
    }

    @FXML
    private void btnDeletePlayListClicked (ActionEvent event) {
        if (tblPlaylist.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            Playlist playlistToDelete = tblPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + playlistToDelete.getName() + " playlist?");
        }
    }
    @FXML
    private void btnDeleteFromPlaylistClicked(ActionEvent event) {
        if (lstSongsInPlaylist.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            btnYesDelete.setOnAction(e -> btnYesDeleteFromPlaylistClicked(event));
            Playlist playlistToDeleteFrom = getSelectedPlaylist();
            Song songToDelete = lstSongsInPlaylist.getSelectionModel().getSelectedItem();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + songToDelete.getTitle()
                    + "\nfrom playlist: " + playlistToDeleteFrom.getName() + "?");
        }
    }
    private void btnYesDeleteFromPlaylistClicked(ActionEvent event) {
        Song songToDelete = getSelectedSongInPlaylist();
        model.deleteFromPlaylist(songToDelete, getSelectedPlaylist());
        int playlistId = getSelectedPlaylist().getId();
        getSelectedPlaylist().setNumberOfSongs(model.getNumberOfSongsInPlaylist(playlistId));
        getSelectedPlaylist().setTotalDuration(String.valueOf(model.getTotalPlaylistTime(playlistId)));
        tblPlaylist.refresh();
        closeDeleteWindow();
    }

    @FXML
    private void btnDeleteSongClicked(ActionEvent event) {
        if (tblSongs.getSelectionModel().getSelectedItem() != null) {
            openDeleteWindow();
            cbDeleteFile.setVisible(true);
            btnYesDelete.setOnAction(e -> btnYesDeleteSongClicked(event));
            Song songToDelete = getSelectedSong();
            lblDeleting.setText(DELETING_DEFAULT_TEXT + songToDelete.getTitle() + " song?");
        }
    }
    private void btnYesDeleteSongClicked(ActionEvent event) {
        model.deleteSong(getSelectedSong(), cbDeleteFile.isSelected());
        closeDeleteWindow();
    }

    @FXML
    private void btncancDeleteClicked(ActionEvent event)  {
        closeDeleteWindow();
    }

    @FXML
    private void btnYesDeleteClicked(ActionEvent event) {
        model.deletePlaylist(getSelectedPlaylist());
        closeDeleteWindow();
    }

    @FXML
    private void btnCloseClicked(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void searchSongs(ActionEvent event) {
        String textFilter = txtFilter.getText().trim();

        if(isFilterMode) {
            if (textFilter.isBlank()) {
                System.out.println("Blank");
            } else {
                model.loadFilteredSongs(textFilter);
                tblSongs.setItems(model.getFilteredSongs());
                btnFilter.setText("Clear");
                isFilterMode = false;
            }
        } else {
            txtFilter.setText("");
            btnFilter.setText("Filter");
            loadSongs();
            isFilterMode = true;
        }
    }


    @FXML
    private void btnPlayClicked(ActionEvent event) {
        Song nextSong = null;

        try {
            if (getSelectedSong() != null && getSelectedPlaylist() == null) {
                Song songToPlay = tblSongs.getSelectionModel().getSelectedItem();

                if (songToPlay == null) {
                    lblPlaying.setText("No song selected");
                    return;
                }

                manager.playSong(songToPlay);
                lblPlaying.setText("Playing: " + songToPlay.getTitle());
            }

            if (getSelectedPlaylist() != null && getSelectedSongInPlaylist() != null) {
                Playlist playlistToPlay = getSelectedPlaylist();
                Song playlistSong = getSelectedSongInPlaylist();

                if (playlistSong == null) {
                    lblPlaying.setText("No song selected in playlist");
                    return;
                }

                manager.playSongInPlaylist(playlistSong, playlistToPlay);
                lblPlaying.setText("Playing: " + playlistSong.getTitle() + " from playlist " + playlistToPlay.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (getSelectedSong() != null && getSelectedPlaylist() == null) {
                nextSong = manager.getCurrentSong();
                lblPlaying.setText(nextSong.getTitle() + " - path not found");
            } if(getSelectedPlaylist() != null && getSelectedSongInPlaylist() != null) {
                Playlist playlistToPlay = getSelectedPlaylist();
                nextSong = manager.getCurrentSongInPlaylist(playlistToPlay);
                lblPlaying.setText(nextSong.getTitle() + " - path not found");
            }
        }
    }


    @FXML
    private void btnStopClicked(ActionEvent event) {
        try {
            manager.stopSong();
            isPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrentSelectedSong(Song song) {
        List<Song> songList;
        ObservableList<Song> observableList = tblSongs.getItems();
        songList = new ArrayList<>(observableList);
        int index = songList.indexOf(song);
        tblSongs.getSelectionModel().select(index);
        tblSongs.scrollTo(index);
    }

    public List<Song> getSongsInPlaylist() {
        List<Song> lstOfSongsInPlaylist = new ArrayList<>(lstSongsInPlaylist.getItems());
        return lstOfSongsInPlaylist;
    }

    @FXML
    private void btnPlayNextClicked(ActionEvent event) {
        List<Song> songList = null;
        Song nextSong = null;

        try {
            Song currentSongInPlaylist = manager.getCurrentSongInPlaylist(getSelectedPlaylist());
            Playlist currentPlaylist = getSelectedPlaylist();

            if (currentSongInPlaylist == null || currentPlaylist == null) {
                ObservableList<Song> observableList = tblSongs.getItems();
                songList = new ArrayList<>(observableList);
                Song currentSong = manager.getCurrentSong();
                nextSong = manager.findNextSong(songList, currentSong);


                manager.playSong(nextSong);
                setCurrentSelectedSong(nextSong);
                lblPlaying.setText("Now playing: " + nextSong.getTitle());
            }


           if (nextSong != null && currentPlaylist != null) {

               Song currentSong = manager.getCurrentSong();
               nextSong = manager.findNextSongInPlaylist(currentPlaylist, currentSong);
               manager.playSongInPlaylist(nextSong, currentPlaylist);
               setCurrentSelectedSong(nextSong);
               lblPlaying.setText("Now playing: " + nextSong.getTitle() + "in Playlist: " + currentPlaylist.getName()) ;

           }

        } catch (Exception e) {
            e.printStackTrace();
            lblPlaying.setText(manager.getCurrentSongTitle() + " - path not found");
            MediaPlayer player = manager.getMediaPlayer();
            player.stop();
        }
    }



    @FXML
    private void btnPlayPreviousClicked(ActionEvent event) {
        List<Song> songList = null;
        Song previousSong = null;
        try {
            ObservableList<Song> observableList = tblSongs.getItems();
            songList = new ArrayList<>(observableList);

            Song currentSong = manager.getCurrentSong();

            previousSong = manager.findPreviousSong(songList, currentSong);

            if (previousSong != null) {
                manager.playSong(previousSong);
                lblPlaying.setText("Now playing: " + previousSong.getTitle());
                setCurrentSelectedSong(previousSong);
            } else {
                lblPlaying.setText("No previous song available.");
            }
        } catch (Exception e) {
            lblPlaying.setText(manager.getCurrentSongTitle() + " - path not found");
            MediaPlayer player = manager.getMediaPlayer();
            setCurrentSelectedSong(previousSong);
            player.stop();
        }
    }


    @FXML
    private void setVolume(MouseEvent event) {
        //mediaPlayer.setVolume(manager.getVolume());
        manager.setVolume(volumeSlider.getValue()/100);
    }

    @FXML
    private void btnChooseClicked(ActionEvent event) {
        String filepath =  manager.openFile(btnChoose.getScene().getWindow());
        txtFilePath.setText(filepath);
    }

    @FXML
    private void btnEditPlayListClicked(ActionEvent event) {
        Playlist playlistToEdit = tblPlaylist.getSelectionModel().getSelectedItem();
        if (playlistToEdit != null) {
            openPlaylistPopUp();
            btnSavePlaylist.setOnAction(e -> saveButtonClicked(event));
            txtNewPlaylist.setText(playlistToEdit.getName());
        }
    }
    @FXML
    private void btnEditSongClicked(ActionEvent event) {
        Song songToEdit = tblSongs.getSelectionModel().getSelectedItem();
        if (songToEdit != null) {
            openSongsPopUp();
            btnSaveSong.setOnAction(e -> btnSaveSongClickedEdit(event));

            txtSongTitle.setText(songToEdit.getTitle());
            txtSongArtist.setText(songToEdit.getArtist());
            txtFilePath.setText(songToEdit.getFilePath());
            txtTime.setText(songToEdit.getDuration());
            choiceCategory.setValue(manager.returnCategoryName(songToEdit.getCategory()));
        }
        else {
            throw new RuntimeException("No song selected");
        }
    }
    @FXML
    private void btnSaveSongClicked(ActionEvent event) {
        int songId = -1;
            String title = txtSongTitle.getText().trim();
            String artist = txtSongArtist.getText().trim();
            String filePath = txtFilePath.getText().trim();
            Time duration = checkValidTime(txtTime.getText().trim());

            int category;
        if (txtNewCategory.getText().isEmpty())
            category = choiceCategory.getSelectionModel().getSelectedItem().getId();
        else {
            category = createNewCategory(txtNewCategory.getText().trim());
        }
            if (!title.isEmpty() && !artist.isEmpty() && !filePath.isEmpty() && category > 0) {
                songId = model.addSong(title, artist, filePath, duration, category);
            }
            if (songId != -1)
                closeSongsPopUp();
        }

    @FXML
    private void btnSaveSongClickedEdit(ActionEvent event) {
        Song songToEdit = tblSongs.getSelectionModel().getSelectedItem();
        if (songToEdit != null) {
            songToEdit.setTitle(txtSongTitle.getText().trim());
            songToEdit.setArtist(txtSongArtist.getText().trim());
            songToEdit.setFilePath(txtFilePath.getText().trim());
            songToEdit.setDuration(checkValidTime(txtTime.getText().trim()));
            if (txtNewCategory.getText().isEmpty())
                songToEdit.setCategory(choiceCategory.getSelectionModel().getSelectedItem().getId());
            else {
                songToEdit.setCategory(createNewCategory(txtNewCategory.getText().trim()));
            }
            if (manager.editSong(songToEdit))
                closeSongsPopUp();
        }
        else
            throw new RuntimeException("No song selected");
    }
    @FXML
    private void btnMoveSongDownClicked(ActionEvent event) {
        Song selectedSong = lstSongsInPlaylist.getSelectionModel().getSelectedItem();
        Playlist selectedPlayList = tblPlaylist.getSelectionModel().getSelectedItem();
        if (selectedSong != null && selectedPlayList != null) {
            model.moveSongDownInList(selectedSong, selectedPlayList.getId());
        }
    }
    @FXML
    private void btnMoveSongUpClicked(ActionEvent event) {
        Song selectedSong = lstSongsInPlaylist.getSelectionModel().getSelectedItem();
        Playlist selectedPlayList = tblPlaylist.getSelectionModel().getSelectedItem();
        if (selectedSong != null && selectedPlayList != null)
            model.moveSongUpInList(selectedSong, selectedPlayList.getId());
        model.loadSongsOnPlaylist(selectedPlayList.getId());

    }


    /**
     *
     *   Opening and closing the popup windows
     *
     */
    private void openPlaylistPopUp() {
        popupBg.setVisible(true);
        popupVBox.setVisible(true);
        txtNewPlaylist.setText("");
    }

    private void closePlaylistPopUp() {
        popupBg.setVisible(false);
        popupVBox.setVisible(false);
    }
    private void openSongsPopUp() {
        popupBg.setVisible(true);
        popupNewSong.setVisible(true);
        popupNewSong.toFront();
        txtFilePath.setText("");
        txtSongTitle.setText("");
        txtSongArtist.setText("");
        txtTime.setText("");
        ObservableList<Category> categories = FXCollections.observableArrayList();
        categories.addAll(model.getCategories());
        choiceCategory.setItems(categories);
    }
    private void closeSongsPopUp() {

        popupBg.setVisible(false);
        popupNewSong.setVisible(false);
        txtNewCategory.setVisible(false);
        btnAddCategory.setVisible(false);
        btnChooseCategory.setVisible(true);
    }
    private void openDeleteWindow() {
        popupBg.setVisible(true);
        popupDelete.setVisible(true);
        cbDeleteFile.setVisible(false);
    }
    private void closeDeleteWindow() {
        cbDeleteFile.setSelected(false);
        popupBg.setVisible(false);
        popupDelete.setVisible(false);
    }

    /**
     * Gets the selected items from the tables/list
     */

    private Song getSelectedSong() {
        return tblSongs.getSelectionModel().getSelectedItem();
    }

    private Playlist getSelectedPlaylist() {
        return tblPlaylist.getSelectionModel().getSelectedItem();
    }

    private Song getSelectedSongInPlaylist() {
        return lstSongsInPlaylist.getSelectionModel().getSelectedItem();
    }

    public void setLabelForSong(Song song) {
        lblPlaying.setText("Playing: " + song.getTitle());
    }

    public void addSongToPlaylist(ActionEvent actionEvent) {
        int songId = getSelectedSong().getId();
        int playlistId = getSelectedPlaylist().getId();
        model.addSongToPlaylist(playlistId, songId);
        model.loadSongsOnPlaylist(playlistId);
        model.getTotalPlaylistTime(playlistId);
        getSelectedPlaylist().setNumberOfSongs(model.getNumberOfSongsInPlaylist(playlistId));
        getSelectedPlaylist().setTotalDuration(String.valueOf(model.getTotalPlaylistTime(playlistId)));
        tblPlaylist.refresh();
        lstSongsInPlaylist.setItems(model.getSongsOnPlaylist());
    }

    /**
     * Checking
     */
    private Time checkValidTime(String time) {
        if (time == null || time.isEmpty()) {
            throw new RuntimeException("Invalid time format: Input is null or empty.");
        }

        String timePattern = "^([01]?\\d|2[0-3]):[0-5]\\d(:[0-5]\\d(\\.\\d{1,7})?)?$";
        String shortTimePattern = "^[0-5]?\\d:[0-5]\\d$"; // m:ss
        if (!time.matches(timePattern) && !time.matches(shortTimePattern)) {
            throw  new RuntimeException("Invalid time format: Does not match TIME pattern.");
        }

        try {
            LocalTime parsedTime;

            if (time.matches(shortTimePattern)) {
                // Convert m:ss to HH:mm:ss by assuming hours = 0
                String[] parts = time.split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                parsedTime = LocalTime.of(0, minutes, seconds);
            } else {
                // Parse HH:mm:ss or HH:mm directly
                parsedTime = LocalTime.parse(time);
            }
            // Format the parsed time as HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = parsedTime.format(formatter);
            // Return as SQL Time
            return Time.valueOf(formattedTime);
        } catch (Exception e) {
            throw new RuntimeException("Invalid time format: Parsing failed.", e);
        }
    }

    private int createNewCategory(String name) {
        return model.createNewCategory(name);
    }

}