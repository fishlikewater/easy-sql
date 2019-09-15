package scorpio.utils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年09月14日 11:39
 * @since
 **/
public class CreateCodeUtils extends Application {

    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/create_code.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1024, 720);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setTitle("代码生成");
        primaryStage.setMaximized(false);//是否最大化显示
        primaryStage.setAlwaysOnTop(false);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);//禁止缩放
        primaryStage.show();
    }

    public static Stage getPriStage(){
        return stage;
    }
}
