package br.com.ifpr.pgto.Empresa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Model.Funcionario;
import br.com.ifpr.pgto.R;
import br.com.ifpr.pgto.Util.MaskEditUtil;

public class VisualizaFunc extends AppCompatActivity {

    TextView nome, cpf, cargo, data, email, nomeEmp, qtDep;
    Empresa e = UsuarioFirebase.getDadosEmpresaLogada();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    List<Empresa> lista;
    Button btnVoltar;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_func);

        nome = findViewById(R.id.nome);
        cpf = findViewById(R.id.cpfs);
        cargo = findViewById(R.id.cargo);
        data = findViewById(R.id.dataAdm);
        email = findViewById(R.id.email);
        qtDep = findViewById(R.id.dep);
        nomeEmp = findViewById(R.id.empresa);
        btnVoltar = findViewById(R.id.btnVoltar);

        gson = new Gson();

        cpf.addTextChangedListener(MaskEditUtil.maskText(cpf, MaskEditUtil.FORMAT_CPF));

        Funcionario func = gson.fromJson(getIntent().getStringExtra("func"), Funcionario.class);

        cargo.setText(func.getCargo());
        nome.setText(func.getNome());
        cpf.setText(func.getCpf());
        cargo.setText(func.getCargo());
        email.setText(func.getEmail());
        data.setText(func.getDataAdmissao());
        qtDep.setText(Integer.toString(func.getQtDependentes()));
        nomeEmp.setText(func.getEmpresa().getRazaoSocial());

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VisualizaFunc.this, ListaFunc.class);
                startActivity(i);
            }
        });

    }
}
