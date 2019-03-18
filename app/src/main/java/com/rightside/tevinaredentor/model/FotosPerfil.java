package com.rightside.tevinaredentor.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;
import com.rightside.tevinaredentor.helper.UsuarioFirebase;

import java.util.HashMap;
import java.util.Map;

public class FotosPerfil {

    private String id;
    private String idUsuario;
    private String caminhoFoto;


    public FotosPerfil() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference fotosPerfilRef = firebaseRef.child("fotosExibirAmigos");
        String idFotosPerfil =  fotosPerfilRef.push().getKey();
        setId(idFotosPerfil);
    }


    public boolean salvar(DataSnapshot seguidoresSnapshot) {
        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        String combinacaoID = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/fotosExibirAmigos" + combinacaoID, this);

        firebaseRef.updateChildren(objeto);
        return true;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
