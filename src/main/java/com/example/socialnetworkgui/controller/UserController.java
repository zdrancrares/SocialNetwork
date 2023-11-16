package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.UserService;
import com.example.socialnetworkgui.utils.observer.Observer;
import com.example.socialnetworkgui.utils.events.UserChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    private UserService userService;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator, String> firstNameColumn;
    @FXML
    TableColumn<Utilizator, String> lastNameColumn;
    @FXML
    TableColumn<Utilizator, Long> idColumn;


    public void setUserService(UserService userService){
        this.userService = userService;
        userService.addObserver(this);
        initModel();
    }
    @FXML
    public void initialize(){
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableView.setItems(model);
    }
    public void initModel(){
        Iterable<Utilizator> users = userService.getAll();
        List<Utilizator> userList = StreamSupport.stream(users.spliterator(), false).toList();
        System.out.println(userList);
        model.setAll(userList);
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }

    public void handleDeleteUser(ActionEvent actionEvent) throws RepositoryExceptions, ServiceExceptions {
        Utilizator selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null){
            Utilizator toBeDeleted = userService.deleteEntity(selectedUser.getId());
            if (toBeDeleted != null){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Utilizatorul a fost sters cu succes.");
            }
            else{
                MessageAlert.showErrorMessage(null, "Nu ati selectat niciun student.");
            }
        }
    }

    @FXML
    public void handleAddUser(ActionEvent event){
        showMessageTaskEditDialog(null);
    }

    @FXML
    public void handleUpdateUser(ActionEvent event){
        Utilizator selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null){
            showMessageTaskEditDialog(selectedUser);
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator.");
        }
    }

    public void showMessageTaskEditDialog(Utilizator utilizator){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/editUser.fxml"));
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(userService, dialogStage, utilizator);

            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
