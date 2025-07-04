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

        List<Label> leftLabels = List.of(leftLabel1, leftLabel2, leftLabel3, leftLabel4, leftLabel5);
        List<Label> rightLabels = List.of(rightLabel1, rightLabel2, rightLabel3, rightLabel4, rightLabel5);

        for (int i = 0; i < 5 && i < ids.size(); i++) {
            String id = ids.get(i);
            leftLabels.get(i).setText(model.get(id, langPair[0]));
            leftLabels.get(i).setId("left_" + id);
            rightLabels.get(i).setText(model.get(id, langPair[1]));
            rightLabels.get(i).setId("right_" + id);
            setupDrag(leftLabels.get(i), rightLabels.get(i));
            setupDrag(rightLabels.get(i), leftLabels.get(i));
        }
    }

    private void setupDrag(Label start, Label target) {
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
                Bounds b = target.localToScene(target.getBoundsInLocal());
                if (b.contains(e.getSceneX(), e.getSceneY())) {
                    Point2D endPoint = getCenter(target);
                    currentLine.setEndX(endPoint.getX());
                    currentLine.setEndY(endPoint.getY());
                } else {
                    drawPane.getChildren().remove(currentLine);
                }
                lines.add(currentLine);
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
        System.out.println("ConnectTrainer: back to menu");
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
