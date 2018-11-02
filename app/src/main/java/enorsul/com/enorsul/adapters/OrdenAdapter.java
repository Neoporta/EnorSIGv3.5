package enorsul.com.enorsul.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import enorsul.com.enorsul.NewsApp;
import enorsul.com.enorsul.OrdenDetailActivity;
import enorsul.com.enorsul.R;
import enorsul.com.enorsul.models.OrdenModelo;

public class OrdenAdapter extends RecyclerView.Adapter<OrdenAdapter.ViewHolder> {

    List<OrdenModelo> ordenes;

    public void setOrdenes(List<OrdenModelo> ordenes) { this.ordenes = ordenes; }

    @NonNull
    @Override
    public OrdenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_orden, parent, false);
        OrdenAdapter.ViewHolder viewHolder = new OrdenAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdenAdapter.ViewHolder holder, final int position) {
        holder.lblRGI.setText(ordenes.get(position).getRGI());
        holder.lblNroOrden.setText(ordenes.get(position).getNroOrden());
        holder.lblSABESP.setText(ordenes.get(position).getCodeSABESP());
        holder.lblObs.setText(ordenes.get(position).getObservaciones());
        holder.lblNombre.setText(ordenes.get(position).getNombre());
        holder.lblOrden.setText(ordenes.get(position).getOrden());

        if (ordenes.get(position).getEstado().equals("0")) {
            holder.ordenCardView.setCardBackgroundColor(Color.parseColor("#FFC9CCF1"));
        } else {
            holder.ordenCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.ordenCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ordenes.get(position).getEstado().equals("0")) {
                NewsApp.getInstance().getService().setOrdenModelo(ordenes.get(position));
                Intent intent = new Intent(v.getContext(), OrdenDetailActivity.class);
                v.getContext().startActivity(intent);
            }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordenes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView ordenCardView;
        TextView lblRGI;
        TextView lblNroOrden;
        TextView lblSABESP;
        TextView lblObs;
        TextView lblNombre;
        TextView lblOrden;
        public ViewHolder(View itemView) {
            super(itemView);
            ordenCardView = itemView.findViewById(R.id.ordenCardView);
            lblRGI = itemView.findViewById(R.id.lblRGI);
            lblNroOrden = itemView.findViewById(R.id.lblNroOrden);
            lblSABESP = itemView.findViewById(R.id.lblSABESP);
            lblObs = itemView.findViewById(R.id.lblObs);
            lblNombre = itemView.findViewById(R.id.lblNombre);
            lblOrden = itemView.findViewById(R.id.lblOrden);
        }
    }

}
