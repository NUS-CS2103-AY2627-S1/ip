package tungtung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** Displays a minimal JavaFX window containing a greeting. */
public class HelloWorld extends Application {

    /**
     * Creates and displays the JavaFX scene for the application.
     *
     * @param stage primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!");
        Scene scene = new Scene(helloWorld);
        stage.setScene(scene);
        stage.show();
    }
}
