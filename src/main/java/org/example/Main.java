package org.example;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.ibatis.jdbc.ScriptRunner;

public class Main {
    public static void main(String[] args) {
        String[] STATEMENTS = {"insert", "select", "update", "delete" };

        try {
            //MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:8010";
            Connection mySqlConnection = DriverManager.getConnection(url, "root", null);

            //MariaDB
            Connection mariaDBConnection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:8009",
                    "root", null
            );

            //Postgresql
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:8008/",
                    "postgres", null
            );

            System.out.println("Connection created");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("results.txt"));
            ScriptRunner scriptRunner = new ScriptRunner(con);

            //Creating the Statement
            Statement stmt = con.createStatement();
            for (String statement: STATEMENTS) {
                //Running and Timing the Script
                for (int i = 0; i < 100; i++) {
                    //SQL script
                    String script_path = String.format("path/%sScript.sql", statement);
                    Reader table = new BufferedReader(new FileReader(script_path));
                    //Script output to console turn off
                    scriptRunner.setLogWriter(null);

                    long start_time = System.currentTimeMillis();
                    //Runs the script
                    scriptRunner.runScript(table);
                    long end_time = System.currentTimeMillis();
                    long duration = (end_time - start_time);
                    bufferedWriter.write(String.valueOf(duration));
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
            stmt.close();

            String filepath = "path/results.txt";
            getAverage(filepath);

            con.close();
            System.out.println("Connection closed");
        }
        catch (Exception e) {
            System.out.println(e.toString());
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
            throw new RuntimeException(e);
        }
    }
}