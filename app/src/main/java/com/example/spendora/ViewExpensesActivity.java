package com.example.spendora;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ViewExpensesActivity extends AppCompatActivity {

    // Declare Variables
    ListView listExpenses;
    TextView tvListTotal;
    Button btnViewBack;

    DBHelper db;

    ArrayList<String> dataList;
    ArrayList<Integer> expenseIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        // Connect XML Components
        listExpenses = findViewById(R.id.listExpenses);
        tvListTotal = findViewById(R.id.tvListTotal);
        btnViewBack = findViewById(R.id.btnViewBack);

        // Connect SQLite Database
        db = new DBHelper(this);

        // Initialize ArrayLists
        dataList = new ArrayList<>();
        expenseIds = new ArrayList<>();

        // Select Expense to Edit
        listExpenses.setOnItemClickListener(
                (parent, view, position, id) -> {

                    int expenseId = expenseIds.get(position);

                    Intent intent = new Intent(
                            ViewExpensesActivity.this,
                            EditExpenseActivity.class
                    );

                    intent.putExtra("expense_id", expenseId);

                    startActivity(intent);
                }
        );

        // Back Button
        btnViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh List When Returning from Edit Screen
        displayExpenses();
    }

    // Read Expenses from SQLite Database
    private void displayExpenses() {

        dataList.clear();
        expenseIds.clear();

        Cursor res = db.getAllExpenses();

        if (res.getCount() == 0) {

            Toast.makeText(
                    ViewExpensesActivity.this,
                    "No expenses found",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            while (res.moveToNext()) {

                int id = res.getInt(
                        res.getColumnIndexOrThrow(DBHelper.COL_ID)
                );

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

                String record = "Expense: " + name +
                        "\nAmount: Rs. " +
                        String.format(Locale.US, "%.2f", amount) +
                        "\nCategory: " + category +
                        "\nType: " + type;

                dataList.add(record);
                expenseIds.add(id);
            }
        }

        res.close();

        // Connect ArrayList to ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataList
        );

        listExpenses.setAdapter(adapter);

        // Update Total Spending
        double total = db.getTotalExpenses();

        tvListTotal.setText(
                String.format(
                        Locale.US,
                        "Total Spending: Rs. %.2f",
                        total
                )
        );
    }
}