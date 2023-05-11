package com.example.planatrip.fragments;

import static com.example.planatrip.MyDatabaseHelper.COLUMN_EXPENSE_STRING;
import static com.example.planatrip.MyDatabaseHelper.COLUMN_EXPENSE_VALUE;
import static com.example.planatrip.MyDatabaseHelper.COLUMN_ID;
import static com.example.planatrip.MyDatabaseHelper.TABLE_NAME3;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.planatrip.BudgetObject;
import com.example.planatrip.MyDatabaseHelper;
import com.example.planatrip.R;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment {
    // Declare any variables or views that will be used in the fragment here
    private TextView totalBudgetTextView;
    private TextView totalExpensesTextView;
    private EditText expenseNameEditText;
    private EditText expensePriceEditText;
    private Button addExpenseButton;
    private ListView expensesListView;
    private ArrayList<String> expensesList;
    private ArrayAdapter<String> expensesAdapter;
    private double totalExpenses = 0.0;
    private MyDatabaseHelper mDbHelper;
    int currenttripID = 0;
    List<BudgetObject> budgetObjects = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize any variables or views here
        totalExpensesTextView = view.findViewById(R.id.total_expenses_text_view);
        expenseNameEditText = view.findViewById(R.id.expense_name_edit_text);
        expensePriceEditText = view.findViewById(R.id.expense_price_edit_text);
        addExpenseButton = view.findViewById(R.id.add_expense_button);
        expensesListView = view.findViewById(R.id.expenses_list_view);
        expensesList = new ArrayList<>();
        expensesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, expensesList);
        expensesListView.setAdapter(expensesAdapter);
        mDbHelper = new MyDatabaseHelper(getActivity());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor;
        String[] projection = {
                MyDatabaseHelper.COLUMN_CURRENTTRIP_ID
        };
        cursor = db.query(
                MyDatabaseHelper.TABLE_NAME2,
                projection,
                COLUMN_ID + "=?",
                new String[]{"0"},
                null,
                null,
                null
        );
        cursor.moveToNext();
        currenttripID = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_CURRENTTRIP_ID));

        getFromDB();
        for(int i = 0; i < budgetObjects.size(); i++ )
        {
            expensesList.add(budgetObjects.get(i).getExpenseName() + " €" + budgetObjects.get(i).getExpenseValue());
        }

        // Set up any event listeners here
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                Cursor cursor;
                String expenseName = expenseNameEditText.getText().toString().trim();
                double expensePrice = Double.parseDouble(expensePriceEditText.getText().toString());
                String expenseString = expenseName + " €" + String.format("%.2f", expensePrice);
                //expensesList.add(expenseString);
                contentValues.put("expense_string", expenseName);
                contentValues.put("expense_value", expensePrice);
                contentValues.put("trip_id", currenttripID);
                db.insert(TABLE_NAME3, null, contentValues);

                expensesList.clear();

                expensesAdapter.notifyDataSetChanged();
                expenseNameEditText.setText("");
                expensePriceEditText.setText("");
                totalExpenses += expensePrice;
                refreshFragment();

            }
        });

        expensesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.Edit_or_Delete_Expense))
                        .setItems(new CharSequence[]{getString(R.string.Edit), getString(R.string.Delete)}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Edit expense
                                        BudgetObject item = budgetObjects.get(position);
                                        AlertDialog.Builder editExpenseDialogBuilder = new AlertDialog.Builder(getActivity());
                                        LayoutInflater inflater = getLayoutInflater();
                                        View dialogView = inflater.inflate(R.layout.edit_expense_dialog, null);
                                        editExpenseDialogBuilder.setView(dialogView);
                                        final EditText expenseNameEditText = dialogView.findViewById(R.id.edit_expense_name_edittext);
                                        final EditText expensePriceEditText = dialogView.findViewById(R.id.edit_expense_value_edittext);
                                        expenseNameEditText.setText(item.getExpenseName());
                                        expensePriceEditText.setText(item.getExpenseValue());
                                        editExpenseDialogBuilder.setPositiveButton(getString(R.string.Save), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String[] selectionArgs = {String.valueOf(item.getId())};
                                                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                                ContentValues contentValues = new ContentValues();
                                                String expenseName = expenseNameEditText.getText().toString().trim();
                                                double expensePrice = Double.parseDouble(expensePriceEditText.getText().toString());
                                                contentValues.put(COLUMN_EXPENSE_STRING, expenseName);
                                                contentValues.put(COLUMN_EXPENSE_VALUE, expensePrice);
                                                db.update(TABLE_NAME3, contentValues, COLUMN_ID + "=?", selectionArgs);
                                                getFromDB();
                                                expensesList.clear();
                                                expensesAdapter.notifyDataSetChanged();
                                                String toast = getString(R.string.Updated_Expense);
                                                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(getActivity(), "Expense updated", Toast.LENGTH_SHORT).show();
                                                refreshFragment();
                                            }
                                        });
                                        editExpenseDialogBuilder.setNegativeButton(getString(R.string.Cancel), null);
                                        AlertDialog editExpenseDialog = editExpenseDialogBuilder.create();
                                        editExpenseDialog.show();
                                        break;
                                    case 1:
                                        // Delete expense
                                        BudgetObject item1 = budgetObjects.get(position);
                                        String[] selectionArgs1 = {String.valueOf(item1.getId())};
                                        SQLiteDatabase db1 = mDbHelper.getWritableDatabase();
                                        db1.delete(TABLE_NAME3, COLUMN_ID + "=?", selectionArgs1);
                                        getFromDB();
                                        expensesList.clear();
                                        expensesAdapter.notifyDataSetChanged();
                                        refreshFragment();
                                        String toast = getString(R.string.Deleted_Expense);
                                        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getActivity(), "Expense deleted", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        return view;
    }

    private void getFromDB(){

        double totalExpenses = 0;
        SQLiteDatabase db_readable = mDbHelper.getReadableDatabase();
        String[] projection = {
                COLUMN_EXPENSE_STRING,
                COLUMN_EXPENSE_VALUE,
                COLUMN_ID
        };

        Cursor cursor2 = db_readable.query(
                TABLE_NAME3,
                projection,
                MyDatabaseHelper.COLUMN_TRIP_ID + "=?",
                new String[]{String.valueOf(currenttripID)},
                null,
                null,
                null
        );
        //budgetObjects.clear();
        while(cursor2.moveToNext()){
            int id = cursor2.getInt(cursor2.getColumnIndexOrThrow(COLUMN_ID));
            String expenseName = cursor2.getString(cursor2.getColumnIndexOrThrow(COLUMN_EXPENSE_STRING));
            String expenseValue = cursor2.getString(cursor2.getColumnIndexOrThrow(COLUMN_EXPENSE_VALUE));
            BudgetObject budgetObject = new BudgetObject(expenseName,expenseValue,id);
            budgetObjects.add(budgetObject);
        }
        cursor2.close();
        db_readable.close();
        for (BudgetObject budgetObject : budgetObjects) {
            totalExpenses += Double.parseDouble(budgetObject.getExpenseValue());
        }
        expensesAdapter.notifyDataSetChanged();
        totalExpensesTextView.setText(String.format("%.2f", totalExpenses));
    }

    private void refreshFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BudgetFragment budgetFragment = new BudgetFragment();

        fragmentTransaction.replace(R.id.framgent_container, budgetFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
