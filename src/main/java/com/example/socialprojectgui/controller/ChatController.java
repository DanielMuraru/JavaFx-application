package com.example.socialprojectgui.controller;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.Message;
import com.example.socialprojectgui.domain.Tuple;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.service.MessageService;
import com.example.socialprojectgui.utils.Observer;
import com.example.socialprojectgui.utils.UserChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Observer<UserChangeEvent> {
    @FXML
    public Label idLabel;
    private MessageService messageService;
    private User sender;
    private User replier;
    @FXML
    public ListView<MessageDTO> idListview;
    @FXML
    public TextField idTextField;
    @FXML
    public Button idSendButton;
    private ObservableList<MessageDTO> messagesModel = FXCollections.observableArrayList();

    public  void initialize()
    {
        idListview.setItems(messagesModel);
        idListview.setCellFactory(message -> new ListCell<>(){
            @Override
            protected void updateItem(MessageDTO messageDTO, boolean empty) {
                super.updateItem(messageDTO, empty);

                if (empty || messageDTO == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text message = new Text(messageDTO.getText());
                    if(messageDTO.getMessageToRespond() != null) {
                        Text repliedMessage = new Text("Reply:" + messageDTO.getMessageToRespond());
                        repliedMessage.setOpacity(0.5);
                        VBox vbox =new VBox(repliedMessage,message);
                        if(messageDTO.getIdSender().equals(replier.getId())){
                            vbox.setAlignment(Pos.CENTER_RIGHT);

                        } else {
                            vbox.setAlignment(Pos.CENTER_LEFT);

                        }

                        setGraphic(vbox);
                    }
                    else {
                        VBox vbox =new VBox(message);
                        if(messageDTO.getIdSender().equals(sender.getId())){
                            vbox.setAlignment(Pos.CENTER_RIGHT);

                        } else {
                            vbox.setAlignment(Pos.CENTER_LEFT);

                        }
                        setGraphic(vbox);
                    }
                    Tooltip onHoverDisplayDate = new Tooltip(messageDTO.getSentTime().toString());
                    setTooltip(onHoverDisplayDate);


//                    StackPane stackPane = new StackPane(message, repliedMessage);
//                        if (strings[0].equals(sender.getFirstName())) {
//                            stackPane.setAlignment(message, Pos.CENTER_RIGHT);
//                            stackPane.setAlignment(repliedMessage, Pos.CENTER);
//
//                        } else {
//                            stackPane.setAlignment(message, Pos.CENTER_LEFT);
//
//                        }
//                    StackPane stackPane = new StackPane(message);
//                    if (strings[0].equals(sender.getFirstName())) {
//                        stackPane.setAlignment(message,Pos.CENTER_RIGHT);
//
//                    } else {
//                        stackPane.setAlignment(message,Pos.CENTER_LEFT);
//
//                    }
                    //setText(messageDTO.getText());

                }

            }
        });

    }
    public void initModel()
    {
        messagesModel.setAll(this.messageService.getMessages(new Tuple(this.sender,this.replier)));
    }
    public void setService(MessageService messageService, User sender, User replier)
    {

        this.messageService = messageService;
        this.sender = sender;
        this.replier = replier;
        idLabel.setText("Chatting with: " + this.replier.getFirstName());
        idSendButton.setOnAction(event -> sendMessage());
        initModel();
    }


    private void sendMessage() {
        String message = this.idTextField.getText();

        if(idListview.getSelectionModel().getSelectedItem()!=null)
        {
            this.messageService.saveReply(this.sender,idListview.getSelectionModel().getSelectedItem().getMessageID(), message);
        }
        else
            if(!messagesModel.isEmpty()) {
//            MessageDTO messageDTO = idListview.getItems().get(idListview.getItems().size()-1);
//            this.messageService.saveReply(this.sender, messageDTO.getMessageID(), message);
                List<User> repliers = new ArrayList<>();
                repliers.add(this.replier);
                this.messageService.saveMessage(this.sender,repliers,message);
            //this.messageService.saveReply(this.sender, messagesModel.get(messagesModel.size() - 1).getMessage(), message);
            }
            else {
                //TODO e la fel ca prima ramura
                List<User> repliers = new ArrayList<>();
                repliers.add(this.replier);
                this.messageService.saveMessage(this.sender,repliers,message);
            }
        this.idTextField.clear();
        initModel();

    }


    @Override
    public void update(UserChangeEvent event) {
        initModel();
    }


}
