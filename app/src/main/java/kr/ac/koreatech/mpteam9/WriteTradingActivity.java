package kr.ac.koreatech.mpteam9;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class WriteTradingActivity extends AppCompatActivity {
    Spinner spinDorm, spinKind, spinState;
    int needNum = 0;
    int reserveNum = 0;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    String[] cameraPermissions;
    String[] storagePermissions;

    EditText titleTradeW, contentTradeW, needNumW, reserveNumW;
    ImageView imageTradeIv;
    Button regTradeBtn, attachTradeBtn;

    String name, email, uid, dp;

    String editTitle, editContent, editImage, editNeed, editReserve;

    Uri image_rui = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_trading);     // 글쓰기

        needNumW = findViewById(R.id.needNumW);
        reserveNumW = findViewById(R.id.reserveNumW);

        // 필요인원 & 모집인원 초기화
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        needNumW.setText(String.valueOf(needNum));
        reserveNumW.setText(String.valueOf(reserveNum));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        titleTradeW = findViewById(R.id.titleTradeW);
        contentTradeW = findViewById(R.id.contentTradeW);
        attachTradeBtn = findViewById(R.id.attachTradeBtn);
        imageTradeIv = findViewById(R.id.imageTradeIv);
        regTradeBtn = findViewById(R.id.regTradeBtn);

        Intent intent = getIntent();
        final String isUpdateKey = "" + intent.getStringExtra("key");
        final String editPostId = "" + intent.getStringExtra("editPostId");
        if(isUpdateKey.equals("editPost")){
            getSupportActionBar().setTitle("게시글 수정");
            loadPostData(editPostId);
        } else{
            getSupportActionBar().setTitle("게시글 작성");
        }

        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attachTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!checkStoragePermission())
                    requestStoragePermission();
                else
                    pickFromGallery();
            }
        });

        regTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String title = titleTradeW.getText().toString().trim();
                String content = contentTradeW.getText().toString().trim();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(getApplicationContext(),"제목을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getApplicationContext(),"내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isUpdateKey.equals("editPost")){
                    beginUpdate(title, content, editPostId);
                } else{
                    uploadData(title, content);
                }
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        needNumW.addTextChangedListener(new TextWatcher() {
            String preText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                preText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(preText)) return;

                if(reserveNumW.isFocusable() && !editable.toString().equals("")){
                    try{
                        needNum = Integer.parseInt(editable.toString());
                        if (reserveNum > needNum) {
                            reserveNum = Integer.parseInt(String.valueOf(needNumW.getText()));
                        }

                    } catch (NumberFormatException e){
                        e.printStackTrace();
                        return;
                    }
                    reserveNumW.setText(String.valueOf(reserveNum));
                }
            }
        });

        reserveNumW.addTextChangedListener(new TextWatcher() {
            String preText;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                preText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(preText)) return;

                if(reserveNumW.isFocusable() && !editable.toString().equals("")){
                    try{
                        reserveNum = Integer.parseInt(editable.toString());
                        if (reserveNum > needNum) {
                            reserveNum = Integer.parseInt(String.valueOf(needNumW.getText()));
                        }

                    } catch (NumberFormatException e){
                        e.printStackTrace();
                        return;
                    }
                    reserveNumW.setText(String.valueOf(reserveNum));
                }
            }
        });

        spinDorm = findViewById(R.id.dormW);
        ArrayAdapter dormAdapter = ArrayAdapter.createFromResource(this, R.array.dormitory, android.R.layout.simple_spinner_dropdown_item);
        dormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDorm.setAdapter(dormAdapter);

        spinDorm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinKind = findViewById(R.id.kindW);
        ArrayAdapter kindAdapter = ArrayAdapter.createFromResource(this, R.array.kind, android.R.layout.simple_spinner_dropdown_item);
        kindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKind.setAdapter(kindAdapter);

        spinKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinState = findViewById(R.id.stateW);
        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this, R.array.state, android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinState.setAdapter(stateAdapter);

        spinKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void beginUpdate(String title, String content, String editPostId) {
        if(!editImage.equals("noImage")){
            updateWasWithImage(title, content, editPostId);
        } else if(imageTradeIv.getDrawable() != null){
            updateWithNowImage(title, content, editPostId);
        } else{
            updateWithoutImage(title, content, editPostId);
        }
    }

    private void updateWithoutImage(String title, String content, String editPostId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("uEmail", email);
        hashMap.put("uDp", dp);
        hashMap.put("pTitle", title);
        hashMap.put("pContent", content);
        hashMap.put("pImage", "noImage");

        String dormWString = spinDorm.getSelectedItem().toString();
        String kindWString = spinKind.getSelectedItem().toString();
        String stateWString = spinState.getSelectedItem().toString();

        hashMap.put("pDorm", dormWString);
        hashMap.put("pKind", kindWString);
        hashMap.put("pState", stateWString);

        hashMap.put("pNeedNum", String.valueOf(needNum));
        hashMap.put("pReserveNum", String.valueOf(reserveNum));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
        ref.child(editPostId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        titleTradeW.setText("");
                        contentTradeW.setText("");
                        imageTradeIv.setImageURI(null);
                        image_rui = null;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWithNowImage(String title, String content, String editPostId) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "PostsTrade/" + timeStamp;

        Bitmap bitmap = ((BitmapDrawable)imageTradeIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();
                        if(uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("uName", name);
                            hashMap.put("uEmail", email);
                            hashMap.put("uDp", dp);
                            hashMap.put("pTitle", title);
                            hashMap.put("pContent", content);
                            hashMap.put("pImage", downloadUri);

                            String dormWString = spinDorm.getSelectedItem().toString();
                            String kindWString = spinKind.getSelectedItem().toString();
                            String stateWString = spinState.getSelectedItem().toString();

                            hashMap.put("pDorm", dormWString);
                            hashMap.put("pKind", kindWString);
                            hashMap.put("pState", stateWString);

                            hashMap.put("pNeedNum", String.valueOf(needNum));
                            hashMap.put("pReserveNum", String.valueOf(reserveNum));

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
                            ref.child(editPostId)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                                            titleTradeW.setText("");
                                            contentTradeW.setText("");
                                            imageTradeIv.setImageURI(null);
                                            image_rui = null;
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWasWithImage(String title, String content, String editPostId) {
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "PostsTrade/" + timeStamp;

                        Bitmap bitmap = ((BitmapDrawable)imageTradeIv.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while(!uriTask.isSuccessful());

                                        String downloadUri = uriTask.getResult().toString();
                                        if(uriTask.isSuccessful()) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("uid", uid);
                                            hashMap.put("uName", name);
                                            hashMap.put("uEmail", email);
                                            hashMap.put("uDp", dp);
                                            hashMap.put("pTitle", title);
                                            hashMap.put("pContent", content);
                                            hashMap.put("pImage", downloadUri);

                                            String dormWString = spinDorm.getSelectedItem().toString();
                                            String kindWString = spinKind.getSelectedItem().toString();
                                            String stateWString = spinState.getSelectedItem().toString();

                                            hashMap.put("pDorm", dormWString);
                                            hashMap.put("pKind", kindWString);
                                            hashMap.put("pState", stateWString);

                                            hashMap.put("pNeedNum", String.valueOf(needNum));
                                            hashMap.put("pReserveNum", String.valueOf(reserveNum));

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
                                            ref.child(editPostId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            finish();
                                                            titleTradeW.setText("");
                                                            contentTradeW.setText("");
                                                            imageTradeIv.setImageURI(null);
                                                            image_rui = null;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void loadPostData(String editPostId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
        Query fquery = ref.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    editTitle = "" + ds.child("pTitle").getValue();
                    editContent = "" + ds.child("pContent").getValue();
                    editImage = "" + ds.child("pImage").getValue();
                    editNeed = "" + ds.child("pNeedNum").getValue();
                    editReserve = "" + ds.child("pReserveNum").getValue();

                    String pDorm = "" + ds.child("pDorm").getValue();
                    String pKind = "" + ds.child("pKind").getValue();
                    String pState = "" + ds.child("pState").getValue();

                    titleTradeW.setText(editTitle);
                    contentTradeW.setText(editContent);
                    needNumW.setText(editNeed);
                    reserveNumW.setText(editReserve);

                    Resources resources = getResources();
                    String []dormitory = resources.getStringArray(R.array.dormitory);
                    String []kind = resources.getStringArray(R.array.kind);
                    String []state = resources.getStringArray(R.array.state);

                    int indexDorm = Arrays.binarySearch(dormitory, pDorm);
                    int indexKind = Arrays.binarySearch(kind, pKind);
                    int indexState = Arrays.binarySearch(state, pState);

                    spinDorm.setSelection(indexDorm);
                    spinKind.setSelection(indexKind);
                    spinState.setSelection(indexState);

                    if(!editImage.equals("noImage")){
                        try{
                            Picasso.get().load(editImage).into(imageTradeIv);
                        } catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClick(View v){
        EditText need = findViewById(R.id.needNumW);
        EditText reserve = findViewById(R.id.reserveNumW);

        switch(v.getId()){
            case R.id.reserveUpBtn:
                reserveNum = Integer.parseInt(String.valueOf(reserve.getText()));
                if(reserveNum < needNum)
                    reserveNum++;
                else{
                    reserveNum = Integer.parseInt(String.valueOf(need.getText()));
                }
                reserve.setText(String.valueOf(reserveNum));
                break;
            case R.id.reserveDownBtn:
                reserveNum = Integer.parseInt(String.valueOf(reserve.getText()));
                if(reserveNum > 0) {
                    if(reserveNum > needNum){
                        reserveNum = Integer.parseInt(String.valueOf(need.getText()));
                    } else {
                        reserveNum--;
                    }
                    reserve.setText(String.valueOf(reserveNum));
                }
                break;
            case R.id.needUpBtn:
                needNum = Integer.parseInt(String.valueOf(need.getText()));
                needNum++;
                need.setText(String.valueOf(needNum));
                break;
            case R.id.needDownBtn:
                needNum = Integer.parseInt(String.valueOf(need.getText()));
                if(needNum > 0) {
                    needNum--;
                    need.setText(String.valueOf(needNum));
                }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void uploadData(String title, String content) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
//        String filePathAndName = "PostsTrade/" + "post_trade_" + timeStamp;
        String filePathAndName = "PostsTrade/" + timeStamp;

        if(imageTradeIv.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)imageTradeIv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail", email);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pContent", content);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);

                                String dormWString = spinDorm.getSelectedItem().toString();
                                String kindWString = spinKind.getSelectedItem().toString();
                                String stateWString = spinState.getSelectedItem().toString();

                                hashMap.put("pDorm", dormWString);
                                hashMap.put("pKind", kindWString);
                                hashMap.put("pState", stateWString);

                                hashMap.put("pNeedNum", String.valueOf(needNum));
                                hashMap.put("pReserveNum", String.valueOf(reserveNum));

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(),"업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                                                titleTradeW.setText("");
                                                contentTradeW.setText("");
                                                imageTradeIv.setImageURI(null);
                                                image_rui = null;
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else{
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pContent", content);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            String dormWString = spinDorm.getSelectedItem().toString();
            String kindWString = spinKind.getSelectedItem().toString();
            String stateWString = spinState.getSelectedItem().toString();

            hashMap.put("pDorm", dormWString);
            hashMap.put("pKind", kindWString);
            hashMap.put("pState", stateWString);

            hashMap.put("pNeedNum", String.valueOf(needNum));
            hashMap.put("pReserveNum", String.valueOf(reserveNum));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PostsTrade");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            Toast.makeText(getApplicationContext(),"업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                            titleTradeW.setText("");
                            contentTradeW.setText("");
                            imageTradeIv.setImageURI(null);
                            image_rui = null;
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            email = user.getEmail();
            uid = user.getUid();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    } else{
                        Toast.makeText(getApplicationContext(),"카메라 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                } else{}
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    } else{
                        Toast.makeText(getApplicationContext(),"갤러리 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                } else{}
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_rui = data.getData();
                imageTradeIv.setImageURI(image_rui);
            } else if(resultCode == IMAGE_PICK_CAMERA_CODE){
                imageTradeIv.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}