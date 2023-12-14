package com.example.socialnetworkgui.presentation;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/*
public class ConsoleUI {
    private Scanner scanner;
    private UserService userService;
    private FriendshipService friendshipService;
    public ConsoleUI(UserService userService, FriendshipService friendshipService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        scanner = new Scanner(System.in);
    }
    private static void printMenu(){
        System.out.println("0 - Iesire");
        System.out.println("1 - Afiseaza meniul");
        System.out.println("2 - Adauga un utilizator");
        System.out.println("3 - Sterge un utilizator");
        System.out.println("4 - Modifica un utilizator");
        System.out.println("5 - Afiseaza toti utilizatorii");
        System.out.println("6 - Adauga o prietenie");
        System.out.println("7 - Sterge o prietenie");
        System.out.println("8 - Afiseaza toti prietenii unui utilizator");
        System.out.println("9 - Afiseaza toti prietenii unui utilizator care s-au creat intr-o anumita luna");
        System.out.println("10 - Afiseaza toate prieteniile");
        System.out.println("11 - Numarul de comunitati");
        System.out.println("12 - Cea mai sociabila comunitate");
    }

    private void addFriend() throws RepositoryExceptions {
        System.out.print("User_1 ID: ");
        Long user1ID = scanner.nextLong();
        System.out.print("User_2 ID: ");
        Long user2ID = scanner.nextLong();
        Optional<Utilizator> user1 = userService.getEntity(user1ID);
        Optional<Utilizator> user2 = userService.getEntity(user2ID);
        if (user1.isPresent() && user2.isPresent()) {
            try {
                if (friendshipService.addEntity(user1.get(), user2.get())) {
                    System.out.println("Prietenia a fost formata cu succes.");
                }
                else{
                    System.out.println("Exista deja aceasta prietenie.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Unul dintre utilizatori nu exista!");
        }
    }

    private void removeFriend(){
        System.out.print("ID-ul utilizatorului pentru care doriti sa stergeti un prieten: ");
        Long user1ID = scanner.nextLong();
        System.out.print("ID-ul prietenului de sters: ");
        Long user2ID = scanner.nextLong();
        Tuple<Long, Long> prietenieID = new Tuple<>(user1ID, user2ID);
        try{
            friendshipService.deleteEntity(prietenieID);
            System.out.println("Prietenia a fost stearsa cu succes.");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showAllFriends() throws RepositoryExceptions{
        System.out.print("Introduceti ID-ul utilizatorului pentru care doriti sa aflati prietenii: ");
        Long userID = scanner.nextLong();
        Optional<Utilizator> user = userService.getEntity(userID);
        if (user.isEmpty()){
            System.out.println("Utilizatorul nu exista");
            return;
        }

        if (user.get().getFriends() == null){
            System.out.println("Utilizatorul nu are niciun prieten");
            return;
        }
        user.get().getFriends().forEach(System.out::println);
    }

    private void showAllFriendships(){
        Iterable<Prietenie> friendships = friendshipService.getAll();
        AtomicBoolean found = new AtomicBoolean(false);
        friendships.forEach(p -> {
            System.out.println(p);
            found.set(true);
        });
        if (!found.get()){
            System.out.println("Nu exista nicio prietenie.");
        }
    }

    public void showAllFriendshipsMonth() throws RepositoryExceptions{
        System.out.print("Introduceti id-ul utilizatorului: ");
        Long id = scanner.nextLong();
        System.out.print("Introduceti luna(numarul lunii): ");
        int month = scanner.nextInt();
        Optional<Utilizator> user = userService.getEntity(id);
        if (user.isEmpty()){
            System.out.println("Utilizatorul nu exista");
            return;
        }
        Iterable<FriendshipDTO> friendships = userService.loadUserFriendsMonth(id,month);
        int sizeOfIterable = ((Collection<?>) friendships).size();
        if (sizeOfIterable == 0){
            System.out.println("Utilizatorul nu s-a imprietenit cu nimeni in aceasta luna.");
            return;
        }
        friendships.forEach(System.out::println);
    }

    private void addUser(){
        System.out.print("Prenume: ");
        String firstName = scanner.next();
        System.out.print("Nume: ");
        String lastName = scanner.next();
        try {
            boolean result = userService.addEntity(firstName, lastName);
            if (result) {
                System.out.println("Utilizatorul a fost adaugat cu succes.");
            }
            else{
                System.out.println("Utilizatorul nu a fost adaugat.");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void deleteUser(){
        System.out.print("ID-ul utilizatorului: ");
        Long userID = scanner.nextLong();
        try{
            userService.deleteEntity(userID);
            System.out.println("Utilizatorul a fost sters cu succes.");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateUser(){
        System.out.print("ID-ul utilizatorului de modificat: ");
        Long userID = scanner.nextLong();
        System.out.print("Noul prenume pentru utilizator: ");
        String firstName = scanner.next();
        System.out.print("Noul nume pentru utilizator: ");
        String lastName = scanner.next();
        try{
            boolean result = userService.updateEntity(userID, firstName, lastName);
            if (result) {
                System.out.println("Utilizatorul a fost modificat cu succes.");
            }
            else{
                System.out.println("Utilizatorul nu a fost modificat.");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showAllUsers(){
        Iterable<Utilizator> users = userService.getAll();
        AtomicBoolean found = new AtomicBoolean(false);
        users.forEach(u -> {
            System.out.print(u.getId() + " - ");
            System.out.println(u);
            found.set(true);
        });
        if (!found.get()){
            System.out.println("Nu exista niciun utilizator.");
        }
    }

    private void noOfCommunities(){
        System.out.println("Aceasta retea este formata din " + userService.noOfCommunities() + " comunitati");
    }

    private void mostSociableCommunity(){
        List<Iterable<Utilizator>> communities = userService.mostSociableCommunity();
        System.out.println("Cea mai sociabila comunitate este formata din: ");
        final AtomicInteger cnt = new AtomicInteger(1);
        communities.forEach(community ->{
            System.out.println("Comunitatea " + cnt);
            for (Utilizator u : community) {
                System.out.println(u);
            }
            cnt.incrementAndGet();
        });
    }

    public void startConsole() throws RepositoryExceptions{
        boolean run = true;
        ConsoleUI.printMenu();
        while (run){
            int command = 0;
            while(true) {
                try {
                   System.out.print("Introduceti comanda: ");
                   command = scanner.nextInt();
                   break;
                } catch (Exception e) {
                    System.out.println("Nu ati introdus o optiune valida.");
                }
            }
            switch (command){
                case 0:
                    System.out.println("La revedere!");
                    run = false;
                    break;
                case 1:
                    ConsoleUI.printMenu();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    showAllUsers();
                    break;
                case 6:
                    addFriend();
                    break;
                case 7:
                    removeFriend();
                    break;
                case 8:
                    showAllFriends();
                    break;
                case 9:
                    showAllFriendshipsMonth();
                    break;
                case 10:
                    showAllFriendships();
                    break;
                case 11:
                    noOfCommunities();
                    break;
                case 12:
                    mostSociableCommunity();
                    break;
                default:
                    System.out.println("Nu ati introdus o optiune valida");
            }
        }
    }
}


 */