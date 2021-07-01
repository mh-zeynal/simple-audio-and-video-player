package sample;

import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;

public class Scene2Controller {
    private boolean flag = false;
    private Duration duration;
    private File file;
    private boolean replayKey;
    private boolean muteKey;
    private double currentVolume;
    @FXML private MediaView mediaView;
    @FXML private MediaPlayer player;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Label fileName;
    @FXML private Button replayButton;
    @FXML private Button muteButton;
    @FXML private Button unmuteButton;
    public void initialize(){
        replayKey = false;
        muteKey = false;
        playButton.setVisible(true);
        playButton.setDisable(false);
        pauseButton.setVisible(false);
        pauseButton.setDisable(true);
        muteButton.setVisible(false);
        muteButton.setDisable(true);
    }
    @FXML public void play(ActionEvent event){
        if (!flag){
            FileChooser chooser = new FileChooser();
            file = chooser.showOpenDialog(null);
            fileName.setText(file.getName());
            class Temp extends Thread {
                @Override
                public void run() {
                    Media media = new Media(Paths.get(file.getAbsolutePath()).toUri().toString());
                    player = new MediaPlayer(media);
                    mediaView.setMediaPlayer(player);
                    player.play();
                }
            }
            new Temp().start();
            playButton.setVisible(false);
            playButton.setDisable(true);
            pauseButton.setVisible(true);
            pauseButton.setDisable(false);
            flag = true;
        }
        else{
            player.setStartTime(duration);
            player.play();
            playButton.setVisible(false);
            playButton.setDisable(true);
            pauseButton.setVisible(true);
            pauseButton.setDisable(false);
        }
    }
    @FXML public void pause(ActionEvent event){
        duration = player.getCurrentTime();
        player.pause();
        pauseButton.setDisable(true);
        pauseButton.setVisible(false);
        playButton.setVisible(true);
        playButton.setDisable(false);
    }
    @FXML public void setReplay(ActionEvent event){
        if (!replayKey){
            replayButton.setStyle("-fx-background-color: #6d1414; -fx-background-radius: 18");
            player.setCycleCount(Timeline.INDEFINITE);
            replayKey = true;
        }
        else{
            replayButton.setStyle("-fx-background-color: purple; -fx-background-radius: 18");
            player.setCycleCount(0);
            replayKey = false;
        }
    }
    @FXML public void setMute(ActionEvent event){
        if (!muteKey){
            currentVolume = player.getVolume();
            player.setVolume(0);
            muteKey = true;
            unmuteButton.setDisable(true);
            unmuteButton.setVisible(false);
            muteButton.setVisible(true);
            muteButton.setDisable(false);
        }
        else
        {
            player.setVolume(currentVolume);
            muteKey = false;
            unmuteButton.setDisable(false);
            unmuteButton.setVisible(true);
            muteButton.setVisible(false);
            muteButton.setDisable(true);
        }
    }
}
