package enorsul.com.enorsul;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import enorsul.com.enorsul.models.OrdenModelo;

public class OrdenDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    OrdenModelo ordenModelo;
    TextView nroOC, RGI, direccion, lblTituloOrden, lblRamal, lblTipoSuspen;
    TextView lblImovel, lblSupressao, lblTSupressao, lblAbastecimiento, lblRepo;
    Spinner txtAcao, txtRamal, txtImovel, txtSupressao, txtTSupressao, txtAbastecimiento, txtRepo;
    EditText txtLectura, txtObservacion, txtNomeDoCliente, txtTelefone, txtNroEsquerda, txtNroDereita, txtCaracteristicasImovel;
    TextView lblNomeDoCliente, lblTelefone, lblNroEsquerda, lblNroDereita, lblCaracteristicasImovel;
    Button btnEnviar;

    String[] acaos;
    String[] acaos_id;

    String horaHoy;
    String valorSpinner = "0";
    String valorSpinner2 = "0";

    Button captureButtona, captureButtonb, captureButtonc, captureButtond, captureButtone, captureButtonf, captureButtong;

    RadioGroup radioG;

    RadioButton radio1, radio2, radio3;
    GridLayout gridFotos;

    private String numeroOs;
    private ImageView capturedImageHolder;
    private ArrayList<ImageView> holders = new ArrayList<>();
    private Intent takePictureIntent;
    private String mCurrentPhotoPath;
    private String mCurrentFileName;
    private String mCurrentSize;
    private File photoFile = null;
    private Uri file;
    static final int REQUEST_TAKE_PHOTO = 1;

    private TextView lblLatitud, lblLongitud;

    private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient apiClient;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EnorsigSQLiteHelper usdbh = new EnorsigSQLiteHelper(this, "DBEnorsig", null, 19);
        db = usdbh.getWritableDatabase();

        nroOC = findViewById(R.id.nroOC);
        RGI = findViewById(R.id.RGI);
        direccion = findViewById(R.id.direccion);
        txtAcao = findViewById(R.id.txtAcao);
        lblRamal = findViewById(R.id.lblRamal);
        txtRamal = findViewById(R.id.txtRamal);
        lblTipoSuspen = findViewById(R.id.lblTipoSuspen);
        txtLectura = findViewById(R.id.txtLectura);
        txtObservacion = findViewById(R.id.txtObservacion);
        lblTituloOrden = findViewById(R.id.lblTituloOrden);
        btnEnviar = findViewById(R.id.btnEnviar);

        txtNomeDoCliente = findViewById(R.id.txtNomeDoCliente);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtNroEsquerda = findViewById(R.id.txtNroEsquerda);
        txtNroDereita = findViewById(R.id.txtNroDereita);
        txtCaracteristicasImovel = findViewById(R.id.txtCaracteristicasImovel);

        lblNomeDoCliente = findViewById(R.id.lblNomeDoCliente);
        lblTelefone = findViewById(R.id.lblTelefone);
        lblNroEsquerda = findViewById(R.id.lblNroEsquerda);
        lblNroDereita = findViewById(R.id.lblNroDereita);
        lblCaracteristicasImovel = findViewById(R.id.lblCaracteristicasImovel);

        lblImovel = findViewById(R.id.lblImovel);
        lblSupressao = findViewById(R.id.lblSupressao);
        lblTSupressao = findViewById(R.id.lblTSupressao);
        lblAbastecimiento = findViewById(R.id.lblAbastecimiento);
        lblRepo = findViewById(R.id.lblRepo);

        txtImovel = findViewById(R.id.txtImovel);
        txtSupressao = findViewById(R.id.txtSupressao);
        txtTSupressao = findViewById(R.id.txtTSupressao);
        txtAbastecimiento = findViewById(R.id.txtAbastecimiento);
        txtRepo = findViewById(R.id.txtRepo);

        captureButtona = findViewById(R.id.button_a);
        captureButtonb = findViewById(R.id.button_b);
        captureButtonc = findViewById(R.id.button_c);
        captureButtond = findViewById(R.id.button_d);
        captureButtone = findViewById(R.id.button_e);
        captureButtonf = findViewById(R.id.button_f);
        captureButtong = findViewById(R.id.button_g);

        radioG = findViewById(R.id.radio_tipos);
        radio1 = findViewById(R.id.radio_1);
        radio2 = findViewById(R.id.radio_2);
        radio3 = findViewById(R.id.radio_3);
        gridFotos = findViewById(R.id.gridFotos);

        lblLatitud = findViewById(R.id.lblLatitud);
        lblLongitud = findViewById(R.id.lblLongitud);

        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(c.getTimeZone());
        horaHoy = dateFormat.format(c.getTime());

        ordenModelo = NewsApp.getInstance().getService().getOrdenModelo();
        setTitle("Ordem de Servicio: " + ordenModelo.getNroOrden());
        nroOC.setText(ordenModelo.getNroOrden());
        RGI.setText(ordenModelo.getRGI());
        direccion.setText(ordenModelo.getObservaciones());
        lblTituloOrden.setText(ordenModelo.getNombre());

        if (lblTituloOrden.getText().toString().equals("Restabelecimento")) {
            lblRamal.setVisibility(View.VISIBLE);
            txtRamal.setVisibility(View.VISIBLE);

            /*LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
            lp1.setMargins(0,0,0,0);
            gridFotos.setLayoutParams(lp1);*/

        } else if (lblTituloOrden.getText().toString().equals("Supressão")) {

            lblRamal.setVisibility(View.VISIBLE);
            txtRamal.setVisibility(View.VISIBLE);
            lblTipoSuspen.setVisibility(View.VISIBLE);
            radioG.setVisibility(View.VISIBLE);

            /*LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
            lp1.setMargins(0,0,0,0);
            gridFotos.setLayoutParams(lp1);*/

        } else if (lblTituloOrden.getText().toString().equals("Corte")) {
            lblTipoSuspen.setVisibility(View.VISIBLE);
            radioG.setVisibility(View.VISIBLE);
            lblTipoSuspen.setText("Tipo de Corte");

            /*LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lblTipoSuspen.getLayoutParams();
            lp.setMargins(0,0,0,0);
            lblTipoSuspen.setLayoutParams(lp);

            txtNomeDoCliente.setHeight(0);
            txtTelefone.setHeight(0);
            txtNroEsquerda.setHeight(0);
            txtNroDereita.setHeight(0);
            txtCaracteristicasImovel.setHeight(0);
            lblNomeDoCliente.setHeight(0);
            lblTelefone.setHeight(0);
            lblNroEsquerda.setHeight(0);
            lblNroDereita.setHeight(0);
            lblCaracteristicasImovel.setHeight(0);

            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
            lp1.setMargins(0,-20,0,0);
            lblNomeDoCliente.setLayoutParams(lp1);

            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
            lp2.setMargins(0,-20,0,0);
            lblTelefone.setLayoutParams(lp2);

            LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
            lp3.setMargins(0,-20,0,0);
            lblNroEsquerda.setLayoutParams(lp3);

            LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
            lp4.setMargins(0,-20,0,0);
            lblNroDereita.setLayoutParams(lp4);

            LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
            lp5.setMargins(0,-20,0,0);
            lblCaracteristicasImovel.setLayoutParams(lp5);

            LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
            lp6.setMargins(0,-900,0,0);
            gridFotos.setLayoutParams(lp6);*/

        } else {

            /*lblTipoSuspen.setHeight(0);
            radio1.setHeight(0);
            radio2.setHeight(0);
            radio3.setHeight(0);

            lblRamal.setHeight(0);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lblTipoSuspen.getLayoutParams();
            lp.setMargins(0,-140,0,0);
            lblTipoSuspen.setLayoutParams(lp);

            LinearLayout.LayoutParams lpa = (LinearLayout.LayoutParams) lblRamal.getLayoutParams();
            lpa.setMargins(0,0,0,0);
            lblTipoSuspen.setLayoutParams(lpa);

            txtNomeDoCliente.setHeight(0);
            txtTelefone.setHeight(0);
            txtNroEsquerda.setHeight(0);
            txtNroDereita.setHeight(0);
            txtCaracteristicasImovel.setHeight(0);
            lblNomeDoCliente.setHeight(0);
            lblTelefone.setHeight(0);
            lblNroEsquerda.setHeight(0);
            lblNroDereita.setHeight(0);
            lblCaracteristicasImovel.setHeight(0);

            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
            lp1.setMargins(0,-20,0,0);
            lblNomeDoCliente.setLayoutParams(lp1);

            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
            lp2.setMargins(0,-20,0,0);
            lblTelefone.setLayoutParams(lp2);

            LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
            lp3.setMargins(0,-20,0,0);
            lblNroEsquerda.setLayoutParams(lp3);

            LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
            lp4.setMargins(0,-20,0,0);
            lblNroDereita.setLayoutParams(lp4);

            LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
            lp5.setMargins(0,-20,0,0);
            lblCaracteristicasImovel.setLayoutParams(lp5);

            LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
            lp6.setMargins(0,0,0,0);
            gridFotos.setLayoutParams(lp6);*/
        }

        Resources res = getResources();
        //String[] acaos = res.getStringArray(R.array.acao_array);
        if (ordenModelo.getNombre().equals("Carta Cartorio")) {
            acaos = new String[] {
                    "0 - NAO EXECUTADO",
                    "105 - CARTA ACARTORIO / NEGATIVAÇÃO ENVIADA",
                    "301 - CONTA PAGA",
                    "309 - PREDIO VAGOSEM MORADOR",
                    "310 - MORADOR AUSENTE",
                    "314 - DEMOLIDO COM LIGAÇÃO",
                    "316 - NÃO LOCALIZADA",
                    "317 - RUA NÃO LOCALIZADA"};

            acaos_id = new String[] {
                    "0","105","301","309","310","314","316","317"};
        } else if (ordenModelo.getNombre().equals("EXTRATO")) {
            acaos = new String[] {
                    "0 - NAO EXECUTADO",
                    "104 - ENTREGA DE EXTRATO",
                    "301 - CONTA PAGA",
                    "309 - PREDIO VAGOSEM MORADOR",
                    "310 - MORADOR AUSENTE",
                    "314 - DEMOLIDO COM LIGAÇÃO",
                    "316 - NÃO LOCALIZADA",
                    "317 - RUA NÃO LOCALIZADA"};

            acaos_id = new String[] {
                    "0","104","301","309","310","314","316","317"};
        } else if (ordenModelo.getNombre().equals("Fita Lacre")) {
            acaos = new String[] {
                    "0 - NAO EXECUTADO",
                    "103 - APLICAÇÃO DE FITA LACRE",
                    "301 - CONTA PAGA",
                    "306 - IMPEDIU O CORTE",
                    "309 - PREDIO VAGOSEM MORADOR",
                    "310 - MORADOR AUSENTE",
                    "311 - HIDRO COM IMPEDIMENTO",
                    "314 - DEMOLIDO COM LIGAÇÃO",
                    "316 - NÃO LOCALIZADA",
                    "317 - RUA NÃO LOCALIZADA"};

            acaos_id = new String[] {
                    "0","103","301","306","309","310","311","314","316","317"};
        } else if (ordenModelo.getNombre().equals("Corte")) {
            acaos = new String[] {
                    "0 - NAO EXECUTADO",
                    "101 - CORTE EFETUADO",
                    "301 - CONTA PAGA",
                    "306 - IMPEDIU O CORTE",
                    "309 - PREDIO VAGOSEM MORADOR",
                    "310 - MORADOR AUSENTE",
                    "311 - HIDRO COM IMPEDIMENTO",
                    "314 - DEMOLIDO COM LIGAÇÃO",
                    "316 - NÃO LOCALIZADA",
                    "317 - RUA NÃO LOCALIZADA"};

            acaos_id = new String[] {
                    "0","101","301","306","309","310","311","314","316","317"};
        } else {
            acaos = res.getStringArray(R.array.acao_array);
            acaos_id = res.getStringArray(R.array.acao_array_id);
        }

        String[] ramal = res.getStringArray(R.array.ramal_array);
        String[] imovel = res.getStringArray(R.array.imovel);
        String[] supressao = res.getStringArray(R.array.supressao);
        String[] tsupressao = res.getStringArray(R.array.tsupressao);
        String[] abastecimiento = res.getStringArray(R.array.abastecimiento);
        String[] repo = res.getStringArray(R.array.repo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        txtAcao.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, acaos));
        txtRamal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ramal));
        txtImovel.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, imovel));
        txtSupressao.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, supressao));
        txtTSupressao.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tsupressao));
        txtAbastecimiento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, abastecimiento));
        txtRepo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, repo));


        if (verificaConexao()) {
            try {
                //PASO 3: Cambio a pariedade 3
                String sql = "UPDATE enorsig_app" +
                        " SET pariedade = '3'" +
                        " WHERE VisCFum = '" + ordenModelo.getFuncionario() + "'" +
                        " AND VisDVis = '" + ordenModelo.getFechaOrden() + "'" +
                        " AND dcsNord = '" + ordenModelo.getNroOrden() + "'" +
                        " AND rginrgi = '" + ordenModelo.getRGI() + "'";

                PreparedStatement ps = conexionBD().prepareStatement(sql);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                //Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        txtAcao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //valorSpinner = getResources().getStringArray(R.array.acao_array_id)[position];
                valorSpinner = acaos_id[position];
                if (valorSpinner.equals("105") && ordenModelo.getNombre().equals("Carta Cartorio")) {
                    txtNomeDoCliente.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNomeDoCliente.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);
                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(106);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(0);
                    txtNroDereita.setHeight(0);
                    txtCaracteristicasImovel.setHeight(0);
                    lblNomeDoCliente.setHeight(57);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(0);
                    lblNroDereita.setHeight(0);
                    lblCaracteristicasImovel.setHeight(0);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,-20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,-20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,-20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,-200,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("309") && ordenModelo.getNombre().equals("Carta Cartorio")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("310") && ordenModelo.getNombre().equals("Carta Cartorio")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    txtTelefone.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);
                    lblTelefone.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(0);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(0);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,-20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

////////////////////////////////////////////////////////////////////////////////////////////////////

                } else if (valorSpinner.equals("101") && ordenModelo.getNombre().equals("Corte")) {
                    txtNomeDoCliente.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNomeDoCliente.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);
                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(106);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(0);
                    txtNroDereita.setHeight(0);
                    txtCaracteristicasImovel.setHeight(0);
                    lblNomeDoCliente.setHeight(57);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(0);
                    lblNroDereita.setHeight(0);
                    lblCaracteristicasImovel.setHeight(0);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,-20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,-20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,-20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,-200,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("309") && ordenModelo.getNombre().equals("Corte")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("310") && ordenModelo.getNombre().equals("Corte")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    txtTelefone.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);
                    lblTelefone.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(0);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(0);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,-20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/


////////////////////////////////////////////////////////////////////////////////////////////////////

                } else if (valorSpinner.equals("104") && ordenModelo.getNombre().equals("EXTRATO")) {
                    txtNomeDoCliente.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNomeDoCliente.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);
                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(106);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(0);
                    txtNroDereita.setHeight(0);
                    txtCaracteristicasImovel.setHeight(0);
                    lblNomeDoCliente.setHeight(57);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(0);
                    lblNroDereita.setHeight(0);
                    lblCaracteristicasImovel.setHeight(0);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,-20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,-20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,-20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,-200,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("309") && ordenModelo.getNombre().equals("EXTRATO")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("310") && ordenModelo.getNombre().equals("EXTRATO")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    txtTelefone.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);
                    lblTelefone.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(0);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(0);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,-20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

////////////////////////////////////////////////////////////////////////////////////////////////////

                } else if (valorSpinner.equals("103") && ordenModelo.getNombre().equals("Fita Lacre")) {
                    txtNomeDoCliente.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNomeDoCliente.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);
                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(106);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(0);
                    txtNroDereita.setHeight(0);
                    txtCaracteristicasImovel.setHeight(0);
                    lblNomeDoCliente.setHeight(57);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(0);
                    lblNroDereita.setHeight(0);
                    lblCaracteristicasImovel.setHeight(0);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,-20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,-20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,-20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,-200,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("309") && ordenModelo.getNombre().equals("Fita Lacre")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(106);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(57);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/

                } else if (valorSpinner.equals("310") && ordenModelo.getNombre().equals("Fita Lacre")) {
                    txtNroEsquerda.setVisibility(View.VISIBLE);
                    txtNroDereita.setVisibility(View.VISIBLE);
                    txtCaracteristicasImovel.setVisibility(View.VISIBLE);
                    lblNroEsquerda.setVisibility(View.VISIBLE);
                    lblNroDereita.setVisibility(View.VISIBLE);
                    lblCaracteristicasImovel.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.GONE);
                    txtTelefone.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);
                    lblTelefone.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(0);
                    txtNroEsquerda.setHeight(106);
                    txtNroDereita.setHeight(106);
                    txtCaracteristicasImovel.setHeight(106);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(0);
                    lblNroEsquerda.setHeight(57);
                    lblNroDereita.setHeight(57);
                    lblCaracteristicasImovel.setHeight(57);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0, -20, 0, 0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0, -20, 0, 0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0, 20, 0, 0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0, 20, 0, 0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0, 20, 0, 0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0, 0, 0, 0);
                    gridFotos.setLayoutParams(lp6);*/

////////////////////////////////////////////////////////////////////////////////////////////////////

                } else if (ordenModelo.getNombre().equals("Fiscalização")) {

                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);

                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    lblNomeDoCliente.setVisibility(View.VISIBLE);
                    lblTelefone.setVisibility(View.VISIBLE);
                    lblImovel.setVisibility(View.VISIBLE);
                    lblSupressao.setVisibility(View.VISIBLE);
                    lblTSupressao.setVisibility(View.VISIBLE);
                    lblAbastecimiento.setVisibility(View.VISIBLE);
                    lblRepo.setVisibility(View.VISIBLE);

                    txtNomeDoCliente.setVisibility(View.VISIBLE);
                    txtTelefone.setVisibility(View.VISIBLE);
                    txtImovel.setVisibility(View.VISIBLE);
                    txtSupressao.setVisibility(View.VISIBLE);
                    txtTSupressao.setVisibility(View.VISIBLE);
                    txtAbastecimiento.setVisibility(View.VISIBLE);
                    txtRepo.setVisibility(View.VISIBLE);


                } else {

                    txtNomeDoCliente.setVisibility(View.GONE);
                    txtTelefone.setVisibility(View.GONE);
                    txtNroEsquerda.setVisibility(View.GONE);
                    txtNroDereita.setVisibility(View.GONE);
                    txtCaracteristicasImovel.setVisibility(View.GONE);
                    lblNomeDoCliente.setVisibility(View.GONE);
                    lblTelefone.setVisibility(View.GONE);
                    lblNroEsquerda.setVisibility(View.GONE);
                    lblNroDereita.setVisibility(View.GONE);
                    lblCaracteristicasImovel.setVisibility(View.GONE);

                    /*txtNomeDoCliente.setHeight(0);
                    txtTelefone.setHeight(0);
                    txtNroEsquerda.setHeight(0);
                    txtNroDereita.setHeight(0);
                    txtCaracteristicasImovel.setHeight(0);
                    lblNomeDoCliente.setHeight(0);
                    lblTelefone.setHeight(0);
                    lblNroEsquerda.setHeight(0);
                    lblNroDereita.setHeight(0);
                    lblCaracteristicasImovel.setHeight(0);

                    LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) lblNomeDoCliente.getLayoutParams();
                    lp1.setMargins(0,-20,0,0);
                    lblNomeDoCliente.setLayoutParams(lp1);

                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) lblTelefone.getLayoutParams();
                    lp2.setMargins(0,-20,0,0);
                    lblTelefone.setLayoutParams(lp2);

                    LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) lblNroEsquerda.getLayoutParams();
                    lp3.setMargins(0,-20,0,0);
                    lblNroEsquerda.setLayoutParams(lp3);

                    LinearLayout.LayoutParams lp4 = (LinearLayout.LayoutParams) lblNroDereita.getLayoutParams();
                    lp4.setMargins(0,-20,0,0);
                    lblNroDereita.setLayoutParams(lp4);

                    LinearLayout.LayoutParams lp5 = (LinearLayout.LayoutParams) lblCaracteristicasImovel.getLayoutParams();
                    lp5.setMargins(0,-20,0,0);
                    lblCaracteristicasImovel.setLayoutParams(lp5);

                    LinearLayout.LayoutParams lp6 = (LinearLayout.LayoutParams) gridFotos.getLayoutParams();
                    lp6.setMargins(0,0,0,0);
                    gridFotos.setLayoutParams(lp6);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtRamal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valorSpinner2 = getResources().getStringArray(R.array.ramal_array_id)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Integer.parseInt(txtLectura.getText().toString()) > 32767){
                    Toast.makeText(getApplicationContext(), "Leitura mayor que 32,767.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (valorSpinner.equals("0")){
                    Toast.makeText(getApplicationContext(), "Favor Escolher a Acao.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (txtObservacion.getText().toString().length() < 3){
                    Toast.makeText(getApplicationContext(), "Preencher a observacao - No minio 3 caracteres", Toast.LENGTH_LONG).show();
                    return;
                }


                if (validar_fotos()) {
                    if (validar_gps()) {
                        try {
                            String localizacion = lblLatitud.getText() + ", " + lblLongitud.getText();

                            Calendar c = Calendar.getInstance();
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            dateFormat.setTimeZone(c.getTimeZone());
                            String fechaHoraHoy = dateFormat.format(c.getTime());

                            String tipoConexion = "";
                            if (radio1.isChecked()) {
                                tipoConexion = "0";
                            }
                            if (radio2.isChecked()) {
                                tipoConexion = "1";
                            }
                            if (radio3.isChecked()) {
                                tipoConexion = "2";
                            }

                            //PREVIO: Actualiza a pariedad 7, pero de SQLite para enviar despues a BD

                            String sql = "UPDATE enorsig_app SET acao = ?, leitura2 = ?, observacao = ?, estado = ?, HoraVisita = ?, Geo = ?," +
                                    " Foto1_GXI = ?, Foto2_GXI = ?, Foto3_GXI = ?, Foto4_GXI = ?, Foto5_GXI = ?, Foto6_GXI = ?, Foto7_GXI = ?," +
                                    " pariedade = ?, ramal = ?, onde = ?, assinatura_gxi = ?," +
                                    " nome_cliente = ?, nro_dereita = ?, nro_esquerda = ?, telefone = ?, caract_imovel = ?," +
                                    " fis_sitimo = ?, fis_sitsup = ?, fis_tipsup = ?, fis_foraba = ?, fis_repo = ?" +
                                    " WHERE VisCFum = ? AND VisDVis = ? AND rgiNrgi = ? AND dcsNord = ?";

                            //PreparedStatement ps = conexionBD().prepareStatement(sql);
                            SQLiteStatement stmt = db.compileStatement(sql);

                            stmt.bindString(1, valorSpinner);
                            stmt.bindString(2, (txtLectura.getText().toString().equals("")) ? "-1" : txtLectura.getText().toString());
                            stmt.bindString(3, "VST " + fechaHoraHoy + " " + txtObservacion.getText().toString());
                            stmt.bindString(4, "1");
                            stmt.bindString(5, horaHoy);
                            stmt.bindString(6, localizacion);

                            String cadParametro = "x|x";

                            String[] partsa = (findViewById(R.id.captured_image_a).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_a).getTag().toString().split("\\|");
                            String[] partsb = (findViewById(R.id.captured_image_b).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_b).getTag().toString().split("\\|");
                            String[] partsc = (findViewById(R.id.captured_image_c).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_c).getTag().toString().split("\\|");
                            String[] partsd = (findViewById(R.id.captured_image_d).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_d).getTag().toString().split("\\|");
                            String[] partse = (findViewById(R.id.captured_image_e).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_e).getTag().toString().split("\\|");
                            String[] partsf = (findViewById(R.id.captured_image_f).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_f).getTag().toString().split("\\|");
                            String[] partsg = (findViewById(R.id.captured_image_g).getTag() == null) ? cadParametro.split("\\|") : findViewById(R.id.captured_image_g).getTag().toString().split("\\|");

                            postImage(partsa[0], Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Enorsoft/" + partsa[0], "");
                            postImage(partsb[0], Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Enorsoft/" + partsb[0], "");
                            postImage(partsc[0], Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Enorsoft/" + partsc[0], "");

                            String cadenaSize = ((partsa[1].equals("x")) ? "" : partsa[1]) + ";" +
                                                ((partsb[1].equals("x")) ? "" : partsb[1]) + ";" +
                                                ((partsc[1].equals("x")) ? "" : partsc[1]) + ";" +
                                                ((partsd[1].equals("x")) ? "" : partsd[1]) + ";" +
                                                ((partse[1].equals("x")) ? "" : partse[1]) + ";" +
                                                ((partsf[1].equals("x")) ? "" : partsf[1]) + ";" +
                                                ((partsg[1].equals("x")) ? "" : partsg[1]) + ";";

                            stmt.bindString(7, (partsa[0].equals("x")) ? "" : partsa[0]);
                            stmt.bindString(8, (partsb[0].equals("x")) ? "" : partsb[0]);//(findViewById(R.id.captured_image_b).getTag() == null) ? "" : findViewById(R.id.captured_image_b).getTag().toString());
                            stmt.bindString(9, (partsc[0].equals("x")) ? "" : partsc[0]);//(findViewById(R.id.captured_image_c).getTag() == null) ? "" : findViewById(R.id.captured_image_c).getTag().toString());
                            stmt.bindString(10, (partsd[0].equals("x")) ? "" : partsd[0]);//(findViewById(R.id.captured_image_d).getTag() == null) ? "" : findViewById(R.id.captured_image_d).getTag().toString());
                            stmt.bindString(11, (partse[0].equals("x")) ? "" : partse[0]);//(findViewById(R.id.captured_image_e).getTag() == null) ? "" : findViewById(R.id.captured_image_e).getTag().toString());
                            stmt.bindString(12, (partsf[0].equals("x")) ? "" : partsf[0]);//(findViewById(R.id.captured_image_f).getTag() == null) ? "" : findViewById(R.id.captured_image_f).getTag().toString());
                            stmt.bindString(13, (partsg[0].equals("x")) ? "" : partsg[0]);//(findViewById(R.id.captured_image_g).getTag() == null) ? "" : findViewById(R.id.captured_image_g).getTag().toString());
                            stmt.bindString(14, "7");
                            stmt.bindString(15, valorSpinner2);
                            stmt.bindString(16, tipoConexion);
                            stmt.bindString(17, cadenaSize);

                            stmt.bindString(18, txtNomeDoCliente.getText().toString());
                            stmt.bindString(19, txtNroDereita.getText().toString());
                            stmt.bindString(20, txtNroEsquerda.getText().toString());
                            stmt.bindString(21, txtTelefone.getText().toString());
                            stmt.bindString(22, txtCaracteristicasImovel.getText().toString());

                            stmt.bindString(23, txtImovel.getSelectedItem().toString());
                            stmt.bindString(24, txtSupressao.getSelectedItem().toString());
                            stmt.bindString(25, txtTSupressao.getSelectedItem().toString());
                            stmt.bindString(26, txtAbastecimiento.getSelectedItem().toString());
                            stmt.bindString(27, txtRepo.getSelectedItem().toString());

                            stmt.bindString(28, ordenModelo.getFuncionario());
                            stmt.bindString(29, ordenModelo.getFechaOrden());
                            stmt.bindString(30, RGI.getText().toString());
                            stmt.bindString(31, nroOC.getText().toString());

                            stmt.execute();
                            stmt.close();

                            if (verificaConexao()) {
                                try {
                                    //PASO 4: Cambio a pariedade 4
                                    sql = "UPDATE enorsig_app" +
                                            " SET pariedade = '4'" +
                                            " WHERE VisCFum = '" + ordenModelo.getFuncionario() + "'" +
                                            " AND VisDVis = '" + ordenModelo.getFechaOrden() + "'" +
                                            " AND dcsNord = '" + ordenModelo.getNroOrden() + "'" +
                                            " AND rginrgi = '" + ordenModelo.getRGI() + "'";

                                    PreparedStatement ps = conexionBD().prepareStatement(sql);
                                    ps.executeUpdate();
                                    ps.close();
                                } catch (SQLException e) {
                                    //Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            Intent intent = new Intent(v.getContext(), PrincipalActivity.class);
                            intent.putExtra("nroEmpleado", ordenModelo.getFuncionario());
                            intent.putExtra("fechaHoy", ordenModelo.getFechaOrden());
                            v.getContext().startActivity(intent);

                        } catch (SQLiteException se) {
                            //System.out.println(se);
                            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Nao se puede gravar. Ativar o GPS.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Falta tomar 3 fotos minimo.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        captureButtona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validar_gps()) {
                        numeroOs = ordenModelo.getNroOrden();
                        capturedImageHolder = (ImageView) findViewById(R.id.captured_image_a);
                        holders.add(capturedImageHolder);
                        dispatchTakePictureIntent("A");
                    } else {
                        Toast.makeText(getApplicationContext(), "Nao se puede gravar. Ativar o GPS.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtonb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_b);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("B");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtonc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_c);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("C");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_d);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("D");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_e);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("E");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtonf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_f);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("F");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

        captureButtong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    numeroOs = ordenModelo.getNroOrden();
                    capturedImageHolder = (ImageView) findViewById(R.id.captured_image_g);
                    holders.add(capturedImageHolder);
                    dispatchTakePictureIntent("G");
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public boolean validar_fotos() {

        int contador = 0;

        ImageView cia = findViewById(R.id.captured_image_a);
        ImageView cib = findViewById(R.id.captured_image_b);
        ImageView cic = findViewById(R.id.captured_image_c);

        if (!(cia.getTag() == null)) {
            contador++;
        }
        if (!(cib.getTag() == null)) {
            contador++;
        }
        if (!(cic.getTag() == null)) {
            contador++;
        }

        if (contador >= 3) {
            return true;
        } else {
            return false;
        }

    }

    public boolean validar_gps() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI(lastLocation);

        }

        if (lblLatitud.getText().toString().equals("") && lblLongitud.getText().toString().equals("")) {
            return false;
        } else {
            return true;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            try {
                lblLatitud.setText(String.valueOf(loc.getLatitude()).substring(0,10));
            } catch (Exception e) {
                //
            }
            try {
                lblLongitud.setText(String.valueOf(loc.getLongitude()).substring(0,10));
            } catch (Exception e) {
                //
            }


        } else {
            lblLatitud.setText("");
            lblLongitud.setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d("SYNCLOG", "OrderDetail_RequestPermitilResult_inicio");


        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            Log.d("enorsoft", mCurrentPhotoPath);
            galleryAddPic();
            setPic();

        }
    }

    private void dispatchTakePictureIntent(String name){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);

        } else {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d("EnorsoftApp", "before takePictureIntent");
            // Ensure that there's a camera activity to handle the intent
            if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                try {
                    photoFile = getOutputMediaFile(name);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("EnorsoftApp", "file not created");
                }
                Log.d("EnorsoftApp", "file created");
                if(photoFile != null){
                    mCurrentPhotoPath = photoFile.getAbsolutePath();
                    mCurrentFileName = photoFile.getName();

                    //file = Uri.fromFile(photoFile);
                    file = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }


            }
        }

    }

    private File getOutputMediaFile(final String name) throws IOException {
        /***********************************
         /For API < 26
         /File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
         /        Environment.DIRECTORY_PICTURES), "Enorsoft Pictures");
         /***********************************/
        Date date = Calendar.getInstance().getTime();
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(date);
        final File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Enorsoft");

        if (!storageDir.exists()){
            Log.d("EnorsoftApp", "Pictures/Enorsoft directory doesn't exists");

            if (!storageDir.mkdirs()){
                Log.d("EnorsoftApp", "failed to create directory");
                return null;
            }
        }
        //Log.d("Enorsoft", "lat is:" + myResult.getLat());
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        String hora = dateFormat.format(c.getTime());

        File image = File.createTempFile(
                "Enorsig_"+ ordenModelo.getFuncionario() +
                        "_" + ordenModelo.getFechaOrden().replace("-", "") +
                        "_" + hora +
                        "_" + numeroOs +
                        "_" + ordenModelo.getRGI() + "_" + name +
                        "_",
                ".jpg",
                storageDir);
        return image;

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        mCurrentSize = String.valueOf(f.length());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = capturedImageHolder.getWidth();
        int targetH = capturedImageHolder.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        capturedImageHolder.setImageBitmap(bitmap);
        capturedImageHolder.setTag(mCurrentFileName + "|" + mCurrentSize); //mCurrentPhotoPath.toString()
    }

    public boolean verificaConexao() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postImage(String nameFoto, String imageFilePath, final String foto) {
        RequestParams params = new RequestParams();
        String encodedPhotoStr = "";
        try {
            Bitmap photo = Utils.decodeSampledBitmapFromFile(imageFilePath,1024,768);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 75, out);
            byte[] byteArrayPhoto = out.toByteArray();
            encodedPhotoStr = Base64.encodeToString(byteArrayPhoto, Base64.DEFAULT);
        } catch (Exception e) {
            e.getLocalizedMessage();
        }

        params.put("imagen", encodedPhotoStr);
        params.put("nombre", nameFoto);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://bp3tecnologia.ddns.net:8181/monitores/app_tzservicos/monitoreamento_fot.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    Log.e("IMAGEN SUBIDA", "Se ha subido la foto " + foto);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
