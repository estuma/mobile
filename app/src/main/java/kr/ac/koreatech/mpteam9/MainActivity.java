package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.koreatech.mpteam9.models.Profile_list;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    String pvertification, pnickname, pimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        checkVerification();
    }

    public void checkVerification(){
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("Users").child(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile_list profile = snapshot.getValue(Profile_list.class);

                pnickname = profile.getNickname();
                pimage = profile.getImage();
                pvertification = profile.getUserVertification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // event handler for buttons in MainActivity
    public void onClick(View view) {
        int buttonId = view.getId();

        if (buttonId == R.id.tradeIcon) {
            if(pvertification.equals("완료")){
                Intent intent = new Intent(this, TradingBoardActivity.class);
                startActivity(intent);
            }
            else if(pvertification.equals("미완료")){
                Toast.makeText(getApplicationContext(),"본인인증 후 이용 가능합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, UserVerificationActivity.class);
                startActivity(intent);
            }
        }
        if (buttonId == R.id.postbullIcon) {
            Intent intent = new Intent(this, BulletinBoardActivity.class);
            startActivity(intent);
        }
        if (buttonId == R.id.buildingIcon) {
            Intent intent = new Intent(this, DormInfoActivity.class);
            startActivity(intent);
        }
        if (buttonId == R.id.chatIcon) {
            Intent intent = new Intent(this, ChatRoomListActivity.class);
            startActivity(intent);
        }
        if (buttonId == R.id.profileIcon) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (buttonId == R.id.text_logout) {
            Toast.makeText(getApplicationContext(),"로그아웃합니다.", Toast.LENGTH_SHORT).show();
            // needs logout process code
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}