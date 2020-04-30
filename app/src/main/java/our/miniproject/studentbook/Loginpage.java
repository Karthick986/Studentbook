package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loginpage extends AppCompatActivity {

    private Button logtoreg, login, forgot_btn;
    private EditText email_log, pass_log;
    private ProgressDialog progressDialog;
    private CheckBox showpass;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        login = findViewById(R.id.log_button);
        email_log = findViewById(R.id.email_log);
        pass_log = findViewById(R.id.password_log);
        forgot_btn = findViewById(R.id.recover);
        showpass = findViewById(R.id.showpass_log);

        progressDialog = new ProgressDialog(Loginpage.this);

        firebaseAuth = FirebaseAuth.getInstance();

        showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pass_log.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    pass_log.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Signing in");
                progressDialog.setMessage("Please wait...");

                checkConnection();

                final String emaillog = email_log.getText().toString();
                String passlog = pass_log.getText().toString();

                if (emaillog.isEmpty()) {
                    email_log.setError("Enter your email");
                    email_log.requestFocus();
                }

                else if (passlog.isEmpty()) {
                    pass_log.setError("Enter password");
                    pass_log.requestFocus();
                }

                else {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(emaillog, passlog)
                            .addOnCompleteListener(Loginpage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        progressDialog.dismiss();
                                        Toast.makeText(Loginpage.this, "Welcome back!", Toast.LENGTH_LONG).show();
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        startActivity(new Intent(Loginpage.this, HomeActivity.class));
                                        finish();

                                    } else {

                                        progressDialog.dismiss();
                                        Toast.makeText(Loginpage.this, "Login failed",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Login");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);

        logtoreg = findViewById(R.id.reg_here);

        logtoreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                progressDialog.dismiss();
                startActivity(new Intent(Loginpage.this, Registrationpage.class));
            }
        });

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Recovering");
                progressDialog.setMessage("Please wait...");
                forgotshow();
            }
        });
    }

    private void forgotshow() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailText = new EditText(this);
        emailText.setHint("Enter email");
        emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailText.setMinEms(15);

        linearLayout.addView(emailText);
        linearLayout.setPadding(10, 10, 10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String recoveremail = emailText.getText().toString();

                if (recoveremail.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Loginpage.this, "Please enter ur email",
                            Toast.LENGTH_SHORT).show();
                } else {
                    beginRecovery(recoveremail);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String foremail) {

        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(foremail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(Loginpage.this, "Check your email",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(Loginpage.this, "Failed...! try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Loginpage.this, "" +e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {

            progressDialog.dismiss();
            Toast.makeText(Loginpage.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
