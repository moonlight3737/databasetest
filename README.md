# databasetest

## Getting Started

To make sure you can run the test you need to install drivers and libraries. 

### Install

For the test you can run it in 3 databases (mySQL, MariaDB, PostgreSQL). For each DB you need a Driver. 
This is easy to do if you folllow these links below. 

MySQL
https://dev.mysql.com/downloads/connector/j/ 

MariaDB
https://mariadb.com/kb/en/about-mariadb-connector-j/

PostgreSQL
https://jdbc.postgresql.org/download/

To run a SQL script in java we use mybatis framework
https://mybatis.org/mybatis-3/getting-started.html

Make sure your pom.xml has these dependencies

    <dependencies>
    
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.13</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <version>3.1.4</version>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>
        </dependency>

    </dependencies>

### Results
All the results are in ms and put in a results.txt and the average will be in ms and in the console out.

**WARNING for each test run overwrites the results.txt, so save the results.txt for each test.**






