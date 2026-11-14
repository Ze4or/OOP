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
    void testExampleFromTask() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        List<Long> result = SubstringFinder.find(file.toString(), "бра");
        assertEquals(List.of(1L, 8L), result);
    }

    @Test
    void testOverlappingOccurrences() throws IOException {
        Path file = tempDir.resolve("overlap.txt");
        Files.writeString(file, "абабаба", StandardCharsets.UTF_8);

        List<Long> result = SubstringFinder.find(file, "аба");
        assertEquals(List.of(0L, 2L, 4L), result);
    }

    @Test
    void testMultilingualAndSurrogatePairs() throws IOException {
        Path file = tempDir.resolve("unicode.txt");

        String content = "Привет🚀 мир𠜎 漢字🚀 мир𠜎";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        List<Long> result = SubstringFinder.find(file, "🚀 мир𠜎");

        assertEquals(List.of(6L, 15L), result);
    }

    @Test
    void testEmptyTargetThrowsException() throws IOException {
        Path file = tempDir.resolve("empty_target.txt");
        Files.writeString(file, "текст", StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> SubstringFinder.find(file, ""));
    }

    @Test
    void testLargeGeneratedFileExceedingSmallHeap() throws IOException {
        Path largeFile = tempDir.resolve("large_generated.txt");

        String fillerBlock = "Строка_заполнитель_без_искомого_слова_1234567890_αβγδεζηθικλμνξοπρστυφχψω\n";
        long fillerCodePoints = fillerBlock.codePoints().count();
        String needle = "КЛЮЧЕВОЕ_СЛОВО_UTF8";

        int blocksBeforeFirst = 500;
        int blocksBeforeSecond = 2_000_000;
        int blocksBeforeThird = 2_000_000;

        long expectedPos1 = blocksBeforeFirst * fillerCodePoints;
        long expectedPos2 = expectedPos1 + needle.codePoints().count() + blocksBeforeSecond * fillerCodePoints;
        long expectedPos3 = expectedPos2 + needle.codePoints().count() + blocksBeforeThird * fillerCodePoints;

        try (BufferedWriter writer = Files.newBufferedWriter(largeFile, StandardCharsets.UTF_8)) {
            writeRepeated(writer, fillerBlock, blocksBeforeFirst);
            writer.write(needle);
            writeRepeated(writer, fillerBlock, blocksBeforeSecond);
            writer.write(needle);
            writeRepeated(writer, fillerBlock, blocksBeforeThird);
            writer.write(needle);
        }

        List<Long> result = SubstringFinder.find(largeFile, needle);
        assertEquals(List.of(expectedPos1, expectedPos2, expectedPos3), result);
    }

    private void writeRepeated(BufferedWriter writer, String block, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            writer.write(block);
        }
    }
}
