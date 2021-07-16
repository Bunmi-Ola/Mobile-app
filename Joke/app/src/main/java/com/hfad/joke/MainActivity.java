package com.hfad.joke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //start DelayedMessageService whenever the user clicks on the button
    public void onClick(View view) {
        Intent intent = new Intent(this, DelayedMessageService.class);
        //add text to intent
        intent.putExtra(DelayedMessageService.EXTRA_MESSAGE,
                getResources().getString(R.string.response));
        //start service
        startService(intent);
        //finish();
    }
}
