package MContact;

import javafx.application.Application;
import javafx.stage.Stage;

/** Main application */
public class Main extends Application {

    /**
     * Starts new application on given stage
     * @param stage stage to be used
     * @throws Exception when controller or view throws it
     */
    @Override
    public void start(Stage stage) throws Exception{
        MainController mainController = new MainController();
        new MainView(stage, mainController);
    }

    public static void main(String[] args) {
        launch(args);
    }
}