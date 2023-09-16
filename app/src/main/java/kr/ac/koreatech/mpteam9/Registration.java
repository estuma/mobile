package kr.ac.koreatech.mpteam9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class Registration extends AppCompatActivity {
    Spinner liveBuilding;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    EditText editTextuserid, editTextname, editTextnickname, editTextpw, editTextpwCheck;
    int buildingPosition=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle("회원 가입");

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            firebaseAuth.signOut();
        }
        liveBuilding = findViewById(R.id.rg_liveBuilding_input);
        ArrayAdapter buildingAdapter = ArrayAdapter.createFromResource(this, R.array.buildingList, android.R.layout.simple_spinner_dropdown_item);
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        liveBuilding.setAdapter(buildingAdapter);

        liveBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buildingPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressDialog = new ProgressDialog(this);

        editTextuserid = (EditText)findViewById(R.id.rg_id_input);
        editTextname = (EditText) findViewById(R.id.rg_name_input);
        editTextnickname = (EditText) findViewById(R.id.rg_nickname_input);
        editTextpw = (EditText) findViewById(R.id.rg_pw_input);
        editTextpwCheck = (EditText) findViewById(R.id.rg_pwCheck_input);
    }

    public void userRegistration(){
        String userid = editTextuserid.getText().toString().trim();
        String name = editTextname.getText().toString().trim();
        String nickname = editTextnickname.getText().toString().trim();
        String pw = editTextpw.getText().toString().trim();
        String pwCheck = editTextpwCheck.getText().toString().trim();
        String buildingName = liveBuilding.getSelectedItem().toString();

        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this,"아이디를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"이름을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(nickname)){
            Toast.makeText(this,"닉네임을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this,"비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwCheck)){
            Toast.makeText(this,"비밀번호 확인을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        userid = userid + "@koreatech.ac.kr";

        if(pw.equals(pwCheck)){
            progressDialog.setMessage("가입 중입니다...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(userid,pw).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String email = user.getEmail();
                        String uid = user.getUid();
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault());
                        String joinDate = format.format(currentTime);

                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("uid",uid);
                        hashMap.put("email",email);
                        hashMap.put("name",name);
                        hashMap.put("nickname",nickname);
                        hashMap.put("pw",pw);
                        hashMap.put("Building",buildingName);
                        hashMap.put("image", "https://firebasestorage.googleapis.com/v0/b/project-e5ca6.appspot.com/o/UserDefault%2Fkakako.jpg?alt=media&token=27fb3ee3-3d6b-4d3a-aa87-2dcba0fa49ed");
                        hashMap.put("BuildingIndex",Integer.toString(buildingPosition));
                        hashMap.put("joinDate", joinDate);
                        hashMap.put("userVertification", "미완료");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        reference.child(uid).setValue(hashMap);

                        finish();
                        startActivity(new Intent(Registration.this, EmailVerificationActivity.class));
                    }
                    else{
                        TextView errorCase = (TextView) findViewById(R.id.rg_errorCase);
                        errorCase.setText("                               =가입 에러 유형= \n 이미 등록된 이메일 | 암호 최소 6자리 이상 | 서버에러");
                        Toast.makeText(Registration.this, "실패하였습니다. 다시 한번 확인해주세요!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });
        }
        else{
            Toast.makeText(this,"비밀번호가 틀렸습니다. 다시 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void onRegistraionClick(View v){
        if(v.getId() == R.id.btt_realRegistration){
            userRegistration();
        }
    }
}
