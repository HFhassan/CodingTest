package com.smallworld;

import com.smallworld.data.Transaction;
import org.json.JSONException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionDataFetcherTest {

    private TransactionDataFetcher dataFetcher;


    // Sample JSON data for testing
    private final String transactionsJson = "[\n" +
            "  {\n" +
            "    \"mtn\": 663458,\n" +
            "    \"amount\": 430.2,\n" +
            "    \"senderFullName\": \"Tom Shelby\",\n" +
            "    \"senderAge\": 22,\n" +
            "    \"beneficiaryFullName\": \"Alfie Solomons\",\n" +
            "    \"beneficiaryAge\": 33,\n" +
            "    \"issueId\": 1,\n" +
            "    \"issueSolved\": false,\n" +
            "    \"issueMessage\": \"Looks like money laundering\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"mtn\": 1284564,\n" +
            "    \"amount\": 150.2,\n" +
            "    \"senderFullName\": \"Tom Shelby\",\n" +
            "    \"senderAge\": 22,\n" +
            "    \"beneficiaryFullName\": \"Arthur Shelby\",\n" +
            "    \"beneficiaryAge\": 60,\n" +
            "    \"issueId\": 2,\n" +
            "    \"issueSolved\": true,\n" +
            "    \"issueMessage\": \"Never gonna give you up\"\n" +
            "  }\n" +
            "]";

    @BeforeEach
    public void setup() throws JSONException {
        dataFetcher = new TransactionDataFetcher(transactionsJson);
    }

    @Test
    public void testGetTotalTransactionAmount() {
        double expectedTotal = 430.2 + 150.2;
        assertEquals(expectedTotal, dataFetcher.getTotalTransactionAmount(), 0.01);
    }

    @Test
    public void testGetTotalTransactionAmountSentBy() {
        String senderFullName = "Tom Shelby";
        double expectedTotal = 430.2 + 150.2;
        assertEquals(expectedTotal, dataFetcher.getTotalTransactionAmountSentBy(senderFullName), 0.01);
    }

    @Test
    public void testGetMaxTransactionAmount() {
        double expectedMax = 430.2;
        assertEquals(expectedMax, dataFetcher.getMaxTransactionAmount(), 0.01);
    }

    @Test
    public void testCountUniqueClients() {
        long expectedUniqueClients = 3;
        assertEquals(expectedUniqueClients, dataFetcher.countUniqueClients());
    }

    @Test
    public void testHasOpenComplianceIssues() {
        String clientWithOpenIssue = "Tom Shelby";
        String clientWithoutOpenIssue = "Alfie Solomons";
        assertTrue(dataFetcher.hasOpenComplianceIssues(clientWithOpenIssue));
        assertFalse(dataFetcher.hasOpenComplianceIssues(clientWithoutOpenIssue));
    }

    @Test
    public void testGetTransactionsByBeneficiaryName() {
        Map<String, Transaction> transactionsByBeneficiary = dataFetcher.getTransactionsByBeneficiaryName();
        assertEquals(4, transactionsByBeneficiary.size());
        assertNotNull(transactionsByBeneficiary.get("Alfie Solomons"));
        assertNotNull(transactionsByBeneficiary.get("Arthur Shelby"));
        assertNotNull(transactionsByBeneficiary.get("Aberama Gold"));
        assertNotNull(transactionsByBeneficiary.get("Ben Younger"));
    }
    @Test
    public void testGetUnsolvedIssueIds() {
        Set<Integer> expectedUnsolvedIssueIds = new HashSet<>(Arrays.asList(1));
        assertEquals(expectedUnsolvedIssueIds, dataFetcher.getUnsolvedIssueIds());
    }

    @Test
    public void testGetAllSolvedIssueMessages() {
        List<String> expectedSolvedIssueMessages = Arrays.asList("Never gonna give you up");
        assertEquals(expectedSolvedIssueMessages, dataFetcher.getAllSolvedIssueMessages());
    }

    @Test
    public void testGetTop3TransactionsByAmount() {
        List<Transaction> top3Transactions = dataFetcher.getTop3TransactionsByAmount();
        assertEquals(2, top3Transactions.size());
        assertEquals(430.2, top3Transactions.get(0).getAmount(), 0.01);
        assertEquals(150.2, top3Transactions.get(1).getAmount(), 0.01);
    }

    @Test
    public void testGetTopSender() {
        assertEquals("Tom Shelby", dataFetcher.getTopSender().orElse(""));
    }

}
