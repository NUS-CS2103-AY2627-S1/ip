package lenzabot.gui;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lenzabot.LenZaBot;
import lenzabot.storage.Storage;

/**
 * Displays LenZaBot's JavaFX user interface.
 */
public class Main extends Application {
    private final LenZaBot lenZaBot = new LenZaBot(new Storage(Path.of("data", "lenzabot.txt")));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setLenZaBot(lenZaBot);

        stage.setTitle("LenZaBot");
        stage.setMinWidth(380);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setScene(new Scene(mainWindow));
        stage.show();
    }
}
