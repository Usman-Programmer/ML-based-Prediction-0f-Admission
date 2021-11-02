package com.example.predictionmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText Metric, Inter, NTS;
    TextView Result;
    Button Predict;
    String url = "https://admissio-prediction.herokuapp.com/predict";
    Spinner spinnerInstitute, spinnerDepart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Inter = findViewById(R.id.inter);
//        Department = findViewById(R.id.department);
        NTS = findViewById(R.id.nts);
//        Institute = findViewById(R.id.institute);
        Predict = findViewById(R.id.predict);
        Result = findViewById(R.id.result);
        Metric = findViewById(R.id.metric);
        spinnerInstitute = findViewById(R.id.spinnerInst);
        spinnerDepart = findViewById(R.id.spinnerDepart);

        spinnerInstitute.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Predictor.institutes));
        ArrayAdapter<String> adapterSahiwal = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Predictor.departmentSahiwal);
        ArrayAdapter<String> adapterVehari = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Predictor.departmentVehari);

        spinnerInstitute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String codeInst = Predictor.instCode[spinnerInstitute.getSelectedItemPosition()];
                if(codeInst == "2"){
                    spinnerDepart.setAdapter(adapterSahiwal);
                } else if(codeInst == "3"){
                    spinnerDepart.setAdapter(adapterVehari);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result.setText("Checking from Model");
                // hit the API -> Volley
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String data = jsonObject.getString("ADMISSION");
                                    if (data.equals("1")) {
                                        Result.setText("Admission Hoga");
                                    } else {
                                        Result.setText("Admission Nahi Hoga");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() {

                        String codeDepart = null;
                        String codeInst = Predictor.instCode[spinnerInstitute.getSelectedItemPosition()];
                        if(codeInst == "2"){
                            codeDepart = Predictor.departmentSahiwalCode[spinnerDepart.getSelectedItemPosition()];
                        } else if(codeInst == "3"){
                            codeDepart = Predictor.departmentVehariCode[spinnerDepart.getSelectedItemPosition()];
                        }
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("METRIC", Metric.getText().toString());
                        params.put("INTER", Inter.getText().toString());
                        params.put("DEPARTMENT",codeDepart);
                        params.put("NTS", NTS.getText().toString());
                        params.put("INSTITUTE", codeInst);

                        return params;
                    }

                };
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(stringRequest);
            }
        });
    }
}