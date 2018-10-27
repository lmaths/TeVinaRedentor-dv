package com.rightside.tevinaredentor.model;

import com.google.firebase.database.DatabaseReference;
import com.rightside.tevinaredentor.helper.ConfiguracaoFirebase;

import java.util.HashMap;

public class Indicador {
    public Comentario coment;
    public Usuario usuario;
    public int qtdIndicação = 0;

    public Indicador() {
    }

    public void salvarindicador() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //objeto usuario

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("nomeUsuario", usuario.getNome());
        dados.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pindicadorRef = firebaseRef.child("Indicadores").child(coment.getIdComentario()).child(usuario.getId());
        pindicadorRef.setValue(dados);

        //atualiza quantidade de indicadores

        atualizarQtdindicador(1);
    }

        public void atualizarQtdindicador(int valor) {

            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

            DatabaseReference pindicadorRef = firebaseRef.child("Indicadores").child(coment.getIdComentario()).child("qtdindicacao");
            setQtdIndicação ( getQtdIndicação()+ valor);
            pindicadorRef.setValue(getQtdIndicação());
            }

            public void remover() {
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
                DatabaseReference pindicadorRef = firebaseRef.child("Indicadores").child(coment.getIdComentario()).child(usuario.getId());
                pindicadorRef.removeValue();

                //atualizar a quantidade
                atualizarQtdindicador(-1);
            }

    public Comentario getComent() {
        return coment;
    }

    public void setComent(Comentario coment) {
        this.coment = coment;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getQtdIndicação() {
        return qtdIndicação;
    }

    public void setQtdIndicação(int qtdIndicação) {
        this.qtdIndicação = qtdIndicação;
    }
}

