package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import uk.ac.hope.mcse.android.coursework.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    public static FirstFragment instance;

    private FragmentFirstBinding binding;
    private ExpenseAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instance = this;

        // ✅ Load saved income and expenses
        DataStorageHelper.loadData(requireContext());

        // Navigate to Add Expense screen
        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // Navigate to Add Income screen
        binding.buttonAddIncome.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_IncomeFragment)
        );

        // Set up RecyclerView with delete listener callback
        binding.recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(SecondFragment.expenseList, this::updateTotals);
        binding.recyclerViewExpenses.setAdapter(adapter);

        updateTotals();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateTotals();
    }

    public void updateTotals() {
        double totalSpent = 0;
        for (Expense e : SecondFragment.expenseList) {
            totalSpent += e.getAmount();
        }

        double totalIncome = IncomeFragment.totalIncome;
        double balance = totalIncome - totalSpent;

        binding.textViewTotalSpent.setText(String.format("Total Spent: £%.2f", totalSpent));
        binding.textViewTotalIncome.setText(String.format("Total Income: £%.2f", totalIncome));
        binding.textViewBalance.setText(String.format("Balance: £%.2f", balance));
    }

    // Called from MainActivity after data reset
    public void updateTotalsAndRefresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateTotals();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        instance = null;
    }
}
