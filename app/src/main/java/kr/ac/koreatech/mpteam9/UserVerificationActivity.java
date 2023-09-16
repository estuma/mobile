package kr.ac.koreatech.mpteam9;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserVerificationActivity extends AppCompatActivity {
    EditText phoneNum, checkNum;
    static final int SMS_SEND_PERMISSION = 1;
    String randCheckNum;
    Button checkBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vertification);
        getSupportActionBar().setTitle("본인인증");
        firebaseAuth = FirebaseAuth.getInstance();

        phoneNum = (EditText) findViewById(R.id.uv_phoneNum);
        checkNum = (EditText) findViewById(R.id.uv_input_checkNum);
        checkBtn = (Button) findViewById(R.id.uv_btn_phoneNum);

        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissonCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){
                Toast.makeText(this,"SMS 권한이 필요합니다.",Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},SMS_SEND_PERMISSION);
        }
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsNum = phoneNum.getText().toString().trim();
                if(TextUtils.isEmpty(smsNum)){
                    Toast.makeText(getApplicationContext(),"핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                randCheckNum = numberGen(4);
                sendSms(smsNum, "[모여봐요 병천살이] 인증 번호:" + randCheckNum);
            }
        });
    }

    private void sendSms(String phoneNum, String message){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT),0);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED),0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(),"문자가 전송되었습니다. 확인해주세요!",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum, null, message, sentPI, deliverPI);
    }

    public static String numberGen(int len){
        Random rand = new Random();
        String numStr = "";
        for(int i=0;i<len;i++){
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }
        return numStr.trim();
    }

    public void onCancelCLick(View v){
        String checkNum2 = checkNum.getText().toString().trim();
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();
        if(TextUtils.isEmpty(checkNum2)){
            Toast.makeText(this,"인증번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkNum2.equals(randCheckNum)){
            Toast.makeText(this,"인증 되었습니다.",Toast.LENGTH_SHORT).show();
            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put("userVertification", "완료");
            databaseReference.child("Users").child(currentUserUid).updateChildren(taskMap);

            Intent profileAct_call = new Intent(getApplicationContext(), ProfileActivity.class);
            profileAct_call.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //같은 액티비티가 여러번 쌓이지 않게 한다.
            profileAct_call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택에 쌓여있던 모든 액티비티를 삭제한다.
            startActivity(profileAct_call);
            finish();
        }
        else{
            Toast.makeText(this,"번호가 다릅니다."+randCheckNum,Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
