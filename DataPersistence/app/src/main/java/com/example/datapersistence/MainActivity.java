package com.example.datapersistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String testFileName = "data";
    public static final String SHARE_PREFRENCE_DATA = "sharePrefrenceData";
    private EditText editText;

    private TextView sharePreferenceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text_view);
        loadTextFromlocalByStream();
        setUpSaveDataButton();
        setUpRestoreButton();
    }

    private void setUpSaveDataButton() {
        Button button = findViewById(R.id.sharePreference_button);
        button.setOnClickListener(v -> {
            saveDataViaSharePreference();
        });
    }

    private void saveDataViaSharePreference() {
        /*以mapping的形式存储着*/
        SharedPreferences.Editor editor = getSharedPreferences(SHARE_PREFRENCE_DATA,0).edit();
        editor.putString("name ","Tony");
        editor.putInt("age",25);
        editor.putBoolean("married",false);
        editor.apply();
    }

    private void setUpRestoreButton() {
        Button restore = findViewById(R.id.sharePreference_restore_button);
        restore.setOnClickListener(v -> {
            readDataFromSharePreferenceFile(SHARE_PREFRENCE_DATA);
        });
    }

    private void readDataFromSharePreferenceFile (String fileName) {
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFRENCE_DATA,0);
        String name = preferences.getString("name","N/A");
        int age = preferences.getInt("age",0);
        boolean married = preferences.getBoolean("married",false);
        String marrage = married? "YES" : "NO";
        String displayText = "name: " +name+" age: "+age+" married :"+ marrage;

        sharePreferenceTextView = findViewById(R.id.sharePreference_display);
        sharePreferenceTextView.setText(displayText);

    }

    private void loadTextFromlocalByStream() {
        String inputFromFile = loadText();
        if (!TextUtils.isEmpty(inputFromFile)) {
            editText.setText(inputFromFile);
            editText.setSelection(inputFromFile.length()); // 移动光标到末尾
            Toast.makeText(MainActivity.this, "Restoring from local file",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (editText != null) {
            String inputText = editText.getText().toString();
            saveText(inputText);
        }
    }

    public void saveText(String inputString) {
        FileOutputStream outputStream = null;
        BufferedWriter writer = null;

        try {
            outputStream = openFileOutput(testFileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(inputString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 传统的数据流读取，不适合存储比较复杂的数据
    public String loadText() {
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder content = new StringBuilder();

        try {
            inputStream = openFileInput(testFileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }


}