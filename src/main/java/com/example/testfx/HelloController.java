package com.example.testfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    private VBox songContainer;
    private List<Song> songs;
    @FXML
    private ScrollPane scrollPaneWithListOfSongs;
    @FXML
    private ScrollPane scrollPaneWithTextOfSong;
    @FXML
    private ChoiceBox<String> searchTypeChoiceBox;
    @FXML
    private TextField coupletNumberTextField;

    @FXML
    private TextFlow lyricsTextArea;

    @FXML
    private TextField searchField;

    @FXML
    private Text resultLabel;
    public void initialize() {
        //removing blue focus while clicking and horizontal scrollbar
        scrollPaneWithListOfSongs.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneWithListOfSongs.setStyle("-fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;");
        scrollPaneWithTextOfSong.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneWithTextOfSong.setStyle("-fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;");

        // Заповнюємо список пісень songs з джерела даних (JSON, .dat і т. д.)
        // Потім створюємо блоки для кожної пісні та додаємо їх до songContainer.
        Song song1 = new Song("AAA","AAA", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo \nhalo halo Halo\nhalo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo\nhelo helo helo helo\nhelo helo helo helo helo helo helo")});
        Song song2 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song3 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song4 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song5 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song6 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song7 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song8 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song9 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song10 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song11 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});
        Song song12 = new Song("Andrii","Halo", new Song.Couplet[]{
                new Song.Couplet(1,"Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo Halo halo halo"),
                new Song.Couplet(2, "helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo helo")});

        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);
        songList.add(song3);
        songList.add(song4);
        songList.add(song5);
        songList.add(song6);
        songList.add(song7);
        songList.add(song8);
        songList.add(song9);
        songList.add(song10);
        songList.add(song11);
        songList.add(song12);
        setSongs(songList);
        for (Song song : songs) {
            VBox songBlock = createSongBlock(song);
            songContainer.getChildren().add(songBlock);
        }

        //initialize scrollPaneWithTextOfSong with first song
        Song firstSong = songs.get(0);
        StringBuilder res = new StringBuilder(firstSong.songName).append("\n");
        for(int i=0;i<firstSong.couplets.length;i++){
            res.append(firstSong.couplets[i]).append("\n");
        }
        Text text = new Text(new String(res));
        lyricsTextArea.getChildren().add(text);

        //initialize search section and choice box
        searchTypeChoiceBox.setItems(FXCollections.observableArrayList("Пошук у файлі", "Пошук у куплеті"));
        searchTypeChoiceBox.getSelectionModel().select(0);
        searchTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Пошук у куплеті")) {
                coupletNumberTextField.setVisible(true);
            } else {
                coupletNumberTextField.setVisible(false);
            }
        });
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
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

        Label authorLabel = new Label("Author: " + song.getAuthorName());
        Label songLabel = new Label(song.getSongName());
        songVBox.getChildren().addAll(authorLabel, songLabel);
        return songVBox;
    }
    private void songClick(MouseEvent event) {
        VBox vBox = (VBox) event.getSource();
        String songName = ((Label)vBox.getChildren().get(1)).getText();
        String couplet = getCoupletForSong(songName);
        lyricsTextArea.getChildren().clear();
        Text text = new Text(couplet);
        lyricsTextArea.getChildren().add(text);
    }
    private String getCoupletForSong(String songName) {
        for(Song song : songs){
            if(songName.equals(song.songName)){
                return songName + "\n\n       " + song.couplets[0] + "\n          " + song.couplets[0] + "\n";
            }
        }
        return "";
    }

    public void searchText() {
        String searchString = searchField.getText();
        String originalText = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        final String textToSearch;
        try{
            if(searchString.length()==0)
                throw new IllegalArgumentException();

            if(searchTypeChoiceBox.getSelectionModel().isSelected(0)){
                textToSearch = originalText;
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
            }

        }catch (NullPointerException e){
            resultLabel.setText("Сталась помилка. Виберіть пісню і спробуйте ще раз");
            resultLabel.setFill(javafx.scene.paint.Color.RED);
            return;
        }catch (IndexOutOfBoundsException | NumberFormatException e){
            resultLabel.setText("Некоректний номер куплету");
            resultLabel.setFill(javafx.scene.paint.Color.RED);
            return;
        }catch (IllegalArgumentException e){
            resultLabel.setText("Введіть в пошук стрічку");
            resultLabel.setFill(javafx.scene.paint.Color.RED);
            return;
        } catch (Exception e){
            resultLabel.setText("Слово відсутнє");
            resultLabel.setFill(javafx.scene.paint.Color.RED);
            return;
        }

        int fromIndex = 0;
        int index = textToSearch.indexOf(searchString, fromIndex);

        if (index != -1) {
            resultLabel.setText("");
            lyricsTextArea.getChildren().clear();

            while (index != -1) {
                int startIndex = index;
                int endIndex = startIndex + searchString.length();

                // Створюємо частини тексту з різними стилями
                Text textBefore = new Text(textToSearch.substring(fromIndex, startIndex));
                Text highlightedText = new Text(textToSearch.substring(startIndex, endIndex));
                highlightedText.setFill(javafx.scene.paint.Color.RED);

                // Додаємо цю частину тексту до TextFlow
                lyricsTextArea.getChildren().addAll(textBefore, highlightedText);

                // Оновлюємо fromIndex для пошуку наступного входження
                fromIndex = endIndex;

                // Знаходимо наступне входження слова
                index = textToSearch.indexOf(searchString, fromIndex);
            }

            // Додаємо останню частину тексту після останнього входження
            Text textAfter = new Text(textToSearch.substring(fromIndex));
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
            resultLabel.setFill(javafx.scene.paint.Color.RED);
        }
    }

    public void lastRowsClick() {
        String text = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        String songName=text.substring(0,text.indexOf("\n"));
        Song.Couplet[] couplets = null;
        for(Song song : songs){
            if(songName.equals(song.songName)){
                couplets = song.couplets;
            }
        }
        if(couplets == null){
            lyricsTextArea.getChildren().add(new Text());
            return;
        }
        StringBuilder res = new StringBuilder();
        String couplet;
        for(int i =0;i<couplets.length;i++){
            couplet = couplets[i].getS();
            String[] lines = couplet.split("\n");
            String lastLine = lines[lines.length - 1];
            res.append(lastLine).append("\n");
        }
        lyricsTextArea.getChildren().clear();
        Text newText = new Text(new String(res));
        lyricsTextArea.getChildren().add(newText);
    }
}