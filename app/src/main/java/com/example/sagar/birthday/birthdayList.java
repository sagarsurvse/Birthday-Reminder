package com.example.sagar.birthday;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Sagar on 22-Jan-2022.
 */

public class birthdayList extends AppCompatActivity {

    GridView gridView;
    ArrayList<model> list;
    birthdayAdapter adapter = null;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_list_activity);


        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.show);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.show:
                    return true;

                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProgressBar=(ProgressBar)findViewById(R.id.pb_load);
        mProgressBar.setProgress(i);
        mCountDownTimer=new CountDownTimer(10,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                mProgressBar.setProgress((int)i*10/(10/10));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                mProgressBar.setProgress(100);
                mProgressBar.setVisibility(View.GONE);

                gridView = (GridView)findViewById(R.id.gridView);
                list = new ArrayList<>();
                adapter = new birthdayAdapter(birthdayList.this,R.layout.item_birth,list);
                gridView.setAdapter(adapter);

                //get all Data from sqlite
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM BUDDY");
                list.clear();
                while (cursor.moveToNext()){
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String dob = cursor.getString(2);
                    byte[] image = cursor.getBlob(3);

                    list.add(new model(id , name , dob , image)) ;
                }
                adapter.notifyDataSetChanged();


                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                        CharSequence[]  items = {"Delete"};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(birthdayList.this);

                        dialog.setTitle("Choose an action");
                        dialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0){
                                    //update

                                    Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM BUDDY");
                                    ArrayList<Integer> arrId = new ArrayList<Integer>();
                                    while (c.moveToNext()){
                                        arrId.add(c.getInt(0));
                                    }
                                    //show the updated dialog box here
                                    showDialogUpdate(birthdayList.this , arrId.get(position));
                                }else {
                                    //delete

                                    Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM BUDDY");
                                    ArrayList<Integer> arrId = new ArrayList<Integer>();
                                    while (c.moveToNext()){
                                        arrId.add(c.getInt(0));
                                    }
                                    showDialogueDelete(arrId.get(position));
                                }
                            }
                        });
                        dialog.show();
                        return true;
                    }
                });


            }
        };
        mCountDownTimer.start();




    }
    ImageView imageViewBuddy;
    private void showDialogUpdate(Activity activity ,final  int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_buddy_activity);
        dialog.setTitle("Update");

        final ImageView imageViewBuddy = (ImageView) dialog.findViewById(R.id.imageViewBuddy);
        final EditText edt3  = (EditText) dialog.findViewById(R.id.editText3);
        final EditText edt4  = (EditText) dialog.findViewById(R.id.editText4);
        Button btnUpdate = (Button)dialog.findViewById(R.id.btnUpdate);

        //set width dialog box
        int width  = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        //set height
        int height  = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        imageViewBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request gallery
                ActivityCompat.requestPermissions(
                        birthdayList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateData(
                            edt3.getText().toString().trim(),
                            edt4.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewBuddy),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();

                }
                catch (Exception error){
                    Log.e("Update Error", error.getMessage());
                }
                updateBuddyList();
            }
        });
    }

    private void showDialogueDelete(final int idBuddy){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(birthdayList.this);

        dialogDelete.setTitle("Delete");
        dialogDelete.setMessage("Delete the selected item?");

        dialogDelete.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    MainActivity.sqLiteHelper.deletebuddy(idBuddy);
                    Toast.makeText(birthdayList.this, "Successfully deleted!!", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Log.e("Deletion Error", e.getMessage());
                }
                updateBuddyList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();


    }


    private void updateBuddyList(){
        //get all Data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM BUDDY");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String dob = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new model(id , name , dob , image)) ;
        }
        adapter.notifyDataSetChanged();

    }
 
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,888);
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
        if (requestCode == 888 && resultCode == RESULT_OK && data !=null){
            Uri uri =data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewBuddy.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
