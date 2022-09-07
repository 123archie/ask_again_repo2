package com.hemant.askagain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileFragment extends Fragment {

    ImageView profilePic;
    TextView personName, personProfession, personEmail, personContact, personGender,personProfession2;
    MaterialButton logOutBtn,editProfileBtn;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        // get bundle data
        getBundleData();

        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        googleSignInClient= GoogleSignIn.getClient(getActivity(), googleSignInOptions);


        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBox();
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                FirebaseDatabase.getInstance().getReference().child("User").child(acct.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userModel = snapshot.getValue(UserModel.class);
                        SetUserInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void getBundleData() {
        Bundle bundle = this.getArguments();
        userModel = new UserModel(bundle.getString("Name"),bundle.getString("ProfilePic"), bundle.getString("Email"));
        userModel.setGender(bundle.getString("Gender"));
        userModel.setProfession(bundle.getString("Profession"));
        userModel.setContactNumber(bundle.getString("Contact"));
        SetUserInfo();
    }

    private void openDialogBox() {
        Log.d("TAG", "openDialogBox: dialog open");
        UpdateProfileDialog updateProfileDialog = new UpdateProfileDialog();
        updateProfileDialog.show(getParentFragmentManager(),"UpdateFragment Dialog");
    }

    private void SetUserInfo() {
        personName.setText(userModel.getName());
        personEmail.setText(userModel.getEmail());
        if(userModel.getProfilePic() != null) {
            Glide.with(MyProfileFragment.this)
                    .load(userModel.getProfilePic())
                    .into(profilePic);
        }
        // when data fields are updated or changed if dialog box is open
        if(userModel.getProfession() != null){
            personProfession.setText(userModel.getProfession());
            personProfession2.setText(userModel.getProfession());
        }
        if(userModel.getGender() != null){
            personGender.setText(userModel.getGender());
        }
        if(userModel.getContactNumber() != null){
            personContact.setText(userModel.getContactNumber());
        }
    }


    private void logOutUser() {
        if(googleSignInClient != null){
            googleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            openSignIn();
                        }
                    });
        }
    }

    private void openSignIn() {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
    }

    private void initViews(View view) {
        profilePic = view.findViewById(R.id.profilePic);
        personName = view.findViewById(R.id.personName);
        personProfession = view.findViewById(R.id.personProfession);
        personEmail = view.findViewById(R.id.personEmail);
        personContact = view.findViewById(R.id.personContact);
        personGender = view.findViewById(R.id.personGender);
        personProfession2 = view.findViewById(R.id.personProfession2);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
    }
}