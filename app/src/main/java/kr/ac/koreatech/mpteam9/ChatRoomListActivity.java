package kr.ac.koreatech.mpteam9;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.ac.koreatech.mpteam9.adapters.AdapterChatList;
import kr.ac.koreatech.mpteam9.models.ModelChat;
import kr.ac.koreatech.mpteam9.models.ModelChatRoom;
import kr.ac.koreatech.mpteam9.models.Profile_list;

public class ChatRoomListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String currentUid;
    List<ModelChatRoom> chatRoomList;
    List<Profile_list> profileLists;
    DatabaseReference reference;
    AdapterChatList adapterChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        getSupportActionBar().setTitle("채팅방 목록");

        recyclerView = findViewById(R.id.chatlist_recyclerview);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatRoomList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(currentUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRoomList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChatRoom chatRoom = ds.getValue(ModelChatRoom.class);
                    chatRoomList.add(chatRoom);
                }
                loadChatRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadChatRooms() {
        profileLists = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileLists.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Profile_list profile = ds.getValue(Profile_list.class);
                    for (ModelChatRoom chatRoom: chatRoomList) {
                        if (profile.getUid() != null && profile.getUid().equals(chatRoom.getId())) {
                            profileLists.add(profile);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(ChatRoomListActivity.this, profileLists);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0; i<profileLists.size(); i++) {
                        lastMessage(profileLists.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void lastMessage(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUid) &&
                            chat.getSender().equals(uid) ||
                            chat.getReceiver().equals(uid) &&
                                    chat.getSender().equals(currentUid)) {
                        theLastMessage = chat.getMessage();
                    }
                }
                adapterChatList.setLastMessageMap(uid, theLastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}