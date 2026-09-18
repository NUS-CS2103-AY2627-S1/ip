package meowmeow.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import meowmeow.MeowMeow;

import java.io.IOException;

/**
 * GUI for MeowMeow using FXML.
 * <p>
 * Reused from the SE-EDU JavaFX tutorial, part 4
 * (https://se-education.org/guides/tutorials/javaFxPart4.html),
 * with minor modifications: renamed from Duke, and added the window title.
 */
public class Main extends Application {

    private MeowMeow meowMeow = new MeowMeow();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("MeowMeow");
            fxmlLoader.<MainWindow>getController().setMeowMeow(meowMeow);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}