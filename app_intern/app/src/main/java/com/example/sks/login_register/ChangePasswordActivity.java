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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldpassword_et,newpassword_et, username_et;
    Button change;
    private ProgressDialog mProgress;
    Connection con;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldpassword_et=findViewById(R.id.oldPass);
        username_et=findViewById(R.id.username);
        newpassword_et=findViewById(R.id.newPass);
        change=findViewById(R.id.change_pass_btn);
        mProgress = new ProgressDialog(this);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        if(!sp.getString("username","").equals("")){
            username_et.setText(sp.getString("username",""));
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePass changePass=new ChangePass();
                changePass.execute();
            }
        });

    }


    public class ChangePass extends AsyncTask<String,String,String> {
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ChangePasswordActivity.this);
                dlgAlert.setMessage("successful");
                dlgAlert.setTitle("password change");
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ChangePasswordActivity.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("Failed to change password");
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
            String oldpassword = oldpassword_et.getText().toString();
            String newpassword = newpassword_et.getText().toString();
            if(username.trim().equals("")|| oldpassword.trim().equals("")||newpassword.trim().equals("") ){
                warning_msg = "Username And Password cant be empty";
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
                        String query = "use app_data;update users set pass=? where username= ? and pass = ?  ";//prepared statement to avoid sql injection
                        PreparedStatement statement = con.prepareStatement(query);
                        statement.setString(1,newpassword);
                        statement.setString(2,username);
                        statement.setString(3,oldpassword);

                        int resultSet = statement.executeUpdate();

                        if(resultSet!=0){
                            isSuccess=true;
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
