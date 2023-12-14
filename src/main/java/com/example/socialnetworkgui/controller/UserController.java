package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.repository.paging.Page;
import com.example.socialnetworkgui.repository.paging.Pageable;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import com.example.socialnetworkgui.utils.observer.Observer;
import com.example.socialnetworkgui.utils.events.UserChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    private UserService userService;
    private FriendshipService friendshipService;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator, String> firstNameColumn;
    @FXML
    TableColumn<Utilizator, String> lastNameColumn;
    @FXML
    TableColumn<Utilizator, Long> idColumn;

    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    private int currentPage = 0;

    @FXML
    TextField recordsPerPage;

    private int numberOfRecordsPerPage = 5;

    private int totalNumberOfElements;


    public void setUserService(UserService userService, FriendshipService friendshipService){
        this.userService = userService;
        this.friendshipService = friendshipService;

        userService.addObserver(this);
        friendshipService.addObserver(this);

        recordsPerPage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleChangeNumberOfRecords(new ActionEvent());
            }
        });

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
        Page<Utilizator> usersOnCurrentPage = userService.getUsersOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements = usersOnCurrentPage.getTotalNumberOfElements();

        //System.out.println(totalNumberOfElements);
        //System.out.println(usersOnCurrentPage);

        //Iterable<Utilizator> users = userService.getAll();
        List<Utilizator> userList = StreamSupport.stream(usersOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        model.setAll(userList);

        handlePageNavigationChecks();
    }

    @FXML
    public void handleChangeNumberOfRecords(ActionEvent actionEvent){
        currentPage = 0;
        try {
            numberOfRecordsPerPage = Integer.parseInt(recordsPerPage.getText());
        }catch(Exception e){
           MessageAlert.showErrorMessage(null, "Nu este un numar valid.");
           return;
        }
        initModel();
    }

    private void handlePageNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*numberOfRecordsPerPage >= totalNumberOfElements);
    }

    @FXML
    public void goToNextPage(ActionEvent actionEvent){
        currentPage++;
        initModel();
    }

    @FXML
    public void goToPreviousPage(ActionEvent actionEvent){
        currentPage--;
        initModel();
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
                MessageAlert.showErrorMessage(null, "Nu exista niciun utilizator cu acest ID.");
            }
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator.");
        }

    }

    @FXML
    public void handleAddUser(ActionEvent event){
        showUserEditDialog(null);
    }

    @FXML
    public void handleUpdateUser(ActionEvent event){
        Utilizator selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null){
            showUserEditDialog(selectedUser);
            update(null);
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator.");
        }
    }

    @FXML
    public void handleFriendshipsUser(ActionEvent event) throws ServiceExceptions, RepositoryExceptions{
        Utilizator selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null){
            showFriendshipEditDialog(selectedUser);
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator.");
        }
    }

    public void showUserEditDialog(Utilizator utilizator){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/editUser.fxml"));
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            dialogStage.getIcons().add(icon);
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


    public void showFriendshipEditDialog(Utilizator user) throws RepositoryExceptions, ServiceExceptions{
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/editFriendship.fxml"));
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            dialogStage.getIcons().add(icon);

            dialogStage.setTitle("Edit the user's friendships(" + user.getFirstName() + " " + user.getLastName() + ")");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditFriendshipController editFriendshipController = loader.getController();
            editFriendshipController.setFriendshipService(userService, friendshipService, dialogStage, user);

            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
