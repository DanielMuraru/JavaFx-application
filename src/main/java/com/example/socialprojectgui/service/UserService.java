package com.example.socialprojectgui.service;

import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.domain.validators.ValidationException;
import com.example.socialprojectgui.domain.validators.Validator;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Exceptions.RepoExceptions;
import com.example.socialprojectgui.repository.Page;
import com.example.socialprojectgui.repository.Pageable;
import com.example.socialprojectgui.repository.PagingRepository;
import com.example.socialprojectgui.repository.Repository;
import com.example.socialprojectgui.utils.*;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
//import com.example.socialprojectgui.repository.InMemory.UserRepoInMemory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class UserService<ID> implements Observable<UserChangeEvent> {
    private int pageSize = 1 ;
    private int currentPage = 0;
    private int totalNoElements = 0;

    private Validator validator;
    private Repository userRepo;
    private PagingRepository friendshipRepo;
    List<Observer<UserChangeEvent>> observers = new ArrayList<>();

    // private Repository friendshipRepo;
    Function<User,Optional> adde= entity -> userRepo.save(entity);

    /**
     * @param validator:object  of type Validator
     * @param userRepo:object of type Validator
     */
    public UserService(Validator validator, Repository userRepo,PagingRepository friendshipRepo) {
        this.validator = validator;
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * Adds a valid user to repository
     *
     * @param id:-int,id               of the user
     *                                 -id must not be null
     * @param first_name:string,user's first name
     * @param last_name:string,user's  last name
     * @return -
     * @throws ValidationException if the user is not valid
     */
    public void addUser(Integer id, String first_name, String last_name, String email, String pass) {

        Supplier<User> createUser= () -> new User(first_name,last_name, email, pass);
        User newUser = createUser.get();
        newUser.setId(id);
        try {
            validator.validator(newUser);
            adde.apply(newUser);
            this.notifyObservers(new UserChangeEvent(ChangeEventType.ADD,newUser,null));

        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }


    }

    /**
     * Adds a friend to a user
     *
     * @param id_user:-int,id of user
     *                -id must not be null
     * @param id_friend:-int,id of the friend we want to add
     *                  -id must not be null
     * @return -
     * @throws RepoExceptions if the ids are not found
     * @throws FriendshipException if they were already friends
     */
    public void addFriend(Integer id_user, Integer id_friend) {
        Optional<User> u =  userRepo.findOne(id_user);
        Optional<User> friend= userRepo.findOne(id_friend);

        if(u.isPresent() && friend.isPresent())
        {
            Tuple ids = new Tuple(u.get(),friend.get());
            Friendship fr=new Friendship();
            fr.setId(ids);
            if(!friendshipRepo.findOne(ids).isPresent())
                friendshipRepo.save(fr);
            else throw new DBExceptions("Friendship already exists!");


            /*Class<? extends Repository> us=userRepo.getClass();
            Class[] parameters=new Class[3];
            parameters[0]=User.class;
            parameters[1]=User.class;
            parameters[2]= LocalDate.class;
            try {
                Method m=us.getMethod("addF",parameters);
                Object o=m.invoke(userRepo,u.get(),friend.get(),fr.getFriendsFrom());


            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }*/
            /*Friendship friendship=new Friendship();
            friendship.setId(new org.example.domain.Tuple<Integer,Integer>(id_user,id_friend));
            userRepo.save(friendship);*/



        }

    }

    /**
     * Removes a user's friend
     *
     * @param id_user:-int,id of the user
     *                -id must not be null
     * @param id_friend:-int,id of the friend we want to remove
     *                  -id must not be null
     * @return -
     * @throws RepoExceptions if the ids are not found
     * @throws FriendshipException if they weren't friends in the first place
     */
    public void removeFriend(Integer id_user, Integer id_friend) {

        Optional<User> u= userRepo.findOne(id_user);
        Optional<User> friend= userRepo.findOne(id_friend);
        if(u.isPresent() && friend.isPresent())
        {

            this.friendshipRepo.delete(new Tuple<User,User>(u.get(),friend.get()));
            notifyObservers(new UserChangeEvent(ChangeEventType.REMOVE,null,null));
//            Class<? extends Repository> us=userRepo.getClass();
//            Class[] parameters=new Class[2];
//            parameters[0]=User.class;
//            parameters[1]=User.class;
//            try {
//                Method m=us.getMethod("removeF",parameters);
//                Object o=m.invoke(userRepo,u.get(),friend.get());
//                notifyObservers(new UserChangeEvent(ChangeEventType.REMOVE,null,null));
//
//            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
        }


    }

    /**
     * Removes a user from other's friends list and from repository
     *
     * @param id:-int,id of the user
     *           -id must not be null
     * @throws RepoExceptions if id is not found
     */
    public void removeUser(ID id) {
        Optional<User> u = userRepo.findOne(id);
        u.ifPresent(user -> userRepo.delete(id));
        this.notifyObservers(new UserChangeEvent(ChangeEventType.REMOVE,null,u.get()));
    }

    /**
     * Finds a user
     *
     * @param id:-int,user's id
     *           -id must not be null
     * @return User-if user is found
     * @throws RepoExceptions if id is not found
     */
    public User findUser(ID id) {

        Optional<User> u = userRepo.findOne(id);
        if (u.isPresent())
            return u.get();
        else throw new RepoExceptions("User does not exist");


    }

    /**
     * Updates a user
     *
     * @param id:-int,user's id
     *           -id must not be null
     * @param first_name:string,user's first name
     * @param last_name:string,user's  last name
     * @return -
     * @throws ValidationException if the updated user is not valid
     * @throws RepoExceptions if the id is not found
     */
    public void updateUser(Integer id, String first_name, String last_name, String email, String pass) {
        User updatedUser = new User(first_name, last_name, email, pass);
        updatedUser.setId(id);

        try {
            validator.validator(updatedUser);
            Optional<User> oldUser = userRepo.update(updatedUser);
            this.notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE,updatedUser,oldUser.get()));

        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns all entities
     *
     * @return :Entity
     */
    public Iterable<User> getAll() {
        return userRepo.findAll();
    }
    public List<Friendship> getFriends(ID id,Integer luna)
    {

        List<Friendship> friendships = (List<Friendship>) friendshipRepo.findAll();
        return friendships.stream().filter(x->{
            try {
                Method m = x.getId().getLeft().getClass().getMethod("getId");
                Method m2 = x.getId().getRight().getClass().getMethod("getId");
                return m.invoke(x.getId().getLeft()) == id || m2.invoke(x.getId().getRight()) == id;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        }).filter(x-> x.getFriendsFrom().getMonth().getValue() == luna).collect(Collectors.toList());



        /*Optional<User> u= userRepo.findOne(id);


        Class<? extends Repository> us=userRepo.getClass();
        try {
                Method m=us.getMethod("getF",User.class);
                Map<LocalDate,User> users= (Map<LocalDate, User>) m.invoke(userRepo,u.get());
                return users;

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
*/

       /* Iterable entitiesList=userRepo.findAll();
        List<User> friendshipList=new ArrayList<>();
        entitiesList.forEach(x->{
            if(x.getClass()==Friendship.class)
            {
                if(((Friendship) x).getId().getLeft()==id)
                {

                    Optional<User> u= userRepo.findOne(((Friendship) x).getId().getRight());
                    friendshipList.add(u.get());
                }
                else if(((Friendship) x).getId().getRight()==id)
                {

                    Optional<User> u= userRepo.findOne(((Friendship) x).getId().getLeft());
                    friendshipList.add(u.get());
                }
            }
        });
        return friendshipList;*/

    }
    public List<Friend> getAllFriendships() {
        List<Friend> friends = new ArrayList<>();
        List<Friendship> friendships = (List<Friendship>) this.friendshipRepo.findAll();
        friendships.forEach(x->{
            User u1 = (User) x.getId().getLeft();
            User u2 = (User) x.getId().getRight();

            Friend friend = new Friend(u1.getId(),u2.getId(),u1.getFirstName()+" "+u1.getLastName(),u2.getFirstName()+" "+u2.getLastName(),x.getFriendsFrom());
            friends.add(friend);
        });
        return friends;
    }
    @Override
    public void addObserver(Observer<UserChangeEvent> observer) {

        this.observers.add(observer);
    }

    @Override
    public void notifyObservers(UserChangeEvent event) {
        this.observers.forEach(x->x.update(event));
    }
    public Page<Friend> getFriends(Pageable p, ID id) {
            Page<Friendship> friendshipPage = this.friendshipRepo.findAll(p, (Integer) id);
            List<Friendship> friendships = (List<Friendship>) friendshipPage.getElementsOfPage();
            List<Friend> friends = new ArrayList<>();

            friendships.forEach(x->{
            User u1 = (User) x.getId().getLeft();
            User u2 = (User) x.getId().getRight();
            if(u1.getId() == id) {
                Friend friend = new Friend(u1.getId(), u2.getId(), u1.getFirstName() + " " + u1.getLastName(), u2.getFirstName() + " " + u2.getLastName(), x.getFriendsFrom());
                friend.setId(u2.getId());
                friends.add(friend);
            }
            else {
                Friend friend = new Friend(u2.getId(), u1.getId(),u2.getFirstName() + " " + u2.getLastName(), u1.getFirstName() + " " + u1.getLastName(), x.getFriendsFrom());
                friend.setId(u2.getId());
                friends.add(friend);
            }
            });
            return new Page<>(friends, friendshipPage.getElementCount());

    }
    public List<Friend> getFriends(ID id) {

        List<Friendship> friendships = (List<Friendship>) friendshipRepo.findAll();
        List<Friendship> friendships1 = friendships.stream().filter(x -> {
            try {
                Method m = x.getId().getLeft().getClass().getMethod("getId");
                Method m2 = x.getId().getRight().getClass().getMethod("getId");
                return m.invoke(x.getId().getLeft()) == id || m2.invoke(x.getId().getRight()) == id;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        }).collect(Collectors.toList());
        List<Friend> friends = new ArrayList<>();
        friendships1.forEach(x->{
            User u1 = (User) x.getId().getLeft();
            User u2 = (User) x.getId().getRight();
            if(u1.getId() == id) {
                Friend friend = new Friend(u1.getId(), u2.getId(), u1.getFirstName() + " " + u1.getLastName(), u2.getFirstName() + " " + u2.getLastName(), x.getFriendsFrom());
                friends.add(friend);
            }
            else {
                Friend friend = new Friend(u2.getId(), u1.getId(),u2.getFirstName() + " " + u2.getLastName(), u1.getFirstName() + " " + u1.getLastName(), x.getFriendsFrom());
                friends.add(friend);
            }
        });
        return friends;
    }
    public Tuple<Integer,String> findUserByMail(String email)
    {
        if(this.userRepo.findByEmail(email).isPresent())
        {
            return (Tuple<Integer, String>) this.userRepo.findByEmail(email).get();
        }
        else throw new RepoExceptions("Email not found");
    }
    public List<User> findByLastName(String letters)
    {
        List<User> users = (List<User>) this.userRepo.findAll();
        if(letters != null)
            return users.stream().filter(x->x.getLastName().contains(letters)||x.getFirstName().contains(letters)).collect(Collectors.toList());
        else {
            return new ArrayList<>();
        }
    }

    public void verifyPassword(String userPass, String pass) {
        try {

            MessageDigest passCipher = MessageDigest.getInstance("SHA-256");
            byte[] bytes = passCipher.digest(pass.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(bytes[4]);
            stringBuilder.append(bytes[2]);
            stringBuilder.append(bytes[8]);
            stringBuilder.append(bytes[9]);
            if(!userPass.equals(stringBuilder.toString()))
            {
                throw new RepoExceptions("Invalid password");
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Friend> computePage(ID myId) {
        Page<Friend> friendsPage = this.getFriends(new Pageable(this.currentPage, this.pageSize), myId);
        int maxPage = (int) Math.ceil((double) friendsPage.getElementCount() / this.pageSize ) - 1;
        if(maxPage == -1)
            maxPage = 0;
        if(this.currentPage > maxPage)
        {
            currentPage = maxPage;
            friendsPage = this.getFriends(new Pageable(this.currentPage, this.pageSize), myId);
        }
        return friendsPage;
    }

    public void updatePageConfig(Page<Friend> friendsPage, Button previousButton, Button nextButton, Spinner<Integer> spinnerId) {
        if(this.totalNoElements == 0 ||  this.totalNoElements != friendsPage.getElementCount()) {
            this.totalNoElements = friendsPage.getElementCount();
            spinnerId.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,this.totalNoElements));
        }

        this.totalNoElements = friendsPage.getElementCount();
        previousButton.setDisable(this.currentPage == 0);
        nextButton.setDisable((this.currentPage + 1) * this.pageSize >= this.totalNoElements);
    }

    public void initSpinner(Spinner<Integer> spinnerId) {
        spinnerId.setOnMouseClicked(x->{
            this.pageSize = spinnerId.getValue();
            notifyObservers(null);


        });
    }

    public void increasePage() {
        this.currentPage ++;
    }

    public void decreasePage() {
        this.currentPage --;
    }
}
