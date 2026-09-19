package benji;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Configures and displays BENJI's JavaFX application window. */
public class Main extends Application {
    private final Benji benji = new Benji();

    /** Loads the main window and connects it to BENJI's command processor. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));

            Scene scene = new Scene(fxmlLoader.load());

            fxmlLoader.<MainWindow>getController().setBenji(benji);

            stage.setMinWidth(420);
            stage.setMinHeight(600);
            stage.setMaxWidth(800);
            stage.setMaxHeight(1200);
            stage.setResizable(true);
            stage.setTitle("BENJI");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load BENJI's main window.", e);
        }
    }
}
