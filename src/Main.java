import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        // Load main view
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
        Parent mainRoot = mainLoader.load();
        // Retrieve main controller handle
        MainController mainControllerHandle = (MainController) mainLoader.getController();
        mainControllerHandle.setStage(primaryStage);

        // Load second view
        FXMLLoader secondLoader = new FXMLLoader(getClass().getResource("Trainer.fxml"));
        Parent secondRoot = secondLoader.load();
        // Retrieve second controller handle
        TrainerController secondControllerHandle = (TrainerController) secondLoader.getController();
        secondControllerHandle.setStage(primaryStage);

        // Create scenes
        Scene mainScene = new Scene(mainRoot);
        Scene secondScene = new Scene(secondRoot);
        mainControllerHandle.setNextScene(secondScene);
        secondControllerHandle.setNextScene(mainScene);

        // Title of the main window
        primaryStage.setTitle("Scene switch example.");
        // Set and show initial scene
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
