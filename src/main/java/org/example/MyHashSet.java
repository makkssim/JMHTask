package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class MyHashSet<T> extends AbstractSet<T> {

    private final int bits;

    private final int capacity;

    private final Object[] storage;

    private int size = 0;

    public MyHashSet(int bits, int capacity, Object[] storage) {
        this.bits = bits;
        this.capacity = capacity;
        this.storage = storage;
    }

    private int startingIndex(Object element) {
        return element.hashCode() & (0x7FFFFFFF >> (31 - bits));
    }

    private static class Removed {
    }

    private final Removed removed = new Removed();

    public MyHashSet(int bits) {
        if (bits < 2 || bits > 31) {
            throw new IllegalArgumentException();
        }
        this.bits = bits;
        capacity = 1 << bits;
        storage = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    @Override
    public boolean contains(Object o) {
        int index = startingIndex(o);
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) {
                return true;
            }
            index = (index + 1) % capacity;
            current = storage[index];
        }
        return false;
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    @Override
    public boolean add(T t) {
        int startingIndex = startingIndex(t);
        int index = startingIndex;
        Object current = storage[index];
        while (current != null && current != removed) {
            if (current.equals(t)) {
                return false;
            }
            index = (index + 1) % capacity;
            if (index == startingIndex) {
                throw new IllegalStateException("Table is full");
            }
            current = storage[index];
        }
        storage[index] = t;
        size++;
        return true;
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     *
     * Средняя
     */
    //трудоемкость O(n)
    //ресурсоемкость O(1)
    @Override
    public boolean remove(Object o) {
        if (!contains(o)) return false;
        int index = startingIndex(o);
        Object cur = storage[index];
        while(cur != null){
            if(cur.equals(o)){
                size--;
                storage[index] = removed;
                return true;
            }
            index = (index + 1) % capacity;
            cur = storage[index];
        }
        return false;
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new OpenAddressingSetIterator();
    }

    public class OpenAddressingSetIterator implements Iterator<T> {
        int numberOfIterations = 0;
        int ind = -1;
        Object cur;

        //трудоемкость O(1)
        //ресурсоемкость O(1)
        @Override
        public boolean hasNext() {
            return size > numberOfIterations;
        }

        //трудоемкость O(n)
        //ресурсоемкость O(1)
        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            cur = null;
            while(cur == null || cur == removed){
                ind++;
                cur = storage[ind];
            }
            numberOfIterations++;
            return (T) cur;
        }

        //трудоемкость O(1)
        //ресурсоемкость O(1)
        @Override
        public void remove() {
            if(cur == null || ind < 0) throw new IllegalStateException();
            storage[ind] = removed;
            size--;
            cur = null;
            numberOfIterations--;
        }
    }
}

