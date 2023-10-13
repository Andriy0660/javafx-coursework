package com.example.testfx;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    private Utils utils;

    @FXML
    private VBox songContainer;
    private List<Song> songs = new ArrayList<>();

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

    @FXML
    private Pane searchPane;
    @FXML
    private Button searchButton;
    @FXML
    private Text searchResultLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;
    @FXML
    private TextField coupletNumberTextField;

    @FXML
    private Pane paneWithOtherFunctionality;
    @FXML
    private Button lastRowsButton;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button sortWordsButton;
    @FXML
    private Button changeRowButton;
    @FXML
    private Button changeRowOKButton;
    @FXML
    private TextField numberOfCouplet;
    @FXML
    private TextField numberOfRow;
    @FXML
    private TextField newRow;
    @FXML
    private Button sonetButton;
    @FXML
    private Text otherFunctionalityResultLabel;

    public void initialize() {
        //initialize songs
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            songs = objectMapper.readValue(new File("src/main/resources/com/example/testfx/data.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Song.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
        //initialize utils
        utils = new Utils(getAllButtons(), songs);

        for (Song song : songs) {
            VBox songBlock = createSongBlock(song);
            songContainer.getChildren().add(songBlock);
        }

        //initialize scrollPaneWithTextOfSong with first song
        String initialSongText = utils.getTextForSong(songs.get(0));
        Text text = new Text(initialSongText);
        lyricsTextArea.getChildren().add(text);

        //initialize search section and combo box
        searchTypeComboBox.setItems(FXCollections.observableArrayList("Пісня", "Куплет"));
        searchTypeComboBox.getSelectionModel().select(0);
        searchTypeComboBox.setVisibleRowCount(2);


        //initialize index text fields
        utils.setSingleDigitIntegerListenerForTextField(coupletNumberTextField);
        utils.setSingleDigitIntegerListenerForTextField(numberOfCouplet);
        utils.setSingleDigitIntegerListenerForTextField(numberOfRow);
    }

    private List<Button> getAllButtons() {
        List<Button> allButtonsList = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == Button.class) {
                try {
                    field.setAccessible(true);
                    Button button = (Button) field.get(this);
                    if (button != null) {
                        allButtonsList.add(button);
                    }
                } catch (IllegalAccessException ignored) {
                    System.out.println("IllegalAccess to one of the buttons");
                }
            }
        }
        return allButtonsList;
    }
    private VBox createSongBlock(Song song) {
        VBox songVBox = new VBox();
        songVBox.setStyle("-fx-background-color: #f0f0f0;" +
                " -fx-padding: 10;");
        songVBox.setPrefWidth(songContainer.getPrefWidth());
        songVBox.setMinHeight(50);
        songVBox.setOnMouseClicked(this::chooseSongOnAction);

        songVBox.setOnMouseEntered(e -> {
            songVBox.setStyle("-fx-background-color: #888fa6; -fx-padding: 10");
        });

        songVBox.setOnMouseExited(e -> {
            songVBox.setStyle("-fx-background-color: transparent;-fx-padding: 10");
        });

        Label authorLabel = new Label(song.getAuthorName());
        Label songLabel = new Label(song.getSongName());
        songVBox.getChildren().addAll(authorLabel, songLabel);
        return songVBox;
    }
    private void chooseSongOnAction(MouseEvent event) {
        VBox vBox = (VBox) event.getSource();
        String songName = ((Label)vBox.getChildren().get(1)).getText();
        Song newSong = utils.getActualSongByName(songName);
        String textForSong = utils.getTextForSong(newSong);

        lyricsTextArea.getChildren().clear();
        Text text = new Text(textForSong);
        lyricsTextArea.getChildren().add(text);

        scrollPaneWithTextOfSong.requestFocus();

    //    Song song = getActualSong();
//        Media media = new Media(song.mp3);
//        MediaPlayer mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setAutoPlay(true);
//        mediaPlayer.play();
        utils.enableAllButtonsExcept(leftArrowButton,rightArrowButton);
        otherFunctionalityResultLabel.setVisible(false);
    }
    public void leftArrowOnAction(){
        otherFunctionalityResultLabel.setVisible(false);
        rightArrowButton.setDisable(false);
        leftArrowButton.setDisable(true);
        searchPane.setLayoutY(0);
        paneWithOtherFunctionality.setLayoutY(-100);
    }
    public void rightArrowOnAction(){
        searchResultLabel.setVisible(false);
        rightArrowButton.setDisable(true);
        leftArrowButton.setDisable(false);
        searchPane.setLayoutY(-100);
        paneWithOtherFunctionality.setLayoutY(0);
        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) { // Перевірка, чи є тільки цифри
                if (newValue.length() > 1) {
                    coupletNumberTextField.setText(oldValue);
                }
            } else {
                coupletNumberTextField.setText(oldValue);
            }
        };
        numberOfCouplet.textProperty().addListener(listener);
        numberOfRow.textProperty().addListener(listener);
    }

    public void searchTypeComboBoxOnAction(){
        String selectedValue = searchTypeComboBox.getValue();
        coupletNumberTextField.setVisible(selectedValue.equals("Куплет"));
    }
    public void searchButtonOnAction(){
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
            searchResultLabel.setFill(Color.GREEN);
            Timeline timelineSetTextBeforeSearching = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    lyricsTextArea.getChildren().clear();
                    lyricsTextArea.getChildren().add(new Text(allTextInTextArea));
                })
            );
            timelineSetTextBeforeSearching.setCycleCount(1);
            timelineSetTextBeforeSearching.play();
        }
        else
            searchResultLabel.setFill(Color.RED);

        searchResultLabel.setVisible(true);
    }

    private String highlightTextAndReturnResultOfSearching(String searchFor) {
        String textToSearchIn;
        String textToSearchInLowCase;
        try{
            if(searchFor.length()==0)
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
            Song actualSong = utils.getActualSongInTextArea(lyricsTextArea);
            Song.Couplet couplet = actualSong.couplets[numberOfCouplet];
            return couplet.getCoupletString();
        }
    }

    public void lastRowsButtonOnAction() {
        Song song = utils.getActualSongInTextArea(lyricsTextArea);

        Song.Couplet[] couplets = song.couplets;
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

        utils.disableAllButtonsExcept(leftArrowButton);

        otherFunctionalityResultLabel.setText("Виберіть пісню щоб продовжити");
        otherFunctionalityResultLabel.setFill(Color.ORANGE);
        otherFunctionalityResultLabel.setVisible(true);
    }
    public void shuffleButtonOnAction(){
        Song song = utils.getActualSongInTextArea(lyricsTextArea);

        List<String> list = Arrays.stream(song.couplets).map(Song.Couplet::getCoupletString).collect(Collectors.toList());
        Collections.shuffle(list);
        for(int i =0;i< song.couplets.length;i++){
            song.couplets[i].setCoupletString(list.get(i));
        }
        String initialSongText = utils.getTextForSong(song);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(initialSongText);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();
    }
    public void sortWordsButtonOnAction(){
        Song song = utils.getActualSongInTextArea(lyricsTextArea);

        List<String> listOfWords = new ArrayList<>();
        for (Song.Couplet couplet : song.getCouplets()){
            for(String word : couplet.getCoupletString().split("[,\\s]+")){
                if(word.toLowerCase().charAt(0) == 'a' || word.toLowerCase().charAt(0) == 'o' ||
                        word.toLowerCase().charAt(0) == 'e' || word.toLowerCase().charAt(0) == 'y' ||
                        word.toLowerCase().charAt(0) == 'i' || word.toLowerCase().charAt(0) == 'u')
                    listOfWords.add(word);
            }
        }
        listOfWords.sort(Comparator.naturalOrder());
        String res = String.join(" ", listOfWords);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(res);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();

        utils.disableAllButtonsExcept(leftArrowButton);

        otherFunctionalityResultLabel.setText("Виберіть пісню щоб продовжити");
        otherFunctionalityResultLabel.setFill(Color.ORANGE);
        otherFunctionalityResultLabel.setVisible(true);
    }

    public void changeRowButtonOnAction(){
        otherFunctionalityResultLabel.setVisible(false);
        newRow.setVisible(true);
        numberOfCouplet.setVisible(true);
        numberOfRow.setVisible(true);
        changeRowOKButton.setVisible(true);
        utils.disableButtons(sortWordsButton,lastRowsButton,sonetButton);
    }
    public void changeRowButtonOKOnAction() {
        this.newRow.setVisible(false);
        this.numberOfCouplet.setVisible(false);
        this.numberOfRow.setVisible(false);
        changeRowOKButton.setVisible(false);

        Song song = utils.getActualSongInTextArea(lyricsTextArea);
        String newRow = this.newRow.getText();
        try{
            if(newRow.length()==0)
                throw new IllegalArgumentException();

            int numberOfCouplet = Integer.parseInt(this.numberOfCouplet.getText()) - 1;
            int numberOfRow = Integer.parseInt(this.numberOfRow.getText()) - 1;
            Song.Couplet couplet = song.couplets[numberOfCouplet];
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
            utils.enableButtons(sortWordsButton,lastRowsButton,sonetButton);
        }
    }

    public void isSonetButtonOnAction(){
        Song song = utils.getActualSongInTextArea(lyricsTextArea);
        int countOfRows = 0;
        String[] lines;
        for(Song.Couplet couplet : song.getCouplets()){
            lines = couplet.getCoupletString().split("\n");
            countOfRows+=lines.length;
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

    //TODO: selection sort
}