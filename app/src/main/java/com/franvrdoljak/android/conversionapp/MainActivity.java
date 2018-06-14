package com.franvrdoljak.android.conversionapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<Currency> CurrencyList = new ArrayList<>();

    private Spinner spinner_to, spinner_from;
    private TextView result_text, sellingResult_text;
    private Button submit_button;
    private EditText value_editText;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI 
        spinner_from = (Spinner) findViewById(R.id.spinner_from);
        spinner_to  = (Spinner) findViewById(R.id.spinner_to);
        result_text = (TextView) findViewById(R.id.result);
        submit_button = (Button) findViewById(R.id.button);
        value_editText = (EditText) findViewById(R.id.value);


        //Create progress dialog
        //dismiss jsonParse() -> onResponse
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait while data is loading...");
        progress.setCancelable(false);
        progress.show();

        //Create volley request queue
        mQueue = Volley.newRequestQueue(this);

        //Get data from API
        getData();

        //Click on submit button and get result
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get result
                if(spinner_from.getSelectedItemPosition() == 0 && spinner_to.getSelectedItemPosition() != 0){
                    result_text.setText(getResultWithSelling(getEnteredValue()));
                }else if(spinner_from.getSelectedItemPosition() != 0 && spinner_to.getSelectedItemPosition() == 0){
                    result_text.setText((getResultWithBuying(getEnteredValue())));
                }else{
                    result_text.setText(Double.toString(getEnteredValue()));
                }

            }
        });
        //spinner listener ----------------------------------
        spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(spinner_to.getSelectedItemPosition()!=0){
                    spinner_from.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(spinner_from.getSelectedItemPosition()!=0){
                    spinner_to.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }
    //Get data from JsonFile (from API), fill CurencyList, fill spinners
    private void getData() {
        //Url of API
        String url = "http://hnbex.eu/api/v1/rates/daily/?date=YYYY-MM-DD";

        //Request JsonArray from API
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        String currency_code;
                        int unit_rate;
                        String buying_rate;
                        String median_rate;
                        String selling_rate;

                        //ADD HRK currency
                        Currency currencyHrk = new Currency("HRK", 1, 1.0,1.0,1.0);
                        CurrencyList.add(currencyHrk);

                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                //Get Currency class attributes from json object
                                currency_code = jsonObject.getString("currency_code");
                                unit_rate = jsonObject.getInt("unit_value");
                                buying_rate = jsonObject.getString("buying_rate");
                                median_rate = jsonObject.getString("median_rate");
                                selling_rate = jsonObject.getString("selling_rate");
                                //ADD HRK currency
                                //Add currency to arrayList
                                Currency currency = new Currency(currency_code, unit_rate, Double.parseDouble(buying_rate), Double.parseDouble(median_rate),
                                        Double.parseDouble(selling_rate));
                                CurrencyList.add(currency);

                                //Set spinner with curreny_cod
                                setSpinner(spinner_from);
                                setSpinner(spinner_to);

                                //Dismiss progress dialog
                                progress.dismiss();

                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Error toast
                        Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Add request to request queue
        mQueue.add(request);

    }

    //Fills spinner with currencies
    private void setSpinner(Spinner spinner){
        //Fill arraylist from CurrencyList
        ArrayList<String> currencies_names = new ArrayList<>();
        for(Currency cur: CurrencyList){
            currencies_names.add(cur.getCurrency_code());
        }
        
        //ArrayAdapter for spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        currencies_names);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        
        //Set adapter to spinner 
        spinner.setAdapter(spinnerArrayAdapter);
    }

    //Calculates amount with buying rate
    private String getResultWithBuying(double iznos){
        Currency from_curency = CurrencyList.get(spinner_from.getSelectedItemPosition());
        Currency to_curency = CurrencyList.get(spinner_to.getSelectedItemPosition());
        double result = (Double) (iznos * from_curency.getBuyin_rate()) / from_curency.getUnit_value();
        String res = String.format("%.2f", result);
        return res + " " + to_curency.getCurrency_code();
    }
    //CalculateS amount with selling rate
    private String getResultWithSelling(double iznos){
        Currency to_curency = CurrencyList.get(spinner_to.getSelectedItemPosition());
        double result = (Double) ((iznos / to_curency.getSelling_rate())) * to_curency.getUnit_value();
        String res = String.format("%.2f", result);
        return res + " " + to_curency.getCurrency_code();
    }

    //Returns entered value
    private double getEnteredValue(){
            final String vaule = value_editText.getText().toString();
            if(vaule.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter value!", Toast.LENGTH_SHORT).show();
                return 0.0;
            }else {
                return Double.parseDouble(vaule);
            }
    }


}