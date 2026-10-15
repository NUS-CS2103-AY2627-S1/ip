package heisenberg;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the FXML-based graphical interface for Heisenberg. */
public class Main extends Application {
    private final Heisenberg heisenberg = new Heisenberg();

    /** Loads the main FXML view, injects the chatbot, and displays the window. */
    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Missing FXML resource: /view/MainWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
        AnchorPane mainWindow = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setHeisenberg(heisenberg);

        stage.setTitle("Heisenberg");
        stage.setMinWidth(460);
        stage.setMinHeight(480);
        stage.setResizable(true);
        Scene scene = new Scene(mainWindow);
        scene.getStylesheets().add(Objects.requireNonNull(
                Main.class.getResource("/view/main.css"),
                "Missing stylesheet resource: /view/main.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
