package ekud;

import java.io.IOException;

import ekud.storage.Storage;
import ekud.ui.MainWindow;
import ekud.ui.Ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Ekud using FXML.
 */
public class Main extends Application {
    private final Ekud ekud = new Ekud(Ui.forGui(), new Storage(Storage.DEFAULT_PATH));

    /**
     * Loads the main window and shows the stage.
     *
     * @param stage primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Ekud");
            stage.setResizable(true);
            stage.setMinHeight(280);
            stage.setMinWidth(340);
            fxmlLoader.<MainWindow>getController().setEkud(ekud);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the main window.", e);
        }
    }
}
