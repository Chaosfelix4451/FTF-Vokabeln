import MainMenu.MainMenuController;
import Settings.SettingsController;
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
        FXMLLoader TrainerLoader = new FXMLLoader(getClass().getResource("Trainer/Trainer.fxml"));
        Parent TrainerRoot = TrainerLoader.load();
        // Retrieve second controller handle
        TrainerController TrainerControllerHandle = (TrainerController) TrainerLoader.getController();
        TrainerControllerHandle.setStage(primaryStage);

        // Load second view
        FXMLLoader SettingsLoader = new FXMLLoader(getClass().getResource("Settings/Settings.fxml"));
        Parent SettingsRoot = SettingsLoader.load();
        // Retrieve Third controller handle
        SettingsController SettingsControllerHandle = (SettingsController) SettingsLoader.getController();
        SettingsControllerHandle.setStage(primaryStage);

        // Create scenes
        Scene mainScene = new Scene(mainRoot);
        Scene TrainerScene = new Scene(TrainerRoot);
        Scene SettingsScene = new Scene(SettingsRoot);
        mainMenuControllerHandle.setNextScene(TrainerScene);
        TrainerControllerHandle.setNextScene(mainScene);


        // Title of the main window
        primaryStage.setTitle("Vokabeltrainer");
        // Set and show initial scene
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
