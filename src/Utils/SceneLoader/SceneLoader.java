package Utils.SceneLoader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import Utils.UserSys.UserSys;

public class SceneLoader {
    /**
     * Minimum and maximum scale factors applied when resizing scenes. These
     * bounds prevent extremely small or excessively large UI elements on
     * unusual window sizes.
     */
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 1.5;

    private static class StageManager {
        private static Stage primaryStage;

        public static void setPrimaryStage(Stage stage) {
            primaryStage = stage;
        }

        public static Stage getPrimaryStage() {
            return primaryStage;
        }
    }

    public static void setPrimaryStage(Stage stage) {
        StageManager.setPrimaryStage(stage);
    }

    public static void load(String fxmlPath) {
        Stage stage = StageManager.getPrimaryStage();
        if (stage == null) {
            throw new IllegalStateException("Primary Stage wurde nicht gesetzt. Nutze SceneLoader.setPrimaryStage(stage) im Startpunkt.");
        }
        load(stage, fxmlPath);
    }

    public static void load(Stage stage, String fxmlPath) {
        try {
            URL url = SceneLoader.class.getResource(fxmlPath);
            System.out.println("SceneLoader: Lade " + fxmlPath + " -> " + url);
            if (url == null) {
                throw new IllegalArgumentException("FXML-Datei nicht gefunden: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent content = loader.load();

            Object controller = loader.getController();
            if (controller instanceof HasStage) {
                ((HasStage) controller).setStage(stage);
            }

            StackPane wrapper = new StackPane(content);
            wrapper.getStyleClass().add("responsive-wrapper");
            // Anchor content to the top-left so menus remain visible on
            // very wide displays.
            wrapper.setAlignment(Pos.TOP_LEFT);
            Scene scene = new Scene(wrapper);
        applyResponsivePadding(wrapper, stage, fxmlPath);
        applyResponsiveSize(wrapper, content, stage, fxmlPath);
        applyResponsiveFontScale(content, stage, fxmlPath);

            // CSS-Dateipfad berechnen: gleicher Pfad wie FXML, aber mit .css statt .fxml
            String cssPath = fxmlPath.replace(".fxml", ".css");
            URL cssUrl = SceneLoader.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println(" 🆗 CSS-Datei gefunden für " + cssPath);
            } else {
                System.out.println("⚠️ Keine CSS-Datei gefunden für " + cssPath);
            }

            UserSys.loadFromJson();
            if (UserSys.getBooleanPreference("darkMode", false)) {
                URL darkUrl = SceneLoader.class.getResource("/dark.css");
                if (darkUrl != null) {
                    scene.getStylesheets().add(darkUrl.toExternalForm());
                }
            }

            URL responsiveUrl = SceneLoader.class.getResource("/responsive.css");
            if (responsiveUrl != null) {
                scene.getStylesheets().add(responsiveUrl.toExternalForm());
            }

            stage.setScene(scene);
            // keep current window state (fullscreen or not)
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyResponsivePadding(StackPane wrapper, Stage stage, String fxmlPath) {
        ChangeListener<Number> listener = (obs, o, n) -> {
            wrapper.setPadding(Insets.EMPTY);
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        // initial call
        listener.changed(null, null, null);
    }

    private static void applyResponsiveSize(StackPane wrapper, Parent content, Stage stage, String fxmlPath) {
        ChangeListener<Number> listener = (obs, o, n) -> {
            double w = stage.getWidth();
            double h = stage.getHeight();
            double targetW = w * 0.9;
            double targetH = h * 0.9;
            wrapper.setMinWidth(targetW);
            wrapper.setMinHeight(targetH);
            wrapper.setPrefWidth(targetW);
            wrapper.setPrefHeight(targetH);
            wrapper.setMaxWidth(targetW);
            wrapper.setMaxHeight(targetH);
            double scale = Math.min(targetW / 800.0, targetH / 600.0);
            scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
            content.setScaleX(scale);
            content.setScaleY(scale);
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        listener.changed(null, null, null);
    }

    private static void applyResponsiveFontScale(Parent content, Stage stage, String fxmlPath) {
        ChangeListener<Number> listener = (obs, o, n) -> {
            double width = stage.getWidth();
            double height = stage.getHeight();
            if (Double.isNaN(width) || Double.isNaN(height)) {
                return; // ignore until stage has valid dimensions
            }
            double scale = Math.min(width / 800.0, height / 600.0);
            scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
            content.setStyle("-fx-font-size: " + (14 * scale) + "px;");
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        listener.changed(null, null, null);
    }

    public interface HasStage {
        void setStage(Stage stage);
    }
}
