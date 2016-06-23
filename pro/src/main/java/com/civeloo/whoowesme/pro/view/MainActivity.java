package com.civeloo.whoowesme.pro.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.civeloo.whoowesme.pro.R;
import com.civeloo.whoowesme.pro.data.ClientDao;

/**
 * Created by DeG on 3/12/13.
 */
public final class MainActivity extends Activity {
    ImageButton bAdd, bSearch;
    Spinner spinner;
    String spId = "";
    String[] asItems;
    ArrayAdapter<String> aaAdapter;
    Boolean vOnLoad = true;
    // Creamos el cursor
    ClientDao cliService;

    //EditText etName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cliService = new ClientDao(this);

        bAdd = (ImageButton) findViewById(R.id.mainIBadd);
        spinner = (Spinner) findViewById(R.id.mainSPname);
        getDataToSpinner();

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdd();
            }
        });

        // Seleccionar Localidad
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long id) {
                        if (vOnLoad) vOnLoad = false;
                        else startQuery(asItems[position]);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        //startQuery("0");
                    }
                });

    }

    public void startQuery(String pos) {
        Intent i = new Intent(this, Search.class);
        i.putExtra("name", pos);
        startActivityForResult(i, 1);
    }

    public void startAdd() {
        Intent i = new Intent(this, Add.class);
        startActivityForResult(i, 2);
    }

    public void getDataToSpinner() {
        Cursor c = cliService.getDataTo();
        if (c.getCount() > 0) {
            // Creamos la lista
            asItems = new String[c.getCount() + 1];
            asItems[0] = this.getString(R.string.main_spinner);
            int i = 1;
            while (c.moveToNext()) {
                asItems[i] = new String(c.getString(1));
                i++;
            }
        } else {
            asItems = new String[1];
            asItems[0] = this.getString(R.string.main_spinner);
            startAdd();
        }
        c.close();
        vOnLoad = true;
        aaAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, asItems);
        aaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aaAdapter);
    }

    /**
     * TRAER PARAMETROS CUANDO VUELVE DE OTRA ACTIVITY *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_OK) startQuery(data.getStringExtra("result"));
                break;
            }
            case (2): {
                if (resultCode == Activity.RESULT_OK) startQuery(data.getStringExtra("result"));
                break;
            }
            default: {
                //getDataToSpinner();
                break;
            }
        }
        getDataToSpinner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
