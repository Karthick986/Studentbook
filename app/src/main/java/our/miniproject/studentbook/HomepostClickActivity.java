package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomepostClickActivity extends AppCompatActivity {

    TextView userName, userDescp, comments;
    EditText shareCom;
    String comuid;
    DatabaseReference databaseReference, userReference, comReference, recy;
    CircleImageView circleImageView, userImage;
    ZoomInImageView photoView;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    ArrayList<HomeCom> homeComs;
    HomeComAdapter homeComAdapter;
    RecyclerView recyclerView;
    String currentId, username, userProfileimage;
    String date, time, name ,disCom;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepost_click);

        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getCurrentUser().getUid();
        Intent intent = getIntent();
        comuid = intent.getStringExtra("homePostkey");

        databaseReference = FirebaseDatabase.getInstance().getReference("Home Activity Info").child(comuid);
        recy = FirebaseDatabase.getInstance().getReference().child("Home Comments").child(comuid);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentId);
        comReference = FirebaseDatabase.getInstance().getReference("Home Comments").child(comuid);

        recyclerView = findViewById(R.id.homecomRecycler);
        floatingActionButton = findViewById(R.id.homecom_fab);
        shareCom = findViewById(R.id.shareComments);
        userName = findViewById(R.id.homeClickUsername);
        userDescp = findViewById(R.id.homeClickDescp);
        userImage = findViewById(R.id.newscomments_pro);
        comments = findViewById(R.id.commentsfromHome);
        circleImageView = findViewById(R.id.homeClickProImage);
        photoView = findViewById(R.id.homeClickPostimage);
        progressBar = findViewById(R.id.progresshomeClick);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime();
                storeComInfo();
            }
        });

        homeComs = new ArrayList<HomeCom>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userproImage = dataSnapshot.child("profileimage").getValue().toString();
                final String userpostImage = dataSnapshot.child("postimage").getValue().toString();
                String userDescr = dataSnapshot.child("description").getValue().toString();
                String userNamestr = dataSnapshot.child("username").getValue().toString();
                String userUid = dataSnapshot.child("uid").getValue().toString();

                userDescp.setText(userDescr);
                userName.setText(userNamestr);
                Picasso.with(getApplicationContext()).load(userproImage).placeholder(R.drawable.navhuman).into(circleImageView);

                (Picasso.with(HomepostClickActivity.this).load(userpostImage))
                        .into(photoView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), HomeImageView.class).putExtra("PostImage", userpostImage));
                        overridePendingTransition(100, 100);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomepostClickActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userproImage = dataSnapshot.child("profileimage").getValue().toString();
                Picasso.with(getApplicationContext()).load(userproImage).placeholder(R.drawable.navhuman).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });

      recyler();
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

    private void storeComInfo() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    disCom = shareCom.getText().toString();
                    username = dataSnapshot.child("username").getValue().toString();
                    userProfileimage = dataSnapshot.child("profileimage").getValue().toString();
                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("profileimage", userProfileimage);
                    postMap.put("username", username);
                    postMap.put("uid", currentId);
                    postMap.put("comment", disCom);

                    comReference.child(name + currentId).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HomepostClickActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomepostClickActivity.this, HomepostClickActivity.class).putExtra("homePostkey", comuid));
                                        overridePendingTransition(100, 100);
                                    } else {
                                        Toast.makeText(HomepostClickActivity.this, "Can't comment! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomepostClickActivity.this, "Error occurred! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class HomeCom {
        public HomeCom() {}
        String comment, username, profileimage ,uid;

        public HomeCom(String comment, String username, String profileimage, String uid) {
          this.comment = comment;
            this.username = username;
            this.profileimage = profileimage;
            this.uid = uid;
        }
        public String getComment() {
            return comment;
        }
        public String getUsername() {
            return username;
        }
        public String getProfileimage() {
            return profileimage;
        }
        public String getUid() { return uid; }
    }
     public void recyler() {
         recy.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 comments.setVisibility(View.GONE);
                 for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                     HomeCom hom = dataSnapshot1.getValue(HomeCom.class);
                     homeComs.add(hom);
                 }
                 homeComAdapter = new HomeComAdapter(HomepostClickActivity.this, homeComs);
                 recyclerView.setAdapter(homeComAdapter);
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
             }
         });
     }
}
