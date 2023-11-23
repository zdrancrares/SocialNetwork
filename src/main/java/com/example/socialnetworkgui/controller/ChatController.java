package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.DTO.MessageDTO;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class ChatController {
    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    @FXML
    VBox chatContainer;

    Stage dialogStage;
    Utilizator user1;
    Utilizator user2;

    private UserService userService;
    private FriendshipService friendshipService;

    public void setChatService(UserService userService, FriendshipService friendshipService, Stage stage, Utilizator user1, Utilizator user2){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.user1 = user1;
        this.user2 = user2;
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        Iterable<MessageDTO>chats = userService.loadChats(user1, user2);
        for (MessageDTO chat : chats) {
            HBox messageBox;
            if (Objects.equals(chat.getFirstName1(), user1.getFirstName())) {
                messageBox = createMessageBox(chat, user1);
            }
            else{
                messageBox = createMessageBox(chat, user2);
            }
            chatContainer.getChildren().add(messageBox);
        }
        ScrollPane scrollPane = new ScrollPane(chatContainer);
        Scene scene = new Scene(scrollPane, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Chats");
        stage.show();
    }

    private HBox createMessageBox(MessageDTO chat, Utilizator user) {
        HBox messageBox = new HBox(10);

        Label userNameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        userNameLabel.setStyle("-fx-font-weight: bold;");

        TextField messageField = new TextField(chat.getContent());

        messageField.setEditable(false);
        messageField.setMinWidth(200);

        if (Objects.equals(user.getFirstName(), user1.getFirstName())) {
            messageBox.getChildren().addAll(messageField, userNameLabel);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.getChildren().addAll(userNameLabel, messageField);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        return messageBox;
    }
}
