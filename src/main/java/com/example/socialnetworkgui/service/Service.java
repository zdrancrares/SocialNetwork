package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;

/**
 * Interface for services
 */

public interface Service<ID, E extends Entity<ID>> {

    /*
     * adds the entity if it's valid, and it isn't already saved
     * @param entity
     *         entity must be not null
     * @return true - if the given entity is saved
     *         otherwise returns false(id already exists)
     * @throws ServiceExceptions
     *            if the entity is not valid
     *
     */

    //boolean addEntity(E entity) throws RepositoryExceptions, ServiceExceptions;

    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws ServiceExceptions
     *                   if the entity doesn't exist
     */

    E deleteEntity(ID id) throws RepositoryExceptions, ServiceExceptions;

    /**
     * returns all the entities of a service
     *
     * @return Iterable<E>
     *
     */
    Iterable<E> getAll();
}
