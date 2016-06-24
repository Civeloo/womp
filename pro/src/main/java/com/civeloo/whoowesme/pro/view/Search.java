package com.civeloo.whoowesme.pro.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.civeloo.whoowesme.pro.R;
import com.civeloo.whoowesme.pro.data.ClientDao;
import com.civeloo.whoowesme.pro.data.DebtorDao;
import com.civeloo.whoowesme.pro.logic.Debtor;

import static com.civeloo.whoowesme.pro.logic.Util.round;

public class Search extends Activity {
    String vName;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Button bPaid = (Button) findViewById(R.id.listBpaid);
        ImageButton bDelete = (ImageButton) findViewById(R.id.listBdelete);
        Button bLend = (Button) findViewById(R.id.listBlend);
        TextView tvTitle = (TextView) findViewById(R.id.listTVtitle);
        TableLayout tabla = (TableLayout) findViewById(R.id.listTableLayout);
        TextView tvTotal = (TextView) findViewById(R.id.listTVtotal);
        TextView tvTdebit = (TextView) findViewById(R.id.listTVtotalDebit);
        TextView tvTcredit = (TextView) findViewById(R.id.listTVtotalCredit);

        Bundle bundle = getIntent().getExtras();
        vName = bundle.getString("name");

        // Array para los totales
        double valores_totales[] = {0, 0, 0};

        //datos
        tvTitle.setText(vName);
        DebtorDao debService = new DebtorDao(this);
        Cursor cur = debService.getCbPk(vName);
        if (cur.moveToFirst()) {
            do {
                TableRow dato = new TableRow(this);
                dato.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                dato.setGravity(Gravity.CENTER_HORIZONTAL);
                tabla.addView(dato);
                for (int i = 2; i < 5; i++) {
                    TextView text = new TextView(this);
                    text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    text.setText(String.valueOf(cur.getString(i)));
                    text.setGravity(Gravity.CENTER_HORIZONTAL);
                    text.setPadding(5, 5, 5, 5);
                    dato.addView(text);
                    if (i > 2) {
                        valores_totales[i - 2] = round(valores_totales[i - 2] + Double.parseDouble(cur.getString(i)));
                    }
                }
            } while (cur.moveToNext());
        }

        tvTotal.setText(String.valueOf(round(Double.valueOf(valores_totales[1] - valores_totales[2]))));
        tvTdebit.setText(String.valueOf(round(Double.valueOf(valores_totales[1]))));
        tvTcredit.setText(String.valueOf(round(Double.valueOf(valores_totales[2]))));

        bPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alert(1);
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DebtorDao debService = new DebtorDao(Search.this);
                Debtor debtor = new Debtor();
                debService.delete(null,vName);
                ClientDao cliService = new ClientDao(Search.this);
                cliService.delete(vName);
                exit();
            }
        });

        bLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alert(2);
            }
        });
    }

    public void alert(final Integer t) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(Search.this);
        View promptsView = li.inflate(R.layout.value, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Search.this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.valueETvalor);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(this.getString(R.string.value_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                process(Double.parseDouble(userInput.getText().toString()), t);
                            }
                        })
                .setNegativeButton(this.getString(R.string.value_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void process(Double v, Integer t) {
        DebtorDao debService = new DebtorDao(Search.this);
        Debtor debtor = new Debtor();
        debtor.setClient(vName);

        switch (t) {
            case 1:
                debtor.setCredit(v);
                debtor.setDebit(0.00);
                break;
            case 2:
                debtor.setCredit(0.00);
                debtor.setDebit(v);
                break;
            default:
                debtor.setCredit(0.00);
                debtor.setDebit(0.00);
                break;
        }

        debService.insert(debtor);
        exit();
    }

    public void exit() {
        // En la Activity llamada utilizamos esto para devolver el parametro
//        Intent resultIntent;
//        resultIntent = new Intent();
//        resultIntent.putExtra("result", vName);
//        resultIntent.putExtra("result", "");
//        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}