package lobby.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lobby.Lobby;

/**
 * Displays the Lobby graphical user interface.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/lobby.txt";

    private final Lobby lobby;

    /**
     * Creates a GUI connected to Lobby's default save file.
     */
    public Main() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a GUI connected to the given save file.
     *
     * @param filePath location used to load and save tasks.
     */
    public Main(String filePath) {
        this.lobby = new Lobby(filePath);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setLobby(lobby);

        stage.setTitle("Lobby");
        stage.setMinWidth(440);
        stage.setMinHeight(620);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
