package ConnectTrainer;

import Trainer.TrainerModel;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.*;

public class ConnectTrainerController extends StageAwareController {

    @FXML private GridPane mainGrid;
    @FXML private VBox leftBox;
    @FXML private VBox rightBox;
    @FXML private AnchorPane drawPane;

    private final List<Line> lines = new ArrayList<>();
    private final Map<Line, Label[]> lineEndpoints = new HashMap<>();
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
            VBox.setMargin(vocabLeft, new Insets(5));

            Label connLeft = new Label();
            connLeft.setPrefSize(10, 20);
            connLeft.getStyleClass().add("connector-box");
            connLeft.setTranslateX(0);

            HBox leftRow = new HBox(vocabLeft, connLeft);
            leftRow.setSpacing(0);
            leftRow.setAlignment(Pos.CENTER_RIGHT);
            VBox.setMargin(leftRow, new Insets(10));
            leftBox.getChildren().add(leftRow);

            // RIGHT
            Label connRight = new Label();
            connRight.setPrefSize(20, 20);
            connRight.getStyleClass().add("connector-box");
            connRight.setTranslateX(10);

            Label vocabRight = new Label(model.get(id, langPair[1]));
            vocabRight.getStyleClass().add("vocab-box");
            VBox.setMargin(vocabRight, new Insets(5));

            HBox rightRow = new HBox(connRight, vocabRight);
            rightRow.setSpacing(0);
            rightRow.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(rightRow, new Insets(10));
            rightBox.getChildren().add(rightRow);

            setupDrag(connLeft);
            setupDrag(connRight);
        }

        // Redraw on pane size change
        drawPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawAllLines());
        drawPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawAllLines());

        // Zusätzlich bei Layout-Änderung (z.B. Stage resized)
        leftBox.layoutBoundsProperty().addListener((obs, o, n) -> redrawAllLines());
        rightBox.layoutBoundsProperty().addListener((obs, o, n) -> redrawAllLines());

        // Zusätzlich bei Stage-Resize (z. B. Wechsel Vollbild/Fenster)
        drawPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin instanceof javafx.stage.Stage stage) {
                        stage.widthProperty().addListener((o, oldW, newW) -> redrawAllLines());
                        stage.heightProperty().addListener((o, oldH, newH) -> redrawAllLines());
                    }
                });
            }
        });

    }

    private void setupDrag(Label connector) {
        connector.setOnMousePressed(e -> {
            removeOldLines(connector);

            Point2D startPoint = getCenter(connector);
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
                Label target = findConnectorAt(release, (Label) currentLine.getUserData());
                if (target != null) {
                    Point2D endPoint = getCenter(target);
                    currentLine.setEndX(endPoint.getX());
                    currentLine.setEndY(endPoint.getY());
                    lines.add(currentLine);
                    lineEndpoints.put(currentLine, new Label[]{(Label) currentLine.getUserData(), target});
                } else {
                    drawPane.getChildren().remove(currentLine);
                }
                currentLine = null;
            }
        });
    }

    private void removeOldLines(Label connector) {
        List<Line> toRemove = new ArrayList<>();
        for (Line l : lines) {
            Label[] endpoints = lineEndpoints.get(l);
            if (endpoints != null && (endpoints[0] == connector || endpoints[1] == connector)) {
                toRemove.add(l);
            }
        }
        for (Line l : toRemove) {
            drawPane.getChildren().remove(l);
            lines.remove(l);
            lineEndpoints.remove(l);
        }
    }

    private void redrawAllLines() {
        for (Map.Entry<Line, Label[]> entry : lineEndpoints.entrySet()) {
            Line line = entry.getKey();
            Label[] pair = entry.getValue();

            Point2D p1 = getCenter(pair[0]);
            Point2D p2 = getCenter(pair[1]);

            line.setStartX(p1.getX());
            line.setStartY(p1.getY());
            line.setEndX(p2.getX());
            line.setEndY(p2.getY());
        }
    }

    private Label findConnectorAt(Point2D scenePoint, Label ignore) {
        for (javafx.scene.Node node : drawPane.lookupAll(".connector-box")) {
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
        Point2D scenePoint = new Point2D(bounds.getMinX() + bounds.getWidth() / 2,
                bounds.getMinY() + bounds.getHeight() / 2);
        return drawPane.sceneToLocal(scenePoint);
    }

    private Point2D sceneToPane(double x, double y) {
        return drawPane.sceneToLocal(x, y);
    }

    @FXML
    private void handleBack() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
