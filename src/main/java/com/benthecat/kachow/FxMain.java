package com.benthecat.kachow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts and configures the Kachow graphical interface.
 */
public class FxMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        AnchorPane rootPane = fxmlLoader.load();
        Scene scene = new Scene(rootPane);
        stage.setTitle("Kachow");
        stage.setScene(scene);
        stage.show();
    }
}
