package fein;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts Fein's JavaFX application. */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final double INITIAL_WINDOW_WIDTH = 460;
    private static final double INITIAL_WINDOW_HEIGHT = 700;
    private static final double MINIMUM_WINDOW_WIDTH = 360;
    private static final double MINIMUM_WINDOW_HEIGHT = 480;

    /** Loads the FXML view and displays Fein's main window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setFein(new Fein());

        Scene scene = new Scene(mainLayout);
        stage.setTitle("Fein");
        stage.setWidth(INITIAL_WINDOW_WIDTH);
        stage.setHeight(INITIAL_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
