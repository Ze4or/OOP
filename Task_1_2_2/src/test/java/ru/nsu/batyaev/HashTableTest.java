package ru.nsu.batyaev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable<String, Integer> hashTable;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable<>();
    }

    @Test
    void testEmptyHashTable() {
        assertNull(hashTable.get("any"));
        assertFalse(hashTable.containsKey("any"));
        assertFalse(hashTable.iterator().hasNext());
        assertEquals("{}", hashTable.toString());
    }

    @Test
    void testPutAndGet() {
        hashTable.put("one", 1);
        hashTable.put("two", 2);

        assertEquals(1, hashTable.get("one"));
        assertEquals(2, hashTable.get("two"));
        assertNull(hashTable.get("three"));
    }

    @Test
    void testPutOverridesValue() {
        hashTable.put("key", 1);
        hashTable.put("key", 2);

        assertEquals(2, hashTable.get("key"));
    }

    @Test
    void testUpdate() {
        hashTable.put("one", 1);

        assertTrue(hashTable.update("one", 100));
        assertEquals(100, hashTable.get("one"));

        assertFalse(hashTable.update("two", 2));
        assertNull(hashTable.get("two"));
    }

    @Test
    void testRemove() {
        hashTable.put("one", 1);
        hashTable.put("two", 2);

        assertTrue(hashTable.remove("one"));
        assertNull(hashTable.get("one"));

        assertFalse(hashTable.remove("three"));
    }

    @Test
    void testContainsKey() {
        hashTable.put("one", 1);
        assertTrue(hashTable.containsKey("one"));
        assertFalse(hashTable.containsKey("two"));
    }

    @Test
    void testResizeAndCapacity() {
        // DEFAULT_CAPACITY = 16, LOAD_FACTOR = 0.75 - resize при 12 элементах
        for (int i = 0; i < 20; i++) {
            hashTable.put("key" + i, i);
        }

        for (int i = 0; i < 20; i++) {
            assertEquals(i, hashTable.get("key" + i));
        }
    }

    // Вспомогательный класс для гарантированного создания коллизий
    private static class BadKey {
        private final String name;

        public BadKey(String name) { this.name = name; }

        @Override
        public int hashCode() { return 42; } // Всегда возвращает один хеш

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            return name.equals(((BadKey) obj).name);
        }
    }

    @Test
    void testCollisions() {
        HashTable<BadKey, String> collisionTable = new HashTable<>();
        BadKey key1 = new BadKey("key1");
        BadKey key2 = new BadKey("key2");
        BadKey key3 = new BadKey("key3");

        collisionTable.put(key1, "value1");
        collisionTable.put(key2, "value2");
        collisionTable.put(key3, "value3");

        assertEquals("value1", collisionTable.get(key1));
        assertEquals("value2", collisionTable.get(key2));
        assertEquals("value3", collisionTable.get(key3));

        assertTrue(collisionTable.remove(key2));
        assertNull(collisionTable.get(key2));
        assertEquals("value1", collisionTable.get(key1));
        assertEquals("value3", collisionTable.get(key3));
    }

    @Test
    void testIteratorFailFastOnPut() {
        hashTable.put("one", 1);
        hashTable.put("two", 2);

        var iterator = hashTable.iterator();
        iterator.next();

        // Модификация коллекции во время обхода
        hashTable.put("three", 3);

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testIteratorFailFastOnRemove() {
        hashTable.put("one", 1);
        hashTable.put("two", 2);

        var iterator = hashTable.iterator();

        // Модификация коллекции во время обхода
        hashTable.remove("two");

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testIteratorNoSuchElement() {
        var iterator = hashTable.iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testEquals() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        assertEquals(table1, table2);

        table1.put("one", 1);
        table1.put("two", 2);

        table2.put("two", 2);
        table2.put("one", 1);

        assertEquals(table1, table2);

        table2.put("three", 3);
        assertNotEquals(table1, table2);

        table1.put("three", 4);
        assertNotEquals(table1, table2);
    }

    @Test
    void testToString() {
        hashTable.put("one", 1);
        String str = hashTable.toString();

        assertTrue(str.startsWith("{"));
        assertTrue(str.endsWith("}"));
        assertTrue(str.contains("one=1"));

        hashTable.put("two", 2);
        String str2 = hashTable.toString();
        assertTrue(str2.contains("one=1"));
        assertTrue(str2.contains("two=2"));
        assertTrue(str2.contains(", "));
    }
}
