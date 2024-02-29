package com.example.socialprojectgui.controller;

import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.service.UserService;
import com.example.socialprojectgui.utils.ChangeEventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static java.lang.Thread.sleep;

public class EditUserController {

    private UserService<Integer> userService;
    private User user;
    private Integer idToFind;
    private Stage addStage;
    @FXML
    Label idUser;
    @FXML
    TextField idUserText;
    @FXML
    Label lastNameID;
    @FXML
    Label firstNameID;
    @FXML
    TextField lastNameText;
    @FXML
    TextField firstNameText;
    @FXML
    public TextField emailText;
    @FXML
    public TextField passText;

    private void add()
    {
        Integer id = Integer.parseInt(idUserText.getText());
        String lastName = lastNameText.getText();
        String firstName = firstNameText.getText();
        String email = emailText.getText();
        String pass = passText.getText();
        this.userService.addUser(id,firstName,lastName, email, pass);
        addStage.close();
    }
    private void update(Integer id)
    {

        String lastName = lastNameText.getText();
        String firstName = firstNameText.getText();
        String mail = emailText.getText();
        String pass = passText.getText();
        this.userService.updateUser(id,lastName,firstName, mail, pass);
        addStage.close();
    }
    private void find()
    {
        this.idToFind = Integer.parseInt(idUserText.getText());
        User user1 = this.userService.findUser(this.idToFind);
        lastNameText.setText(user1.getLastName());
        firstNameText.setText(user1.getFirstName());
    }
    @FXML
    private void onDoneClick()
    {

        if(this.user != null)
            this.update(user.getId());
        else if(this.idToFind != -2)
                this.find();
            else this.add();
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"INFO","Operation successfully done");



    }
    private void predefinedSettings(User user,ChangeEventType type)
    {
        if(type == ChangeEventType.FIND)
        {
            firstNameText.setEditable(false);
            lastNameText.setEditable(false);
        }
        if(type == ChangeEventType.UPDATE) {
            idUserText.setText(user.getId().toString());
            firstNameText.setText(user.getFirstName());
            lastNameText.setText(user.getLastName());
            idUserText.setEditable(false);
        }
    }
    public void setService(UserService<Integer> userService, Stage stage, Integer id, User user, ChangeEventType event)
    {

        this.userService = userService;
        this.addStage = stage;
        this.user = user;
        this.idToFind = id;
        this.predefinedSettings(user, event);

    }
}
