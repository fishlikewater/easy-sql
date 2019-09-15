package scorpio.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年09月14日 22:00
 * @since
 **/

public class ProgressFrom {
    private Stage dialogStage;
    private Stage primaryStage;
    private ProgressIndicator progressIndicator;

    public ProgressFrom(Stage primaryStage) {
        this.primaryStage = primaryStage;
        dialogStage = new Stage();
        progressIndicator = new ProgressIndicator();
        // 窗口父子关系
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        //dialogStage.sizeToScene();
        Label label = new Label("操作中...");
        label.setTextFill(Color.BLUE);
        progressIndicator.setProgress(-1);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().addAll(progressIndicator,label);
        Scene scene = new Scene(vBox);
        scene.setFill(null);
        dialogStage.setScene(scene);
    }

    public void activateProgressBar() {
        double x = primaryStage.getX();
        double y = primaryStage.getY();
        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();
        Platform.runLater(()->{
            dialogStage.setX(x+ width /2);
            dialogStage.setY(y+height/2-10);
            dialogStage.show();
        });
    }

    public Stage getDialogStage(){
        return dialogStage;
    }

    public void cancelProgressBar() {
        Platform.runLater(()->{
            dialogStage.close();
        });
    }
}
