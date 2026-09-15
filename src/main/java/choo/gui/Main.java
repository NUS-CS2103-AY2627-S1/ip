package choo.gui;

import java.io.IOException;

import choo.Choo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays CHOO's JavaFX user interface.
 */
public class Main extends Application {
    private final Choo choo = new Choo();

    /**
     * Loads the main FXML view and displays it in the primary stage.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setChoo(this.choo);

            stage.setTitle("CHOO");
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the CHOO interface.", exception);
        }
    }
}
