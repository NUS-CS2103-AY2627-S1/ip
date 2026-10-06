package com.gihun456.gui;

import java.io.IOException;

import com.gihun456.Gihun456;
import com.gihun456.GihunException;
import com.gihun456.ui.UiMessages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Gihun456 using FXML.
 */
public class Main extends Application {

    private Gihun456 gihun = new Gihun456();

    @Override
    public void start(Stage stage) {
        String startupError = null;
        try {
            gihun.loadTasks();
        } catch (GihunException e) {
            startupError = e.getMessage();
        }

        stage.setMinHeight(420);
        stage.setMinWidth(600);
        stage.setWidth(800);
        stage.setHeight(1000);
        stage.setTitle(UiMessages.PRODUCT_NAME);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/GihunBot.png")));

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setGihun(gihun);
            if (startupError != null) {
                mainWindow.showError(startupError);
            }
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start " + UiMessages.PRODUCT_NAME + ".", e);
        }
    }
}
