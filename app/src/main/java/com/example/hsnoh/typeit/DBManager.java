package com.example.hsnoh.typeit;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;


public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE WORD_SET( _id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, word_in_Korean TEXT);");
        db.execSQL("CREATE TABLE SENTENCE_SET( _id INTEGER PRIMARY KEY AUTOINCREMENT, sentence TEXT, translation TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String getWord(int num) {
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        int i = 0;
        Cursor cursor = db.rawQuery("select * from WORD_SET", null);
        while (cursor.moveToNext()) {
            if (i == num) {
                string = cursor.getString(1);
                break;
            } else i++;
        }
        return string;
    }

    public String getWordInKorean(int num) {
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        int i = 0;
        Cursor cursor = db.rawQuery("select * from WORD_SET", null);
        while (cursor.moveToNext()) {
            if (i == num) {
                string = cursor.getString(2);
                break;
            } else i++;
        }
        return string;
    }

   /* public boolean isAcceptableAnswer(String wordInKorean, String inputWord) {
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        Cursor cursor = db.rawQuery("select * from WORD_SET where word_in_Korean =" + wordInKorean, null);
        while (cursor.moveToNext()) {
            if (inputWord.equals(cursor.getShort(1))) return true;
        }
        return false;
    }*/

    public String getSentence(int num) {
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        int i = 0;
        Cursor cursor = db.rawQuery("select * from SENTENCE_SET", null);
        while (cursor.moveToNext()) {
            if (i == num) {
                string = cursor.getString(1);
                break;
            } else i++;
        }
        return string;
    }
    public String getTranslation(int num) {
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        int i = 0;
        Cursor cursor = db.rawQuery("select * from SENTENCE_SET", null);
        while (cursor.moveToNext()) {
            if (i == num) {
                string = cursor.getString(2);
                break;
            } else i++;
        }
        return string;
    }
    public String getAnswer(int num) {
        String blank = "-------";
        SQLiteDatabase db = getReadableDatabase();
        String string = "";
        int answer;
        int i = 0;
        Cursor cursor = db.rawQuery("select * from SENTENCE_SET", null);
        while (cursor.moveToNext()) {
            if (i == num) {
                string = cursor.getString(1);
                answer = cursor.getInt(2);
                string = string.replace(blank, cursor.getString(answer + 2));
                break;
            } else i++;
        }

        return string;
    }


    //  public void searchTable(String searchName) {
    //      SQLiteDatabase db = getReadableDatabase();
    //      Cursor c = db.rawQuery("select * from " + searchName, null);
    //  }

}