package uk.ac.hope.mcse.android.coursework;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.hope.mcse.android.coursework.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    // Temporary storage for expenses
    public static List<Expense> expenseList = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Category options
        String[] categories = {"Food", "Transport", "Rent", "Shopping", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(adapter);

        // Date picker
        binding.editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view1, year1, month1, dayOfMonth) -> {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                binding.editTextDate.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        // Save button
        binding.buttonSecond.setOnClickListener(v -> {
            String amountStr = binding.editTextAmount.getText().toString().trim();
            String category = binding.spinnerCategory.getSelectedItem().toString();
            String date = binding.editTextDate.getText().toString().trim();

            if (amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                Expense expense = new Expense(amount, category, date);
                expenseList.add(expense);
                Toast.makeText(requireContext(), "Expense saved", Toast.LENGTH_SHORT).show();

                // Navigate back to FirstFragment
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);

            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
