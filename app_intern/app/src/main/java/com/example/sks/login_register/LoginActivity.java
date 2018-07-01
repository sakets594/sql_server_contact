package com.example.sks.login_register;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.os.StrictMode.*;

public class LoginActivity extends AppCompatActivity {

    EditText username_et,password_et;
    Connection con;
    SharedPreferences sp;
    private ProgressDialog mProgress;
    private  DBManager dbManager;
    String username_db,password_db,name_db,ip_db;//database server credentials

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbManager = new DBManager(this);
        try {
            dbManager.open();
            dbManager.altr();
        }
        catch (Exception e){

        }

        username_et=findViewById(R.id.username_et);
        password_et =findViewById(R.id.password_et);
        TextView change_password_tv =findViewById(R.id.change_password_tv);
        Button login_btn =findViewById(R.id.button_login);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Logging you in");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        if (sp.getBoolean("isLoggedIn", false)||false) {
            startActivity(new Intent(this, UserAreaActivity.class));
        }


        change_password_tv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this,ChangePasswordActivity.class));
           }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogin checkLogin =new CheckLogin();
                checkLogin.execute();
            }
        });



    }

   public class CheckLogin extends AsyncTask<String,String,String>{
        String warning_msg="";
        Boolean isSuccess =false;

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.hide();
            password_et.setText("");
            if(isSuccess){
                username_et.setText("");
                startActivity(new Intent(LoginActivity.this,UserAreaActivity.class));
            }
            else
            {
                Log.i("asdf", "  "+warning_msg+" ");
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("Failed to Login");
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
            String username= username_et.getText().toString();
            String password = password_et.getText().toString();
            if(username.trim().equals("")|| password.trim().equals("")) {
                warning_msg = "Enter Username And Password";
            }
            else
            {
                try{
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.43.15:1433;database=app_data;integratedSecurity=true","SA","Sks@12345");

                    if(con==null){
                        warning_msg = "Check Your Internet Access!";
                    }
                    else{
                        String query = "use app_data;select * from users where username= ? and pass = ?  ";//prepared statement to avoid sql injection
                        PreparedStatement statement = con.prepareStatement(query);
                        statement.setString(1,username);
                        statement.setString(2,password);

                        ResultSet resultSet = statement.executeQuery();

                        if(resultSet.next()){
                            sp.edit().putBoolean("isLoggedIn",true).apply();
                            sp.edit().putInt("userid",resultSet.getInt("userid")).apply();
                            sp.edit().putString("username",resultSet.getString("username")).apply();
                            sp.edit().putString("email",resultSet.getString("email")).apply();
                            sp.edit().putString("name",resultSet.getString("name")).apply();
                            sp.edit().putString("blocked",resultSet.getString("blocked")).apply();
                            sp.edit().putString("role",resultSet.getString("role")).apply();
                            sp.edit().putInt("userid",resultSet.getInt("userid")).apply();
                            isSuccess=true && resultSet.getString("blocked").toLowerCase().equals("no");
                        }
                        else{
                            sp.edit().putBoolean("isLoggedIn",false).apply();
                            warning_msg = "Invalid Credentials!";
                            isSuccess = false;

                        }
                    }
                }catch (Exception e){
                    isSuccess =false;
                    sp.edit().putBoolean("isLoggedIn",false);
                    warning_msg=e.toString();
                }

            }
            return warning_msg;

        }
    }





}
