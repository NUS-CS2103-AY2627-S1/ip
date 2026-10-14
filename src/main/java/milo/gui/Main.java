package milo.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import milo.Milo;

/**
 * Displays Milo's graphical interface using JavaFX and FXML.
 */
public class Main extends Application {
    private final Milo milo = new Milo();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setMilo(milo);

        stage.setScene(new Scene(root));
        stage.setTitle("NUS BroSpot - Milo");
        stage.setMinHeight(600.0);
        stage.setMinWidth(520.0);
        stage.show();
    }
}
