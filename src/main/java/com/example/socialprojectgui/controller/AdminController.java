package com.example.socialprojectgui.controller;

import com.example.socialprojectgui.HelloApplication;
import com.example.socialprojectgui.domain.Friend;
import com.example.socialprojectgui.domain.FriendRequest;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.service.FriendRequestService;
import com.example.socialprojectgui.service.MessageService;
import com.example.socialprojectgui.service.UserService;
import com.example.socialprojectgui.utils.ChangeEventType;
import com.example.socialprojectgui.utils.Event;
import com.example.socialprojectgui.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController implements Observer {

    private UserService<Integer> userService;
    private MessageService messageService;
    private FriendRequestService friendRequestService;
    @FXML
    TableView<User> UserTableView;
    @FXML
    TableColumn<User,String> lastName;
    @FXML
    TableColumn<User,String> firstName;
    @FXML
    TableColumn<User,String> emailID;
    @FXML
    TableColumn<User,String> passwordID;
    ObservableList<User> usersModel = FXCollections.observableArrayList();


    @FXML
    TableView<Friend> FriendshipTableView;
    @FXML
    TableColumn<Friend,String> userName;
    @FXML
    TableColumn<Friend,String> friendsWith;
    @FXML
    TableColumn<Friend, LocalDate> friendsFrom;
    ObservableList<Friend> friendshipsModel = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequest> FriendRequestTableView;
    @FXML
    TableColumn<FriendRequest,String> fromWhom;
    @FXML
    TableColumn<FriendRequest,String> toWhom;
    @FXML
    TableColumn<FriendRequest,String> status;
    ObservableList<FriendRequest> friendRequestsModel = FXCollections.observableArrayList();


    @FXML
    public void initialize()
    {

        UserTableView.setItems(usersModel);
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        emailID.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        passwordID.setCellValueFactory(new PropertyValueFactory<>("userPassword"));

        FriendshipTableView.setItems(friendshipsModel);
        userName.setCellValueFactory(new PropertyValueFactory<>("nameUser"));
        friendsWith.setCellValueFactory(new PropertyValueFactory<>("nameFriendsWith"));
        friendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));

        FriendRequestTableView.setItems(friendRequestsModel);
        fromWhom.setCellValueFactory(new PropertyValueFactory<>("nameFrom"));
        toWhom.setCellValueFactory(new PropertyValueFactory<>("nameTo"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));


    }
    private void initModel()
    {
        usersModel.setAll(StreamSupport.stream(userService.getAll().spliterator(),false).collect(Collectors.toList()));
        friendshipsModel.setAll(StreamSupport.stream(userService.getAllFriendships().spliterator(),false).collect(Collectors.toList()));
        friendRequestsModel.setAll(StreamSupport.stream(friendRequestService.getAllRequests().spliterator(),false).collect(Collectors.toList()));
    }
    public void setUserService(UserService<Integer> userService,FriendRequestService friendRequestService, MessageService messageService, String pass)
    {
        if(!pass.equals("root"))
            throw new IllegalArgumentException("Invalid password");
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.friendRequestService.addObserver(this);
        this.userService.addObserver(this);
        initModel();
    }
    @FXML
    private void onAddButtonClick()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/AddUserView.fxml"));

            AnchorPane root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setWidth(512);
            stage.setHeight(512);
            stage.setTitle("User Info");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            EditUserController editUserController = fxmlLoader.getController();
            editUserController.setService(this.userService,stage,-2,null, ChangeEventType.ADD);

            stage.show();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void onDeleteFriendshipClick()
    {
        Friend friend = this.FriendshipTableView.getSelectionModel().getSelectedItem();
        if(friend == null)
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Please select a friendship!");

        this.userService.removeFriend(friend.getIdUser(),friend.getIdFriend());
    }
    @FXML
    private void onDeleteButtonClick()
    {
        User user = this.UserTableView.getSelectionModel().getSelectedItem();
        if(user == null)
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Please select a user!");

        this.userService.removeUser(user.getId());
    }
    @FXML
    private void onFindButtonClick()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/AddUserView.fxml"));

            AnchorPane root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setWidth(512);
            stage.setHeight(512);
            stage.setTitle("User Info");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            EditUserController editUserController = fxmlLoader.getController();
            editUserController.setService(this.userService,stage, -1,null, ChangeEventType.FIND);

            stage.show();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onUpdateButtonClick()
    {
       try {
           User user = this.UserTableView.getSelectionModel().getSelectedItem();
           if(user == null)
               MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Please select a user!");

           FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/AddUserView.fxml"));

           AnchorPane root = fxmlLoader.load();
           Stage stage = new Stage();
           stage.setWidth(512);
           stage.setHeight(512);
           stage.setTitle("User Info");
           stage.initModality(Modality.WINDOW_MODAL);
           Scene scene = new Scene(root);
           stage.setScene(scene);
           EditUserController editUserController = fxmlLoader.getController();
           editUserController.setService(this.userService, stage,null, user, ChangeEventType.UPDATE);
           stage.show();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }

    }
    @FXML
    public void onshowChatButton()
    {
        Stage stage = new Stage();
        TextField textField = new TextField("First user id");
        TextField textField2 = new TextField("Second user id");
        Button done = new Button("Done");
        VBox vBox =  new VBox(textField,textField2,done);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        done.setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/ChatView.fxml"));
            try {
                Stage stage1 = new Stage();
                Scene scene1 = new Scene(fxmlLoader.load());
                ChatController chat = fxmlLoader.getController();
                User user1 = this.userService.findUser(Integer.parseInt(textField.getText()));
                User user2 = this.userService.findUser(Integer.parseInt(textField2.getText()));
                chat.setService(this.messageService, user1, user2);
                stage1.setScene(scene1);
                stage1.show();
                stage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stage.show();
    }
    @Override
    public void update(Event event) {
        initModel();
    }
}
