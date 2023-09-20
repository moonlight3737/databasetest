package org.example;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonKey;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.apache.ibatis.jdbc.ScriptRunner;

import static java.lang.Double.parseDouble;

public class Main {
    public static void main(String[] args) {
        String[] STATEMENTS = { "insert", "select", "update", "delete" };
        String[] DATABASES = { "mariaDB", "mySQL", "postgreSQL" };
        int[] PORTS = { 3305, 3306, 5432 };

        for (int i = 0; i < DATABASES.length; i++) {
            String databaseName = DATABASES[i];
            int port = PORTS[i];
            String filepath = String.format("src\\main\\resources\\org\\example\\results\\%sResults.json", databaseName);

            JsonObject databaseStats = new JsonObject();

            try {
                //MySQL Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = String.format("jdbc:%s://localhost:%s/testing", databaseName.toLowerCase(), port);
                String user = databaseName.equals("postgreSQL") ? "postgres" : "root";
                String password = databaseName.equals("mariaDB") ? "maria" : "root";
                Connection connection = DriverManager.getConnection(url, user, password);

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath));
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.setLogWriter(null);

                // Create table
                String createScriptPath = "src\\main\\resources\\org\\example\\queries\\createScript.sql";
                Reader createTable = new BufferedReader(new FileReader(createScriptPath));
                scriptRunner.runScript(createTable);

                //Creating the Statement
                Statement stmt = connection.createStatement();
                for (String statement: STATEMENTS) {
                    System.out.printf("Testing %s!\n", statement);

                    JsonArray statementStats = new JsonArray();

                    //Running and Timing the Script
                    for (int j = 0; j < 100; j++) {
                        //SQL script
                        String scriptPath = String.format("src\\main\\resources\\org\\example\\queries\\%sScript.sql", statement);
                        Reader statementQuery = new BufferedReader(new FileReader(scriptPath));

                        long startTime = System.currentTimeMillis();
                        //Runs the script
                        scriptRunner.runScript(statementQuery);
                        long endTime = System.currentTimeMillis();
                        long duration = (endTime - startTime);
                        statementStats.add(duration);
                    }

                    databaseStats.put(statement, statementStats);
                }

                try (PrintWriter out = new PrintWriter(new FileWriter(filepath))) {
                    out.write(databaseStats.toJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bufferedWriter.close();
                stmt.close();

                System.out.println(databaseName + ":");
                printFormattedStats(databaseStats);

                connection.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void printFormattedStats(JsonObject collectedStats) {
        AtomicReference<String> consoleString = new AtomicReference<>("");


        Set<String> keySet = collectedStats.keySet();
        for (String key: keySet) {
            JsonArray statementStats = (JsonArray) collectedStats.get(key);

            Object[] convertedList = statementStats.toArray();
            int averageMs = Arrays.stream(convertedList).mapToInt(a -> Integer.parseInt(a.toString())).sum() / convertedList.length;
            Arrays.sort(convertedList);
            Object medianMs = Array.get(convertedList, convertedList.length/2);

            String currentString = consoleString.get();
            consoleString.set(currentString + String.format("average %s took %sms\n", key, averageMs));
            currentString = consoleString.get();
            consoleString.set(currentString + String.format("median %s is %sms\n", key , medianMs));

        }

        System.out.println(consoleString.get());
    }
}