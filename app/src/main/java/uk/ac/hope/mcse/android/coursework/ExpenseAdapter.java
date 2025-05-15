package uk.ac.hope.mcse.android.coursework;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private OnExpenseDeletedListener deleteListener;

    // Constructor with delete callback
    public ExpenseAdapter(List<Expense> expenseList, OnExpenseDeletedListener listener) {
        this.expenseList = expenseList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.amountText.setText(String.format("£%.2f", expense.getAmount()));
        holder.categoryText.setText(expense.getCategory());
        holder.dateText.setText(expense.getDate());

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        expenseList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, expenseList.size());

                        // ✅ Save updated data after deleting
                        DataStorageHelper.saveData(
                                holder.itemView.getContext(),
                                IncomeFragment.totalIncome,
                                expenseList
                        );

                        // Trigger callback to update totals
                        if (deleteListener != null) {
                            deleteListener.onExpenseDeleted();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, categoryText, dateText;
        Button deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.textView_amount);
            categoryText = itemView.findViewById(R.id.textView_category);
            dateText = itemView.findViewById(R.id.textView_date);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }

    public interface OnExpenseDeletedListener {
        void onExpenseDeleted();
    }
}
