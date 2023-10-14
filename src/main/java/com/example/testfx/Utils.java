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
    private final List<Button> allButtonsList;
    private final List<Song> songs;

    public Utils(List<Button> allButtonsList, List<Song> songs) {
        this.allButtonsList = allButtonsList;
        this.songs = songs;
    }

    public void disableButtons(Button... buttons){
        for(Button button : buttons){
            button.setDisable(true);
        }
    }
    public void disableAllButtonsExcept(Button ... buttons){
        OUTER:
        for(Button button : allButtonsList){
            for(Button buttonExc : buttons){
                if(button.equals(buttonExc))
                    continue OUTER;
            }
            button.setDisable(true);
        }
    }
    public void enableButtons(Button ... buttons){
        for(Button button : buttons){
            button.setDisable(false);
        }
    }
    public void enableAllButtonsExcept(Button ... buttons){
        OUTER:
        for(Button button : allButtonsList){
            for(Button buttonExc : buttons){
                if(button.equals(buttonExc))
                    continue OUTER;
            }
            button.setDisable(false);
        }
    }
    public Song getActualSongInTextArea(TextFlow lyricsTextArea){
        String text = ((Text)lyricsTextArea.getChildren().get(0)).getText();
        String songName=text.substring(0,text.indexOf("\n"));
        return getActualSongByName(songName);
    }
    public Song getActualSongByName(String songName){
        for(Song song : songs){
            if(song.songName.equals(songName)){
                return song;
            }
        }
        return null;
    }

    public String getTextForSong(Song song) {
        StringBuilder res = new StringBuilder();
        res.append(song.songName).append("\n\n\t");
        for(int i=0;i<song.couplets.length;i++) {
            res.append(song.couplets[i]).append("\n\n\t");
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

    public MediaPlayer getNewMediaPlayer(Media media, Slider slider, boolean isSliderBeingDragged, Label endTimeLabel){
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSliderBeingDragged) {
                Duration totalTime = mediaPlayer.getTotalDuration();
                double value = newValue.toSeconds() / totalTime.toSeconds();
                slider.setValue(value);
            }
        });
        mediaPlayer.setOnReady(()->{
            long totalSeconds = (long) media.getDuration().toSeconds();
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            endTimeLabel.setText(formattedTime);
        });
        return mediaPlayer;
    }
}
