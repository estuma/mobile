package kr.ac.koreatech.mpteam9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPassword extends AppCompatActivity {
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    TextView userid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        getSupportActionBar().setTitle("비밀번호 찾기");

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = (TextView) findViewById(R.id.fp_id_input);
    }

    public void onFindPWCheck(View v){
        if(v.getId() == R.id.btt_send){
            String emailAddress = userid.getText().toString().trim();

            if(TextUtils.isEmpty(emailAddress)){
                Toast.makeText(this,"아이디를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                return;
            }
            emailAddress = emailAddress+ "@koreatech.ac.kr";

            progressDialog.setMessage("처리중입니다. 잠시 기다려 주세요...");
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Toast.makeText(FindPassword.this, "이메일을 전송하였습니다. 확인해주세요!", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    else{
                        Toast.makeText(FindPassword.this, "메일 전송에 실패하였습니다. 이메일을 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
