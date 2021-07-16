package com.example.olanrewaju4401.burgercaloriecalculator;

import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    //Task 1: Declare UI Objects to be referenced
    private RadioGroup pattyRG;
    private CheckBox prosciuttoCBX;
    private RadioGroup cheeseRG;
    private SeekBar sauceSBR;
    private TextView caloriesTV;

    //Task2: Declare variables for computing calories
    private Burger burger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Task 3: Initialise UI Objects and variables
        burger = new Burger();
        initialise();


        //Task 4: Register CHANGE LISTENERS
        registerChangeListener();

    }

    private void initialise() {
        //Task 5: get reference to each of the UI components
        pattyRG =(RadioGroup) findViewById(R.id.radioGroup1);
        prosciuttoCBX = (CheckBox) findViewById(R.id.checkbox1);
        cheeseRG = (RadioGroup) findViewById(R.id.radioGroup2);
        sauceSBR = (SeekBar) findViewById(R.id.seekBar1);
        caloriesTV = (TextView) findViewById(R.id.textView2);

        displayCalories();
    }

    private void registerChangeListener() {

        pattyRG.setOnCheckedChangeListener(foodListener);
        prosciuttoCBX.setOnClickListener(baconListener);
        cheeseRG.setOnCheckedChangeListener(foodListener);
        sauceSBR.setOnSeekBarChangeListener(sauceListener);


    }

    private OnCheckedChangeListener foodListener = new
            OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int radioId) {

                    switch (radioId) {
                        case R.id.radio0: //Beef Patty
                            burger.setPattyCalories(Burger.BEEF);
                            break;

                        case R.id.radio1: //Lamb Patty
                            burger.setPattyCalories(Burger.LAMB);
                            break;

                        case R.id.radio2: //Ostrich Patty
                            burger.setPattyCalories(Burger.OSTRICH);
                            break;

                        case R.id.radio3: //Beef ASIAGO
                            burger.setPattyCalories(Burger.ASIAGO);
                            break;

                        case R.id.radio4: //Beef CREME_FRAICHE
                            burger.setPattyCalories(Burger.CREME_FRAICHE);

                    }
                    displayCalories();

                }
            };

    private OnClickListener baconListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((CheckBox) view).isChecked())
                burger.setProsciuttoCalories(Burger.PROSCIUTTO);

            else
                burger.clearProsciuttoCalories();

            displayCalories();

        }
    };

    private OnSeekBarChangeListener sauceListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            burger.setProsciuttoCalories(seekBar.getProgress());
            displayCalories();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void displayCalories(){

        //construct an output string and display in the textview

        String calorieText = "Calories: " + burger.getTotalCalories();
        caloriesTV.setText(calorieText);
    }

//    @Override
//    public boolean onCreateOptionsMenu (Menu menu){
//
//        //inflate the menu;
//         getMenuInflater().inflate(R.menu.Main, menu);
//        return true;
//    }
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.string.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
