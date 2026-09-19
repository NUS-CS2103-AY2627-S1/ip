package chai;

import java.io.IOException;

import chai.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays Chai's JavaFX user interface. */
public class Main extends Application {
    /** The chatbot session shared with the window controller. */
    private final Chai chai = new Chai();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setChai(chai);

        stage.setScene(new Scene(root));
        stage.setTitle("Chai — Calm Task Companion");
        stage.setMinHeight(500);
        stage.setMinWidth(420);
        stage.setHeight(700);
        stage.setWidth(520);
        stage.show();
    }
}
