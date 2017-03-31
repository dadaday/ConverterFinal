package com.example.tagaev_be.converterfinal;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

public class ConverterActivity extends AppCompatActivity {

    public static final String CONVERSION_RATIO_FACTORS = "CONVERSION_FACTORS";
    public static final String CONVERSION_RATIO_UNITS = "CONVERSION_UNITS";
    private TextWatcher firstWatcher;
    private TextWatcher secondWatcher;

    private EditText firstUnitEditText;
    private EditText secondUnitEditText;

    private Spinner firstUnitSpinner;
    private Spinner secondUnitSpinner;

    private TypedArray conversionRatioToCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        initializeUIItems();
        initializeWatchers();
        setListeners();
    }

    private void doCalculation(EditText fromEditText, Spinner fromSpinner,
                               EditText toEditText, Spinner toSpinner, TextWatcher toWatcher) {
        float value = 0.0f;
        try {
            value = Float.parseFloat(fromEditText.getText().toString());
        } catch (NumberFormatException ignored) {
            return;
        }

        int firstSelectedUnit = fromSpinner.getSelectedItemPosition();
        int secondSelectedUnit = toSpinner.getSelectedItemPosition();


        Intent intent = getIntent();
        int indexForSpinner = intent.getIntExtra(CONVERSION_RATIO_FACTORS, -1);
        conversionRatioToCalculate = getResources().obtainTypedArray(indexForSpinner);

        float from = conversionRatioToCalculate.getFloat(firstSelectedUnit, 0.0f);
        float to = conversionRatioToCalculate.getFloat(secondSelectedUnit, 0.0f);


        float result = value * from / to;

        toEditText.removeTextChangedListener(toWatcher);
        toEditText.setText(String.format(Locale.getDefault(), "%.2f", result));
        toEditText.addTextChangedListener(toWatcher);
    }

    private void initializeUIItems(){
        firstUnitEditText = (EditText)findViewById(R.id.firstUnitEditText);
        secondUnitEditText = (EditText)findViewById(R.id.secondUnitEditText);

        firstUnitSpinner = (Spinner)findViewById(R.id.firstUnitSpinner);
        secondUnitSpinner = (Spinner)findViewById(R.id.secondUnitSpinner);

        Intent intent = getIntent();
        int indexForSpinner = intent.getIntExtra(CONVERSION_RATIO_UNITS, -1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, indexForSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        firstUnitSpinner.setAdapter(adapter);
        secondUnitSpinner.setAdapter(adapter);

    }

    private  void initializeWatchers(){
        firstWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                doCalculation(firstUnitEditText, firstUnitSpinner, secondUnitEditText, secondUnitSpinner, secondWatcher);
            }
        };

        secondWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                doCalculation(secondUnitEditText, secondUnitSpinner, firstUnitEditText, firstUnitSpinner, firstWatcher);
            }
        };

        firstUnitEditText.addTextChangedListener(firstWatcher);
        secondUnitEditText.addTextChangedListener(secondWatcher);
    }

    private  void setListeners(){
        AdapterView.OnItemSelectedListener firstOnItemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        doCalculation(firstUnitEditText, firstUnitSpinner, secondUnitEditText, secondUnitSpinner, secondWatcher);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                };

        AdapterView.OnItemSelectedListener secondOnItemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        doCalculation(secondUnitEditText, secondUnitSpinner, firstUnitEditText, firstUnitSpinner, firstWatcher);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                };

        firstUnitSpinner.setOnItemSelectedListener(firstOnItemSelectedListener);
        secondUnitSpinner.setOnItemSelectedListener(secondOnItemSelectedListener);
    }
}

// put calculation logic to different class

