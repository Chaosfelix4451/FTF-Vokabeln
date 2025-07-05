package Utils.Confetti;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;

/**
 * Erzeugt einfache Konfetti-Animationen zur Belohnung.
 */
public class Confetti {
    private static final Random random = new Random();

    /**
     * Startet eine Konfetti-Animation auf dem angegebenen Pane.
     */
    public static void show(Pane pane) {
        if (pane == null) {
            return;
        }

        javafx.application.Platform.runLater(() -> spawnConfetti(pane));
    }

    /**
     * Legt die Partikel an und animiert sie.
     */
    private static void spawnConfetti(Pane pane) {
        double width = pane.getWidth();
        double height = pane.getHeight();

        if (width < 2) {
            width = pane.getLayoutBounds().getWidth();
        }
        if (height < 2) {
            height = pane.getLayoutBounds().getHeight();
        }

        if (width < 2 && pane.getScene() != null) {
            width = pane.getScene().getWidth();
        }
        if (height < 2 && pane.getScene() != null) {
            height = pane.getScene().getHeight();
        }

        if (width < 2) {
            width = 800;
        }
        if (height < 2) {
            height = 600;
        }

        for (int i = 0; i < 60; i++) {
            Circle c = new Circle(5, randomColor());
            c.setCenterX(random.nextDouble() * width);
            c.setCenterY(-10);
            pane.getChildren().add(c);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(3 + random.nextDouble()), c);
            tt.setFromY(-10);
            tt.setToY(height + 10);
            tt.setOnFinished(e -> pane.getChildren().remove(c));
            tt.play();

            FadeTransition ft = new FadeTransition(Duration.seconds(3 + random.nextDouble()), c);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();
        }
    }

    /**
     * Liefert eine zufällige, satte Farbe.
     */
    private static Color randomColor() {
        return Color.hsb(random.nextDouble() * 360, 1.0, 1.0);
    }
}
