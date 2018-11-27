package com.example.s3k_user1.appzonas.Others;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.WebTokenActivity;
import com.example.s3k_user1.appzonas.R;

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
                uploadImage();
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
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
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
