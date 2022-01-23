package com.example.sagar.birthday;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Sagar on 22-Jan-2022.
 */

public class SQLiteHelper extends SQLiteOpenHelper{
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String name, String dob, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO BUDDY VALUES (NULL,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2,dob);
        statement.bindBlob(3, image);

        statement.executeInsert();
    }

    public void updateData(String name, String dob , byte[] image,int id){
         SQLiteDatabase database = getWritableDatabase();
         String sql = "UPDATE BUDDY SET name = ?,dob=?, image = ? WHERE id=?";
         SQLiteStatement statement = database.compileStatement(sql);

         statement.bindString(1,name);
         statement.bindString(2,dob);
         statement.bindBlob(3,image);
         statement.bindDouble(4,(double)id);

         statement.execute();
         database.close();
    }

    public void deletebuddy(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM BUDDY WHERE id =?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindDouble(1,(double)id);
        statement.execute();
        database.close();

    }



    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql,null);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
