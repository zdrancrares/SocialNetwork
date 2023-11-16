package com.example.socialnetworkgui;

/*
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

 */
