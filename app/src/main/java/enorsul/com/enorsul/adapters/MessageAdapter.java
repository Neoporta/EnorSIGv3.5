package enorsul.com.enorsul.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import enorsul.com.enorsul.R;
import enorsul.com.enorsul.models.MessageModelo;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<MessageModelo> mensajes;

    public void setMensajes(List<MessageModelo> mensajes) { this.mensajes = mensajes; }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_message, parent, false);
        MessageAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        holder.lblFecha.setText(mensajes.get(position).getFecha());
        holder.lblMensaje.setText(mensajes.get(position).getMensaje());

    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblFecha;
        TextView lblMensaje;

        public ViewHolder(View itemView) {
            super(itemView);
            lblFecha = itemView.findViewById(R.id.lblFecha);
            lblMensaje = itemView.findViewById(R.id.lblMensaje);
        }
    }
}
