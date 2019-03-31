package enorsul.com.enorsul.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import enorsul.com.enorsul.EnorsigSQLiteHelper;
import enorsul.com.enorsul.R;
import enorsul.com.enorsul.camara.MyLocation;

public class ServiceSincronizar extends IntentService implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    SQLiteDatabase db;

    static final Double EARTH_RADIUS = 6371.00;

    String nroEmpleado, fechaHoy;
    private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;

    private Location mLastLocation;
    private LocationManager locationManager;
    protected LocationRequest mLocationRequest;

    private MyLocation myResult = new MyLocation();

    private String lblLatitud, lblLongitud;

    public ServiceSincronizar() {
        super("ServiceSincronizar");
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

    public Connection conexionBD() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://wrcenorsul.ddns.net;databaseName=RCLOCAL_PROD;user=assis;password=assis;");
            conexion = DriverManager.getConnection(getString(R.string.cadena_cnx_sql));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("SYNCLOG", "ServiceSincronizar_104_Destroy");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        apiClient.connect();


        try {

            lblLatitud = "";

            lblLongitud = "";

            EnorsigSQLiteHelper usdbh = new EnorsigSQLiteHelper(this, "DBEnorsig", null, 18);
            db = usdbh.getWritableDatabase();

            nroEmpleado =    enorsul.com.enorsul.Utils.leerValor(getApplicationContext(),"usuario");

            Calendar c = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(c.getTimeZone());
            fechaHoy = dateFormat.format(c.getTime());

            c = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("HH:mm");
            String hora = dateFormat.format(c.getTime());

            Log.d("SYNCLOG", "ServiceSincronizar_121_VerificaInternet");


            if (verificaConexao()) {

                String sql = "SELECT t1.VisCFum" +
                        "  ,t1.VisDVis" +
                        "  ,t1.rgiNrgi" +
                        "  ,t1.dcsNord" +
                        "  ,t1.rgiCsab" +
                        "  ,t1.estado" +
                        "  ,Nome" +
                        "  ,observacao" +
                        "  ,acao" +
                        "  ,leitura2" +
                        "  ,horavisita" +
                        "  ,geo" +
                        "  ,foto1_gxi" +
                        "  ,foto2_gxi" +
                        "  ,foto3_gxi" +
                        "  ,foto4_gxi" +
                        "  ,foto5_gxi" +
                        "  ,foto6_gxi" +
                        "  ,foto7_gxi" +
                        "  ,pariedade" +
                        "  ,ramal" +
                        "  ,IFNULL(onde,0) onde" +
                        "  ,assinatura_gxi" +
                        "  ,nome_cliente" +
                        "  ,nro_dereita" +
                        "  ,nro_esquerda" +
                        "  ,telefone" +
                        "  ,caract_imovel" +
                        "  FROM [enorsig_app] t1" +
                        "  where 1 = 1" +
                        "  AND estado = 1" +
                        "  AND pariedade = 7" +
                        "  AND t1.VisCFum = '" + nroEmpleado + "'" +
                        "  and VisDVis = '" + fechaHoy + "'";

                Cursor cursor = db.rawQuery(sql, null);

                if (cursor.moveToFirst()) {
                    do {

                        try {
                            PreparedStatement ps = conexionBD().prepareStatement(
                                    "UPDATE enorsig_app SET acao = ?, leitura2 = ?, observacao = ?, estado = ?, HoraVisita = ?, Geo = ?," +
                                            " Foto1_GXI = ?,  Foto2_GXI = ?, Foto3_GXI = ?, Foto4_GXI = ?, Foto5_GXI = ?, Foto6_GXI = ?, Foto7_GXI = ?," +
                                            " pariedade = ?, ramal = ?, onde = ?, assinatura_gxi = ?," +
                                            " nome_cliente = ?, nro_direita = ?, nro_esquerda = ?, telefone = ?, caract_imovel = ?" +
                                            " WHERE VisCFum = ? AND VisDVis = ? AND rgiNrgi = ? AND dcsNord = ?");

                            // set the preparedstatement parameters
                            ps.setInt(1, Integer.parseInt(cursor.getString(8)));
                            ps.setInt(2, Integer.parseInt(cursor.getString(9)));
                            ps.setString(3, cursor.getString(7));
                            ps.setInt(4, Integer.parseInt(cursor.getString(5)));
                            ps.setString(5, cursor.getString(10));
                            ps.setString(6, cursor.getString(11));

                            ps.setString(7, cursor.getString(12));
                            ps.setString(8, cursor.getString(13));
                            ps.setString(9, cursor.getString(14));
                            ps.setString(10, cursor.getString(15));
                            ps.setString(11, cursor.getString(16));
                            ps.setString(12, cursor.getString(17));
                            ps.setString(13, cursor.getString(18));
                            ps.setString(14, cursor.getString(19));
                            ps.setString(15, cursor.getString(20));
                            ps.setString(16, cursor.getString(21));
                            ps.setString(17, cursor.getString(22));

                            ps.setString(18, cursor.getString(23));
                            ps.setString(19, cursor.getString(24));
                            ps.setString(20, cursor.getString(25));
                            ps.setString(21, cursor.getString(26));
                            ps.setString(22, cursor.getString(27));

                            ps.setString(23, cursor.getString(0));
                            ps.setString(24, cursor.getString(1));
                            ps.setString(25, cursor.getString(2));
                            ps.setInt(26, Integer.parseInt(cursor.getString(3)));

                            // call executeUpdate to execute our sql update statement
                            ps.executeUpdate();
                            ps.close();

                        } catch (SQLException se) {
                          //  Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();
                            Log.d("TESTE1", se.getMessage());
                        }

                        db.execSQL("UPDATE enorsig_app" +
                                " SET pariedade = '8'" +
                                " WHERE viscfum = '"+nroEmpleado+"'" +
                                " and visdvis = '" + fechaHoy + "'" +
                                " and rginrgi = '"+cursor.getString(2)+"'" +
                                " and dcsnord = "+cursor.getString(3));

                    } while (cursor.moveToNext());

                    // Toast.makeText(getApplicationContext(), "Carga exitosa.", Toast.LENGTH_SHORT).show();

                    Log.d("TESTE1", "Sincronizado");

                }

                String sql_tab = "SELECT t1.Funcionario" +
                        "  ,t1.Data" +
                        "  ,t1.Hora" +
                        "  ,t1.x" +
                        "  ,t1.y" +
                        "  ,t1.Estado" +
                        "  ,t1.obs" +
                        "  FROM [TAB_Rastreamento] t1" +
                        "  where 1 = 1";

                Cursor cursor_tab = db.rawQuery(sql_tab, null);

                if (cursor_tab.moveToFirst()) {
                    do {

                        try {
                            PreparedStatement ps = conexionBD().prepareStatement(
                                    "INSERT INTO TAB_Rastreamento ()" +
                                            " VALUES (?, ?, ?, ?, ?, 1, null, ?)");

                            // set the preparedstatement parameters
                            ps.setString(1, cursor.getString(0));
                            ps.setString(2, cursor.getString(1));
                            ps.setString(3, cursor.getString(2));
                            ps.setString(4, cursor.getString(3));
                            ps.setString(5, cursor.getString(4));
                            ps.setString(6, cursor.getString(5));

                            // call executeUpdate to execute our sql update statement
                            ps.executeUpdate();
                            ps.close();

                        } catch (SQLException se) {
                            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } while (cursor.moveToNext());

                    Toast.makeText(getApplicationContext(), "Carga exitosa.", Toast.LENGTH_SHORT).show();

                }

                db.execSQL("DELETE FROM TAB_Rastreamento");



                Log.d("SYNCLOG", "ServiceSincronizar_211_ValidarGPS?");

                if (validar_gps()) {

                    Log.d("SYNCLOG", "ServiceSincronizar_217_ValidarGPS-ok");

                    PreparedStatement ps = conexionBD().prepareStatement(
                            "INSERT INTO TAB_Rastreamento (Funcionario, Data, Hora, x, y, Estado, obs) VALUES (?, ?, ?, ?, ?, 1,?)");

                    ps.setString(1, nroEmpleado);
                    ps.setString(2, fechaHoy);
                    ps.setString(3, hora);

                    Log.d("SYNCLOG", "ServiceSincronizar_217_ValidarGPS-ok-TyConverter");

                    ps.setFloat(4, Float.parseFloat(lblLatitud.toString()));
                    ps.setFloat(5, Float.parseFloat(lblLongitud.toString()));
                    ps.setString(6, "Ok - (" + lblLatitud.toString()  + "," +  lblLongitud.toString() + ")");



                    Log.d("SYNCLOG", "ServiceSincronizar_234_ValidarGPS-ok-TyConverter-ok");


                    ps.executeUpdate();
                    ps.close();


                    Log.d("SYNCLOG", "ServiceSincronizar_239_ValidarGPS-ok-SalvoSql");


                } else {

                    Log.d("SYNCLOG", "ServiceSincronizar_248_SemGPS");


                    PreparedStatement ps = conexionBD().prepareStatement(
                            "INSERT INTO TAB_Rastreamento (Funcionario, Data, Hora, x, y, Estado, obs) VALUES (?, ?, ?, ?, ?, 1,?)");

                    ps.setString(1, nroEmpleado);
                    ps.setString(2, fechaHoy);
                    ps.setString(3, hora);

                    ps.setFloat(4, Float.parseFloat("0.f"));
                    ps.setFloat(5, Float.parseFloat("0.f"));
                    ps.setString(6, "Erro_Sem_GPS");

                    ps.executeUpdate();
                    ps.close();


                    Log.d("SYNCLOG", "ServiceSincronizar_239_ValidarGPS-Erro-SalvoSql");


                }

            } else {

                Log.d("SYNCLOG", "ServiceSincronizar_248_SemInternet");

                String valorGPS;

                if (validar_gps()) {
                    valorGPS = "Ok - (" + lblLatitud.toString()  + "," +  lblLongitud.toString() + ")";
                } else {
                    valorGPS = "Erro_Sem_GPS";
                }


                String sql = "INSERT INTO TAB_Rastreamento (Funcionario, Data, Hora, x, y, Estado, obs) VALUES (?, ?, ?, ?, ?, 1,?)";

                android.database.sqlite.SQLiteStatement stmt = db.compileStatement(sql);

                stmt.bindString(1, nroEmpleado);
                stmt.bindString(2, fechaHoy);
                stmt.bindString(3, hora);

                stmt.bindDouble(4, Float.parseFloat("0.f"));
                stmt.bindDouble(5, Float.parseFloat("0.f"));
                stmt.bindString(6, valorGPS);

                stmt.execute();
                stmt.close();

            }

            Log.d("SYNCLOG", "ServiceSincronizar_252_SyncFinalizadoOK");


        } catch (Exception e) {

            Log.d("SYNCLOG", "ServiceSincronizar_252_Erro:_" + e.toString());


        }


    }


    public boolean validar_gps() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("SYNCLOG", "ServiceSincronizar_293_Validar_GPS_NaoAtivo");


        } else {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI(lastLocation);

        }

        if (lblLatitud.toString().equals("") && lblLongitud.toString().equals("")) {
            return false;
        } else {
            return true;
        }



    }

    private void updateUI(Location loc) {
        if (loc != null) {
            try {
                lblLatitud = String.valueOf(loc.getLatitude()).substring(0,10);
            } catch (Exception e) {
                //
            }
            try {
                lblLongitud = String.valueOf(loc.getLongitude()).substring(0,10);
            } catch (Exception e) {
                //
            }


        } else {
            lblLatitud = "";
            lblLongitud = "";
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        Log.d("SYNCLOG", "ServiceSincronizar_329_OnConnect");


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("SYNCLOG", "ServiceSincronizar_329_OnSupend");


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("SYNCLOG", "ServiceSincronizar_329_OnConnectFail");


    }


    public boolean verificaGPS(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

// Verifica se o GPS está ativo
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

// Caso não esteja ativo abre um novo diálogo com as configurações para
// realizar se ativamento
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        return enabled;
    }


    public void setCoordinates(){
        final ConnectivityManager[] cm = {(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)};
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity)this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        } else {
            client.requestLocationUpdates(request, new LocationCallback(){
                @Override
                public void onLocationResult(final LocationResult locationResult){
                    setMyLocation(locationResult.getLastLocation());

                }
            }, null);
        }


    }

    public void setMyLocation(Location l){
        myResult.setLat(formatCoordinate(l.getLatitude()));
        myResult.setLon(formatCoordinate(l.getLongitude()));
    }

    public String formatCoordinate(Double d){
        String result = "";
        result = String.format("%.5f", d);
        result = result.replace(".", "");
        result = result.replace(",", "");

        return result;
    }

    private class myLocationListener implements LocationListener {
        double lat_old = 0.0;
        double lon_old = 0.0;
        double lat_new;
        double lon_new;
        double time = 10;
        double speed = 0.0;

        @Override
        public void onLocationChanged(Location location) {
            Log.v("Debug", "in onLocation changed..");
            if (location != null) {
                //locManager.removeUpdates(locListener);
                //String Speed = "Device Speed: " +location.getSpeed();
                lat_new = location.getLongitude();
                lon_new = location.getLatitude();
                String longitude = "Longitude: " + location.getLongitude();
                String latitude = "Latitude: " + location.getLatitude();
                double distance = CalculationByDistance(lat_new, lon_new, lat_old, lon_old);
                speed = distance / time;
                Toast.makeText(getApplicationContext(), longitude + "\n" + latitude + "\nDistance is: "
                        + distance + "\nSpeed is: " + speed, Toast.LENGTH_SHORT).show();
                lat_old = lat_new;
                lon_old = lon_new;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {
        double Radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }



}
