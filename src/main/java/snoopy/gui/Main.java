package snoopy.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import snoopy.Snoopy;

/**
 * Displays Snoopy's graphical user interface using FXML.
 */
public class Main extends Application {
    private final Snoopy snoopy = new Snoopy();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setSnoopy(snoopy);

        stage.setTitle(snoopy.getDisplayName());
        stage.setMinHeight(480.0);
        stage.setMinWidth(420.0);
        stage.setScene(scene);
        stage.show();
    }
}
