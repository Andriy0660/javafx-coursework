package com.example.testfx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
    private Utils utils = new Utils();
    @FXML
    private Pane mainPane;

    @FXML
    private VBox songContainer;
    private List<Song> songs = new ArrayList<>();
    private Label currentAuthorLabel;
    private Label currentSongLabel;

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
    private int currentSongIndex;

    //searching
    @FXML
    private Pane searchPane;
    @FXML
    private Button searchButton;
    @FXML
    private Label searchResultLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;
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
    private Button sonetButton;
    @FXML
    private Text otherFunctionalityResultLabel;

    //media player
    private MediaPlayer mediaPlayer;
    @FXML
    private Slider slider;
    private boolean isSliderBeingDragged = false;
    @FXML
    private Label authorNameLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button nextSongButton;
    @FXML
    private Button previousSongButton;

    //add song

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
    @FXML
    private Label addSongChooseSongLabel;
    @FXML
    private Label addSongChoosePlaylistLabel;
    @FXML
    private Label addSongSuccessLabel;
    @FXML
    private ComboBox<String> addSongChoosePlaylist;
    @FXML
    private String playlistNameForNewSong = null;
    private String filePath = null;

    private Set<String> playlistNamesSet = new HashSet<>();
    @FXML
    private ComboBox<String> playlistsComboBox;
    @FXML
    private Pane addPlaylistPane;
    @FXML
    private Button addPlaylistButton;
    @FXML
    private Button addPlaylistOKButton;
    @FXML
    private Button addPlaylistCancelButton;
    @FXML
    private TextField addPlaylistName;

    public void initialize() {
        //initialize songs
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            songs = objectMapper.readValue(new File("src/main/resources/com/example/testfx/data.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Song.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        playlistNamesSet.add("Всі плейлисти");
        for (Song song : songs) {
            playlistNamesSet.add(song.getPlaylistName());
            VBox songBlock = createSongBlock(song);
            songContainer.getChildren().add(songBlock);
        }


        Song initialSong = songs.get(0);
        currentSongIndex = 0;
        VBox songBox = (VBox)songContainer.getChildren().get(0);
        currentAuthorLabel = (Label) songBox.getChildren().get(0);
        currentSongLabel = (Label) songBox.getChildren().get(1);
        currentAuthorLabel.setTextFill(Color.RED);
        currentSongLabel.setTextFill(Color.RED);

        //initialize scrollPaneWithTextOfSong with first song
        String initialSongText = utils.getTextForSong(initialSong);
        Text text = new Text(initialSongText);
        lyricsTextArea.getChildren().add(text);

        //initialize playlists combo box
        playlistsComboBox.setItems(FXCollections.observableList(new ArrayList<>(playlistNamesSet)));
        playlistsComboBox.getSelectionModel().select(0);
        playlistsComboBox.setVisibleRowCount(7);

        playlistsComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = playlistsComboBox.getValue();
            songContainer.getChildren().clear();
            if(value == null) return;
            if(value.equals("Всі плейлисти")){
                for (Song song : songs) {
                    VBox songBlock = createSongBlock(song);
                    songContainer.getChildren().add(songBlock);
                }
            }
            else {
                for (Song song : songs) {
                    if(!song.getPlaylistName().equals(value))
                        continue;
                    VBox songBlock = createSongBlock(song);
                    songContainer.getChildren().add(songBlock);
                }
            }

        });
        //initialize search section and search combo box
        searchTypeComboBox.setItems(FXCollections.observableArrayList("Пісня", "Куплет"));
        searchTypeComboBox.getSelectionModel().select(0);
        searchTypeComboBox.setVisibleRowCount(2);

        //initialize index text fields
        utils.setSingleDigitIntegerListenerForTextField(coupletNumberTextField);
        utils.setSingleDigitIntegerListenerForTextField(numberOfCoupletToBeReplaced);
        utils.setSingleDigitIntegerListenerForTextField(numberOfRowToBeReplaced);

        //initialize media player
        Media media = new Media(initialSong.getMp3());
        mediaPlayer = getNewMediaPlayer(media);
        slider.setMin(0);
        slider.setMax(1);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Duration totalTime = mediaPlayer.getTotalDuration();
            double value = (double)newValue*totalTime.toSeconds();
            long currentTime = (long) value;
            long minutes = currentTime / 60;
            long seconds = currentTime % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            currentTimeLabel.setText(formattedTime);
        });

        authorNameLabel.setText(initialSong.getAuthorName());
        songNameLabel.setText(initialSong.getSongName());
        previousSongButton.setDisable(true);
        previousSongButton.setFocusTraversable(false);

        //initialize add song
        List<String> newList = new ArrayList<>(playlistNamesSet);
        newList.set(0,"Вибрати плейлист");

        addSongChoosePlaylist.setItems(FXCollections.observableList(newList));
        addSongChoosePlaylist.getSelectionModel().select(0);
        addSongChoosePlaylist.setVisibleRowCount(4);
        addSongChoosePlaylist.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = addSongChoosePlaylist.getValue();
            if(value == null) return;
            if(value.equals("Вибрати плейлист")){
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

        BooleanBinding isButtonActive = Bindings.createBooleanBinding(() ->
                        !addSongAuthorName.getText().trim().isEmpty() &&
                                !addSongSongName.getText().trim().isEmpty() &&
                                !addSongTextOfSong.getText().trim().isEmpty() &&
                                addSongChooseSongLabel.getText().equals("Вибрано") &&
                                addSongChoosePlaylistLabel.getText().equals("Вибрано"),

                addSongAuthorName.textProperty(), addSongSongName.textProperty(),
                addSongTextOfSong.textProperty(),addSongChooseSongLabel.textProperty(),addSongChoosePlaylistLabel.textProperty());

        addSongOKButton.disableProperty().bind(isButtonActive.not());

        addPlaylistName.textProperty().addListener((observable, oldValue, newValue) -> {
            addPlaylistOKButton.setDisable(newValue.isEmpty());
        });

    }

    private VBox createSongBlock(Song song) {
        VBox songBox = new VBox();
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
            this.currentAuthorLabel.setTextFill(Color.BLACK);
            this.currentSongLabel.setTextFill(Color.BLACK);
            this.currentAuthorLabel = authorLabel;
            this.currentSongLabel = songLabel;

            previousSongButton.setDisable(false);
            nextSongButton.setDisable(false);
            authorLabel.setTextFill(Color.RED);
            songLabel.setTextFill(Color.RED);

            int index = songs.indexOf(song);
            currentSongIndex  = index;

            if(index == 0){
                previousSongButton.setDisable(true);
            }
            else if(index + 1 == songs.size()){
                nextSongButton.setDisable(true);
            }
            pauseButton.setVisible(true);
            playButton.setVisible(false);
            otherFunctionalityResultLabel.setVisible(false);

            mediaPlayer.pause();
            Media media = new Media(song.getMp3());
            mediaPlayer = getNewMediaPlayer(media);
            mediaPlayer.play();

            String textForSong = utils.getTextForSong(song);
            lyricsTextArea.getChildren().clear();
            Text text = new Text(textForSong);
            lyricsTextArea.getChildren().add(text);

            authorNameLabel.setText(song.getAuthorName());
            songNameLabel.setText(song.getSongName());
            slider.setValue(0);

            scrollPaneWithTextOfSong.requestFocus();
        });

        return songBox;
    }

    public void leftArrowButtonOnAction(){
        otherFunctionalityResultLabel.setVisible(false);
        rightArrowButton.setDisable(false);
        leftArrowButton.setDisable(true);
        paneWithOtherFunctionality.setLayoutY(-100);
        searchPane.setLayoutY(0);
        searchPane.setLayoutX(264);
    }
    public void rightArrowButtonOnAction(){
        searchResultLabel.setVisible(false);
        rightArrowButton.setDisable(true);
        leftArrowButton.setDisable(false);
        searchPane.setLayoutY(-100);
        paneWithOtherFunctionality.setLayoutY(0);
        paneWithOtherFunctionality.setLayoutX(264);
    }

    public void searchTypeComboBoxOnAction(){
        String selectedValue = searchTypeComboBox.getValue();
        if(selectedValue.equals("Куплет")) {
            coupletNumberTextField.setVisible(true);
            searchField.setPrefWidth(70);
        } else {
            coupletNumberTextField.setVisible(false);

            searchField.setPrefWidth(102);
        }
    }
    public void searchButtonOnAction(){
        addPlaylistButton.setVisible(false);
        searchButton.setDisable(true);

        Timeline timelineDisableButton = new Timeline(new KeyFrame(Duration.seconds(2), e2 -> {
            searchButton.setDisable(false);
        }));
        timelineDisableButton.setCycleCount(1);
        timelineDisableButton.play();
        
        String searchFor = searchField.getText().toLowerCase();
        String allTextInTextArea = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        String resOfSearching = highlightTextAndReturnResultOfSearching(searchFor);

        searchResultLabel.setText(resOfSearching);
        if(resOfSearching.equals("Знайдено")) {
            searchResultLabel.setTextFill(Color.GREEN);
//            Timeline timelineSetTextBeforeSearching = new Timeline(
//                new KeyFrame(Duration.seconds(2), event -> {
//                    lyricsTextArea.getChildren().clear();
//                    lyricsTextArea.getChildren().add(new Text(allTextInTextArea));
//                })
//            );
//            timelineSetTextBeforeSearching.setCycleCount(1);
//            timelineSetTextBeforeSearching.play();
        }
        else
            searchResultLabel.setTextFill(Color.RED);

        searchResultLabel.setVisible(true);
        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    lyricsTextArea.getChildren().add(new Text(allTextInTextArea));
                    searchResultLabel.setVisible(false);
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
            return "Некоректний номер куплету";
        }catch (IllegalArgumentException e){
            return "Введіть в пошук стрічку";
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
    private String getTextToSearchIn(){
        if(searchTypeComboBox.getSelectionModel().isSelected(0)){
            return ((Text)lyricsTextArea.getChildren().get(0)).getText();
        } else {
            int numberOfCouplet = Integer.parseInt(coupletNumberTextField.getText()) - 1;
            Song actualSong = songs.get(currentSongIndex);
            Song.Couplet couplet = actualSong.getCouplets()[numberOfCouplet];
            return couplet.getCoupletString();
        }
    }

    // other functionality
    public void lastRowsButtonOnAction() {
        Song actualSong = songs.get(currentSongIndex);

        Song.Couplet[] couplets = actualSong.getCouplets();
        StringBuilder res = new StringBuilder();
        for (Song.Couplet value : couplets) {
            String[] lines = value.getCoupletString().split("\n");
            String lastLine = lines[lines.length - 1];
            res.append(lastLine).append("\n");
        }
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(new String(res));
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();
    }
    public void shuffleButtonOnAction(){
        Song song = songs.get(currentSongIndex);

        List<String> list = Arrays.stream(song.getCouplets()).map(Song.Couplet::getCoupletString).collect(Collectors.toList());
        Collections.shuffle(list);
        for(int i = 0; i < song.getCouplets().length; i++){
            song.getCouplets()[i].setCoupletString(list.get(i));
        }
        String initialSongText = utils.getTextForSong(song);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(initialSongText);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();
    }
    public void sortWordsButtonOnAction(){
        Song song = songs.get(currentSongIndex);

        List<String> listOfWords = new ArrayList<>();
        for (Song.Couplet couplet : song.getCouplets()){
            for(String word : couplet.getCoupletString().split("[,\\s]+")){
                if(word.toLowerCase().charAt(0) == 'а' || word.toLowerCase().charAt(0) == 'о' ||
                        word.toLowerCase().charAt(0) == 'е' || word.toLowerCase().charAt(0) == 'у' ||
                        word.toLowerCase().charAt(0) == 'і' || word.toLowerCase().charAt(0) == 'и')
                    listOfWords.add(word.toLowerCase());
            }
        }
        listOfWords.sort(Comparator.naturalOrder());
        listOfWords = listOfWords.stream()
                .map(s -> s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase())
                .collect(Collectors.toList());

        String res = String.join("\n", listOfWords);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(res);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();
    }

    public void changeRowButtonOnAction(){
        otherFunctionalityResultLabel.setVisible(false);
        newRowToBeInserted.setVisible(true);
        numberOfCoupletToBeReplaced.setVisible(true);
        numberOfRowToBeReplaced.setVisible(true);
        changeRowOKButton.setVisible(true);

        //to avoid conflict in otherFuncResultLabel
        sonetButton.setDisable(true);
    }
    public void changeRowButtonOKOnAction() {
        newRowToBeInserted.setVisible(false);
        numberOfCoupletToBeReplaced.setVisible(false);
        numberOfRowToBeReplaced.setVisible(false);
        changeRowOKButton.setVisible(false);

        Song song = songs.get(currentSongIndex);

        String newRow = this.newRowToBeInserted.getText();
        try{
            if(newRow.length() == 0)
                throw new IllegalArgumentException();

            int numberOfCouplet = Integer.parseInt(this.numberOfCoupletToBeReplaced.getText()) - 1;
            int numberOfRow = Integer.parseInt(this.numberOfRowToBeReplaced.getText()) - 1;
            Song.Couplet couplet = song.getCouplets()[numberOfCouplet];
            String[] lines = couplet.getCoupletString().split("\n");
            lines[numberOfRow] = newRow;
            String newCouplet = String.join("\n", lines);
            couplet.setCoupletString(newCouplet);

            //display changes
            String newSongText = utils.getTextForSong(song);

            lyricsTextArea.getChildren().clear();
            Text text = new Text(newSongText);
            lyricsTextArea.getChildren().add(text);

            otherFunctionalityResultLabel.setText("Змінено успішно");

        }catch (NullPointerException e){
            otherFunctionalityResultLabel.setText("Сталась помилка. Виберіть пісню і спробуйте ще раз");
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            otherFunctionalityResultLabel.setText("Некоректний номер куплету або рядка");
        }catch (IllegalArgumentException e){
            otherFunctionalityResultLabel.setText("Введіть стрічку");
        }finally {

            if(otherFunctionalityResultLabel.getText().equals("Змінено успішно")){
                otherFunctionalityResultLabel.setFill(Color.GREEN);
            }
            else
                otherFunctionalityResultLabel.setFill(Color.RED);

            otherFunctionalityResultLabel.setVisible(true);
            sonetButton.setDisable(false);
        }
    }

    public void isSonnetButtonOnAction(){
        Song song = songs.get(currentSongIndex);

        int countOfRows = 0;
        String[] lines;
        for(Song.Couplet couplet : song.getCouplets()){
            lines = couplet.getCoupletString().split("\n");
            countOfRows += lines.length;
        }
        if (countOfRows == 14) {
            otherFunctionalityResultLabel.setText("Це сонет типу Shakespeare");
            otherFunctionalityResultLabel.setFill(Color.GREEN);

        } else if (countOfRows == 12) {
            otherFunctionalityResultLabel.setText("Це сонет типу Spencerian");
            otherFunctionalityResultLabel.setFill(Color.GREEN);

        } else {
            otherFunctionalityResultLabel.setText("Ця пісня не є сонетом");
            otherFunctionalityResultLabel.setFill(Color.RED);
        }
        otherFunctionalityResultLabel.setVisible(true);
    }

    //media player
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
    //TODO: export some code to new util func
    public void changeSongInMP3Player(boolean isNextSong){
        Song song = songs.get(currentSongIndex);

        int index = songs.indexOf(song);
        int newIndex;
        Song newSong;

        if(isNextSong){
            newIndex = index + 1;
            newSong = songs.get(newIndex);
            previousSongButton.setDisable(false);
            if(index + 2 >= songs.size()){
                nextSongButton.setDisable(true);
            }
        } else {
            newIndex = index - 1;
            newSong = songs.get(newIndex);
            nextSongButton.setDisable(false);
            if(index - 2 <= -1){
                previousSongButton.setDisable(true);
            }
        }
        currentSongIndex = newIndex;
        this.currentAuthorLabel.setTextFill(Color.BLACK);
        this.currentSongLabel.setTextFill(Color.BLACK);
        VBox songBox = (VBox) songContainer.getChildren().get(newIndex);
        Label authorLabel = (Label)songBox.getChildren().get(0);
        Label songLabel = (Label)songBox.getChildren().get(1);
        this.currentAuthorLabel = authorLabel;
        this.currentSongLabel = songLabel;

        authorLabel.setTextFill(Color.RED);
        songLabel.setTextFill(Color.RED);


        pauseButton.setVisible(true);
        playButton.setVisible(false);
        mediaPlayer.pause();

        String textForSong = utils.getTextForSong(newSong);
        lyricsTextArea.getChildren().clear();
        Text text = new Text(textForSong);
        lyricsTextArea.getChildren().add(text);

        Media media = new Media(newSong.getMp3());
        mediaPlayer = getNewMediaPlayer(media);
        mediaPlayer.play();
        authorNameLabel.setText(newSong.getAuthorName());
        songNameLabel.setText(newSong.getSongName());
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
            endTimeLabel.setText(formattedTime);
        });
        mediaPlayer.setOnEndOfMedia(this::nextSongButtonOnAction);
        return mediaPlayer;
    }

    // add song
    public void addSongButtonOnAction(){
        scrollPaneWithTextOfSong.setDisable(true);
        scrollPaneWithListOfSongs.setDisable(true);
        addSongPane.setLayoutY(100);
    }


    public void addSongOKButtonOnAction(){
        String authorName = addSongAuthorName.getText();
        String songName = addSongSongName.getText();
        String textOfSong = addSongTextOfSong.getText();
        String mp3PathForMedia = filePath.trim().substring(2);
        mp3PathForMedia = "file:" + mp3PathForMedia;
        mp3PathForMedia = mp3PathForMedia.replace("\\","/");

        Song song = new Song(authorName,songName, new Song.Couplet[]{new Song.Couplet(1, textOfSong)},mp3PathForMedia, playlistNameForNewSong);
        songs.add(song);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

        try {
            objectWriter.writeValue(new File("src/main/resources/com/example/testfx/data.json"), songs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        VBox newSongBox = createSongBlock(song);
        songContainer.getChildren().add(newSongBox);

        addSongSuccessLabel.setVisible(true);

        resetValueInAddSongBox();

    }
    public void addSongCancelButtonOnAction(){
        addSongPane.setLayoutY(600);
        scrollPaneWithListOfSongs.setDisable(false);
        scrollPaneWithTextOfSong.setDisable(false);

        addSongSuccessLabel.setVisible(false);

        resetValueInAddSongBox();
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

    public void chooseFileButtonOnAction() {
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

    public void addPlaylistButtonOnAction(){
        scrollPaneWithTextOfSong.setDisable(true);
        addPlaylistPane.setLayoutY(150);

    }
    public void addPlaylistCancelButtonOnAction(){
        scrollPaneWithTextOfSong.setDisable(false);
        addPlaylistPane.setLayoutY(900);
        addPlaylistName.setText("");
    }
    public void addPlaylistOKButtonOnAction(){
        playlistNamesSet.add(addPlaylistName.getText());
        playlistsComboBox.setItems(FXCollections.observableList(new ArrayList<>(playlistNamesSet)));
        playlistsComboBox.getSelectionModel().select(0);
        playlistsComboBox.setVisibleRowCount(7);

        List<String> newList = new ArrayList<>(playlistNamesSet);
        newList.set(0,"Вибрати плейлист");
        addSongChoosePlaylist.setItems(FXCollections.observableList(newList));
        addSongChoosePlaylist.getSelectionModel().select(0);
        addSongChoosePlaylist.setVisibleRowCount(4);

        scrollPaneWithTextOfSong.setDisable(false);
        addPlaylistPane.setLayoutY(900);
        addPlaylistName.setText("");
    }
    //TODO: selection sort
}