package com.hackerivanovich.nonbanappmarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthLoginOptions;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;

import de.hdodenhof.circleimageview.CircleImageView;

public class LogActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText email, password;
    private Button login;
    SharedPreferences sPref;
    private CircleImageView yandex_login;
    final YandexAuthSdk sdk = new YandexAuthSdk(this, new YandexAuthOptions.Builder(this)
            .enableLogging()
            .build());
    final YandexAuthLoginOptions.Builder loginOptionsBuilder =  new YandexAuthLoginOptions.Builder();
    final Intent y_intent = sdk.createLoginIntent(loginOptionsBuilder.build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        init();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    saveText(email.getText().toString(), "EMAIL");
                    saveText(password.getText().toString(), "PASSWORD");
                    mAuth.signInWithEmailAndPassword(loadText("EMAIL"), loadText("PASSWORD"))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LogActivity.this, AppListActivity.class));
                                    } else {
                                        Toast.makeText(LogActivity.this, "No Internet",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        yandex_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(y_intent, 101);
            }
        });
    }

    private void init() {
        email = findViewById(R.id.email_log);
        password = findViewById(R.id.pass_log);
        login = findViewById(R.id.log_in);
        yandex_login = findViewById(R.id.yandex_auth);
    }

    private void saveText(String data, final String namePref) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(namePref, data);
        editor.commit();
    }

    private String loadText(final String namePref) {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(namePref, "");
        return savedText;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101) {
            try {
                final YandexAuthToken yandexAuthToken = sdk.extractToken(resultCode, data);
                if (yandexAuthToken != null) {
                    startActivity(new Intent(this, AppListActivity.class));
                    saveText("OK", "AUTH_YANDEX");
                }
            } catch (YandexAuthException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}