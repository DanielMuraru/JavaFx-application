package com.example.socialprojectgui.repository;

public class Page<E> {
    private Iterable<E> elementsOfPage;
    private int elementCount;

    public Page(Iterable<E> elementsOfPage, int elementCount) {
        this.elementsOfPage = elementsOfPage;
        this.elementCount = elementCount;
    }

    public Iterable<E> getElementsOfPage() {
        return elementsOfPage;
    }

    public int getElementCount() {
        return elementCount;
    }
}
