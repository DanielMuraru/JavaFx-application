package com.example.socialprojectgui.controller;

import com.example.socialprojectgui.HelloApplication;
import com.example.socialprojectgui.service.UserService;
import com.example.socialprojectgui.utils.ChangeEventType;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

    private UserService userService;
    @FXML
    public VBox layout;
    @FXML
    public Label emailLabel;
    @FXML
    public Label passLabel;
    HelloApplication helloApplication;
    @FXML
    public Button loginButton;


    @FXML
    TextField emailText;
    @FXML
    PasswordField passwordText;
    Stage stage;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.passwordText.setVisible(true);
        this.loginButton.setAlignment(Pos.CENTER);
    }
    public void determineUser() throws IOException {
        String user = emailText.getText();
        String pass = passwordText.getText();
        if(user.equals("admin") && pass.equals("root"))
        {
            //this.stage.close();
            this.helloApplication.loginHandler("admin","root");
            emailText.clear();
            passwordText.clear();

        }
        else {

            //this.stage.close();
            this.helloApplication.loginHandler(user,pass);
            emailText.clear();
            passwordText.clear();
        }
    }

    public void setApplication(HelloApplication helloApplication, Stage stage) {
        this.helloApplication = helloApplication;
        this.stage = stage;
        this.stage.getScene().setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER)
            {
                try {
                    this.determineUser();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }


}
