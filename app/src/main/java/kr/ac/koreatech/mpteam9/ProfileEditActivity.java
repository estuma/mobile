package kr.ac.koreatech.mpteam9;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {
    Spinner spinDorm;
    String nickname, email, name, joinDate, image, vertification;
    int buildingIndex = 0;
    TextView joinDate_tv, email_tv, name_tv, vertification_tv;
    EditText nickname_tv;
    ImageView profileImage_tv;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    File tempFile;
    Uri imageUri;
    String pathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        getSupportActionBar().setTitle("프로필 수정");

        firebaseAuth = FirebaseAuth.getInstance();

        Intent profileAct = getIntent();
        nickname = profileAct.getStringExtra("nickname");
        email = profileAct.getStringExtra("email");
        name = profileAct.getStringExtra("name");
        joinDate = profileAct.getStringExtra("joinDate");
        buildingIndex = profileAct.getIntExtra("buildingIndex", 0);
        image = profileAct.getStringExtra("image");
        vertification = profileAct.getStringExtra("vertification");

        spinDorm = findViewById(R.id.belongSpinner);

        ArrayAdapter dormAdapter = ArrayAdapter.createFromResource(this, R.array.buildingList, android.R.layout.simple_spinner_dropdown_item);
        dormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDorm.setAdapter(dormAdapter);
        spinDorm.setSelection(buildingIndex);
        spinDorm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buildingIndex = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        joinDate_tv = (TextView) findViewById(R.id.p_joindate);
        email_tv = (TextView) findViewById(R.id.p_email);
        name_tv = (TextView) findViewById(R.id.p_name);
        nickname_tv = (EditText) findViewById(R.id.p_nickname);
        profileImage_tv = (ImageView) findViewById(R.id.p_profileImage);
        vertification_tv = (TextView) findViewById(R.id.p_vertificationCheck);
        try {
            Picasso.get().load(image).into(profileImage_tv);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.kakako).into(profileImage_tv);
        }
        profileImage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAlbum();
            }
        });

        setProfile();
    }

    private void gotoAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1); //pick_from_album == 1
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (tempFile != null){
                if(tempFile.exists()){
                    if(tempFile.delete()){
                        tempFile = null;
                    }
                }
            }
            return;
        }
        switch(requestCode){
            case 1:
                imageUri = data.getData();
                pathUri = getPath(data.getData());
                profileImage_tv.setImageURI(imageUri);
                break;
        }
    }

    public String getPath(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj , null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    public void onProfileEditClick(View v){
        if(v.getId() == R.id.editPasswdButton2){
            String currentUserUid = firebaseAuth.getCurrentUser().getUid();
            String newNickname = nickname_tv.getText().toString();
            String newBuildingName = spinDorm.getSelectedItem().toString();

            if(!TextUtils.isEmpty(pathUri)){
                final Uri file = Uri.fromFile(new File(pathUri));

                StorageReference storageReference = firebaseStorage.getReference().child("UserProfileImages").child(currentUserUid+"/"+file.getLastPathSegment());
                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while(!imageUrl.isComplete());
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("nickname", newNickname);
                        taskMap.put("BuildingIndex", Integer.toString(buildingIndex));
                        taskMap.put("Building", newBuildingName);
                        taskMap.put("image", imageUrl.getResult().toString());
                        databaseReference.child("Users").child(currentUserUid).updateChildren(taskMap);
                    }
                });
            }
            else{
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("nickname", newNickname);
                taskMap.put("BuildingIndex", Integer.toString(buildingIndex));
                taskMap.put("Building", newBuildingName);
                databaseReference.child("Users").child(currentUserUid).updateChildren(taskMap);
            }

            Toast.makeText(ProfileEditActivity.this, "변경 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Intent profileAct_call = new Intent(getApplicationContext(), ProfileActivity.class);
            profileAct_call.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //같은 액티비티가 여러번 쌓이지 않게 한다.
            profileAct_call.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택에 쌓여있던 모든 액티비티를 삭제한다.
            startActivity(profileAct_call);
            finish();
        }
    }

    public void setProfile(){
        nickname_tv.setText(nickname);
        email_tv.setText(email);
        name_tv.setText(name);
        joinDate_tv.setText(joinDate);
        vertification_tv.setText(vertification);
    }
}