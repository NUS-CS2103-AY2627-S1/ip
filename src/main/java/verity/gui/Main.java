package verity.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import verity.Verity;

/**
 * A GUI for Verity using FXML.
 */
public class Main extends Application {

    private static final String SAVE_FILE_PATH = "data/verity.txt";

    private Verity verity = new Verity(SAVE_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Verity");
            stage.setMinHeight(320);
            stage.setMinWidth(450);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setVerity(verity);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
