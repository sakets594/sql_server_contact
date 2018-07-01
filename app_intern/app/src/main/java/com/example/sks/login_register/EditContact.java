package com.example.sks.login_register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class EditContact extends AppCompatActivity {
    SharedPreferences sp;
    Contact contact;
    TextView contact_et,number_et,email_et,website_et,state_et,country_et,city_et;
    Button save_btn;
    private ProgressDialog mProgress;
    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        contact_et=findViewById(R.id.name_et);
        number_et=findViewById(R.id.phone_et);
        email_et=findViewById(R.id.email_et);
        website_et=findViewById(R.id.website_et);
        state_et=findViewById(R.id.state_et);
        country_et=findViewById(R.id.country_et);
        city_et=findViewById(R.id.city_et);
        save_btn=findViewById(R.id.save_btn);
        mProgress = new ProgressDialog(this);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        contact=new Contact(getIntent().getStringExtra("contact_name"),
                getIntent().getStringExtra("contact_number"),
                getIntent().getStringExtra("email"),
                getIntent().getStringExtra("website"),
                getIntent().getStringExtra("city"),
                getIntent().getStringExtra("state"),
                getIntent().getStringExtra("country"),
                getIntent().getIntExtra("addedby",1),
                getIntent().getIntExtra("contact_id",0)

        );


        contact_et.setText(contact.getName());
        number_et.setText(contact.getNumber1());
        email_et.setText(contact.getEmail());
        website_et.setText(contact.getWebsite());
        state_et.setText(contact.getState());
        city_et.setText( contact.getCity());
        country_et.setText(contact.getCountry());


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ModifyCont modifyContact=new ModifyCont();
                    int [] i=new int[1];
                    i[0]=getIntent().getIntExtra("contact_id",0);
                    modifyContact.execute(String.valueOf(i));
            }
        });

    }



    public class ModifyCont extends AsyncTask<String,String,String> {//synchronize data withserver
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(EditContact.this);
                dlgAlert.setMessage(warning_msg+"successfull\n sync to show updated data");
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(EditContact.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("Failed to update");
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
        protected String doInBackground(String... contactID) {


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
                        query = "use app_data;update contacts set name=?,phone=?,email=?,website=?,city=?,state=?,country=?,addedby=? where contactid=?";
                        statement = con.prepareStatement(query);
                        statement.setString(1,contact.getName());
                        statement.setString(2,contact.getNumber1());
                        statement.setString(3,contact.getEmail());
                        statement.setString(4,contact.getWebsite());
                        statement.setString(5,contact.getCity());
                        statement.setString(6,contact.getState());
                        statement.setString(7,contact.getCountry());
                        statement.setString(8,String.valueOf(sp.getInt("userid",1)));
                        statement.setString(9,String.valueOf(contact.getContactid()));

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
}
