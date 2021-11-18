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

    public boolean add(WordEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, entry.getWord());
        cv.put(COL_DESC, entry.getDescription());
        cv.put(COL_MEANINGS, entry.getWordsJson());
        long feedback = db.insert(TABLE_NAME, null, cv);

        if (feedback == -1)
            return false;
        else
            return true;
    }

    public List<WordEntry> getAll() {
        List<WordEntry> data = new ArrayList<>();
        String queryStr = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor.moveToFirst()) {
            do {

                data.add(new WordEntry(cursor.getString(COL_WORD_INDEX), cursor.getString(COL_DESC_INDEX), cursor.getString(COL_MEANINGS_INDEX)));
            } while (cursor.moveToNext());
        }

        return data;
    }

    public boolean edit(String oldWord, WordEntry newEntry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_WORD, newEntry.getWord());
        cv.put(COL_DESC, newEntry.getDescription());
        cv.put(COL_MEANINGS, newEntry.getWordsJson());
        int updated = db.update(TABLE_NAME, cv, COL_WORD + "=?", new String[]{oldWord});
        if (updated == 0)
            return add(newEntry);
        return true;
    }

    public boolean delete(String word) {
        SQLiteDatabase db = getWritableDatabase();
        int deleted = db.delete(TABLE_NAME, COL_WORD + "=?", new String[]{word});
        return deleted > 0;
    }

    public boolean delete(WordEntry entry) {

        return delete(entry.getWord());
    }
}
