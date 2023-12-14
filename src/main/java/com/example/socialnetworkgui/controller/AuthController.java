package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class AuthController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Button authButton;
    private UserService userService;
    private FriendshipService friendshipService;
    Stage stage;
    Utilizator user;

    public void setService(UserService userService, FriendshipService friendshipService, Stage stage, boolean flag){
        //todo: flag = 1 - login
        //todo: flag = 0 - signup
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.stage = stage;
        //this.dialogStage = stage;

        if (flag){ // log-in
            //textFieldId.setVisible(false);
            textFieldFirstName.setVisible(false);
            textFieldLastName.setVisible(false);
            firstNameLabel.setVisible(false);
            lastNameLabel.setVisible(false);

            authButton.setText("Log In");

            textFieldEmail.setText("");
            passwordField.setText("");

        }
        else { //sign-up
            //textFieldId.setVisible(true);
            textFieldFirstName.setVisible(true);
            textFieldLastName.setVisible(true);

            this.textFieldFirstName.setText("");
            this.textFieldLastName.setText("");

            authButton.setText("Sign Up");
            //textFieldId.setEditable(false);
        }
    }

    @FXML
    public void handleCancelButton(ActionEvent event){
        this.stage.close();
    }
    @FXML
    public void handleClearButton(ActionEvent event){
        this.textFieldFirstName.setText("");
        this.textFieldLastName.setText("");
        this.textFieldEmail.setText("");
        this.passwordField.setText("");
    }

    @FXML
    public void handleLogIn(ActionEvent actionEvent) throws Exception{
        String email = textFieldEmail.getText();
        String password = passwordField.getText();

        Optional<Utilizator> foundUser = userService.findUserByEmailPassword(email, password);
        if (foundUser.isPresent()){
            this.stage.close();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("views/mainView.fxml"));
                AnchorPane root = loader.load();
                Stage dialogStage = new Stage();

                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
                dialogStage.getIcons().add(icon);
                dialogStage.setTitle("Log In");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                UserController userController = loader.getController();
                userController.setUserService(userService, friendshipService);

                dialogStage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Log in error", "Email sau parola incorecta.");
        }
    }
}
