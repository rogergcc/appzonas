package com.example.s3k_user1.appzonas.Others;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.DetalleDocumentosActivity;
import com.example.s3k_user1.appzonas.R;
import com.example.s3k_user1.appzonas.WebTokenActivity;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.snackbar.Snackbar;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener
//        , OnPageChangeListener, OnLoadCompleteListener,
//        OnPageErrorListener
{

    Button btnElegirImagenOPdf,btnSubirImagenOPdf;

    ImageView imgView;
    final int IMG_REQUEST =1;
    final int FILE_REQUEST =1;
    final int FILE_IMAGE_PDF_REQUEST=1;
    Bitmap bitmap;
    File myFileGlobal;

    AppCompatRadioButton rbImagen,rbPdf;
    private String pdfFileName;
    private int pageNumber = 0;

    private String IP_LEGAL = WebTokenActivity.IP_APK;

    private boolean respuestaRevisizarDocuento = false;
    private ProgressDialog progressDialog;
    View view;

    private boolean archivoUploadEsImagen=false;
    String fileStringImagenOrPdf="-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        btnElegirImagenOPdf = findViewById(R.id.btnElegirImagenOPdf);
        btnSubirImagenOPdf = findViewById(R.id.btnSubirImagenOPdf);

        imgView = findViewById(R.id.imagen);

        rbImagen = findViewById(R.id.rbImagen);
        rbPdf=findViewById(R.id.rbPdf);

        view = findViewById(R.id.view_uploadimage);
        rbImagen.setOnClickListener(this);
        rbPdf.setOnClickListener(this);
        btnSubirImagenOPdf.setOnClickListener(this);
        btnElegirImagenOPdf.setOnClickListener(this);


    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {

                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    } else {
                        result = uri.getLastPathSegment();
                    }
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
    public String checkImageOrPdfSelection(String image){

        return "SAF";
    }
//    private void displayFromFile(File file) {
//
//        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
//        Log.e("uri: ",file.getAbsolutePath());
//
//        pdfFileName = getFileName(uri);
//
//        Log.e("pdfFileName: ",pdfFileName);
//        pdfView.fromFile(file)
//                .defaultPage(pageNumber)
//                .onPageChange(this)
//                .enableAnnotationRendering(true)
//                .onLoad(this)
//                .scrollHandle(new DefaultScrollHandle(this))
//                .spacing(10) // in dp
//                .onPageError(this)
//                .load();
//    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,FILE_IMAGE_PDF_REQUEST);
    }
    private void selectFilePDF(){
        Intent intent = new Intent();

        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,FILE_IMAGE_PDF_REQUEST);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rbImagen) {
            btnElegirImagenOPdf.setText("Elegir Imagen");
            btnSubirImagenOPdf.setText("Subir Imagen");
        } else if (id == R.id.rbPdf) {
            btnElegirImagenOPdf.setText("Elegir PDF");
            btnSubirImagenOPdf.setText("Subir PDF");
        } else if (id == R.id.btnElegirImagenOPdf) {
            if (rbImagen.isChecked() || rbPdf.isChecked()) {

                if (rbImagen.isChecked()) {
                    Log.w("Seleccion", "imagenChecked");
                    selectImage();
                    archivoUploadEsImagen = true;
                }
                if (rbPdf.isChecked()) {
                    Log.w("Seleccion", "pdf checked");
                    selectFilePDF();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Seleccione Imagen o Pdf", Toast.LENGTH_SHORT).show();

            }
        } else if (id == R.id.btnSubirImagenOPdf) {
            if (fileStringImagenOrPdf.equals("-1")) {
                Log.e("SI", fileStringImagenOrPdf);
                Toast.makeText(getApplicationContext(), "Antes eliga un archivo", Toast.LENGTH_SHORT).show();
                return;
            } else {
                RevizarDocumentoJson(); //Web service revizar Correcto
            }
//                RevizarDocumentoPDFJson();
        }
    }
    private void RevizarDocumentoPDFJson(){
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
            js.put("fileImagenOrPdf",fileStringImagenOrPdf);
            js.put("nombre",nombre);
            js.put("esImagen",archivoUploadEsImagen);
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

                            boolean respuesta = jsonObject.getBoolean("respuestaConsulta");
                             mensaje = jsonObject.getString("mensaje");

                            respuestaRevisizarDocuento= respuesta;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        View v1 = getWindow().getDecorView().getRootView();

                        Toast.makeText(UploadImageActivity.this,mensaje , Toast.LENGTH_SHORT).show();
                        Snackbar.make(v1, mensaje, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        if(respuestaRevisizarDocuento){
                            finish();
                            startActivity(new Intent(getApplicationContext(), DetalleDocumentosActivity.class));
                        }

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


        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
    private String fileToString(File file) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

        //byte[] imgBytes = byteArrayOutputStream.toByteArray();

        InputStream is = null;

        try {
            is = new FileInputStream(file.getPath());

            is.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] fileBytes = new byte[0];
        try {
            fileBytes = IOUtils.toByteArray(is);
            Log.e("Datos PDF:", fileBytes.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error PDF:", "EEEE");
        }



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
        if (requestCode==FILE_IMAGE_PDF_REQUEST &&
               resultCode==RESULT_OK && data!=null ){

//            try {
//                String uriString = uri.toString();
//                File myFile = new File(getPDFPath(uri));
//                String path = myFile.getAbsolutePath();
//                String displayName = null;
//                Log.e("URI PDF: ",getPDFPath(uri));
//                //Log.e("uriString: ",data.toString());
//
//
//                displayName = myFile.getName();
//                Log.e("displayName: ",displayName);
//                Log.e("path: ",path);
//                //displayFromFile(myFile);
//
//                myFileGlobal = new File(getPDFPath(uri));
//            }catch (Exception e) {
//                Log.e("EXXcep", e.getMessage());
//                e.printStackTrace();
//            }

            Uri uri = data.getData();
            Log.e("FILE_IMAGE_PDF_REQUEST",FILE_IMAGE_PDF_REQUEST+"");
            Log.e("RESULT_OK",RESULT_OK+"");
            Log.e("data",data+"");

            if (rbPdf.isChecked()){
                String displayName = null;

                try {
                    File myFile = new File(getPDFPath(uri));
                    Log.e("INgreso","Ingreos pdf check resu");
                    Log.e("FILE PDF",fileToString(myFile));



                    String path = myFile.getAbsolutePath();

                    displayName = myFile.getName();
                    Log.e("displayName: ",displayName);
//                Log.e("path: ",path);
                    myFileGlobal = new File(getPDFPath(uri));
                    fileStringImagenOrPdf=fileToString(myFileGlobal);

                }catch (Exception e) {
                    Log.e("EXXcep paa", e.getMessage());
                    e.printStackTrace();
                }
            }

            if (rbImagen.isChecked()){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                } catch (IOException e) {
                    Log.e("Excep", e.getMessage());
                    e.printStackTrace();
                }

                Log.e("FILE IMG",imageToString(bitmap));
                //MediaStore.Files.FileColumns.MEDIA_TYPE(p)
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                fileStringImagenOrPdf=imageToString(bitmap);

            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void loadComplete(int nbPages) {
//        PdfDocument.Meta meta = pdfView.getDocumentMeta();
//
//        printBookmarksTree(pdfView.getTableOfContents(), "-");
//    }
//
//    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
//        for (PdfDocument.Bookmark b : tree) {
//
//            //Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
//
//            if (b.hasChildren()) {
//                printBookmarksTree(b.getChildren(), sep + "-");
//            }
//        }
//    }
//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        pageNumber = page;
//        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
//    }
//
//    @Override
//    public void onPageError(int page, Throwable t) {
//
//    }
}
