package MContact;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        ThreadView threadView = new ThreadView(stage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}