package com.android.dynamic_dictionary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "WORDS_TABLE", COL_WORD = "WORD", COL_DESC = "DESCRIPTION", COL_MEANINGS = "MEANINGSJSON";
    private static final int COL_WORD_INDEX = 0, COL_DESC_INDEX = 1, COL_MEANINGS_INDEX = 2;

    public enum Returns {
        ADDED,
        UPDATED,
        UPDATED_ADDED_NEW,
        DELETED,
        NOT_ADDED_ALREADY_EXISTS,
        NOT_ADDED_ERROR,
        NOT_UPDATED,
        NOT_DELETED
    }

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME + ".db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " "
                + "("
                + COL_WORD + " TEXT, "
                + COL_DESC + " TEXT, "
                + COL_MEANINGS + " TEXT"
                + ")";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Returns add(WordEntry entry) {
        if (find(entry.getWord()) != null)
            return Returns.NOT_ADDED_ALREADY_EXISTS;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, entry.getWord());
        cv.put(COL_DESC, entry.getDescription());
        cv.put(COL_MEANINGS, entry.getWordsJson());
        long feedback = db.insert(TABLE_NAME, null, cv);

        if (feedback != -1)
            return Returns.ADDED;
        else
            return Returns.NOT_ADDED_ERROR;
    }

    public List<WordEntry> getAll() {
        List<WordEntry> data = new ArrayList<>();
        String queryStr = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor.moveToFirst()) {
            do {
                String desc = cursor.getString(COL_DESC_INDEX);
                // remove redundant characters and see if the word is blank
                if (desc != null && desc.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").equals(""))
                    desc = null;
                data.add(new WordEntry(cursor.getString(COL_WORD_INDEX), desc, cursor.getString(COL_MEANINGS_INDEX)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public Returns update(String oldWord, WordEntry newEntry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, newEntry.getWord());
        cv.put(COL_DESC, newEntry.getDescription());
        cv.put(COL_MEANINGS, newEntry.getWordsJson());
        int updated = db.update(TABLE_NAME, cv, COL_WORD + "=?", new String[]{oldWord});
        if (updated == 0) {
            if (add(newEntry) == Returns.NOT_ADDED_ERROR)
                return Returns.NOT_UPDATED;
            else
                return Returns.UPDATED_ADDED_NEW;
        }
        return Returns.UPDATED;
    }

    public Returns delete(String word) {
        SQLiteDatabase db = getWritableDatabase();
        int deleted = db.delete(TABLE_NAME, COL_WORD + "=?", new String[]{word});
        if (deleted > 0)
            return Returns.DELETED;
        else
            return Returns.NOT_DELETED;
    }

    public WordEntry find(String word) {
        String desc, wordsJson;
        String queryString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_WORD + " = ?";
        String[] selArgs = {word};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, selArgs);
        if (cursor.moveToFirst()) {
            desc = cursor.getString(COL_DESC_INDEX);
            wordsJson = cursor.getString(COL_MEANINGS_INDEX);
            cursor.close();
            return new WordEntry(word, desc, wordsJson);
        } else
            return null;
    }
}
