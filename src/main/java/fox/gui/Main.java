package fox.gui;

import java.io.IOException;

import fox.Fox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Provides Fox's JavaFX application window using an FXML view. */
public class Main extends Application {
    private final Fox fox = new Fox();

    /** Loads the main FXML view and displays the Fox window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setFox(fox);
            stage.setScene(new Scene(mainWindow));
            stage.setTitle("Fox");
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Fox's main window.", exception);
        }
    }
}
