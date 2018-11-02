package enorsul.com.enorsul.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import enorsul.com.enorsul.PrincipalActivity;
import enorsul.com.enorsul.R;
import enorsul.com.enorsul.adapters.MessageAdapter;
import enorsul.com.enorsul.models.MessageModelo;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView messagesRecyclerView;
    MessageAdapter adaptadorMensaje;
    List<MessageModelo> mensajes;
    GridLayoutManager messagesLayoutManager;
    String nroEmpleado, fechaHoy;
    String fechaMensaje;

    Button btnAceptar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        nroEmpleado = getIntent().getStringExtra("nroEmpleado");
        fechaHoy = getIntent().getStringExtra("fechaHoy");

        btnAceptar = findViewById(R.id.btnAceptar);
        btnCancelar = findViewById(R.id.btnCancelar);

        setTitle("Mensajes" );

        mensajes = new ArrayList<>();
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);;
        adaptadorMensaje = new MessageAdapter();
        messagesLayoutManager = new GridLayoutManager(this,1);
        adaptadorMensaje.setMensajes(mensajes);
        messagesRecyclerView.setAdapter(adaptadorMensaje);
        messagesRecyclerView.setLayoutManager(messagesLayoutManager);

        mensajes = obtenerOrdenesBD(fechaHoy);

        adaptadorMensaje.setMensajes(mensajes);
        adaptadorMensaje.notifyDataSetChanged();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFormat.setTimeZone(c.getTimeZone());
                String fechaMomento = dateFormat.format(c.getTime());

                String sql = "INSERT INTO [TAB_MSG_CONTROL] ([msg_data]" +
                        "      ,[viscfum]" +
                        "      ,[msg_registro]" +
                        "      ,[msg_accion])" +
                        " VALUES (convert(varchar,'"+fechaMensaje+"',120), '"+nroEmpleado+"', convert(varchar,'"+fechaMomento+"',120), 'ACEITOU')";

                PreparedStatement ps = null;
                try {
                    ps = conexionBD().prepareStatement(sql);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                intent.putExtra("nroEmpleado", nroEmpleado);
                intent.putExtra("fechaHoy", fechaHoy);
                getApplicationContext().startActivity(intent);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                dateFormat.setTimeZone(c.getTimeZone());
                String fechaMomento = dateFormat.format(c.getTime());

                String sql = "INSERT INTO [TAB_MSG_CONTROL] ([msg_data]" +
                        "      ,[viscfum]" +
                        "      ,[msg_registro]" +
                        "      ,[msg_accion])" +
                        " VALUES (convert(varchar,'"+fechaMensaje+"',120), '"+nroEmpleado+"', convert(varchar,'"+fechaMomento+"',120), 'REJEITOU')";

                System.out.println(sql);

                PreparedStatement ps = null;
                try {
                    ps = conexionBD().prepareStatement(sql);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public List<MessageModelo> obtenerOrdenesBD(String fecha) {

        List<MessageModelo> msgs = new ArrayList<>();

        try {
            Statement st = conexionBD().createStatement();

            String sql = "SELECT [msg_data]" +
                    "      ,[msg_txt]" +
                    "      ,[msg_reg]" +
                    "  FROM [TAB_MSG_ALERTAS]" +
                    "  where Cast(msg_data AS date) = '"+fecha+"'" ;

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                fechaMensaje = rs.getString("msg_data");
                msgs.add(new MessageModelo(
                        rs.getString("msg_data"),
                        rs.getString("msg_txt"),
                        rs.getString("msg_reg")));

            }

            st.close();

        } catch (SQLException e) {
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return msgs;
    }

    public Connection conexionBD() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://wrcenorsul.ddns.net;databaseName=RCLOCAL_PROD;user=assis;password=assis;");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }

}
