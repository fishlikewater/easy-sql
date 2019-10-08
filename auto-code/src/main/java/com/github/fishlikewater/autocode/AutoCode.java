package com.github.fishlikewater.autocode;

import com.fishlikewater.kit.jfx.ApplicationLanuch;
import com.fishlikewater.kit.jfx.SplashScreen;
import javafx.stage.Stage;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年09月14日 11:39
 * @since
 **/
public class AutoCode extends ApplicationLanuch {

    private static Stage stage;

    public static void main(String[] args) {
        lanuch(AutoCode.class, AutoCodeView.class, new SplashScreen(), args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
    }

    public static Stage getPriStage(){
        return stage;
    }
}
