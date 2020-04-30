package our.miniproject.studentbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class HomeImageView extends AppCompatActivity {

    PhotoView photoView;
    String postImage;
    ImageView imageView;
    ProgressBar progressBar;
    Bitmap bitmap;
    RelativeLayout relativeLayout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_imageview);

        Intent intent = getIntent();
        postImage = intent.getStringExtra("PostImage");

        relativeLayout = findViewById(R.id.viewImage);
        imageView = findViewById(R.id.homeImageDownload);
        progressBar = findViewById(R.id.hmeImageprogress);
        photoView = findViewById(R.id.homePhoto);
        progressDialog = new ProgressDialog(this);

        Picasso.with(HomeImageView.this).load(postImage).placeholder(R.drawable.loadingimage)
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Downloding");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
                String time = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault())
                        .format(System.currentTimeMillis());
                File path = Environment.getExternalStorageDirectory();
                File dir = new File(path + "/Student");
                dir.mkdir();
                String imageName = time + ".jpeg";
                File file = new File(dir, imageName);
                OutputStream outputStream;

                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    progressDialog.dismiss();
                    Toast.makeText(HomeImageView.this, "Image downloaded!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(HomeImageView.this, "Can't download! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
