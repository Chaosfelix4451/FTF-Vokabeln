package Utils;

import Utils.SceneLoader.SceneLoader.HasStage;
import javafx.stage.Stage;

/**
 * Basisklasse für Controller, denen die aktuelle {@link Stage}
 * vom {@link Utils.SceneLoader.SceneLoader} übergeben wird.
 * Sie stellt lediglich eine Implementierung von {@link HasStage}
 * bereit und speichert das Stage-Objekt für Unterklassen.
 */
public abstract class StageAwareController implements HasStage {

    /** Die vom SceneLoader gesetzte Stage. */
    protected Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
