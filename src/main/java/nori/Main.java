package nori;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nori.ui.BotAvatar;
import nori.ui.GuiUi;
import nori.ui.MainWindow;

/**
 * Displays Nori's JavaFX user interface.
 */
public class Main extends Application {
    /** The smallest window Nori still lays out properly, verified against its own content. */
    private static final double WINDOW_MIN_HEIGHT = 420;
    private static final double WINDOW_MIN_WIDTH = 480;
    private static final double WINDOW_PREFERRED_HEIGHT = 720;
    private static final double WINDOW_PREFERRED_WIDTH = 920;
    /** Names the product, and the character behind it, wherever the window is listed. */
    private static final String WINDOW_TITLE = "Nori — Your task penguin";

    /** Captures command responses for display in the JavaFX interface. */
    private final GuiUi guiUi = new GuiUi();
    /** Coordinates commands, tasks, and storage for the GUI session. */
    private final Nori nori = new Nori(guiUi);

    /** Creates the application, as required by the JavaFX launcher. */
    public Main() {
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setNori(nori, guiUi);

        Scene scene = new Scene(root, WINDOW_PREFERRED_WIDTH, WINDOW_PREFERRED_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.getIcons().add(BotAvatar.createWindowIcon());
        stage.setMinHeight(WINDOW_MIN_HEIGHT);
        stage.setMinWidth(WINDOW_MIN_WIDTH);
        stage.setScene(scene);
        stage.show();
    }
}
