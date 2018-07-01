package com.example.sks.login_register;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sks on 30/6/18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "Contacts";
    public static final String TABLE_NAME_insert = "newContact";

    // Table columns
    public static final String  contactName= "name";
    public static final String phone = "phone";
    public static final String email = "email";
    public static final String website = "website";
    public static final String city = "city";
    public static final String state = "state";
    public static final String country = "country";
    public static final String addedBy = "addedby";
    public static final String contactId = "contactId";


    // Database Information
    static final String DB_NAME = "app_data";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table contacts ("+contactName+" varchar(50),"+phone
            +" varchar(20),"+email+" varchar(50),"+website+" varchar(100),"+city+" varchar(100),"+state
            +" varchar(50),"+country+" varchar(50),"+addedBy+" int ,"+contactId+" int)";
    private static final String CREATE_TABLE2 = "create table "+TABLE_NAME_insert+" ("+contactName+" varchar(50),"+phone
            +" varchar(20),"+email+" varchar(50),"+website+" varchar(100),"+city+" varchar(100),"+state
            +" varchar(50),"+country+" varchar(50),"+addedBy+" int ,"+contactId+" int autoincrement)";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void drop(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void altr(SQLiteDatabase db){
        db.execSQL("IF NOT EXISTS( SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS where tablename= '"+
                        TABLE_NAME+"' AND table_schema = '"+DB_NAME
                        +"'AND column_name = '"+"last"+"')  THEN "+
                "ALTER TABLE "+TABLE_NAME+" ADD last  datetime DEFAULT (datetime('now','localtime'));"+
        "END IF;");
        db.execSQL("IF NOT EXISTS( SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS where tablename= '"+
                TABLE_NAME_insert+"' AND table_schema = '"+DB_NAME
                +"'AND column_name = '"+"last"+"')  THEN "+
                "ALTER TABLE "+TABLE_NAME_insert+" ADD last  datetime DEFAULT (datetime('now','localtime'));"+
                "END IF;");
    }

}