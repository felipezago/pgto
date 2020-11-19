package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;
import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Model.Funcionario;
import br.com.ifpr.pgto.Principal;
import br.com.ifpr.pgto.R;
import br.com.ifpr.pgto.Util.MaskEditUtil;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class CadastroFunc extends AppCompatActivity {

    TextView txNome, txCargo, txCPF, txEmail, txDep, txDataAdmissão;
    RadioButton rdSim, rdNao;
    RadioGroup rd;
    EditText edtNome, edtCargo, edtCPF, edtEmail, edtQtDep, edtDataAdmissão;
    Button btnAvança, btnVoltar;
    RelativeLayout rel;
    String userAtual, senhaAtual;
    ProgressBar pb;
    Empresa e = UsuarioFirebase.getDadosEmpresaLogada();
    DatabaseReference ref;
    List<Empresa> lista = new ArrayList<Empresa>();
    CircularProgressButton btnFinaliza;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        init();

        edtCPF.addTextChangedListener(MaskEditUtil.mask(edtCPF, MaskEditUtil.FORMAT_CPF));
        edtDataAdmissão.addTextChangedListener(MaskEditUtil.mask(edtDataAdmissão, MaskEditUtil.FORMAT_DATE));

        pb = findViewById(R.id.progressbar);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        ref= firebaseDatabase.getReference("Empresas").child(e.getId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Empresa e = dataSnapshot.getValue(Empresa.class);
                senhaAtual = e.getSenha();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userAtual = e.getEmail();

        Log.d("email atual", "onCreate: email:" +userAtual);
        Log.d("senha atual", "onCreate: senha:" +senhaAtual);

        btnAvança.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaCampos();
            }
        });



        rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rdSim.isChecked()){
                    edtQtDep.setVisibility(View.VISIBLE);
                }else if(rdNao.isChecked()){
                    edtQtDep.setVisibility(View.GONE);
                }
            }
        });

        btnFinaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaCamposFinalizar();
                //showLoadingBar();
                btnFinaliza.startAnimation();

                cadastrar();

            }
        });


        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnAvança.getVisibility() == View.GONE){
                    voltar();
                }else{
                    Intent intent = new Intent(CadastroFunc.this, ListaFunc.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    public void startAnimation(){
        btnFinaliza.startAnimation();
    }

    public void voltar(){
        edtNome.setVisibility(View.VISIBLE);
        edtEmail.setVisibility(View.VISIBLE);
        edtCPF.setVisibility(View.VISIBLE);
        btnAvança.setVisibility(View.VISIBLE);
        txNome.setVisibility(View.VISIBLE);
        txEmail.setVisibility(View.VISIBLE);
        txCPF.setVisibility(View.VISIBLE);

        edtCargo.setVisibility(View.GONE);
        edtDataAdmissão.setVisibility(View.GONE);

        rd.setVisibility(View.GONE);
        txCargo.setVisibility(View.GONE);
        txDataAdmissão.setVisibility(View.GONE);
        txDep.setVisibility(View.GONE);
        btnFinaliza.setVisibility(View.GONE);

    }

    public void logInLogOff(){

        autenticacao.signOut();

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                userAtual, senhaAtual
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    AsyncTask<Void, Void, Void> demoLoad = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {


                            return null;
                        }
                    };
                    demoLoad.execute();
                    startAnimation();
                    //UsuarioFirebase.redirecionaUsuarioLogado(CadastroFunc.this);
                    finish();
                }else{
                    try{
                        String excecao = "";
                        try {
                            throw task.getException();
                        }catch ( FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não cadastrado!";
                            btnFinaliza.revertAnimation();
                            e.printStackTrace();

                        }catch ( FirebaseAuthInvalidCredentialsException e){

                            excecao = "E-mail e senha não correspondem a nenhum cadastro";
                            btnFinaliza.revertAnimation();

                        }catch ( Exception e){

                            excecao = "Erro ao cadastrar usuario: " +e.getMessage();
                            e.printStackTrace();
                            btnFinaliza.revertAnimation();

                        }

                        Toast.makeText(CadastroFunc.this,
                                excecao, Toast.LENGTH_SHORT).show();
                        btnFinaliza.revertAnimation();


                    }catch (Exception e){
                        e.printStackTrace();
                        btnFinaliza.revertAnimation();
                    }

                }

            }
        });

    }

    public void cadastrar(){
        final Funcionario funcionario = new Funcionario();
        String nome = MaskEditUtil.unmask(edtNome.getText().toString());
        String cpf = edtCPF.getText().toString();
        String senha = "semsenha";
        String cargo = edtCargo.getText().toString();
        String email = edtEmail.getText().toString();
        String date = "";
        int dependentes = 0;

        if(edtQtDep.length() > 0){
            dependentes= Integer.parseInt(edtQtDep.getText().toString());
        }

        addData(funcionario, date);

        funcionario.setCpf(cpf);
        funcionario.setEmail(email);
        funcionario.setNome(nome);
        funcionario.setSenha(senha);
        funcionario.setQtDependentes(dependentes);
        funcionario.setCargo(cargo);

        threadConsultaEmpresa(funcionario);

        cadastrarFuncionario(funcionario);
    }

    public void addData(Funcionario funcionario, String date){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.

        Date dateObject;

        try{
            String dob_var=(edtDataAdmissão.getText().toString());

            dateObject = formatter.parse(dob_var);

            date = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
            funcionario.setDataAdmissao(date);

        }

        catch (java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("E11111111111", e.toString());
        }

    }

    public void threadConsultaEmpresa(final Funcionario func){

        new Thread(new Runnable(){
            public void run(){
                ref = FirebaseDatabase.getInstance().getReference("Empresas").child(e.getId());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Empresa a = dataSnapshot.getValue(Empresa.class);

                        lista.add(a);

                        for(Empresa e: lista){
                            func.setEmpresa(e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }).start();
    }


    public void redirecionaLogin(){

        try {

            TimeUnit.SECONDS.sleep(3);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        Intent intent = new Intent(this, ListaFunc.class);
        startActivity(intent);


    }

    public void showLoadingBar(){

        pb.setVisibility(View.VISIBLE);
    }

    public void hideLoadingBar(){
        pb.setVisibility(View.GONE);
    }

    public void cadastrarFuncionario(final Funcionario emp) {

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                emp.getEmail(),
                emp.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    btnFinaliza.revertAnimation();
                    try {
                        String idUsuario = task.getResult().getUser().getUid();

                        logInLogOff();
                        emp.setId(idUsuario);
                        emp.salvar();
                        Log.i("tag", "SUCESSO");
//                        /UsuarioFirebase.AtualizaNome(emp.getNome());

                        redirecionaLogin();

                        Toast.makeText(CadastroFunc.this, "Funcionário: "+emp.getNome()+" cadastrado", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    String excecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {

                        excecao = "Digite uma senha mais forte!";
                        btnFinaliza.revertAnimation();
                    } catch (FirebaseAuthUserCollisionException e) {

                        excecao = "Esta conta já está cadastrada";
                        btnFinaliza.revertAnimation();
                        e.printStackTrace();
                    } catch (Exception e) {

                        excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                        btnFinaliza.revertAnimation();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroFunc.this,
                            excecao, Toast.LENGTH_SHORT).show();
                    btnFinaliza.revertAnimation();

                }
            }
        });

    }


    public void trocaCampos(){

        edtNome.setVisibility(View.GONE);
        edtEmail.setVisibility(View.GONE);
        edtCPF.setVisibility(View.GONE);
        btnAvança.setVisibility(View.GONE);
        txNome.setVisibility(View.GONE);
        txEmail.setVisibility(View.GONE);
        txCPF.setVisibility(View.GONE);

        edtCargo.setVisibility(View.VISIBLE);
        edtDataAdmissão.setVisibility(View.VISIBLE);

        rd.setVisibility(View.VISIBLE);
        txCargo.setVisibility(View.VISIBLE);
        txDataAdmissão.setVisibility(View.VISIBLE);
        txDep.setVisibility(View.VISIBLE);
        btnFinaliza.setVisibility(View.VISIBLE);



        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.toolbar);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rel.setLayoutParams(params);

    }

    public void validaCampos(){
            boolean validador = true;

            String nome = edtNome.getText().toString();
            String email = edtEmail.getText().toString();
            String cpf = edtCPF.getText().toString();

            if(nome.isEmpty()){
                edtNome.setError("Campo Nome é obrigatótio!");
                validador = false;
            }

            if(email.isEmpty()){
                edtEmail.setError("Campo Email é obrigatótio!");
                validador = false;
            }

            if(cpf.isEmpty()){
                edtCPF.setError("Campo CPF é obrigatótio!");
                validador = false;
            }else if(edtCPF.length() < 14){
                edtCPF.setError("Favor preencher o campo CPF Completo");
                validador = false;
            }

           /* if(edtDataAdmissão.length() < 10){
                edtDataAdmissão.setError("Favor preencher o campo Data Completo");
                validador = false;
            }*/

            if(validador){

                trocaCampos();
                rdNao.setSelected(true);

            }

    }

    public void validaCamposFinalizar(){
        boolean validador = true;

        String cargo = edtNome.getText().toString();
        String data = edtDataAdmissão.getText().toString();


        if(cargo.isEmpty()){
            edtNome.setError("Campo Cargo é obrigatótio!");
            validador = false;
        }

        if(data.isEmpty()){
            edtEmail.setError("Campo Email é obrigatótio!");
            validador = false;
        }


        if(validador){

            trocaCampos();

        }

    }

    public void init(){
        txNome = findViewById(R.id.txNomeFunc);
        txCargo = findViewById(R.id.txCargoFunc);
        txCPF = findViewById(R.id.txCPFFunc);
        txEmail = findViewById(R.id.txEmailFunc);
        txDep = findViewById(R.id.txDependentes);
        txDataAdmissão = findViewById(R.id.txDataAdmissaoFunc);

        rel = findViewById(R.id.relaCargo);

        btnAvança = findViewById(R.id.btnAvançar1);
        btnFinaliza = findViewById(R.id.btnAvançar2);

        rdSim = findViewById(R.id.sim);
        rdNao = findViewById(R.id.nao);
        rd = findViewById(R.id.radioGroup);

        edtNome = findViewById(R.id.edtNomeFunc);
        edtCargo = findViewById(R.id.edtCargoFunc);
        edtCPF = findViewById(R.id.edtCpfFunc);
        edtEmail = findViewById(R.id.edtEmailFunc);
        edtQtDep = findViewById(R.id.edtDependentes);
        edtDataAdmissão = findViewById(R.id.edtDataAdmissaoFunc);

        edtCargo.setVisibility(View.GONE);
        edtDataAdmissão.setVisibility(View.GONE);
        edtQtDep.setVisibility(View.GONE);
        rd.setVisibility(View.GONE);
        txCargo.setVisibility(View.GONE);
        txDataAdmissão.setVisibility(View.GONE);
        txDep.setVisibility(View.GONE);
        btnFinaliza.setVisibility(View.GONE);

        btnVoltar = findViewById(R.id.btnVoltar);
    }
}
