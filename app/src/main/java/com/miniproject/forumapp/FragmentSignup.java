package com.miniproject.forumapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentSignup extends Fragment {

    TextInputEditText mEmail,mName,mPassword,mCPassword;
    TextInputLayout mTxtEmail,mTxtName,mTxtPassword,mTxtCPassword;
    AppCompatButton mSignupBtn;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        addTextChangeListner();

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr,emailStr,passwordStr,cpasswordStr;
                nameStr = mName.getText().toString().trim();
                emailStr = mEmail.getText().toString().trim();
                passwordStr = mPassword.getText().toString().trim();
                cpasswordStr = mCPassword.getText().toString().trim();

                if (nameStr.equals("")) {
                    mTxtName.setError("This field cannot be empty");
                    return;
                }
                if (emailStr.equals("")) {
                    mTxtEmail.setError("This field cannot be empty");
                    return;
                }
                if (passwordStr.length()<8) {
                    mTxtPassword.setError("Password length should be 8 characters atleast");
                    return;
                }
                if (!passwordStr.equals(cpasswordStr)){
                    mTxtCPassword.setError("Password Doesn't Match");
                    return;
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog = new ProgressDialog(getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                progressDialog.setTitle("Registering");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                fAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //send verification email to user
                            sendVerificationMail();
                            //save user data to firebase
                            saveUserToDatabase(nameStr,emailStr);
                            Intent intent = new Intent(getActivity(), ActivityHome.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }else{
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

            }
        });

        return view;
    }

    private void addTextChangeListner() {
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEmail.getText().toString().trim().length()>0){
                    mTxtEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mName.getText().toString().trim().length()>0){
                    mTxtName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPassword.getText().toString().trim().length()>8){
                    mTxtPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCPassword.getText().toString().trim().equals(mPassword.getText().toString().trim())){
                    mTxtCPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendVerificationMail() {
        FirebaseUser fUser = fAuth.getCurrentUser();
        fUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Verification Email has been sent to you", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToDatabase(String nameStr, String emailStr) {

        Map<String,Object> userdata = new HashMap<>();
        userdata.put(Keys.USERNAME,nameStr);
        userdata.put(Keys.EMAIL,emailStr);
        userdata.put(Keys.AVATAR,0);

        Map<String,Object> map = new HashMap<>();
        userdata.put(Keys.JOINEDFORUMS,map);
        userdata.put(Keys.REQUESTEDFORUMS,map);
        userdata.put(Keys.CREATEDFORUMS,map);
        userdata.put(Keys.FRIENDREQUESTS,map);
        userdata.put(Keys.MYREQUESTS,map);
        userdata.put(Keys.FRIENDS,map);

        fStore.collection(Keys.USER_COLLECTION)
                .document(fAuth.getUid())
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Data Added Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private void initiateWithID() {
        mEmail = view.findViewById(R.id.signup_mail);
        mName = view.findViewById(R.id.signup_name);
        mPassword = view.findViewById(R.id.signup_password);
        mCPassword = view.findViewById(R.id.signup_cpassword);

        mTxtEmail = view.findViewById(R.id.signup_txt_mail);
        mTxtName = view.findViewById(R.id.signup_txt_name);
        mTxtPassword = view.findViewById(R.id.signup_txt_password);
        mTxtCPassword = view.findViewById(R.id.signup_txt_cpassword);

        mSignupBtn = view.findViewById(R.id.signup_btn);
    }
}