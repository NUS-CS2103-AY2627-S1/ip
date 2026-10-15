package jiji.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jiji.Jiji;
import jiji.ui.PersonalityBank;

/**
 * A GUI for Jiji using FXML.
 * <p>
 * Adapted and enhanced from the SE-EDU JavaFX Tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
 */
public class Main extends Application {

    private final Jiji jiji = new Jiji();

    @Override
    public void start(Stage stage) {
        try {
            PersonalityBank.setRandomized(true);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Jiji ₍^ ᵕ ᵕ ^₎ - Cozy Feline Assistant");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaJiji.png")));
            stage.setMinHeight(400);
            stage.setMinWidth(420);
            stage.setWidth(450);
            stage.setHeight(650);
            fxmlLoader.<MainWindow>getController().setJiji(jiji);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
