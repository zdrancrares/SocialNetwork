package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.utils.observer.Observer;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.events.UserChangeEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class UserService implements Service<Long, Utilizator>, Observable<UserChangeEvent> {
    private final Repository<Long, Utilizator> userRepo;
    private final Repository<Tuple<Long,Long>, Prietenie> prietenieRepo;

    private List<Observer<UserChangeEvent>> observers = new ArrayList<>();

    private static Long usersID;
    public UserService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> prietenieRepo){
        usersID = 0L;
        this.userRepo = userRepo;
        this.prietenieRepo = prietenieRepo;
    }

    /**
     * adds a friend to a certain user(it also adds it to the other user)
     *
     * @param userID:   the first user in the friendship
     * @param friendID: the second user in the friendship
     * @return true: if the two users are friends
     * otherwise it returns false
     * @throws RepositoryExceptions if either one of the users doesn't exist
     */
    @Deprecated
    public Optional<Boolean> addFriend(Long userID, Long friendID) throws RepositoryExceptions{
        Optional<Utilizator> user = userRepo.findOne(userID);
        Optional<Utilizator> friend = userRepo.findOne(friendID);

        Optional<Boolean> result = Optional.of(false);
        Predicate<Optional<Utilizator>> isPresentTest = Optional::isPresent;

        if (isPresentTest.test(user) && isPresentTest.test(friend)){
            user.get().addFriend(friend.get());
            friend.get().addFriend(user.get());
            List<Utilizator> friendList = user.get().getFriends();
            result = Optional.of(friendList.contains(friend.get()));
        }
        return result;
    }

    /**
     * generates an unique ID using an incrementing static variable
     * @return an ID(Long)
     */
    private Long generateID(){
        usersID += 1;
        return usersID;
    }

    /**
     * creates an entity with firstName and lastName
     * adds the entity if it's valid, and it isn't already saved
     * @param firstName
     *         the first name of the entity to be created and saved
     * @param lastName
     *          the last name of the entity to be saved
     * @return true - if the entity is saved
     *         otherwise returns false(id already exists)
     * @throws RepositoryExceptions from Repository
     *            if the entity is not valid
     *
     */

    public boolean addEntity(String firstName, String lastName) throws RepositoryExceptions {
        Utilizator entity = new Utilizator(firstName, lastName);
        //entity.setId(generateID());
        Optional<Utilizator> savedUser = userRepo.save(entity);
        if (savedUser.isEmpty()){
            notifyObservers(new UserChangeEvent(null, null));
            return true;
        }
        return false;
        //return userRepo.save(entity).isEmpty();
    }

    @Override
    public Utilizator deleteEntity(Long id) throws ServiceExceptions, RepositoryExceptions {
        Optional<Utilizator> userToDelete = userRepo.delete(id);
        if (userToDelete.isPresent()){
            notifyObservers(new UserChangeEvent(null, null));
            return userToDelete.get();
        }
        throw new ServiceExceptions("Utilizatorul pe care doriti sa-l stergeti nu exista.");
    }

    public boolean updateEntity(Long id, String firstName, String lastName) throws RepositoryExceptions{
        Utilizator entity = new Utilizator(firstName, lastName);
        entity.setId(id);
        Optional<Utilizator> updatedUser = userRepo.update(entity);
        if (updatedUser.isEmpty()){
            notifyObservers(new UserChangeEvent(null, null));
            return true;
        }
        return false;
        //return userRepo.update(entity).isEmpty();
    }

    /**
     * loads all the friends of a user from a specific month
     * @param id: the id of the user
     * @param month: the month when the friendships were created
     * @returns a list of all the friendships containing the user and created in that month
     * @throws RepositoryExceptions: if the user is null
     */
    public Iterable<FriendshipDTO> loadUserFriendsMonth(Long id, int month) throws RepositoryExceptions{
        ArrayList<Prietenie> prietenii = new ArrayList<>();
        Optional<Utilizator> user = userRepo.findOne(id);
        if (user.isPresent()){
            for (Utilizator f: user.get().getFriends()){
                Tuple<Long, Long> idPereche = new Tuple<>(null, null);
                if (f.getId() < user.get().getId()){
                    idPereche.setLeft(f.getId());
                    idPereche.setRight(user.get().getId());
                }
                else{
                    idPereche.setRight(f.getId());
                    idPereche.setLeft(user.get().getId());
                }
                Optional<Prietenie> p = prietenieRepo.findOne(idPereche);
                p.ifPresent(prietenii::add);
            }
        }
        /*
        return prietenii.stream()
              .filter(friendship -> friendship.getDate().getMonthValue() == month)
            .map(friendship -> Objects.equals(friendship.getUser1().getId(), user.get().getId()) ?
                    friendship.getUser2().getLastName() + " | " + friendship.getUser2().getFirstName() + " | " + friendship.getDate():
                    friendship.getUser1().getLastName() + " | " + friendship.getUser1().getFirstName() + " | " + friendship.getDate())
          .toList();

         */
        return prietenii.stream()
                .filter(friendship -> friendship.getDate().getMonthValue() == month)
                .map(prietenie -> {
                    Utilizator friend = (Objects.equals(prietenie.getId().getLeft(), id))
                            ? prietenie.getUser2()
                            : prietenie.getUser1();

                    return new FriendshipDTO(
                            friend.getId(),
                            friend.getFirstName(),
                            friend.getLastName(),
                            prietenie.getDate()
                    );
                })
                .toList();
    }

    public Iterable<FriendshipDTO> loadUserFriendsDTO(Long id) throws RepositoryExceptions, ServiceExceptions {
        ArrayList<Prietenie> prietenii = new ArrayList<>();
        Optional<Utilizator> user = userRepo.findOne(id);
        if (user.isPresent()) {
            for (Utilizator f : user.get().getFriends()) {
                Tuple<Long, Long> idPereche = new Tuple<>(null, null);
                if (f.getId() < user.get().getId()) {
                    idPereche.setLeft(f.getId());
                    idPereche.setRight(user.get().getId());
                } else {
                    idPereche.setRight(f.getId());
                    idPereche.setLeft(user.get().getId());
                }
                Optional<Prietenie> p = prietenieRepo.findOne(idPereche);
                p.ifPresent(prietenii::add);
            }
        }

        return prietenii.stream()
                .map(prietenie -> {
                    Utilizator friend = (Objects.equals(prietenie.getId().getLeft(), id))
                            ? prietenie.getUser2()
                            : prietenie.getUser1();

                    return new FriendshipDTO(
                            friend.getId(),
                            friend.getFirstName(),
                            friend.getLastName(),
                            prietenie.getDate()
                    );
                })
                .toList();
    }

    /**
     * Depth First Search to find the users of a community
     * @param utilizator: the user we reached with searching
     * @param set: the set of users so we won't visit them twice
     * @return the users who form a community starting from 'user'
     */

    public List<Utilizator> DFS(Utilizator utilizator, Set<Utilizator> set) throws RepositoryExceptions {
        List<Utilizator> users = new ArrayList<>();
        Stack<Utilizator> stack = new Stack<>();

        stack.push(utilizator);
        set.add(utilizator);

        while (!stack.isEmpty()) {
            Utilizator current = stack.pop();
            //users.add(current);

            Optional<Utilizator> user = userRepo.findOne(current.getId());
            user.ifPresent(value -> value.getFriends().stream()
                    .filter(u -> !set.contains(u))
                    .forEach(u -> {
                        stack.push(u);
                        set.add(u);
                    }));

            user.ifPresent(users::add);
        }
        return users;
    }

    /**
     * function which calculates the total number of communities in our social network(using DFS for finding communities)
     * @return the number of distinct communities in the network
     */
    public int noOfCommunities(){
        Set<Utilizator> set = new HashSet<>();
        AtomicInteger count = new AtomicInteger(0);

        Iterable<Utilizator> users = userRepo.findAll();

        users.forEach(u -> {
            if (!set.contains(u)) {
                count.incrementAndGet();
                try {
                    DFS(u, set);
                } catch (RepositoryExceptions e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return count.get();
    }

    /**
     * function which finds the most sociable community/communities in our network
     * @return a list of iterable's with the most sociable communities
     */
    public List<Iterable<Utilizator>> mostSociableCommunity(){

        Iterable<Utilizator> users = userRepo.findAll();

        Set<Utilizator> set = new HashSet<>();
        List<Iterable<Utilizator>> result = new ArrayList<>();
        AtomicInteger maxLength = new AtomicInteger(-1);

        users.forEach(u -> {
            if (!set.contains(u)) {
                int beforeSize = set.size();

                List<Utilizator> community = null;
                try {
                    community = DFS(u, set);
                } catch (RepositoryExceptions e) {
                    throw new RuntimeException(e);
                }
                //int afterSize = set.size();
                int afterSize = 0;
                for (Utilizator c: community){
                    afterSize += c.getFriends().size();
                }
                //int friendsCounter = afterSize - beforeSize;
                int friendsCounter = afterSize;

                if (friendsCounter > maxLength.get()) {
                    maxLength.set(friendsCounter);
                    result.clear();
                    result.add(community);
                } else if (friendsCounter == maxLength.get()) {
                    result.add(community);
                }
            }
        });
        return result;
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    /**
     * function which finds an entity by its id
     * @return the entity if there is an entity with this id
     *          otherwise it returns null
     */
    public Optional<Utilizator> getEntity(Long id) throws RepositoryExceptions{
        return userRepo.findOne(id);
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
