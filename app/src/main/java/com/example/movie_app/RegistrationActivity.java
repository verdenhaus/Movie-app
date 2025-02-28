package com.example.movie_app;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private ConnectionClass connectionClass;
    private Connection con;
    private ResultSet result;
    private String name, str;

    private EditText userEdt, passEdt, emailEdt;
    private Button signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        connectionClass = new ConnectionClass();
        connect();
    }

    public ArrayList<User> getUserList() {
        File file = new File("users.txt");
        ArrayList<User> users = new ArrayList<>();
        if (!file.exists()) {
            return users;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] line_arr = line.split(" ");
                String name = line_arr[0].trim();
                String password = line_arr[1].trim();
                String email = line_arr[2].trim();
                users.add(new User(name, password, email));
            }
        } catch (Exception e) {
            System.out.println("hjkfdvnvskdaln");
        }
        return users;
    }

    public void saveUserList(ArrayList<User> users) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"));

            for (User user : users) {
                writer.write(user.getUsername() + " " + user.getPassword() + " "+user.getEmail() +"\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("gyueidcnsml");
        }
    }

    public boolean validationEmail(){
        emailEdt = findViewById(R.id.editTextEmail);
        Pattern pattern  =  Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");
        Matcher match= pattern.matcher(emailEdt.getText());

        String email = emailEdt.getText().toString();
        if(match.find() && match.group().matches(email)){
            return true;
        }
        else {
            Toast.makeText(RegistrationActivity.this, "Invalid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void regBtnClick(View view) {

    }

    public void connect(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try{
                con = connectionClass.CONN();
                if(con==null){
                    str = "Error in connection with MySQL server";
                }else{
                    str = "Connected with MySQL server";
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            runOnUiThread(()->{
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
            });
        });
    }
}
