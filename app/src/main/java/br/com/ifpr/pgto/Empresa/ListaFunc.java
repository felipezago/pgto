package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Model.Funcionario;
import br.com.ifpr.pgto.Model.RecyclerAdapter;
import br.com.ifpr.pgto.R;

public class ListaFunc extends AppCompatActivity {

    Button btnNovo, btnVoltar;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    List<Funcionario> lista = new ArrayList<Funcionario>();
    List<Funcionario> listaAdapter = new ArrayList<Funcionario>();
    ProgressBar pb;
    Empresa e = UsuarioFirebase.getDadosEmpresaLogada();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_func);

        btnNovo = findViewById(R.id.btnNovo);
        btnVoltar = findViewById(R.id.btnVoltar);
        pb = findViewById(R.id.progressbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref= firebaseDatabase.getReference("Funcionario");
        Log.d("id", "onCreate: "+e.getId());
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Funcionario u = dataSnapshot1.getValue(Funcionario.class);
                    lista.add(u);
                }

                for(Funcionario f: lista ){

                    if(f.getEmpresa().getId().equals(e.getId())){
                        listaAdapter.add(f);
                    }
                }


                adapter = new RecyclerAdapter(ListaFunc.this, listaAdapter);
                recyclerView.setAdapter(adapter);

                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListaFunc.this, CadastroFunc.class);
                startActivity(i);
                finish();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListaFunc.this, PrincipalEmpresa.class);
                startActivity(i);
                finish();
            }
        });


    }
}
