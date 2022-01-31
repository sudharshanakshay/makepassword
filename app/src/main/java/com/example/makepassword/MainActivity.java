package com.example.makepassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import javax.net.ssl.SNIHostName;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

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

    Random random =  new Random();
    Switch includeSymbol, includeUpperCase, includeLowerCase, includeNumerals, includeAmbiguous;

    final private  String[] logs = new String[10];

    private void setLogs(){
        if(logs.length == 10) ;
    }

    private char getMyPassword(int type){
        char p = ' ';
        String symbols = "!@#$%*?+=&";
        String ambiguous = "{}[]()/\\'\"`~,;:.<>^";
        switch (type){
            case 0:
                if (includeLowerCase.isChecked())
                p = (char)(random.nextInt(26)+'a'); // random alphabet
            break;
            case 1: if(includeSymbol.isChecked()){
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

    private void setVariantsON(){
        if(includeSymbol.isChecked() || includeUpperCase.isChecked() || includeLowerCase.isChecked()
        || includeNumerals.isChecked() || includeAmbiguous.isChecked()){
            return;
        }else {
            includeLowerCase.setChecked(true);
            includeUpperCase.setChecked(true);
            includeSymbol.setChecked(true);
        }
    }

    private String generatePassword(int passLEN){
        StringBuilder password= new StringBuilder();
        char value;
        while(password.length() < passLEN){
            setVariantsON();
            value = getMyPassword(random.nextInt(5));
            if(value == ' ') continue;
            password.append(value);
        }
        return password.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)){

            setContentView(R.layout.activity_main);

            final TextView displayPassword = findViewById(R.id.displayPass);
            final TextView displayPassLen = findViewById(R.id.displayPassLen);

            Button generatePasswordBtn = findViewById(R.id.genpass);
            Button copyBtn = findViewById(R.id.copyBtn);

            // "Switch" for diff Variants
            includeSymbol = findViewById(R.id.includeSymbol);
            includeUpperCase = findViewById(R.id.includeUpperChar);
            includeLowerCase = findViewById(R.id.includeLowerCase);
            includeNumerals = findViewById(R.id.includeNumericals);
            includeAmbiguous = findViewById(R.id.includeAmbiguous);

            // "SeekBar" to set password length
            SeekBar getPassLenSeekBar = findViewById(R.id.getPassLenSeekBar);

            // On start App, if lowerCase, upperCase & symbol Variants ON
            setVariantsON();

            final String[] password = new String[1];
            final String label = "pass";
            final int[] passLen = new int[1];

            getPassLenSeekBar.setMin(5);
            getPassLenSeekBar.setMax(50);

            // set initial seekBar progress to 8
            getPassLenSeekBar.setProgress(8);

            password[0] = generatePassword(getPassLenSeekBar.getProgress());
            displayPassword.setText(password[0]);
            displayPassLen.setText(String.valueOf(getPassLenSeekBar.getProgress()));

            // change the password length according to user
            getPassLenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekBar.setTooltipText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    displayPassLen.setText(String.valueOf(seekBar.getProgress()));
                }
            });

            // generate password based on seekBar length
            generatePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password[0] = generatePassword(getPassLenSeekBar.getProgress());
                    displayPassword.setText(password[0]);
                    displayPassLen.setText(String.valueOf(password[0].length()));
                    System.out.println("generated password "+password[0]);
                }
            });

            // copy password to clipBoard
            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(label, password[0]);
                    clipboardManager.setPrimaryClip(clipData);
                    makeText(MainActivity.this, "password copied !",Toast.LENGTH_SHORT).show();

                    insertValueToQueue(String.valueOf(password[0]));
                    System.out.println("password when copied "+password[0]);
                    for(String i : getValueFromQueue())
                     System.out.println("password in the iteration \t"+i);
                }
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
        if (!String.valueOf(menu.getItem(menu.size()-1)).equals(queue[prev]))
        for (String x : getValueFromQueue())
            if (x != null) menu.add(x);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.right_drawer_menu, menu);
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