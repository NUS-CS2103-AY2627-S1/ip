package axiom;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import axiom.ui.MainWindow;

/**
 * A GUI for AXIOM using FXML.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_HEIGHT = 400.0;
    private static final double MIN_WINDOW_WIDTH = 340.0;
    private static final double DEFAULT_WINDOW_HEIGHT = 560.0;
    private static final double DEFAULT_WINDOW_WIDTH = 420.0;

    private final Axiom axiom = new Axiom(Axiom.DEFAULT_FILE_PATH);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("AXIOM");
            stage.setResizable(true);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setHeight(DEFAULT_WINDOW_HEIGHT);
            stage.setWidth(DEFAULT_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setAxiom(axiom);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainWindow.fxml", e);
        }
    }
}
