package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.R;

public class PrincipalEmpresa extends AppCompatActivity {

    Empresa e = UsuarioFirebase.getDadosEmpresaLogada();
    TextView txEmp, txFunc, txNome;
    Button imgFunc, imgEmp;
    List<Empresa> lista;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_empresa);

        txEmp = findViewById(R.id.descEmp);
        txFunc = findViewById(R.id.descFunc);
        txNome = findViewById(R.id.desc);
        imgEmp = findViewById(R.id.imgEmp);
        imgFunc = findViewById(R.id.imgFunc);

        txNome.setText(e.getRazaoSocial());

        firebaseDatabase = FirebaseDatabase.getInstance();


        lista = new ArrayList<Empresa>();
        lista.add(e);

/*        for(Empresa a: lista){
            Log.d("cnpj", ""+a.getCnpj());
            Log.d("email", ""+a.getEmail());
            Log.d("end", ""+a.getEndereço());
            Log.d("id", ""+a.getId());
            Log.d("razao", ""+a.getRazaoSocial());

        }*/

        imgEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PrincipalEmpresa.this, VisualizaEmpresa.class);
                startActivity(i);
            }
        });

        imgFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PrincipalEmpresa.this, ListaFunc.class);
                startActivity(i);
            }
        });

    }
}
