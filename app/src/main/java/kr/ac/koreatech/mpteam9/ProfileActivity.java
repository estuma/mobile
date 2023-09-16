package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import kr.ac.koreatech.mpteam9.models.Profile_list;

public class ProfileActivity extends AppCompatActivity {
    TextView nickname, email, name, building, joinDate, vertification;
    ImageView profileImage;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    int building_index = 0;
    String pnickname, pemail, pname, pbuilding, pbuildingIndex, pjoindate, pw, pimage, pvertification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("개인 정보");

        nickname = (TextView) findViewById(R.id.p_nickname);
        email = (TextView)findViewById(R.id.p_email);
        name = (TextView)findViewById(R.id.p_name);
        building = (TextView)findViewById(R.id.p_building);
        joinDate = (TextView)findViewById(R.id.p_joindate);
        profileImage = (ImageView)findViewById(R.id.p_profileImage);
        vertification = (TextView) findViewById(R.id.p_vertification);
        firebaseAuth = FirebaseAuth.getInstance();

        setProfile();
    }

    public void setProfile(){
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("Users").child(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile_list profile = snapshot.getValue(Profile_list.class);

                pnickname = profile.getNickname();
                String email_t = profile.getEmail();
                String[] em = email_t.split("@");
                pemail = em[0]+"\n"+"@koreatech.ac.kr";
                pname = profile.getName();
                pbuilding = profile.getBuilding();
                pbuildingIndex = profile.getBuildingIndex();
                pjoindate = profile.getJoinDate();
                pw = profile.getPw();
                pimage = profile.getImage();
                pvertification = profile.getUserVertification();

                nickname.setText(pnickname);
                email.setText(pemail);
                name.setText(pname);
                building.setText(pbuilding);
                joinDate.setText(pjoindate);
                vertification.setText(pvertification);
                building_index = Integer.parseInt(pbuildingIndex);
                try {
                    Picasso.get().load(pimage).into(profileImage);
                }
                catch (Exception e){
                    Picasso.get().load(R.drawable.kakako).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClick(View v){
        Intent intent = null;

        switch(v.getId()){
            case R.id.editProfileButton:
                intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra("nickname", pnickname);
                intent.putExtra("email", pemail);
                intent.putExtra("name", pname);
                intent.putExtra("buildingIndex", building_index);
                intent.putExtra("joinDate", pjoindate);
                intent.putExtra("image", pimage);
                intent.putExtra("vertification",pvertification);
                startActivity(intent);
                break;
            case R.id.editPasswdButton:
                intent = new Intent(ProfileActivity.this, EditPasswdActivity.class);
                intent.putExtra("pw", pw);
                startActivity(intent);
        }
    }

    public void onVertificationClick(View v){
        if(pvertification.equals("완료")){
            Toast.makeText(this,"이미 인증되었습니다.",Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(), UserVerificationActivity.class);
            startActivity(intent);
        }
    }
}