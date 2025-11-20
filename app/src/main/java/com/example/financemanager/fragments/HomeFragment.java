package com.example.financemanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financemanager.classes.CurrencyManager;
import com.example.financemanager.classes.TransactionData;
import com.example.financemanager.adapters.TransactionAdapter;
import com.example.financemanager.databinding.FragmentHomeBinding;
import com.example.financemanager.activities.TransactionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    TransactionAdapter adapter;
    List<TransactionData> transactionList = new ArrayList<>();

    FirebaseAuth auth;
    DatabaseReference transactionRef;
    DatabaseReference userRef;
    private ValueEventListener transactionListener;
    private ValueEventListener userListener;

    private CurrencyManager currencyManager;
    private String currencySymbol = "$";
    private double currencyRate = 1.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        if (getActivity() != null) currencyManager = new CurrencyManager(getActivity());
        updateCurrencyFromManager();

        auth = FirebaseAuth.getInstance();

        binding.fabMain.setOnClickListener(v -> {
            if (getActivity() != null)
                startActivity(new android.content.Intent(getActivity(), TransactionActivity.class));
        });

        setupRecyclerView();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(getContext(), transactionList);
        binding.recyclerRecentTransactions.setAdapter(adapter);
    }

    private void attachListener() {
        if (auth == null || auth.getCurrentUser() == null) return;

        if (transactionRef == null) {
            transactionRef = FirebaseDatabase.getInstance()
                    .getReference("Transactions")
                    .child(auth.getCurrentUser().getUid());
        }
        if (transactionListener == null) {
            transactionListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded() || getView() == null) return;
                    transactionList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TransactionData transaction = data.getValue(TransactionData.class);
                        if (transaction != null) transactionList.add(transaction);
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                    updateTotals();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded())
                        Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            };
            transactionRef.addValueEventListener(transactionListener);
        }

        if (userRef == null) {
            userRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(auth.getCurrentUser().getUid());
        }
        if (userListener == null) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    String username = snapshot.child("username").getValue(String.class);
                    if (username == null) username = " ";
                    binding.welcomeText.setText("Hi Welcome, " + username + " 👋");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            userRef.addValueEventListener(userListener);
        }
    }

    private void detachListener() {
        if (transactionRef != null && transactionListener != null) {
            transactionRef.removeEventListener(transactionListener);
            transactionListener = null;
        }
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
            userListener = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachListener();
        updateCurrencyFromManager();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachListener();
    }

    private void updateTotals() {
        double income = 0, expense = 0;
        for (TransactionData t : transactionList) {
            if (t != null) {
                double amount = t.getAmount();
                if ("income".equals(t.getType())) income += amount;
                else if ("expense".equals(t.getType())) expense += amount;
            }
        }

        double totalBalance = (income - expense) * currencyRate;

        binding.totalBalanceAmount.setText(currencySymbol + totalBalance);
        binding.earnedAmount.setText("+" + currencySymbol + income);
        binding.spentAmount.setText("-" + currencySymbol + expense);
    }

    private void updateCurrencyFromManager() {
        if (currencyManager != null) {
            currencySymbol = currencyManager.getCurrentSymbol();
            currencyRate = currencyManager.getCurrentRate();
            updateTotals();
        }
    }

    public void updateCurrency(String symbol, double rate) {
        currencySymbol = symbol;
        currencyRate = rate;
        updateTotals();
    }

    public void refreshTransactions() {
        detachListener();
        attachListener();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
