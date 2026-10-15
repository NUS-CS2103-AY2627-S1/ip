package lumi;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lumi.ui.MainWindow;

/**
 * Displays Lumi's JavaFX user interface using FXML.
 */
public class Main extends Application {
    private final Lumi lumi = new Lumi();

    /**
     * Creates and displays the primary application window.
     *
     * @param stage Primary stage provided by JavaFX.
     * @throws IOException If the main window layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setLumi(lumi);
        stage.setTitle("Lumi — Task Navigator");
        stage.setMinHeight(360);
        stage.setMinWidth(320);
        stage.setResizable(true);
        stage.show();
    }
}
