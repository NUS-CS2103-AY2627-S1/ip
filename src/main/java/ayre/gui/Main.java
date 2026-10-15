package ayre.gui;

import java.io.IOException;

import ayre.Ayre;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * A GUI for Ayre using FXML.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 417;
    private static final double MIN_WINDOW_HEIGHT = 220;

    private final Ayre ayre = new Ayre();

    /**
     * Initializes the stage parameters and the main window of the GUI.
     *
     * @param stage the primary stage for this application, onto which
     *     the application scene can be set.
     *     Applications may create other stages, if needed, but they will not be
     *     primary stages.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.getIcons().add(new Image("/images/Ayre.png"));
            stage.setTitle("Ayre");
            stage.setOnCloseRequest(event -> HelpWindow.close());
            fxmlLoader.<MainWindow>getController().setAyre(ayre); // inject the Ayre instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
