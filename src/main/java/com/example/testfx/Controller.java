package com.example.testfx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private final Utils utils = new Utils();
    private final Trie trie = new Trie();

    private List<Song> allSongs = new ArrayList<>();
    private Map<String, List<Song>> allSongsByPlaylists = new HashMap<>();
    private ObservableList<String> playlistNamesObsList;
    private int currentSongBoxIndex;
    private VBoxCustom currentSongBox;


    @FXML
    private VBox songContainer;
    @FXML
    private Button leftArrowButton;
    @FXML
    private Button rightArrowButton;
    @FXML
    private ScrollPane scrollPaneWithListOfSongs;
    @FXML
    private ScrollPane scrollPaneWithTextOfSong;
    @FXML
    private TextFlow lyricsTextArea;

    // search song
    @FXML
    private VBox searchSongBox;
    @FXML
    private TextField searchSongTextField;
    @FXML
    private HBox mediaPlayerBox;
    @FXML
    private Button searchSongCancelButton;

    // playlist
    @FXML
    private ComboBox<String> playlistsComboBox;
    @FXML
    private Pane addPlaylistPane;
    @FXML
    private Button addPlaylistButton;
    @FXML
    private Button addPlaylistOKButton;
    @FXML
    private TextField addPlaylistName;

    // search word
    @FXML
    private Pane searchWordPane;
    @FXML
    private Button searchWordButton;
    @FXML
    private Label searchWordResultLabel;
    @FXML
    private TextField searchWordField;
    @FXML
    private ComboBox<String> searchWordTypeComboBox;
    @FXML
    private TextField coupletNumberTextField;

    // other functionality
    @FXML
    private Pane paneWithOtherFunctionality;
    @FXML
    private Button changeRowOKButton;
    @FXML
    private TextField numberOfCoupletToBeReplaced;
    @FXML
    private TextField numberOfRowToBeReplaced;
    @FXML
    private TextField newRowToBeInserted;
    @FXML
    private Button sonnetButton;
    @FXML
    private Label otherFunctionalityResultLabel;
    @FXML
    private Pane changeRowPane;
    @FXML
    private Label newRowResultLabel;

    // media player
    private MediaPlayer mediaPlayer;
    @FXML
    private JFXSlider slider;
    private boolean isSliderBeingDragged = false;
    @FXML
    private Label authorNameLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private Label currentTimeOfMediaLabel;
    @FXML
    private Label endTimeOfMediaLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button nextSongButton;
    @FXML
    private Button previousSongButton;

    // add song
    @FXML
    private Pane addSongPane;
    @FXML
    private Button addSongOKButton;
    @FXML
    private TextField addSongAuthorName;
    @FXML
    private TextField addSongSongName;
    @FXML
    private TextArea addSongTextOfSong;
    private String filePath = null;
    @FXML
    private Label addSongChooseSongLabel;
    @FXML
    private Label addSongChoosePlaylistLabel;
    @FXML
    private Label addSongSuccessLabel;
    @FXML
    private ComboBox<String> addSongChoosePlaylist;
    private String playlistNameForNewSong = null;

    public void initialize() {
        //initialize songs and playlists
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            allSongs = objectMapper.readValue(new File("src/main/resources/com/example/testfx/data.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Song.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        allSongs.forEach(trie::insertSong);

        allSongsByPlaylists = allSongs.stream().collect(Collectors.groupingBy(Song::getPlaylistName));

        playlistNamesObsList = FXCollections.observableArrayList(allSongsByPlaylists.keySet());
        playlistNamesObsList.add(0,"Всі плейлисти");


        for (int i = 0; i < allSongs.size(); i++) {
            Song song = allSongs.get(i);
            VBoxCustom songBlock = createSongBlock(song,i);
            songContainer.getChildren().add(songBlock);
        }
        Song initialSong = allSongs.get(0);

        currentSongBoxIndex = 0;
        currentSongBox = (VBoxCustom) songContainer.getChildren().get(0);

        Label currentAuthorLabel = (Label) currentSongBox.getChildren().get(0);
        Label currentSongLabel = (Label) currentSongBox.getChildren().get(1);
        currentAuthorLabel.setTextFill(Color.RED);
        currentSongLabel.setTextFill(Color.RED);

        //initialize scrollPaneWithTextOfSong with first song
        String initialSongText = utils.getTextForSong(initialSong);
        Text text = new Text(initialSongText);
        lyricsTextArea.getChildren().add(text);

        //initialize search song
        searchSongCancelButton.setDisable(true);
        searchSongTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.toLowerCase();
            if (newValue.length()>0)
                searchSongCancelButton.setDisable(false);
            List<Song> foundedSongs = trie.findWordsWithPrefix(newValue.toLowerCase()).stream()
                    .distinct()
                    .toList();

            String currentPlaylist = playlistsComboBox.getValue();
            songContainer.getChildren().clear();

            if(!currentPlaylist.equals("Всі плейлисти")){
                List<Song> listOfSongsInCurrentPlaylist = allSongsByPlaylists.get(currentPlaylist);
                foundedSongs = foundedSongs.stream()
                        .filter(listOfSongsInCurrentPlaylist::contains)
                        .toList();
            }
            for (int i = 0; i < foundedSongs.size(); i++) {
                Song song = foundedSongs.get(i);
                VBoxCustom songBlock = createSongBlock(song,i);
                songContainer.getChildren().add(songBlock);
            }
        });


        //initialize search word
        searchWordTypeComboBox.setItems(FXCollections.observableArrayList("Пісня", "Куплет"));
        searchWordTypeComboBox.getSelectionModel().select(0);
        searchWordTypeComboBox.setVisibleRowCount(2);
        searchWordField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchWordButton.setDisable(newValue.length() == 0);
        });

        //initialize index text fields
        utils.setSingleDigitIntegerListenerForTextField(coupletNumberTextField);
        utils.setSingleDigitIntegerListenerForTextField(numberOfCoupletToBeReplaced);
        utils.setSingleDigitIntegerListenerForTextField(numberOfRowToBeReplaced);

        //initialize media player
        Media media = new Media(initialSong.getMp3());
        mediaPlayer = getNewMediaPlayer(media);
        slider.setMin(0);
        slider.setMax(1);
        slider.setValue(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Duration totalTime = mediaPlayer.getTotalDuration();
            double value = (double)newValue*totalTime.toSeconds();
            long currentTime = (long) value;
            long minutes = currentTime / 60;
            long seconds = currentTime % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            currentTimeOfMediaLabel.setText(formattedTime);
        });

        authorNameLabel.setText(initialSong.getAuthorName());
        songNameLabel.setText(initialSong.getSongName());
        previousSongButton.setDisable(true);
        previousSongButton.setFocusTraversable(false);

        //initialize add song
        addSongChoosePlaylist.setItems(playlistNamesObsList);
        addSongChoosePlaylist.getSelectionModel().select(0);
        addSongChoosePlaylist.setVisibleRowCount(4);

        addSongChoosePlaylist.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = addSongChoosePlaylist.getValue();
            if(value == null) return;
            if(value.equals("Всі плейлисти")){
                addSongChoosePlaylistLabel.setText("Виберіть плейлист");
                addSongChoosePlaylistLabel.setTextFill(Color.RED);
            }
            else {
                addSongSuccessLabel.setVisible(false);
                playlistNameForNewSong = value;
                addSongChoosePlaylistLabel.setText("Вибрано");
                addSongChoosePlaylistLabel.setTextFill(Color.GREEN);
            }
        });

        BooleanBinding isAddSongButtonActive = Bindings.createBooleanBinding(() ->

                                !addSongAuthorName.getText().trim().isEmpty() &&
                                !addSongSongName.getText().trim().isEmpty() &&
                                !addSongTextOfSong.getText().trim().isEmpty() &&
                                addSongChooseSongLabel.getText().equals("Вибрано") &&
                                addSongChoosePlaylistLabel.getText().equals("Вибрано"),

                addSongAuthorName.textProperty(), addSongSongName.textProperty(),
                addSongTextOfSong.textProperty(),addSongChooseSongLabel.textProperty(),
                addSongChoosePlaylistLabel.textProperty());

        addSongOKButton.disableProperty().bind(isAddSongButtonActive.not());

        //initialize playlist

        addPlaylistName.textProperty().addListener((observable, oldValue, newValue) -> {
            addPlaylistOKButton.setDisable(newValue.isEmpty());
        });

        playlistsComboBox.setItems(playlistNamesObsList);
        playlistsComboBox.getSelectionModel().select(0);
        playlistsComboBox.setVisibleRowCount(7);

        playlistsComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            nextSongButton.setDisable(true);
            previousSongButton.setDisable(true);
            searchSongTextField.setText("");
            String value = playlistsComboBox.getValue();
            songContainer.getChildren().clear();
            if(value == null) return;

            List<Song> newListOfSongs;
            if(value.equals("Всі плейлисти")){
                newListOfSongs = allSongs;
            } else {
                newListOfSongs = allSongsByPlaylists.get(value);
            }

            for (int i = 0; i < newListOfSongs.size(); i++) {
                Song song = newListOfSongs.get(i);
                VBoxCustom songBlock = createSongBlock(song,i);
                songContainer.getChildren().add(songBlock);
            }
        });

        //initialize change row pane
        BooleanBinding isChangeRowButtonActive = Bindings.createBooleanBinding(() ->

                        !numberOfCoupletToBeReplaced.getText().trim().isEmpty() &&
                                !numberOfRowToBeReplaced.getText().trim().isEmpty() &&
                                !newRowToBeInserted.getText().trim().isEmpty() ,

                numberOfCoupletToBeReplaced.textProperty(),
                numberOfRowToBeReplaced.textProperty(),
                newRowToBeInserted.textProperty());

        changeRowOKButton.disableProperty().bind(isChangeRowButtonActive.not());
    }
    private VBoxCustom createSongBlock(Song song, int id) {
        VBoxCustom songBox = new VBoxCustom(id,song);

        songBox.setStyle("-fx-background-color: #f0f0f0;" +
                " -fx-padding: 10;");
        songBox.setPrefWidth(songContainer.getPrefWidth());
        songBox.setMinHeight(50);

        songBox.setOnMouseEntered(e -> {
            songBox.setStyle("-fx-background-color: #888fa6; -fx-padding: 10");
        });

        songBox.setOnMouseExited(e -> {
            songBox.setStyle("-fx-background-color: transparent;-fx-padding: 10");
        });

        Label authorLabel = new Label(song.getAuthorName());
        Label songLabel = new Label(song.getSongName());
        songBox.getChildren().addAll(authorLabel, songLabel);

        songBox.setOnMouseClicked( e -> {
            previousSongButton.setDisable(false);
            nextSongButton.setDisable(false);

            int index = songBox.getCustomId();
            currentSongBoxIndex  = index;
            utils.configureAuthorAndSongLabelsForNewSong(currentSongBox,songBox);
            currentSongBox = songBox;

            if(index == 0){
                previousSongButton.setDisable(true);
            }

            if(index + 1 == songContainer.getChildren().size()){
                nextSongButton.setDisable(true);
            }
            configureMediaAndTextAreaForNewSong(song);
        });

        return songBox;
    }

    public void leftArrowButtonOnAction(){
        rightArrowButton.setDisable(false);
        leftArrowButton.setDisable(true);
        paneWithOtherFunctionality.setLayoutY(-100);
        searchWordPane.setLayoutY(0);
        searchWordPane.setLayoutX(264);
    }
    public void rightArrowButtonOnAction(){
        searchWordResultLabel.setVisible(false);
        rightArrowButton.setDisable(true);
        leftArrowButton.setDisable(false);
        searchWordPane.setLayoutY(-100);
        paneWithOtherFunctionality.setLayoutY(0);
        paneWithOtherFunctionality.setLayoutX(264);
    }

    //search song
    public void searchSongCancelOnAction(){
        searchSongTextField.setText("");
    }

    //search word
    public void searchTypeComboBoxOnAction(){
        String selectedValue = searchWordTypeComboBox.getValue();
        if(selectedValue.equals("Куплет")) {
            coupletNumberTextField.setVisible(true);
            searchWordField.setPrefWidth(70);
        } else {
            coupletNumberTextField.setVisible(false);
            searchWordField.setPrefWidth(102);
        }
    }
    public void searchButtonOnAction(){
        addPlaylistButton.setVisible(false);
        searchWordButton.setDisable(true);

        Song currentSong = currentSongBox.getSong();

        Timeline timelineDisableButton = new Timeline(new KeyFrame(Duration.seconds(2), e2 -> {
            searchWordButton.setDisable(false);
        }));
        timelineDisableButton.setCycleCount(1);
        timelineDisableButton.play();
        
        String searchFor = searchWordField.getText().toLowerCase();
        String resOfSearching = highlightTextAndReturnResultOfSearching(searchFor);

        searchWordResultLabel.setText(resOfSearching);
        if(resOfSearching.equals("Знайдено")) {
            searchWordResultLabel.setTextFill(Color.GREEN);
        }
        else
            searchWordResultLabel.setTextFill(Color.RED);

        searchWordResultLabel.setVisible(true);
        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    //removing red text
                    lyricsTextArea.getChildren().add(new Text(utils.getTextForSong(currentSong)));
                    searchWordResultLabel.setVisible(false);
                    addPlaylistButton.setVisible(true);

                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();
    }

    private String highlightTextAndReturnResultOfSearching(String searchFor) {
        String textToSearchIn;
        String textToSearchInLowCase;
        try{
            if(searchFor.length() == 0)
                throw new IllegalArgumentException();

            textToSearchIn = getTextToSearchIn();
            textToSearchInLowCase = textToSearchIn.toLowerCase();

        } catch (IndexOutOfBoundsException | NumberFormatException e){
            return "Некоректний індекс";
        }

        int fromIndex = 0;
        int index = textToSearchInLowCase.indexOf(searchFor, fromIndex);

        if (index != -1) {
            lyricsTextArea.getChildren().clear();

            while (index != -1) {
                int startIndex = index;
                int endIndex = startIndex + searchFor.length();

                Text textBefore = new Text(textToSearchIn.substring(fromIndex, startIndex));
                Text highlightedText = new Text(textToSearchIn.substring(startIndex, endIndex));
                highlightedText.setFill(Color.RED);
                lyricsTextArea.getChildren().addAll(textBefore, highlightedText);

                fromIndex = endIndex;

                index = textToSearchInLowCase.indexOf(searchFor, fromIndex);
            }

            Text textAfter = new Text(textToSearchIn.substring(fromIndex));
            lyricsTextArea.getChildren().add(textAfter);
            return "Знайдено";
        } else {
            return "Не знайдено";
        }
    }
    private String getTextToSearchIn() throws NumberFormatException, IndexOutOfBoundsException{
        Song currentSong = currentSongBox.getSong();

        if(searchWordTypeComboBox.getSelectionModel().isSelected(0)){
            return utils.getTextForSong(currentSong);
        } else {
            int numberOfCouplet = Integer.parseInt(coupletNumberTextField.getText()) - 1;
            Song.Couplet couplet = currentSong.getCouplets()[numberOfCouplet];
            return couplet.getCoupletString();
        }
    }

    // other functionality
    public void lastRowsButtonOnAction() {
        Song currentSong = currentSongBox.getSong();

        Song.Couplet[] couplets = currentSong.getCouplets();
        StringBuilder res = new StringBuilder();
        for (Song.Couplet value : couplets) {
            String[] lines = value.getCoupletString().split("\n");
            String lastLine = lines[lines.length - 1];
            res.append(lastLine).append("\n");
        }
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(res.toString());
        lyricsTextArea.getChildren().add(newText);

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    Text textOfSong = new Text(utils.getTextForSong(currentSong));
                    lyricsTextArea.getChildren().add(textOfSong);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();
        scrollPaneWithTextOfSong.requestFocus();
    }
    public void shuffleButtonOnAction(){
        Song currentSong = currentSongBox.getSong();

        List<String> list = Arrays.stream(currentSong.getCouplets())
                .map(Song.Couplet::getCoupletString)
                .collect(Collectors.toList());

        //TODO: manual shuffle
        Collections.shuffle(list);

        for(int i = 0; i < currentSong.getCouplets().length; i++){
            currentSong.getCouplets()[i].setCoupletString(list.get(i));
        }

        String initialSongText = utils.getTextForSong(currentSong);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(initialSongText);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();


    }
    public void sortWordsButtonOnAction(){
        Song currentSong = currentSongBox.getSong();

        List<String> listOfWords = new ArrayList<>();
        for (Song.Couplet couplet : currentSong.getCouplets()){
            for(String word : couplet.getCoupletString().split("[,\\s]+")){
                if(word.toLowerCase().charAt(0) == 'а' || word.toLowerCase().charAt(0) == 'о' ||
                        word.toLowerCase().charAt(0) == 'е' || word.toLowerCase().charAt(0) == 'у' ||
                        word.toLowerCase().charAt(0) == 'і' || word.toLowerCase().charAt(0) == 'и')
                    listOfWords.add(word.toLowerCase());
            }
        }
        //TODO: selection sort
        listOfWords.sort(Comparator.naturalOrder());
        listOfWords = listOfWords.stream()
                .map(s -> s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase())
                .collect(Collectors.toList());

        String res = String.join("\n", listOfWords);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(res);
        lyricsTextArea.getChildren().add(newText);
        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    Text textOfSong = new Text(utils.getTextForSong(currentSong));
                    lyricsTextArea.getChildren().add(textOfSong);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();

        scrollPaneWithTextOfSong.requestFocus();
    }

    public void changeRowButtonOnAction(){
        disableAllBackElements(true);
        changeRowPane.setLayoutY(150);
    }
    public void changeRowCancelButtonOnAction(){
        disableAllBackElements(false);
        changeRowPane.setLayoutY(1200);
    }
    public void changeRowOKButtonOnAction() {
        Song currentSong = currentSongBox.getSong();

        String newRow = this.newRowToBeInserted.getText();
        try{

            int numberOfCouplet = Integer.parseInt(this.numberOfCoupletToBeReplaced.getText()) - 1;
            int numberOfRow = Integer.parseInt(this.numberOfRowToBeReplaced.getText()) - 1;
            Song.Couplet couplet = currentSong.getCouplets()[numberOfCouplet];
            String[] lines = couplet.getCoupletString().split("\n");
            lines[numberOfRow] = newRow;
            String newCouplet = String.join("\n", lines);
            couplet.setCoupletString(newCouplet);

            //display changes
            String newSongText = utils.getTextForSong(currentSong);
            lyricsTextArea.getChildren().clear();
            Text text = new Text(newSongText);
            lyricsTextArea.getChildren().add(text);

            newRowResultLabel.setText("Змінено успішно");
            newRowResultLabel.setTextFill(Color.GREEN);
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            newRowResultLabel.setText("Некоректний індекс");
            newRowResultLabel.setTextFill(Color.RED);
        }

        changeRowPane.setPrefHeight(107);
        sonnetButton.setDisable(false);
        numberOfCoupletToBeReplaced.setText("");
        numberOfRowToBeReplaced.setText("");
        newRowToBeInserted.setText("");

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    newRowResultLabel.setText("");
                    changeRowPane.setPrefHeight(92);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();
    }

    public void isSonnetButtonOnAction(){
        Song currentSong = currentSongBox.getSong();

        int countOfRows = 0;
        String[] lines;
        for(Song.Couplet couplet : currentSong.getCouplets()){
            lines = couplet.getCoupletString().split("\n");
            countOfRows += lines.length;
        }
        if (countOfRows == 14) {
            otherFunctionalityResultLabel.setText("Shakespeare");
            otherFunctionalityResultLabel.setTextFill(Color.GREEN);

        } else if (countOfRows == 12) {
            otherFunctionalityResultLabel.setText("Spencerian");
            otherFunctionalityResultLabel.setTextFill(Color.GREEN);

        } else {
            otherFunctionalityResultLabel.setText("Не є сонетом");
            otherFunctionalityResultLabel.setTextFill(Color.RED);
        }
        otherFunctionalityResultLabel.setVisible(true);
        sonnetButton.setVisible(false);
        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    otherFunctionalityResultLabel.setVisible(false);
                    sonnetButton.setVisible(true);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();
    }

    // media player
    public void sliderOnMouseReleased(){
        double newValue = slider.getValue();
        mediaPlayer.seek(Duration.seconds(newValue * mediaPlayer.getTotalDuration().toSeconds()));
        isSliderBeingDragged = false;
    }
    public void sliderOnMousePressed() {
        isSliderBeingDragged = true;
    }
    public void playButtonOnAction(){
        playButton.setVisible(false);
        pauseButton.setVisible(true);
        mediaPlayer.play();
    }
    public void pauseButtonOnAction(){
        pauseButton.setVisible(false);
        playButton.setVisible(true);
        mediaPlayer.pause();
    }
    public void changeSongInMP3Player(boolean isNextSong){
        int newIndex;
        Song song;
        if(isNextSong){
            newIndex = currentSongBoxIndex + 1;
            previousSongButton.setDisable(false);
            if(currentSongBoxIndex + 2 >= songContainer.getChildren().size()){
                nextSongButton.setDisable(true);
            }
        } else {
            newIndex = currentSongBoxIndex - 1;
            nextSongButton.setDisable(false);
            if(currentSongBoxIndex - 2 <= -1){
                previousSongButton.setDisable(true);
            }
        }

        currentSongBoxIndex = newIndex;
        VBoxCustom newCurrentSongBox = (VBoxCustom) songContainer.getChildren().get(currentSongBoxIndex);
        utils.configureAuthorAndSongLabelsForNewSong(currentSongBox,newCurrentSongBox);
        currentSongBox = newCurrentSongBox;

        song = currentSongBox.getSong();
        configureMediaAndTextAreaForNewSong(song);
    }

    private void configureMediaAndTextAreaForNewSong(Song song) {
        String textForSong = utils.getTextForSong(song);
        lyricsTextArea.getChildren().clear();
        Text text = new Text(textForSong);
        lyricsTextArea.getChildren().add(text);

        pauseButton.setVisible(true);
        playButton.setVisible(false);
        mediaPlayer.pause();
        Media media = new Media(song.getMp3());
        mediaPlayer = getNewMediaPlayer(media);
        mediaPlayer.play();

        authorNameLabel.setText(song.getAuthorName());
        songNameLabel.setText(song.getSongName());
        slider.setValue(0);

        scrollPaneWithTextOfSong.requestFocus();
    }

    public void nextSongButtonOnAction() {
        changeSongInMP3Player(true);
    }
    public void previousSongButtonOnAction() {
        changeSongInMP3Player(false);
    }
    private MediaPlayer getNewMediaPlayer(Media media){
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSliderBeingDragged) {
                Duration totalTime = mediaPlayer.getTotalDuration();
                double value = newValue.toSeconds() / totalTime.toSeconds();
                slider.setValue(value);
            }
        });
        mediaPlayer.setOnReady(() -> {
            long totalSeconds = (long) media.getDuration().toSeconds();
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            endTimeOfMediaLabel.setText(formattedTime);
        });
        mediaPlayer.setOnEndOfMedia(this::nextSongButtonOnAction);
        return mediaPlayer;
    }

    // add song
    public void addSongButtonOnAction(){
        addSongPane.setLayoutY(100);
        disableAllBackElements(true);
    }

    public void addSongOKButtonOnAction(){
        String authorName = addSongAuthorName.getText();
        String songName = addSongSongName.getText();
        String textOfSong = addSongTextOfSong.getText();
        String mp3PathForMedia = filePath.trim().substring(2);
        mp3PathForMedia = "file:" + mp3PathForMedia;
        mp3PathForMedia = mp3PathForMedia.replace("\\","/");

        Song newSong = new Song(authorName, songName, new Song.Couplet[]{
                new Song.Couplet(1, textOfSong)}, mp3PathForMedia, playlistNameForNewSong);

        //adding song to all data structures
        allSongs.add(newSong);
        allSongsByPlaylists.get(playlistNameForNewSong).add(newSong);
        trie.insertSong(newSong);

        //updating songs in playlist (need if selected playlist == playlist of new song)
        int currentSelectedIndex = playlistsComboBox.getSelectionModel().getSelectedIndex();
        playlistsComboBox.getSelectionModel().select(0);
        playlistsComboBox.getSelectionModel().select(currentSelectedIndex);


        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        try {
            objectWriter.writeValue(new File("src/main/resources/com/example/testfx/data.json"), allSongs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addSongChooseSongLabel.setVisible(false);
        addSongChoosePlaylistLabel.setVisible(false);
        addSongSuccessLabel.setVisible(true);

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    addSongChooseSongLabel.setVisible(true);
                    addSongChoosePlaylistLabel.setVisible(true);
                    addSongSuccessLabel.setVisible(false);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();

        resetValueInAddSongBox();
        disableAllBackElements(false);
    }
    public void addSongCancelButtonOnAction(){
        addSongPane.setLayoutY(600);
        addSongSuccessLabel.setVisible(false);
        resetValueInAddSongBox();
        disableAllBackElements(false);
    }
    private void resetValueInAddSongBox() {
        addSongChooseSongLabel.setText("Виберіть пісню");
        addSongChooseSongLabel.setTextFill(Color.RED);
        addSongChoosePlaylistLabel.setText("Виберіть плейлист");
        addSongChoosePlaylistLabel.setTextFill(Color.RED);

        addSongTextOfSong.setText("");
        addSongAuthorName.setText("");
        addSongSongName.setText("");
        addSongChoosePlaylist.getSelectionModel().select(0);
    }

    public void addSongChooseFileButtonOnAction() {
        addSongSuccessLabel.setVisible(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            System.out.println(filePath);
            addSongChooseSongLabel.setText("Вибрано");
            addSongChooseSongLabel.setTextFill(Color.GREEN);
        } else {
            addSongChooseSongLabel.setText("Вибір скасовано.");
            addSongChooseSongLabel.setTextFill(Color.RED);
        }
    }


    // playlist
    public void addPlaylistButtonOnAction(){
        scrollPaneWithTextOfSong.setDisable(true);
        addPlaylistPane.setLayoutY(150);
        disableAllBackElements(true);
    }
    public void addPlaylistCancelButtonOnAction(){
        scrollPaneWithTextOfSong.setDisable(false);
        addPlaylistPane.setLayoutY(900);
        addPlaylistName.setText("");
        disableAllBackElements(false);
    }
    public void addPlaylistOKButtonOnAction(){
        String playlistName = addPlaylistName.getText();
        if(!playlistNamesObsList.contains(playlistName)){
            playlistNamesObsList.add(playlistName);
            allSongsByPlaylists.put(playlistName, new ArrayList<>());
        }
        scrollPaneWithTextOfSong.setDisable(false);
        addPlaylistPane.setLayoutY(900);
        addPlaylistName.setText("");
        disableAllBackElements(false);
    }
    private void disableAllBackElements(boolean isDisabled){
        scrollPaneWithTextOfSong.setDisable(isDisabled);
        scrollPaneWithListOfSongs.setDisable(isDisabled);
        leftArrowButton.setDisable(isDisabled);
        rightArrowButton.setDisable(isDisabled);
        searchWordPane.setDisable(isDisabled);
        paneWithOtherFunctionality.setDisable(isDisabled);
        searchSongBox.setDisable(isDisabled);
        mediaPlayerBox.setDisable(isDisabled);
    }
}