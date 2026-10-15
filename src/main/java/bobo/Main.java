package bobo;

import bobo.ui.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class for Bobo GUI.
 */
public class Main extends Application {

    private final Bobo bobo = new Bobo();

    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setBobo(bobo);

        Scene scene = new Scene(mainWindow);
        stage.setScene(scene);
        stage.setTitle("Bobo Chatbot");
        stage.setMinWidth(400.0);
        stage.setMinHeight(600.0);
        stage.setWidth(450.0);
        stage.setHeight(650.0);
        stage.show();
    }
}
