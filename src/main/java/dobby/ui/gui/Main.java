package dobby.ui.gui;

import java.io.IOException;

import dobby.Dobby;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Dobby's JavaFX user interface.
 */
public class Main extends Application {
    private static final double DEFAULT_WIDTH = 620.0;
    private static final double DEFAULT_HEIGHT = 820.0;
    private static final double MINIMUM_WIDTH = 520.0;
    private static final double MINIMUM_HEIGHT = 680.0;

    private final Dobby dobby = new Dobby();

    /**
     * Creates and displays the primary application window.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setDobby(dobby);

        stage.setTitle("Knock Knock - Dobby");
        stage.setWidth(DEFAULT_WIDTH);
        stage.setHeight(DEFAULT_HEIGHT);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }
}
