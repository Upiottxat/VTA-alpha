package com.monster.videotoaudio.vta;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    Path path=new Path();

    public Scene getScene() {
        return scene;
    }

    Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("screen.fxml"));
        scene = new Scene(fxmlLoader.load(), 813, 542);
        stage.setTitle("VTA (Video to Audio convertor)");
        stage.setScene(scene);
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(false);
        stage.getIcons().add(new Image(Application.class.getResource("Drawable/VTA.png").openStream()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}