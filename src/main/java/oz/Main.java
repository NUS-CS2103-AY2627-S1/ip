package oz;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A graphical user interface application for Oz using JavaFX.
 */
public class Main extends Application {

    /** Application title displayed on the primary stage window. */
    public static final String APP_TITLE = "Oz";

    /** Classpath location of the main window layout. */
    private static final String MAIN_WINDOW_FXML_PATH = "/view/MainWindow.fxml";

    /** Classpath location of the application icon. */
    private static final String APP_ICON_IMAGE_PATH = "/images/icon.png";

    /** Minimum stage width that keeps the command controls usable. */
    private static final double MINIMUM_STAGE_WIDTH = 320;

    /** Minimum stage height that leaves room for conversation history. */
    private static final double MINIMUM_STAGE_HEIGHT = 400;

    /** The Oz chatbot instance handling application logic. */
    private final Oz oz = new Oz(Oz.DEFAULT_STORAGE_PATH);

    /**
     * Initializes and configures the main GUI layout and displays the primary
     * stage.
     *
     * @param stage Primary stage for this JavaFX application.
     * @throws IOException If the main window layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML_PATH));
        AnchorPane mainLayout = fxmlLoader.load();
        configureStage(stage, mainLayout);

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setOz(this.oz);
        stage.show();
    }

    /**
     * Applies the application scene and window settings to the primary stage.
     *
     * @param stage Primary application stage.
     * @param mainLayout Loaded main window layout.
     */
    private void configureStage(Stage stage, AnchorPane mainLayout) {
        Scene scene = new Scene(mainLayout);
        stage.setTitle(APP_TITLE);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream(APP_ICON_IMAGE_PATH)));
        stage.setScene(scene);
        stage.setMinWidth(MINIMUM_STAGE_WIDTH);
        stage.setMinHeight(MINIMUM_STAGE_HEIGHT);
        stage.setResizable(true);
    }
}
