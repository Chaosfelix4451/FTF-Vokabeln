package Utils.Sound; // Passe das Package ggf. an

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class SoundModel {

    private MediaPlayer mediaPlayer;

    /**
     * Spielt eine Sounddatei ab.
     * @param /src/Utils/Sound/ Dateipfad zur .mp3-Datei
     */
    public void playSound(String pfad) {
        // Wenn bereits ein Ton läuft, stoppen
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // Neue Media-Instanz erzeugen
        Media sound = new Media(new File(pfad).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);

        // Bei Ende des Sounds Player freigeben
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            mediaPlayer = null;
        });

        // Ton abspielen
        mediaPlayer.play();
    }
}
