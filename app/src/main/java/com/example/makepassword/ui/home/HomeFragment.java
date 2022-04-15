package com.example.makepassword.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makepassword.R;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Random;

import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Random random =  new Random();
    CheckBox uppercaseCB, lowercaseCB, numeralsCB, symbolsCB;
    final private  String[] logs = new String[10];
    private static int rear=0, prev=0, capacity = 10;
    private static String[] queue = new String[capacity];

    static String numeralsFontColor = "<font color='#FF6329'>";     //orange
    static String upperCaseFontColor = "<font color='#cc80cc'>";           // purple
    static String lowerCaseFontColor = "<font color='#5b4579'>";            // some dark grey
    static String specialCharFontColor = "<font color='#4041d4'>";        //some other blue
    static String endHtmlFont = "</font>";

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
        String q ="";
        String symbols = "!@#$%*?+=&";
        String ambiguous = "{}[]()/\\'\"`~,;:.<>^";
        switch (type){
            case 0:
                if(lowercaseCB.isChecked())
                {
                    p = (char)(random.nextInt(26)+'a'); // random alphabet
                }
                break;
            case 1:
                if(symbolsCB.isChecked())
                {
                    p = symbols.charAt(random.nextInt(symbols.length())); // random special char
                }
                break;
            case 2:
                if(numeralsCB.isChecked())
                {
                    p = (char)(random.nextInt(9) + 48); // random number
                }
                break;
            case 3:
                if(uppercaseCB.isChecked())
                {
                    p = (char)(random.nextInt(26)+'A'); // random alphabet CAPS
                }
                break;
            default:
            {
                p = ambiguous.charAt((random.nextInt(ambiguous.length())));
            }
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
        while (password.length() < passLEN){
            setVariantsON();
            value = getPasswordChar(random.nextInt(4));
            if(value == ' ') continue;
            password.append(value);
        }
        System.out.println(colorPassword(password.toString()));
        return colorPassword(password.toString());
    }

    private String colorPassword(String password){
        StringBuilder newPassword = new StringBuilder();
        for(int i=0; i<password.length(); i++){
            if(Character.isLowerCase(password.charAt(i))){
                newPassword.append(lowerCaseFontColor+password.charAt(i)+endHtmlFont);
            }
            else if(Character.isUpperCase(password.charAt(i))){
                newPassword.append(upperCaseFontColor+password.charAt(i)+endHtmlFont);
            }
            else if(Character.isDigit(password.charAt(i))){
                newPassword.append(numeralsFontColor+password.charAt(i)+endHtmlFont);
            }
            else{
                newPassword.append(specialCharFontColor+password.charAt(i)+endHtmlFont);
            }
        }
        return newPassword.toString();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView displayPassLen = root.findViewById(R.id.displayPassLen);
        final TextView displayPassword = root.findViewById(R.id.displayPass);

        ImageButton generatePasswordBtn = root.findViewById(R.id.genpass);
        ImageButton copyBtn = root.findViewById(R.id.copyBtn);

        // CheckBox for different variants
        uppercaseCB = root.findViewById(R.id.uppercaseCheckbox);
        lowercaseCB = root.findViewById(R.id.lowercaseCheckbox);
        numeralsCB = root.findViewById(R.id.numeralsCheckbox);
        symbolsCB = root.findViewById(R.id.symbolsCheckbox);


        // "SeekBar" to set password length
        SeekBar passwordLenseekBar = root.findViewById(R.id.getPassLenSeekBar);

        // On start App, if lowerCase, upperCase & symbol Variants ON
        setVariantsON();

        final String label = "pass";

        passwordLenseekBar.setMin(5);
        passwordLenseekBar.setMax(30);

        // set initial seekBar progress to 8
        passwordLenseekBar.setProgress(12);

        displayPassword.setText(Html.fromHtml(generatePassword(passwordLenseekBar.getProgress())));
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
//                    displayPassword.setText(generatePassword(seekBar.getProgress()));
                displayPassword.setText(Html.fromHtml(generatePassword(passwordLenseekBar.getProgress())));
            }

        });

        // generate password based on seekBar length
        generatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    displayPassword.setText(generatePassword(passwordLenseekBar.getProgress()));
                displayPassword.setText(Html.fromHtml(generatePassword(passwordLenseekBar.getProgress())));
            }
        });

        // copy password to clipBoard
        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(label, displayPassword.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getActivity(), "password copied !", Toast.LENGTH_SHORT).show();
            insertValueToQueue(String.valueOf(displayPassword.getText()));
        });

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        return root;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        System.out.println(rear);
//        if (!String.valueOf(menu.getItem(menu.size()-1)).equals(queue[prev])){
//            menu.clear();
//            menu.add("Password Logs");
//            menu.add("Copy Password ..");
//            for (String x : getValueFromQueue())
//                if (x != null) menu.add(x);
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.left_drawer_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        final String label = "pass";
//        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clipData =  ClipData.newPlainText(label, String.valueOf(item));
//        clipboardManager.setPrimaryClip(clipData);
//        makeText(MainActivity.this, "password copied !",Toast.LENGTH_SHORT).show();
//        return super.onOptionsItemSelected(item);
//    }

}

