package com.mycompany.irr00_group_project.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a table with headers and a file path.
 * The first header must be "id".
 */
public class Table {
    private String[] headers;
    private String path;
    private int id;

    /**
     * Constructs a Table with the specified headers and file path.
     * @param headers the headers of the table, must not be null or empty and must start with "id"
     * @param path the file path where the table will be stored, must not be null or empty
     * @throws IllegalArgumentException if the headers are null, empty, or do not start with "id",
     * or if the path is null, empty, or a file already exists at that path
     * @throws IOException if an I/O error occurs while creating the file
     *
     */
    public Table(String[] headers, String path) throws IllegalArgumentException, IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (headers == null || headers.length == 0) {
            throw new IllegalArgumentException("Headers cannot be null or empty");
        }
        if (!headers[0].equals("id")) {
            throw new IllegalArgumentException("First header must be 'id'");
        }

        this.headers = headers;
        this.path = path;

        if (new File(path).isFile()) {
            // File exists, refresh ID counter from existing data
            refreshIdCounter();
        } else {
            // File doesn't exist, create it with headers
            this.id = 0;
            try (FileWriter writer = new FileWriter(path, true)) {
                writer.append(String.join(",", headers)).append("\n");
            } catch (IOException e) {
                throw e;
            }
        }
    }

    /**
     * Adds a line to the table.
     * @param line the line to be added, must not be null and must match the number of headers
     * @throws IllegalArgumentException if the line is null or does not match the number of headers
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void addLine(String[] line) throws IllegalArgumentException, IOException {
        if (line == null) {
            throw new IllegalArgumentException("Line must not be null");
        }
        if (line.length != headers.length - 1) {
            throw new IllegalArgumentException("Line must match the number of headers");
        }
        line = removeCommas(line);
        try (FileWriter writer = new FileWriter(path, true)) {
            // Build the line with ID and data
            String newLine = String.valueOf(id++) + "," + String.join(",", line);
            writer.append(newLine).append("\n");
        } catch (IOException e) {
            throw e;
        }
    }

    /**
    * Retrieves lines from the table that match the specified header and value.
    * @param header the header to match, must not be null
    * @param value the value to match, must not be null
    * @return a list of maps where each map represents a line with the specified
    * header and value
    * @throws IllegalArgumentException if the header or value is null
    * @throws IOException if an I/O error occurs while reading the file
    */
    public List<Map<String, String>> getLines(String header, String value)
        throws IllegalArgumentException, IOException {
        if (header == null || value == null) {
            throw new IllegalArgumentException("Header and value must not be null");
        }

        int headerIndex = getHeaderIndex(header);

        List<Map<String, String>> resultList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);

                if (isValueMatchingAtHeader(values, headerIndex, value)) {
                    Map<String, String> row = parseLineToMap(values);
                    resultList.add(row);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage(), e);
        }

        return resultList;
    }

    /**
     * Retrieves all values in the specified header.
     * @param header the header to retrieve values from, must not be null
     * @return a list of all values in the specified header
     * @throws IllegalArgumentException if the header is null
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<String> getEverythingInHeader(String header) 
        throws IllegalArgumentException, IOException {
        List<String> res = new ArrayList<String>();

        if (header == null) {
            throw new IllegalArgumentException("Header and value must not be null");
        }
        
        int headerIndex = getHeaderIndex(header);
                
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                
                res.add(values[headerIndex]);
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage(), e);
        }
        
        return res;
    }

    /**
     * Retrieves lines from the table that contain the specified query in the given header.
     * @param header the header to search in, must not be null
     * @param query the query to search for, must not be null
     * @return a list of maps where each map represents a line that contains the
     * query in the specified header
     * @throws IllegalArgumentException if the header or query is null
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<Map<String, String>> getLinesQuery(String header, String query)
        throws IllegalArgumentException, IOException {
        if (header == null || query == null) {
            throw new IllegalArgumentException("Header and value must not be null");
        }

        int headerIndex = getHeaderIndex(header);

        List<Map<String, String>> resultList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                if (values[headerIndex].toLowerCase().contains(query)) {
                    Map<String, String> row = parseLineToMap(values);
                    resultList.add(row);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + e.getMessage(), e);
        }

        return resultList;
    }

    /**
     * Helper method to read all lines from a table file and return them as maps.
     * @return List of maps representing all data rows (excluding header)
     * @throws IOException if an I/O error occurs while reading the file
     */
    public List<Map<String, String>> getAllLines() throws IOException {
        List<Map<String, String>> resultList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                resultList.add(parseLineToMap(line.split(",", -1)));
            }
        }

        return resultList;
    }

    /**
     * Gets the index of the specified header in the headers array.
     * @param headerName The name of the header to find.
     * @return The index of the header in the headers array.
     * @throws IllegalArgumentException if the header does not exist.
     */
    private int getHeaderIndex(String headerName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(headerName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Header '" + headerName + "' not found");
    }

    /**
     * Checks if the values array has a matching value at the specified header index.
     * @param values The array of string values from a CSV line.
     * @param headerIndex The index of the header to check.
     * @param value The value to match.
     * @return true if the value at the header index matches the specified value.
     */
    private boolean isValueMatchingAtHeader(String[] values, int headerIndex, String value) {
        return values.length >= headers.length
            && headerIndex < values.length
            && values[headerIndex].equals(value);
    }

    /**
     * Parses a line of CSV values into a Map using the headers.
     *
     * @param values The array of string values from a CSV line.
     * @return A Map where keys are headers and values are the corresponding values from the line.
     */
    private Map<String, String> parseLineToMap(String[] values) {
        Map<String, String> row = new HashMap<>();
        for (int i = 0; i < headers.length && i < values.length; i++) {
            row.put(headers[i], values[i].replace("'CoMma'", ","));
        }
        return row;
    }

    /**
     * Changes a line in the table specified by id to the newline.
     * @param id id to change
     * @param newLine new line to replace old
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void changeLine(String id, String[] newLine) throws IOException {
        if (id == null || newLine == null || newLine.length != headers.length - 1) {
            throw new IllegalArgumentException("Invalid input for changeLine");
        }
        File inputFile = new File(path);
        File tempFile = new File(path.replace(".csv", "_tmp.csv"));

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    // Always write header line
                    writer.write(line);
                    writer.newLine();
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",", -1);
                if (values.length > 0 && values[0].equals(id)) {
                    newLine = removeCommas(newLine);
                    writer.write(id + ",");
                    writer.write(String.join(",", newLine));
                } else {
                    // Write original line
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        // Replace original file with updated temp file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to replace the original file with the updated file.");
        }
    }    
    
    /**
     * Removes commas in a line.
     * @param line line to remove commas from
     * @return returns line without commas
     */
    private String[] removeCommas(String[] line) {
        for (int i = 0; i < line.length; i++) {
            if (line[i] != null) {
                line[i] = line[i].replace(",", "'CoMma'");
            } else {
                line[i] = "";
            }
        }
        return line;
    }

    /**
     * Deletes a line in the table.
     * @param id the ID of the line to be deleted
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void deleteLine(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        File inputFile = new File(path);
        File tempFile = new File(path.replace(".csv", "_tmp.csv"));

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    // Always write header line
                    writer.write(line);
                    writer.newLine();
                    firstLine = false;
                    continue;
                }

                String[] values = line.split(",", -1);
                if (values.length > 0 && !values[0].equals(id)) {
                    // Write original line if ID does not match
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
        // Replace original file with updated temp file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to replace the original file with the updated file.");
        }

        // Resequence all IDs to remove gaps and ensure sequential numbering
        resequenceIds();
    }

    public String[] getHeaders() {
        return headers;
    }

    /**
     * Returns the next available ID for the table.
     * This represents the number of data lines (excluding header) in the table.
     *
     * @return the next available ID
     */
    public int getNextId() {
        return this.id;
    }

    /**
     * Refreshes the ID counter by finding the highest existing ID in the file.
     * This ensures that new IDs are assigned correctly after deletions.
     */
    private void refreshIdCounter() throws IOException {
        int maxId = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] values = line.split(",", -1);
                    if (values.length > 0) {
                        try {
                            int currentId = Integer.parseInt(values[0]);
                            maxId = Math.max(maxId, currentId);
                        } catch (NumberFormatException e) {
                            // Skip lines with invalid IDs
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file to refresh ID counter: " + e.getMessage(), e);
        }

        this.id = maxId + 1;
    }

    /**
     * Returns the ID of a line that matches the specified values.
     *
     * @param line the values to match, must not be null or empty
     * @return the ID of the matching line, or null if no match is found
     * @throws IOException if an I/O error occurs while reading the file
     * @throws IllegalArgumentException if the line is null or does not match the number of headers
     */
    public String getLineId(String [] line) throws IOException, IllegalArgumentException {
        if (line == null || line.length != headers.length - 1) {
            for (String header : headers) {
                System.err.println(header);
            }
            int a = headers.length - 1;
            System.err.println(a);
            throw new IllegalArgumentException("Line must not be null or empty");
        }        
    
        List<Map<String, String>> allLines = getAllLines();
        for (Map<String, String> row : allLines) {
            boolean match = true;
            for (int i = 0; i < line.length; i++) {
                String rowValue = row.get(headers[i + 1]);
                if (rowValue == null || !rowValue.equals(line[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return row.get("id");
            }
        }
        return null;
    }

    /**
     * Resequences all IDs in the file to remove gaps after deletions.
     * This ensures that IDs are always sequential starting from 0.
     */
    private void resequenceIds() throws IOException {
        File inputFile = new File(path);
        File tempFile = new File(path.replace(".csv", "_resequence_tmp.csv"));

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line = reader.readLine(); // Read and write header line
            if (line != null) {
                writer.write(line);
                writer.newLine();
            }

            int newId = 1;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] values = line.split(",", -1);
                    if (values.length > 0) {
                        // Replace the first column (ID) with the new sequential ID
                        values[0] = String.valueOf(newId++);
                        writer.write(String.join(",", values));
                        writer.newLine();
                    }
                }
            }

            // Set the ID counter to the next available ID
            this.id = newId;
        }

        // Replace original file with resequenced temp file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to replace the original file with the resequenced file.");
        }
    }

    /**
     * Manually resequences all IDs in the table to remove gaps.
     * This can be called to clean up ID sequences in existing files.
     *
     * @throws IOException if an I/O error occurs while resequencing the file
     */
    public void resequenceTable() throws IOException {
        resequenceIds();
    }

}
