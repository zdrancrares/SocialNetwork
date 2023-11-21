package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.UserController;
import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.domain.validators.PrietenieValidator;
import com.example.socialnetworkgui.domain.validators.UtilizatorValidator;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.FriendshipDBRepository;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.UserDBRepository;
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
    private Repository<Long, Utilizator> userRepo;
    private UserService userService;
    private FriendshipService friendshipService;
    private Validator<Prietenie> friendshipValidator;
    private Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;

    @Override
    public void start(Stage primaryStage) throws Exception {
        userValidator = new UtilizatorValidator();
        userRepo = new UserDBRepository(userValidator);

        friendshipValidator = new PrietenieValidator();
        friendshipRepo = new FriendshipDBRepository(friendshipValidator);

        userService = new UserService(userRepo, friendshipRepo);
        friendshipService = new FriendshipService(friendshipRepo);

        initView(primaryStage);
        primaryStage.setTitle("SocialNetworkApp");
        primaryStage.show();
    }

    public void initView(Stage stage) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/mainView.fxml"));

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            stage.getIcons().add(icon);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            UserController userController = fxmlLoader.getController();
            userController.setUserService(userService, friendshipService);
    }
    //TODO: la delete daca n am selectat nimic, sa afiseze mesaj de warning
    public static void main(String[] args){
        launch(args);
    }

}
