package com.example.socialprojectgui.utils;

public interface Observable<E extends Event> {
    public void addObserver(Observer<E> observer);
    public void notifyObservers(E event);
}
