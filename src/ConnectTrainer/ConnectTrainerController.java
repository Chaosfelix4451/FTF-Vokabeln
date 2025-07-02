package ConnectTrainer;

import Trainer.TrainerModel;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class ConnectTrainerController extends StageAwareController {
    @FXML
    private Pane drawPane;
    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private Line currentLine;

    @FXML
    private void initialize() {
        String listId = UserSys.getPreference("vocabFile", "defaultvocab.json");
        String mode = UserSys.getPreference("vocabMode", "Deutsch zu Englisch");
        TrainerModel model = new TrainerModel();
        model.LoadJSONtoDataObj(listId);
        String id = model.getAllIds().iterator().next();
        String[] langPair = model.getLangPairForMode(mode);
        if (langPair == null) {
            langPair = new String[]{"de", "en"};
        }
        leftLabel.setText(model.get(id, langPair[0]));
        rightLabel.setText(model.get(id, langPair[1]));

        setupDrag(leftLabel, rightLabel);
        setupDrag(rightLabel, leftLabel);
    }

    private void setupDrag(Label start, Label other) {
        start.setOnMousePressed(e -> {
            Point2D startPoint = getCenter(start);
            currentLine = new Line(startPoint.getX(), startPoint.getY(), startPoint.getX(), startPoint.getY());
            drawPane.getChildren().add(currentLine);
        });
        start.setOnMouseDragged(e -> {
            if (currentLine != null) {
                Point2D p = sceneToPane(e.getSceneX(), e.getSceneY());
                currentLine.setEndX(p.getX());
                currentLine.setEndY(p.getY());
            }
        });
        start.setOnMouseReleased(e -> {
            if (currentLine != null) {
                Bounds b = other.localToScene(other.getBoundsInLocal());
                if (b.contains(e.getSceneX(), e.getSceneY())) {
                    Point2D endPoint = getCenter(other);
                    currentLine.setEndX(endPoint.getX());
                    currentLine.setEndY(endPoint.getY());
                } else {
                    drawPane.getChildren().remove(currentLine);
                }
                currentLine = null;
            }
        });
    }

    private Point2D getCenter(Label node) {
        Bounds bounds = node.getBoundsInParent();
        return new Point2D(bounds.getMinX() + bounds.getWidth() / 2,
                bounds.getMinY() + bounds.getHeight() / 2);
    }

    private Point2D sceneToPane(double x, double y) {
        return drawPane.sceneToLocal(x, y);
    }

    @FXML
    private void handleBack() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
