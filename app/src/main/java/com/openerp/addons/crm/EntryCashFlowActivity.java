package com.openerp.addons.crm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.MainActivity;
import com.openerp.R;
import com.openerp.orm.OEDBHelper;
import com.openerp.orm.OEDataRow;
import com.openerp.orm.OEHelper;
import com.openerp.orm.OEValues;
import com.openerp.util.logger.OELog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryCashFlowActivity extends Activity {

    private Spinner spinner1, spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("typeText");

        setTitle(message);
        setContentView(R.layout.activity_entry_cash_flow);


        EditText editText = (EditText)findViewById(R.id.typeEditText);
        editText.setText(message);

        addItemsOnSpinner();
        //EditText categoryEditText = (EditText)findViewById(R.id.categoryEditText);
        //categoryEditText.setText(intent.getStringExtra("categoryText"), TextView.BufferType.EDITABLE);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry_cash_flow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createSampleData(View view) {
        //db().truncateTable();
        EditText amount = (EditText) findViewById(R.id.amountEditText);
        EditText type = (EditText) findViewById(R.id.typeEditText);
        //EditText category = (EditText) findViewById(R.id.categoryEditText);
        Spinner category = (Spinner) findViewById(R.id.categorySpinner);
        EditText description = (EditText) findViewById(R.id.titleEditText);

        //for (int i = 1; i <= 2; i++) {
        String catval = category.getSelectedItem().toString();


            CashFlowDB db = new CashFlowDB(this);

            //OEHelper oe = db.getOEInstance();

            CashFlowDB.CashFlowCateg categdb = db.new CashFlowCateg(this);
            int id=1;
            //String[] whereArgs = new String[] { "Income" };
            //String where = "name";
            //List<OEDataRow> lcateg = categdb.select(where, whereArgs, null,null,null);

            for(OEDataRow row : categdb.select()){
                if(row.getString("name").equals(catval))
                {
                    id = row.getInt("id");
                }
            }

            OEValues values = new OEValues();
            values.put("category", id);
            values.put("description", description.getText().toString());
            values.put("amount", amount.getText().toString());
            values.put("type", type.getText().toString());


            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            //System.out.println(dateFormat.format(date));

            values.put("date",dateFormat.format(date));
            values.put("status", "not sync");
            values.put("id", db.lastId()+1);

            long newId =    db.create(values);//oe.create(values);



            Log.d("Insert new data Cash Flow", newId + " Record created for kas_bag");
        //}
            Toast toast = Toast.makeText(this, "Transaksi tersimpan!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

    }

    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        spinner2 = (Spinner) findViewById(R.id.categorySpinner);
        CashFlowDB db = new CashFlowDB(this);
        CashFlowDB.CashFlowCateg categdb = db.new CashFlowCateg(this);
        List<String> list = new ArrayList<String>();
        List<OEDataRow> lcateg = categdb.select();
        for(OEDataRow row : lcateg){
            int i = row.getInt("id");
            list.add(row.getString("name"));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }
}
