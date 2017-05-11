package MContact;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        MainController mainController = new MainController();
        MainView mainView = new MainView(stage, mainController);
    }


    public static void main(String[] args) {
        launch(args);
    }
}