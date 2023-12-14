package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.repository.Repository;

import java.util.Optional;

public interface PagingRepository <ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAll(Pageable pageable);
    public Optional<Utilizator> findUserByEmailPassword(String email, String password);
}
