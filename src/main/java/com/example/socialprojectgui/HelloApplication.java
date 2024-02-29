package com.example.socialprojectgui;

import com.example.socialprojectgui.controller.LoginController;
import com.example.socialprojectgui.controller.AdminController;
import com.example.socialprojectgui.controller.MessageAlert;
import com.example.socialprojectgui.controller.UserController;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.domain.validators.UserValidator;
import com.example.socialprojectgui.domain.validators.Validator;
import com.example.socialprojectgui.repository.Database.*;
import com.example.socialprojectgui.repository.Exceptions.RepoExceptions;
import com.example.socialprojectgui.repository.PagingRepository;
import com.example.socialprojectgui.repository.Repository;
import com.example.socialprojectgui.service.CommunityService;
import com.example.socialprojectgui.service.FriendRequestService;
import com.example.socialprojectgui.service.MessageService;
import com.example.socialprojectgui.service.UserService;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

public class HelloApplication extends Application {
    Validator<User> validator = new UserValidator();
    Repository<Integer, User> userRepository = new UserDBRepo<>();
    PagingRepository<Tuple<User,User>, Friendship> friendshipRepository = new FriendshipDBRepo("jdbc:postgresql://localhost:5432/ProjectDB","postgres","Aluire1@2");
    Repository<Integer, FriendRequest> friendRequestRepository = new FriendRequestDBRepo("jdbc:postgresql://localhost:5432/ProjectDB","postgres","Aluire1@2");
    UserService<Integer> userService = new UserService<>(validator,userRepository,friendshipRepository);
    CommunityService<Integer, Entity<Integer>> communityService = new CommunityService<>(friendshipRepository,userService);
    FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepository, friendshipRepository);

    Repository<Tuple<Integer,List<Integer>>, Message> messageRepository = new MessagesDBRepo("jdbc:postgresql://localhost:5432/ProjectDB", "postgres", "Aluire1@2");
    Repository<Tuple<Integer,List<Integer>>, ReplyMessage> replyMessageRepository = new ReplyMessagesDBRepo("jdbc:postgresql://localhost:5432/ProjectDB", "postgres", "Aluire1@2");
    MessageService messageService = new MessageService(messageRepository, replyMessageRepository);
    public void loginHandler(String user,String pass) throws IOException {

        if(user.equals("admin"))
        {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/AdminView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            AdminController adminController = fxmlLoader.getController();
            try {
                adminController.setUserService(userService, friendRequestService,messageService, pass);
                stage.setTitle("Users!");
                stage.setScene(scene);
                stage.setHeight(512);
                stage.setWidth(1024);
                stage.show();
            }
            catch (IllegalArgumentException e)
            {
                MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Info",e.getMessage());
                //stage.close();
                //start(new Stage());
            }

        }
        else {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/UserView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            UserController userController = fxmlLoader.getController();
            try {
                friendRequestService.addObserver(userController);
                userService.addObserver(userController);
                userController.setService(userService, friendRequestService, messageService, stage, user, pass);
                stage.setTitle("User!");
                stage.setScene(scene);
                stage.setHeight(512);
                stage.setWidth(1024);
                stage.show();
            }
            catch(RepoExceptions e)
            {
                MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Info",e.getMessage());
                //stage.close();
                //start(new Stage());
            }


        }


    }
    @Override
    public void start(Stage stage) throws IOException {
//        Validator<User> validator = new UserValidator();
//        Repository<Integer, User> userRepository = new UserDBRepo<>();
//        Repository<Tuple<User,User>, Friendship> friendshipRepository = new FriendshipDBRepo("jdbc:postgresql://localhost:5432/ProjectDB","postgres","Aluire1@2");
//        UserService<Integer> userService = new UserService<>(validator,userRepository,friendshipRepository);
//
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/AdminView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        AdminController userController = fxmlLoader.getController();
//        userController.setUserService(userService);
//        stage.setTitle("Users!");
//        stage.setScene(scene);
//        stage.setHeight(512);
//        stage.setWidth(1024);
//        CommunityService<Integer, Entity<Integer>> communityService = new CommunityService<>(friendshipRepository,userService);
//        UserInterface userInterface = new UserInterface(userService, communityService);
//        stage.show();

        FXMLLoader fxmlLoaderLogin = new FXMLLoader(HelloApplication.class.getResource("views/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoaderLogin.load());
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setHeight(512);
        stage.setWidth(1024);
        LoginController loginController = fxmlLoaderLogin.getController();
        loginController.setApplication(this, stage);
        stage.show();



    }

    public static void main(String[] args) {
        launch();
    }
}