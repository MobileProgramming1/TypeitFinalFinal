package com.example.hsnoh.typeit;

import com.example.hsnoh.typeit.DBManager;
import com.example.hsnoh.typeit.Egg;

import java.util.Random;

class word extends Egg {

    protected String word;
    protected String wordInKorean;

    public word(DBManager dbManager) {
        Random rand = new Random();
        int random = rand.nextInt(90);
        word = dbManager.getWord(random);
        wordInKorean = dbManager.getWordInKorean(random);
    }

    public String getWord() {
        return word;
    }

    public String getWordInKorean() {
        return wordInKorean;
    }
}
class randomWord extends word {
    //  private String word;
    //  private String wordInKorean;
    public final int lengthOfWord;

    public randomWord(DBManager dbManager) {
        super(dbManager);
        lengthOfWord = word.length();
    }

    public boolean isLetterInWord(char letter) {
        if (word.indexOf(letter) < 0) return false;
        return true;
    }//문자가 단어안에 있는지 확인

    @Override
    public boolean equals(Object other) {
        return word.equals(other);
    }

    public char charAt(int index) {
        return word.charAt(index);
    }

}