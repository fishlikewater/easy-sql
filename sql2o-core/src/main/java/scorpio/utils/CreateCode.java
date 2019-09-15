package scorpio.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年09月14日 11:49
 * @since
 **/
public class CreateCode implements Initializable {

    @FXML
    private JFXTextField url;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private JFXTextField driver;

    @FXML
    private JFXTextField table;

    @FXML
    private JFXTextField pack;

    @FXML
    private JFXTextField mapper;

    @FXML
    private JFXTextField basePath;

    @FXML
    private JFXTextField templates;

    @FXML
    private JFXButton mapperBnt;

    @FXML
    private JFXButton modelBnt;

    @FXML
    private JFXButton serviceBnt;

    private ProgressFrom progressFrom;

    private Properties properties = new Properties();

    @FXML
    void createMapper(ActionEvent event) {
    /*    CompletableFuture.runAsync(() -> {
            progressFrom.activateProgressBar();
            try {
                Thread.sleep(5000);
                progressFrom.cancelProgressBar();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });*/
        if (check()) {
            CompletableFuture.runAsync(() -> {
                try {
                    progressFrom.activateProgressBar();
                    CreateTemplate.init(properties);
                    CreateTemplate.createMapper();
                } finally {
                    progressFrom.cancelProgressBar();
                }
            });
        }
    }

    @FXML
    void createModel(ActionEvent event) {
        if (check()) {
            CompletableFuture.runAsync(() -> {
                try {
                    progressFrom.activateProgressBar();
                    CreateTemplate.init(properties);
                    CreateTemplate.createModel();
                } finally {
                    progressFrom.cancelProgressBar();
                }
            });
        }
    }

    @FXML
    void createService(ActionEvent event) {
        if (check()) {
            CompletableFuture.runAsync(() -> {
                try {
                    progressFrom.activateProgressBar();
                    CreateTemplate.init(properties);
                    CreateTemplate.createService();
                } finally {
                    progressFrom.cancelProgressBar();
                }
            });
        }
    }


    private boolean check() {
        if (StringUtils.isBlank(url.getText())) {
            url.requestFocus();
            alertWarning("url不能为空");
            return false;
        }
        if (StringUtils.isBlank(driver.getText())) {
            driver.requestFocus();
            alertWarning("driver不能为空");
            return false;
        }
        if (StringUtils.isBlank(table.getText())) {
            table.requestFocus();
            alertWarning("table不能为空");
            return false;
        }
        if (StringUtils.isBlank(pack.getText())) {
            pack.requestFocus();
            alertWarning("pack不能为空");
            return false;
        }
        if (StringUtils.isBlank(mapper.getText())) {
            mapper.requestFocus();
            alertWarning("mapper不能为空");
            return false;
        }
        if (StringUtils.isBlank(basePath.getText())) {
            basePath.requestFocus();
            alertWarning("basePath不能为空");
            return false;
        }
        if (StringUtils.isBlank(templates.getText())) {
            templates.requestFocus();
            alertWarning("templates不能为空");
            return false;
        }
        properties.setProperty("jdbc.url", url.getText());
        properties.setProperty("jdbc.username", username.getText());
        properties.setProperty("jdbc.password", password.getText());
        properties.setProperty("jdbc.driver", driver.getText());
        properties.setProperty("table", table.getText());
        properties.setProperty("pack", pack.getText());
        properties.setProperty("fileMapper", mapper.getText());
        properties.setProperty("basePath", basePath.getText());
        properties.setProperty("templates", templates.getText());
        return true;
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressFrom = new ProgressFrom(CreateCodeUtils.getPriStage());
    }
}
