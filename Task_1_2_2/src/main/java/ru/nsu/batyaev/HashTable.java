package ru.nsu.batyaev;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * Параметризованный контейнер HashTable, реализующий структуру данных хеш-таблица
 * с разрешением коллизий методом цепочек.
 *
 * @param <K> тип ключей, поддерживаемый данной хеш-таблицей
 * @param <V> тип значений
 */
public class HashTable<K, V> implements Iterable<HashTable.Node<K, V>> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private int modCount; // Счетчик структурных модификаций для итератора

    /**
     * Внутренний класс для хранения пар (узел связного списка для коллизий).
     *
     * @param <K> тип ключа
     * @param <V> тип значения
     */
    public static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        final int hash;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    // 1. Создание пустой хеш-таблицы
    @SuppressWarnings("unchecked")
    public HashTable() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        size = 0;
        modCount = 0;
    }

    private int hash(Object key) {
        return (key == null) ? 0 : Math.abs(key.hashCode()) % table.length;
    }

    // 2. Добавление пары
    public void put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value; // Если ключ уже есть заменяем значение
                return;
            }
            current = current.next;
        }

        // Добавление в начало цепочки при коллизии
        Node<K, V> newNode = new Node<>(index, key, value, table[index]);
        table[index] = newNode;
        size++;
        modCount++;
    }

    // 3. Удаление пары ключ-значение
    public boolean remove(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];
        Node<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                modCount++;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    // 4. Поиск значения по ключу
    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    // 5. Обновление значения по ключу (поведение аналогично put, но обновляет только существующий)
    public boolean update(K key, V newValue) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.value = newValue;
                return true;
            }
            current = current.next;
        }
        return false; // Ключ не найден
    }

    // 6. Проверка наличия значения по ключу
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // Перехеширование
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[oldTable.length * 2];
        size = 0;

        for (Node<K, V> node : oldTable) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }

    // 7. Итерирование по элементам с обработкой ConcurrentModificationException
    @Override
    public Iterator<Node<K, V>> iterator() {
        return new Iterator<Node<K, V>>() {
            private int expectedModCount = modCount;
            private int bucketIndex = 0;
            private Node<K, V> nextNode = null;
            private int count = 0;

            {
                advanceToNext();
            }

            private void advanceToNext() {
                if (nextNode != null) {
                    nextNode = nextNode.next;
                }
                while (nextNode == null && bucketIndex < table.length) {
                    nextNode = table[bucketIndex++];
                }
            }

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Node<K, V> next() {
                if (modCount != expectedModCount) {
                    throw new ConcurrentModificationException("HashTable was modified during iteration!");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Node<K, V> current = nextNode;
                advanceToNext();
                count++;
                return current;
            }
        };
    }

    // 8. Сравнение на равенство с другой хеш-таблицей
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // Приводим объект к HashTable с типами <K, V>
        HashTable<K, V> that = (HashTable<K, V>) o;

        if (this.size != that.size) return false;

        for (Node<K, V> node : this) {
            if (!that.containsKey(node.key) ||
                    !Objects.equals(node.value, that.get(node.key))) {
                return false;
            }
        }
        return true;
    }

    // 9. Вывод в строку
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Node<K, V> node : this) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(node.key).append("=").append(node.value);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        System.out.println(hashTable.get("one"));
    }
}
