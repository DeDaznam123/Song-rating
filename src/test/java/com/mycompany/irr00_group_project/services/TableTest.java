package com.mycompany.irr00_group_project.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Table service.
 */
public class TableTest {
    private static final String[] HEADERS = { "id", "username", "email", "avatar" };
    private static final String TEST_FILE_PATH = 
        "src/test/resources/temporary-test-files/users.csv";

    private Table table;

    @BeforeEach
    private void setUpTable() throws IllegalArgumentException, IOException {
        // Ensure the directory exists before creating the table
        File testFile = new File(TEST_FILE_PATH);
        File parentDir = testFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        this.table = new Table(HEADERS, TEST_FILE_PATH);
    }

    @AfterEach
    private void cleanupTableFile() {
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }

        // Also clean up the temporary directory if it's empty
        File directory = new File("temporary-test-files");
        if (directory.exists() && directory.isDirectory()) {
            String[] files = directory.list();
            if (files == null || files.length == 0) {
                directory.delete();
            }
        }
    }

    @Test
    public void testAddLineAndGetAllLines() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        String[] user2 = { "bob", "bob@example.com", "avatar2.png" };
        table.addLine(user1);
        table.addLine(user2);

        List<Map<String, String>> all = table.getAllLines();
        assertEquals(2, all.size());
        assertEquals("alice", all.get(0).get("username"));
        assertEquals("bob", all.get(1).get("username"));
        cleanupTableFile();
    }

    @Test
    public void testGetLines() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        String[] user2 = { "bob", "bob@example.com", "avatar2.png" };
        table.addLine(user1);
        table.addLine(user2);

        List<Map<String, String>> found = table.getLines("username", "bob");
        assertEquals(1, found.size());
        assertEquals("bob@example.com", found.get(0).get("email"));
        cleanupTableFile();
    }

    @Test
    public void testChangeLine() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        table.addLine(user1);

        String[] newLine = { "alice", "alice@new.com", "avatarX.png" };
        table.changeLine("0", newLine);

        List<Map<String, String>> all = table.getAllLines();
        assertEquals("alice@new.com", all.get(0).get("email"));
        assertEquals("avatarX.png", all.get(0).get("avatar"));
        cleanupTableFile();
    }

    @Test
    public void testDeleteLine() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        String[] user2 = { "bob", "bob@example.com", "avatar2.png" };
        table.addLine(user1);
        table.addLine(user2);

        table.deleteLine("0");

        List<Map<String, String>> all = table.getAllLines();
        assertEquals(1, all.size());
        assertEquals("bob", all.get(0).get("username"));
        cleanupTableFile();
    }

    @Test
    public void testResequenceTable() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        String[] user2 = { "bob", "bob@example.com", "avatar2.png" };
        table.addLine(user1);
        table.addLine(user2);

        table.deleteLine("0");
        table.resequenceTable();

        List<Map<String, String>> all = table.getAllLines();
        assertEquals("1", all.get(0).get("id"));
        cleanupTableFile();
    }

    @Test
    public void testGetLineId() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        table.addLine(user1);

        String id = table.getLineId(user1);
        assertEquals("0", id);
        cleanupTableFile();
    }

    @Test
    public void testAddLineWithComma() throws IOException {
        String[] user = { "ali,ce", "ali,ce@example.com", "avatar,1.png" };
        table.addLine(user);

        List<Map<String, String>> all = table.getAllLines();
        assertEquals("ali,ce", all.get(0).get("username"));
        assertEquals("ali,ce@example.com", all.get(0).get("email"));
        assertEquals("avatar,1.png", all.get(0).get("avatar"));
        cleanupTableFile();
    }

    @Test
    public void testGetEverythingInHeaderReturnsCorrectValues() throws IOException {
        String[] user1 = {"alice", "alice@example.com", "avatar1.png"};
        String[] user2 = {"bob", "bob@example.com", "avatar2.png"};
        table.addLine(user1);
        table.addLine(user2);

        List<String> usernames = table.getEverythingInHeader("username");
        assertEquals(2, usernames.size());
        assertTrue(usernames.contains("alice"));
        assertTrue(usernames.contains("bob"));

        List<String> emails = table.getEverythingInHeader("email");
        assertEquals(2, emails.size());
        assertTrue(emails.contains("alice@example.com"));
        assertTrue(emails.contains("bob@example.com"));
    }

    @Test
    public void testGetLinesQueryReturns() throws IOException {
        String[] user1 = { "alice", "alice@example.com", "avatar1.png" };
        String[] user2 = { "bob", "bob@example.com", "avatar2.png" };
        String[] user3 = { "alicia", "alicia@example.com", "avatar3.png" };
        table.addLine(user1);
        table.addLine(user2);
        table.addLine(user3);

        List<Map<String, String>> found = table.getLinesQuery("username", "ali");
        List<String> usernames = new ArrayList<>();
        for (Map<String, String> line : found) {
            usernames.add(line.get("username"));
        }
        assertEquals(2, found.size());
        assertTrue(usernames.contains("alice"));
        assertTrue(usernames.contains("alicia"));
        cleanupTableFile();
    }

    @Test
    public void testGetEverythingInHeaderThrowsOnNullHeader() {
        assertThrows(IllegalArgumentException.class, () -> table.getEverythingInHeader(null));
    }

    @Test
    public void testGetEverythingInHeaderThrowsOnInvalidHeader() {
        assertThrows(IllegalArgumentException.class, () 
            -> table.getEverythingInHeader("notAHeader"));
    }

    /////////////TESTS FOR EXCEPTIONS////////////////////

    @Test
    public void testAddLineThrowsOnNull() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.addLine(null));
        cleanupTableFile();
    }

    @Test
    public void testAddLineThrowsOnWrongLength() throws IOException {
        String[] badUser = { "only", "two" };
        assertThrows(IllegalArgumentException.class, () -> table.addLine(badUser));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesThrowsOnNullHeader() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLines(null, "value"));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesThrowsOnNullValue() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLines("username", null));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesThrowsOnInvalidHeader() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLines("notAHeader", "value"));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesQueryThrowsOnNullHeader() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLinesQuery(null, "query"));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesQueryThrowsOnNullQuery() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLinesQuery("username", null));
        cleanupTableFile();
    }

    @Test
    public void testGetLinesQueryThrowsOnInvalidHeader() throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> table.getLinesQuery("notAHeader", "query"));
        cleanupTableFile();
    }

    @Test
    public void testChangeLineThrowsOnNullId() throws IOException {
        String[] user = { "alice", "alice@example.com", "avatar1.png" };
        assertThrows(IllegalArgumentException.class, () -> table.changeLine(null, user));
        cleanupTableFile();
    }

    @Test
    public void testChangeLineThrowsOnNullLine() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.changeLine("0", null));
        cleanupTableFile();
    }

    @Test
    public void testChangeLineThrowsOnWrongLength() throws IOException {
        String[] badLine = { "just", "two" };
        assertThrows(IllegalArgumentException.class, () -> table.changeLine("0", badLine));
        cleanupTableFile();
    }

    @Test
    public void testDeleteLineThrowsOnNullId() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.deleteLine(null));
        cleanupTableFile();
    }

    @Test
    public void testGetLineIdThrowsOnNull() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> table.getLineId(null));
        cleanupTableFile();
    }

    @Test
    public void testGetLineIdThrowsOnWrongLength() throws IOException {
        String[] badLine = { "just", "two" };
        assertThrows(IllegalArgumentException.class, () -> table.getLineId(badLine));
        cleanupTableFile();
    }
}
