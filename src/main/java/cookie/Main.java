package cookie;

import java.io.IOException;

import cookie.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** The JavaFX application for Cookie, with its layout defined using FXML. */
public class Main extends Application {
    private final Cookie cookie = new Cookie();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Cookie");
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setCookie(cookie);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
