package com.example.testfx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.List;

public class Utils {

    public String getTextForSong(Song song) {
        StringBuilder res = new StringBuilder();
        res.append(song.getSongName()).append("\n\n\t");
        for(int i=0;i<song.getCouplets().length;i++) {
            res.append(song.getCouplets()[i]).append("\n\n\t");
        }
        return new String(res);
    }
    public void setSingleDigitIntegerListenerForTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) { // Перевірка, чи є тільки цифри
                if (newValue.length() > 1) {
                    textField.setText(oldValue);
                }
            } else {
                textField.setText(oldValue);
            }
        });
    }
}
