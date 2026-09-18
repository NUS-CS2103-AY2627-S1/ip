package rene.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rene.Rene;

/**
 * Creates Rene's JavaFX scene from its FXML view.
 */
public class Main extends Application {
    private final Rene rene = new Rene();

    /**
     * Loads the main window, injects Rene, and displays the application stage.
     *
     * @param stage the primary JavaFX stage.
     * @throws IOException if the main window FXML cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setRene(rene);

        stage.setScene(new Scene(mainWindow));
        stage.setTitle("Rene — Productivity Liaison");
        stage.setMinWidth(420);
        stage.setMinHeight(560);
        stage.show();
    }
}
