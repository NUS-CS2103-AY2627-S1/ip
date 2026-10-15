package alex.gui;

import java.io.IOException;

import alex.Alex;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Runs the JavaFX interface for Alex.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/alex.txt";

    private final Alex alex = new Alex(DEFAULT_FILE_PATH);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setAlex(alex);

        Scene scene = new Scene(mainLayout);
        stage.setTitle("Alex");
        stage.setMinHeight(420.0);
        stage.setMinWidth(360.0);
        stage.setScene(scene);
        stage.show();
    }
}
