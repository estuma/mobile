package kr.ac.koreatech.mpteam9.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kr.ac.koreatech.mpteam9.ChatRoomActivity;
import kr.ac.koreatech.mpteam9.R;
import kr.ac.koreatech.mpteam9.models.Profile_list;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder>{

    Context context;
    List<Profile_list> profileLists;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatList(Context context, List<Profile_list> profileLists) {
        this.context = context;
        this.profileLists = profileLists;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_list, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String uid = profileLists.get(position).getUid();
        String userImage = profileLists.get(position).getImage();
        String userName = profileLists.get(position).getName();
        String lastMessage = lastMessageMap.get(uid);

        holder.nameTv.setText(userName);
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessageTv.setVisibility(View.GONE);
        }
        else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(userImage).into(holder.profileIv);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.kakako).into(holder.profileIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("otherUid", uid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String uid, String lastMessage) {
        lastMessageMap.put(uid, lastMessage);
    }

    @Override
    public int getItemCount() {
        return profileLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView profileIv;
        TextView nameTv, lastMessageTv, lastTimeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.chatlist_image);
            nameTv = itemView.findViewById(R.id.chatlist_name);
            lastMessageTv = itemView.findViewById(R.id.chatlist_last_message);
        }
    }
}
