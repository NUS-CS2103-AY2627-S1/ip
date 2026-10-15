package duchess.ui.gui.controller;

import java.io.IOException;
import java.net.URL;

import duchess.ui.Duchess;
import duchess.ui.gui.components.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Sets up the window for the application.
 */
public class Main extends Application {
    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = "duchess.txt";
    private static final String MAIN_WINDOW_RESOURCE = "/view/MainWindow.fxml";
    private static final double MIN_WINDOW_WIDTH = 400.0;
    private static final double MIN_WINDOW_HEIGHT = 600.0;

    @Override
    public void start(Stage stage) {
        Duchess duchess = createDuchess();
        MainWindow mainWindow = loadMainWindow();
        mainWindow.setBot(duchess);
        configureStage(stage, mainWindow);
    }

    private Duchess createDuchess() {
        try {
            return new Duchess(DATA_DIRECTORY, DATA_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialise task storage", e);
        }
    }

    private MainWindow loadMainWindow() {
        URL fxmlResource = Main.class.getResource(MAIN_WINDOW_RESOURCE);
        assert (fxmlResource != null) : "MainWindow.fxml must be present in /resources/view/";
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);

        AnchorPane root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load MainWindow.fxml", e);
        }

        MainWindow mainWindow = fxmlLoader.getController();
        assert (mainWindow != null) : "MainWindow.fxml must provide a MainWindow controller";
        assert (root != null) : "MainWindow.fxml must provide a root node";
        return mainWindow;
    }

    private void configureStage(Stage stage, MainWindow mainWindow) {
        stage.setScene(new Scene(mainWindow));
        stage.setTitle(Duchess.NAME);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.show();
    }


}
