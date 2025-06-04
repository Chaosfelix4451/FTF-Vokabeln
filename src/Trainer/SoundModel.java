package Trainer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
//Initialisierung des Sounds
public class SoundModel {
    public void playSound(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
