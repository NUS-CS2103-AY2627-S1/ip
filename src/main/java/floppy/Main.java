package floppy;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the Floppy chatbot GUI defined using FXML.
 */
public class Main extends Application {
    private final Floppy floppy = new Floppy();

    /**
     * Loads and displays the main application window.
     *
     * @param stage Primary JavaFX stage.
     * @throws IOException If the main-window FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setFloppy(floppy);

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Floppy");
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        stage.show();
    }
}
