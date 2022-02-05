package com.example.makepassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {
    Random random =  new Random();
    CheckBox uppercaseCB, lowercaseCB, numeralsCB, symbolsCB;
    final private  String[] logs = new String[10];
    private static int rear=0, prev=0, capacity = 10;
    private static String[] queue = new String[capacity];

    static void insertValueToQueue(String value){
        if(value.equals(queue[prev])) return;
        queue[rear] = value;
        prev = rear;
        rear = ((rear+1) % capacity);
    }
    static String[] getValueFromQueue(){
        return queue;
    }
    private char getPasswordChar(int type){
        char p = ' ';
        String symbols = "!@#$%*?+=&";
        String ambiguous = "{}[]()/\\'\"`~,;:.<>^";
        switch (type){
            case 0:
                if(lowercaseCB.isChecked())
                    p = (char)(random.nextInt(26)+'a'); // random alphabet
                System.out.println("lowerCase");
            break;
            case 1:
                if(symbolsCB.isChecked())
                    p = symbols.charAt(random.nextInt(symbols.length())); // random special char
                break;
            case 2:
                if(numeralsCB.isChecked())
                    p = (char)(random.nextInt(9) + 48); // random number
            break;
            case 3:
                if(uppercaseCB.isChecked())
                    p = (char)(random.nextInt(26)+'A'); // random alphabet CAPS
                break;
            default:
                    p = ambiguous.charAt((random.nextInt(ambiguous.length())));
                break;
        }
        return p;
    }
    private void setVariantsON(){
        if(!(symbolsCB.isChecked() || uppercaseCB.isChecked() || lowercaseCB.isChecked()
                || numeralsCB.isChecked())) {
            uppercaseCB.setChecked(true);
            lowercaseCB.setChecked(true);
        }
    }
    private String generatePassword(int passLEN){
        StringBuilder password= new StringBuilder();
        char value;
        while(password.length() < passLEN){
            setVariantsON();
            value = getPasswordChar(random.nextInt(4));
            if(value == ' ') continue;
            password.append(value);
        }
        return password.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)){

            setContentView(R.layout.main_layout2);

            final TextView displayPassword = findViewById(R.id.displayPass);
            final TextView displayPassLen = findViewById(R.id.displayPassLen);

            ImageButton generatePasswordBtn = findViewById(R.id.genpass);
            ImageButton copyBtn = findViewById(R.id.copyBtn);

            // CheckBox for different variants
            uppercaseCB = findViewById(R.id.uppercaseCheckbox);
            lowercaseCB = findViewById(R.id.lowercaseCheckbox);
            numeralsCB = findViewById(R.id.numeralsCheckbox);
            symbolsCB = findViewById(R.id.symbolsCheckbox);


            // "SeekBar" to set password length
            SeekBar passwordLenseekBar = findViewById(R.id.getPassLenSeekBar);

            // On start App, if lowerCase, upperCase & symbol Variants ON
            setVariantsON();

            final String label = "pass";

            passwordLenseekBar.setMin(5);
            passwordLenseekBar.setMax(30);

            // set initial seekBar progress to 8
            passwordLenseekBar.setProgress(12);

            displayPassword.setText(generatePassword(passwordLenseekBar.getProgress()));
            displayPassLen.setText(String.valueOf(passwordLenseekBar.getProgress()));

            // change the password length according to user
            passwordLenseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    displayPassLen.setText(String.valueOf(seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    displayPassword.setText(generatePassword(seekBar.getProgress()));
                }
            });

            // generate password based on seekBar length
            generatePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayPassword.setText(generatePassword(passwordLenseekBar.getProgress()));
                }
            });

            // copy password to clipBoard
            copyBtn.setOnClickListener(v -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(label, displayPassword.getText());
                clipboardManager.setPrimaryClip(clipData);
                makeText(MainActivity.this, "password copied !",Toast.LENGTH_SHORT).show();
                insertValueToQueue(String.valueOf(displayPassword.getText()));
            });
        }

        else setContentView(R.layout.landscape);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.END))
            drawer.closeDrawer(GravityCompat.END);
        else super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println(rear);
        if (!String.valueOf(menu.getItem(menu.size()-1)).equals(queue[prev])){
            menu.clear();
            menu.add("Password Logs");
            menu.add("Copy Password ..");
            for (String x : getValueFromQueue())
                if (x != null) menu.add(x);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.left_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final String label = "pass";
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData =  ClipData.newPlainText(label, String.valueOf(item));
        clipboardManager.setPrimaryClip(clipData);
        makeText(MainActivity.this, "password copied !",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

}