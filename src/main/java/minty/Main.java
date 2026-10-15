package minty;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import minty.ui.MainWindow;

/**
 * Displays the JavaFX user interface for Minty.
 */
public class Main extends Application {
    /**
     * Creates and displays Minty's primary window.
     *
     * @param stage primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setMinty(new Minty(Path.of("data", "minty.txt")));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Main.class.getResource("/view/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Minty");
        stage.setMinHeight(500);
        stage.setMinWidth(420);
        stage.show();
    }
}
