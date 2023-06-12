package Arrays.IntArray;

import javax.swing.*;
import java.util.*;

public class IntArray<T> implements List<T> {
    static final Integer INITIAL_CAPACITY = 3;
    Integer increasedCapacity = INITIAL_CAPACITY;
    T[] dataElements = (T[]) new Object[INITIAL_CAPACITY];
    int index = 0;
    private int modCount = 0;

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
    }

    public void rangeCheckForAdd(int index) {
        if (index < 0 || index > dataElements.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        for (int to = size(), i = (index -= hi - lo); i < to; i++) {
            es[i] = null;
        }
    }

    @Override
    public int size() {
        return index;
    }

    @Override
    public boolean isEmpty() {
        return dataElements.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (T element : dataElements) {
            if (o.equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new CustomIterator<T>();
    }

    @Override
    public Object[] toArray() {
        Object[] object = new Object[index];
        for (int i = 0; i < index; i++) {
            object[i] = dataElements[i];
        }
        return object;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a.length <= index) {
            return (T1[]) Arrays.copyOf(dataElements, index, a.getClass());
        }
        System.arraycopy(dataElements, 0, a, 0, index);
        return a;
    }

    @Override
    public boolean add(T t) {
        modCount++;
        if (index > increasedCapacity - 1) {
            dataElements = (T[]) growArray();
        }
        dataElements[index] = (T) t;
        index++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int i = 0; i < index; i++) {
                if (dataElements[i] == null) {
                    remove(i);
                    return true;
                }
            }
        } else {
            for (int i = 0; i < index; i++) {
                if (o.equals(dataElements[i])) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object ob : c) {
            if (!contains(ob))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T t : c) {
            if (add(t)) {
                modified = true;
            }
        }
        modCount++;
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        rangeCheckForAdd(index);
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0) {
            return false;
        }
        modCount++;
        int s;
        if (numNew > dataElements.length - (s = this.size())) {
            dataElements = growArray();
        }
        int numMoved = s - index;
        if (numMoved > 0) {
            System.arraycopy(dataElements, index, dataElements, index + numNew, numMoved);
        }
        System.arraycopy(a, 0, dataElements, index, numNew);
        this.index = s + numNew;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (int i = 0; i < index; i++) {
            if (c.contains(dataElements[i])) {
                remove(i);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size());
    }

    boolean batchRemove(Collection<?> c, boolean complement, final int from, final int end) {
        Objects.requireNonNull(c);// checks if the object is null
        final Object[] es = dataElements;
        int r;
        for (r = from; ; r++) {
            if (r == end) {
                return false;
            }
            if (c.contains(es[r]) != complement) { // if doesnt contain break
                break;
            }
        }
        int w = r++;
        try {
            for (Object e; r < end; r++) {
                if (c.contains(e = es[r]) == complement) {
                    es[w++] = e;
                }
            }
        } catch (Throwable ex) {
            throw ex;
        } finally {
            shiftTailOverGap(es, w, end);
        }
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < index; i++) {
            dataElements[i] = null;
        }
        index = 0;
    }

    @Override
    public T get(int index) {
        return (T) dataElements[index];
    }

    @Override
    public T set(int indexToSet, T element) {
        Objects.checkIndex(indexToSet, index);
        T oldValue = dataElements[indexToSet];
        dataElements[indexToSet] = element;
        return oldValue;
    }

    @Override
    public void add(int indexToAdd, T element) {
        Objects.checkIndex(indexToAdd, index + 1);
        if (indexToAdd < index) {
            dataElements[indexToAdd] = element;
        }
        if (indexToAdd == index) {
            dataElements = growArray();
            dataElements[index] = element;
            index++;
        }
    }

    @Override
    public T remove(int indexToRemove) {
        T[] anotherArray = (T[]) new Object[index - 1];
        T removedElement = null;
        for (int i = 0, k = 0; i < index; i++) {
            if (i == indexToRemove) {
                removedElement = (T) dataElements[i];
                continue;
            }
            anotherArray[k++] = (T) dataElements[i];
        }
        dataElements = anotherArray;
        index--;
        return removedElement;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < index; i++) {
                if (dataElements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < index; i++) {
                if (o.equals(dataElements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = index - 1; i >= 0; i++) {
                if (dataElements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = index - 1; i >= 0; i++) {
                if (o.equals(dataElements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size());
        return new SubList<>(this, fromIndex, toIndex);
    }

    private T[] growArray() {
        increasedCapacity = increasedCapacity * 2;
        return Arrays.copyOf(dataElements, increasedCapacity);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        rangeCheckForAdd(index);
        return new ListItr(index);
    }

    class CustomIterator<T> implements Iterator<T> {
        int cursor = 0;
        int lastRet = -1;
        int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public T next() {
            checkForComodification();
            try {
                int i = cursor;
                T next = (T) get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e);
            }
        }

        public void remove() {
            if (cursor < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                IntArray.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    class ListItr extends CustomIterator<T> implements ListIterator<T> {
        ListItr(int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public T previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                T previous = get(i);
                lastRet = cursor - 1;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e);
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(T t) {
            if (lastRet < 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            try {
                IntArray.this.set(lastRet, t);
            } catch (IndexOutOfBoundsException ex) {
                System.out.println(ex);
            }
        }

        @Override
        public void add(T t) {
            checkForComodification();
            try {
                int i = cursor;
                IntArray.this.add(i, t);
                lastRet = -1;
                cursor = i + 1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class SubList<T> extends AbstractList<T> {
        private final IntArray<T> root;
        private final SubList<T> parent;
        private final int offset;
        protected int size;

        public SubList(IntArray<T> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        protected SubList(SubList<T> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }


        @Override
        public T get(int index) {
            checkForComodification();
            Objects.checkIndex(index, size);
            return root.get(offset + index);
        }

        @Override
        public int size() {
            checkForComodification();
            return size;
        }

        public void add(int index, T element) {
            IntArray.this.rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        public T remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            T result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        public boolean addAll(Collection<? extends T> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends T> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize == 0)
                return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }

        public Object[] toArray() {
            checkForComodification();
            return root.toArray();
        }

        public <T> T[] toArray(T[] a) {
            checkForComodification();
            return root.toArray(a);
        }

        public int indexOf(Object o) {
            checkForComodification();
            return root.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            checkForComodification();
            return root.lastIndexOf(o);
        }

        public Iterator<T> iterator() {
            checkForComodification();
            return listIterator();
        }

        public ListIterator<T> listIterator(int index) {
            checkForComodification();

            return new ListIterator<T>() {
                private final ListIterator<T> i = root.listIterator(offset + index);

                @Override
                public boolean hasNext() {
                    return nextIndex() < size;
                }

                @Override
                public T next() {
                    if (hasNext()) {
                        return i.next();
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                @Override
                public T previous() {
                    if (hasPrevious()) {
                        return i.previous();
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public int nextIndex() {
                    return i.nextIndex() - offset;
                }

                @Override
                public int previousIndex() {
                    return i.previousIndex() - offset;
                }

                @Override
                public void remove() {
                    i.remove();
                    updateSizeAndModCount(-1);
                }

                @Override
                public void set(T t) {
                    i.set(t);
                }

                @Override
                public void add(T t) {
                    i.add(t);
                    updateSizeAndModCount(1);
                }
            };
        }

        private void checkForComodification() {
            if (root.modCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }

        private void updateSizeAndModCount(int sizeChange) {
            SubList<T> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while (slist != null);
        }

    }
}
