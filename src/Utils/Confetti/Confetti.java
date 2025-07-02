package Utils.Confetti;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;

public class Confetti {
    private static final Random random = new Random();

    public static void show(Pane pane) {
        if (pane == null) return;
        double width = pane.getWidth();
        double height = pane.getHeight();
        // fallback if width/height are zero (not yet laid out)
        if (width == 0) width = 800; // default
        if (height == 0) height = 600;
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

    private static Color randomColor() {
        return Color.hsb(random.nextDouble() * 360, 1.0, 1.0);
    }
}
