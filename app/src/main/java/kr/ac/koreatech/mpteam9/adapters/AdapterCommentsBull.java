package kr.ac.koreatech.mpteam9.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.ac.koreatech.mpteam9.R;
import kr.ac.koreatech.mpteam9.models.ModelComment;

public class AdapterCommentsBull extends RecyclerView.Adapter<AdapterCommentsBull.MyHolder> {

    Context context;
    ArrayList<ModelComment> commentList;
    String myUid, postId;

    public AdapterCommentsBull(Context context, ArrayList<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(commentList.get(i).getTimestamp()));
        String pTime = DateFormat.format("yyyy/MM/dd aa hh:mm", calendar).toString();

        myHolder.nameTv.setText(commentList.get(i).getuName());
        myHolder.commentTv.setText(commentList.get(i).getComment());
        myHolder.timeTv.setText(pTime);

        String uid = commentList.get(i).getUid();
        String cid = commentList.get(i).getcId();
        String image = commentList.get(i).getuDp();
        try{
            Picasso.get().load(image).placeholder(R.drawable.kakako).into(myHolder.avatarIv);
        } catch (Exception e) { }

        myHolder.itemView.findViewById(R.id.deleteBtnC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myUid.equals(uid)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setMessage("정말로 댓글을 삭제하겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                } else{
                    Toast.makeText(context,"다른 사람이 쓴 댓글입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteComment(String cid) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsBull").child(postId);
        ref.child("Comments").child(cid).removeValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return (commentList != null ? commentList.size() : 0);
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;

        public MyHolder(@NonNull View itemView){
            super(itemView);
            this.avatarIv = itemView.findViewById(R.id.userImgC);
            this.nameTv = itemView.findViewById(R.id.userIdC);
            this.commentTv = itemView.findViewById(R.id.contentC);
            this.timeTv = itemView.findViewById(R.id.userDateC);
        }
    }
}