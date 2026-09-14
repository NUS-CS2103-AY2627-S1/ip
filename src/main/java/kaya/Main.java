package kaya;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kaya.ui.MainWindow;

/**
 * Displays Kaya's JavaFX interface.
 */
public class Main extends Application {
    private static final Path DATA_FILE = Path.of("data", "kaya.txt");

    private final Kaya kaya = new Kaya(DATA_FILE);

    /**
     * Creates the JavaFX application using Kaya's default data file.
     */
    public Main() {
        // JavaFX constructs this application through its no-argument constructor.
    }

    /**
     * Loads the main FXML view and displays it in the primary stage.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setKaya(kaya);

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Kaya");
        stage.setMinWidth(420);
        stage.setMinHeight(600);
        stage.show();
    }
}
