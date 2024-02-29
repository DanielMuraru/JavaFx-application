package com.example.socialprojectgui.repository;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.Entity;
import com.example.socialprojectgui.domain.FriendRequest;
import com.example.socialprojectgui.domain.Tuple;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.repository.Exceptions.RepoExceptions;

import java.util.List;
import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    Optional<E> save(E entity);

    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     * @throws RepoExceptions           if the given id doesn't exit in repository
     */
    Optional<E> delete(ID id);

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    Optional<E> findOne(ID id);


    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * @throws IllegalArgumentException if the given entity is null.
     * @throws RepoExceptions           if the ID of the updated Entity is not found in repository
     */
    Optional<E> update(E entity);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    List<MessageDTO> findAll(Tuple<User, User> id);

    Optional<Tuple<Integer,String>> findByEmail(String email);

    Iterable<FriendRequest> findByUserId(Integer idUser);
}
