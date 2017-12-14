package com.example.zz.zhihu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class PersonActivity extends AppCompatActivity {
    private String sex;
    private SQLiteOpenHelper dbHelper;
    private String imagePath;
    public static final int CHOOSE_PHOTO=2;
    private boolean set_user_image=false;
    private String username_intent;
    private EditText OldPassword,NewPassword,re_NewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");
        Button person_yes=findViewById(R.id.person_yes);
        Button person_no=findViewById(R.id.person_no);
        final EditText person_nickname=findViewById(R.id.person_nickname);
        final EditText person_signature=findViewById(R.id.person_signature);
        final TextView person_username=findViewById(R.id.person_username);
        final ImageView person_head=findViewById(R.id.head_person);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final Button change_user_image=findViewById(R.id.change_user_image);
        final RadioButton btnWoman=findViewById(R.id.btnWoman);
        final RadioButton btnMan=findViewById(R.id.btnMan);
        final Button ChangePassword=findViewById(R.id.ChangePassword);

        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 设置内容区域为自定义View
                 */
                View view = LayoutInflater.from(PersonActivity.this).inflate(R.layout.change_password,null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(PersonActivity.this);
                dialog.setTitle("修改密码");
                dialog.setIcon(R.drawable.password);
                dialog.setView(view);
                dialog.setCancelable(false);
                OldPassword=view.findViewById(R.id.OldPassword);
                NewPassword=view.findViewById(R.id.NewPassword);
                re_NewPassword=view.findViewById(R.id.re_NewPassword);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (check1()&&check2()){
                            SQLiteDatabase db=dbHelper.getReadableDatabase();
                            ContentValues values=new ContentValues();
                            values.put("password",NewPassword.getText().toString().trim());
                            db.update("user_table",values,"username=?",new String[]{username_intent});
                            Toast.makeText(PersonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("user_table",null,null,null,null,null,null);
        if (cursor.getCount()!=0&&cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                String header=cursor.getString(cursor.getColumnIndex("user_image"));
                String signature=cursor.getString(cursor.getColumnIndex("signature"));
                String sex=cursor.getString(cursor.getColumnIndex("sex"));
                if (username.equals(username_intent)){
                    person_username.setText(username);
                    person_nickname.setText(nickname);
                    person_signature.setText(signature);
                    if(sex.equals("m"))btnMan.setChecked(true);
                    else btnWoman.setChecked(true);
                    Glide.with(this).load(header).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(person_head);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        change_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PersonActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PersonActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        person_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radioGroup.getChildAt(i);
                    if (rd.isChecked()) {
                        if (rd.getText().equals("男"))sex="m";
                        else sex="f";
                        break;
                    }
                }
                if (check1()&&!check2()){
                    SQLiteDatabase sdb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nickname", person_nickname.getText().toString().trim());
                    values.put("signature", person_signature.getText().toString().trim());
                    values.put("sex", sex);
                    if(set_user_image)
                    values.put("user_image", imagePath);
                    sdb.update("user_table" ,values, "username=?",new String[]{person_username.getText().toString()});
                    values.clear();
                    Toast.makeText(PersonActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PersonActivity.this,IndexActivity.class);
                    intent.putExtra("username_intent",person_username.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
            }
            private boolean check1(){
                if (person_nickname.getText().toString().equals("")){
                    Toast.makeText(PersonActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }else if (person_nickname.getText().toString().contains(" ")){
                    Toast.makeText(PersonActivity.this,"不能包含空格",Toast.LENGTH_SHORT).show();
                }else{
                    return true;
                }
                return false;
            }
            private boolean check2() {
                SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                Cursor cursor=sdb.query("user_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                        if (!username.equals(username_intent)&&nickname.equals(person_nickname.getText().toString().trim())){
                            Toast.makeText(PersonActivity.this,"昵称已存在",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                return false;
            }
        });
        person_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PersonActivity.this,IndexActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode != PersonActivity.RESULT_CANCELED&&data!=null) {
                    if (resultCode == RESULT_OK) {
                        // 判断手机系统版本号
                        if (Build.VERSION.SDK_INT>=19)
                            // 4.4及以上系统使用这个方法处理图片
                            handleImageOnKitKat(data);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void displayImage(String imagePath) {
        ImageView head_person=findViewById(R.id.head_person);
        if (imagePath != null) {
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //head_person.setImageBitmap(bitmap);
            Glide.with(this).load(imagePath).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(head_person);
            set_user_image=true;
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        TextView username=findViewById(R.id.person_username);
        Intent intent =new Intent(PersonActivity.this,IndexActivity.class);
        intent.putExtra("username_intent",username.getText().toString());
        startActivity(intent);
        finish();
    }
    public boolean check1(){
        String username,password=null;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query("user_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                username=cursor.getString(cursor.getColumnIndex("username"));
                password=cursor.getString(cursor.getColumnIndex("password"));
                if (username.equals(username_intent))break;
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (OldPassword.getText().toString().equals(""))Toast.makeText(PersonActivity.this,"原密码不能为空",Toast.LENGTH_SHORT).show();
        else if (!password.equals(OldPassword.getText().toString())){
            Toast.makeText(PersonActivity.this,"原密码不正确",Toast.LENGTH_SHORT).show();
        } else return true;
        return false;
    }
    public boolean check2(){
        if (NewPassword.getText().toString().equals("")||re_NewPassword.getText().toString().equals("")){
            Toast.makeText(PersonActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
        }else if (NewPassword.getText().toString().contains(" ")||re_NewPassword.getText().toString().contains(" ")){
            Toast.makeText(PersonActivity.this,"不能包含空格",Toast.LENGTH_SHORT).show();
        }else if (!NewPassword.getText().toString().equals(re_NewPassword.getText().toString())){
            Toast.makeText(PersonActivity.this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
        }else return true;
        return false;
    }

}
