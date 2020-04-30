package our.miniproject.studentbook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeComAdapter extends RecyclerView.Adapter<HomeComAdapter.HomeComViewHolder> {
    Context context;
    ArrayList<HomepostClickActivity.HomeCom> homeComs;
    private List<HomepostClickActivity.HomeCom> homeComList;
    private AdapterView.OnItemClickListener listener;

    HomeComAdapter() {}

    public HomeComAdapter(List<HomepostClickActivity.HomeCom> hList, AdapterView.OnItemClickListener listener) {
        this.homeComList = hList;
        this.listener = listener;
    }

    public HomeComAdapter(Context c, ArrayList<HomepostClickActivity.HomeCom> h) {
        context = c;
        homeComs = h;
    }

    @NonNull
    @Override
    public HomeComAdapter.HomeComViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeComAdapter.HomeComViewHolder(LayoutInflater.from(context).inflate(R.layout.home_comments,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeComAdapter.HomeComViewHolder holder, int position) {

        holder.bind(homeComs.get(position), listener);
        holder.username.setText(homeComs.get(position).getUsername());
        holder.homeComments.setText(homeComs.get(position).getComment());
        holder.uid = homeComs.get(position).getUid();
        Picasso.with(context).load(homeComs.get(position).getProfileimage()).placeholder(R.drawable.navhuman).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return homeComs.size();
    }

    public interface OnItemClickListener {
        void onItemClick(HomepostClickActivity.HomeCom homeCom);
    }

    public class HomeComViewHolder extends RecyclerView.ViewHolder {

        TextView username, homeComments;
        CircleImageView circleImageView;
        String uid;

        public HomeComViewHolder(View itemView) {

            super(itemView);

            circleImageView = itemView.findViewById(R.id.homeComProImage);
            username = itemView.findViewById(R.id.homeComUsername);
            homeComments = itemView.findViewById(R.id.shareHomeComments);
        }

        public void bind(HomepostClickActivity.HomeCom item,AdapterView.OnItemClickListener listener) {
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UserInfoActivity.class).putExtra("UserInfo", uid));
                }
            });
        }
    }
}
