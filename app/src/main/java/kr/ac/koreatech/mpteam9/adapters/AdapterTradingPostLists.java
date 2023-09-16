package kr.ac.koreatech.mpteam9.adapters;

import static android.view.View.GONE;

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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.blogspot.atifsoftwares.firebaseapp.R;
//import com.blogspot.atifsoftwares.firebaseapp.models.ModelPost;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import kr.ac.koreatech.mpteam9.DetailTradActivity;
import kr.ac.koreatech.mpteam9.R;
import kr.ac.koreatech.mpteam9.models.ModelTradingPostList;

public class AdapterTradingPostLists extends RecyclerView.Adapter<AdapterTradingPostLists.TradingListHolder> implements Filterable {

    Context context;
    ArrayList<ModelTradingPostList> postList;
    ArrayList<ModelTradingPostList> postListsAll;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference tradingPostRef;

    public AdapterTradingPostLists(Context context, ArrayList<ModelTradingPostList> postList) {
        this.context = context;
        this.postList = postList;
        this.postListsAll = postList;
    }

    @NonNull
    @Override
    public TradingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_trading_list, parent, false);
        return new TradingListHolder(view);
    }

    // 각 아이템들에 대한 매칭
    @Override
    public void onBindViewHolder(@NonNull TradingListHolder holder, int position) {
        holder.titleTv.setText(postList.get(position).getpTitle());
        holder.dormTv.setText(postList.get(position).getpDorm());
        holder.kindTv.setText(postList.get(position).getpKind());
        holder.stateIv.setImageResource(R.drawable.soldout);
        if(!postList.get(position).getpState().equals("판매완료")) {
            holder.stateIv.setVisibility(GONE);
        }
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
            ArrayList<ModelTradingPostList> filteredList = new ArrayList<>();
            String filterPattern = charSequence.toString().toLowerCase().trim();

            if(postListsAll.size() == 0){
                postListsAll = new ArrayList<>(postList);
                loadPosts();
            }

            for(ModelTradingPostList item : postListsAll) {
                if (item.getpTitle().contains(filterPattern)) {
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
            postList.addAll((Collection<? extends ModelTradingPostList>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    //view holder class
    public class TradingListHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        TextView dormTv, kindTv, titleTv;
        ImageView stateIv;

        public TradingListHolder(@NonNull View itemView){
            super(itemView);

            this.dormTv = itemView.findViewById(R.id.list_dorm);
            this.kindTv = itemView.findViewById(R.id.list_kind);
            this.titleTv = itemView.findViewById(R.id.list_title);
            this.stateIv = itemView.findViewById(R.id.list_soldout);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, DetailTradActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("pId", String.valueOf(postList.get(pos).getpId()));
                        intent.putExtra("uId", postList.get(pos).getuId());


                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    public void loadPosts(){
        firebaseDatabase = FirebaseDatabase.getInstance();

        tradingPostRef = firebaseDatabase.getReference("PostsTrade");
        tradingPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListsAll.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelTradingPostList postList = ds.getValue(ModelTradingPostList.class);

                    postListsAll.add(0, postList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e("TradingBoardActivity", String.valueOf(error.toException()));
            }
        });
    }
}