package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DormInfoAdditionalActivity extends AppCompatActivity {
    int dorm_select = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm_info_additinal);

        getSupportActionBar().setTitle("기숙사 세부 정보");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent dorm = getIntent();
        dorm_select = dorm.getIntExtra("dormSelect", 0);
        setDormInfo(dorm_select);
    }

    public void onClick_dormInfoAdditional(View v){
        if(v.getId() == R.id.btn_left){
            if(dorm_select == 1) Toast.makeText(getApplicationContext(),"첫 번째 정보입니다.", Toast.LENGTH_SHORT).show();
            else dorm_select -= 1;
        }
        if(v.getId() == R.id.btn_right){
            if(dorm_select == 11) Toast.makeText(getApplicationContext(),"마지막 정보입니다.", Toast.LENGTH_SHORT).show();
            else dorm_select += 1;
        }
        setDormInfo(dorm_select);
    }

    public void setDormInfo(int dorm){
        TextView dormName = findViewById(R.id.dorm_name);
        ImageView roomImage = findViewById(R.id.dorm_image);
        TextView elevator = findViewById(R.id.elevator);
        TextView heating = findViewById(R.id.heating);
        TextView toilet = findViewById(R.id.toilet);
        TextView refrigerator = findViewById(R.id.refrigerator);
        TextView veranda = findViewById(R.id.veranda);
        TextView restRoom = findViewById(R.id.restRoom);
        TextView studyRoom = findViewById(R.id.studyRoom);

        if(dorm == 1){ //해울관
            dormName.setText("\u003C 해울관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_haeul);
            elevator.setText("X");
            heating.setText("히터");
            toilet.setText("X");
            refrigerator.setText("X");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("X");
        }
        if(dorm == 2){ //예지관
            dormName.setText("\u003C 예지관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_yegi);
            elevator.setText("X");
            heating.setText("히터");
            toilet.setText("X");
            refrigerator.setText("X");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("O");
        }
        if(dorm == 3){ //함지관
            dormName.setText("\u003C 함지관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_hamgi);
            elevator.setText("X");
            heating.setText("바닥난방");
            toilet.setText("X");
            refrigerator.setText("X");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("O");
        }
        if(dorm == 4){ //다솔관
            dormName.setText("\u003C 다솔관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_dasol);
            elevator.setText("X");
            heating.setText("바닥난방");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("X");
        }
        if(dorm == 5){ //한울관
            dormName.setText("\u003C 한울관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_hanul);
            elevator.setText("X");
            heating.setText("바닥난방");
            toilet.setText("X");
            refrigerator.setText("O");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("O");
        }
        if(dorm == 6){ //참빛관
            dormName.setText("\u003C 참빛관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_chambit);
            elevator.setText("O");
            heating.setText("히터");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("X");
            restRoom.setText("O");
            studyRoom.setText("O");
        }
        if(dorm == 7){ //청솔관
            dormName.setText("\u003C 청솔관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_chungsol);
            elevator.setText("O");
            heating.setText("히터");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("O");
            restRoom.setText("X");
            studyRoom.setText("X");
        }
        if(dorm == 8){ //예솔관
            dormName.setText("\u003C 예솔관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_yeosol);
            elevator.setText("O");
            heating.setText("바닥난방");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("O");
            restRoom.setText("O");
            studyRoom.setText("X");
        }
        if(dorm == 9){ //IH
            dormName.setText("\u003C IH \u003E");
            roomImage.setImageResource(R.drawable.dorm_ih);
            elevator.setText("O");
            heating.setText("히터");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("O");
            restRoom.setText("X");
            studyRoom.setText("X");
        }
        if(dorm == 10){ //은솔관
            dormName.setText("\u003C 은솔관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_eunsol);
            elevator.setText("O");
            heating.setText("바닥난방");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("O");
            restRoom.setText("O");
            studyRoom.setText("X");
        }
        if(dorm == 11){ //솔빛관
            dormName.setText("\u003C 솔빛관 \u003E");
            roomImage.setImageResource(R.drawable.dorm_solbit);
            elevator.setText("O");
            heating.setText("바닥난방");
            toilet.setText("O");
            refrigerator.setText("O");
            veranda.setText("O");
            restRoom.setText("O");
            studyRoom.setText("X");
        }
    }
}