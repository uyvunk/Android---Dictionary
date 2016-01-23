package com.example.vnguyen.dictionary;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Set database
    SQLiteDatabase DICT_DATA;
    boolean DICT_AVAILABLE = false;
    String SQL_FILE_PATH = "dict_en_vi.sql";
    String DATABASE_NAME = "dictionary";
    String DICT_TABLE_NAME = "dict_en_vi";
    String WORD = "word";
    String PHONETIC = "phonetic";
    String MEANING = "meanings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getDatabase();
    }

    public void getDatabase() {
        //DICT_AVAILABLE = checkDict();
        if(!DICT_AVAILABLE){
            // Dictionary is not available, wait to import form SQL file
            DICT_DATA = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            DICT_DATA.execSQL("CREATE TABLE IF NOT EXISTS dict_en_vi(id INTEGER PRIMARY KEY AUTOINCREMENT,word varchar(64) NOT NULL,phonetic varchar(64) NOT NULL,meanings text NOT NULL);");
            DICT_DATA.execSQL("INSERT INTO dict_en_vi VALUES(1,'hello','helo','Xin chao');");
//            try {
//                String file = readSQL();
//                DICT_DATA.execSQL(file);
//
//            } catch (IOException e) {
//                Log.e("ERROR", " WHEN READ SQL FILE",e);
//            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // find definition of the input word
    public void findWord(View view) {
        EditText editText = (EditText) findViewById(R.id.input);
        String input = format(editText.getText().toString());
        String result;
        if(input == null || input.length() == 0) {
            result = "Empty Input";
        } else {
            result = lookUp(input);
        }

        // Display the result to the definition textView

        TextView textView = (TextView) findViewById(R.id.definition);
        textView.setText(result);

    }

    // format the input
    public String format(String in) {
        return in.toLowerCase().trim();
    }

    // find the word
    public String lookUp(String in) {
        String sql = "SELECT * FROM " + DICT_TABLE_NAME + " WHERE " + WORD + "=" + in;
        Cursor resultSet = DICT_DATA.rawQuery(sql, null);
        resultSet.moveToFirst();
        String phonetic = resultSet.getString(3);
        String meaning = resultSet.getString(4);

        return "\n" + phonetic + "\n" + meaning;

    }

    // Check if dictionary is available
    public boolean checkDict() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e){
            Log.e("ERROR", " WHEN CHECK SQL DB", e);
            return false;
        }

        return true;
    }

    // Read the SQL command from a file
    public String readSQL() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SQL_FILE_PATH));
            String read;
            while((read = bufferedReader.readLine()) != null) {
                stringBuffer.append(read);
            }
        }finally {

        }
        return stringBuffer.toString();
    }
}
