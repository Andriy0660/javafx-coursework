package com.example.testfx;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
import java.util.stream.IntStream;

public class Controller {
    @FXML
    private VBox songContainer;
    private List<Song> songs = new ArrayList<>();
    @FXML
    private ScrollPane scrollPaneWithListOfSongs;
    @FXML
    private ScrollPane scrollPaneWithTextOfSong;
    @FXML
    private TextFlow lyricsTextArea;
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;
    @FXML
    private TextField coupletNumberTextField;
    @FXML
    private Text resultLabel;
    @FXML
    private Button lastRowsButton;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button sortWordsButton;
    @FXML
    private Pane searchPane;
    @FXML
    private Pane otherFuncPane;
    @FXML
    private Button leftArrowButton;
    @FXML
    private Button rightArrowButton;
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
    private Text resultLabel2;
    private List<Button> buttonList = new ArrayList<>();
    public void isSonet(){
        resultLabel2.setVisible(true);
        Song song = getActualSong();
        int countOfRows = 0;
        String[] lines;
        for(Song.Couplet couplet : song.getCouplets()){
            lines = couplet.getS().split("\n");
            countOfRows+=lines.length;
        }
        if (countOfRows == 14) {
            resultLabel2.setText("Це сонет типу Shakespeare");
            resultLabel2.setFill(javafx.scene.paint.Color.GREEN);

        } else if (countOfRows == 12) {
            resultLabel2.setText("Це сонет типу Spencerian");
            resultLabel2.setFill(javafx.scene.paint.Color.GREEN);

        } else {
            resultLabel2.setText("Ця пісня не є сонетом");
            resultLabel2.setFill(javafx.scene.paint.Color.RED);

        }
    }
    public void changeRowButtonOnClick(){
        resultLabel2.setVisible(false);
        newRow.setVisible(true);
        numberOfCouplet.setVisible(true);
        numberOfRow.setVisible(true);
        changeRowOKButton.setVisible(true);
        disableButtons(sortWordsButton,lastRowsButton,sonetButton);
    }
    public void changeRowButtonOKOnClick() {
        String newRow = this.newRow.getText();
        Song song = getActualSong();
        if(song == null){
            resultLabel2.setText("Сталась помилка. Виберіть пісню і спробуйте ще раз");
            resultLabel2.setFill(Color.RED);
            return;
        }
        try{
            if(newRow.length()==0)
                throw new IllegalArgumentException();

            int numberOfCouplet = Integer.parseInt(this.numberOfCouplet.getText()) - 1;
            int numberOfRow = Integer.parseInt(this.numberOfRow.getText()) - 1;
            Song.Couplet couplet = song.couplets[numberOfCouplet];
            String[] lines = couplet.getS().split("\n");
            lines[numberOfRow] = newRow;
            String newCouplet = String.join("\n", lines);
            couplet.setS(newCouplet);
            System.out.println(song.couplets[numberOfCouplet].getS());

            //display changes
            lyricsTextArea.getChildren().clear();
            String newSongText = getTextForSong(song.songName);
            Text text = new Text(newSongText);
            lyricsTextArea.getChildren().add(text);
            resultLabel2.setText("Змінено успішно");
            resultLabel2.setFill(Color.GREEN);

        }catch (NullPointerException e){
            resultLabel2.setText("Сталась помилка. Виберіть пісню і спробуйте ще раз");
            resultLabel2.setFill(Color.RED);
            return;
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            resultLabel2.setText("Некоректний номер куплету або рядка");
            resultLabel2.setFill(Color.RED);
            return;
        }catch (IllegalArgumentException e){
            resultLabel2.setText("Введіть стрічку");
            resultLabel2.setFill(Color.RED);
            return;
        }finally {
            this.newRow.setVisible(false);
            this.numberOfCouplet.setVisible(false);
            this.numberOfRow.setVisible(false);
            changeRowOKButton.setVisible(false);
            resultLabel2.setVisible(true);
            enableButtons(sortWordsButton,lastRowsButton,sonetButton);
        }
    }
        public void leftArrowOnClick(){
        rightArrowButton.setDisable(false);
        leftArrowButton.setDisable(true);
        searchPane.setLayoutY(0);
        otherFuncPane.setLayoutY(-100);
    }
    public void rightArrowOnClick(){
        resultLabel2.setVisible(false);
        rightArrowButton.setDisable(true);
        leftArrowButton.setDisable(false);
        searchPane.setLayoutY(-100);
        otherFuncPane.setLayoutY(0);

    }
    public void initialize() {
        //initialize list of buttons
        findAllButtons();

        //initialize songs
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            songs = objectMapper.readValue(new File("src/main/resources/com/example/testfx/data.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Song.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Song song : songs) {
            VBox songBlock = createSongBlock(song);
            songContainer.getChildren().add(songBlock);
        }

        //initialize scrollPaneWithTextOfSong with first song
        String initialSongText = getTextForSong(songs.get(0).songName);
        Text text = new Text(initialSongText);
        lyricsTextArea.getChildren().add(text);

        //removing blue focus while clicking and horizontal scrollbar
        scrollPaneWithListOfSongs.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneWithListOfSongs.setStyle("-fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;");
        scrollPaneWithTextOfSong.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneWithTextOfSong.setStyle("-fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;");

        //initialize search section and combo box

        searchTypeComboBox.setItems(FXCollections.observableArrayList("Файл", "Куплет"));
        searchTypeComboBox.getSelectionModel().select(0);
        searchTypeComboBox.setVisibleRowCount(2); // Встановлюємо видиму кількість варіантів вибору

        searchTypeComboBox.setOnAction(event -> {
            String selectedValue = searchTypeComboBox.getValue();
            if (selectedValue.equals("Куплет")) {
                coupletNumberTextField.setVisible(true);
            } else {
                coupletNumberTextField.setVisible(false);
            }
        });

        //initialize button
        searchButton.setOnAction(e1 -> {
            searchButton.setDisable(true); // Робимо кнопку неклікабельною
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e2 -> {
                searchButton.setDisable(false); // Робимо кнопку знову клікабельною після затримки
            }));
            timeline.setCycleCount(1);
            timeline.play();
            searchTextClick();
        });
    }
    private void findAllButtons() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == Button.class) {
                try {
                    field.setAccessible(true);
                    Button button = (Button) field.get(this);
                    if (button != null) {
                        buttonList.add(button);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void disableButtons(Button ... buttons){
        for(Button button : buttons){
            button.setDisable(true);
        }
    }
    private void disableAllButtonsExcept(Button ... buttons){
        OUTER:
        for(Button button : buttonList){
            for(Button buttonExc : buttons){
                if(button.equals(buttonExc))
                    continue OUTER;
            }
            button.setDisable(true);
        }
    }
    private void enableButtons(Button ... buttons){
        for(Button button : buttons){
            button.setDisable(false);
        }
    }
    private void enableAllButtonsExcept(Button ... buttons){
        OUTER:
        for(Button button : buttonList){
            for(Button buttonExc : buttons){
                if(button.equals(buttonExc))
                    continue OUTER;
            }
            button.setDisable(false);
        }
    }

    private VBox createSongBlock(Song song) {
        VBox songVBox = new VBox();
        songVBox.setStyle("-fx-background-color: #f0f0f0;" +
                " -fx-padding: 10;");
        songVBox.setPrefWidth(scrollPaneWithListOfSongs.getPrefWidth());
        songVBox.setMinHeight(50);
        songVBox.setOnMouseClicked(this::songClick);

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
    private void songClick(MouseEvent event) {
        VBox vBox = (VBox) event.getSource();
        String songName = ((Label)vBox.getChildren().get(1)).getText();
        String textForSong = getTextForSong(songName);
        lyricsTextArea.getChildren().clear();
        Text text = new Text(textForSong);
        lyricsTextArea.getChildren().add(text);

        scrollPaneWithTextOfSong.requestFocus();

        enableAllButtonsExcept(leftArrowButton,rightArrowButton);
        resultLabel2.setVisible(false);
    }
    private String getTextForSong(String songName) {
        StringBuilder res = new StringBuilder();
        for(Song song : songs){
            if(songName.equals(song.songName)){
                res.append(songName).append("\n\n\t");
                for(int i=0;i<song.couplets.length;i++) {
                    res.append(song.couplets[i]).append("\n\n\t");
                }
                return new String(res);
            }
        }
        return "Помилка. Виберіть пісню зі списку за спробуйте ще раз";
    }

    public void searchTextClick() {
        String searchString = searchField.getText().toLowerCase();
        String originalText = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        final String textToSearch;
        final String textToSearchLowCase;
        try{
            if(searchString.length()==0)
                throw new IllegalArgumentException();

            if(searchTypeComboBox.getSelectionModel().isSelected(0)){
                textToSearch = originalText;
                textToSearchLowCase = originalText.toLowerCase();
            } else {
                int number = Integer.parseInt(coupletNumberTextField.getText()) - 1;
                String songName=originalText.substring(0,originalText.indexOf("\n"));
                Song.Couplet couplet = null;
                for(Song song : songs){
                    if(songName.equals(song.songName)){
                        couplet = song.couplets[number];
                    }
                }
                textToSearch = couplet.getS();
                textToSearchLowCase = couplet.getS().toLowerCase();
            }

        }catch (NullPointerException e){
            resultLabel.setText("Сталась помилка. Виберіть пісню і спробуйте ще раз");
            resultLabel.setFill(Color.RED);
            return;
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            resultLabel.setText("Некоректний номер куплету");
            resultLabel.setFill(Color.RED);
            return;
        }catch (IllegalArgumentException e){
            resultLabel.setText("Введіть в пошук стрічку");
            resultLabel.setFill(Color.RED);
            return;
        }

        int fromIndex = 0;
        int index = textToSearchLowCase.indexOf(searchString, fromIndex);

        if (index != -1) {
            resultLabel.setText("");
            lyricsTextArea.getChildren().clear();

            while (index != -1) {
                int startIndex = index;
                int endIndex = startIndex + searchString.length();

                // Створюємо частини тексту з різними стилями
                Text textBefore = new Text(textToSearch.substring(fromIndex, startIndex));
                Text highlightedText = new Text(textToSearch.substring(startIndex, endIndex));
                highlightedText.setFill(Color.RED);

                // Додаємо цю частину тексту до TextFlow
                lyricsTextArea.getChildren().addAll(textBefore, highlightedText);

                // Оновлюємо fromIndex для пошуку наступного входження
                fromIndex = endIndex;

                // Знаходимо наступне входження слова
                index = textToSearchLowCase.indexOf(searchString, fromIndex);
            }

            // Додаємо останню частину тексту після останнього входження
            Text textAfter = new Text(textToSearch.substring(fromIndex));
            resultLabel.setText("Знайдено");
            resultLabel.setFill(Color.GREEN);

            lyricsTextArea.getChildren().add(textAfter);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), event -> {
                        lyricsTextArea.getChildren().clear();
                        lyricsTextArea.getChildren().add(new Text(originalText));
                    })
            );
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            resultLabel.setText("Слово відсутнє");
            resultLabel.setFill(Color.RED);
        }
    }

    public void lastRowsClick() {
        Song song = getActualSong();
        if(song == null){
            lyricsTextArea.getChildren().clear();
            lyricsTextArea.getChildren().add(new Text("Сталась помилка. Виберіть пісню і спробуйте ще раз"));
            return;
        }
        Song.Couplet[] couplets = song.couplets;
        StringBuilder res = new StringBuilder();
        for (Song.Couplet value : couplets) {
            String[] lines = value.getS().split("\n");
            String lastLine = lines[lines.length - 1];
            res.append(lastLine).append("\n");
        }
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(new String(res));
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();

        disableAllButtonsExcept(leftArrowButton);

        resultLabel2.setVisible(true);
        resultLabel2.setText("Виберіть пісню щоб продовжити");
        resultLabel2.setFill(Color.ORANGE);
    }
    public void shuffleClick(){
        Song song = getActualSong();
        if(song == null){
            lyricsTextArea.getChildren().clear();
            lyricsTextArea.getChildren().add(new Text("Сталась помилка. Виберіть пісню і спробуйте ще раз"));
            return;
        }

        List<String> list = Arrays.stream(song.couplets).map(Song.Couplet::getS).collect(Collectors.toList());
        Collections.shuffle(list);
        for(int i =0;i< song.couplets.length;i++){
            song.couplets[i].setS(list.get(i));
        }
        String initialSongText = getTextForSong(song.songName);
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(initialSongText);
        lyricsTextArea.getChildren().add(newText);

        scrollPaneWithTextOfSong.requestFocus();

    }
    public void sortWordsClick(){
        Song song = getActualSong();
        if(song == null){
            lyricsTextArea.getChildren().clear();
            lyricsTextArea.getChildren().add(new Text("Сталась помилка. Виберіть пісню і спробуйте ще раз"));
            return;
        }
        List<String> listOfWords = new ArrayList<>();
        for (Song.Couplet couplet : song.getCouplets()){
            for(String word : couplet.getS().split("[,\\s]+")){
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

        disableAllButtonsExcept(leftArrowButton);

        resultLabel2.setVisible(true);
        resultLabel2.setText("Виберіть пісню щоб продовжити");
        resultLabel2.setFill(Color.ORANGE);

    }
    private Song getActualSong(){
        String text = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        String songName=text.substring(0,text.indexOf("\n"));
        for(Song song : songs){
            if(song.songName.equals(songName)){
                return song;
            }
        }
        return null;
    }

    //TODO: selection sort
}