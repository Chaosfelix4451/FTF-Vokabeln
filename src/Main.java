import Utils.SceneLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Utils.SceneLoader;

import java.io.IOException;

public class Main extends Application {

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Lade NUR die MainMenu-Szene
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu/mainMenu.fxml"));
//        Parent root = loader.load();
//
//        MainMenu.MainMenuController controller = loader.getController();
//        controller.setStage(primaryStage); // Übergibt nur den Stage
//
//        primaryStage.setTitle("Vokabeltrainer");
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneLoader.setPrimaryStage(primaryStage); // 🔥 einmalig registrieren
        SceneLoader.load("/MainMenu/mainMenu.fxml"); // Kein explizites stage mehr nötig
    }


}