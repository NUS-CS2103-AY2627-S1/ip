package bot.gui;

import java.io.IOException;

import bot.Bot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides a JavaFX GUI for Bot, backed by the same {@link Bot} used by the
 * console UI (see {@code bot.Bot#main}) and the same data file, so tasks
 * added through either interface are visible in both.
 */
public class Main extends Application {

    private final Bot bot = new Bot("data/bot.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            ap.getStylesheets().add(Main.class.getResource("/view/bot.css").toExternalForm());
            // Keep the background visible even when the ScrollPane viewport is transparent.
            ap.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0f2fe, "
                    + "#f8fafc 48%, #ede9fe);");
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Pip - Your Cheerful Task Companion");
            fxmlLoader.<MainWindow>getController().setBot(bot);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
