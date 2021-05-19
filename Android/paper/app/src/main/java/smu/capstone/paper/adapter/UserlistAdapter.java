package smu.capstone.paper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import smu.capstone.paper.R;
import smu.capstone.paper.activity.ProfileActivity;
import smu.capstone.paper.responseData.User;
import smu.capstone.paper.server.RetrofitClient;


public class UserlistAdapter extends  RecyclerView.Adapter<UserlistAdapter.ViewHolder>{

    List<User> userList;
    Context context;
    int len = 15;

    public UserlistAdapter (Context context,  List<User>  obj) {
        this.context = context;
        userList = obj;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.userlist_item, parent, false);
        return new UserlistAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User item = (User) userList.get(position);
        holder.login_id.setText(item.getLogin_id());
        String intro = item.getIntro();
        if( intro.length() > len)
            intro = intro.substring(0,len) +" ...";
        holder.intro.setText(intro);
        String img_addr = RetrofitClient.getBaseUrl() + item.getImg_profile();
        Glide.with(context).load(img_addr).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class  ViewHolder extends  RecyclerView.ViewHolder{
        ImageView image;
        TextView login_id;
        TextView intro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.userlist_img);
            login_id = (TextView) itemView.findViewById(R.id.userlist_loginid);
            intro = (TextView) itemView.findViewById(R.id.userlist_intro);

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("userId", login_id.getText());
                    context.startActivity(intent);
                }
            });
        }
    }
}
