package javafx;

import java.io.IOException;

import cbt.Cbt;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storage.Storage;
import ui.Ui;

/** A JavaFX GUI for CBT. */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 220;
    private static final double MINIMUM_WINDOW_WIDTH = 417;

    private final Cbt cbt = new Cbt(new Ui(), new Storage("./data/CBT.txt"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setCbt(cbt);

            stage.setTitle("CBT - Task Navigator");
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setScene(new Scene(mainLayout));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
