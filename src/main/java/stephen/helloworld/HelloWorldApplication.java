package stephen.helloworld;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Minimal FXML-based application used to verify the JavaFX setup independently. */
public class HelloWorldApplication extends Application {
    /** Loads and displays the setup-verification view. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelloWorld.fxml"));
        Scene scene = new Scene(loader.load(), 360, 180);
        scene.getStylesheets().add(getClass().getResource("/css/hello-world.css").toExternalForm());
        stage.setTitle("JavaFX Setup Check");
        stage.setScene(scene);
        stage.show();
    }
}
