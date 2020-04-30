package our.miniproject.studentbook;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegInformation {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public String username, erpid, email, pass, role, profileimage, college;

    public RegInformation() {

    }

    public RegInformation(String role, String username, String erpid, String email, String pass, String profileimage, String college) {
        this.role = role;
        this.username = username;
        this.erpid = erpid;
        this.email = email;
        this.pass = pass;
        this.profileimage = profileimage;
        this.college = college;
    }
}

