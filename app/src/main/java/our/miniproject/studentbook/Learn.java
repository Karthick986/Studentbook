package our.miniproject.studentbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class Learn extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private TextView username, usererp;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        firebaseAuth = FirebaseAuth.getInstance();
        circleImageView = findViewById(R.id.userLearnlogo);
        String currentId = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference("Profile Images").child(currentId + ".jpg");

        navdata();
        Toolbar toolbar = findViewById(R.id.learn_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_learn);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_notices, R.id.nav_shoppe,
                R.id.nav_learn, R.id.nav_schedule, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {

            progressDialog.dismiss();
            Toast.makeText(Learn.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = new NavController(null);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_about:
                startActivity(new Intent(Learn.this, About.class));
                break;
            case R.id.action_contact:
                startActivity(new Intent(Learn.this, Contact.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.nav_home:
                startActivity(new Intent(Learn.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                break;

            case R.id.nav_notices:
                startActivity(new Intent(Learn.this, CollegeNotices.class));
                overridePendingTransition(0, 0);
                finish();
                break;

            case R.id.nav_shoppe:
                startActivity(new Intent(Learn.this, CollegeShoppe.class));
                overridePendingTransition(0, 0);
                finish();
                break;

            case R.id.nav_learn:
                break;
            case R.id.nav_schedule:
                Toast.makeText(Learn.this, "Scheduler yet to be done", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(Learn.this, ProfileActivity.class));
                finish();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(Learn.this, Settings.class));
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                Toast.makeText(Learn.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Learn.this, Loginpage.class));
                finish();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void navdata() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseAuth.getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    try {
                        final File file = File.createTempFile("image", "jpg");
                        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Picasso.with(Learn.this).load(file)
                                        .into(circleImageView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                               // Toast.makeText(Learn.this, "Image failed to load", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                username = findViewById(R.id.user_name);
                usererp = findViewById(R.id.user_erp);

                String usernamestr = dataSnapshot.child("username").getValue().toString();
                String usererpidstr = dataSnapshot.child("erpid").getValue().toString();

                username.setText(usernamestr);
                usererp.setText(usererpidstr);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
