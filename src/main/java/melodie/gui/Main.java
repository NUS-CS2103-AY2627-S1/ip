package melodie.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import melodie.Melodie;

/**
 * Starts Melodie's JavaFX graphical interface.
 */
public class Main extends Application {

    private final Melodie melodie = new Melodie();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);

            fxmlLoader.<MainWindow>getController().setMelodie(this.melodie);
            stage.setTitle(Melodie.NAME + " — Task Conductor");
            stage.setMinWidth(420);
            stage.setMinHeight(520);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Melodie interface", e);
        }
    }
}
