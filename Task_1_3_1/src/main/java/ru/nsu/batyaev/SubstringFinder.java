package ru.nsu.batyaev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, предоставляющий функционал для поиска всех вхождений заданной подстроки
 * в текстовом файле.
 * Реализация использует алгоритм Кнута-Морриса-Пратта (КМП) для эффективного поиска
 * и потоковое чтение файла, что позволяет обрабатывать
 * файлы любого размера без полной загрузки их в оперативную память.
 * Поддерживается поиск символов любых алфавитов, включая многобайтовые символы UTF-8
 * (например, эмодзи или иероглифы), путем корректной обработки суррогатных пар.
 */
public class SubstringFinder {

    private static final int BUFFER_SIZE = 65_536;

    public static List<Long> find(String filePath, String target) throws IOException {
        return find(Path.of(filePath), target);
    }

    public static List<Long> find(Path filePath, String target) throws IOException {
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Искомая подстрока не должна быть пустой или null");
        }

        int[] pattern = target.codePoints().toArray();
        int[] lps = computeLps(pattern);
        List<Long> occurrences = new ArrayList<>();

        try (Reader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8),
                BUFFER_SIZE)) {

            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;
            int j = 0; // текущая позиция совпадения в pattern
            long charIndex = 0; // индекс символа (Unicode code point) в файле
            int pendingHighSurrogate = -1;

            while ((charsRead = reader.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; i++) {
                    char ch = buffer[i];
                    int codePoint;

                    if (pendingHighSurrogate != -1) {
                        if (Character.isLowSurrogate(ch)) {
                            codePoint = Character.toCodePoint((char) pendingHighSurrogate, ch);
                            pendingHighSurrogate = -1;
                        } else {
                            // Если встретился непарный high surrogate, обрабатываем его отдельно
                            j = advanceKmp(pendingHighSurrogate, pattern, lps, j, charIndex++, occurrences);
                            pendingHighSurrogate = -1;

                            if (Character.isHighSurrogate(ch)) {
                                pendingHighSurrogate = ch;
                                continue;
                            }
                            codePoint = ch;
                        }
                    } else if (Character.isHighSurrogate(ch)) {
                        pendingHighSurrogate = ch;
                        continue;
                    } else {
                        codePoint = ch;
                    }

                    j = advanceKmp(codePoint, pattern, lps, j, charIndex++, occurrences);
                }
            }

            if (pendingHighSurrogate != -1) {
                advanceKmp(pendingHighSurrogate, pattern, lps, j, charIndex, occurrences);
            }
        }

        return occurrences;
    }

    private static int advanceKmp(
            int codePoint,
            int[] pattern,
            int[] lps,
            int j,
            long charIndex,
            List<Long> occurrences) {

        while (j > 0 && codePoint != pattern[j]) {
            j = lps[j - 1];
        }
        if (codePoint == pattern[j]) {
            j++;
        }
        if (j == pattern.length) {
            occurrences.add(charIndex - pattern.length + 1);
            j = lps[j - 1];
        }
        return j;
    }

    /**
     * Вычисляет массив длин наибольших собственных префиксов,
     * совпадающих с суффиксами (префикс-функция для КМП).
     */
    private static int[] computeLps(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;
        int i = 1;

        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }
}
