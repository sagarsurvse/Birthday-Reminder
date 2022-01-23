package com.example.sagar.birthday;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by Sagar on 22-Jan-2022.
 */

public class MainActivity extends AppCompatActivity {

    EditText editText,editText2;
    Button button,button2;
    ImageView imageView;
    ImageButton button4;
    private ProgressDialog pDialog;


    //private static int SPLASH_TIME_OUT = 4000;

    final int REQUEST_CODE_GALLERY = 999;
    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,splash.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);

*/
        SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        String id = prefs.getString("accid", "Null");//"No name defined" is the default value.
        if (id.equals("Null")){
            SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
            editor.putString("accid", "123");
            editor.apply();
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.home);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.show:
                    startActivity(new Intent(getApplicationContext(), birthdayList.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.home:
                    return true;
            }
            return false;
        });

        init();

        sqLiteHelper = new SQLiteHelper(MainActivity.this, "BuddyDB.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS BUDDY (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, dob VARCHAR, image BLOG)");

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c= Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,new DateListener(),mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });





        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = ProgressDialog.show(MainActivity.this, "Saving Data..", "Please wait", true,false);
                pDialog.show();
                if (editText.getText().toString().length() > 1 && editText2.getText().toString().length() > 1) {

                    try {
                        sqLiteHelper.insertData(
                                editText.getText().toString().trim(),
                                editText2.getText().toString().trim(),
                                imageViewToByte(imageView)
                        );
                        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                        editText2.setText("");
                       Intent i = new Intent(MainActivity.this, birthdayList.class);
                       startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Enter Name OR Date", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }

            }
        });
    }

    class DateListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month , int day){
            editText2.setText(day+ "-" + (month+1)+ "-" + year);
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] byteArray = stream.toByteArray();
       return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(this, "You don't have enough permission", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data !=null){
            Uri uri =data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory .decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        editText = (EditText)findViewById(R.id.editText);
        editText2 = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        imageView = (ImageView)findViewById(R.id.imageView);
        button4 = (ImageButton)findViewById(R.id.button4);
    }
}
