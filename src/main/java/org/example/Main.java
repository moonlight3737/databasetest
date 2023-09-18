package org.example;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.ibatis.jdbc.ScriptRunner;

public class Main {
    public static void main(String[] args) {
        String[] STATEMENTS = { "insert", "select", "update", "delete" };
        String[] DATABASES = { "mariaDB", "mySQL", "postgreSQL" };
        int[] PORTS = { 8009, 8010, 8008 };

        for (int i = 0; i < DATABASES.length; i++) {
            String databaseName = DATABASES[i];
            int port = PORTS[i];
            String filepath = String.format("src\\main\\resources\\org\\example\\results\\%sResults.txt", databaseName);

            try {
                //MySQL Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = String.format("jdbc:%s://localhost:%s/testing", databaseName.toLowerCase(), port);
                String user = databaseName.equals("postgreSQL") ? "postgres" : "root";
                Connection connection = DriverManager.getConnection(url, user, null);

                System.out.println("Connection created");
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
                    //Running and Timing the Script
                    for (int j = 0; j < 10_000; j++) {
                        //SQL script
                        String scriptPath = String.format("src\\main\\resources\\org\\example\\queries\\%sScript.sql", statement);
                        Reader statementQuery = new BufferedReader(new FileReader(scriptPath));

                        long startTime = System.currentTimeMillis();
                        //Runs the script
                        scriptRunner.runScript(statementQuery);
                        long endTime = System.currentTimeMillis();
                        long duration = (endTime - startTime);
                        bufferedWriter.write(String.valueOf(duration));
                        bufferedWriter.newLine();
                    }
                }

                bufferedWriter.close();
                stmt.close();

                getAverage(filepath);

                connection.close();
                System.out.println("Connection closed");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void getAverage(String filepath ) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            //Variables to get an average
            String st;
            ArrayList<Integer> list = new ArrayList<>();

            // Condition holds true till
            // there is character in a string
            while ((st = bufferedReader.readLine()) != null) {
                // Print the string
                list.add(Integer.valueOf(st));
                //System.out.println(st);
            }
            bufferedReader.close();

            int sum = 0, avg;
            for (Integer integer : list) {
                sum = sum + integer;
            }

            avg = sum / list.size();
            System.out.println("The average of the List: " + avg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}