package enorsul.com.enorsul;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import enorsul.com.enorsul.activities.MapActivity;
import enorsul.com.enorsul.activities.PersonDetailActivity;
import enorsul.com.enorsul.activities.PhotosActivity;

public class PrincipalActivity extends AppCompatActivity {

    ViewPager mViewPager;
    public String nroEmpleado, fechaHoy;
    private String appVersion = BuildConfig.VERSION_NAME;

    private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    public static String usuario_logado = null;
    private DrawerLayout drawerLayout;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        setToolbar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        }

        EnorsigSQLiteHelper usdbh = new EnorsigSQLiteHelper(this, "DBEnorsig", null, 19);
        db = usdbh.getWritableDatabase();

        nroEmpleado = getIntent().getStringExtra("nroEmpleado");
        fechaHoy = getIntent().getStringExtra("fechaHoy");

        setTitle("OS " + fechaHoy + " [" + nroEmpleado + "]-" + appVersion.toString() + "" );

        // Setear adaptador al viewpager.
        mViewPager = findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        setupViewPager(mViewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);



    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle bundle = new Bundle();
        bundle.putString("nroEmpleado", nroEmpleado);
        bundle.putString("fechaHoy", fechaHoy);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        OrdenesFaltantesFragment nf = new OrdenesFaltantesFragment();
        nf.setArguments(bundle);

        OrdenesPendientesFragment np = new OrdenesPendientesFragment();
        np.setArguments(bundle);

        //nf.setArguments(bundle);
        adapter.addFragment(nf, "Pendiente");
        adapter.addFragment(np, "Atualizado");
        viewPager.setAdapter(adapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
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

    public void sincronizar() {

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
                    "  ,fis_sitimo" +
                    "  ,fis_sitsup" +
                    "  ,fis_tipsup" +
                    "  ,fis_foraba" +
                    "  ,fis_repo" +
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

                        //PASO 7: Actualiza a pariedad 7
                        PreparedStatement ps = conexionBD().prepareStatement(
                                "UPDATE enorsig_app SET acao = ?, leitura2 = ?, observacao = ?, estado = ?, HoraVisita = ?, Geo = ?," +
                                        " Foto1_GXI = ?,  Foto2_GXI = ?, Foto3_GXI = ?, Foto4_GXI = ?, Foto5_GXI = ?, Foto6_GXI = ?, Foto7_GXI = ?," +
                                        " pariedade = ?, ramal = ?, onde = ?, assinatura_gxi = ?," +
                                        " nome_cliente = ?, nro_direita = ?, nro_esquerda = ?, telefone = ?, caract_imovel = ?," +
                                        " fis_sitimo = ?, fis_sitsup = ?, fis_tipsup = ?, fis_foraba = ?, fis_repo = ?" +
                                        " WHERE VisCFum = '"+ cursor.getString(0) +"'" +
                                        " AND VisDVis = '"+ cursor.getString(1) +"'" +
                                        " AND rgiNrgi = '"+ cursor.getString(2) +"'" +
                                        " AND dcsNord = "+ cursor.getString(3) + "");

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

                        ps.setString(23, cursor.getString(28));
                        ps.setString(24, cursor.getString(29));
                        ps.setString(25, cursor.getString(30));
                        ps.setString(26, cursor.getString(31));
                        ps.setString(27, cursor.getString(32));


                        // call executeUpdate to execute our sql update statement
                        ps.executeUpdate();
                        ps.close();

                    } catch (SQLException se) {
                        Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    db.execSQL("UPDATE enorsig_app" +
                                " SET pariedade = '8'" +
                                " WHERE viscfum = '"+nroEmpleado+"'" +
                                " and visdvis = '" + fechaHoy + "'" +
                                " and rginrgi = '"+cursor.getString(2)+"'" +
                                " and dcsnord = "+cursor.getString(3));

                } while (cursor.moveToNext());

                Toast.makeText(getApplicationContext(), "Carga exitosa.", Toast.LENGTH_SHORT).show();

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

        } else {
            Toast.makeText(getApplicationContext(), "No existe conexion con la BD. Intente nuevamente.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        intent.putExtra("nroEmpleado", nroEmpleado);
        intent.putExtra("fechaHoy", fechaHoy);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();

    }

    public void sincronizar_emergencia() {

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
                    "  ,fis_sitimo" +
                    "  ,fis_sitsup" +
                    "  ,fis_tipsup" +
                    "  ,fis_foraba" +
                    "  FROM [enorsig_app] t1" +
                    "  where 1 = 1" +
                    "  AND estado = 1" +
                    "  AND pariedade = 8" +
                    "  AND t1.VisCFum = '" + nroEmpleado + "'" +
                    "  and VisDVis = '" + fechaHoy + "'";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {

                    try {

                        //PASO 7: Actualiza a pariedad 7
                        PreparedStatement ps = conexionBD().prepareStatement(
                                "UPDATE enorsig_app SET acao = ?, leitura2 = ?, observacao = ?, estado = ?, HoraVisita = ?, Geo = ?," +
                                        //" Foto1_GXI = ?,  Foto2_GXI = ?, Foto3_GXI = ?, Foto4_GXI = ?, Foto5_GXI = ?, Foto6_GXI = ?, Foto7_GXI = ?," +
                                        " Foto1_GXI = ?,  Foto2_GXI = ?, Foto3_GXI = ?, Foto4_GXI = ?, Foto5_GXI = ?, Foto6_GXI = ?," +
                                        " pariedade = ?, ramal = ?, onde = ?, assinatura_gxi = ?," +
                                        " nome_cliente = ?, nro_direita = ?, nro_esquerda = ?, telefone = ?, caract_imovel = ?," +
                                        " fis_sitimo = ?, fis_sitsup = ?, fis_tipsup = ?, fis_foraba = ?" +
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
                        //ps.setString(13, cursor.getString(18));
                        ps.setString(13, cursor.getString(19));
                        ps.setString(14, cursor.getString(20));
                        ps.setString(15, cursor.getString(21));
                        ps.setString(16, cursor.getString(22));

                        ps.setString(17, cursor.getString(23));
                        ps.setString(18, cursor.getString(24));
                        ps.setString(19, cursor.getString(25));
                        ps.setString(20, cursor.getString(26));
                        ps.setString(21, cursor.getString(27));

                        ps.setString(22, cursor.getString(28));
                        ps.setString(23, cursor.getString(29));
                        ps.setString(24, cursor.getString(30));
                        ps.setString(25, cursor.getString(31));

                        ps.setString(26, cursor.getString(0));
                        ps.setString(27, cursor.getString(1));
                        ps.setString(28, cursor.getString(2));
                        ps.setInt(29, Integer.parseInt(cursor.getString(3)));

                        // call executeUpdate to execute our sql update statement
                        ps.executeUpdate();
                        ps.close();

                    } catch (SQLException se) {
                        Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    db.execSQL("UPDATE enorsig_app" +
                            " SET pariedade = '8'" +
                            " WHERE viscfum = '"+nroEmpleado+"'" +
                            " and visdvis = '" + fechaHoy + "'" +
                            " and rginrgi = '"+cursor.getString(2)+"'" +
                            " and dcsnord = "+cursor.getString(3));

                } while (cursor.moveToNext());

                Toast.makeText(getApplicationContext(), "Carga exitosa.", Toast.LENGTH_SHORT).show();

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

        } else {
            Toast.makeText(getApplicationContext(), "No existe conexion con la BD. Intente nuevamente.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        intent.putExtra("nroEmpleado", nroEmpleado);
        intent.putExtra("fechaHoy", fechaHoy);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);
                        // Crear nuevo fragmento
                        String title = menuItem.getTitle().toString();
                        selectItem(title);
                        return true;
                    }
                }
        );
    }

    private void selectItem(String title) {
        // Enviar título como arguemento del fragmento
        Bundle args = new Bundle();

        if (title.equals("Fotos")) {
            Intent intent2 = new Intent(getApplicationContext(), PhotosActivity.class);
            intent2.putExtra("nroEmpleado", nroEmpleado);
            intent2.putExtra("fechaHoy", fechaHoy);
            startActivity(intent2);
        }

        if (title.equals("Meta Mensal")) {
            Intent intent = new Intent(getApplicationContext(), PersonDetailActivity.class);
            intent.putExtra("nroEmpleado", nroEmpleado);
            intent.putExtra("fechaHoy", fechaHoy);
            startActivity(intent);
        }

        if (title.equals("Mapa")) {
            Intent intent1 = new Intent(getApplicationContext(), MapActivity.class);
            intent1.putExtra("nroEmpleado", nroEmpleado);
            intent1.putExtra("fechaHoy", fechaHoy);
            startActivity(intent1);
        }

        if (title.equals("Subir Ordenes")) {
            sincronizar();
        }

        if (title.equals("Actualizar Ordenes")) {
            actualizarOrden();
        }

        if (title.equals("Reenviar")) {
            sincronizar_emergencia();
        }

        if (title.equals("Cerrar Sesión")) {
            cerrar_sesion();
        }

        drawerLayout.closeDrawers(); // Cerrar drawer
        //setTitle(title); // Setear título actual

    }

    private void cerrar_sesion() {
        enorsul.com.enorsul.Utils.guardarValor(getApplicationContext(), "usuario", "");
        enorsul.com.enorsul.Utils.guardarValor(getApplicationContext(), "clave", "");

        Intent intent1 = new Intent(this, LoginActivity.class);
        startActivity(intent1);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.hideOverflowMenu();
        //toolbar.setOverflowIcon(null);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void actualizarOrden() {

        String mensaje = null;
        try {
            mensaje = getContenidoUrl("http://enorsig.sytes.net:8181/Producao/app/enorsig_orden.phtml?gsUser="+nroEmpleado+"&gsData="+fechaHoy+"");
            String[] mensajes = mensaje.split("\\*");

            for(String msj : mensajes) {
                String[] campos = msj.split("=");
                String nro_orden = campos[1];
                String id = campos[0];

                db.execSQL("UPDATE enorsig_app" +
                        " SET nro_orden = '" + nro_orden + "'" +
                        " WHERE dcsnord = "+ id );

                // Setear adaptador al viewpager.
                mViewPager = findViewById(R.id.pager);
                mViewPager.setOffscreenPageLimit(3);
                setupViewPager(mViewPager);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String getContenidoUrl(String url) throws IOException {

        String linea = "";
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URLConnection conn = new URL(url).openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            linea = br.readLine();
        } catch (MalformedURLException e) {
            Log.w("","MALFORMED URL EXCEPTION");
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
        return linea;
    }

}
