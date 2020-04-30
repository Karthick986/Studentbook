package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    TextView userName, userRole;
    CircleImageView circleImageView;
    DatabaseReference databaseReference;
    String userUid;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        userUid = intent.getStringExtra("UserInfo");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = findViewById(R.id.userInfoname);
                userRole = findViewById(R.id.userInforole);
                circleImageView = findViewById(R.id.userInfoImage);

                String usernamestr = dataSnapshot.child("username").getValue().toString();
                String userrolestr = dataSnapshot.child("role").getValue().toString();
                String userImagestr = dataSnapshot.child("profileimage").getValue().toString();

                userName.setText(usernamestr);
                userRole.setText(userrolestr);

                Picasso.with(getApplicationContext()).load(userImagestr).placeholder(R.drawable.navhuman).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
