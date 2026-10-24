package clara.gui;

import java.io.IOException;

import clara.Clara;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// AI-assisted JavaFX GUI implementation. See CITATIONS.md [C-013].
// NOTE: AI-assisted GUI polish. See CITATIONS.md [C-019].

/**
 * JavaFX GUI for Clara.
 */
public class Main extends Application {

    private final Clara clara = new Clara();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setClara(clara);
            stage.setTitle("Clara");
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setResizable(true);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Clara's main window.", exception);
        }
    }
}
