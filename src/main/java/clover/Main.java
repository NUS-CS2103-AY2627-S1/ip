package clover;

import java.io.IOException;

import clover.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A GUI for Clover using FXML.
 */
public class Main extends Application {
    public static final String APPLICATION_TITLE = "Clover";
    private Clover clover = new Clover();

    /**
     * Loads and displays the main FXML view.
     *
     * @param stage the primary window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle(APPLICATION_TITLE);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setClover(clover);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            showStartupFailure(stage);
        }
    }

    /** Shows a usable fallback window when Clover's FXML user interface cannot be loaded. */
    private void showStartupFailure(Stage stage) {
        Label message = new Label("Clover could not load its main window.\n"
                + "Please check that the application files are complete, then restart Clover.");
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> stage.close());
        VBox fallbackLayout = new VBox(12, message, closeButton);
        fallbackLayout.setStyle("-fx-padding: 24;");
        stage.setScene(new Scene(fallbackLayout));
        stage.setMinHeight(180);
        stage.setMinWidth(420);
        stage.show();
    }

    /** Releases Clover's data lock when the JavaFX application exits. */
    @Override
    public void stop() {
        clover.close();
    }
}
