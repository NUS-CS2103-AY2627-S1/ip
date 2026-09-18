import java.io.IOException;

import hamtaro.Hamtaro;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Hamtaro hamtaro = new Hamtaro();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/view/application.css").toExternalForm());
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHamtaro(hamtaro);
            stage.setTitle("Hamtaro");
            stage.setMinWidth(360.0);
            stage.setMinHeight(420.0);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
