package enorsul.com.enorsul.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import enorsul.com.enorsul.R;

public class PersonDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String nroEmpleado = getIntent().getStringExtra("nroEmpleado");
        String fechaHoy = getIntent().getStringExtra("fechaHoy");

        setTitle("Producao Funcionario: " + nroEmpleado);

        WebView myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String wwwCadena = "http://enorsig.sytes.net:8181/Producao/app/enorsig_meta.phtml?gsUser=" +
                nroEmpleado + "&gsData=" + fechaHoy;
        myWebView.loadUrl(wwwCadena);
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
}
