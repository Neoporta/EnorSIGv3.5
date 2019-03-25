package enorsul.com.enorsul;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import enorsul.com.enorsul.activities.MessagesActivity;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsuario, txtPassword;
    Button btnEnviar;
    String nroEmpledo;
    String clave;
    String fechaHoy;
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("SYNCLOG", "LoginActivity_45_EntrarDispararAlarme");

        disparaAlarme();
        //launchApp("com.resilio.sync");


        EnorsigSQLiteHelper usdbh = new EnorsigSQLiteHelper(this, "DBEnorsig", null, 19);
        db = usdbh.getWritableDatabase();

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnEnviar = findViewById(R.id.btnEnviar);

        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(c.getTimeZone());
        fechaHoy = dateFormat.format(c.getTime());

        //fechaHoy = "2019-01-20";

        if (!(enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "usuario").equals("")) && (enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "clave").equals("5581")) && BuildConfig.VERSION_CODE >= 12 ) {
            if (verificaConexao()) {
                obtenerEventosBD();
                obtenerOrdenesBD(enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "usuario"), fechaHoy);

            }

            //NewsApp.getInstance().getService().setAllOrdenes(obtenerOrdenesBD(enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "usuario"), fechaHoy));
            //Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
            Intent intent = null;
            if (verificar_mensaje(fechaHoy, enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "usuario"))) {
                intent = new Intent(getApplicationContext(), PrincipalActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), MessagesActivity.class);
            }

            intent.putExtra("nroEmpleado", enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "usuario"));

            PrincipalActivity.usuario_logado = nroEmpledo;

            intent.putExtra("fechaHoy", fechaHoy);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

            finish();

        } else {

            if (BuildConfig.VERSION_CODE <= 11){
                Toast.makeText(getApplicationContext(), "Versao Desatualizada, Favor Procurar o Assis ou Pietro.", Toast.LENGTH_SHORT).show();

            }

            if (!(enorsul.com.enorsul.Utils.leerValor(getApplicationContext(), "clave").equals("5581"))){
                Toast.makeText(getApplicationContext(), "Sem Chave de Seguranca, Favor Procurar o Assis ou Pietro.", Toast.LENGTH_SHORT).show();

            }


        }


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nroEmpledo = txtUsuario.getText().toString();
                clave = txtPassword.getText().toString();

                if ((clave.equals("1112223334449"))){

                    try {
                        db.execSQL("DELETE FROM enorsig_app");
                        Toast.makeText(getApplicationContext(), "apagando banco de dados.", Toast.LENGTH_SHORT).show();
                    } catch (android.database.SQLException e) {
                        Toast.makeText(getApplicationContext(), "eRRO AO APAGAR." + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                if (!(clave.equals("5581"))){
                    Toast.makeText(getApplicationContext(), "Favor Procurar o Assis ou Pietro.", Toast.LENGTH_SHORT).show();
                    return;
                }




                if (verificaConexao())  {
                    try {

                        Statement st = conexionBD().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        String sql = "Select top 1 * from ctaUsu Where UsuCUsu = '" + nroEmpledo + "'";
                        ResultSet rs = st.executeQuery(sql);

                        if (rs.next()) {
                            rs.beforeFirst();
                            while (rs.next()) {
                                Toast.makeText(getApplicationContext(), "Bienvenido: " + rs.getString("UsuMUsu"), Toast.LENGTH_SHORT).show();

                                enorsul.com.enorsul.Utils.guardarValor(getApplicationContext(), "usuario", nroEmpledo);
                                enorsul.com.enorsul.Utils.guardarValor(getApplicationContext(), "clave", clave);

                                if (verificaConexao()) {
                                    obtenerEventosBD();
                                    obtenerOrdenesBD(nroEmpledo, fechaHoy);
                                }

                                //NewsApp.getInstance().getService().setAllOrdenes(obtenerOrdenesBD(nroEmpledo, fechaHoy));
                                //Intent intent = new Intent(v.getContext(), PrincipalActivity.class);
                                //Intent intent = new Intent(v.getContext(), MessagesActivity.class);
                                Intent intent = null;
                                if (verificar_mensaje(fechaHoy, nroEmpledo)) {
                                    intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                                } else {
                                    intent = new Intent(getApplicationContext(), MessagesActivity.class);
                                }
                                intent.putExtra("nroEmpleado", nroEmpledo);
                                intent.putExtra("fechaHoy", fechaHoy);
                                PrincipalActivity.usuario_logado = nroEmpledo;

                                v.getContext().startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Favor Procurar o Assis Ou Pietro", Toast.LENGTH_SHORT).show();
                        }
                        rs.close();

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (enorsul.com.enorsul.Utils.leerValor(getApplicationContext(),"usuario").equals(nroEmpledo) &&
                            enorsul.com.enorsul.Utils.leerValor(getApplicationContext(),"clave").equals("5581")) {
                        //Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                        //Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                        Intent intent = null;
                        if (verificar_mensaje(fechaHoy, nroEmpledo)) {
                            intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), MessagesActivity.class);
                        }
                        intent.putExtra("nroEmpleado", nroEmpledo);
                        intent.putExtra("fechaHoy", fechaHoy);
                        getApplicationContext().startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Favor Procurar o Assis ou Pietro", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    //PASO 1: Carga de ordenes al SQLite
    public void obtenerOrdenesBD(String nroEmpleado, String fecha) {

        try {
            Statement st = conexionBD().createStatement();

            String sql = "SELECT rtrim(t1.VisCFum) VisCFum" +
                    "  ,t1.VisDVis" +
                    "  ,t1.rgiNrgi" +
                    "  ,t1.dcsNord" +
                    "  ,t1.rgiCsab" +
                    "  ,ISNULL(estado, 0) estado" +
                    "  ,Nome" +
                    "  ,(rtrim(t1.rgiKend) + ' ' + cast(t1.rgiNend as varchar) + ' ' + rtrim(t1.rgiBai) + ' ' + xcep) obs" +
                    "  ,'2' pariedade" +
                    "  ,acao" +
                    "  ,NroOrden" +
                    "  FROM [enorsig_app] t1" +
                    "  where 1 = 1" +
                    //"  AND ISNULL(pariedade, 1) = 1" +
                    "  AND t1.VisCFum = '"+nroEmpleado+"'" +
                    "  and VisDVis = '" +fecha+ "'";

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                sql = "INSERT INTO enorsig_app (rgiNrgi, dcsNord, rgiCsab, obs, estado, nome, VisCFum, VisDVis, acao, nro_orden, pariedade)" +
                        " VALUES ('"+rs.getString("rgiNrgi")+"'," +
                        "           '"+rs.getString("DcsNord")+"'," +
                        "           '"+rs.getString("rgiCsab")+"'," +
                        "           '"+rs.getString("obs")+"'," +
                        "           '"+rs.getString("estado")+"'," +
                        "           '"+rs.getString("nome")+"'," +
                        "           '"+nroEmpleado+"'," +
                        "           '"+fecha+"'," +
                        "           '"+rs.getString("acao")+"'," +
                        "           '"+rs.getString("NroOrden")+"'," +
                        "           '"+rs.getString("pariedade")+"')";

                db.execSQL(sql);

            }

            //PASO 2: Cambio a Pariedade 2
            sql = "UPDATE enorsig_app" +
                    " SET pariedade = '2'" +
                    " WHERE VisCFum = '"+nroEmpleado+"'" +
                    " AND VisDVis = '"+fecha+"'" +
                    " AND ISNULL(pariedade, 1) = 1" +
                    " AND ISNULL(estado, '0') = '0'";

            PreparedStatement ps = conexionBD().prepareStatement(sql);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (SQLiteException le) {
            System.out.println(le.getStackTrace());
        }
    }

    public void obtenerEventosBD() {

        try {
            Statement st = conexionBD().createStatement();

            String sql = "SELECT t1.cod_eve" +
                    "  ,t1.des_eve" +
                    "  ,t1.show_eve" +
                    "  FROM [tab_eventos] t1";

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                sql = "INSERT INTO tab_eventos (cod_eve, des_eve, show_eve)" +
                        " VALUES ('"+rs.getString("cod_eve")+"'," +
                        "           '"+rs.getString("des_eve")+"'," +
                        "           '"+rs.getString("show_eve")+"')";

                try {
                    db.execSQL(sql);
                } catch (android.database.SQLException e) {
                    //e.getMessage();
                }

            }

        } catch (SQLException e) {
            Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SYNCLOG", "LoginActivity_disparaAlarme_268_Desdroy");
    }

    private void disparaAlarme(){

//        boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0, new Intent("EXECUTA"), PendingIntent.FLAG_NO_CREATE) == null);

        boolean alarmeAtivo = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("EXECUTA"), PendingIntent.FLAG_NO_CREATE) == null);


        Log.d("SYNCLOG", "LoginActivity_disparaAlarme_266_ConferirSeAtivo");

        if(alarmeAtivo){

            Log.d("SYNCLOG", "LoginActivity_disparaAlarme_274_alarmeNovoAlarme");
            Log.i("Script", "Novo alarme");

            Intent intent = new Intent();
            intent.setAction("EXECUTA");
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.SECOND, 3);

            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 60000, p);

        }
        else{

            Log.d("SYNCLOG", "LoginActivity_disparaAlarme_274_alarmeJaExiste");

        }

    }

    public void agendaAbertura() {
        Intent it = new Intent("EXECUTA");
        PendingIntent p = PendingIntent.getBroadcast(LoginActivity.this, 0, it, 0);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 100);
        long tempoReabrir = c.getTimeInMillis();
        AlarmManager reabrir = (AlarmManager) getSystemService(ALARM_SERVICE);
        reabrir.set(AlarmManager.RTC_WAKEUP, tempoReabrir, p);

    }

    public void launchApp(String packageName) {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }

    public boolean verificar_mensaje(String fecha, String nroEmpleado) {

        ResultSet rs = null;
        boolean retorno = false;
        try {
            Statement st = conexionBD().createStatement();

            /*String sql = "SELECT ISNULL(MAX(1), 0) campo1" +
                    "  FROM [TAB_MSG_CONTROL]" +
                    "  where 1=1" +
                    "  and [msg_accion] = 'ACEITOU'" +
                    "  and Cast([msg_data] AS date) = '"+fecha+"'" +
                    "  and [viscfum] = '"+nroEmpleado+"'" ;*/

            String sql = "SELECT case when ISNULL(MAX(1), 0) = 0 then (select ISNULL(MAX(0), 2) " +
                    "from tab_msg_alertas " +
                    "where 1=1 " +
                    "and Cast([msg_data] AS date) = '"+fecha+"') else 1 end campo1 " +
                    "FROM [TAB_MSG_CONTROL] " +
                    "where 1=1  and [msg_accion] = 'ACEITOU'" +
                    "and Cast([msg_data] AS date) = '"+fecha+"' " +
                    "and [viscfum] = '"+nroEmpleado+"'";

            System.out.println("SQL CONTROL: " + sql);

            rs = st.executeQuery(sql);

            while (rs.next()) {

                if (rs.getString("campo1").equals("0")) {
                    retorno = false;
                } else if (rs.getString("campo1").equals("0")) {
                    retorno = false;
                } else {
                    retorno = true;
                }

            }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retorno;

    }


}
