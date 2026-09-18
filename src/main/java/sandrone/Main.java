package sandrone;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sandrone.ui.MainWindow;

/** Creates and displays Sandrone's JavaFX window. */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/tasks.txt";

    /** Loads the main window and injects Sandrone's application logic into it. */
    @Override
    public void start(Stage stage) throws IOException {
        Sandrone sandrone = new Sandrone(DEFAULT_FILE_PATH);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Sandrone");
        stage.setMinWidth(420);
        stage.setMinHeight(500);
        stage.setScene(scene);
        loader.<MainWindow>getController().setSandrone(sandrone);
        stage.show();
    }
}
