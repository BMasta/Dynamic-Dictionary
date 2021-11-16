package com.android.dynamic_dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener,
            NewWordDialog.NewWordDialogListener, EditWordDialog.EditWordDialogListener,
            WebDictionary.WebDictionaryResponseListener {
    //***********************************Global scope variables***********************************//

    // UI elements (Views)
    private ListView listViewWords;
    private FloatingActionButton buttonAdd;

    // local lists
    private List<WordEntry> words;

    // used to access the database
    DatabaseHelper dbHelper;

    //***************************************init functions***************************************//

    /**
    * Synchronizes local lists with the database.
    */
    public void dbSync() {
        words.clear();
        words = dbHelper.getAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //------------------------------------basic code-----------------------------------//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------get views------------------------------------//
        listViewWords = findViewById(R.id.listViewWords);
        buttonAdd = findViewById(R.id.floatingActionButtonAdd);
        ConstraintLayout constraintLayoutMain = findViewById(R.id.constrainedLayoutMain);
        registerForContextMenu(listViewWords);
        //-----------------------------define global variables-----------------------------//
        words = new ArrayList<>();
        dbHelper = new DatabaseHelper(MainActivity.this);
        //----------------------------------set listeners----------------------------------//
        listViewWords.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        constraintLayoutMain.setOnClickListener(this);
        //---------------------------------------------------------------------------------//
        WebDictionary.sendDefinitionRequest(this, "brave");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //-------------------------------call init functions-------------------------------//
        dbSync();
        listUpdate();
        //---------------------------------------------------------------------------------//
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.delete_word_menu, menu);
    }

    //**************************************event listeners***************************************//
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButtonAdd:
                openNewWordDialog();
                break;
            default:
                //--------------------Hide keyboard on outside click --------------------//
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText()) {
                    inputMethodManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
                }
                //-----------------------------------------------------------------------//
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.optionEdit:
                String oldWord = (String) listViewWords.getItemAtPosition(info.position);
                String oldDesc = words.get(getIndexByWord(words, oldWord)).getDescription();
                openEditWordDialog(info.position, oldWord, oldDesc);
                break;
            case R.id.optionDelete:
                deleteWord((String) listViewWords.getItemAtPosition(info.position), true);
                break;
            default:
                break;
        }
        listUpdate();

        return super.onContextItemSelected(item);
    }

    //*********************************dialog initiate functions**********************************//
    public void openNewWordDialog() {
        NewWordDialog newWordDialog = new NewWordDialog();
        newWordDialog.show(getSupportFragmentManager(), "New Word Dialog");
    }

    public void openEditWordDialog(int position, String oldWord, String oldDesc) {
        EditWordDialog editWordDialog = new EditWordDialog(position, oldWord, oldDesc);
        editWordDialog.show(getSupportFragmentManager(), "Edit Word Dialog");
    }

    //***************************************data handlers****************************************//
    @Override
    public void handleNewDialogData(String word, String desc) {
        addWord(word, desc, "", true);
        listUpdate();
    }

    @Override
    public void handleEditDialogData(int position, String word, String desc) {
        editWord(position, word, desc, true);
        listUpdate();
    }

    @Override
    public void handleResponseData(String word, String response) {
        WordEntry we = new WordEntry("", "", response, true);
        we.getMeanings();
    }

    //********************************word manipulation functions*********************************//

    /**
     * Adds a new word to the app.
     * Includes modifying database, local lists, and visual elements representing the words
     *
     * @param wd            A word to be added
     * @param desc          Description of the new word
     * @param toastFeedback If set to true, a toast feedback message would appear upon addition
     * @return Was the addition successful
     */
    public boolean addWord(String wd, String desc, String meaningsJson, boolean toastFeedback) {
        WordEntry newEntry = new WordEntry(wd, desc, meaningsJson, false);
        boolean added = dbHelper.add(newEntry);
        if (toastFeedback) {
            if (added)
                Toast.makeText(MainActivity.this, "Successfully added new element", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Failed to add the element", Toast.LENGTH_SHORT).show();
        }
        if (added) {
            words.add(newEntry);
            return true;
        } else return false;
    }

    /**
     * Edits an existing word.
     * Includes modifying database, local lists, and visual elements representing the words
     *
     * @param position      Position of an object that holds the word in a ListView
     * @param newWord       Modified version of a word
     * @param newDesc       Modified version of a description
     * @param toastFeedback If set to true, a toast feedback message would appear upon addition
     * @return Was the edit successful
     */
    public boolean editWord(int position, String newWord, String newDesc, boolean toastFeedback) {
        String oldWord = (String) listViewWords.getItemAtPosition(position);
        WordEntry entry = new WordEntry(newWord, newDesc, words.get(getIndexByWord(words, oldWord)).getMeaningsJson(), false);
        boolean edited = dbHelper.edit(oldWord, entry);
        if (edited) {
            words.set(getIndexByWord(words, oldWord), entry);

            return true;
        } else
            return false;
    }
    public boolean editWord(int position, String newWord, String newDesc, String newMeaningsJson, boolean toastFeedback) {
        String oldWord = (String) listViewWords.getItemAtPosition(position);
        WordEntry entry = new WordEntry(newWord, newDesc, newMeaningsJson, false);
        boolean edited = dbHelper.edit(oldWord, entry);
        if (edited) {
            words.set(getIndexByWord(words, oldWord), entry);

            return true;
        } else
            return false;
    }

    /**
     * Deletes existing word
     * Includes modifying database, local lists, and visual elements representing the words
     *
     * @param wd            Word to be deleted
     * @param toastFeedback If set to true, a toast feedback message would appear upon addition
     * @return Was the deletion successful
     */
    public int deleteWord(String wd, boolean toastFeedback) {
        int deleted = 0;
        if (dbHelper.delete(wd)) {
            while (true) {
                int i = getIndexByWord(words, wd);
                if (i == -1)
                    break;
                else {
                    deleted++;
                    words.remove(i);
                }
            }
            listUpdate();
            if (toastFeedback)
                Toast.makeText(this, "Successfully deleted " + deleted + " element(s)", Toast.LENGTH_SHORT).show();
        } else {
            if (toastFeedback)
                Toast.makeText(this, "Failed to delete the element", Toast.LENGTH_SHORT).show();
        }
        return deleted;
    }

    //***************************************misc functions***************************************//

    /**
    * Updates ListView with the data in local lists.
    */
    public void listUpdate() {
        List<WordEntry> reversedWords = new ArrayList<>(words);
        Collections.reverse(reversedWords);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getWordArray(reversedWords));
        listViewWords.setAdapter(adapter);
    }

    public String[] getWordArray(List<WordEntry> list) {
        String[] res = new String[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            res[i] = list.get(i).getWord();
        }
        return res;
    }

    public int getIndexByWord(List<WordEntry> list, String word) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getWord().equals(word))
                return i;
        }
        return -1;
    }

    //********************************************************************************************//
}