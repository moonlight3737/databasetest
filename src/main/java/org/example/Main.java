package org.example;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import org.apache.ibatis.jdbc.ScriptRunner;

public class Main {
    public static void main(String[] args) {
        String[] STATEMENTS = { "insert", "select", "update", "delete" };
        String[] DATABASES = { "mariaDB", "mySQL", "postgreSQL" };
        int[] PORTS = { 8009, 8010, 8008 };

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
                Connection connection = DriverManager.getConnection(url, user, null);

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath));
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.setLogWriter(null);

                // Create table
                String createScriptPath = "src\\main\\resources\\org\\example\\queries\\createScript.sql";
                Reader createTable = new BufferedReader(new FileReader(createScriptPath));
                scriptRunner.runScript(createTable);

                RandomDataGenerator rng = new RandomDataGenerator();

                //Creating the Statement
                Statement stmt = connection.createStatement();
                for (String statement: STATEMENTS) {
                    System.out.printf("Testing %s!\n", statement);

                    JsonArray statementStats = new JsonArray();

                    //Running and Timing the Script
                    for (int j = 0; j < 10_000; j++) {
                        //SQL script
                        String scriptToExecute = "";

                        switch (statement) {
                            case "insert": {
                                scriptToExecute = String.format("INSERT INTO flight_logs (flight_number, departure_airport, arrival_airport, departure_date, arrival_date, departure_time, arrival_time, airline, fare_class, passenger_count) " +
                                        "VALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %s);",
                                        rng.getRandomInt(), rng.getRandomString(), rng.getRandomString(), rng.getRandomDate(), rng.getRandomDate(), rng.getRandomString(), rng.getRandomString(), rng.getRandomString(), rng.getRandomString(), rng.getRandomInt());
                            }
                                break;
                            case "select": {
                                scriptToExecute = String.format("SELECT * FROM flight_logs\n" +
                                        "WHERE flight_number = %s;", rng.getRandomInt());
                            }
                                break;
                            case "update": {
                                scriptToExecute = String.format("UPDATE flight_logs\n" +
                                        "SET airline = 'PatatAir'\n" +
                                        "WHERE flight_number = %s;", rng.getRandomInt());
                            }
                                break;
                            case "delete": {
                                scriptToExecute = String.format("DELETE FROM flight_logs\n" +
                                        "WHERE flight_number = %s;", rng.getRandomInt());
                            }
                                break;
                            default:
                                continue;
                        }
                        StringReader scriptReader = new StringReader(scriptToExecute);
                        
                        long startTime = System.currentTimeMillis();
                        //Runs the script
                        scriptRunner.runScript(scriptReader);
                        long endTime = System.currentTimeMillis();
                        long duration = (endTime - startTime);
                        statementStats.add(duration);
                    }

                    databaseStats.put(statement, statementStats);
                }

                String dropScriptPath = "src\\main\\resources\\org\\example\\queries\\dropScript.sql";
                Reader dropTable = new BufferedReader(new FileReader(dropScriptPath));
                scriptRunner.runScript(dropTable);

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
            if (convertedList.length < 1) {
                continue;
            }

            int averageMs = Arrays.stream(convertedList).mapToInt(a -> Integer.parseInt(a.toString())).sum() / convertedList.length;
            String currentString = consoleString.get();
            consoleString.set(currentString + String.format("%s took %sms\n", key, averageMs));
        }

        System.out.println(consoleString.get());
    }
}