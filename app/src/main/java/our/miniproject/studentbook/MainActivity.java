package our.miniproject.studentbook;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends RecyclerView.Adapter<MainActivity.PostsViewHolder> {

    Context context;
    ArrayList<HomePosts> homeActivities;
    private List<HomePosts> homePostsList;
    private AdapterView.OnItemClickListener listener;

    public MainActivity() {
    }

    public MainActivity(List<HomePosts> hList, AdapterView.OnItemClickListener listener) {
        this.homePostsList = hList;
        this.listener = listener;
    }

    public MainActivity(Context c, ArrayList<HomePosts> h) {
        context = c;
        homeActivities = h;
    }

    @NonNull
    @Override
    public MainActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.activity__homepost,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MainActivity.PostsViewHolder holder, int position) {

        holder.bind(homeActivities.get(position), listener);
        holder.homepostkey = homeActivities.get(position).getUidpost();
        holder.useruid = homeActivities.get(position).getUid();
        holder.postImage = homeActivities.get(position).getPostimage();
        holder.postTime.setText(homeActivities.get(position).getTime());
        holder.username.setText(homeActivities.get(position).getUsername());
        holder.role.setText(homeActivities.get(position).getRole());
        holder.date.setText(homeActivities.get(position).getDate());
        holder.description.setText(homeActivities.get(position).getDiscription());

        Picasso.with(context).load(homeActivities.get(position).getProfileimage()).placeholder(R.drawable.navhuman).into(holder.circleImageView);

        holder.progressImageBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(homeActivities.get(position).getPostimage())
                .into(holder.inImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.progressImageBar != null) {
                            holder.progressImageBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {
                        holder.progressImageBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return homeActivities.size();
    }

    public interface OnItemClickListener {
        void onItemClick(HomePosts homePosts);
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {

        TextView date, description, username, role, homeComments, postTime, homeShare;
        CircleImageView circleImageView;
        ProgressBar progressImageBar;
        SwipeRefreshLayout refreshLayout;
        RelativeLayout relativeLayout;
        ZoomInImageView inImageView;
        int currentTime, pTime;
        ProgressDialog progressDialog;
        String homepostkey, useruid, postImage;

        public PostsViewHolder(View itemView) {

            super(itemView);

            circleImageView = itemView.findViewById(R.id.homeProImage);
            date = itemView.findViewById(R.id.homeDate);
            description = itemView.findViewById(R.id.homeDescp);
            username = itemView.findViewById(R.id.homeUsername);
            role = itemView.findViewById(R.id.homeRole);
            postTime = itemView.findViewById(R.id.hometimepost);
            progressImageBar = itemView.findViewById(R.id.progress);
            refreshLayout = itemView.findViewById(R.id.refreshhome);
            relativeLayout = itemView.findViewById(R.id.userHomepost);
            inImageView = itemView.findViewById(R.id.homePostimage);
            homeComments = itemView.findViewById(R.id.homeComment);
            homeShare = itemView.findViewById(R.id.homeShare);
        }

        public void bind(final HomePosts item, final AdapterView.OnItemClickListener listener) {

            progressDialog = new ProgressDialog(context);

            homeComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent homepostView = new Intent(context, HomepostClickActivity.class);
                homepostView.putExtra("homePostkey", homepostkey);
                context.startActivity(homepostView);
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent userinfo = new Intent(context, UserInfoActivity.class);
                    userinfo.putExtra("UserInfo", useruid);
                    context.startActivity(userinfo);
                }
            });
            inImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewImage = new Intent(context, HomeImageView.class);
                    viewImage.putExtra("PostImage", postImage);
                    context.startActivity(viewImage);
                }
            });

            homeShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) inImageView.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Share via", null);
                        Uri uri = Uri.parse(bitmapPath);
                        progressDialog.dismiss();
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/jpeg");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Image not exist! Can't Share", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
