package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        //loader.getClass().getResource("style.css");
        Parent root = loader.load();

        MainController controller = (MainController)loader.getController();
        controller.setStage(primaryStage);



        Scene scene = new Scene(root);
        primaryStage.setTitle("IGU Application");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
