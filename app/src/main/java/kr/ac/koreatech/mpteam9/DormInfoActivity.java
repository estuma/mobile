package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DormInfoActivity extends AppCompatActivity {

    public int dorm_select = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_info);

        getSupportActionBar().setTitle("기숙사 정보");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onClick_dormInfo(View v){
        int id = v.getId();
        ImageView map = findViewById(R.id.map);

        if(id == R.id.btn_haeul){
            dorm_select = 1;
            map.setImageResource(R.drawable.haeul);
        }
        else if(id == R.id.btn_yegi){
            dorm_select = 2;
            map.setImageResource(R.drawable.yegi);
        }
        else if(id == R.id.btn_hamgi){
            dorm_select = 3;
            map.setImageResource(R.drawable.hamgi);
        }
        else if(id == R.id.btn_dasol){
            dorm_select = 4;
            map.setImageResource(R.drawable.dasol);
        }
        else if(id == R.id.btn_hanul){
            dorm_select = 5;
            map.setImageResource(R.drawable.hanul);
        }
        else if(id == R.id.btn_chambit){
            dorm_select = 6;
            map.setImageResource(R.drawable.chambit);
        }
        else if(id == R.id.btn_chungsol){
            dorm_select = 7;
            map.setImageResource(R.drawable.chungsol);
        }
        else if(id == R.id.btn_yeosol){
            dorm_select = 8;
            map.setImageResource(R.drawable.yeosol);
        }
        else if(id == R.id.btn_ih){
            dorm_select = 9;
            map.setImageResource(R.drawable.ih);
        }
        else if(id == R.id.btn_eunsol){
            dorm_select = 10;
            map.setImageResource(R.drawable.eunsol);
        }else if(id == R.id.btn_solbit){
            dorm_select = 11;
            map.setImageResource(R.drawable.solbit);
        }

        if(id == R.id.btn_additionalInfo){
            Intent in = new Intent(DormInfoActivity.this, DormInfoAdditionalActivity.class);
            in.putExtra("dormSelect", dorm_select);
            startActivity(in);
        }

        if(id == R.id.btn_goDormSite){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dorm.koreatech.ac.kr"));
            startActivity(intent);
        }
    }
}