package com.example.socialprojectgui.domain.validators;

import com.example.socialprojectgui.domain.User;


public class UserValidator implements Validator<User> {


    /**
     * Validates attributes of an entity
     *
     * @param entity:User,user to be validated
     * @throws ValidationException if the user is not valid
     */
    @Override
    public void validator(User entity) {
        String errors = "";
        if (entity.getFirstName().isEmpty()) {
            errors += "Prenume invalid";
        }
        if (entity.getLastName().isEmpty()) {
            errors += "Nume invalid";
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

    }
}
