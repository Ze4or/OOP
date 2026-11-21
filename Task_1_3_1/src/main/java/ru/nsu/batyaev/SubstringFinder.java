package ru.nsu.batyaev;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс для потокового поиска всех вхождений подстроки в текстовых файлах
 * в кодировке UTF-8.
 * Реализация использует алгоритм Кнута-Морриса-Пратта (КМП).
 */
public class SubstringFinder {

    private static final int BUFFER_SIZE = 65536;

    public static List<Long> find(String fileName, String target) throws IOException {
        Objects.requireNonNull(fileName, "Имя файла не должно быть null");
        Objects.requireNonNull(target, "Искомая подстрока не должна быть null");
        if (target.isEmpty()) {
            throw new IllegalArgumentException("Искомая подстрока не должна быть пустой");
        }

        int[] pattern = target.codePoints().toArray();
        int[] lps = computePrefixFunction(pattern);
        int targetLen = pattern.length;
        List<Long> occurrences = new ArrayList<>();

        try (Reader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8),
                BUFFER_SIZE)) {

            int j = 0;
            long currentIndex = 0;
            int pendingChar = -1;

            while (true) {
                int ch = (pendingChar != -1) ? pendingChar : reader.read();
                pendingChar = -1;

                if (ch == -1) {
                    break;
                }

                char c1 = (char) ch;
                int codePoint;

                if (Character.isHighSurrogate(c1)) {
                    int next = reader.read();
                    if (next != -1 && Character.isLowSurrogate((char) next)) {
                        codePoint = Character.toCodePoint(c1, (char) next);
                    } else {
                        codePoint = c1;
                        pendingChar = next;
                    }
                } else {
                    codePoint = c1;
                }

                while (j > 0 && codePoint != pattern[j]) {
                    j = lps[j - 1];
                }
                if (codePoint == pattern[j]) {
                    j++;
                }
                if (j == targetLen) {
                    occurrences.add(currentIndex - targetLen + 1);
                    j = lps[j - 1];
                }
                currentIndex++;
            }
        }

        return occurrences;
    }

    private static int[] computePrefixFunction(int[] pattern) {
        int m = pattern.length;
        int[] lps = new int[m];
        int j = 0;

        for (int i = 1; i < m; i++) {
            while (j > 0 && pattern[i] != pattern[j]) {
                j = lps[j - 1];
            }
            if (pattern[i] == pattern[j]) {
                j++;
            }
            lps[i] = j;
        }
        return lps;
    }
}
