package com.rightside.tevinaredentor.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.rightside.tevinaredentor.R;
import com.rightside.tevinaredentor.activity.FiltroActivity;
import com.rightside.tevinaredentor.helper.Permissao;
import com.rightside.tevinaredentor.helper.ShowCamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {

    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;
    //android.hardware.Camera camera;
   // FrameLayout frameLayout;
    //ShowCamera showCamera;



    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);
        //frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);

        //camera = android.hardware.Camera.open();
        //showCamera = new ShowCamera(getActivity(), camera);
        //frameLayout.addView(showCamera);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1 );

        //Inicializar componentes
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);
        //buttontirar = view.findViewById(R.id.buttonFoto);



        //Adiciona evento de clique no botão da camera



         buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                   startActivityForResult(i, SELECAO_CAMERA );
                }
          }
            });

          // buttontirar.setOnClickListener(new View.OnClickListener() {
              // @Override
            //   public void onClick(View v) {
                // if (camera!= null) {
                      //  camera.takePicture(null,null, mPictureCallback);


             //      }
             //   }
          //  });


            //Adiciona evento de clique no botão da galeria
        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, SELECAO_GALERIA );
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == getActivity().RESULT_OK ){

            Bitmap imagem = null;

            try {

                //Valida tipo de seleção da imagem
                switch ( requestCode ){
                    case SELECAO_CAMERA :
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA :
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Valida imagem selecionada
                if( imagem != null ){

                    //Converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Envia imagem escolhida para aplicação de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("fotoEscolhida", dadosImagem );
                    startActivity( i );

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    //  Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
    //   @Override
    //  public void onPictureTaken(byte[] data, Camera camera) {

    //     File picture_file = getOutputMediaFile();

    //     if (picture_file == null)  {
    //        return;
    //     } else {

    //       try {
    //       FileOutputStream fos = new FileOutputStream(picture_file);
    //         fos.write(data);
    //         fos.close();
    //         camera.startPreview();
    //       }catch (FileNotFoundException e) {
    //           e.printStackTrace();
    //       } catch (IOException e ) {
    //          e.printStackTrace();
    //       }


    //    }
    //  }
    // };

    // private File getOutputMediaFile() {
    //   String state = Environment.getExternalStorageState();
    //   if(!state.equals(Environment.MEDIA_MOUNTED)) {
    //       return null;
    //    } else {
    //       File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
//
    //      if (!folder_gui.exists()) {
    //       folder_gui.mkdir();
    //    }
    //    File outputFile = new File(folder_gui, "temp.jpg");
    //      return outputFile;
    //  }

    //  }


}
