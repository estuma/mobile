package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.Collection;

import kr.ac.koreatech.mpteam9.adapters.AdapterTradingPostLists;
import kr.ac.koreatech.mpteam9.models.ModelTradingPostList;

public class TradingBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterTradingPostLists adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ModelTradingPostList> tradingPostLists;
    ArrayList<ModelTradingPostList> tradingPostLists2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference tradingPostRef;
    DatabaseReference tradingPostRef2;

    Spinner spinDorm, spinKind;
    private int preDorm = 0;
    private int preKind = 0;
    private int DormSelected = 0;
    private int KindSelected = 0;
    int isSelectedDorm = 1;
    int isSelectedKind = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_board);

        getSupportActionBar().setTitle("거래게시판");

        spinDorm = findViewById(R.id.dorm);
        spinKind = findViewById(R.id.kind);

        // 리사이클러뷰
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tradingPostLists = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        loadPosts();
        loadPosts2();

        adapter = new AdapterTradingPostLists(this, tradingPostLists);
        recyclerView.setAdapter(adapter);

        ArrayAdapter dormAdapter = ArrayAdapter.createFromResource(this, R.array.dormitory, android.R.layout.simple_spinner_dropdown_item);
        dormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDorm.setAdapter(dormAdapter);
        spinDorm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSelectedDorm == 1){
                    isSelectedDorm = 0;
                } else {
                    preDorm = DormSelected;
                    DormSelected = position;
                    if (preDorm != DormSelected) {
                        searchPosts();
                    }
                    isSelectedDorm = -1;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ArrayAdapter kindAdapter = ArrayAdapter.createFromResource(this, R.array.kind, android.R.layout.simple_spinner_dropdown_item);
        kindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKind.setAdapter(kindAdapter);
        spinKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSelectedKind == 1){
                    isSelectedKind = 0;
                } else {
                    preKind = KindSelected;
                    KindSelected = position;
                    if(preKind != KindSelected)
                        searchPosts();
                    isSelectedKind = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    public void loadPosts(){
        tradingPostRef = firebaseDatabase.getReference("PostsTrade");
        tradingPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tradingPostLists.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelTradingPostList postList = ds.getValue(ModelTradingPostList.class);
                    tradingPostLists.add(0, postList);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e("TradingBoardActivity", String.valueOf(error.toException()));
            }
        });
    }

    public void loadPosts2(){
        tradingPostLists2 = new ArrayList<>();
        tradingPostRef2 = firebaseDatabase.getReference("PostsTrade");
        tradingPostRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tradingPostLists2.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelTradingPostList postList = ds.getValue(ModelTradingPostList.class);
                    tradingPostLists2.add(0, postList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e("TradingBoardActivity", String.valueOf(error.toException()));
            }
        });
    }

    public void onClick(View v){
        Intent intent = null;

        switch(v.getId()){
            case R.id.writeTradingButton:
                intent = new Intent(TradingBoardActivity.this, WriteTradingActivity.class);
                startActivity(intent);
                break;
            case R.id.tradeResetBtn:
                Toast.makeText(getApplicationContext(),"초기화 합니다.", Toast.LENGTH_SHORT).show();
                spinDorm.setSelection(0);
                spinKind.setSelection(0);
                DormSelected = 0;
                KindSelected = 0;
                loadPosts();
                break;

        }
    }

    // 액션바
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
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
                if(s.equals("") || s.length() == 0){
                    if(DormSelected == 0 && KindSelected == 0){
                        loadPosts();
                    } else {
                        searchPosts();
                    }
                } else {
                    adapter.getFilter().filter(s);
                    searchPosts();
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
                Log.v("ActionBar", "search button");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void InitializeListData(){
        tradingPostLists = new ArrayList<ModelTradingPostList>();
    }

    public void searchPosts(){
        ArrayList<ModelTradingPostList> filteredList = new ArrayList<>();

        String selectDorm = spinDorm.getSelectedItem().toString();
        String selectKind = spinKind.getSelectedItem().toString();
        String filterPatternDorm = selectDorm.trim();
        String filterPatternKind = selectKind.trim();

        if((preDorm != DormSelected) || (preKind != KindSelected)) {
            if(KindSelected == 0 && DormSelected == 0){
                loadPosts();
            }
            else if (KindSelected == 0 && (preDorm != DormSelected) ) {
                for (ModelTradingPostList item : tradingPostLists2) {
                        if (item.getpDorm().contains(filterPatternDorm)) {
                            filteredList.add(item);
                        }
                }
            } else if (DormSelected == 0 && (preKind != KindSelected)) {
                for (ModelTradingPostList item : tradingPostLists2) {
                    if(item.getpKind().contains(filterPatternKind)) {
                        filteredList.add(item);
                    }
                }
            } else{
                for (ModelTradingPostList item : tradingPostLists2) {
                    if (item.getpKind().contains(filterPatternKind) && item.getpDorm().contains(filterPatternDorm)){
                        filteredList.add(item);
                    }
                }
            }
        }

        tradingPostLists.clear();
        tradingPostLists.addAll((Collection<? extends ModelTradingPostList>) filteredList);
        adapter.notifyDataSetChanged();
    }
}