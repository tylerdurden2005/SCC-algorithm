package application;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        settingsInterface(primaryStage);
        primaryStage.show();
    }

    private void settingsInterface(Stage primaryStage){
        primaryStage.setTitle("Kosaraju's Algorithm");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);

        CustomInterface interfaceApp = new CustomInterface();
        InterfaceController controller = new InterfaceController(interfaceApp, primaryStage);
        GridPane rootPane = interfaceApp.getRootPane();

        Scene scene = new Scene(rootPane, 900, 700);
        primaryStage.setScene(scene);
    }
}





