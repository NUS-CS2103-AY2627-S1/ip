package sherlock.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sherlock.Sherlock;

/**
 * Displays Sherlock's JavaFX interface from its FXML view.
 */
public class Main extends Application {
    private final Sherlock sherlock = new Sherlock();

    /**
     * Loads the main window, injects Sherlock, and displays the stage.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setSherlock(sherlock);

        stage.setTitle("Sherlock — Detective Casebook");
        stage.setMinHeight(600);
        stage.setMinWidth(420);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
