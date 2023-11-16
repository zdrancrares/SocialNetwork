package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.repository.Repository;

import java.util.Optional;


public class FriendshipService implements Service<Tuple<Long,Long>, Prietenie>{
    private Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    public FriendshipService(Repository<Tuple<Long,Long>, Prietenie> friendshipRepo){
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * adds the friendship between the two given users if it's valid, and it isn't already saved
     * creates the id based on the users' id's
     * @param user1
     *         the first user of the friendship
     * @param user2
     *          the second user of the friendship
     * @return true - if the entity is saved
     *         otherwise returns false(id already exists)
     * @throws ServiceExceptions
     *            if the friendship is not valid
     *
     */

    public boolean addEntity(Utilizator user1, Utilizator user2) throws ServiceExceptions, RepositoryExceptions{
        Prietenie entity;
        Tuple<Long, Long> prietenieID;
        if (user1.getId() < user2.getId()) {
            entity = new Prietenie(user1, user2);
            prietenieID = new Tuple<>(entity.getUser1().getId(), entity.getUser2().getId());
        }
        else{
            entity = new Prietenie(user2, user1);
            prietenieID = new Tuple<>(entity.getUser2().getId(), entity.getUser1().getId());
        }
        entity.setId(prietenieID);
        Optional<Prietenie> friendship = friendshipRepo.findOne(entity.getId());
        if (friendship.isPresent()){
            throw new ServiceExceptions("Prietenia exista deja");
        }
        Optional<Prietenie> p = friendshipRepo.save(entity);
        return p.isEmpty();
    }

    @Override
    public Prietenie deleteEntity(Tuple<Long, Long> id) throws ServiceExceptions, RepositoryExceptions {
        Long id1 = id.getLeft();
        Long id2 = id.getRight();

        if (id1 > id2){
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }

        Tuple<Long, Long> newID = new Tuple<>(id1, id2);
        Optional<Prietenie> friendship = friendshipRepo.delete(newID);
        if (friendship.isPresent()){
            return friendship.get();
        }
        throw new ServiceExceptions("Nu exista aceasta prietenie!");
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }

    /**
     * gets the friendship with a specific id
     * @param id the id of the friendship
     * @returns Optional.of(entity) if found, Optional.empty() otherwise
     * @throws RepositoryExceptions: if the entity is null
     */
    public Optional<Prietenie> getEntity(Tuple<Long,Long> id) throws RepositoryExceptions{
        return friendshipRepo.findOne(id);
    }
}
