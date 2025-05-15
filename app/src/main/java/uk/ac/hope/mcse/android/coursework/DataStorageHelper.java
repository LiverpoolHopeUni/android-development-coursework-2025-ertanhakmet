package uk.ac.hope.mcse.android.coursework;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DataStorageHelper {

    private static final String FILE_NAME = "expenses_data.txt";

    public static void saveData(Context context, double totalIncome, List<Expense> expenseList) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

            // First line = income
            writer.write("INCOME:" + totalIncome);
            writer.newLine();

            // Expenses
            for (Expense e : expenseList) {
                writer.write(e.getAmount() + "|" + e.getCategory() + "|" + e.getDate());
                writer.newLine();
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load income and expenses
    public static void loadData(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            SecondFragment.expenseList = new ArrayList<>();
            IncomeFragment.totalIncome = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("INCOME:")) {
                    String incomeValue = line.replace("INCOME:", "").trim();
                    IncomeFragment.totalIncome = Double.parseDouble(incomeValue);
                } else {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        double amount = Double.parseDouble(parts[0]);
                        String category = parts[1];
                        String date = parts[2];
                        SecondFragment.expenseList.add(new Expense(amount, category, date));
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
