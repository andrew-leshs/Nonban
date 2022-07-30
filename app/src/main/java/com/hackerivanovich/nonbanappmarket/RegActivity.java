package com.hackerivanovich.nonbanappmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class RegActivity extends AppCompatActivity {

    private Button saveEmail, sendCode, send_name, send_gender;
    private TextView firstStep, codeView, name_view, gender_view;
    private EditText email, codeEmail, name, surname, location;
    final String SAVED_TEXT = "saved_text";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User");
    DatabaseReference appR = database.getReference("Apps");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences sPref;
    private static final String TAG = "DataUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        init();

        saveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty()){
                    saveText(email.getText().toString(), "EMAIL");
                    saveEmail.setVisibility(View.INVISIBLE);
                    sendCode.setVisibility(View.VISIBLE);
                    firstStep.setVisibility(View.INVISIBLE);
                    codeView.setVisibility(View.VISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    codeEmail.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(RegActivity.this, "Enter right email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!codeEmail.getText().toString().isEmpty()) {
                    saveText(codeEmail.getText().toString(), "PASSWORD");
                    send_name.setVisibility(View.VISIBLE);
                    sendCode.setVisibility(View.INVISIBLE);
                    name_view.setVisibility(View.VISIBLE);
                    codeView.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.VISIBLE);
                    surname.setVisibility(View.VISIBLE);
                    codeEmail.setVisibility(View.INVISIBLE);
                }
            }
        });
        send_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !surname.getText().toString().isEmpty()) {
                    send_name.setVisibility(View.INVISIBLE);
                    name_view.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    surname.setVisibility(View.INVISIBLE);
                    location.setVisibility(View.VISIBLE);
                    send_gender.setVisibility(View.VISIBLE);
                    gender_view.setVisibility(View.VISIBLE);
                    saveText(name.getText().toString(), "NAME");
                    saveText(surname.getText().toString(), "SURNAME");
                } else {

                }
            }
        });
        send_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!location.getText().toString().isEmpty()) {
                    saveText(location.getText().toString(), "LOCATION");
                    reg();
                }
            }
        });
    }

    private void reg() {
        mAuth.createUserWithEmailAndPassword(loadText("EMAIL"), loadText("PASSWORD"))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String id = myRef.getKey();
                            String name = loadText("NAME");
                            String surname = loadText("SURNAME");
                            String email = loadText("EMAIL");
                            String password = loadText("PASSWORD");
                            String location = loadText("LOCATION");

                            User user = new User(id, email, password, name, surname, location);
                            myRef.push().setValue(user);
                            startActivity(new Intent(RegActivity.this, AppListActivity.class));
                        } else {
                            Toast.makeText(RegActivity.this, "No Internet",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        saveEmail = findViewById(R.id.save_email);
        sendCode = findViewById(R.id.send_code);
        send_name = findViewById(R.id.send_name);
        firstStep = findViewById(R.id.first_view);
        codeView = findViewById(R.id.code_view);
        name_view = findViewById(R.id.name_view);
        email = findViewById(R.id.email);
        codeEmail = findViewById(R.id.code_email);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        send_gender = findViewById(R.id.send_gender);
        gender_view = findViewById(R.id.gender_view);
        location = findViewById(R.id.location);
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
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //
        }
    }
}
