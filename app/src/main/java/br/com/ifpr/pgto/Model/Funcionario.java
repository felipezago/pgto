package br.com.ifpr.pgto.Model;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;
import br.com.ifpr.pgto.Model.Empresa;


@SuppressWarnings("serial")
public class Funcionario {

    private String id;
    private String email;
    private String senha;
    private Empresa empresa;
    private String cpf;
    private String nome;
    private String cargo;
    private int qtDependentes;
    private String dataAdmissao;

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getQtDependentes() {
        return qtDependentes;
    }

    public void setQtDependentes(int qtDependentes) {
        this.qtDependentes = qtDependentes;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracoesFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("Funcionario").child(getId());

        usuarios.setValue(this);

    }


}
