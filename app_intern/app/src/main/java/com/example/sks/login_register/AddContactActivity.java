package com.example.sks.login_register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddContactActivity extends AppCompatActivity {
    SharedPreferences sp;
    private ProgressDialog mProgress;
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final EditText name_et=findViewById(R.id.name_et);
        final EditText phone_et=findViewById(R.id.phone_et);
        final EditText email_et=findViewById(R.id.email_et);
        final EditText website_et=findViewById(R.id.website_et);
        final EditText city_et=findViewById(R.id.city_et);
        final EditText state_et=findViewById(R.id.state_et);
        final EditText country_et=findViewById(R.id.country_et);
        Button save_btn=findViewById(R.id.save_btn);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        mProgress = new ProgressDialog(this);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="";
                Contact[] contact=new Contact[1];
                contact[0]=new Contact(name_et.getText().toString(),
                        email_et.getText().toString(),
                        phone_et.getText().toString(),
                        website_et.getText().toString(),
                        city_et.getText().toString(),
                        state_et.getText().toString(),
                        country_et.getText().toString());
                if(!contact[0].validate().equals("")){
                    msg=contact[0].validate();
                }
                else{

                    addContact checkLogin =new addContact();
                    checkLogin.execute(contact);
                }
                if(!msg.equals("")) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(AddContactActivity.this);
                    dlgAlert.setMessage(msg+"");
                    dlgAlert.setTitle("failed to insert data");
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
        });



    }


    public class addContact extends AsyncTask<Contact,String,String> {//synchronize data withserver
        String warning_msg="";
        Boolean isSuccess =false;

        @Override
        protected void onPreExecute() {
            mProgress.show();

        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.hide();

            if(isSuccess){

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddContactActivity.this);
                dlgAlert.setMessage(warning_msg+"successfull");
                dlgAlert.setTitle("adding data");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

            }
            else
            {

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(AddContactActivity.this);
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
        protected String doInBackground(Contact... contact) {


            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.43.15:1433;database=app_data;integratedSecurity=true","SA","Sks@12345");

                if(con==null){
                    warning_msg = "Check Your Internet Access!";
                }
                else{
                    String query ;
                    PreparedStatement statement;
                    if(sp.getString("blocked","yes").equals("no")){
                        query = "use app_data;insert into contacts (name,phone,email,website,city,state,country,addedby) values (?,?,?,?,?,?,?,?)";
                        statement = con.prepareStatement(query);
                        statement.setString(1,contact[0].getName());
                        statement.setString(2,contact[0].getNumber1());
                        statement.setString(3,contact[0].getEmail());
                        statement.setString(4,contact[0].getWebsite());
                        statement.setString(5,contact[0].getCity());
                        statement.setString(6,contact[0].getState());
                        statement.setString(7,contact[0].getCountry());
                        statement.setString(8,String.valueOf(sp.getInt("userid",1)));
                        int resultSet = statement.executeUpdate();
                        isSuccess=resultSet!=0;

                    }



                }

            }catch (Exception e){
                isSuccess =false;
                sp.edit().putBoolean("isLoggedIn",false);
                warning_msg=e.toString();
            }


            return warning_msg;

        }
    }
    private void done(){
        finish();
    }

}
