package enorsul.com.enorsul.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import enorsul.com.enorsul.BuildConfig;
import enorsul.com.enorsul.R;

public class PhotosActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    Button captureButtona, captureButtonb;
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
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleApiClient apiClient;
    private static final String LOGTAG = "android-localizacion";

    String nroEmpleado, fechaHoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nroEmpleado = getIntent().getStringExtra("nroEmpleado");
        fechaHoy = getIntent().getStringExtra("fechaHoy");

        captureButtona = findViewById(R.id.button_a);
        captureButtonb = findViewById(R.id.button_b);
        lblLatitud = findViewById(R.id.lblLatitud);
        lblLongitud = findViewById(R.id.lblLongitud);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        captureButtona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validar_gps()) {

                        Calendar c = Calendar.getInstance();
                        DateFormat dateFormat = new SimpleDateFormat("HHmm");
                        String hora = dateFormat.format(c.getTime());

                        capturedImageHolder = findViewById(R.id.captured_image_a);
                        holders.add(capturedImageHolder);

                        DecimalFormat formateador = new DecimalFormat("000.000000");

                        dispatchTakePictureIntent("CONTROL_" +
                                nroEmpleado +
                                "_" +
                                fechaHoy.replace("-", "") +
                                "_" +
                                hora +
                                "_" +
                                formateador.format(Math.abs(Double.parseDouble(lblLatitud.getText().toString()))).replace(",", "") +
                                "_" +
                                formateador.format(Math.abs(Double.parseDouble(lblLongitud.getText().toString()))).replace(",", "") +
                                "_IN_");
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
                    if (validar_gps()) {

                        Calendar c = Calendar.getInstance();
                        DateFormat dateFormat = new SimpleDateFormat("HHmm");
                        String hora = dateFormat.format(c.getTime());

                        capturedImageHolder = findViewById(R.id.captured_image_b);
                        holders.add(capturedImageHolder);

                        DecimalFormat formateador = new DecimalFormat("000.000000");

                        dispatchTakePictureIntent("CONTROL_" +
                                nroEmpleado +
                                "_" +
                                fechaHoy.replace("-", "") +
                                "_" +
                                hora +
                                "_" +
                                formateador.format(Math.abs(Double.parseDouble(lblLatitud.getText().toString()))).replace(",", "") +
                                //lblLatitud.getText().toString().replace(".", "") +
                                "_" +
                                formateador.format(Math.abs(Double.parseDouble(lblLongitud.getText().toString()))).replace(",", "") +
                                "_OUT_");
                    } else {
                        Toast.makeText(getApplicationContext(), "Nao se puede gravar. Ativar o GPS.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        });

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

        File image = File.createTempFile(name, ".jpg", storageDir);
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}
