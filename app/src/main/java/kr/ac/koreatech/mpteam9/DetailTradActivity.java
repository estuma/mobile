package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import kr.ac.koreatech.mpteam9.adapters.AdapterCommentsTrade;
import kr.ac.koreatech.mpteam9.models.ModelComment;

public class DetailTradActivity extends AppCompatActivity {

    String pImage, hisUid, hisDp, hisName;          // 글 정보(사진,글 작성자 id/프로필/이름)
    String pId, myUid, myDp, myEmail, myName;       // 글 id, 내 정보(내 id/프로필/이메일/이름)
    String post_title;  // 글제목, 인텐트용

    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv;
    TextView pProTextTv, pDormTv, pKindTv, pStateTv;
    ImageButton moreBtn;                // 더보기 버튼(수정, 삭제)
    RecyclerView recyclerView;
    // 글 작성자 프로필, 글에 첨부된 사진     // 글 작성자 이름, 글 올린 시간, 글 제목, 글 내용

    ArrayList<ModelComment> commentList;
    AdapterCommentsTrade adapterComments;

    EditText commentEt;
    Button sendBtn;                     // 댓글 내용, 댓글 등록 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trading);

        getSupportActionBar().setTitle("거래 게시판");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        pId = intent.getStringExtra("pId");

        uPictureIv = findViewById(R.id.userImgT);
        uNameTv = findViewById(R.id.userIdT);
        pTimeTv = findViewById(R.id.userDateT);
        moreBtn = findViewById(R.id.moreBtnT);
        pDormTv = findViewById(R.id.subTxt1T);
        pKindTv = findViewById(R.id.subTxt2T);
        pStateTv = findViewById(R.id.subTxt3T);
        pTitleTv = findViewById(R.id.titleT);
        pImageIv = findViewById(R.id.pImageT);      // 글에 첨부된 사진
        pDescriptionTv = findViewById(R.id.contentT);

        pProTextTv = findViewById(R.id.proTxtT);

        commentEt = findViewById(R.id.commentEtT);
        sendBtn = findViewById(R.id.sendBtnT);

        loadPostInfo();
        checkUserStatus();
        loadUserInfo();

        recyclerView = findViewById(R.id.recyclerViewT);
        loadComments();

        sendBtn.setOnClickListener((v) -> { postComment(); });
        moreBtn.setOnClickListener( (v) -> { showMoreOptions(); });
    }

    public void onClick_enterChatRoom(View v){
        if(v.getId() == R.id.chatFloating){
            String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //Toast.makeText(getApplicationContext(), hisUid + " " + currUid, Toast.LENGTH_SHORT).show();
            if (hisUid.equals(currUid)) {
                Toast.makeText(getApplicationContext(),"자기 자신과는 채팅을 못해요!", Toast.LENGTH_SHORT).show();
            }
            else {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference chatRoomRef1 = FirebaseDatabase.getInstance()
                        .getReference("ChatRooms").child(hisUid).child(myUid);
                chatRoomRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            chatRoomRef1.child("id").setValue(myUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference chatRoomRef2 = FirebaseDatabase.getInstance()
                        .getReference("ChatRooms").child(myUid).child(hisUid);
                chatRoomRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            chatRoomRef2.child("id").setValue(hisUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent in = new Intent(DetailTradActivity.this, ChatRoomActivity.class);
                in.putExtra("title", post_title);
                //in.putExtra("isPrevDetailTrade", true);
                in.putExtra("otherUid", hisUid);
                startActivity(in);
            }
        }
    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        commentList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade").child(pId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentList.add(modelComment);
                }
                adapterComments.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        adapterComments = new AdapterCommentsTrade(this, commentList, myUid, pId);
        recyclerView.setAdapter(adapterComments);
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), moreBtn, Gravity.END);

        if(hisUid.equals(myUid)){  // 글 작성자 id == 내 id
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "수정");
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == 0){
                    beginDelete();
                } else if(id == 1){
                    Intent intent = new Intent(getApplicationContext(), WriteTradingActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",pId);
                    startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete() {
        if(pImage.equals("noImage")){
            deleteWithoutImage();
        } else{
            deleteWithImage();
        }
        Intent intent = new Intent(getApplicationContext(), TradingBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 플래그 지정-그동안 쌓여있던 액티비티를 모두 종료
        // 위의 두 줄로 인해, 메인과 거래글 목록만 스택에 쌓임
        startActivity(intent);          // 거래글 목록 액티비티 시작
    }

    private void deleteWithImage(){
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("PostsTrade").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                for(DataSnapshot ds: datasnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteWithoutImage(){
        Query fquery = FirebaseDatabase.getInstance().getReference("PostsTrade").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds: datasnapshot.getChildren()){
                    ds.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postComment() {
        String comment = commentEt.getText().toString().trim();
        if(TextUtils.isEmpty(comment)){
            Toast.makeText(this,"내용을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade").child(pId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);   // 댓글 id, 내용, 시간, 댓글 작성자 uid/이메일/프로필/이름

        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        commentEt.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailTradActivity.this,"실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo() {
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = "" + ds.child("name").getValue();
                    myDp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
        Query query = ref.orderByChild("pId").equalTo(pId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String pTitle = "" + ds.child("pTitle").getValue();
                    post_title = pTitle;
                    String pDescr = "" + ds.child("pContent").getValue();
                    String pTimeStamp = "" + ds.child("pTime").getValue();
                    pImage = "" + ds.child("pImage").getValue();
                    // 글 제목/내용/시간/사진

                    String pDorm = "[" + ds.child("pDorm").getValue() + "]";
                    String pKind = "[" + ds.child("pKind").getValue() + "]";
                    String pState = "[" + ds.child("pState").getValue() + "]";
                    String pProText = ds.child("pReserveNum").getValue() + "/" + ds.child("pNeedNum").getValue();

                    hisUid = "" + ds.child("uid").getValue();
                    hisDp = "" + ds.child("uDp").getValue();
                    String uEmail = "" + ds.child("uEmail").getValue();
                    hisName = "" + ds.child("uName").getValue();
                    // 글 작성자 uid/프로필/이메일/이름

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();

                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pTimeTv.setText(pTime);

                    pDormTv.setText(pDorm);
                    pKindTv.setText(pKind);
                    pStateTv.setText(pState);
                    pProTextTv.setText(pProText);

                    uNameTv.setText(hisName);

                    // 글에 첨부된 사진 보이게 하기
                    if(pImage.equals("noImage")){
                        pImageIv.setVisibility(View.GONE);
                    } else{
                        pImageIv.setVisibility(View.VISIBLE);
                        try{
                            Picasso.get().load(pImage).into(pImageIv);
                        } catch (Exception e) {}
                    }

                    // 프로그레스바
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setIndeterminate(false);

                    String reserve = (String) ds.child("pReserveNum").getValue();
                    String need = (String) ds.child("pNeedNum").getValue();
                    int setprogress = (int) (Double.parseDouble(reserve) / Double.parseDouble(need) * 100);

                    progressBar.setProgress(setprogress);

                    // 글 작성자의 프로필 설정
                    try{
                        Picasso.get().load(hisDp).placeholder(R.drawable.kakako).into(uPictureIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.kakako).into(uPictureIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            myEmail = user.getEmail();
            myUid = user.getUid();
        } else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}