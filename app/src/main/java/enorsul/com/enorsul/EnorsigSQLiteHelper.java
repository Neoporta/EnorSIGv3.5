package enorsul.com.enorsul;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EnorsigSQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla
    String sqlCreate = "CREATE TABLE IF NOT EXISTS enorsig_app( acao TEXT, leitura2 TEXT, observacao TEXT," +
                                                            " estado TEXT, horavisita TEXT, geo TEXT," +
                                                            " foto1_gxi TEXT, foto2_gxi TEXT, foto3_gxi TEXT," +
                                                            " foto4_gxi TEXT, foto5_gxi TEXT, foto6_gxi TEXT," +
                                                            " foto7_gxi TEXT, VisCFum TEXT, VisDVis TEXT," +
                                                            " rgiNrgi TEXT, dcsNord TEXT, obs TEXT," +
                                                            " rgiCsab TEXT, nome TEXT, pariedade TEXT, onde TEXT," +
                                                            " ramal TEXT, assinatura_gxi TEXT, nome_cliente TEXT," +
                                                            " nro_dereita TEXT, nro_esquerda TEXT, telefone TEXT," +
                                                            " caract_imovel TEXT, nro_orden TEXT," +
                                                            " fis_sitimo TEXT, fis_sitsup TEXT, fis_tipsup TEXT," +
                                                            " fis_foraba TEXT, fis_repo TEXT," +
                                                            " PRIMARY KEY (VisCFum, VisDVis, rgiNrgi, dcsNord))";

    String sqlCreate2 = "CREATE TABLE IF NOT EXISTS tab_eventos( cod_eve TEXT, des_eve TEXT, show_eve TEXT, PRIMARY KEY (cod_eve))";

    String sqlCreate3 = "CREATE TABLE IF NOT EXISTS TAB_Rastreamento( Funcionario TEXT, Data TEXT, Hora TEXT," +
                                                                    " x DOUBLE, y DOUBLE, Estado TEXT, obs TEXT)";

    public EnorsigSQLiteHelper(Context contexto, String nombre, CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior,
                          int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente
        //      la opción de eliminar la tabla anterior y crearla de nuevo
        //      vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la
        //      tabla antigua a la nueva, por lo que este método debería
        //      ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS enorsig_app");
        db.execSQL("DROP TABLE IF EXISTS tab_eventos");
        db.execSQL("DROP TABLE IF EXISTS TAB_Rastreamento");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
    }

}
