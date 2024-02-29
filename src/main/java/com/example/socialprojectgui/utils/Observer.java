package com.example.socialprojectgui.utils;

public interface Observer<E extends Event> {

    public void update(E event);
}
