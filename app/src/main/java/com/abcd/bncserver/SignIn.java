package com.abcd.bncserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.abcd.bncserver.Common.Common;
import com.abcd.bncserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    Button btnSignIn;
    EditText edtPhone,edtPassword;

    FirebaseDatabase db;
    DatabaseReference users;
    CheckBox ckbRemember;



    public boolean validate() {
        boolean valid = true;
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();


        if (phone.isEmpty() ) {
            edtPhone.setError("Invalid Id");
            valid = false;
        } else {
            edtPhone.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPassword.setError("Invalid Password");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        edtPassword = (EditText) findViewById(R.id.edtpassword);
        edtPhone = (EditText) findViewById(R.id.edtphone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);
        Paper.init(this);



        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = edtPhone.getText().toString();
                String password = edtPassword.getText().toString();
                if (phone.isEmpty() && password.isEmpty()) {
                    edtPhone.setError("Required");
                    edtPassword.setError("Required");
                }
                else{
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog=new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please wait");
                    mDialog.show();

                    final String localPhone=phone;
                    final String localPassword=password;
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(localPhone).exists())
                            {
                                mDialog.dismiss();

                                User user= dataSnapshot.child(localPhone).getValue(User.class);
                                user.setPhone(localPhone);
                                if(Boolean.parseBoolean(user.getIsStaff()))
                                {
                                    if(user.getPassword().equals(localPassword))
                                    {
                                        Toast.makeText(SignIn.this, "Sign In Successfull", Toast.LENGTH_SHORT).show();
                                        Common.currentUser = users;
                                        Intent login=new Intent(SignIn.this,Home.class);
                                        startActivity(login);
                                        finish();
                                    }
                                    else
                                        Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(SignIn.this, "Please Login with Staff Account", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User does not exist in database", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }}
        });
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user != null && pwd != null) {

            if (!user.isEmpty() && !pwd.isEmpty()) {
                Intent home = new Intent(SignIn.this, Home.class);

                startActivity(home);


            }


        }


    }



}

