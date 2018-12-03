package com.example.s3k_user1.appzonas.Others;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.WebTokenActivity;
import com.example.s3k_user1.appzonas.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnElegirImagen,btnSubirImagen;
    EditText edtNombreImagen;
    ImageView imgView;
     final int IMG_REQUEST =1;
     Bitmap bitmap;
    private String IP_LEGAL = WebTokenActivity.IP_APK;

    private String respuestaRevisizarDocuento = "";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        btnElegirImagen = findViewById(R.id.btnElegirImagen);
        btnSubirImagen = findViewById(R.id.btnSubirImagen);
        edtNombreImagen = findViewById(R.id.edtNombreImagen);
        imgView = findViewById(R.id.imagen);

        btnSubirImagen.setOnClickListener(this);
        btnElegirImagen.setOnClickListener(this);

    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnElegirImagen:
                selectImage();
                break;
            case R.id.btnSubirImagen:
                RevizarDocumentoJson();
                break;
        }
    }
    private void uploadImage(){
        //Direccion.replaceAll(" ", "");
        String url = IP_LEGAL + "/legal/Documento/DocumentoGuardarImagenDocumentoAprobadoJson";
        //
        JSONObject js = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            js.put("nombre",edtNombreImagen.getText().toString().trim());
            js.put("imagen",imageToString(bitmap));
            //js.put("documentoId","A");


        }catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("TAG PUES", response.toString());
                        String mensaje="";
                        try {
                            mensaje = response.getString("mensaje");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(UploadImageActivity.this,mensaje , Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response.Volley", error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/x-www-form-urlencoded");
                //params.put("nombre",edtNombreImagen);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }
    public void RevizarDocumentoJson() {
        //RevizarDocumentoJson(int usuarioId, int documentoId, string esRechazado, string observacion, string perfil)
        String url = IP_LEGAL+"/legal/RevisionDocumento/RevizarDocumentoJson";
        final int usuarioId = getIntent().getExtras().getInt("vusuarioId");
        final int documentoId = getIntent().getExtras().getInt("vdocumentoId");
        final String esRechazado = getIntent().getExtras().getString("vesRechazado");
        final String observacion = getIntent().getExtras().getString("vobservacion");
        final String perfil = getIntent().getExtras().getString("vperfil");
        final String nombre = getIntent().getExtras().getString("vnombre");
        JSONObject js = new JSONObject();
        try {
            JSONObject params = new JSONObject();


            js.put("usuarioId",usuarioId);
            js.put("documentoId",documentoId);
            js.put("esRechazado",esRechazado);
            js.put("observacion",observacion);
            js.put("perfil",perfil);
            js.put("imagen",imageToString(bitmap));
            js.put("nombre",nombre);



        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,url, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("MENSAJE VALIDA: ", jsonObject.toString());
                        String mensaje="";
                        try {
                            //JSONObject objectUser = jsonObject.getJSONObject("usuario");

                            String respuesta = jsonObject.getString("mensaje");
                             mensaje = jsonObject.getString("mensaje");

                            respuestaRevisizarDocuento= respuesta;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(UploadImageActivity.this,mensaje , Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        })
        {@Override
        public Request.Priority getPriority() {
            return Priority.NORMAL;
        };
        };



        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);

    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==IMG_REQUEST &&
               resultCode==RESULT_OK && data!=null ){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                edtNombreImagen.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
