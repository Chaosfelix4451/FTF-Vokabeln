package Utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that keeps track of all currently opened {@link Stage}s and
 * allows applying the dark stylesheet to each of them.
 */
public class StageRegistry {
    private static final List<Stage> STAGES = new ArrayList<>();
    private static String darkCss;

    /** Registers a stage to be managed. */
    public static void register(Stage stage) {
        if (stage == null || STAGES.contains(stage)) return;
        STAGES.add(stage);
        stage.setOnHidden(e -> STAGES.remove(stage));
    }

    /**
     * Applies or removes the dark stylesheet for all registered stages.
     *
     * @param enable true to enable dark mode, false to disable
     */
    public static void applyDarkMode(boolean enable) {
        String css = getDarkCss();
        for (Stage stage : new ArrayList<>(STAGES)) {
            Scene scene = stage.getScene();
            if (scene == null || css == null) continue;
            List<String> stylesheets = scene.getStylesheets();
            if (enable) {
                if (!stylesheets.contains(css)) {
                    stylesheets.add(css);
                }
            } else {
                stylesheets.remove(css);
            }
        }
    }

    private static String getDarkCss() {
        if (darkCss == null) {
            var url = StageRegistry.class.getResource("/dark.css");
            if (url != null) {
                darkCss = url.toExternalForm();
            }
        }
        return darkCss;
    }
}
