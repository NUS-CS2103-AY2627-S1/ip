package yapbot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for YapBot's GUI: loads the main window and starts a
 * {@link YapBotGui} session behind it.
 */
public class Main extends Application {

    private static final String FXML_PATH = "/view/MainWindow.fxml";
    private static final String WINDOW_TITLE = "YapBot";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(FXML_PATH));
        AnchorPane root = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setYapBotGui(new YapBotGui());

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
