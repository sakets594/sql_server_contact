package com.example.sks.login_register;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;
;
public class UserAreaActivity extends AppCompatActivity {
    String username,email,name;
    static int userid;
    SharedPreferences sp;
    private RecyclerView mRecyclerView;
    private ContactsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    SearchView searchView;
    private  DBManager dbManager;
    Cursor cursor;
    private ProgressDialog mProgress;
    Connection con;
    public static int getUserid(){
        return userid;
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        //updateList();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        username=sp.getString("username","username_undefined");
        email=sp.getString("email","email_undefined");
        name=sp.getString("name","name_undefined");
        userid=sp.getInt("userid",-1);
        dbManager = new DBManager(this);
        mProgress = new ProgressDialog(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        try{
            dbManager.open();
            cursor = dbManager.fetch();

            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter =  new ContactsAdapter(this, tmpData(), new ContactsAdapter.ContactsAdapterListener() {
                @Override
                public void onContactSelected(Contact contact) {
                    Intent i= new Intent(UserAreaActivity.this,DetailActivity.class);
                    i.putExtra("contact_name",contact.getName());
                    i.putExtra("contact_number",contact.getNumber1());
                    i.putExtra("email",contact.getEmail());
                    i.putExtra("website",contact.getWebsite());
                    i.putExtra("city",contact.getCity());
                    i.putExtra("state",contact.getState());
                    i.putExtra("country",contact.getCountry());
                    i.putExtra("contact_id",contact.getContactid());
                    startActivity(i);


                }
            });
            mRecyclerView.setAdapter(mAdapter);

        }
        catch (Exception e){
            Log.i("her",e.toString());
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(UserAreaActivity.this);
            dlgAlert.setMessage(e.getMessage());
            dlgAlert.setTitle("database error");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }




        if (!sp.getBoolean("everSync",false)) {
            //suncronising on first time{}
            sp.edit().putBoolean("everSync",true).apply();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem listUser=menu.findItem(R.id.listUser);
        MenuItem addUser=menu.findItem(R.id.addUser);

        if(sp.getString("role","user").toLowerCase().equals("director")) {
            listUser.setVisible(true);
            addUser.setVisible(true);

        }
        else if(sp.getString("role","user").toLowerCase().equals("manager")){
            listUser.setVisible(true);
            addUser.setVisible(false);
        }
        else
        {
            listUser.setVisible(false);
            listUser.setVisible(false);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUser:
                return true;
            case R.id.sync:
                Synchronize2 synchronize2=new Synchronize2();
                synchronize2.execute();
                return true;

            case R.id.addContact:
                startActivity(new Intent(this, AddContactActivity.class));
                return true;
            case R.id.change_password_menu:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            case R.id.log_out:
                log_out();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void log_out(){
        sp.edit().putString("userid","-1").apply();
        sp.edit().putString("username","").apply();
        sp.edit().putString("name","").apply();
        sp.edit().putString("email","").apply();
        sp.edit().putBoolean("isLoggedIn",false).apply();
        sp.edit().putBoolean("everSync",true).apply();
    }

    @Override
    public void onBackPressed() {
        //stops from going back to login page once logged in
        moveTaskToBack(true);
    }

    public List<Contact> tmpData(){
        List<Contact> tmp_list=new ArrayList<Contact>();
        try{
            dbManager.open();
            cursor=dbManager.fetch();}catch (Exception e){}


        if (cursor.moveToFirst()){
            do{
                //String data = cursor.getString(cursor.getColumnIndex("data"));
                Contact tmp=new Contact(cursor.getString(cursor.getColumnIndex(DatabaseHelper.contactName)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.email)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.phone)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.website)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.city)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.state)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.country))

                );
                tmp_list.add(tmp);
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        return  tmp_list;
    }

    public void updateList(){

            mAdapter.contactList.clear();

        mAdapter.contactList.addAll(tmpData());
        mAdapter.notifyDataSetChanged();
    }




    public class Synchronize2 extends AsyncTask<String,String,String> {//synchronize data withserver
        String warning_msg="";
        Boolean isSuccess =false;

        @Override
        protected void onPreExecute() {
            mProgress.show();
            dbManager.deleteAllContacts();
        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.hide();

            if(isSuccess){
                sp.edit().putBoolean("everSync",true).apply();
                updateList();
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(UserAreaActivity.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("fetched data");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
            else
            {

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(UserAreaActivity.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("Failed to fetch data");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }

        }

        @Override
        protected String doInBackground(String... strings) {


                try{
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.43.15:1433;database=app_data;integratedSecurity=true","SA","Sks@12345");

                    if(con==null){
                        warning_msg = "Check Your Internet Access!";
                    }
                    else{
                        String query ;
                        PreparedStatement statement;
                        if(sp.getString("role","user").equals("director")){
                            query = "use app_data;select * from contacts ";
                            statement = con.prepareStatement(query);


                        }
                        else {
                            query = "use app_data;select * from contacts where addedby =?";//prepared statement to avoid sql injection
                            statement = con.prepareStatement(query);
                            statement.setString(1,String.valueOf(sp.getInt("userid",0)));

                        }
                        ResultSet resultSet = statement.executeQuery();

                        while (resultSet.next()){
                            dbManager.insert(
                                    resultSet.getString("name"),
                                    resultSet.getString("phone"),
                                    resultSet.getString("email"),
                                    resultSet.getString("website"),
                                    resultSet.getString("city"),
                                    resultSet.getString("state"),
                                    resultSet.getString("country"),
                                    resultSet.getInt("addedby")
                            );
                        }
                        isSuccess=true;

                    }
                }catch (Exception e){
                    isSuccess =false;
                    sp.edit().putBoolean("isLoggedIn",false);
                    warning_msg=e.toString();
                }


            return warning_msg;

        }
    }



}
