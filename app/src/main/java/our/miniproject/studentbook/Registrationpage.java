package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrationpage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private Button regtolog, register;
    private EditText email_reg, password_reg, user_name, erp_id, role_edit;
    private ProgressDialog progressDialog;
    private CheckBox showpass;
    private ArrayAdapter<CharSequence> arrayAdapter;
    String college;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationpage);

        spinner = findViewById(R.id.spin_reg);
        role_edit = findViewById(R.id.role_edit);
        regtolog = findViewById(R.id.log_here);
        user_name = findViewById(R.id.user_reg);
        erp_id = findViewById(R.id.erp_reg);
        email_reg = findViewById(R.id.email_reg);
        password_reg = findViewById(R.id.password_reg);
        register = findViewById(R.id.reg_button);
        progressDialog = new ProgressDialog(Registrationpage.this);
        showpass = findViewById(R.id.showpass_reg);

        //databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        toolbar.setTitleTextColor(Color.WHITE);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    startActivity(new Intent(Registrationpage.this, HomeActivity.class));
                    finish();
                }
            }
        };

        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password_reg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password_reg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.college, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        college = "Priyadarshini Bhagwati College of Engineering";
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Validating");
                progressDialog.setMessage("Please wait...");
                checkConnection();

                final String username = user_name.getText().toString();
                final String erpid = erp_id.getText().toString();
                final String emailreg = email_reg.getText().toString();
                final String passreg = password_reg.getText().toString();
                final String role = role_edit.getText().toString();
                final String profileimage = "!";

                if (username.isEmpty()) {
                    user_name.setError("Enter your name");
                    user_name.requestFocus();
                } else if (erpid.isEmpty()) {
                    erp_id.setError("Enter ERP ID");
                    erp_id.requestFocus();
                } else if (emailreg.isEmpty()) {
                    email_reg.setError("Enter your email");
                    email_reg.requestFocus();
                } else if (passreg.isEmpty()) {
                    password_reg.setError("Enter your password");
                    password_reg.requestFocus();
                } else if (erpid.length() != 9) {
                    Toast.makeText(Registrationpage.this, "ERP ID not valid",
                            Toast.LENGTH_SHORT).show();
                } else if (passreg.length() < 6) {
                    Toast.makeText(Registrationpage.this, "Password length minimum 6 characters",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(emailreg, passreg)
                            .addOnCompleteListener(Registrationpage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        RegInformation regInformation = new RegInformation(
                                                role, username, erpid, emailreg, passreg, profileimage, college
                                        );

                                        FirebaseDatabase.getInstance().getReference(databaseReference.getKey())
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .setValue(regInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Registrationpage.this, "Hi " + username + "... Welcome to Studentbook!",
                                                        Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Registrationpage.this, HomeActivity.class));
                                                finish();
                                            }
                                        });

                                    } else {

                                        progressDialog.dismiss();
                                        Toast.makeText(Registrationpage.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        regtolog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                progressDialog.dismiss();
                startActivity(new Intent(Registrationpage.this, Loginpage.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {

            progressDialog.dismiss();
            Toast.makeText(Registrationpage.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
