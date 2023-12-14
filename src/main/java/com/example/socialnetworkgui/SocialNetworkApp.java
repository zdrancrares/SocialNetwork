package com.example.socialnetworkgui;

import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.domain.validators.PrietenieValidator;
import com.example.socialnetworkgui.domain.validators.UtilizatorValidator;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.*;
import com.example.socialnetworkgui.repository.paging.PagingRepository;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.Objects;

public class SocialNetworkApp extends Application {
    private Validator<Utilizator> userValidator;
    private PagingRepository<Long, Utilizator> userRepo;
    private UserService userService;
    private FriendshipService friendshipService;
    private Validator<Prietenie> friendshipValidator;
    private Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    private MessageRepository messageRepo;
    private FriendRequestRepository friendRequestRepo;

    @Override
    public void start(Stage primaryStage) throws Exception {
        userValidator = new UtilizatorValidator();
        userRepo = new UserDBRepository(userValidator);

        friendshipValidator = new PrietenieValidator();
        friendshipRepo = new FriendshipDBRepository(friendshipValidator);

        messageRepo = new MessageDBRepository();
        friendRequestRepo = new FriendRequestDBRepository();

        userService = new UserService(userRepo, friendshipRepo, messageRepo, friendRequestRepo);
        friendshipService = new FriendshipService(friendshipRepo, friendRequestRepo);

        initView(primaryStage);
        primaryStage.setTitle("Log In");
        primaryStage.show();
    }

    public void initView(Stage stage) throws IOException{

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/editUser.fxml"));

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            stage.getIcons().add(icon);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            AuthController authController = fxmlLoader.getController();
            authController.setService(userService, friendshipService, stage, true);

    }
    public static void main(String[] args){
        launch(args);
    }

}
