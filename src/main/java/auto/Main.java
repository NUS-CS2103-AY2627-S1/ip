package auto;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import auto.ui.MainWindow;

/**
 * Configures and displays Auto's JavaFX window.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String APPLICATION_ICON = "/images/auto-icon.png";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setAuto(new Auto());

        stage.setScene(new Scene(root));
        stage.setTitle("Auto");
        stage.getIcons().add(new Image(Main.class.getResource(APPLICATION_ICON).toExternalForm()));
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.show();
    }
}
