package com.example.socialprojectgui.domain;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class User extends Entity<Integer> {
    private String firstName;
    private String lastName;
    private String userEmail;
    private String userPassword;



    private Map<LocalDate,List<User>> friends;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = email;
        this.userPassword = password;
        friends = new HashMap<>();
    }

    /**
     * Returns user's first name
     *
     * @return :string firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets user's firstName
     *
     * @param firstName:string,user's first name
     * @return -
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns user's last name
     *
     * @return :string lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets user's last name
     *
     * @param lastName:string ,user's last name
     * @return -
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns user's friend list
     *
     * @return :list of users
     */
    public Map<LocalDate,List<User>> getFriends() {
        return this.friends;
    }

    /**
     * Adds a friend to this user
     *
     * @param ot:other user
     * @return -
     */
    public void addFriend(User ot,LocalDate str) {
        List<User> listOfFriends = this.friends.get(str);
        if (!listOfFriends.contains(ot)) {
            listOfFriends.add(ot);
            ot.addFriend(ot,str);
        } else throw new FriendshipException("User already has this friend");
    }

    /**
     * Removes a friend of this user
     *
     * @param ot:user to be removed from friend list
     * @return -
     */
    public void removeFriend(User ot) {

        AtomicInteger ok = new AtomicInteger();
        this.friends.forEach((x,y)->{
            if(y.contains(ot))
            {
                ok.set(1);
                y.remove(ot);
                ot.removeFriend(ot);
            }


        });
        if(ok.get() == 0)
            throw new FriendshipException("User doesn't have this friend");
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, friends);
    }

    @Override
    public String toString() {
        return  "id=" + id +
                " firstName='" + firstName + '\'' +
                " lastName='" + lastName + '\'' ;
    }

}
