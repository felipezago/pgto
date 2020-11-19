package br.com.ifpr.pgto.Firebase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.ifpr.pgto.Empresa.PrincipalEmpresa;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Empresa.CadEmp;
import br.com.ifpr.pgto.Principal;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario  = ConfiguracoesFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){

        FirebaseAuth usuario = ConfiguracoesFirebase.getFirebaseAutenticacao();
        String email = getUsuarioAtual().getEmail();
        String identificadorUsuario = Base6.codificarBase64(email);

        return identificadorUsuario;

    }

    public static Empresa getDadosEmpresaLogada(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Empresa empresa = new Empresa();

        empresa.setId(firebaseUser.getUid());
        empresa.setEmail(firebaseUser.getEmail());
        empresa.setRazaoSocial(firebaseUser.getDisplayName());

        return empresa;
    }

    public static Empresa getDadosEmp(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Empresa e = new Empresa();

        e.setId(firebaseUser.getUid());
        e.setEmail(firebaseUser.getEmail());
        e.setRazaoSocial(firebaseUser.getDisplayName());

        return e;
    }



    public static boolean AtualizaNome(String nome){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome).build();
            user.updateProfile( profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful() ){
                        Log.d("Perfil", "Erro ao atualizar perfil");
                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }



    public static void  redirecionaUsuarioLogado(final Activity activity){

        FirebaseUser user = getUsuarioAtual();
        if(user != null){

            DatabaseReference usuariosRef = ConfiguracoesFirebase.getFirebaseDatabase()
                    .child("Empresa")
                    .child(getidusuario() );
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Cadastra_Usuario user = dataSnapshot.getValue(Cadastra_Usuario.class);
                    //  String tipoUsuario = user.getNome();

                    Intent i = new Intent(activity, PrincipalEmpresa.class);
                    activity.startActivity(i);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public static String getidusuario(){
        return getUsuarioAtual().getUid();
    }
}
