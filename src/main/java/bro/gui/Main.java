package bro.gui;

import java.io.IOException;

import bro.Bro;
import bro.Personality;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Configures and displays Bro's primary JavaFX stage. */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String WINDOW_TITLE = Personality.PERSONA_NAME + " — Task HQ";
    private static final double MIN_WINDOW_HEIGHT = 520;
    private static final double MIN_WINDOW_WIDTH = 380;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setBro(new Bro());

        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setScene(new Scene(root));
        stage.setTitle(WINDOW_TITLE);
        stage.show();
    }
}
