package kr.ac.koreatech.mpteam9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView userEmail;
    String email;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_vertification);
        getSupportActionBar().setTitle("메일 인증");

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userEmail = (TextView) findViewById(R.id.ev_userEmail);
        email = user.getEmail();
        userEmail.setText(email);
    }

    public void onSendClick(View v){
        progressDialog.setMessage("메일 전송 중입니다. 잠시 기다려 주세요...");
        progressDialog.show();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"메일을 전송하였습니다. 확인해주세요!",Toast.LENGTH_LONG).show();
                    finish();
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"메일 전송에 실패하였습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
