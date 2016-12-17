package com.example.hsnoh.typeit;

import java.util.ArrayList;

class Accuracy {
    private double accuracy;
    private int characterCount;
    private int wrongCount;

    public Accuracy(double accuracy, int characterCounter, int wrongCount) {
        this.accuracy = accuracy;
        this.wrongCount = wrongCount;
        this.characterCount = characterCounter;
    }

    public void updateAccuracy() {
        accuracy = (characterCount <= 0 ? 0 : 100 * (characterCount - wrongCount) / (double) characterCount);
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int count) {
        characterCount = count;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int count) {
        wrongCount = count;
    }
}

class Memento {
    private Accuracy accuracy;

    public Memento(Accuracy accuracy) {
        this.accuracy = accuracy;
    }

    public Accuracy getAccuracy() {
        return accuracy;
    }

   // public void updateAccuracy(Accuracy accuracy) {
   //     this.accuracy = accuracy;
    //}
}

class Caretaker {
    private ArrayList<Memento> accuracyList = new ArrayList<Memento>();

    public void addMemento(Memento memento) {
        accuracyList.add(memento);
    }

    public void removeMemento(int index) {
        accuracyList.remove(index);
    }

    public Memento getMemento(int index) {
        Memento memento = accuracyList.get(index);
        //removeMemento(memento);
        return memento;
    }
}
class Originator {
    Accuracy accuracy;

    public Accuracy getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Accuracy accuracy) {
        this.accuracy = accuracy;
    }

    public Memento createMemento() {
        return new Memento(accuracy);
    }

    public void restoreMemento(Memento memento) {
        accuracy = memento.getAccuracy();

    }
}