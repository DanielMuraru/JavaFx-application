package com.example.socialprojectgui.repository;

import com.example.socialprojectgui.domain.Entity;
import com.example.socialprojectgui.domain.Friendship;

public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAll(Pageable p,Integer id);


}
