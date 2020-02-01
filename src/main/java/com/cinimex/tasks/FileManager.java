package com.cinimex.tasks;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager<K, V> {

    private volatile Map<Key, V> repository = new ConcurrentHashMap<>();
    private long timeToLive;
    private long checkTime;

    /**
     * Default, the check time for deleting "expired" elements = = timeToLive
     * @param timeToLive - lifetime of an item in the cache
     */
    public FileManager(long timeToLive) {
        this.timeToLive = timeToLive;
        this.checkTime = timeToLive;
    }

    /**
     * Thread for checking "expired" elements
     */
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        removeExpired();
                        Thread.sleep(checkTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * The method adds an item to the cache
     * @param key   - the unique key
     * @param value - the value to store
     */
    public void put(K key, V value) {
        repository.put(new Key(key, timeToLive), value);
    }

    /**
     * The method retrieves an item from the cache
     * @param key - unique key for getting the value
     * @return an object by key
     */
    public V get(K key) {
        return repository.get(new Key(key));
    }

    /**
     * The method removes an item from the cache
     * @param key - unique key for getting the value
     */
    public void remove(K key) {
        repository.remove(new Key(key));
    }

    /**
     * Deletes items from the cache that have expired storage time
     */
    public void removeExpired() {
        long current = System.currentTimeMillis();

        for (Key k : repository.keySet()) {
            if (k.isDead(current)) {
                repository.remove(k);
            }
        }
    }

    /**
     * Deletes all items from the cache
     */
    public void removeAll() {
        repository.clear();
    }

    /**
     * Checks whether the cache contains an item in the collection by key
     * @param key - unique key for getting the value
     * @return - true if contains. false if not contain
     */
    public boolean containsItem(K key) {
        removeExpired();
        V value = get(key);
        return value != null;
    }

    /**
     * Method for updating the time when an item was added.
     * @param key - unique key for getting the value.
     * @param dateCreation - time in millis.
     */
    public void refreshCreationDate(K key, long dateCreation) {
        for (Key k : repository.keySet()) {
            if (k.getKey().equals(key)) {
                k.refreshDateCreation(dateCreation);
            }
        }
    }

    /**
     * Method for getting the time when an item was added.
     * @param key - unique key for getting the value.
     * @return - creation date in millis.
     */
    public long getTimeToLiveForElement(K key) {
        long dk = 0;
        for (Key k : repository.keySet()) {
            if (k.getKey().equals(key)) {
                dk = k.getDateCreation();
            }
        }
        return dk;
    }


    /**
     * @return the number of items in the cache
     */
    public int size() {
        return repository.size();
    }

    /**
     * Allows you to change the standard lifetime of an object.
     * @param timeToLive - time live in millis.
     */
    public void setDefaultTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Getter timeToLive for test
     */
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * Allows you to get the time after which "expired" objects are deleted.
     * @return checkTime in millis.
     */
    public long getCheckTime() {
        return checkTime;
    }

    /**
     * Sets the scan time for the cache to automatically remove the "expired" elements.
     * @param checkTime - time in millis
     */
    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    private static class Key {
        private final Object key;
        private long lifeTime;
        private long dateCreation;

        /**
         * @param key     - The key by which we get access to the object.
         * @param timeout - The lifetime of the object.
         * @value dateCreation  - Time when the item was added
         */
        public Key(Object key, long timeout) {
            this.key = key;
            this.lifeTime = timeout;
            this.dateCreation = System.currentTimeMillis();
        }

        public Key(Object key) {
            this.key = key;
        }

        /**
         * @return true or false depending on the lifetime of the object
         * false if current time more than lifetime of the object in the cache
         * true  if current time less than lifetime of the object in the cache
         */
        public boolean isDead(long currentTimeMillis) {
            return currentTimeMillis - dateCreation > lifeTime;
        }

        /**
         * Method for updating the time when an item was added.
         * @param dateCreation - time when an item was added to the cache.
         */
        public void refreshDateCreation(long dateCreation) {
            System.out.println("body dateCreat: " + dateCreation);
            this.dateCreation = dateCreation;
        }

        public long getDateCreation() {
            return dateCreation;
        }

        public Object getKey() {
            return key;
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
