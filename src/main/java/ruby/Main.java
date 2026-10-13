package ruby;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point of Ruby's graphical interface, loading the main window layout.
 */
public class Main extends Application {
    private static final String SAVE_FILE_PATH = "data/ruby.txt";
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";

    private final Ruby ruby = new Ruby(SAVE_FILE_PATH);

    /**
     * Loads the main window, wires it to Ruby, and shows it to the user.
     *
     * @param stage The stage provided by the JavaFX platform.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        AnchorPane anchorPane = fxmlLoader.load();
        MainWindow controller = fxmlLoader.<MainWindow>getController();
        controller.setRuby(ruby);
        controller.setStage(stage);
        stage.setTitle("Ruby");
        stage.setScene(new Scene(anchorPane));
        stage.show();
    }
}
