package cmsc256;

import java.util.Iterator;

public class HashTableQuadraticProbing<K, V> extends HashTableOpenAddressing<K, V>{
    public static void main(String[] args){
        HashTableQuadraticProbing<String, Integer> purchases = new HashTableQuadraticProbing<>();

        String[] names = {"Pax", "Eleven", "Angel", "Abigail", "Jack"};
        purchases.put(names[0], 654);
        purchases.put(names[1], 341);
        purchases.put(names[2], 70);
        purchases.put(names[3], 867);
        purchases.put(names[4], 5309);

        System.out.println("Contents with linear probing:\n" + purchases.toString());

        System.out.println("Replaced old value was " + purchases.put(names[1], 170));
        System.out.println("Contents after changing Eleven to 170:\n" + purchases.toString());

        System.out.println("Calling getValue() on Pax, Eleven, & Angel:");
        System.out.println("\tPax: " + purchases.getValue(names[0]));
        System.out.println("\tEleven: " + purchases.getValue(names[1]));
        System.out.println("\tAngel: " + purchases.getValue(names[2]));

        purchases.remove(names[0]);
        purchases.remove(names[2]);
        System.out.println("Contents after removing Pax & Angel:\n" + purchases);

        purchases.put("Gino", 348);
        System.out.println("Contents after adding Gino:\n" + purchases);

        Iterator<String> keyIter = purchases.getKeyIterator();
        Iterator<Integer> valueIter = purchases.getValueIterator();
        System.out.println("Contents of the hash table:");
        while(keyIter.hasNext())
            System.out.println("Key-" + keyIter.next() + " : Value-" + valueIter.next());
    }

    public int quadraticProbe(int index, K keyIn) {
        boolean found = false;
        int removedStateIndex = -1; // Index of first removed location
        int indexx = index;
        if(table[index] == null)    // The hash index is available
            return index;
        int i = 1;
        while (!found && table[index] != null) {
            if (table[index].isIn()) {
                if (keyIn.equals(table[index].getKey()))
                    found = true;       // Key found
                else                     // Follow probe sequence
                    index = (int)(indexx + Math.pow(i,2)) % table.length;
            }
            else {                              // Skip entries that were removed
                // Save index of first location in removed state
                if (removedStateIndex == -1)
                    removedStateIndex = index;
                index = (int) (indexx + Math.pow(i, 2)) % table.length;
            }
            i++;
        }
        if (found || (removedStateIndex == -1) )
            return index;             // Index of either key or null
        else
            return removedStateIndex; // Index of an available location
    }

    @Override
    public V put(K key, V value) {
        if(key == null || value == null){
            throw new IllegalArgumentException();
        }
        V oldValue = null;
        Entry<K, V> newEntry = new Entry<>(key,value);
        int index = super.getHashIndex(key);
        index = quadraticProbe(index, key);
        if((table[index] == null)||(table[index].isRemoved())){
            table[index] = newEntry;
            numEntries++;
        }
        else {
            oldValue = table[index].getValue();
            table[index].setValue(value);
        }
        if(isFull()){
            enlargeHashTable();
        }
        return oldValue;
    }

    @Override
    public V remove(K key) {
        V removedValue = null;
        int index = super.getHashIndex(key);
        index = quadraticProbe(index, key);
        if (contains(key)){
            removedValue = getValue(key);
            table[index].setToRemoved();
            numEntries--;
        }
        return removedValue;
    }

    @Override
    public V getValue(K key) {
        int index = super.getHashIndex(key);
        Entry<K, V> item = table[index];
        if((item != null) && item.isIn()){
            return item.getValue();
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }
}
