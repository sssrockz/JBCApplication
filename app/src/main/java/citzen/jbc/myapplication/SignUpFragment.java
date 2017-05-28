package citzen.jbc.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shyam on 18-May-17.
 */

public class SignUpFragment extends Fragment {

    final static String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    @BindView(R.id.etname)
    MaterialEditText etname;
    @BindView(R.id.etemail)
    MaterialEditText etemail;
    @BindView(R.id.etcollege)
    MaterialEditText etcollege;
    @BindView(R.id.etreferid)
    MaterialEditText etreferid;
    @BindView(R.id.etpass)
    MaterialEditText etpass;
    @BindView(R.id.etrepass)
    MaterialEditText etrepass;

    String name, email, college, referid, pass, repass;
    String LOG_TAG = "SIGNUP";

    View view;
    FragmentActivity mActivity;
    ProgressDialog mSignUpDialog, mUserUpdateDialog;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mActivity.setResult(Activity.RESULT_CANCELED);
    }

    @OnClick(R.id.btnSignUp)
    void signUp() {

        name = etname.getText().toString();
        email = etemail.getText().toString();
        college = etcollege.getText().toString();
        referid = etreferid.getText().toString();
        pass = etpass.getText().toString();
        repass = etrepass.getText().toString();

        if (!etname.isCharactersCountValid()) {
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etemail.getText().toString()).matches() || !etemail.isCharactersCountValid()) {
            Toast.makeText(mActivity, "Invalid E-mail", Toast.LENGTH_SHORT).show();
            etemail.setError("Invalid E-mail");
            return;
        }
        if (TextUtils.isEmpty(college)) {
            Toast.makeText(mActivity, "Invalid College", Toast.LENGTH_SHORT).show();
            etcollege.setError("Invalid College");
            return;
        }
        if (!etpass.isCharactersCountValid() || !etpass.getText().toString().matches(PASSWORD_PATTERN)) {
            Toast.makeText(mActivity, "Invalid Password", Toast.LENGTH_SHORT).show();
            etpass.setError("Invalid Password");
            return;
        }
        if (!etrepass.isCharactersCountValid() || !pass.equals(repass)) {
            Toast.makeText(mActivity, "RePassword InValid", Toast.LENGTH_SHORT).show();
            etrepass.setError("InValid RePassword");
            return;
        }

        mSignUpDialog = ProgressDialog.show(mActivity, null, "Signing Up", false, false);

        mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e(LOG_TAG, "Sign Up Success");
                Toast.makeText(mActivity, "Sign Up Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mSignUpDialog.dismiss();
                if (task.isSuccessful()) {
                    Log.e(LOG_TAG, "Successful");
                    mActivity.setResult(Activity.RESULT_OK);

                    UserProfileChangeRequest mChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    final FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    if (user != null) {
                        mUserUpdateDialog = ProgressDialog.show(mActivity, null, "Setting up Your Account", false, false);
                        user.updateProfile(mChangeRequest).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setUpUserDataBase(user);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mActivity,"Failed to link users name with the account",Toast.LENGTH_LONG).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserUpdateDialog.dismiss();
                                mActivity.finish();
                            }
                        });
                    }
                }
            }
        });
    }

    private void setUpUserDataBase(FirebaseUser user) {
        //TO-DO list
        /*
        1-create a unique referral key for user by combining name and a random no
        2-put up all the details in users node

         */

        String refKey = name.substring(0, 3) + new Random().nextInt(9999);
        Log.e("Referral key", refKey);

        DatabaseReference mUserRef = mFirebaseDatabase.getReference().child("users").child(user.getUid());
        FirebaseUserProfile mProfile = new FirebaseUserProfile(name, email, college, pass, refKey);
        mUserRef.setValue(mProfile).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
           Toast.makeText(mActivity,"Failed upload users data",Toast.LENGTH_LONG).show();
            }
        });
    }
}