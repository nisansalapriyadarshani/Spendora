package com.example.spendora;

import android.database.Cursor;
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
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class EditExpenseActivity extends AppCompatActivity {

    // Declare Variables
    EditText etEditExpenseName, etEditAmount;
    Spinner spEditCategory;
    RadioGroup rgEditExpenseType;
    RadioButton rbEditNeed, rbEditWant;
    CheckBox cbEditEssential;

    Button btnUpdateExpense, btnDeleteExpense, btnEditBack;

    DBHelper db;

    int expenseId;

    String[] categories = {
            "Food",
            "Transport",
            "Education",
            "Shopping",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Connect XML Components
        etEditExpenseName = findViewById(R.id.etEditExpenseName);
        etEditAmount = findViewById(R.id.etEditAmount);

        spEditCategory = findViewById(R.id.spEditCategory);

        rgEditExpenseType = findViewById(R.id.rgEditExpenseType);
        rbEditNeed = findViewById(R.id.rbEditNeed);
        rbEditWant = findViewById(R.id.rbEditWant);

        cbEditEssential = findViewById(R.id.cbEditEssential);

        btnUpdateExpense = findViewById(R.id.btnUpdateExpense);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);
        btnEditBack = findViewById(R.id.btnEditBack);

        // Connect SQLite Database
        db = new DBHelper(this);

        // Get Selected Expense ID
        expenseId = getIntent().getIntExtra("expense_id", -1);

        if (expenseId == -1) {
            Toast.makeText(
                    this,
                    "Invalid expense",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Add Categories to Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spEditCategory.setAdapter(adapter);

        // Load Selected Expense
        loadExpense();

        // Update Button
        btnUpdateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etEditExpenseName.getText().toString().trim();
                String amountText = etEditAmount.getText().toString().trim();

                if (name.isEmpty() || amountText.isEmpty()) {

                    Toast.makeText(
                            EditExpenseActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                double amount;

                try {
                    amount = Double.parseDouble(amountText);

                } catch (NumberFormatException e) {

                    Toast.makeText(
                            EditExpenseActivity.this,
                            "Enter a valid amount",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (!Double.isFinite(amount) || amount <= 0) {

                    Toast.makeText(
                            EditExpenseActivity.this,
                            "Amount must be greater than zero",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                String category = spEditCategory.getSelectedItem().toString();

                String type;

                if (rbEditNeed.isChecked()) {
                    type = "Need";
                } else {
                    type = "Want";
                }

                boolean essential = cbEditEssential.isChecked();

                // Update SQLite Record
                boolean result = db.updateExpense(
                        expenseId,
                        name,
                        amount,
                        category,
                        type,
                        essential
                );

                if (result) {

                    Toast.makeText(
                            EditExpenseActivity.this,
                            "Expense updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            EditExpenseActivity.this,
                            "Expense update failed",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        // Delete Button
        btnDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(EditExpenseActivity.this)
                        .setTitle("Delete Expense")
                        .setMessage("Are you sure you want to delete this expense?")
                        .setPositiveButton("Delete", (dialog, which) -> {

                            boolean result = db.deleteExpense(expenseId);

                            if (result) {

                                Toast.makeText(
                                        EditExpenseActivity.this,
                                        "Expense deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();

                            } else {

                                Toast.makeText(
                                        EditExpenseActivity.this,
                                        "Expense delete failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Back Button
        btnEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Read Selected Expense from SQLite
    private void loadExpense() {

        Cursor res = db.getExpenseById(expenseId);

        if (res.moveToFirst()) {

            String name = res.getString(
                    res.getColumnIndexOrThrow(DBHelper.COL_NAME)
            );

            double amount = res.getDouble(
                    res.getColumnIndexOrThrow(DBHelper.COL_AMOUNT)
            );

            String category = res.getString(
                    res.getColumnIndexOrThrow(DBHelper.COL_CATEGORY)
            );

            String type = res.getString(
                    res.getColumnIndexOrThrow(DBHelper.COL_TYPE)
            );

            int essential = res.getInt(
                    res.getColumnIndexOrThrow(DBHelper.COL_ESSENTIAL)
            );

            // Display Existing Values
            etEditExpenseName.setText(name);
            etEditAmount.setText(String.valueOf(amount));

            // Select Saved Category
            for (int i = 0; i < categories.length; i++) {

                if (categories[i].equals(category)) {
                    spEditCategory.setSelection(i);
                    break;
                }
            }

            // Select Saved Expense Type
            if ("Need".equals(type)) {
                rbEditNeed.setChecked(true);
            } else {
                rbEditWant.setChecked(true);
            }

            // Set Essential Checkbox
            cbEditEssential.setChecked(essential == 1);

        } else {

            Toast.makeText(
                    this,
                    "Expense not found",
                    Toast.LENGTH_SHORT
            ).show();

            res.close();
            finish();
            return;
        }

        res.close();
    }
}