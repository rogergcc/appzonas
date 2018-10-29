package com.example.s3k_user1.appzonas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DetalleDocumentosActivity extends AppCompatActivity {

    TextView tituloDetalle;
    private RecyclerView recyclerView;
    private List<Documento> documentoList;
    private DetalleDocumentosActivity.StoreAdapter mAdapter;
    private View vista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documentos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getExtras().getString("vNombre"));
        setSupportActionBar(toolbar);

        String intentDetalleAnterior = getIntent().getExtras().getString("vNombre");

        vista= findViewById(R.id.act_det_document);


        //tituloDetalle = findViewById(R.id.detalle_documentos_titulo);

        recyclerView = findViewById(R.id.detalle_documento_recycler_view);
        documentoList = new ArrayList<>();
        final Date date = new Date(); // your date
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.format(date);

        for (int i = 0; i <20 ; i++) {
            Documento documentoNew = new Documento();
            documentoNew.setNombre("MEMONICO S0-"+i +" "+ intentDetalleAnterior);

            documentoNew.setFecha(sdf.format(date));
            documentoList.add(documentoNew);
        }


        mAdapter = new StoreAdapter(this, documentoList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class StoreAdapter extends RecyclerView.Adapter<DetalleDocumentosActivity.StoreAdapter.MyViewHolder> {
        Context contextDetalleDocumento;
        private List<Documento> movieList;
        Dialog myDialog;

        Button botonAprobar, botonCancelar;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public CardView detalle_documentoCardView;
            public TextView name, fecha;
            public ImageView thumbnail;
            public LinearLayout det_doc_item_layout;


            public MyViewHolder(View view) {
                super(view);
                det_doc_item_layout = findViewById(R.id.det_doc_item_layout);
                detalle_documentoCardView = (CardView) view.findViewById(R.id.detalle_documento_card_view);
                name = view.findViewById(R.id.detalle_documento_title);
                fecha = view.findViewById(R.id.det_doc_fecha);
            }
        }


        public StoreAdapter(Context context, List<Documento> movieList) {
            this.contextDetalleDocumento = context;
            this.movieList = movieList;
        }

        @Override
        public DetalleDocumentosActivity.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detalle_documentos_item, parent, false);

            return new DetalleDocumentosActivity.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DetalleDocumentosActivity.StoreAdapter.MyViewHolder holder, final int position) {
            final Documento documentoViewHolder = documentoList.get(position);
            holder.name.setText(documentoViewHolder.getNombre());
            holder.fecha.setText(documentoViewHolder.getFecha());
            //TODO inicializar Dialog
            myDialog = new Dialog(contextDetalleDocumento);
            myDialog.setContentView(R.layout.act_det_doc_dialog_doc);

            holder.detalle_documentoCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView documento =  myDialog.findViewById(R.id.det_doc_tit_doc);
                    TextView memonico =  myDialog.findViewById(R.id.det_doc_tit_subt);
                    TextView fecha =  myDialog.findViewById(R.id.det_doc_fecha);
                    documento.setText(documentoViewHolder.getNombre());
                    memonico.setText(documentoViewHolder.getNombre());
                    fecha.setText(documentoViewHolder.getFecha());
                    botonAprobar = myDialog.findViewById(R.id.btnAprobar);
                    botonCancelar = myDialog.findViewById(R.id.btnCancelar);
                    myDialog.show();

                    Bundle bundle = new Bundle();
                    bundle.putString("vNombreDocRechazar", documento.getText().toString());
                    botonCancelar.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent newDDocumentsActivity = new Intent(v.getContext(), RechazarDocumentoActivity.class);
                            //newDDocumentsActivity.putExtras(bundle);
                            v.getContext().startActivity(newDDocumentsActivity);
                        }
                    });
                    botonAprobar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            /*AlertDialog.Builder builder = new AlertDialog.Builder(contextDetalleDocumento);
                            builder.setTitle("Confirmar");
                            builder.setMessage("Seguro que desea aprobar documento");

                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myDialog.hide();
                                    Snackbar.make(vista, "Documento Aprobado", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();*/
                            myDialog.hide();
                            Snackbar.make(vista, "Documento Aprobado", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            });
            //Glide.with(context).load(movie.get(0));

            /*
            * final Movie movie = movieList.get(position);
            holder.name.setText(movie.getTitle());
            holder.price.setText(movie.getPrice());

            Glide.with(context)
                    .load(movie.getImage())
                    .into(holder.thumbnail);
            *
            * */
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }

}
