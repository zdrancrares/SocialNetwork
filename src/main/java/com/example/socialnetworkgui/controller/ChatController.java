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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class ChatController {
    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    @FXML
    VBox chatContainer;

    Stage dialogStage;
    Utilizator user1;
    Utilizator user2;
    @FXML
    TextField sendReply;

    @FXML
    Button sendMessage;

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox main;

    private UserService userService;
    private FriendshipService friendshipService;

    public void setChatService(UserService userService, FriendshipService friendshipService, Stage stage, Utilizator user1, Utilizator user2){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.user1 = user1;
        this.user2 = user2;
        main = new VBox(20);
        main.setPadding(new Insets(10));
        initChat();

        Scene scene = new Scene(main, 500, 450);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Chats");
        dialogStage.show();
    }

    public void initChat(){
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        Iterable<MessageDTO>chats = userService.loadChats(user1, user2);
        for (MessageDTO chat : chats) {
            VBox messageBox;
            if (Objects.equals(chat.getFirstName1(), user1.getFirstName())) {
                messageBox = createMessageBox(chat, user1);
            }
            else{
                messageBox = createMessageBox(chat, user2);
            }
            chatContainer.getChildren().add(messageBox);
        }
        scrollPane = new ScrollPane(chatContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setMinHeight(350);

        sendReply = new TextField();

        main.getChildren().clear();
        main.getChildren().addAll(scrollPane, sendReply);

    }

    public void handleSendReply(ActionEvent actionEvent) throws RepositoryExceptions, ServiceExceptions {
        if (Objects.equals(sendReply.getText(), "")) {
            MessageAlert.showErrorMessage(null, "Nu ati introdus niciun mesaj.");
            return;
        }
        ArrayList<Utilizator> to = new ArrayList<>();
        to.add(user2);
        userService.addMessage(sendReply.getText(), user1.getId(), to);
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Send message details", "Mesajul a fost trimis cu succes.");
        sendReply.setText("");
        initChat();
    }

    private VBox createMessageBox(MessageDTO chat, Utilizator user){
        HBox messageBox = new HBox(10);

        Label dateLabel = new Label(chat.getDate().toString());

        Label userNameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        userNameLabel.setStyle("-fx-font-weight: bold;");

        TextField messageField = new TextField(chat.getContent());

        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-background-color: #77c3ec;");

        replyButton.setOnAction(event -> {
            try {
                handleSendReply(new ActionEvent());
            }catch (Exception e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        });

        messageField.setEditable(false);
        messageField.setMinWidth(200);

        VBox chatBox = new VBox(5);

        if (Objects.equals(user.getFirstName(), user1.getFirstName())) {
            messageBox.getChildren().addAll(messageField, userNameLabel);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            chatBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.getChildren().addAll(replyButton, userNameLabel, messageField);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        chatBox.getChildren().addAll(dateLabel, messageBox);

        return chatBox;
    }
}
