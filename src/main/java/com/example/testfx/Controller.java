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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private final int INVISIBLE = 1000;


    @FXML
    private VBox songContainer;
    @FXML
    private Pane leftRightButtonsPane;
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
    private Button searchSongCancelButton;

    // playlist
    @FXML
    private ComboBox<String> playlistsComboBox;

    @FXML
    private Pane createPlaylistPane;
    @FXML
    private Button createPlaylistButton;
    @FXML
    private Button createPlaylistOKButton;
    @FXML
    private TextField createPlaylistNameOfPlaylist;

    // search word
    @FXML
    private Pane searchWordPane;
    @FXML
    private Button searchWordButton;
    @FXML
    private TextField searchWordTargetField;
    @FXML
    private ComboBox<String> searchWordTypeComboBox;
    @FXML
    private TextField searchWordNumberOfCoupletTextField;

    // other functionality
    @FXML
    private Pane paneWithOtherFunctionality;
    @FXML
    private Pane changeRowPane;
    @FXML
    private Label changeRowResultLabel;
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

    // media player
    private MediaPlayer mediaPlayer;
    @FXML
    private HBox mediaPlayerBox;
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
    private Label addSongSuccessLabel;


    //song settings
    @FXML
    private Pane songSettingsPane;

        //add song to playlist
    @FXML
    private Button addSongToPlaylistButton;
    @FXML
    private Pane addSongToPlaylistPane;
    @FXML
    private Button addSongToPlaylistOKButton;
    @FXML
    private Button addSongToPlaylistCancelButton;
    @FXML
    private ComboBox<String> addSongToPlaylistComboBox;
    @FXML
    private Label addSongToPlaylistErrorLabel;
        //delete song
    @FXML
    private Pane deleteSongPane;
    @FXML
    private Label deleteSongInformLabel;


    private List<Song> likedSongs = new ArrayList<>();

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

        allSongsByPlaylists = allSongs.stream()
                .filter(i -> i.getPlaylistName() != null)
                .collect(Collectors.groupingBy(Song::getPlaylistName));

        playlistNamesObsList = FXCollections.observableArrayList(allSongsByPlaylists.keySet());
        playlistNamesObsList.add(0,"Всі плейлисти");
        playlistNamesObsList.add(1,"Вподобані");


        for (int i = 0; i < allSongs.size(); i++) {
            Song song = allSongs.get(i);
            if(song.isLiked())
                likedSongs.add(song);
            HBox songBlock = createSongBlock(song,i);
            songContainer.getChildren().add(songBlock);
        }
        Song initialSong = allSongs.get(0);

        currentSongBoxIndex = 0;
        HBox initHBox = (HBox)songContainer.getChildren().get(0);
        //settings button
        initHBox.getChildren().get(1).setVisible(true);
        //heart button
        initHBox.getChildren().get(2).setVisible(true);
        currentSongBox = (VBoxCustom) initHBox.getChildren().get(0);

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
            searchSongCancelButton.setDisable(newValue.length() == 0);

            List<Song> foundedSongs = trie.findWordsWithPrefix(newValue.toLowerCase()).stream()
                    .distinct()
                    .toList();

            String currentPlaylist = playlistsComboBox.getValue();
            songContainer.getChildren().clear();
            List<Song> listOfSongsInCurrentPlaylist;
            if(currentPlaylist.equals("Вподобані")){
                listOfSongsInCurrentPlaylist = likedSongs;
            }
            else if(!currentPlaylist.equals("Всі плейлисти")){
                listOfSongsInCurrentPlaylist = allSongsByPlaylists.get(currentPlaylist);
            } else
                listOfSongsInCurrentPlaylist = allSongs;

            foundedSongs = foundedSongs.stream()
                    .filter(listOfSongsInCurrentPlaylist::contains)
                    .toList();

            for (int i = 0; i < foundedSongs.size(); i++) {
                Song song = foundedSongs.get(i);
                HBox songBlock = createSongBlock(song,i);
                songContainer.getChildren().add(songBlock);
            }
        });


        //initialize search word
        searchWordTypeComboBox.setItems(FXCollections.observableArrayList("Пісня", "Куплет"));
        searchWordTypeComboBox.getSelectionModel().select(0);
        searchWordTypeComboBox.setVisibleRowCount(2);
        searchWordTargetField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchWordButton.setDisable(newValue.length() == 0);
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

        //initialize index text fields
        utils.setSingleDigitIntegerListenerForTextField(searchWordNumberOfCoupletTextField);
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
        BooleanBinding isAddSongButtonActive = Bindings.createBooleanBinding(() ->

                                !addSongAuthorName.getText().trim().isEmpty() &&
                                !addSongSongName.getText().trim().isEmpty() &&
                                !addSongTextOfSong.getText().trim().isEmpty() &&
                                addSongChooseSongLabel.getText().equals("Вибрано"),

                addSongAuthorName.textProperty(), addSongSongName.textProperty(),
                addSongTextOfSong.textProperty(),addSongChooseSongLabel.textProperty());

        addSongOKButton.disableProperty().bind(isAddSongButtonActive.not());

        //initialize playlist
        createPlaylistNameOfPlaylist.textProperty().addListener((observable, oldValue, newValue) -> {
            createPlaylistOKButton.setDisable(newValue.isEmpty());
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
            } else if(value.equals("Вподобані")){
                newListOfSongs = likedSongs;
            }else {
                newListOfSongs = allSongsByPlaylists.get(value);
            }

            for (int i = 0; i < newListOfSongs.size(); i++) {
                Song song = newListOfSongs.get(i);
                HBox songBlock = createSongBlock(song,i);
                songContainer.getChildren().add(songBlock);
            }
        });

        //initialize settings pane
        addSongToPlaylistComboBox.setItems(playlistNamesObsList);
        addSongToPlaylistComboBox.getSelectionModel().select(0);
        int sizeOfComboBox = Math.min(4, playlistNamesObsList.size());
        addSongToPlaylistComboBox.setVisibleRowCount(sizeOfComboBox);

        addSongToPlaylistComboBox.setOnShowing(e -> {
            addSongToPlaylistPane.setPrefHeight(addSongToPlaylistPane.getHeight()+sizeOfComboBox*25);
            addSongToPlaylistCancelButton.setLayoutY(addSongToPlaylistCancelButton.getLayoutY()+sizeOfComboBox*25);
            addSongToPlaylistOKButton.setLayoutY(addSongToPlaylistOKButton.getLayoutY()+sizeOfComboBox*25);
        });
        addSongToPlaylistComboBox.setOnHiding(e -> {
            addSongToPlaylistPane.setPrefHeight(addSongToPlaylistPane.getHeight()-sizeOfComboBox*25);
            addSongToPlaylistCancelButton.setLayoutY(addSongToPlaylistCancelButton.getLayoutY()-sizeOfComboBox*25);
            addSongToPlaylistOKButton.setLayoutY(addSongToPlaylistOKButton.getLayoutY()-sizeOfComboBox*25);
        });

        addSongToPlaylistComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = addSongToPlaylistComboBox.getValue();
            if(value == null) return;
            addSongToPlaylistOKButton.setDisable(value.equals("Всі плейлисти"));
        });

    }

    private HBox createSongBlock(Song song, int id) {
        HBox currentHBox = new HBox();
        VBoxCustom songBox = new VBoxCustom(id,song);

        songBox.setStyle("-fx-background-color: #f0f0f0;" +
                " -fx-padding: 10;");
        songBox.setPrefWidth(songContainer.getPrefWidth());
        songBox.setMinHeight(50);

        currentHBox.setOnMouseEntered(e -> {
            songBox.setStyle("-fx-background-color: #888fa6; -fx-padding: 10");
            currentHBox.setStyle("-fx-background-color: #888fa6; ");
        });

        currentHBox.setOnMouseExited(e -> {
            songBox.setStyle("-fx-background-color: transparent; -fx-padding: 10");
            currentHBox.setStyle("-fx-background-color: transparent;");
        });

        Label authorLabel = new Label(song.getAuthorName());
        Label songLabel = new Label(song.getSongName());
        songBox.getChildren().addAll(authorLabel, songLabel);

        Button songSettingsButton = new Button("⋮");
        songSettingsButton.setStyle("-fx-font-size:30;");
        songSettingsButton.setPadding(new Insets(-10,0,-10,0));
        HBox.setMargin(songSettingsButton,new Insets(13,10,0,0));
        songSettingsButton.setVisible(false);
        songSettingsButton.setOnMouseClicked(e -> {
            disableAllBackElements(true);
            if (songSettingsPane.getLayoutY()!=90){
                songSettingsPane.setLayoutY(90);
            }
            else {
                songSettingsPane.setLayoutY(INVISIBLE);
            }
        });

        Song currentSong = songBox.getSong();
        Button heartButton;
        if(currentSong.isLiked())
            heartButton = new Button("♥");
        else
            heartButton = new Button("♡");
        heartButton.setVisible(false);
        heartButton.setStyle("-fx-font-size: 25");
        heartButton.setPadding(new Insets(-6,-6,-5,-3));
        heartButton.setMinSize(25, 25);
        heartButton.setMaxSize(25, 25);
        HBox.setMargin(heartButton,new Insets(13,10,0,0));
        heartButton.setOnAction(e ->{
            if(heartButton.getText().equals("♡")) {
                heartButton.setText("♥");
                currentSong.setLiked(true);
                likedSongs.add(currentSong);
            } else {
                heartButton.setText("♡");
               currentSong.setLiked(false);
                likedSongs.remove(currentSong);
            }
        });

        currentHBox.setOnMouseClicked( e -> {
            try {
                HBox oldCurHBox = (HBox) songContainer.getChildren().get(currentSongBoxIndex);
                oldCurHBox.getChildren().get(1).setVisible(false);
                oldCurHBox.getChildren().get(2).setVisible(false);
            } catch (Exception ignored) {
            }
            changingVisibilityOfSongSettingsButton(currentHBox);

            songBoxOnClick(currentHBox);
        });
        currentHBox.getChildren().addAll(songBox,songSettingsButton,heartButton);
        return currentHBox;
    }
    private void songBoxOnClick(HBox hbox){
        VBoxCustom songBox = (VBoxCustom) hbox.getChildren().get(0);
        Song song = songBox.getSong();
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
    }

    public void leftArrowButtonOnAction(){
        rightArrowButton.setDisable(false);
        leftArrowButton.setDisable(true);
        paneWithOtherFunctionality.setLayoutY(-100);
        searchWordPane.setLayoutY(0);
        searchWordPane.setLayoutX(264);
    }
    public void rightArrowButtonOnAction(){
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
            searchWordNumberOfCoupletTextField.setVisible(true);
            searchWordTargetField.setPrefWidth(70);
        } else {
            searchWordNumberOfCoupletTextField.setVisible(false);
            searchWordTargetField.setPrefWidth(102);
        }
    }
    public void searchWordButtonOnAction(){
        searchWordButton.setDisable(true);

        Song currentSong = currentSongBox.getSong();

        Timeline timelineDisableButton = new Timeline(new KeyFrame(Duration.seconds(2), e2 -> {
            searchWordButton.setDisable(false);
        }));
        timelineDisableButton.setCycleCount(1);
        timelineDisableButton.play();
        
        String searchFor = searchWordTargetField.getText().toLowerCase();
        String resOfSearching = highlightTextAndReturnResultOfSearching(searchFor);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Andrii`s Song App");;
        alert.getDialogPane().setHeaderText("Результат пошуку у пісні");
        alert.getDialogPane().setContent(new Label(resOfSearching));
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("myDialog");

        if(resOfSearching.equals("Знайдено"))
            alert.getDialogPane().getContent().getStyleClass().add("green");

        else
            alert.getDialogPane().getContent().getStyleClass().add("red");

        alert.showAndWait();

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    //removing red text
                    lyricsTextArea.getChildren().add(new Text(utils.getTextForSong(currentSong)));
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
            int numberOfCouplet = Integer.parseInt(searchWordNumberOfCoupletTextField.getText()) - 1;
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
        changeRowPane.setLayoutY(INVISIBLE);
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

            changeRowResultLabel.setText("Змінено успішно");
            changeRowResultLabel.setTextFill(Color.GREEN);
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            changeRowResultLabel.setText("Некоректний індекс");
            changeRowResultLabel.setTextFill(Color.RED);
        }

        changeRowPane.setPrefHeight(107);
        sonnetButton.setDisable(false);
        numberOfCoupletToBeReplaced.setText("");
        numberOfRowToBeReplaced.setText("");
        newRowToBeInserted.setText("");

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    changeRowResultLabel.setText("");
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
        previousSongButton.setDisable(false);
        nextSongButton.setDisable(false);
        int newIndex;
        Song song;
        if(isNextSong){
            newIndex = currentSongBoxIndex + 1;
        } else {
            newIndex = currentSongBoxIndex - 1;
        }
        if(newIndex == 0){
            previousSongButton.setDisable(true);
        }

        if(newIndex + 1 == songContainer.getChildren().size()){
            nextSongButton.setDisable(true);
        }

        try {
            HBox oldCurHBox = (HBox) songContainer.getChildren().get(currentSongBoxIndex);
            oldCurHBox.getChildren().get(1).setVisible(false);
            oldCurHBox.getChildren().get(2).setVisible(false);
        } catch (Exception ignored) {
        }
        currentSongBoxIndex = newIndex;
        HBox currentHBox = (HBox)songContainer.getChildren().get(currentSongBoxIndex);
        changingVisibilityOfSongSettingsButton(currentHBox);

        VBoxCustom newCurrentSongBox = (VBoxCustom) currentHBox.getChildren().get(0);

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
                new Song.Couplet(1, textOfSong)}, mp3PathForMedia, null);

        //adding song to all data structures
        allSongs.add(newSong);
        trie.insertSong(newSong);

        //updating songs
        songContainer.getChildren().clear();
        if(playlistsComboBox.getValue().equals("Всі плейлисти")){
            for (int i = 0; i < allSongs.size(); i++) {
                Song song = allSongs.get(i);
                HBox songBlock = createSongBlock(song,i);
                songContainer.getChildren().add(songBlock);
            }
        }

        //writing changes
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        try {
            objectWriter.writeValue(new File("src/main/resources/com/example/testfx/data.json"), allSongs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addSongChooseSongLabel.setVisible(false);
        addSongSuccessLabel.setVisible(true);

        Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    addSongChooseSongLabel.setVisible(true);
                    addSongSuccessLabel.setVisible(false);
                })
        );
        timelineSetTextBeforeSearching.setCycleCount(1);
        timelineSetTextBeforeSearching.play();

        resetValueInAddSongBox();
    }
    public void addSongCancelButtonOnAction(){
        addSongPane.setLayoutY(INVISIBLE);
        addSongSuccessLabel.setVisible(false);
        resetValueInAddSongBox();
        disableAllBackElements(false);
    }
    private void resetValueInAddSongBox() {
        addSongChooseSongLabel.setText("Виберіть пісню");
        addSongChooseSongLabel.setTextFill(Color.RED);
        addSongTextOfSong.setText("");
        addSongAuthorName.setText("");
        addSongSongName.setText("");
    }

    public void addSongChooseFileButtonOnAction() {
        addSongSuccessLabel.setVisible(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
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
        createPlaylistPane.setLayoutY(150);
        disableAllBackElements(true);
    }
    public void addPlaylistCancelButtonOnAction(){
        createPlaylistPane.setLayoutY(INVISIBLE);
        createPlaylistNameOfPlaylist.setText("");
        disableAllBackElements(false);
    }
    public void addPlaylistOKButtonOnAction(){
        String playlistName = createPlaylistNameOfPlaylist.getText();
        if(!playlistNamesObsList.contains(playlistName)){
            playlistNamesObsList.add(playlistName);
            allSongsByPlaylists.put(playlistName, new ArrayList<>());
        }
        createPlaylistPane.setLayoutY(INVISIBLE);
        createPlaylistNameOfPlaylist.setText("");
        disableAllBackElements(false);
    }
    private void disableAllBackElements(boolean isDisabled){
        scrollPaneWithTextOfSong.setDisable(isDisabled);
        scrollPaneWithListOfSongs.setDisable(isDisabled);
        leftRightButtonsPane.setDisable(isDisabled);
        searchWordPane.setDisable(isDisabled);
        paneWithOtherFunctionality.setDisable(isDisabled);
        searchSongBox.setDisable(isDisabled);
        mediaPlayerBox.setDisable(isDisabled);
    }

    // settings pane
    public void settingsCancelButtonOnAction(){
        disableAllBackElements(false);
        songSettingsPane.setLayoutY(INVISIBLE);
    }
                     // add song to playlist
    public void addSongToPlaylistButtonOnAction(){
        addSongToPlaylistPane.setLayoutY(150);
        songSettingsPane.setVisible(false);
        disableAllBackElements(true);
    }
    public void addSongToPlaylistCancelButtonOnAction(){
        addSongToPlaylistPane.setLayoutY(INVISIBLE);
        songSettingsPane.setVisible(true);
    }
    public void addSongToPlaylistOKButtonOnAction(){
        String playlistName = addSongToPlaylistComboBox.getValue();
        Song currentSong = currentSongBox.getSong();
        if(currentSong.getPlaylistName()!=null){
            addSongToPlaylistErrorLabel.setText("Ця пісня вже знаходиться в плейлисті " + currentSong.getPlaylistName());
            addSongToPlaylistPane.setPrefHeight(addSongToPlaylistPane.getHeight()+30);
            addSongToPlaylistOKButton.setDisable(true);
            Timeline timelineDisableButton = new Timeline(new KeyFrame(Duration.seconds(2), e2 -> {
                addSongToPlaylistErrorLabel.setText("");
                addSongToPlaylistPane.setPrefHeight(addSongToPlaylistPane.getHeight()-30);
                addSongToPlaylistComboBox.getSelectionModel().select(0);
            }));
            timelineDisableButton.setCycleCount(1);
            timelineDisableButton.play();
        } else{
            allSongsByPlaylists.get(playlistName).add(currentSong);
            addSongToPlaylistPane.setLayoutY(INVISIBLE);
            songSettingsPane.setLayoutY(INVISIBLE);
            songSettingsPane.setVisible(true);
            disableAllBackElements(false);
            currentSong.setPlaylistName(playlistName);

            //writing changes
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
            try {
                objectWriter.writeValue(new File("src/main/resources/com/example/testfx/data.json"), allSongs);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


                    // delete song
    public void deleteSongButtonOnAction(){
        String currentPlayListName = playlistsComboBox.getValue();
        if(currentPlayListName.equals("Всі плейлисти")){
            deleteSongInformLabel.setText("Ви впевнені, що хочете повністю видалити поточну пісню?");
        } else {
            deleteSongInformLabel.setText("Ви впевнені, що хочете видалити поточну пісню з плейлисту " +
                    currentPlayListName + "?" );
        }
        disableAllBackElements(true);
        songSettingsPane.setVisible(false);
        deleteSongPane.setLayoutY(150);
    }
    public void deleteSongCancelButtonOnAction(){
        songSettingsPane.setVisible(true);
        deleteSongPane.setLayoutY(INVISIBLE);
    }
    public void deleteSongOKButtonOnAction(){
        disableAllBackElements(false);
        songSettingsPane.setVisible(true);
        songSettingsPane.setLayoutY(INVISIBLE);
        deleteSongPane.setLayoutY(INVISIBLE);

        String currentPlayListName = playlistsComboBox.getValue();
        Song songToBeDeleted = currentSongBox.getSong();

        List<Song> newListOfSongs;
        songContainer.getChildren().clear();
        if(!currentPlayListName.equals("Всі плейлисти")) {
            allSongsByPlaylists.get(songToBeDeleted.getPlaylistName()).remove(songToBeDeleted);
            newListOfSongs = allSongsByPlaylists.get(songToBeDeleted.getPlaylistName());
            Song songWithoutPlayList = new Song(songToBeDeleted.getAuthorName(),
                    songToBeDeleted.getSongName(),
                    songToBeDeleted.getCouplets(),
                    songToBeDeleted.getMp3(),
                    null);

            allSongs.set(allSongs.indexOf(songToBeDeleted), songWithoutPlayList);
        }
        else {
            allSongs.remove(songToBeDeleted);
            newListOfSongs = allSongs;
            if(allSongsByPlaylists.get(songToBeDeleted.getPlaylistName())!=null){
                allSongsByPlaylists.get(songToBeDeleted.getPlaylistName())
                        .remove(songToBeDeleted);
            }
        }
        for (int i = 0; i < newListOfSongs.size(); i++) {
            Song song = newListOfSongs.get(i);
            HBox songBlock = createSongBlock(song,i);
            songContainer.getChildren().add(songBlock);
        }

        //writing changes
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        try {
            objectWriter.writeValue(new File("src/main/resources/com/example/testfx/data.json"), allSongs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            HBox oldCurHBox = (HBox) songContainer.getChildren().get(currentSongBoxIndex);
            oldCurHBox.getChildren().get(1).setVisible(false);
        } catch (Exception ignored) {
        }

        if(songContainer.getChildren().size() == 0) {
            playlistsComboBox.getSelectionModel().select(0);
            currentSongBoxIndex = 0;
        }
        else if(songContainer.getChildren().size() == 1){
            currentSongBoxIndex = 0;
        }
        else if(currentSongBoxIndex == songContainer.getChildren().size()) {
            currentSongBoxIndex--;
        }

        HBox currentHBox = (HBox)songContainer.getChildren().get(currentSongBoxIndex);
        changingVisibilityOfSongSettingsButton(currentHBox);
        songBoxOnClick(currentHBox);
    }

    private void changingVisibilityOfSongSettingsButton(HBox currentHBox) {
        if(playlistsComboBox.getValue().equals("Вподобані")){
            currentHBox.getChildren().get(1).setVisible(false);
            currentHBox.getChildren().get(2).setVisible(true);
            return;
        }
        addSongToPlaylistButton.setVisible(false);
        if(playlistsComboBox.getValue().equals("Всі плейлисти")) {
            addSongToPlaylistButton.setVisible(true);
            currentHBox.getChildren().get(1).setVisible(true);
            currentHBox.getChildren().get(2).setVisible(true);
            songSettingsPane.setPrefHeight(112);

        } else {
            currentHBox.getChildren().get(1).setVisible(true);
            currentHBox.getChildren().get(2).setVisible(true);
            addSongToPlaylistButton.setVisible(false);
            songSettingsPane.setPrefHeight(78);
        }
    }
}