package com.example.sks.login_register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DetailActivity extends AppCompatActivity {
    SharedPreferences sp;
    private ProgressDialog mProgress;
    Connection con;
    Contact contact;
    TextView contact_tv,number_tv,email_tv,website_tv,state_tv,country_tv,city_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);
        contact_tv=findViewById(R.id.name_tv);
        number_tv=findViewById(R.id.phone_tv);
        email_tv=findViewById(R.id.email_tv);
        website_tv=findViewById(R.id.website_tv);
        state_tv=findViewById(R.id.state_tv);
        country_tv=findViewById(R.id.country_tv);
        city_tv=findViewById(R.id.city_tv);
        mProgress = new ProgressDialog(this);
        sp=getSharedPreferences("userInfo",MODE_PRIVATE);

        contact_tv.setText(getIntent().getStringExtra("contact_name"));
        number_tv.setText(getIntent().getStringExtra("contact_number"));
        email_tv.setText(getIntent().getStringExtra("email"));
        website_tv.setText(getIntent().getStringExtra("website"));
        state_tv.setText( getIntent().getStringExtra("state"));
        city_tv.setText( getIntent().getStringExtra("city"));
        country_tv.setText(getIntent().getStringExtra("country"));





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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_director_detail, menu);
        MenuItem delete=menu.findItem(R.id.delete);
        MenuItem edit=menu.findItem(R.id.edit);
        if(sp.getString("role","user").toLowerCase().equals("director")) {
                edit.setVisible(true);
                delete.setVisible(true);

        }
        else if(sp.getString("role","user").toLowerCase().equals("manager")){
                edit.setVisible(true);
                delete.setVisible(false);
        }
        else
        {
            edit.setVisible(false);
            delete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent i=new Intent(DetailActivity.this, EditContact.class);
                i.putExtra("contact_name",contact.getName());
                i.putExtra("contact_number",contact.getNumber1());
                i.putExtra("email",contact.getEmail());
                i.putExtra("website",contact.getWebsite());
                i.putExtra("city",contact.getCity());
                i.putExtra("state",contact.getState());
                i.putExtra("country",contact.getCountry());
                i.putExtra("contact_id",contact.getContactid());
                startActivity(i);
                return true;

            case R.id.delete:
                Delete_Contact dc=new Delete_Contact();
                dc.execute();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class Delete_Contact extends AsyncTask<String,String,String> {//synchronize data withserver
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DetailActivity.this);
                dlgAlert.setMessage(warning_msg+"successfull\n sync to update local data");
                dlgAlert.setTitle("deleting data");
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

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DetailActivity.this);
                dlgAlert.setMessage(warning_msg);
                dlgAlert.setTitle("Failed to delete data");
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
        protected String doInBackground(String... sda) {


            try{
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.43.15:1433;database=app_data;integratedSecurity=true","SA","Sks@12345");

                if(con==null){
                    warning_msg = "Check Your Internet Access!";
                }
                else{
                    String query ;
                    PreparedStatement statement;

                        query = "use app_data;Delete from contacts where contactid=?";
                        statement = con.prepareStatement(query);
                       statement.setString(1,String.valueOf(contact.getContactid()));
                        int resultSet = statement.executeUpdate();
                        isSuccess=resultSet!=0;
                        warning_msg="no such data in cloud\n sync"
;



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
