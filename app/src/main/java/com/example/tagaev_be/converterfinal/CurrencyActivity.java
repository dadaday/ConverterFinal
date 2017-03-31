package com.example.tagaev_be.converterfinal;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class CurrencyActivity extends AppCompatActivity {

    public static final String CURRENCY_RATIOS_JSON = "currency_ratios.json";

    // declaration of EditTexts, Spinners, TextWatcher and JsonObject
    private EditText firstCurrencyEditText;
    private EditText secondCurrencyEditText;
    private EditText firstCustomConversionRatioEditText;
    private EditText secondCustomConversionRatioEditText;

    private TextWatcher firstEditTextWatcher;
    private TextWatcher secondEditTextWatcher;
    private TextWatcher firstCustomConversionRatioTextWatcher;
    private TextWatcher secondCustomConversionRatioTextWatcher;

    private Spinner firstCurrencySpinner;
    private Spinner secondCurrencySpinner;

    private JSONObject conversionRatios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        // assigning them by ID
        firstCurrencyEditText = (EditText)findViewById(R.id.firstCurrencyEditText);
        secondCurrencyEditText = (EditText)findViewById(R.id.secondCurrencyEditText);
        firstCustomConversionRatioEditText = (EditText)findViewById(R.id.firstCustomConversionRatioEditText);
        secondCustomConversionRatioEditText = (EditText)findViewById(R.id.secondCustomConversionRatioEditText);
        firstCurrencySpinner =(Spinner)findViewById(R.id.firstCurrencySpinner);
        secondCurrencySpinner =(Spinner)findViewById(R.id.secondCurrencySpinner);

        populateSpinner();
        setupListeners();
        loadConversionRatios();
    }

    // Adapters help to tell spinners how to show up, what to list inside
    private void populateSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this, R.array.currencies,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        firstCurrencySpinner.setAdapter(adapter);
        secondCurrencySpinner.setAdapter(adapter);
    }

    // Listeners are used to collect data that have been done
    private  void setupListeners() {
        setupSpinnerListeners();
        setupConversionRatioEditTextListener(firstCustomConversionRatioEditText, firstCustomConversionRatioTextWatcher,
                firstCurrencySpinner, secondCurrencySpinner);
        setupConversionRatioEditTextListener(secondCustomConversionRatioEditText, secondCustomConversionRatioTextWatcher,
                secondCurrencySpinner, firstCurrencySpinner);
        initializeWatchers();
    }

    // spinner's item might change, so we have to recalculate
    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener onFirstItemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        calculate(firstCurrencyEditText, firstCurrencySpinner, secondCurrencyEditText, secondCurrencySpinner, secondEditTextWatcher);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                };
        AdapterView.OnItemSelectedListener onSecondItemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        calculate(secondCurrencyEditText, secondCurrencySpinner, firstCurrencyEditText, firstCurrencySpinner, firstEditTextWatcher);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                };
        firstCurrencySpinner.setOnItemSelectedListener(onFirstItemSelectedListener);
        secondCurrencySpinner.setOnItemSelectedListener(onSecondItemSelectedListener);
    }

    // gets entered numbers from EditText and CustomRatio field
    private void setupConversionRatioEditTextListener(EditText conversionRatioEditText, TextWatcher conversionRatioTextWatcher,
                                                      final Spinner fromSpinner, final Spinner toSpinner) {
        conversionRatioTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                double value = 0.0;
                try {
                    value = Double.parseDouble(s.toString());
                } catch (NumberFormatException ignored) {
                    return;
                }

                String firstSelectedUnit = fromSpinner.getSelectedItem().toString();
                String secondSelectedUnit = toSpinner.getSelectedItem().toString();

                String currencyPair = firstSelectedUnit + " - " + secondSelectedUnit;

                try {
                    conversionRatios.put(currencyPair, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        conversionRatioEditText.addTextChangedListener(conversionRatioTextWatcher);
    }

    private  void initializeWatchers(){
        firstEditTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculate(firstCurrencyEditText, firstCurrencySpinner, secondCurrencyEditText, secondCurrencySpinner, secondEditTextWatcher);
            }
        };

        secondEditTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculate(secondCurrencyEditText, secondCurrencySpinner, firstCurrencyEditText, firstCurrencySpinner, firstEditTextWatcher);
            }
        };

        firstCurrencyEditText.addTextChangedListener(firstEditTextWatcher);
        secondCurrencyEditText.addTextChangedListener(secondEditTextWatcher);
    }


    // is calculate or recalculate values
    private  void calculate(EditText fromEditText, Spinner fromSpinner,
                            EditText toEditText, Spinner toSpinner, TextWatcher toWatcher) {
        double value;
        try {
            value = Double.parseDouble(fromEditText.getText().toString());
        } catch (NumberFormatException ignored) {
            return;
        }

        double ratio = getConversionRatio(fromSpinner, toSpinner);
        double result = value * ratio;

        toEditText.removeTextChangedListener(toWatcher);
        toEditText.setText(
                String.format(
                        Locale.getDefault(), "%.2f", result
                )
        );
        toEditText.addTextChangedListener(toWatcher);
    }

    // gets a ratio from json for a specific currencies
    private double getConversionRatio(Spinner fromSpinner, Spinner toSpinner) {
        String firstSelectedUnit = fromSpinner.getSelectedItem().toString();

        String secondSelectedUnit = toSpinner.getSelectedItem().toString();

        String currencyPair = firstSelectedUnit + " - " + secondSelectedUnit;
        return conversionRatios.optDouble(
                currencyPair, 1.0f
        );
    }

    // fills a field with ratio that can be changed later by users
    private  void setConversionRatioText(EditText conversionRatioEditText, TextWatcher conversionRatioTextWatcher, double ratio) {
        conversionRatioEditText.removeTextChangedListener(conversionRatioTextWatcher);
        conversionRatioEditText.setText(String.valueOf(ratio));
        conversionRatioEditText.addTextChangedListener(conversionRatioTextWatcher);
    }

    // this loads our json file, so we can use it
    private void loadConversionRatios() {
        InputStream inputStream = null;
        File conversionRatiosFile;
        if ((conversionRatiosFile = getFileStreamPath(CURRENCY_RATIOS_JSON)).exists()) {
            try {
                inputStream = new FileInputStream(conversionRatiosFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            inputStream = getResources().openRawResource(R.raw.currency_ratios);
        }

        String jsonFileContent = "";

        if(inputStream != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonFileContent = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            conversionRatios = new JSONObject(jsonFileContent);
        } catch (JSONException e) {
            e.printStackTrace();
            conversionRatios = new JSONObject();
            setConversionRatioText(firstCustomConversionRatioEditText, firstCustomConversionRatioTextWatcher,
                    getConversionRatio(firstCurrencySpinner, secondCurrencySpinner));
            setConversionRatioText(secondCustomConversionRatioEditText, secondCustomConversionRatioTextWatcher,
                    getConversionRatio(secondCurrencySpinner, firstCurrencySpinner));
        }
    }

    //built in function to store conversion ratios
    @Override
    protected void onStop() {
        super.onStop();
        saveConversionRatios();
    }

    private void saveConversionRatios() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(CURRENCY_RATIOS_JSON, Context.MODE_PRIVATE);
            fileOutputStream.write(conversionRatios.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

