package com.merkado.merkadoclient.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.merkado.merkadoclient.MyMethods;
import com.merkado.merkadoclient.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.merkado.merkadoclient.Views.MainActivity;
import com.merkado.merkadoclient.Views.SignupActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;


public class LoginFragment extends Fragment {



    Context context;
    TextInputLayout emailEditText,passwordEditText;
    MaterialButton login,signup,forgetPassword;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    public LoginFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public LoginFragment() {
    }

    public static LoginFragment newInstance(Context context) {
        return new LoginFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        connectToViews(view);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("برجاء الانتظار....");
        Objects.requireNonNull(emailEditText.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailEditText.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = emailEditText.getEditText().getText().toString().trim();
                if (email.isEmpty())
                    emailEditText.setError("ادخل البريد الالكتروني");
            }
        });
        Objects.requireNonNull(passwordEditText.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordEditText.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = passwordEditText.getEditText().getText().toString().trim();
                if (password.isEmpty())
                    passwordEditText.setError("ادخل كلمة السر ");
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getEditText().getText().toString().trim();
                String password = passwordEditText.getEditText().getText().toString().trim();
                if (email.isEmpty())
                    emailEditText.setError("ادخل البريد الالكتروني");
                if (!MyMethods.isValidEmail(email))
                    emailEditText.setError("من فضلك ادخل بريد الكتروني صحيح");
                if (!email.isEmpty()&&!password.isEmpty()&&MyMethods.isValidEmail(email)) {
                    signIn(email,password);
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpActivity();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email = emailEditText.getEditText().getText().toString().trim();
                if (email.isEmpty())
                    emailEditText.setError("ادخل البريد الالكتروني");
                if (!MyMethods.isValidEmail(email))
                    emailEditText.setError("من فضلك ادخل بريد الكتروني صحيح");
                if (!email.isEmpty()&&MyMethods.isValidEmail(email)) {
                    getNewPassword(email);
                }
            }
        });
        return view;
    }

    private void getNewPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()){
                            new AlertDialog.Builder(context)
                                    .setMessage("تم إرسال رسالة إعادة تعيين كلمة المرور إلى بريدك الالكتروني \n من فضلك أعد تعيين كلمة المرور ثم أعد المحاولة لتسجيل الدخول")
                                    .setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        } else
                            FancyToast.makeText(context,"حدث خطأ ما",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();

                    }
                });
    }

    private void goToSignUpActivity() {
        startActivity(new Intent(context, SignupActivity.class));
    }

    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FancyToast.makeText(context,"تم تسجيل الدخول بنجاح",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("cart","");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    FancyToast.makeText(context,"البريد الالكتروني أو كلمة المرور التي ادخلتهما غير صحيحان",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    emailEditText.requestFocus();
                }
            }
        });
    }


    public void connectToViews(View view) {
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        signup = view.findViewById(R.id.signup);
        forgetPassword = view.findViewById(R.id.forget_password);
    }



}