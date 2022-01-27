package com.example.makepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random random =  new Random();
    Switch includeSpecial, includeUpperCase, includeLowerCase, includeNumericals, includeAmbiguous;


    private char getMyPassword(int type){
        char p = ' ';
        String symbols = "!@#$%^*?+=&";
        String ambiguous = "{}[]()/\\'\"`~,;:.<>";
        switch (type){
            case 0:
                if (includeLowerCase.isChecked())
                p = (char)(random.nextInt(26)+'a'); // random alphabet
            break;
            case 1: if(includeSpecial.isChecked()){
                p = symbols.charAt(random.nextInt(symbols.length())); // random special char
                }
                break;
            case 2:
                if(includeNumericals.isChecked())
                p = (char)(random.nextInt(9) + 48); // random number
            break;
            case 3:
                if(includeUpperCase.isChecked())
                p = (char)(random.nextInt(26)+'A'); // random alphabet CAPS
                break;
            case 4:
                if(includeAmbiguous.isChecked())
                    p = ambiguous.charAt((random.nextInt(ambiguous.length())));
                break;
        }
        return p;
    }

    private void setAtleastOneVarientON(){
        if(includeSpecial.isChecked() || includeUpperCase.isChecked() || includeLowerCase.isChecked()
        || includeNumericals.isChecked() || includeAmbiguous.isChecked()){
            return;
        }else {
            includeLowerCase.setChecked(true);
            includeUpperCase.setChecked(true);

        }
    }


    private String generatepass(){
        StringBuilder password= new StringBuilder();
        char value;
        while(password.length() <= 8){
            setAtleastOneVarientON();
            value = getMyPassword(random.nextInt(5));
            if(value == ' ') continue;
            password.append(value);
        }
        return password.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button genPass = findViewById(R.id.genpass);
        final TextView displayPass = findViewById(R.id.displayPass);

        includeSpecial = findViewById(R.id.includeSpecial);
        includeUpperCase = findViewById(R.id.includeUpperChar);
        includeLowerCase = findViewById(R.id.includeLowerCase);
        includeNumericals = findViewById(R.id.includeNumericals);
        includeAmbiguous = findViewById(R.id.includeAmbiguous);
        includeLowerCase.setChecked(true);
        includeUpperCase.setChecked(true);

        Button copyBtn = findViewById(R.id.copyBtn);

        final String[] password = new String[1];
        final String lable = "pass";

        genPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password[0] = generatepass();
                displayPass.setText(password[0]);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(lable, password[0]);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "password copied !",Toast.LENGTH_SHORT).show();
            }
        });

    }
}