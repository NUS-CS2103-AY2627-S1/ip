package beemo;

import java.io.IOException;
import java.nio.file.Path;

import beemo.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Beemo's JavaFX user interface.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Beemo";
    private static final String MAIN_WINDOW_RESOURCE = "/view/MainWindow.fxml";
    private static final String APPLICATION_ICON_RESOURCE = "/images/Beemo.png";
    private static final double MINIMUM_WINDOW_HEIGHT = 360.0;
    private static final double MINIMUM_WINDOW_WIDTH = 420.0;

    private final Beemo beemo = new Beemo(Path.of("data", "beemo.txt"));

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_RESOURCE));
        AnchorPane mainWindow = loader.load();
        Scene scene = new Scene(mainWindow);

        stage.setScene(scene);
        stage.setTitle(WINDOW_TITLE);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream(APPLICATION_ICON_RESOURCE)));
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        loader.<MainWindow>getController().setBeemo(beemo);
        stage.show();
    }
}
