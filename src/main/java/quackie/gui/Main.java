package quackie.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import quackie.Quackie;

/**
 * Displays Quackie's JavaFX interface.
 */
public class Main extends Application {
    private final Quackie quackie = new Quackie();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);

        stage.setTitle("Quackie");
        stage.setMinHeight(560.0);
        stage.setMinWidth(480.0);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setQuackie(quackie);
        stage.show();
    }
}
