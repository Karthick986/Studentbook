package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SlideActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    ViewPager viewPager;
    LinearLayout linearLayout;
    TextView[] dots;
    Button next, prev, finish;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }
        };

        viewPager = findViewById(R.id.viewPager);
        final SliderAdapter sliderAdapter = new SliderAdapter(this);
        linearLayout = findViewById(R.id.lin);

        next = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        finish = findViewById(R.id.finish);

        viewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(pageChangeListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(currentPage + 1);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage -1);
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Firstpage.this, "", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Registrationpage.class));
                finish();
            }
        });
    }
    public void addDotsIndicator(int position) {

        dots = new TextView[4];
        linearLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorAccent));

            linearLayout.addView(dots[i]);

        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);

            currentPage = i;

            if (i==0) {
                next.setEnabled(true);
                prev.setEnabled(false);
                finish.setEnabled(false);
                next.setVisibility(View.VISIBLE);
                prev.setVisibility(View.INVISIBLE);
                finish.setVisibility(View.INVISIBLE);
                next.setText("Next");
                prev.setText("");
                finish.setText("");
            }

            else if (i==dots.length-1) {
                next.setEnabled(false);
                prev.setEnabled(true);
                finish.setEnabled(true);
                next.setVisibility(View.INVISIBLE);
                prev.setVisibility(View.VISIBLE);
                finish.setVisibility(View.VISIBLE);
                finish.setText("Register");
                prev.setText("Previous");
            }

            else  {
                next.setEnabled(true);
                prev.setEnabled(true);
                finish.setEnabled(false);
                next.setVisibility(View.VISIBLE);
                prev.setVisibility(View.VISIBLE);
                finish.setVisibility(View.INVISIBLE);
                next.setText("Next");
                prev.setText("Previous");
                finish.setText("");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
