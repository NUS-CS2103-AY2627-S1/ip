package alfred.gui;

import java.io.IOException;

import alfred.AlfredTheButler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Runs the graphical version of Alfred as a JavaFX application: reads the
 * layout, puts it on the stage, and hands the window the chatbot to ask.
 */
public class Main extends Application {
    private static final int MIN_WINDOW_HEIGHT = 240;

    private static final int MIN_WINDOW_WIDTH = 360;

    private final AlfredTheButler alfred = new AlfredTheButler();

    /**
     * Builds the window and shows it.
     *
     * @param stage the primary stage JavaFX provides, which is the window itself.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox mainLayout = fxmlLoader.load();
            stage.setScene(new Scene(mainLayout));
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setAlfred(alfred);

            stage.setTitle("Alfred Pennyworth");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);

            stage.show();
            // Focus cannot land on a control that is not yet on the screen.
            mainWindow.focusInput();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read the main window layout", e);
        }
    }
}
