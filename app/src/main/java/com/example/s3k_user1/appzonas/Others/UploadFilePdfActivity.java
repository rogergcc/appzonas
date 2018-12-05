package com.example.s3k_user1.appzonas.Others;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.common.util.IOUtils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFilePdfActivity extends AppCompatActivity implements View.OnClickListener, OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    Button btnElegirImagen,btnSubirImagen;
    EditText edtNombreImagen;
    ImageView imgView;
    final int IMG_REQUEST =1;
    final int FILE_REQUEST =1;
    Bitmap bitmap;
    File myFileGlobal;

    private PDFView pdfView;
    private String pdfFileName;
    private int pageNumber = 0;

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

        pdfView = findViewById(R.id.pdfView);

        btnSubirImagen.setOnClickListener(this);
        btnElegirImagen.setOnClickListener(this);

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void displayFromFile(File file) {

        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        Log.e("uri: ",file.getAbsolutePath());

        pdfFileName = getFileName(uri);

        Log.e("pdfFileName: ",pdfFileName);
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }
    private void selectFilePDF(){
        Intent intent = new Intent();


        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,FILE_REQUEST);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnElegirImagen:
//                selectFilePDF();
                selectImage();
                break;
            case R.id.btnSubirImagen:
//                try {
//                    RevizarDocumentoPDFJson();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                RevizarDocumentoJson(); //Web service revizar Correcto
                break;
        }
    }
    private void RevizarDocumentoPDFJson() throws IOException {
        //Direccion.replaceAll(" ", "");
        String url = IP_LEGAL + "/legal/RevisionDocumento/RevizarDocumentoPDFJson";
        //
        final String nombre = getIntent().getExtras().getString("vnombre");
        JSONObject js = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            js.put("usuarioId",4);

            js.put("filepdf",fileToString(myFileGlobal));
            js.put("nombre",nombre);
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
                        Toast.makeText(UploadFilePdfActivity.this,mensaje , Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UploadFilePdfActivity.this,mensaje , Toast.LENGTH_SHORT).show();
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

            Log.e("Datos:", js.toString());

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
                        Toast.makeText(UploadFilePdfActivity.this,mensaje , Toast.LENGTH_SHORT).show();

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
        //Log.e("Datos imgBytes:", imgBytes.toString());
        for (byte dato :
                imgBytes) {

        }
        for (int i = 0 ; i< imgBytes.length;i++){
            Log.e("imgBytes:", "["+i+"]"+imgBytes[i]);
        }
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
    private String fileToString(File file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

        //byte[] imgBytes = byteArrayOutputStream.toByteArray();

        byte[] fileBytes = IOUtils.toByteArray(file);

        return Base64.encodeToString(fileBytes,Base64.DEFAULT);
    }
    public String getPDFPath(Uri uri){

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==IMG_REQUEST &&
                resultCode==RESULT_OK && data!=null ){

//            Uri uri = data.getData();
//
//            String uriString = uri.toString();
//            File myFile = new File(getPDFPath(uri));
//            String path = myFile.getAbsolutePath();
//            String displayName = null;
//            Log.e("URI PDF: ",getPDFPath(uri));
//            //Log.e("uriString: ",data.toString());
//
//
//            displayName = myFile.getName();
//            Log.e("displayName: ",displayName);
//            Log.e("path: ",path);
//            //displayFromFile(myFile);
//
//            myFileGlobal = new File(getPDFPath(uri));



            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);

                Log.e("FILE STR",imageToString(bitmap));
                //MediaStore.Files.FileColumns.MEDIA_TYPE(p)
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                edtNombreImagen.setVisibility(View.VISIBLE);

                //IOUtils.toByteArray(myFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }
}
