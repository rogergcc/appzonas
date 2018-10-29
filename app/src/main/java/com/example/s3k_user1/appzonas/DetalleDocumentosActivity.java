package com.example.s3k_user1.appzonas;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalleDocumentosActivity extends AppCompatActivity {

    TextView tituloDetalle;
    private RecyclerView recyclerView;
    private List<String> itemsList;
    private DetalleDocumentosActivity.StoreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documentos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getExtras().getString("vNombre"));
        setSupportActionBar(toolbar);

        String intentDetalleAnterior = getIntent().getExtras().getString("vNombre");

        //tituloDetalle = findViewById(R.id.detalle_documentos_titulo);

        recyclerView = findViewById(R.id.detalle_documento_recycler_view);
        itemsList = new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            itemsList.add(intentDetalleAnterior+" MEMONICO S0-"+i);
        }


        mAdapter = new StoreAdapter(this, itemsList);

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
        private Context context;
        private List<String> movieList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public CardView detalle_documentoCardView;
            public TextView name, price;
            public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);
                detalle_documentoCardView = (CardView) view.findViewById(R.id.detalle_documento_card_view);
                name = view.findViewById(R.id.detalle_documento_title);

            }
        }


        public StoreAdapter(Context context, List<String> movieList) {
            this.context = context;
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
            final List<String> movie = Collections.singletonList(movieList.get(position));
            holder.name.setText(movie.get(0));



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
