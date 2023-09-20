package org.example;

import java.sql.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataGenerator {
    public int getRandomInt() {
        return (int) (Math.random() * 100);
    }

    public String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int stringLength = 8;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(stringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public Date getRandomDate() {
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(Integer.MAX_VALUE);

        return new Date(randomMillisSinceEpoch);
    }
}
