package nova.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nova.Nova;

/**
 * Displays Nova's JavaFX GUI using its FXML main window.
 */
public class Main extends Application {
    private final Nova nova = new Nova();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);
        scene.getStylesheets().add(Main.class.getResource("/view/Nova.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Nova");
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        fxmlLoader.<MainWindow>getController().setNova(nova);
        stage.show();
    }
}
