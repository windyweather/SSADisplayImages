package net.windyweather.ssadisplayimages;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SSADisplayImagesApp extends Application {

    SSADisplayImagesController ssadiController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SSADisplayImagesApp.class.getResource("ssadisplayimages-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);
        stage.setTitle("Screen Shot Archive Display Images");
        stage.setScene(scene);
        stage.show();
        /*
            Call the controller to initialize things
         */
        ssadiController = fxmlLoader.getController();
        ssadiController.SetUpStuff();
    }

    public static void main(String[] args) {
        launch();
    }
}