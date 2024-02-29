package com.example.socialprojectgui.controller;

import com.example.socialprojectgui.HelloApplication;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Exceptions.RepoExceptions;
import com.example.socialprojectgui.repository.Page;
import com.example.socialprojectgui.repository.Pageable;
import com.example.socialprojectgui.service.FriendRequestService;
import com.example.socialprojectgui.service.MessageService;
import com.example.socialprojectgui.service.UserService;
import com.example.socialprojectgui.utils.ChangeEventType;
import com.example.socialprojectgui.utils.Event;
import com.example.socialprojectgui.utils.Observer;
import com.example.socialprojectgui.utils.UserChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer {

    @FXML
    public Button nextButton;
    @FXML
    public Button previousButton;
    @FXML
    public Spinner<Integer> spinnerId;
    UserService<Integer> userService;
    private User thisUser;
    FriendRequestService friendRequestService;
    private MessageService messageService;
    String myEmail;
    String myPass;
    Integer myId;
    String nameToFind;
    private String myName;
    @FXML
    public Label nameLabel;
    @FXML
    public TableView<Friend> friendsTable;

    @FXML
    public TableColumn<Friend,String> friendsID;
    @FXML
    public TextField searchText;
    @FXML
    public TableView<User> searchTable;
    @FXML
    public TableColumn<User, String> firstName;
    @FXML
    public TableColumn<User, String> lastName;
    @FXML
    public TableColumn<User, String> email;
    @FXML
    public Button friendRequestbutton;
    @FXML
    public TableView<FriendRequest> frToTable;
    @FXML
    public TableColumn<FriendRequest,String> frRequestToId;
    @FXML
    public TableColumn<FriendRequest,String> statusColumnTo;
    @FXML
    public TableView<FriendRequest> frFromTable;
    @FXML
    public TableColumn<FriendRequest,String> frRequestFromId;
    @FXML
    public TableColumn<FriendRequest,String> statusColumnFrom;

    ObservableList<User> usersModel = FXCollections.observableArrayList();
    ObservableList<Friend> friendsModel = FXCollections.observableArrayList();
    ObservableList<FriendRequest> friendRequestsToModel = FXCollections.observableArrayList();
    ObservableList<FriendRequest> friendRequestsFromModel = FXCollections.observableArrayList();


    @FXML
    public void initialize()
    {

        friendsTable.setItems(friendsModel);
        friendsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        friendsID.setCellValueFactory(new PropertyValueFactory<>("nameFriendsWith"));

        searchTable.setItems(usersModel);
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        email.setCellValueFactory(new PropertyValueFactory<>("userEmail"));

        frToTable.setItems(friendRequestsToModel);
        frRequestToId.setCellValueFactory(new PropertyValueFactory<>("nameTo"));
        statusColumnTo.setCellValueFactory(new PropertyValueFactory<>("status"));

        frFromTable.setItems(friendRequestsFromModel);
        frRequestFromId.setCellValueFactory(new PropertyValueFactory<>("nameFrom"));
        statusColumnFrom.setCellValueFactory(new PropertyValueFactory<>("status"));




    }

    private void initModel()
    {


        usersModel.setAll(StreamSupport.stream(userService.findByLastName(this.nameToFind).spliterator(),false).collect(Collectors.toList()));
        friendRequestsToModel.setAll(StreamSupport.stream(friendRequestService.getRequests(this.myId, "to").spliterator(),false).collect(Collectors.toList()));
        friendRequestsFromModel.setAll(StreamSupport.stream(friendRequestService.getRequests(this.myId,"from").spliterator(),false).collect(Collectors.toList()));

        Page<Friend> friendsPage  = this.userService.computePage(this.myId);
        friendsModel.setAll(StreamSupport.stream(friendsPage.getElementsOfPage().spliterator(),false).collect(Collectors.toList()));

        this.userService.updatePageConfig(friendsPage,previousButton,nextButton,spinnerId);


    }
    public void setService(UserService service,FriendRequestService friendRequestService, MessageService messageService, Stage stage, String myEmail, String pass)
    {

        this.userService = service;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
//        this.friendRequestService.addObserver(this);
//        this.userService.addObserver(this);
        this.myEmail = myEmail;
        this.thisUser = this.userService.findUser(this.userService.findUserByMail(this.myEmail).getLeft());

        Tuple<Integer,String> info = service.findUserByMail(this.myEmail);
        this.myId = info.getLeft();
        this.myPass = info.getRight();
        this.userService.verifyPassword(this.myPass,pass);


        User me = this.userService.findUser(this.myId);
        this.myName = me.getLastName() + " " + me.getFirstName() + " profile";
        nameLabel.setText(this.myName);
        this.nameToFind = null;
        this.searchText.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER)
            {
                update(null);
            }
        });
        this.userService.initSpinner(spinnerId);

        update(null);
        //initModel();
    }

    public void onDenyRequestButton()
    {
        try {

            FriendRequest friendRequest = this.frFromTable.getSelectionModel().getSelectedItem();
            if (friendRequest == null)
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a friend request!");
            if(friendRequest.getStatus().equals("pending")) {
                this.friendRequestService.update(friendRequest, "rejected");
            }
            else
                MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", "Friend request already answered");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void onAcceptRequestButton()
    {

        try {
            FriendRequest friendRequest = this.frFromTable.getSelectionModel().getSelectedItem();
            if (friendRequest == null)
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a friend request!");
            if(friendRequest.getStatus().equals("pending")) {
                try {
                    this.userService.addFriend(friendRequest.getIdFromWhom(), friendRequest.getIdToWhom());
                } catch (DBExceptions e) {
                    MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", e.getMessage());
                }
                this.friendRequestService.update(friendRequest,"accepted");
            }
            else
                MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", "Friend request already answered");
        } catch (Exception e) {
            throw new RuntimeException(e);

        }


    }

    public void onFriendRequestButton() {
        try {
            User user = this.searchTable.getSelectionModel().getSelectedItem();
            if (user == null)
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a user!");
            User u = this.userService.findUser(myId);
            this.myName = u.getLastName() +" "+u.getFirstName();
            try {
                this.friendRequestService.save(this.myId, user.getId(), this.myName, user.getLastName() + " " + user.getFirstName());
            }
            catch (DBExceptions e)
            {
                MessageAlert.showMessage(null,Alert.AlertType.WARNING,"Warning",e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void onChatButton()
    {
        if(this.friendsTable.getSelectionModel().getSelectedItems().size() == 0)
        {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Warning","Please select minimum one friend");
        }

        else{
            ObservableList<Friend> selectedFriends = this.friendsTable.getSelectionModel().getSelectedItems();
            List<User> friends = new ArrayList<>();
            selectedFriends.forEach(friend -> {
                User u = this.userService.findUser(friend.getIdFriend());
                friends.add(u);
            });
            friends.forEach(friend->{
                Stage stage1 = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/ChatView.fxml"));
                try {
                    Scene scene1 = new Scene(fxmlLoader.load());
                    ChatController chat = fxmlLoader.getController();
                    this.messageService.addObserver(chat);
                    chat.setService(this.messageService, this.thisUser, friend);
                    stage1.setScene(scene1);
                    stage1.show();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            });

        }
    }
    @FXML
    private void onDeleteFriendshipClick()
    {
        Friend friend = this.friendsTable.getSelectionModel().getSelectedItem();
        if(friend == null)
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Please select a friendship!");
        else {
            List<FriendRequest> friendRequests = this.friendRequestService.getRequests(this.myId,"to");
            int id = 0;
            for(FriendRequest friendRequest:friendRequests)
            {
                if(friendRequest.getIdFromWhom() == friend.getIdFriend())
                    id = friendRequest.getId();
            }
            int id2 = 0;
            List<FriendRequest> friendRequests1 = this.friendRequestService.getRequests(this.myId,"from");
            for(FriendRequest friendRequest:friendRequests1)
            {
                if(friendRequest.getIdFromWhom() == friend.getIdFriend())
                    id2 = friendRequest.getId();
            }

            if(id!=0)
                this.friendRequestService.delete(id);
            if(id2!=0)
                this.friendRequestService.delete(id2);
            this.userService.removeFriend(friend.getIdUser(), friend.getIdFriend());
        }
    }
    @FXML
    public void onNewMessageButton()
    {
        if(this.friendsTable.getSelectionModel().getSelectedItems().size() == 0)
        {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Warning","Please select minimum one friend");
        }

        else {
            Stage stage = new Stage();
            stage.setTitle("New message");
            Label label = new Label("Message:");
            TextField textField = new TextField();
            HBox hBox = new HBox(label, textField);
            Button sendButton = new Button("Send");
            VBox vBox = new VBox(hBox, sendButton);
            Scene scene = new Scene(vBox);
            stage.setScene(scene);
            ObservableList<Friend> selectedFriends = this.friendsTable.getSelectionModel().getSelectedItems();
            List<User> friends = new ArrayList<>();
            selectedFriends.forEach(friend -> {
                User u = this.userService.findUser(friend.getIdFriend());
                friends.add(u);
            });
            sendButton.setOnAction(event -> {
                if(textField.getText().isEmpty())
                {
                    MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Warning","Please type a message");

                }
                else{

                    messageService.saveMessage(this.thisUser, friends, textField.getText());
                    friends.forEach(friend->{
                        Stage stage1 = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/ChatView.fxml"));
                        try {
                            Scene scene1 = new Scene(fxmlLoader.load());
                            ChatController chat = fxmlLoader.getController();
                            this.messageService.addObserver(chat);
                            chat.setService(this.messageService, this.thisUser, friend);
                            stage1.setScene(scene1);
                            stage1.show();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    });
                    stage.close();
                }
            });
            stage.show();
        }



    }
    public void onNext()
    {
        this.userService.increasePage();
        update(new UserChangeEvent(ChangeEventType.UPDATE,null,null));
    }
    public void onPrevious()
    {
        this.userService.decreasePage();
        update(new UserChangeEvent(ChangeEventType.UPDATE,null,null));
    }
    @Override
    public void update(Event event) {

        this.nameToFind = this.searchText.getText();
        if(this.searchText.getText().isEmpty())
            this.nameToFind = " ";
        this.initModel();

    }


}
