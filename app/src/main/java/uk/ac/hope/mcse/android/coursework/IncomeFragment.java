package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import uk.ac.hope.mcse.android.coursework.databinding.FragmentIncomeBinding;

public class IncomeFragment extends Fragment {

    public static double totalIncome = 0;

    private FragmentIncomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIncomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSaveIncome.setOnClickListener(v -> {
            String input = binding.editTextIncomeAmount.getText().toString().trim();

            if (input.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    Toast.makeText(getContext(), "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }

                totalIncome += amount;

                // ✅ Save updated income and expenses
                DataStorageHelper.saveData(requireContext(), totalIncome, SecondFragment.expenseList);

                Toast.makeText(getContext(), "Income added successfully", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(IncomeFragment.this)
                        .navigate(R.id.action_IncomeFragment_to_FirstFragment);

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
