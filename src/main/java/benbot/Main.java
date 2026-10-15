package benbot;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Starts and owns BenBot's JavaFX graphical interface. */
public class Main extends Application {
    private static final double INITIAL_WINDOW_WIDTH = 520;
    private static final double INITIAL_WINDOW_HEIGHT = 680;
    private static final double MINIMUM_WINDOW_WIDTH = 440;
    private static final double MINIMUM_WINDOW_HEIGHT = 560;

    /** The chatbot instance shared with the graphical controller. */
    private final BenBot benBot = new BenBot();

    /**
     * Loads the FXML view, connects it to BenBot, and displays the primary window.
     *
     * @param stage the primary JavaFX window.
     * @throws IOException if an FXML or stylesheet resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        benBot.load();

        URL view = requireResource("/view/MainWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(view);
        Parent root = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setBenBot(benBot);

        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        scene.getStylesheets().add(requireResource("/styles/main.css").toExternalForm());

        stage.setTitle("BenBot");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(controller::handleCloseRequest);
        stage.show();
    }

    /** Returns a required classpath resource or fails with a helpful message. */
    private URL requireResource(String path) {
        URL resource = Main.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Missing application resource: " + path);
        }
        return resource;
    }
}
