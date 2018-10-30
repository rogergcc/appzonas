package com.example.s3k_user1.appzonas;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class RechazarDocumentoActivity extends AppCompatActivity {
    TextView rechazo_doc_texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazar_documento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String intentDocumentoARechazar = getIntent().getExtras().getString("vNombreDocRechazar");

        rechazo_doc_texto = findViewById(R.id.rechazo_doc_texto);
        rechazo_doc_texto.setText(intentDocumentoARechazar);

        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rechazar Documento");
    }

}
