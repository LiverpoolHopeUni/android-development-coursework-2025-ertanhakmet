package uk.ac.hope.mcse.android.coursework;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import uk.ac.hope.mcse.android.coursework.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> {
            StringBuilder exportData = new StringBuilder();
            exportData.append("--- Expense Tracker Report ---\n\nExpenses:\n");

            for (Expense e : SecondFragment.expenseList) {
                exportData.append(String.format("£%.2f - %s (%s)\n", e.getAmount(), e.getCategory(), e.getDate()));
            }

            double totalSpent = 0;
            for (Expense e : SecondFragment.expenseList) {
                totalSpent += e.getAmount();
            }

            double totalIncome = IncomeFragment.totalIncome;
            double balance = totalIncome - totalSpent;

            exportData.append("\nTotal Spent: £").append(String.format("%.2f", totalSpent));
            exportData.append("\nTotal Income: £").append(String.format("%.2f", totalIncome));
            exportData.append("\nBalance: £").append(String.format("%.2f", balance));

            String fileName = "expense_report_" + System.currentTimeMillis() + ".txt";

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
                    values.put(MediaStore.Downloads.IS_PENDING, 1);

                    ContentResolver resolver = getContentResolver();
                    Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    Uri fileUri = resolver.insert(collection, values);

                    if (fileUri != null) {
                        try (OutputStream out = resolver.openOutputStream(fileUri)) {
                            out.write(exportData.toString().getBytes(StandardCharsets.UTF_8));
                        }
                        values.clear();
                        values.put(MediaStore.Downloads.IS_PENDING, 0);
                        resolver.update(fileUri, values, null, null);
                        Toast.makeText(this, "Exported to Downloads", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    File file = new File(getExternalFilesDir(null), fileName);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(exportData.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    Toast.makeText(this, "Saved to app storage:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            new AlertDialog.Builder(this)
                    .setTitle("Reset All Data")
                    .setMessage("Are you sure you want to delete all income and expenses?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SecondFragment.expenseList.clear();
                        IncomeFragment.totalIncome = 0;

                        // ✅ Save the cleared state to file
                        DataStorageHelper.saveData(this, 0, new ArrayList<>());

                        if (FirstFragment.instance != null) {
                            FirstFragment.instance.updateTotalsAndRefresh();
                        }

                        Toast.makeText(this, "Data reset successfully", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
