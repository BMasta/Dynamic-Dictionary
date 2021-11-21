package com.android.dynamic_dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewGroupCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener,
        NewWordDialog.NewWordDialogListener, EditDescDialog.EditWordDialogListener,
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
        constraintLayoutDesc = findViewById(R.id.layoutDesc);
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
        dbSync();
        listUpdate();
        //---------------------------------------------------------------------------------//
        WebDictionary.updateDefinitions(this, words);
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
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        constraintLayoutDesc.setVisibility(View.VISIBLE);

        WordEntry e = words.get(getIndexByWord(words, (String) listViewWords.getItemAtPosition(position)));
        pagerAdapter = new VariationsPagerAdapter(this, e.getWordVariations(), e.getWord(), e.getDescription());
        pagerVariations.setAdapter(pagerAdapter);
        pagerVariations.setCurrentItem(1, false);

        int height = constraintLayoutRoot.getHeight();
        constraintLayoutDesc.setTranslationY(height);
        buttonAdd.animate().alpha(0.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dim();
                constraintLayoutDesc.animate().translationY(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        constraintLayoutDesc.clearAnimation();
                    }
                });
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (constraintLayoutDesc.getVisibility() == View.VISIBLE) {
            int height = constraintLayoutDesc.getHeight();
            unDim();
            constraintLayoutDesc.animate().alpha(0.0f).translationY(height).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    constraintLayoutDesc.clearAnimation();
                    constraintLayoutDesc.setAlpha(1.0f);
                    constraintLayoutDesc.setVisibility(View.GONE);
                    buttonAdd.animate().alpha(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            constraintLayoutDesc.clearAnimation();
                            buttonAdd.clearAnimation();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.optionEdit:
                String oldWord = (String) listViewWords.getItemAtPosition(info.position);
                String oldDesc = words.get(getIndexByWord(words, oldWord)).getDescription();
                openEditWordDialog(getIndexByWord(words, oldWord), oldWord, oldDesc);
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
        EditDescDialog editDescDialog = new EditDescDialog(position, oldWord, oldDesc);
        editDescDialog.show(getSupportFragmentManager(), "Edit Word Dialog");
    }

    //***************************************data handlers****************************************//
    @Override
    public void handleNewDialogData(String word, String desc) {
        addWord(word, desc, "", true);
        WebDictionary.sendDefinitionRequest(this, word, null);
        listUpdate();
    }

    @Override
    public void handleEditDialogData(int position, String desc) {
        editWord(position, words.get(position).getWord(), desc, true);
        listUpdate();
    }

    @Override
    public void handleResponseData(String word, String responseData, WebDictionary.Responses responseType, VariationsPagerAdapter.ViewPagerViewHolder holder) {
        switch (responseType) {
            case SUCCESS:
                WordEntry we = new WordEntry(word, "", responseData);
                editWord(word, word, words.get(getIndexByWord(words, word)).getDescription(), responseData, false);
                break;
            case NO_DATA:

                break;
            case RESPONSE_ERROR:

                break;
            case NO_RESPONSE:

                break;
        }
        if (holder != null)
            pagerAdapter.applyDictUpdate(word, responseData, responseType, holder);
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
        // skip blank words
        if (wd.equals("")) {
            if (toastFeedback)
                Toast.makeText(this, "Word can't be blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        WordEntry newEntry = new WordEntry(wd, desc, meaningsJson);
        DatabaseHelper.Returns added = dbHelper.add(newEntry);
        if (toastFeedback) {
            if (added == DatabaseHelper.Returns.ADDED)
                Toast.makeText(MainActivity.this, "Successfully added new element", Toast.LENGTH_SHORT).show();
            else if (added == DatabaseHelper.Returns.NOT_ADDED_ERROR)
                Toast.makeText(MainActivity.this, "Failed to add the element", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Word already exists", Toast.LENGTH_SHORT).show();
        }
        if (added == DatabaseHelper.Returns.ADDED) {
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
        DatabaseHelper.Returns updated = dbHelper.update(oldWord, entry);
        if (updated == DatabaseHelper.Returns.UPDATED) {
            words.set(getIndexByWord(words, oldWord), entry);
            return true;
        } else
            return false;
    }

    public boolean editWord(int position, String newWord, String newDesc, boolean toastFeedback) {
        return editWord(words.get(position).getWord(), newWord, newDesc, toastFeedback);
    }

    public boolean editWord(int position, String newWord, String newDesc, String newMeaningsJson, boolean toastFeedback) {
        return editWord(words.get(position).getWord(), newWord, newDesc, newMeaningsJson, toastFeedback);
    }

    public boolean editWord(String oldWord, String newWord, String newDesc, String newMeaningsJson, boolean toastFeedback) {
        WordEntry entry = new WordEntry(newWord, newDesc, newMeaningsJson);
        DatabaseHelper.Returns updated = dbHelper.update(oldWord, entry);
        if (updated == DatabaseHelper.Returns.UPDATED) {
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
        if (dbHelper.delete(wd) == DatabaseHelper.Returns.DELETED) {
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

    private void dim() {
        findViewById(R.id.layoutMain_Dimmer).animate().alpha(0.60f);
    }

    private void unDim() {
        findViewById(R.id.layoutMain_Dimmer).animate().alpha(0.0f);
    }
    //********************************************************************************************//

    public interface MainActivityInterface {
        public void applyDictUpdate(String word, String responseData, WebDictionary.Responses responseType, VariationsPagerAdapter.ViewPagerViewHolder holder);
    }
}
