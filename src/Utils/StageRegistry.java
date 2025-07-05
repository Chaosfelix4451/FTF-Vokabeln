package Utils;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Hält alle geöffneten {@link Stage}s fest und ermöglicht das Aktivieren des
 * Dark‑Mode‑Stylesheets.
 */
public class StageRegistry {
    private static final List<Stage> STAGES = new ArrayList<>();
    private static String darkCss;

    /** Meldet eine Stage zur Verwaltung an. */
    public static void register(Stage stage) {
        if (stage == null || STAGES.contains(stage)) return;
        STAGES.add(stage);
        stage.setOnHidden(e -> STAGES.remove(stage));
    }

    /**
     * Schaltet das dunkle Design für alle registrierten Stages ein oder aus.
     *
     * @param enable {@code true} aktiviert den Modus, {@code false} deaktiviert ihn
     */
    public static void applyDarkMode(boolean enable) {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + (enable ? "🌙" : "☀️") + " Dark Mode " + (enable ? "aktiviert" : "deaktiviert"));
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
