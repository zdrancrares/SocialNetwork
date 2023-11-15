package com.example.repository;

import com.example.domain.Entity;
import com.example.domain.Prietenie;
import com.example.domain.Utilizator;
import com.example.domain.validators.Validator;
import com.example.exceptions.RepositoryExceptions;

import java.util.*;
import java.util.function.Predicate;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id) throws RepositoryExceptions{
        Predicate<ID> isNull = Objects::isNull;
        if (isNull.test(id))
            throw new RepositoryExceptions("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws RepositoryExceptions{
        Predicate<E> isNull = Objects::isNull;
        if (isNull.test(entity))
            throw new RepositoryExceptions("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);
        }
        else entities.putIfAbsent(entity.getId(),entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) throws RepositoryExceptions{
        Optional<E> entity = findOne(id);
        if (entity.isPresent()){
            return Optional.ofNullable(entities.remove(id));
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) throws RepositoryExceptions{
        Predicate<E> isNull = Objects::isNull;
        if (isNull.test(entity))
            throw new RepositoryExceptions("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
