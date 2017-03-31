package com.example.tagaev_be.converterfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startConverterActivity(View view) {
        Intent intent = new Intent();

        int buttonID = view.getId();
        switch (buttonID) {
            case R.id.massButton:
                intent.setClass(this, ConverterActivity.class);
                intent.putExtra("CONVERSION_UNITS", R.array.massUnits);
                intent.putExtra("CONVERSION_FACTORS", R.array.massConversionFactors);
                startActivity(intent);
                break;
            case R.id.lengthButton:
                intent.setClass(this, ConverterActivity.class);
                intent.putExtra("CONVERSION_UNITS", R.array.lenghtUnits);
                intent.putExtra("CONVERSION_FACTORS", R.array.lengthConversionFactors);
                startActivity(intent);
                break;
            case R.id.currencyButton:
                intent.setClass(this, CurrencyActivity.class);
                startActivity(intent);
                break;
        }
        startActivity(intent);
    }
}
