package com.hackerivanovich.nonbanappmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button reg;
    private TextView log;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    private void init() {
        reg = findViewById(R.id.reg);
        log = findViewById(R.id.log);
    }

    @Override
    protected void onStart() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null || getPreferences(MODE_PRIVATE).getString("AUTH_YANDEX", "") != null){
            startActivity(new Intent(MainActivity.this, AppListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }

        super.onStart();
    }
}