package org.example.service;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;


@Service
public class KeyGeneratorService {
    public String create() {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .withinRange('0', '9')
                        .withinRange('a', 'z')
                        .withinRange('A', 'Z')
                        .filteredBy(CharacterPredicates.ASCII_ALPHA_NUMERALS, CharacterPredicates.DIGITS)
                        .build();
        return randomStringGenerator.generate(7);
    }
}