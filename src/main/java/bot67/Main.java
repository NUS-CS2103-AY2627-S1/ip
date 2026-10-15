package bot67;

import java.io.IOException;

import bot67.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Provides Bot67's JavaFX entry point and loads the FXML-based main window. */
public class Main extends Application {
    private final Bot67 bot = new Bot67();

    /** Creates and displays the main application window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setBot(bot);

        stage.setScene(new Scene(root));
        stage.setTitle("Bot67");
        stage.getIcons().add(new Image(Main.class.getResource("/images/Bot67.png").toExternalForm()));
        stage.setMinHeight(500);
        stage.setMinWidth(420);
        stage.show();
    }
}
