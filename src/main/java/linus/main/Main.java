package linus.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import linus.Linus;
import linus.invalidtaskexception.InvalidTaskException;
import linus.mainwindow.MainWindow;
import linus.ui.Ui;

/**
 * A GUI for Linus using FXML.
 */
public class Main extends Application {
    private static final String STORAGE_FILEPATH = "data/tasklist.txt";

    @Override
    public void start(Stage stage) {
        try {
            Linus linus = new Linus(STORAGE_FILEPATH);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Linus");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setLinus(linus); // inject the Linus instance
            stage.show();
        } catch (IOException | InvalidTaskException e) {
            Ui.display(e.getMessage());
            Platform.exit();
        }
    }
}



