package kia;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kia.ui.MainWindow;

/**
 * Starts Kia's JavaFX chatbot window.
 */
public class Main extends Application {
    /**
     * Loads the FXML view and displays the chatbot scene on JavaFX's primary stage.
     *
     * @param stage the primary window supplied by JavaFX
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow controller = loader.getController();
        controller.setKia(new Kia());

        stage.setTitle("Kia");
        stage.setScene(new Scene(root));
        stage.setMinWidth(420);
        stage.setMinHeight(600);
        stage.show();
    }
}
