package notmarth;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import notmarth.ui.MainWindow;

/** The JavaFX entry point for the NotMarth tactical assistant. */
public final class Main extends Application {
    private final NotMarthGui notMarth = new NotMarthGui();

    /** Loads and displays the FXML-defined chat window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setNotMarth(notMarth);

            stage.setTitle("NotMarth");
            stage.setMinHeight(420.0);
            stage.setMinWidth(360.0);
            stage.setScene(new Scene(mainWindow, 500.0, 680.0));
            stage.getScene().getStylesheets().add(
                    Main.class.getResource("/css/main.css").toExternalForm());
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
