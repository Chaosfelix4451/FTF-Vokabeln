package ConnectTrainer;

import Trainer.TrainerModel;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectTrainerController extends StageAwareController {

    @FXML private GridPane mainGrid;
    @FXML private VBox leftBox;
    @FXML private VBox rightBox;
    @FXML private AnchorPane drawPane;

    private final List<Line> lines = new ArrayList<>();
    private Line currentLine;

    @FXML
    private void initialize() {
        String listId = UserSys.getPreference("vocabFile", "defaultvocab.json");
        String mode = UserSys.getPreference("vocabMode", "Deutsch zu Englisch");
        TrainerModel model = new TrainerModel();
        model.LoadJSONtoDataObj(listId);

        String[] langPair = model.getLangPairForMode(mode);
        if (langPair == null) langPair = new String[]{"de", "en"};

        List<String> ids = new ArrayList<>(model.getAllIds());
        Collections.shuffle(ids);

        for (int i = 0; i < 5 && i < ids.size(); i++) {
            String id = ids.get(i);

            // LEFT
            Label vocabLeft = new Label(model.get(id, langPair[0]));
            vocabLeft.getStyleClass().add("vocab-box");

            Label connLeft = new Label();
            connLeft.setPrefSize(20, 20);
            connLeft.getStyleClass().add("connector-box");

            VBox leftRow = new VBox(5, vocabLeft, connLeft);
            leftBox.getChildren().add(leftRow);

            // RIGHT
            Label connRight = new Label();
            connRight.setPrefSize(20, 20);
            connRight.getStyleClass().add("connector-box");

            Label vocabRight = new Label(model.get(id, langPair[1]));
            vocabRight.getStyleClass().add("vocab-box");

            VBox rightRow = new VBox(5, connRight, vocabRight);
            rightBox.getChildren().add(rightRow);

            setupDrag(connLeft);
            setupDrag(connRight);
        }
    }

    private void setupDrag(Label connector) {
        connector.setOnMousePressed(e -> {
            // Remove old line from this connector
            lines.removeIf(line -> {
                if (line.getUserData() == connector) {
                    drawPane.getChildren().remove(line);
                    return true;
                }
                return false;
            });

            Point2D startScene = getCenter(connector);
            Point2D startPoint = sceneToPane(startScene.getX(), startScene.getY());
            currentLine = new Line(startPoint.getX(), startPoint.getY(), startPoint.getX(), startPoint.getY());
            currentLine.setStroke(randomColor());
            currentLine.setUserData(connector);
            drawPane.getChildren().add(currentLine);
        });

        connector.setOnMouseDragged(e -> {
            if (currentLine != null) {
                Point2D p = sceneToPane(e.getSceneX(), e.getSceneY());
                currentLine.setEndX(p.getX());
                currentLine.setEndY(p.getY());
            }
        });

        connector.setOnMouseReleased(e -> {
            if (currentLine != null) {
                Point2D release = new Point2D(e.getSceneX(), e.getSceneY());
                Label target = findConnectorAt(release, connector);
                if (target != null) {
                    Point2D endPoint = getCenter(target);
                    currentLine.setEndX(endPoint.getX());
                    currentLine.setEndY(endPoint.getY());
                    lines.add(currentLine);
                } else {
                    drawPane.getChildren().remove(currentLine);
                }
                currentLine = null;
            }
        });
    }

    private Label findConnectorAt(Point2D scenePoint, Label ignore) {
        for (javafx.scene.Node node : mainGrid.lookupAll(".connector-box")) {
            if (!(node instanceof Label conn) || conn == ignore) continue;
            Bounds bounds = conn.localToScene(conn.getBoundsInLocal());
            if (bounds.contains(scenePoint)) return conn;
        }
        return null;
    }

    private Color randomColor() {
        double hue = Math.random() * 360;
        double saturation = 0.5 + Math.random() * 0.3;
        double brightness = 0.5 + Math.random() * 0.2;
        return Color.hsb(hue, saturation, brightness);
    }

    private Point2D getCenter(Label node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
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