package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.MessageAlert;
import com.example.socialnetworkgui.controller.UserController;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    @FXML
    private Button changeAuthButton;
    @FXML
    private Label authLabel;
    private UserService userService;
    private FriendshipService friendshipService;
    private Stage stage;
    private boolean flag;

    public void setService(UserService userService, FriendshipService friendshipService, Stage stage, boolean flag){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.stage = stage;
        this.flag = flag;

        if (flag){ // log-in
            textFieldFirstName.setVisible(false);
            textFieldLastName.setVisible(false);
            firstNameLabel.setVisible(false);
            lastNameLabel.setVisible(false);

            authButton.setText("Log In");
            authLabel.setText("Nu aveti cont? Inregistrati-va!");
            changeAuthButton.setText("Sign Up");

            textFieldEmail.setText("");
            passwordField.setText("");

        }
        else { //sign-up
            textFieldFirstName.setVisible(true);
            textFieldLastName.setVisible(true);

            this.textFieldFirstName.setText("");
            this.textFieldLastName.setText("");
            textFieldEmail.setText("");
            passwordField.setText("");

            authButton.setText("Sign Up");
            authLabel.setText("Aveti deja un cont? Conectati-va!");
            changeAuthButton.setText("Log In");
        }

        authButton.setOnAction(event -> {
            String buttonText = authButton.getText();
            if ("Sign Up".equals(buttonText)) {
                handleSignUp(new ActionEvent());
            } else{
                handleLogIn(new ActionEvent());
            }
        });

        changeAuthButton.setOnAction(event ->{
            String buttonText = authButton.getText();
            if ("Sign Up".equals(buttonText)){
                handleChangeToSignUp(new ActionEvent());
            }
            else{
                handleChangeToLogIn(new ActionEvent());
            }
        });
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
    public void handleLogIn(ActionEvent actionEvent){
        String email = textFieldEmail.getText();
        String password = passwordField.getText();

        try {
            Optional<Utilizator> foundUser = userService.findUserByEmailPassword(email, password);
            if (foundUser.isPresent()){
                this.stage.close();
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("controller/views/mainView.fxml"));
                    VBox root = loader.load();
                    Stage dialogStage = new Stage();

                    Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
                    dialogStage.getIcons().add(icon);
                    dialogStage.setTitle("Home Page");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    UserController userController = loader.getController();
                    userController.setUserService(userService, friendshipService, foundUser.get(), dialogStage);

                    dialogStage.show();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            else{
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Log in error", "Email sau parola incorecta.");
                this.textFieldEmail.setText("");
                this.passwordField.setText("");
            }
        }catch(Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Password encrypt error", e.getMessage());
        }
    }

    @FXML
    public void handleSignUp(ActionEvent actionEvent){
        String email = textFieldEmail.getText();
        String password = passwordField.getText();

        try {
            Optional<Utilizator> foundUser = userService.findUserByEmailPassword(email, password);
            if (foundUser.isEmpty()){
                userService.addEntity(textFieldFirstName.getText(), textFieldLastName.getText(), email, password);
                MessageAlert.showMessage(stage, Alert.AlertType.CONFIRMATION, "Save details", "Utilizatorul a fost adaugat cu succes.");

                handleChangeToSignUp(new ActionEvent());

            }
            else{
                MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Sign Up Error", "Exista deja un cont cu acest email.");
            }
        }catch(Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Password encrypt error", e.getMessage());
        }
    }

    @FXML
    public void handleChangeToSignUp(ActionEvent actionEvent){
        try {
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/editUser.fxml"));

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            stage.getIcons().add(icon);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            AuthController authController = fxmlLoader.getController();
            authController.setService(userService, friendshipService, stage, true);

            stage.setTitle("Log In");
            stage.show();
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "FXMLLoader error", e.getMessage());
        }
    }

    @FXML
    public void handleChangeToLogIn(ActionEvent actionEvent){
        try {
            stage.close();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/editUser.fxml"));

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            stage.getIcons().add(icon);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            AuthController authController = fxmlLoader.getController();
            authController.setService(userService, friendshipService, stage, false);

            stage.setTitle("Sign Up");
            stage.show();
        }catch (Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "FXMLLoader error", e.getMessage());
        }
    }
}
