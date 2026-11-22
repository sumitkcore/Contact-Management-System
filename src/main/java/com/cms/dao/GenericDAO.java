package com.cms.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) Interface.
 * Demonstrates Generics, DAO Pattern, and CRUD Abstraction.
 *
 * @param <T>  The Domain Entity Type
 * @param <ID> The Primary Key Identifier Type
 */
public interface GenericDAO<T, ID extends Serializable> {

    /**
     * Persists a new entity.
     * @return true if successful, false otherwise.
     */
    boolean save(T entity);

    /**
     * Updates an existing entity.
     * @return true if updated, false otherwise.
     */
    boolean update(T entity);

    /**
     * Soft-deletes or removes an entity by ID.
     */
    boolean delete(ID id);

    /**
     * Finds an entity by its unique ID.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all active entities.
     */
    List<T> findAll();

    /**
     * Returns total count of active entities.
     */
    long count();
}
