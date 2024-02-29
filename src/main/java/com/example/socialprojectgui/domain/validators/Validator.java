package com.example.socialprojectgui.domain.validators;

public interface Validator<E> {
    /**
     * Validates attributes of an entity
     *
     * @param entity:Entity,entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    public void validator(E entity);
}
