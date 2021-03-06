package com.learning_app.user.chathamkulam.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nelson Andrew on 03-02-2017.
 */

public class StoreEntireDetails extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StoreData.db";
    public static final String TABLE_NAME = "StoreData";
    public static final String Col_Id = "ID";
    public static final String Col_Country = "COUNTRY";
    public static final String Col_University = "UNIVERSITY";
    public static final String Col_Course = "COURSE";
    public static final String Col_Semester = "SEMESTER";
    public static final String Col_Subject = "SUBJECT";
    public static final String Col_Subject_Id = "SUBJECT_ID";
    public static final String Col_Subject_No = "SUBJECT_NO";
    public static final String Col_Price_type = "PRICE_TYPE";
    public static final String Col_Amount = "AMOUNT";
    public static final String Col_Image = "IMAGE";
    public static final String Col_Validity = "VALIDITY";
    public static final String Col_Free_Validity = "FREE_VALIDITY";
    public static final String Col_Paid_Validity = "PAID_VALIDITY";
    public static final String Col_Duration = "DURATION";
    public static final String Col_VideoCount = "VIDEOCOUNT";
    public static final String Col_NotesCount = "NOTESCOUNT";
    public static final String Col_QbankCount = "QBANKCOUNT";

    public StoreEntireDetails(Context context) {
        super(context, DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " COUNTRY TEXT, UNIVERSITY TEXT, COURSE TEXT, SEMESTER TEXT, SUBJECT TEXT, SUBJECT_ID TEXT, SUBJECT_NO TEXT," +
                " FREE_VALIDITY TEXT,PAID_VALIDITY TEXT,DURATION TEXT,VIDEOCOUNT TEXT,NOTESCOUNT TEXT,QBANKCOUNT TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS" + TABLE_NAME);

    }

    public boolean addData(String country,String university,String course, String semester,
                           String subject,String subject_id,String subject_no,String free_validity,String paid_validity,String duration,
                           String videoCount,String notesCount,String qbankCount){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Country,country);
        contentValues.put(Col_University,university);
        contentValues.put(Col_Course,course);
        contentValues.put(Col_Semester,semester);
        contentValues.put(Col_Subject,subject);
        contentValues.put(Col_Subject_Id,subject_id);
        contentValues.put(Col_Subject_No,subject_no);
        contentValues.put(Col_Free_Validity,free_validity);
        contentValues.put(Col_Paid_Validity,paid_validity);
        contentValues.put(Col_Duration,duration);
        contentValues.put(Col_VideoCount,videoCount);
        contentValues.put(Col_NotesCount,notesCount);
        contentValues.put(Col_QbankCount,qbankCount);

        long result = db.insert(TABLE_NAME,null,contentValues);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
    }

    public Cursor groupMainDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " GROUP BY COUNTRY,SUBJECT_ID,SEMESTER" ,null);
    }

    public Cursor groupSubDetails(String subject_id,String sem_no){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE SUBJECT_ID = ? AND SEMESTER = ?" + " GROUP BY SUBJECT_NO" ,new String[]{subject_id,sem_no});
    }

    public Cursor getSubjectFromTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT SUBJECT FROM " + TABLE_NAME,null);
    }

    public void removeExpireSubject(String subject) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + Col_Subject + "= '" + subject + "'");

        //Close the database
        database.close();
    }

    public Cursor getSubjectRow(String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE SUBJECT = ?", new String[]{subject});

    }

    public boolean ifExists(String member) {

        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + Col_Subject + "= '"+ member + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    public Cursor getVideoPathFromTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT VIDEO FROM " + TABLE_NAME, null);
    }

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
        db.close();
    }

    public Integer DeleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "Id = ?", new String[]{id});
    }
}
