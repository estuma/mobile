package kr.ac.koreatech.mpteam9.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kr.ac.koreatech.mpteam9.DetailBullActivity;
import kr.ac.koreatech.mpteam9.R;
import kr.ac.koreatech.mpteam9.models.ModelBulletinPostList;

public class AdapterBulletinPostLists extends RecyclerView.Adapter<AdapterBulletinPostLists.BulletinListHolder> implements Filterable {

    Context context;
    ArrayList<ModelBulletinPostList> postList;
    ArrayList<ModelBulletinPostList> postListsAll;
    ArrayList<ModelBulletinPostList> postListsAll2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference bulletinPostRef;


    public AdapterBulletinPostLists(Context context, ArrayList<ModelBulletinPostList> postList, ArrayList<ModelBulletinPostList> postList2) {
        this.context = context;
        this.postList = postList;
        this.postListsAll = postList2;
        this.postListsAll2 = new ArrayList<>(postList);
    }

    @NonNull
    @Override
    public AdapterBulletinPostLists.BulletinListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bulletin_list, parent, false);
        return new BulletinListHolder(view);
    }

    @Override
    // 각 아이템들에 대한 매칭
    public void onBindViewHolder(@NonNull AdapterBulletinPostLists.BulletinListHolder holder, int position) {
        holder.titleTv.setText(postList.get(position).getpTitle());
    }

    @Override
    public int getItemCount() {
        return (postList != null ? postList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ModelBulletinPostList> filteredList = new ArrayList<>();
            String filterPattern = charSequence.toString().toLowerCase().trim();

            if(postListsAll.size() == 0) {
                postListsAll = new ArrayList<>(postList);
                loadPosts2();
            }

            for(ModelBulletinPostList item : postListsAll) {
                if (item.getpTitle().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        // runs on ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            postList.clear();
            postList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    //view holder class
    public class BulletinListHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        TextView titleTv;

        public BulletinListHolder(@NonNull View itemView){
            super(itemView);

            this.titleTv = itemView.findViewById(R.id.list_title);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, DetailBullActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("pId", String.valueOf(postList.get(pos).getpId()));
                        intent.putExtra("uId", postList.get(pos).getuId());


                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    public void loadPosts2(){
        firebaseDatabase = FirebaseDatabase.getInstance();

        bulletinPostRef = firebaseDatabase.getReference("PostsBull");
        bulletinPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListsAll.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelBulletinPostList postList = ds.getValue(ModelBulletinPostList.class);

                    postListsAll.add(0, postList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e("AdapterBulletinPost", String.valueOf(error.toException()));
            }
        });
    }
}