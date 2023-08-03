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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentLogin extends Fragment {

    TextInputEditText mEmail,mPassword;
    TextInputLayout mTxtEmail,mTxtPassword;
    AppCompatButton mLoginBtn;
    TextView mForgotPass;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        initiateWithID();

        initiateFirebaseClasses();

        addTextChangeListner();

        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ActivityForgotPass.class));
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr,passwordStr;

                emailStr = mEmail.getText().toString().trim();
                passwordStr = mPassword.getText().toString().trim();

                if (emailStr.equals("")) {
                    mTxtEmail.setError("This field cannot be empty");
                    return;
                }
                if (passwordStr.length()<8) {
                    mTxtPassword.setError("Password length should be 8 characters atleast");
                    return;
                }

                progressDialog = new ProgressDialog(getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                progressDialog.setTitle("Sign In");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                fAuth.signInWithEmailAndPassword(emailStr,passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getActivity(), ActivityHome.class));
                            getActivity().finish();
                        }else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

    private void initiateWithID() {
        mEmail = view.findViewById(R.id.login_mail);
        mPassword = view.findViewById(R.id.login_password);

        mTxtEmail = view.findViewById(R.id.login_txt_mail);
        mTxtPassword = view.findViewById(R.id.login_txt_password);

        mForgotPass = view.findViewById(R.id.login_forgotpassword);

        mLoginBtn = view.findViewById(R.id.login_btn);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
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
    }
}