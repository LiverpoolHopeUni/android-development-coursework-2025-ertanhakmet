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

        // Navigate to Add Expense screen when button is clicked
        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // Set up RecyclerView to display expenses
        binding.recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(SecondFragment.expenseList);
        binding.recyclerViewExpenses.setAdapter(adapter);

        // Show the current total
        updateTotalSpent();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list and total every time the screen comes back into focus
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateTotalSpent();
    }

    private void updateTotalSpent() {
        double total = 0;
        for (Expense e : SecondFragment.expenseList) {
            total += e.getAmount();
        }
        binding.textViewTotalSpent.setText(String.format("Total Spent: £%.2f", total));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
