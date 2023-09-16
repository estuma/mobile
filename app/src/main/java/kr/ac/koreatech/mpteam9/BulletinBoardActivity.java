package kr.ac.koreatech.mpteam9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.ac.koreatech.mpteam9.adapters.AdapterBulletinPostLists;
import kr.ac.koreatech.mpteam9.models.ModelBulletinPostList;
import kr.ac.koreatech.mpteam9.models.ModelTradingPostList;


public class BulletinBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterBulletinPostLists adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ModelBulletinPostList> bulletinPostLists;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference bulletinPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_board);

        getSupportActionBar().setTitle("자유 게시판");

        // 리사이클러뷰
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        bulletinPostLists = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        loadPosts();

        adapter = new AdapterBulletinPostLists(this, bulletinPostLists, bulletinPostLists);
        recyclerView.setAdapter(adapter);
    }

    public void onClick(View v){
        Intent intent = null;

        switch(v.getId()){
            case R.id.writeBulletinButton:
                intent = new Intent(BulletinBoardActivity.this, WriteBulletinActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 액션바
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_board, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("") || s.length()==0){
                    loadPosts();
                } else {
                    adapter.getFilter().filter(s);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search:
                Log.v("ActionBar", "app_bar_search button");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadPosts(){
        bulletinPostRef = firebaseDatabase.getReference("PostsBull");
        bulletinPostRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bulletinPostLists.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelBulletinPostList postList = ds.getValue(ModelBulletinPostList.class);

                    bulletinPostLists.add(0, postList);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e("BulletinBoardActivity", String.valueOf(error.toException()));
            }
        });
    }
}