import MainMenu.MainMenuController;
import Trainer.TrainerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        // Load main view
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainMenu/mainMenu.fxml"));
        Parent mainRoot = mainLoader.load();

        // Retrieve main controller handle
        MainMenuController mainMenuControllerHandle = (MainMenuController) mainLoader.getController();
        mainMenuControllerHandle.setStage(primaryStage);

        // Load second view
        FXMLLoader secondLoader = new FXMLLoader(getClass().getResource("Trainer/Trainer.fxml"));
        Parent secondRoot = secondLoader.load();
        // Retrieve second controller handle
        TrainerController secondControllerHandle = (TrainerController) secondLoader.getController();
        secondControllerHandle.setStage(primaryStage);

        // Create scenes
        Scene mainScene = new Scene(mainRoot);
        Scene secondScene = new Scene(secondRoot);
        mainMenuControllerHandle.setNextScene(secondScene);
        secondControllerHandle.setNextScene(mainScene);

        // Title of the main window
        primaryStage.setTitle("Vokabel Trainer.");
        // Set and show initial scene
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
