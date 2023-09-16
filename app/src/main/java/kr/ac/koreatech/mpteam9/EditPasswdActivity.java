package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditPasswdActivity extends AppCompatActivity {
    String currentPw;
    EditText newPw_tv, newPwCheck_tv, currentPwCheck_tv;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_passwd);
        getSupportActionBar().setTitle("비밀번호 변경");

        Intent profileAct = getIntent();
        currentPw = profileAct.getStringExtra("pw");
        newPw_tv = (EditText) findViewById(R.id.epw_newPw);
        newPwCheck_tv = (EditText) findViewById(R.id.epw_newPwCheck);
        currentPwCheck_tv = (EditText) findViewById(R.id.epw_currentPw);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v){
        String newPw = newPw_tv.getText().toString().trim();
        String newPwCheck = newPwCheck_tv.getText().toString().trim();
        String currentPwCheck = currentPwCheck_tv.getText().toString().trim();

        if(TextUtils.isEmpty(newPw)){
            Toast.makeText(this,"새로운 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(newPwCheck)){
            Toast.makeText(this,"새로운 비밀번호를 다시 확인주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(currentPwCheck)){
            Toast.makeText(this,"현재 비밀번호를 다시 입력해주세요",Toast.LENGTH_SHORT).show();
            return;
        }

        if(newPw.equals(newPwCheck)){
            if(currentPwCheck.equals(currentPw)){
                firebaseAuth.getCurrentUser().updatePassword(newPw);
                String currentUserUid = firebaseAuth.getCurrentUser().getUid();
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("pw", newPw);
                databaseReference.child("Users").child(currentUserUid).updateChildren(taskMap);

                Toast.makeText(this,"비밀번호를 변경하였습니다.",Toast.LENGTH_SHORT).show();
                Intent profileAct_call = new Intent(getApplicationContext(), ProfileActivity.class);
                profileAct_call.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //같은 액티비티가 여러번 쌓이지 않게 한다.
                profileAct_call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택에 쌓여있던 모든 액티비티를 삭제한다.
                startActivity(profileAct_call);
                finish();
            }
            else{
                Toast.makeText(this,"현재 비밀번호가 맞지 않습니다",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            Toast.makeText(this,"새로운 비밀번호가 맞지 않습니다",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}