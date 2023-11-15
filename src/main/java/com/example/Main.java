package com.example;

import com.example.domain.Prietenie;
import com.example.domain.Tuple;
import com.example.domain.Utilizator;
import com.example.domain.validators.PrietenieValidator;
import com.example.domain.validators.UtilizatorValidator;
import com.example.domain.validators.Validator;
import com.example.exceptions.RepositoryExceptions;
import com.example.presentation.ConsoleUI;
import com.example.repository.FriendshipDBRepository;
import com.example.repository.InMemoryRepository;
import com.example.repository.Repository;
import com.example.repository.UserDBRepository;
import com.example.service.FriendshipService;
import com.example.service.UserService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws RepositoryExceptions {
        Validator<Utilizator> userValidator = new UtilizatorValidator();
        Validator<Prietenie> friendshipValidator = new PrietenieValidator();

        //InMemoryRepository<Long, Utilizator> repoUser =  new InMemoryRepository<>(userValidator);
        //InMemoryRepository<Tuple<Long,Long>, Prietenie> repoFriendship= new InMemoryRepository<>(friendshipValidator);

        Repository<Long, Utilizator> repoUserDB = new UserDBRepository(userValidator);
        Repository<Tuple<Long,Long>,Prietenie> repoFriendshipDB = new FriendshipDBRepository(friendshipValidator);

        UserService userService = new UserService(repoUserDB, repoFriendshipDB);
        FriendshipService friendshipService = new FriendshipService(repoFriendshipDB);

        ConsoleUI console = new ConsoleUI(userService, friendshipService);
        console.startConsole();
    }
}
