package moon;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Creates Moon's JavaFX window and connects it to the task-management engine. */
public class MoonApplication extends Application {
    /**
     * Builds and displays Moon's main window.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the main FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MoonApplication.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        assert root != null : "MainWindow.fxml must load a root node.";
        Scene scene = new Scene(root);
        URL stylesheet = MoonApplication.class.getResource("/styles/moon.css");
        assert stylesheet != null : "Moon stylesheet must be available on the classpath.";
        scene.getStylesheets().add(stylesheet.toExternalForm());
        MainWindow controller = loader.getController();
        controller.setMoon(new MoonEngine());
        stage.setTitle("Moon");
        stage.setMinWidth(520);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
    }
}
