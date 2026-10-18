package com.example.spendora;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etExpenseName, etAmount;
    Spinner spCategory;
    RadioGroup rgExpenseType;
    RadioButton rbNeed, rbWant;
    CheckBox cbEssential;
    Button btnSaveExpense, btnBack;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Connect XML Components
        etExpenseName = findViewById(R.id.etExpenseName);
        etAmount = findViewById(R.id.etAmount);

        spCategory = findViewById(R.id.spCategory);

        rgExpenseType = findViewById(R.id.rgExpenseType);
        rbNeed = findViewById(R.id.rbNeed);
        rbWant = findViewById(R.id.rbWant);

        cbEssential = findViewById(R.id.cbEssential);

        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnBack = findViewById(R.id.btnBack);

        // Connect SQLite Database
        db = new DBHelper(this);

        // Spinner Categories
        String[] categories = {"Food", "Transport", "Education", "Shopping", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategory.setAdapter(adapter);
        // Default Radio Button
        rbNeed.setChecked(true);
        // Save Expense Button
        btnSaveExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Get User Inputs
                String name = etExpenseName.getText().toString().trim();
                String amountText = etAmount.getText().toString().trim();

                String category = spCategory.getSelectedItem().toString();

                // Check Empty Fields
                if (name.isEmpty() || amountText.isEmpty()) {
                    Toast.makeText(AddExpenseActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Convert Amount to Double
                double amount;

                try {
                    amount = Double.parseDouble(amountText);

                } catch (NumberFormatException e) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate Amount
                if (!Double.isFinite(amount) || amount <= 0) {

                    Toast.makeText(AddExpenseActivity.this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get Expense Type
                String type;

                if (rbNeed.isChecked()) {
                    type = "Need";
                } else {
                    type = "Want";
                }

                // Get CheckBox Value
                boolean essential = cbEssential.isChecked();

                // Get Current Date
                String date = new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

                // Insert Data into SQLite Database
                boolean result = db.addExpense(
                        name,
                        amount,
                        category,
                        type,
                        essential,
                        date
                );
                // Check Insert Result
                if (result) {
                    Toast.makeText(AddExpenseActivity.this, "Expense saved successfully", Toast.LENGTH_SHORT).show();
                    // Return to Home Screen
                    finish();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Expense save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }
}