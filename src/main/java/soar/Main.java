package soar;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the JavaFX graphical user interface from its FXML view.
 */
public class Main extends Application {
    private final Soar soar = new Soar();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setSoar(soar);

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Soar");
        stage.setResizable(true);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        stage.show();
    }
}
