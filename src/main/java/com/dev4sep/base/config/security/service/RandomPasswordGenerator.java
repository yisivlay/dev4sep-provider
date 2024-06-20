package com.dev4sep.base.config.security.service;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author YISivlay
 */
public class RandomPasswordGenerator {

    private final int numberOfCharactersInPassword;
    private static final SecureRandom secureRandom = new SecureRandom();

    public RandomPasswordGenerator(final int numberOfCharactersInPassword) {
        this.numberOfCharactersInPassword = numberOfCharactersInPassword;
    }

    public String generate() {
        return IntStream.range(0, this.numberOfCharactersInPassword)
                .mapToObj(i -> String.valueOf((char) ((int) (secureRandom.nextDouble() * 26) + 97)))
                .collect(Collectors.joining());
    }

}
