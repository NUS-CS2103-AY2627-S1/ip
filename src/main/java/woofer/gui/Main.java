package woofer.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import woofer.WooferService;

/**
 * JavaFX application that displays Woofer's graphical user interface.
 */
public class Main extends Application {
    /**
     * Loads the FXML view, connects it to the application service, and shows the window.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        URL fxmlResource = Main.class.getResource("/view/MainWindow.fxml");
        if (fxmlResource == null) {
            throw new IllegalStateException("Unable to find the Woofer main window resource.");
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            addStylesheet(scene, "/css/main.css");
            addStylesheet(scene, "/css/dialog-box.css");

            MainWindow controller = fxmlLoader.getController();
            controller.setWooferService(new WooferService());

            stage.setTitle("Woofer — Your task buddy");
            stage.setMinWidth(400.0);
            stage.setMinHeight(420.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Woofer GUI.", exception);
        }
    }

    /**
     * Adds a stylesheet loaded from the classpath to a scene.
     *
     * @param scene scene receiving the stylesheet.
     * @param resourcePath absolute classpath path to the stylesheet.
     */
    private void addStylesheet(Scene scene, String resourcePath) {
        URL stylesheet = Main.class.getResource(resourcePath);
        if (stylesheet == null) {
            throw new IllegalStateException("Unable to find stylesheet: " + resourcePath);
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());
    }
}
