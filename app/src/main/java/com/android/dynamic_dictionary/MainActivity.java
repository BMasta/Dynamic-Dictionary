package com.android.dynamic_dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
    private ConstraintLayout constraintLayoutRoot;
    private ConstraintLayout constraintLayoutDesc;
    private ViewPager2 pagerVariations;
    private ConstraintSet setDescVisible, setDescInvisible;
    private VariationsPagerAdapter pagerAdapter;

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
        constraintLayoutRoot = findViewById(R.id.constrainedLayoutRoot);
        constraintLayoutDesc = findViewById(R.id.constrainedLayoutDesc);
        pagerVariations = findViewById(R.id.pagerVariations);
        //-----------------------------define global variables-----------------------------//
        words = new ArrayList<>();
        dbHelper = new DatabaseHelper(MainActivity.this);
        //----------------------------------set listeners----------------------------------//
        listViewWords.setOnItemClickListener(this);
        buttonAdd.setOnClickListener(this);
        constraintLayoutRoot.setOnClickListener(this);
        registerForContextMenu(listViewWords);
        //---------------------------------------------------------------------------------//
    }

    @Override
    protected void onStart() {
        super.onStart();
        //-------------------------------call init functions-------------------------------//
        constraintUpdate();
        dbSync();
        listUpdate();
        //---------------------------------------------------------------------------------//
        WebDictionary.sendMultipleDefinitionRequests(this, words);
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
        constraintLayoutDesc.setVisibility(View.VISIBLE);
        constraintUpdate();
        WordEntry e = words.get(getIndexByWord(words, (String) listViewWords.getItemAtPosition(position)));
        ((TextView) findViewById(R.id.textViewWord)).setText(e.getWord());
        Lifecycle l = new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        }
        pagerAdapter = new VariationsPagerAdapter(getSupportFragmentManager(), e.getWordVariations().get(0));
        pagerVariations.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutVariations);
    }

    @Override
    public void onBackPressed() {
        if (constraintLayoutDesc.getVisibility() == View.VISIBLE) {
            constraintLayoutDesc.setVisibility(View.INVISIBLE);
            constraintUpdate();
        }
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
        WebDictionary.sendDefinitionRequest(this, word);
        listUpdate();
    }

    @Override
    public void handleEditDialogData(int position, String word, String desc) {
        editWord(position, word, desc, true);
        listUpdate();
    }

    @Override
    public void handleResponseData(String word, String response) {
        WordEntry we = new WordEntry(word, "", response);
        editWord(word, word, words.get(getIndexByWord(words, word)).getDescription(), response, false);
        //Toast.makeText(this, "Assigned meanings for \"" + word + "\"", Toast.LENGTH_SHORT).show();
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
        WordEntry newEntry = new WordEntry(wd, desc, meaningsJson);
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
     * @param oldWord       Old word
     * @param newWord       Modified version of a word
     * @param newDesc       Modified version of a description
     * @param toastFeedback If set to true, a toast feedback message would appear upon addition
     * @return Was the edit successful
     */
    public boolean editWord(String oldWord, String newWord, String newDesc, boolean toastFeedback) {
        WordEntry entry = new WordEntry(newWord, newDesc, words.get(getIndexByWord(words, oldWord)).getWordsJson());
        boolean edited = dbHelper.edit(oldWord, entry);
        if (edited) {
            words.set(getIndexByWord(words, oldWord), entry);

            return true;
        } else
            return false;
    }

    public boolean editWord(int position, String newWord, String newDesc, boolean toastFeedback) {
        return editWord((String) listViewWords.getItemAtPosition(position), newWord, newDesc, toastFeedback);
    }

    public boolean editWord(int position, String newWord, String newDesc, String newMeaningsJson, boolean toastFeedback) {
        return editWord((String) listViewWords.getItemAtPosition(position), newWord, newDesc, newMeaningsJson, toastFeedback);
    }

    public boolean editWord(String oldWord, String newWord, String newDesc, String newMeaningsJson, boolean toastFeedback) {
        WordEntry entry = new WordEntry(newWord, newDesc, newMeaningsJson);
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

    public void constraintUpdate() {
        // init sets
        setDescVisible = new ConstraintSet();
        setDescInvisible = new ConstraintSet();
        setDescVisible.clone(constraintLayoutRoot);
        setDescInvisible.clone(constraintLayoutRoot);
        setDescVisible.connect(R.id.floatingActionButtonAdd, ConstraintSet.BOTTOM, R.id.constrainedLayoutDesc, ConstraintSet.TOP);
        setDescVisible.setVerticalBias(R.id.floatingActionButtonAdd, 0.7f);
        setDescInvisible.connect(R.id.floatingActionButtonAdd, ConstraintSet.BOTTOM, R.id.constrainedLayoutRoot, ConstraintSet.BOTTOM);
        setDescInvisible.setVerticalBias(R.id.floatingActionButtonAdd, 0.9f);

        // apply sets
        if (constraintLayoutDesc.getVisibility() == View.VISIBLE)
            setDescVisible.applyTo(constraintLayoutRoot);
        else
            setDescInvisible.applyTo(constraintLayoutRoot);
    }

    //********************************************************************************************//
}