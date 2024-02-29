package com.example.socialprojectgui.service;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.Message;
import com.example.socialprojectgui.domain.ReplyMessage;
import com.example.socialprojectgui.domain.Tuple;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.repository.Repository;
import com.example.socialprojectgui.utils.ChangeEventType;
import com.example.socialprojectgui.utils.Observable;
import com.example.socialprojectgui.utils.Observer;
import com.example.socialprojectgui.utils.UserChangeEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements Observable<UserChangeEvent> {
    private Repository<Tuple<Integer,List<Integer>>, Message> messageRepository;
    private Repository<Tuple<Integer,List<Integer>>, ReplyMessage> replyMessageRepository;

    private List<Observer<UserChangeEvent>> observers = new ArrayList<>();
    public MessageService(Repository<Tuple<Integer, List<Integer>>, Message> messageRepository, Repository<Tuple<Integer, List<Integer>>, ReplyMessage> replyMessageRepository) {
        this.messageRepository = messageRepository;
        this.replyMessageRepository = replyMessageRepository;
    }

    public void saveMessage(User Sender, List<User> Repliers, String text)
    {
        Message message = new Message(Sender, text, LocalDateTime.now());
        Repliers.forEach(message::addToUsers);
        this.messageRepository.save(message);
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE,null,null));
    }
    public void saveReply(User sender, Integer messageToRespond, String text)
    {
        ReplyMessage replyMessage = new ReplyMessage(sender, text, LocalDateTime.now(), messageToRespond);
        this.replyMessageRepository.save(replyMessage);
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE,null,null));
    }
    public List<MessageDTO> getMessages(Tuple<User, User> id)
    {
        return this.messageRepository.findAll(id);
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(UserChangeEvent event) {
        observers.forEach(x->x.update(event));
    }
}
