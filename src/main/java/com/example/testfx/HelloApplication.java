package com.example.testfx;//package com.example.testfx;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class HelloApplication extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//}
//import javafx.application.Application;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.Scene;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//
//public class HelloApplication extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        // Create TableView for displaying songs
//        TableView<Song> songTableView = new TableView<>();
//
//        TableColumn<Song, Integer> numberColumn = new TableColumn<>("Номер");
//        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
//
//        TableColumn<Song, String> titleColumn = new TableColumn<>("Назва");
//        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
//
//        TableColumn<Song, String> authorColumn = new TableColumn<>("Автор");
//        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
//
//        TableColumn<Song, Integer> yearColumn = new TableColumn<>("Рік");
//        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
//
//        songTableView.getColumns().addAll(numberColumn, titleColumn, authorColumn, yearColumn);
//
//        // Create ScrollPane to add a scrollbar to the table
//        ScrollPane scrollPane = new ScrollPane(songTableView);
//
//        // Create a scene and add the ScrollPane to it
//        Scene scene = new Scene(scrollPane, 600, 400);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Список пісень");
//        primaryStage.show();
//
//        // Read songs from a file or other data source and add them to the TableView
//        ObservableList<Song> songs = FXCollections.observableArrayList(
//                new Song(1, "Пісня 1", "Автор 1", 2000),
//                new Song(2, "Пісня 2", "Автор 2", 1995)
//                // Add more songs here
//        );
//
//        songTableView.setItems(songs);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    public static class Song {
//        private final SimpleIntegerProperty number;
//        private final SimpleStringProperty title;
//        private final SimpleStringProperty author;
//        private final SimpleIntegerProperty year;
//
//        public Song(Integer number, String title, String author, Integer year) {
//            this.number = new SimpleIntegerProperty(number);
//            this.title = new SimpleStringProperty(title);
//            this.author = new SimpleStringProperty(author);
//            this.year = new SimpleIntegerProperty(year);
//        }
//
//        public int getNumber() {
//            return number.get();
//        }
//
//        public String getTitle() {
//            return title.get();
//        }
//
//        public String getAuthor() {
//            return author.get();
//        }
//
//        public int getYear() {
//            return year.get();
//        }
//    }
//}
import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelloApplication extends Application {

    private Text textToDisplay;
    private TextFlow textFlowToDisplay;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Пошук слова в тексті");

        textToDisplay = new Text("Тут ваш текст, в якому потрібно робити пошук.");
        textFlowToDisplay = new TextFlow(textToDisplay);
        textFlowToDisplay.setVisible(true);

        Button searchButton = new Button("🔍"); // Значок лупи
        TextField searchField = new TextField();
        searchField.setVisible(false); // Початково поле вводу приховане
        Text resultLabel = new Text();

        searchButton.setOnAction(e -> {
            searchField.setVisible(!searchField.isVisible()); // Перемикаємо видимість поля вводу
            resultLabel.setText(""); // Очищаємо результат
        });

        searchField.setOnAction(e -> {
            String searchString = searchField.getText();
            if (textToDisplay.getText().contains(searchString)) {
                resultLabel.setText("");
                String originalText = textToDisplay.getText();
                int startIndex = originalText.indexOf(searchString);
                int endIndex = startIndex + searchString.length();

                // Створюємо частини тексту з різними стилями
                Text textBefore = new Text(originalText.substring(0, startIndex));
                Text highlightedText = new Text(originalText.substring(startIndex, endIndex));
                highlightedText.setFill(javafx.scene.paint.Color.RED);
                highlightedText.setStyle("-fx-fill: red;");

                Text textAfter = new Text(originalText.substring(endIndex));

                // Збираємо всі частини разом

                textFlowToDisplay.getChildren().clear();
                textFlowToDisplay.getChildren().addAll(textBefore, highlightedText, textAfter);

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), event -> {
                            textFlowToDisplay.getChildren().clear();
                            textFlowToDisplay.getChildren().add(textToDisplay);
                        })
                );
                timeline.setCycleCount(1);
                timeline.play();
            } else {
                resultLabel.setText("Слово не знайдено в тексті.");
            }
        });

        Scene scene = new Scene(new TextFlow(searchButton, textFlowToDisplay, searchField, resultLabel), 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}





