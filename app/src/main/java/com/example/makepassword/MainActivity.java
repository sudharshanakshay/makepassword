package com.example.makepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random random =  new Random();
    Switch includeSpecial, includeUpperCase, includeLowerCase, includeNumerals, includeAmbiguous;


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
                if(includeNumerals.isChecked())
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

    private void setVarientON(){
        if(includeSpecial.isChecked() || includeUpperCase.isChecked() || includeLowerCase.isChecked()
        || includeNumerals.isChecked() || includeAmbiguous.isChecked()){
            return;
        }else {
            includeLowerCase.setChecked(true);
            includeUpperCase.setChecked(true);

        }
    }

    private String generatepass(int passLEN){
        StringBuilder password= new StringBuilder();
        char value;
        while(password.length() <= passLEN){
            setVarientON();
            value = getMyPassword(random.nextInt(5));
            if(value == ' ') continue;
            password.append(value);
        }
        return password.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_body);

        Button genPass = findViewById(R.id.genpass);
        Button copyBtn = findViewById(R.id.copyBtn);
        final TextView displayPass = findViewById(R.id.displayPass);
        final TextView displayPassLen = findViewById(R.id.displayPassLen);
        SeekBar getPassLen = findViewById(R.id.getPassLenSeekBar);
        includeSpecial = findViewById(R.id.includeSpecial);
        includeUpperCase = findViewById(R.id.includeUpperChar);
        includeLowerCase = findViewById(R.id.includeLowerCase);
        includeNumerals = findViewById(R.id.includeNumericals);
        includeAmbiguous = findViewById(R.id.includeAmbiguous);
        includeLowerCase.setChecked(true);
        includeUpperCase.setChecked(true);

        final String[] password = new String[1];
        final String lable = "pass";
        final int[] passLen = new int[1];
        passLen[0] = 8;

        displayPass.setText(generatepass(passLen[0]));


        getPassLen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passLen[0] =  progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                displayPassLen.setText(passLen.toString());
            }
        });

        genPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password[0] = generatepass(passLen[0]);
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