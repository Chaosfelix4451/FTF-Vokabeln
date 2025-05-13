package org.example;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    
    private MainMenuModel model;
    
    public MainMenuController() {
        this.model = new MainMenuModel();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Wird aufgerufen, wenn die FXML-Datei geladen wurde
        updateView();
    }
    
    private void updateView() {
        // Hier View-Elemente aktualisieren
    }
}