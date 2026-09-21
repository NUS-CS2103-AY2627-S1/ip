package topaz.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import topaz.Topaz;

/**
 * Displays the Gronk graphical user interface.
 */
public class Main extends Application {
    private final Topaz topaz = new Topaz();

    /**
     * Creates and displays the application window.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle(Ui.NAME);
        stage.setMinWidth(360);
        stage.setMinHeight(400);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setTopaz(topaz);
        stage.show();
    }
}
