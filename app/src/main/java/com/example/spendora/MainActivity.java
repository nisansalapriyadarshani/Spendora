package com.example.spendora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvTotalExpenses, tvTotalTransactions;
    Button btnAddExpense, btnViewExpenses;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect XML Components
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);

        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpenses = findViewById(R.id.btnViewExpenses);

        // Initialize SQLite Database
        dbHelper = new DBHelper(this);

        // Open Add Expense Screen
        btnAddExpense.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddExpenseActivity.class
            );

            startActivity(intent);
        });

        // Open View Expenses Screen
        btnViewExpenses.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ViewExpensesActivity.class
            );

            startActivity(intent);
        });
    }

    // Refresh Dashboard When Returning Home
    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    // Update Total Expenses and Transactions
    private void updateDashboard() {

        double total = dbHelper.getTotalExpenses();

        int transactions = dbHelper.getTransactionCount();

        tvTotalExpenses.setText(
                String.format(Locale.US, "Rs. %.2f", total)
        );

        tvTotalTransactions.setText(
                String.valueOf(transactions)
        );
    }
}