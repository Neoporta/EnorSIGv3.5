package enorsul.com.enorsul;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import enorsul.com.enorsul.adapters.OrdenAdapter;
import enorsul.com.enorsul.models.OrdenModelo;

public class OrdenesFaltantesFragment extends Fragment {

    RecyclerView recyclerViewOrden;
    OrdenAdapter adaptadorOrden;
    List<OrdenModelo> ordenes;
    GridLayoutManager ordenesLayoutManager;
    String nroEmpleado, fechaHoy;

    SQLiteDatabase db;

    public OrdenesFaltantesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ordenes_faltantes, container, false);

        EnorsigSQLiteHelper usdbh = new EnorsigSQLiteHelper(getContext(), "DBEnorsig", null, 19);
        db = usdbh.getReadableDatabase();

        nroEmpleado = getArguments().getString("nroEmpleado");
        fechaHoy = getArguments().getString("fechaHoy");

        ordenes = new ArrayList<>();
        recyclerViewOrden = view.findViewById(R.id.ordenesRecyclerView);;
        adaptadorOrden = new OrdenAdapter();
        ordenesLayoutManager = new GridLayoutManager(getActivity(),1);
        adaptadorOrden.setOrdenes(ordenes);
        recyclerViewOrden.setAdapter(adaptadorOrden);
        recyclerViewOrden.setLayoutManager(ordenesLayoutManager);

        ordenes = obtenerOrdenesBD(nroEmpleado, fechaHoy);
        //ordenes = NewsApp.getInstance().getService().getAllOrdenes();

        adaptadorOrden.setOrdenes(ordenes);
        adaptadorOrden.notifyDataSetChanged();

        return view;
    }

    public List<OrdenModelo> obtenerOrdenesBD(String nroEmpleado, String fecha) {
        List<OrdenModelo> orden = new ArrayList<>();
        try {

            String sql = "SELECT t1.VisCFum" +
                    "  ,t1.VisDVis" +
                    "  ,t1.rgiNrgi" +
                    "  ,t1.dcsNord" +
                    "  ,t1.rgiCsab" +
                    "  ,IFNULL(estado, 0) estado" +
                    "  ,Nome" +
                    "  ,obs" +
                    "  ,pariedade" +
                    "  ,nro_orden" +
                    "  FROM [enorsig_app] t1" +
                    "  where 1 = 1" +
                    "  AND IFNULL(estado, '0') = '0' " +
                    "  AND t1.VisCFum = '"+nroEmpleado+"'" +
                    "  and VisDVis = '" +fecha+ "' ORDER BY CAST(nro_orden as integer)";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {

                    orden.add(new OrdenModelo(cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(7),
                            cursor.getString(5),
                            cursor.getString(6),
                            nroEmpleado,
                            fecha,
                            "",
                            cursor.getString(8),
                            cursor.getString(9)));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return orden;
    }

}
