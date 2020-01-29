package com.cinimex.tasks;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager<K, V> {

    private volatile ConcurrentHashMap<Key, V> repository = new ConcurrentHashMap<>();
    private long timeLeft;

    public FileManager(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * The method adds an item to the cache
     *
     * @param key   - the unique key
     * @param value - the value to store
     */
    public void put(K key, V value) {
        repository.put(new Key(key, timeLeft), value);
    }

    /**
     * The method retrieves an item from the cache
     *
     * @param key - unique key for getting the value
     * @return an object by key
     */
    public V get(K key) {
        return repository.get(new Key(key, timeLeft));
    }

    /**
     * The method removes an item from the cache
     *
     * @param key - unique key for getting the value
     */
    public void remove(K key) {
        repository.remove(new Key(key, timeLeft));
    }

    /**
     * @return the number of items in the cache
     */
    public int size() {
        return repository.size();
    }


    private static class Key {
        private final Object key;
        private long lifeTime;

        /**
         * @param key     - The key by which we get access to the object.
         * @param timeout - The lifetime of the object.
         */
        public Key(Object key, long timeout) {
            this.key = key;
            this.lifeTime = System.currentTimeMillis() + timeout;
        }

        public Key(Object key) {
            this.key = key;
        }

        /**
         * @return true or false depending on the lifetime of the object
         * false if current time more than lifetime of the object in the cache
         * true  if current time less than lifetime of the object in the cache
         */
        public boolean isLive(long currentTimeMillis) {
            return currentTimeMillis < lifeTime;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 43 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return "Key{" + "key=" + key + '}';
        }
    }
}
