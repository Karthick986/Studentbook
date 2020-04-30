package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostNewsActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Button btnpost, btnchoose, btnclear;
    ProgressDialog progressDialog;
    final static int GalleryPick = 1;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, filepath;
    DatabaseReference postReference, userReference;
    String time, date, name, currentTime;
    int postTime, updateTime;
    CircleImageView circleImageView;
    String downloadUrl = "!", description = " ";
    String currentId;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.postNews_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.post_image);
        circleImageView = findViewById(R.id.postnews_pro);
        btnclear =findViewById(R.id.clearDescp_postnews);
        editText = findViewById(R.id.edit_post);
        btnpost = findViewById(R.id.postnews);
        btnchoose = findViewById(R.id.chooseinnews);
        currentId = firebaseAuth.getCurrentUser().getUid();

        postReference = FirebaseDatabase.getInstance().getReference("Home Activity Info");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Home Activity Images");

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent setImage = new Intent();
                setImage.setAction(Intent.ACTION_GET_CONTENT);
                setImage.setType("image/*");
                startActivityForResult(setImage, GalleryPick);
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = editText.getText().toString();

                if (imageUri == null && TextUtils.isEmpty(description)) {
                    Toast.makeText(PostNewsActivity.this, "Post image or Write any post", Toast.LENGTH_SHORT).show();
                }
                else if (imageUri == null) {
                    dateTime();
                    storePostInfo();
                }
                else {
                    progressDialog.setTitle("Updating post");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    dateTime();
                    storageImage();
                }
            }
        });

        userReference.child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userImagestr = dataSnapshot.child("profileimage").getValue().toString();
                Picasso.with(getApplicationContext()).load(userImagestr).placeholder(R.drawable.navhuman).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void dateTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(" dd-mm-yyyy ");
        date = dateFormat.getDateInstance().format(calendar.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(" hh:mm:ss:ms ");
        time = simpleDateFormatTime.format(calendarTime.getTime());

        name = date + time;
    }
    private void storageImage() {

        filepath = storageReference.child(currentId +name+ ".jpg");

                    filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    storePostInfo();
                                }
                            });
                        }
                    });
    }

    private void storePostInfo() {
        userReference.child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.show();

                    String username = dataSnapshot.child("username").getValue().toString();
                    String userProfileimage = dataSnapshot.child("profileimage").getValue().toString();
                    String userRole = dataSnapshot.child("role").getValue().toString();

                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("date", date);
                    postMap.put("time", time.substring(0, 6));
                    postMap.put("description", description);
                    postMap.put("postimage", downloadUrl);
                    postMap.put("profileimage", userProfileimage);
                    postMap.put("username", username);
                    postMap.put("role", userRole);
                    postMap.put("uid", currentId);
                    postMap.put("uidpost",  name + currentId);

                    postReference.child(name + currentId).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(PostNewsActivity.this, HomeActivity.class));
                                        Toast.makeText(PostNewsActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PostNewsActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostNewsActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        progressDialog.dismiss();
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            progressDialog.dismiss();
            imageUri = data.getData();
            Picasso.with(getApplicationContext()).load(imageUri).resize(imageView.getMeasuredWidth(), 500).placeholder(R.drawable.navhuman).into(imageView);
        }
    }
}
