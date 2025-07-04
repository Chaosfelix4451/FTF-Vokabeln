package ConnectTrainer;

import Trainer.TrainerModel;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectTrainerController extends StageAwareController {
    @FXML
    private Pane drawPane;
    @FXML
    private Label leftLabel1, leftLabel2, leftLabel3, leftLabel4, leftLabel5;
    @FXML
    private Label rightLabel1, rightLabel2, rightLabel3, rightLabel4, rightLabel5;
    @FXML
    private Label leftConnector1, leftConnector2, leftConnector3, leftConnector4, leftConnector5;
    @FXML
    private Label rightConnector1, rightConnector2, rightConnector3, rightConnector4, rightConnector5;

    private final List<Label> leftLabels = new ArrayList<>();
    private final List<Label> rightLabels = new ArrayList<>();
    private final List<Label> leftConnectors = new ArrayList<>();
    private final List<Label> rightConnectors = new ArrayList<>();


    private final List<Line> lines = new ArrayList<>();
    private Line currentLine;

    @FXML
    private void initialize() {
        System.out.println("ConnectTrainer: initialize");
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
            double y = 40 + i * 60;

            // Linke Seite: Label halb in Connector
            Label lLabel = new Label(model.get(id, langPair[0]));
            lLabel.setId("left_" + id);
            lLabel.setLayoutX(100);
            lLabel.setLayoutY(y);
            lLabel.setPrefSize(100, 40);
            lLabel.getStyleClass().add("vocab-box");

            Label lConn = new Label();
            lConn.setLayoutX(180);
            lConn.setLayoutY(y + 10);
            lConn.setPrefSize(20, 20);
            lConn.getStyleClass().add("connector-box");

            leftLabels.add(lLabel);
            leftConnectors.add(lConn);
            drawPane.getChildren().addAll(lLabel, lConn);

            // Rechte Seite: Label halb in Connector
            Label rConn = new Label();
            rConn.setLayoutX(400);
            rConn.setLayoutY(y + 10);
            rConn.setPrefSize(20, 20);
            rConn.getStyleClass().add("connector-box");

            Label rLabel = new Label(model.get(id, langPair[1]));
            rLabel.setId("right_" + id);
            rLabel.setLayoutX(410);
            rLabel.setLayoutY(y);
            rLabel.setPrefSize(100, 40);
            rLabel.getStyleClass().add("vocab-box");

            rightLabels.add(rLabel);
            rightConnectors.add(rConn);
            drawPane.getChildren().addAll(rConn, rLabel);

            // Drag & Drop Setup
            setupDrag(lConn, rightConnectors);
            setupDrag(rConn, leftConnectors);
        }
    }


    private void setupDrag(Label start, List<Label> targets) {
        start.setOnMousePressed(e -> {
            Point2D startPoint = getCenter(start);
            currentLine = new Line(startPoint.getX(), startPoint.getY(), startPoint.getX(), startPoint.getY());
            currentLine.setStroke(randomColor());
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
                boolean placed = false;
                for (Label target : targets) {
                    Bounds b = target.localToScene(target.getBoundsInLocal());
                    if (b.contains(e.getSceneX(), e.getSceneY())) {
                        Point2D endPoint = getCenter(target);
                        currentLine.setEndX(endPoint.getX());
                        currentLine.setEndY(endPoint.getY());
                        placed = true;
                        break;
                    }
                }
                if (!placed) {
                    drawPane.getChildren().remove(currentLine);
                } else {
                    lines.add(currentLine);
                }
                currentLine = null;
            }
        });
    }

    private Color randomColor() {
        double hue = Math.random() * 360;
        double saturation = 0.5 + Math.random() * 0.3; // avoid extremes
        double brightness = 0.5 + Math.random() * 0.2;
        return Color.hsb(hue, saturation, brightness);
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
        System.out.println("ConnectTrainer: back to menu");
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
