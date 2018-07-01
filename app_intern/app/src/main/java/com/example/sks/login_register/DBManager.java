package com.example.sks.login_register;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by sks on 30/6/18.
 */

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String name, String phone ,String email,
                       String website,String city,String state,String country,long addedby) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.contactName, name);
        contentValue.put(DatabaseHelper.phone, phone);
        contentValue.put(DatabaseHelper.email, email);
        contentValue.put(DatabaseHelper.website, website);
        contentValue.put(DatabaseHelper.city, city);
        contentValue.put(DatabaseHelper.state, state);
        contentValue.put(DatabaseHelper.country, country);
        contentValue.put(DatabaseHelper.addedBy, addedby);

       return  database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public long insert(Contact contact) {

           // return insert(getContentValue(contact));

        return  insert(contact.getName(),contact.getNumber1(),contact.getEmail(),contact.getWebsite(),contact.getCity(),contact.getState(),contact.getCountry(),UserAreaActivity.getUserid());
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.contactName, DatabaseHelper.phone, DatabaseHelper.email,
                DatabaseHelper.website, DatabaseHelper.city, DatabaseHelper.state,
                DatabaseHelper.country, DatabaseHelper.addedBy,DatabaseHelper.contactId
        };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, DatabaseHelper.contactName);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(Contact contact) {
        ContentValues contentValue=getContentValue(contact);
        return database.update(DatabaseHelper.TABLE_NAME, contentValue, DatabaseHelper.contactId + " = "+contact.getContactid()  , null);
        //return i;
    }

    public void delete(String name) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.contactName + "=" + name, null);
    }
    public void deleteAllContacts(){
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }


    public void drop(){
        dbHelper.drop(database);
    }
    public void altr(){
        dbHelper.altr(database);
    }

    public  ContentValues getContentValue(Contact contact){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.contactName, contact.getName());
        contentValue.put(DatabaseHelper.phone, contact.getNumber1());
        contentValue.put(DatabaseHelper.email, contact.getEmail());
        contentValue.put(DatabaseHelper.website, contact.getWebsite());
        contentValue.put(DatabaseHelper.city,contact.getCity());
        contentValue.put(DatabaseHelper.state, contact.getState());
        contentValue.put(DatabaseHelper.country, contact.getCountry());
        contentValue.put(DatabaseHelper.addedBy, contact.getAddedby());
        contentValue.put(DatabaseHelper.contactName, contact.getContactid());
        return contentValue;

    }
}