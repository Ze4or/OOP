package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubstringFinderTest {

    @TempDir
    Path tempDir;

    @Test
    void testTaskExample() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        assertEquals(List.of(1L, 8L), SubstringFinder.find(file.toString(), "бра"));
    }

    @Test
    void testOverlappingOccurrences() throws IOException {
        Path file = tempDir.resolve("overlap.txt");
        Files.writeString(file, "банана", StandardCharsets.UTF_8);

        assertEquals(List.of(1L, 3L), SubstringFinder.find(file.toString(), "ана"));
    }

    @Test
    void testMultiAlphabetUtf8AndEmojis() throws IOException {
        Path file = tempDir.resolve("unicode.txt");
        String content = "Hello 世界! Привет мир 🚀 привет 世界!";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        assertEquals(List.of(6L, 30L), SubstringFinder.find(file.toString(), "世界"));
        assertEquals(List.of(21L), SubstringFinder.find(file.toString(), "🚀"));
    }

    @Test
    void testSurrogatePairOnBufferBoundary() throws IOException {
        Path file = tempDir.resolve("boundary.txt");
        String padding = "x".repeat(65535);
        String target = "🚀граница";
        Files.writeString(file, padding + target + "end", StandardCharsets.UTF_8);

        assertEquals(List.of(65535L), SubstringFinder.find(file.toString(), target));
    }

    @Test
    void testLargeFileGeneration() throws IOException {
        Path largeFile = tempDir.resolve("large_test.txt");
        String fillerChunk = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя🚀0123456789\n";
        long chunkCodePoints = fillerChunk.codePoints().count();
        String target = "КЛЮЧЕВОЕ_СЛОВО_世界_🔥";
        long targetFirstIndex;
        long targetSecondIndex;

        try (BufferedWriter writer = Files.newBufferedWriter(largeFile, StandardCharsets.UTF_8)) {
            long writtenCodePoints = 0;

            for (int i = 0; i < 1_200_000; i++) {
                writer.write(fillerChunk);
                writtenCodePoints += chunkCodePoints;
            }

            targetFirstIndex = writtenCodePoints;
            writer.write(target);
            writtenCodePoints += target.codePoints().count();

            for (int i = 0; i < 1_200_000; i++) {
                writer.write(fillerChunk);
                writtenCodePoints += chunkCodePoints;
            }

            targetSecondIndex = writtenCodePoints;
            writer.write(target);
        }

        assertEquals(
                List.of(targetFirstIndex, targetSecondIndex),
                SubstringFinder.find(largeFile.toString(), target)
        );
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> SubstringFinder.find("any.txt", ""));
        assertThrows(NullPointerException.class, () -> SubstringFinder.find(null, "abc"));
        assertThrows(NullPointerException.class, () -> SubstringFinder.find("any.txt", null));
    }
}
