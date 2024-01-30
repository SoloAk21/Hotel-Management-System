/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.hotelmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class HotelVerwaltungFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Launcher.fxml"));
        Parent root = (Parent) loader.load();
        LauncherController controller = (LauncherController)loader.getController();
        controller.setStage(stage);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}