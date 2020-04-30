package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayAdapter<CharSequence> arrayAdapter;
    final static int GalleryPick = 1;
    EditText setName, setEmail, setPass, setErp, setRole;
    String editName, editEmail, editPass, editErp, editRole;
    CircleImageView circleImageView;
    String currentId;
    Spinner spinner;
    FloatingActionButton editInfo, editImage;
    String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getCurrentUser().getUid();
        editInfo = findViewById(R.id.setFloat);
        spinner = findViewById(R.id.spin_pro);
        editImage = findViewById(R.id.uploadImage);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images").child(currentId +".jpg");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navpro);
        circleImageView = findViewById(R.id.set_image);
        setName = findViewById(R.id.setname);
        setEmail = findViewById(R.id.setemail);
        setPass = findViewById(R.id.setpass);
        setErp  = findViewById(R.id.seterp);
        setRole = findViewById(R.id.setrol);

        bottomNavigationView.setSelectedItemId(R.id.bot_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.bot_explore:
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bot_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bot_profile:
                        return true;
                }
                return false;
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
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editName = setName.getText().toString();
                editEmail = setEmail.getText().toString();
                editErp = setErp.getText().toString();
                editPass = setPass.getText().toString();
                editRole = setRole.getText().toString();

                updateProfile();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent setImage = new Intent();
                setImage.setAction(Intent.ACTION_GET_CONTENT);
                setImage.setType("image/*");
                startActivityForResult(setImage, GalleryPick);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String setNamestr = dataSnapshot.child("username").getValue().toString();
                String setEmailstr = dataSnapshot.child("email").getValue().toString();
                String setPassstr = dataSnapshot.child("pass").getValue().toString();
                String setErpidstr = dataSnapshot.child("erpid").getValue().toString();
                String setRolestr = dataSnapshot.child("role").getValue().toString();
                String setImagestr = dataSnapshot.child("profileimage").getValue().toString();

                setName.setText(setNamestr);
                setEmail.setText(setEmailstr);
                setPass.setText(setPassstr);
                setErp.setText(setErpidstr);
                setRole.setText(setRolestr);

                Picasso.with(getApplicationContext()).load(setImagestr).placeholder(R.drawable.navhuman).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavBehavior());
    }

    private void updateProfile() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.setTitle("Updating your profile");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("username", editName);
                    postMap.put("role", editRole);
                    postMap.put("pass", editPass);
                    postMap.put("erpid", editErp);
                    postMap.put("email", editEmail);

                    databaseReference.updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                        overridePendingTransition(0, 0);
                                        finish();
                                        Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();

                final StorageReference filePath = storageReference;

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("profileimage").setValue(String.valueOf(uri))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ProfileActivity.this, "Error Occurred! Try again "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }
    }
}
