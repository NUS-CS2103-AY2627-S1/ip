package littler.gui;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import littler.LittleR;

/**
 * A GUI for LittleR using FXML.
 */
public class Main extends Application {

    private static final String DEFAULT_DATA_FILE_PATH = "./data/littler.txt";

    private LittleR littleR;

    @Override
    public void start(Stage stage) {
        littleR = new LittleR(resolveDataFilePath());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("LittleR");
            stage.setMaximized(true);
            fxmlLoader.<MainWindow>getController().setLittleR(littleR); // inject the LittleR instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines which data file LittleR should use. If the user supplied a
     * command-line argument when launching the app, that path is used;
     * otherwise, a sensible default path is used.
     *
     * @return the resolved data file path
     */
    private String resolveDataFilePath() {
        List<String> args = getParameters().getRaw();
        return args.isEmpty() ? DEFAULT_DATA_FILE_PATH : args.get(0);
    }
}
