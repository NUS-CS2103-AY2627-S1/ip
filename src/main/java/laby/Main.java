package laby;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the JavaFX graphical user interface for Laby. */
public class Main extends Application {

    /**
     * Loads the FXML view, injects the application logic, and shows the main window.
     *
     * @param stage Main JavaFX window.
     * @throws IOException If the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setLaby(new Laby(Paths.get("data", "laby.txt").toString()));

        Scene scene = new Scene(root);
        stage.setTitle("Laby");
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.setScene(scene);
        stage.show();
    }

}
