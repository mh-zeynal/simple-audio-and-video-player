package sample;

import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    private double wholeDuration;
    private Media media;
    @FXML private MediaView mediaView;
    @FXML private MediaPlayer player;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Label fileName;
    @FXML private Button replayButton;
    @FXML private Button muteButton;
    @FXML private Button unmuteButton;
    @FXML private Button loadFile;
    @FXML private Button forward10;
    @FXML private Button backward10;
    @FXML private Slider volumeSlider;
    @FXML private Label volumeValue;
    @FXML private Slider progressSlider;
    @FXML private Label currentTime;
    @FXML private Label mediaWholeTime;
    class ReplayThread extends Thread{
        @Override
        public void run() {
            while (true){
                if (player.getMedia() != null && player != null){
                    if (player.getMedia().getDuration().toSeconds() == player.getCurrentTime().toSeconds()) {
                        player.setStartTime(new Duration(0));
                        break;
                    }
                }
                else if(player.getMedia() == null)
                    break;
            }
        }
    }
    private ReplayThread replayThread;
    public void initialize(){
        replayKey = false;
        muteKey = false;
        playButton.setVisible(true);
        playButton.setDisable(false);
        pauseButton.setVisible(false);
        pauseButton.setDisable(true);
        muteButton.setVisible(false);
        muteButton.setDisable(true);
        volumeSlider.setValue(0.5);
    }
    public String getTime(int temp){
        int hour = 0;
        int minute = 0;
        int seconds = 0;
        if ((temp / 60) == 0){
            seconds = temp;
            return "" + getClearFormat(hour) + ":" + getClearFormat(minute) + ":" + getClearFormat(seconds);
        }
        else if ((int)(temp / 3600) == 0 && (temp / 60) > 0){
            minute = temp / 60;
            temp = temp % 60;
            seconds = temp;
            return "" + getClearFormat(hour) + ":" + getClearFormat(minute) + ":" + getClearFormat(seconds);
        }
        else if((temp / 3600) > 0){
            hour = temp / 3600;
            temp = temp % 3600;
            minute = temp / 60;
            temp = temp % 60;
            seconds = temp;
            return "" + getClearFormat(hour) + ":" + getClearFormat(minute) + ":" + getClearFormat(seconds);
        }
        return null;
    }
    private String getClearFormat(int x){
        if (x < 10){
            return "0" + x;
        }
        return "" + x;
    }
    @FXML public void play(ActionEvent event){
        player.setStartTime(duration);
        player.play();
        playButton.setVisible(false);
        playButton.setDisable(true);
        pauseButton.setVisible(true);
        pauseButton.setDisable(false);
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
            replayThread = new ReplayThread();
            replayThread.start();
        }
        else{
            replayButton.setStyle("-fx-background-color: #0C1F84FF; -fx-background-radius: 18");
            player.setCycleCount(0);
            replayThread.interrupt();
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
        else {
            player.setVolume(currentVolume);
            muteKey = false;
            unmuteButton.setDisable(false);
            unmuteButton.setVisible(true);
            muteButton.setVisible(false);
            muteButton.setDisable(true);
        }
    }
    @FXML public void loadFileClick(){
        FileChooser chooser = new FileChooser();
        file = chooser.showOpenDialog(null);
        fileName.setText(file.getName());
        media = new Media(Paths.get(file.getAbsolutePath()).toUri().toString());
        if (replayThread != null) {
            replayThread.interrupt();
            player.stop();
        }
        player = new MediaPlayer(media);
        mediaView.setMediaPlayer(player);
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                progressSlider.setMin(0);
                progressSlider.setMax(media.getDuration().toSeconds());
                progressSlider.setValue(0);
                progressSlider.setBlockIncrement(1);
                player.setVolume(0.5);
                mediaWholeTime.setText(getTime((int) media.getDuration().toSeconds()));
                player.currentTimeProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        progressSlider.setValue(player.getCurrentTime().toSeconds());
                        currentTime.setText(getTime((int) player.getCurrentTime().toSeconds()));
                    }
                });
            }
        });
        player.play();
        playButton.setVisible(false);
        playButton.setDisable(true);
        pauseButton.setVisible(true);
        pauseButton.setDisable(false);
        if (replayKey){
            player.setCycleCount(Timeline.INDEFINITE);
            replayThread = new ReplayThread();
            replayThread.start();
        }
    }
    @FXML public void loadFileKeyBoard(KeyEvent event){
        KeyCodeCombination keyCodeCombination = new KeyCodeCombination(KeyCode.F,
                KeyCombination.ALT_ANY,
                KeyCombination.CONTROL_ANY);
        if (keyCodeCombination.match(event)){
            FileChooser chooser = new FileChooser();
            file = chooser.showOpenDialog(null);
            fileName.setText(file.getName());
            media = new Media(Paths.get(file.getAbsolutePath()).toUri().toString());
            if (replayThread != null) {
                replayThread.interrupt();
                player.stop();
            }
            player = new MediaPlayer(media);
            mediaView.setMediaPlayer(player);
            player.setOnReady(new Runnable() {
                @Override
                public void run() {
                    progressSlider.setMin(0);
                    progressSlider.setMax(media.getDuration().toSeconds());
                    progressSlider.setValue(0);
                    progressSlider.setBlockIncrement(1);
                    player.setVolume(0.5);
                    String temp = getTime((int) media.getDuration().toSeconds());
                    mediaWholeTime.setText(temp);
                    System.out.println(temp);
                    player.currentTimeProperty().addListener(new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            progressSlider.setValue(player.getCurrentTime().toSeconds());
                            currentTime.setText(getTime((int) player.getCurrentTime().toSeconds()));
                        }
                    });
                }
            });
            player.play();
            playButton.setVisible(false);
            playButton.setDisable(true);
            pauseButton.setVisible(true);
            pauseButton.setDisable(false);
            if (replayKey){
                player.setCycleCount(Timeline.INDEFINITE);
                replayThread = new ReplayThread();
                replayThread.start();
            }
        }
    }
    @FXML public void for10(ActionEvent event){
        player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + Duration.seconds(10).toSeconds()));
    }
    @FXML public void for10KeyBoard(KeyEvent event){
        if (event.getCode() == KeyCode.RIGHT)
            player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + Duration.seconds(10).toSeconds()));
    }
    @FXML public void back10(ActionEvent event){
        player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + Duration.seconds(-10).toSeconds()));
    }
    @FXML public void back10KeyBoard(KeyEvent event){
        if (event.getCode() == KeyCode.LEFT)
            player.seek(Duration.seconds(player.getCurrentTime().toSeconds() + Duration.seconds(-10).toSeconds()));

    }
    @FXML public void VolumeEqualizer(){
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                player.setVolume(t1.doubleValue());
                volumeValue.setText(String.valueOf((int)(t1.floatValue() * 100)));
                if (t1.floatValue() == 0){
                    unmuteButton.setDisable(true);
                    unmuteButton.setVisible(false);
                    muteButton.setVisible(true);
                    muteButton.setDisable(false);
                }
                else{
                    unmuteButton.setDisable(false);
                    unmuteButton.setVisible(true);
                    muteButton.setVisible(false);
                    muteButton.setDisable(true);
                }
            }
        });
    }
    @FXML public void setProgress(){
        progressSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (progressSlider.isValueChanging()) {
                    player.setStartTime(new Duration(progressSlider.getValue()));
                    player.play();
                    System.out.println(progressSlider.getValue());
                }
            }
        });
    }
}
