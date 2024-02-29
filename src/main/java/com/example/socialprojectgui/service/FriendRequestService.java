package com.example.socialprojectgui.service;

import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Repository;
import com.example.socialprojectgui.utils.Event;
import com.example.socialprojectgui.utils.Observable;
import com.example.socialprojectgui.utils.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendRequestService implements Observable {
    private List<Observer> observers = new ArrayList<>();
    private Repository<Integer, FriendRequest> friendRequestRepo;
    private Repository<Tuple<User, User>,Friendship> friendshipRepo;

    public FriendRequestService(Repository<Integer, FriendRequest> friendRequestRepo, Repository<Tuple<User,User>,Friendship> friendshipRepo) {
        this.friendRequestRepo = friendRequestRepo;
        this.friendshipRepo = friendshipRepo;
    }

    public void save(Integer fromWhom, Integer toWhom, String nameWhom, String nameto)
    {

        User u1 = new User(null,null,null,null);
        User u2 = new User(null,null,null,null);
        u1.setId(fromWhom);
        u2.setId(toWhom);
        if(this.friendshipRepo.findOne(new Tuple<>(u1,u2)).isPresent())
            throw new DBExceptions("Friendship already exists");
        FriendRequest friendRequest = new FriendRequest(fromWhom, toWhom, nameWhom, nameto,"pending");
        this.friendRequestRepo.save(friendRequest);
        notifyObservers(null);
    }
    public void delete(Integer id)
    {
        this.friendRequestRepo.delete(id);
        notifyObservers(null);
    }

    public void update(FriendRequest friendRequest, String status)
    {
        friendRequest.setStatus(status);
        this.friendRequestRepo.update(friendRequest);
        notifyObservers(null);
    }
    public List<FriendRequest> getAllRequests()
    {
        return (List<FriendRequest>) this.friendRequestRepo.findAll();

    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        observers.forEach(x->x.update(event));

    }

    public List<FriendRequest> getRequests(Integer id, String filter) {
        List<FriendRequest> friendRequests = (List<FriendRequest>) this.friendRequestRepo.findAll();
        if(filter.equals("to"))
        {
            return friendRequests.stream().filter(x-> Objects.equals(x.getIdFromWhom(), id)).collect(Collectors.toList());
        }
        if(filter.equals("from"))
        {
            return friendRequests.stream().filter(x-> Objects.equals(x.getIdToWhom(), id)).collect(Collectors.toList());

        }
        return null;
    }
}
