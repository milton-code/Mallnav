package com.proyecto.mallnav.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.mallnav.R;
import com.proyecto.mallnav.ui.activities.LoginActivity;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    static DocumentReference docRef;
    Button mEdit, mLogout;
    TextView mNombre, mCorreo;
    static String nombre, apellido, correo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        setViewsParams();
        setViewsListeners();
        return view;
    }

    private void setViewsParams() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        mfirestore = FirebaseFirestore.getInstance();
        docRef = mfirestore.collection("user").document(userId);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            nombre = document.getString("nombre");
                            apellido = document.getString("apellido");
                            correo = document.getString("e_mail");
                            mNombre.setText(nombre + " " + apellido);
                            mCorreo.setText(correo);
                        }
                        else {
                            Toast.makeText(getContext(),"Error al obtener el documento"+ task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void setViewsListeners() {
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    mAuth.signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialog editDialog = new EditDialog();
                editDialog.show(getChildFragmentManager(),"navegar a editDialog");
            }
        });
    }

    private void initViews(View view) {
        mNombre = view.findViewById(R.id.usuario);
        mCorreo = view.findViewById(R.id.correo);
        mEdit = view.findViewById(R.id.buttonEditar);
        mLogout = view.findViewById(R.id.buttonLogout);
        mAuth = FirebaseAuth.getInstance();
    }
}