package com.example.financemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.classes.TransactionData;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<TransactionData> transactionList;
    private Context context;

    public TransactionAdapter(Context context, List<TransactionData> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, date, amount;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.transactionIcon);
            title = itemView.findViewById(R.id.transactionTitle);
            date = itemView.findViewById(R.id.transactionDate);
            amount = itemView.findViewById(R.id.transactionAmount);
        }
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        bindTransaction(transactionList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    private void bindTransaction(TransactionData transaction, TransactionViewHolder holder) {
        holder.title.setText(transaction.getCategory());
        holder.date.setText(transaction.getDate());

        if (transaction.getType().equals("income")) {
            holder.icon.setImageResource(R.drawable.income_icon);
            holder.amount.setText("+$" + transaction.getAmount());
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorIncome));
        } else {
            holder.icon.setImageResource(R.drawable.expense_icon);
            holder.amount.setText("-$" + transaction.getAmount());
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorExpense));
        }
    }
}
