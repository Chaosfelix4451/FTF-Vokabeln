package Utils.Sound; // Passe das Package ggf. an

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**
 * Einfache Hilfsklasse zum Abspielen von kurzen Soundeffekten.
 */
public class SoundModel {

    private MediaPlayer mediaPlayer;

    /**
     * Spielt eine Sounddatei ab.
     *
     * @param pfad Dateipfad zur MP3-Datei
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
