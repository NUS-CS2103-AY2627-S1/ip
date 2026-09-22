package steph;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX {@link Application} entry point. Builds the window from
 * {@code MainWindow.fxml}, hands the {@link MainWindow} controller a
 * {@link Steph} instance to answer commands, and shows the stage.
 *
 * <p>The same {@code ./data/steph.txt} save file as the console version is
 * used, so tasks are shared between the two front ends.
 */
public class Main extends Application {

    private final Steph steph = new Steph("./data/steph.txt");

    /**
     * Builds and shows the main window.
     *
     * @param stage The primary stage supplied by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Steph");
            // Lower bounds so the anchored layout never collapses past a usable size.
            stage.setMinWidth(417.0);
            stage.setMinHeight(320.0);

            MainWindow controller = fxmlLoader.getController();
            controller.setSteph(steph);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
