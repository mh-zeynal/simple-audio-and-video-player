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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
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
    private int mediaDuration;
    private boolean playKey;
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
    @FXML private Pane pane;
    @FXML private Pane buttonsPane;
    @FXML private BorderPane borderPane;
    @FXML private MenuItem darkTheme, lightTheme, playVideo, openFile, volumeUp, volumeDown, replay;
    @FXML private MenuItem goForward10, goBackward10, muteVideo;
    public void initialize(){
        playKey = false;
        replayKey = false;
        muteKey = false;
        playButton.setVisible(true);
        playButton.setDisable(false);
        pauseButton.setVisible(false);
        pauseButton.setDisable(true);
        muteButton.setVisible(false);
        muteButton.setDisable(true);
        volumeSlider.setValue(0.5);
        darkTheme.setAccelerator(new KeyCodeCombination(KeyCode.D,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.ALT_DOWN));
        lightTheme.setAccelerator(new KeyCodeCombination(KeyCode.L,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.ALT_DOWN));
        playVideo.setAccelerator(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN));
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN));
        volumeUp.setAccelerator(new KeyCodeCombination(KeyCode.UP));
        volumeDown.setAccelerator(new KeyCodeCombination(KeyCode.DOWN));
        replay.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        goForward10.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT));
        goBackward10.setAccelerator(new KeyCodeCombination(KeyCode.LEFT));
        muteVideo.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
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
        player.seek(duration);
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
        }
        else{
            replayButton.setStyle("-fx-background-color: #0C1F84FF; -fx-background-radius: 18");
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
        if (replayKey)
            player.stop();
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
                mediaDuration = (int) progressSlider.getMax();
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
        if (replayKey)
            player.setCycleCount(Timeline.INDEFINITE);
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
    @FXML public void goDark(ActionEvent event){
        currentTime.setTextFill(Color.WHITE);
        mediaWholeTime.setTextFill(Color.WHITE);
        borderPane.setStyle("-fx-background-color: #383636");
        pane.setStyle("-fx-background-color: black");
        buttonsPane.setStyle("-fx-background-color: black");
        volumeValue.setTextFill(Color.WHITE);
    }
    @FXML public void goLight(ActionEvent event){
        currentTime.setTextFill(Color.BLACK);
        mediaWholeTime.setTextFill(Color.BLACK);
        borderPane.setStyle("-fx-background-color: #939191");
        pane.setStyle("-fx-background-color: white");
        buttonsPane.setStyle("-fx-background-color: white");
        volumeValue.setTextFill(Color.BLACK);
    }
    @FXML public void playingOption(ActionEvent event){
        if (!playKey) {
            pause(event);
            playKey = true;
        }
        else{
            play(event);
            playKey = false;
        }
    }
    @FXML public void volumeIncrease(ActionEvent event){
        volumeSlider.setValue(volumeSlider.getValue() + 0.01);
        volumeValue.setText(String.valueOf((int)(volumeSlider.getValue() * 100)));
        player.setVolume(volumeSlider.getValue());
        if (volumeSlider.getValue() == 0){
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
    @FXML public void volumeDecrease(ActionEvent event){
        volumeSlider.setValue(volumeSlider.getValue() - 0.01);
        volumeValue.setText(String.valueOf((int)(volumeSlider.getValue() * 100)));
        player.setVolume(volumeSlider.getValue());
        if (volumeSlider.getValue() == 0){
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
}
