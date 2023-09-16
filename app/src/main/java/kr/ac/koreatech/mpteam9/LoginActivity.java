package kr.ac.koreatech.mpteam9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText editTextId;
    EditText editTextPW;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("병천살이");
        getSupportActionBar().hide();

        editTextId = (EditText) findViewById(R.id.id_input);
        editTextPW = (EditText) findViewById(R.id.input_password);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            if(!firebaseAuth.getCurrentUser().isEmailVerified()){
                Toast.makeText(getApplicationContext(),"메일을 먼저 인증해주세요.",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), EmailVerificationActivity.class));
            }
            else{
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    }

    private void userLogin(){
        String userId = editTextId.getText().toString().trim();
        String pw = editTextPW.getText().toString().trim();

        if(TextUtils.isEmpty(userId)){
            Toast.makeText(this,"아이디를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this,"비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        userId = userId + "@koreatech.ac.kr";
        progressDialog.setMessage("로그인 중입니다. 잠시 기다려 주세요...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userId,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    if(!firebaseAuth.getCurrentUser().isEmailVerified()){
                        Toast.makeText(getApplicationContext(),"메일을 먼저 인증해주세요.",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), EmailVerificationActivity.class));
                    }
                    else{
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(getApplicationContext(),"환영합니다!",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onMainClick(View v){
        int viewId = v.getId();
        if(v.getId() == R.id.btt_login){
            userLogin();
        }
        if(v.getId() == R.id.btt_registration){
            Intent in = new Intent(LoginActivity.this, Registration.class);
            startActivity(in);
        }
        if(v.getId() == R.id.btt_findpassword){
            Intent in = new Intent(LoginActivity.this, FindPassword.class);
            startActivity(in);
        }
    }
}