package com.smallworld;

import com.smallworld.data.Transaction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class TransactionDataFetcher {
    private List<Transaction> transactions;
    public TransactionDataFetcher(String transactionsJson) throws JSONException {

    JSONArray jsonArray = new JSONArray(transactionsJson);
    transactions = new ArrayList<>();
        if (transactionsJson == null || transactionsJson.isEmpty()) {
            return; // Return an empty list if the JSON is null or empty
        }
        for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Transaction transaction = new Transaction(
                jsonObject.getLong("mtn"),
                jsonObject.getDouble("amount"),
                jsonObject.getString("senderFullName"),
                jsonObject.getInt("senderAge"),
                jsonObject.getString("beneficiaryFullName"),
                jsonObject.getInt("beneficiaryAge"),
                jsonObject.isNull("issueId") ? null : jsonObject.getInt("issueId"),
                jsonObject.getBoolean("issueSolved"),
                jsonObject.isNull("issueMessage") ? null : jsonObject.getString("issueMessage")
        );
        transactions.add(transaction);
    }
}
    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount() {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        return transactions.stream()
                .filter(transaction -> transaction.getSenderFullName().equals(senderFullName))
                .mapToDouble(Transaction::getAmount).sum();
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount() {
        return transactions.stream().mapToDouble(Transaction::getAmount).max().orElse(0);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {
        Set<String> uniqueClients = new HashSet<>();
        for (Transaction transaction : transactions) {
            uniqueClients.add(transaction.getSenderFullName());
            uniqueClients.add(transaction.getBeneficiaryFullName());
        }
        return uniqueClients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        return transactions.stream()
                .anyMatch(transaction ->
                        (transaction.getSenderFullName().equals(clientFullName) ||
                                transaction.getBeneficiaryFullName().equals(clientFullName))
                                && !transaction.isIssueSolved());
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, Transaction> getTransactionsByBeneficiaryName() {
        Map<String, Transaction> transactionsByBeneficiary = new HashMap<>();
        for (Transaction transaction : transactions) {
            transactionsByBeneficiary.put(transaction.getBeneficiaryFullName(), transaction);
        }
        return transactionsByBeneficiary;
    }
    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() {
        Set<Integer> unsolvedIssueIds = new HashSet<>();
        for (Transaction transaction : transactions) {
            if (!transaction.isIssueSolved() && transaction.getIssueId() != null) {
                unsolvedIssueIds.add(transaction.getIssueId());
            }
        }
        return unsolvedIssueIds;
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        List<String> solvedIssueMessages = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.isIssueSolved() && transaction.getIssueMessage() != null) {
                solvedIssueMessages.add(transaction.getIssueMessage());
            }
        }
        return solvedIssueMessages;
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    public List<Transaction> getTop3TransactionsByAmount() {
        transactions.sort(Comparator.comparingDouble(Transaction::getAmount).reversed());
        int top3 = Math.min(3, transactions.size());
        return transactions.subList(0, top3);
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    public Optional<String> getTopSender() {
        Map<String, Double> totalAmountSentBySender = new HashMap<>();
        for (Transaction transaction : transactions) {
            totalAmountSentBySender.put(transaction.getSenderFullName(),
                    totalAmountSentBySender.getOrDefault(transaction.getSenderFullName(), 0.0) + transaction.getAmount());
        }

        return totalAmountSentBySender.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }
}
