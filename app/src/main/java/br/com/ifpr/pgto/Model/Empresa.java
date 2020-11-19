package br.com.ifpr.pgto.Model;
import com.google.firebase.database.DatabaseReference;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;

public class Empresa {

    String id;
    String cnpj;
    String razaoSocial;
    String email;
    String endereço;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String senha;

    public String getEndereço() {
        return endereço;
    }

    public void setEndereço(String endereço) {
        this.endereço = endereço;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracoesFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("Empresas").child(getId());

        usuarios.setValue(this);

    }

}
