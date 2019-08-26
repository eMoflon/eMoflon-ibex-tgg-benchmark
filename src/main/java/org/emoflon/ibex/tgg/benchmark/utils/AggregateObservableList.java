package org.emoflon.ibex.tgg.benchmark.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sun.javafx.collections.ListListenerHelper;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * AggregateObservableList
 */
public class AggregateObservableList<E> implements ObservableList<E> {

    private final ObservableList<ObservableList<E>> lists;
    private ListListenerHelper<E> listenerHelper;
    private List<ListChangeListener> listChangeListenerList;
    private List<InvalidationListener> invalidationListenerList;
    private int size;

    public AggregateObservableList() {
        lists = FXCollections.observableArrayList();
        listChangeListenerList = new LinkedList<>();
        invalidationListenerList = new LinkedList<>();
        size = 0;
    }

    public void addLists(Collection<ObservableList<E>> lists) {
        for (ObservableList<E> list : lists) {
            this.addList(list);
        }
    }

    public boolean addList(ObservableList<E> list) {
        for (ListChangeListener listener : listChangeListenerList) {
            list.addListener(listener);
        }
        for (InvalidationListener listener : invalidationListenerList) {
            list.addListener(listener);
        }
        return lists.add(list);
    }

    public boolean removeList(ObservableList<E> list) {
        return lists.remove(list);
    }

    private void updateSize() {
        size = 0;
        for (ObservableList<E> list : lists) {
            size += list.size();
        }
    }

    @Override
    public E get(int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            // it's not efficient but it dosn't have to be anyway
            int aggregatedSize = 0;
            for (ObservableList<E> list : lists) {
                if (aggregatedSize + list.size() > index) {
                    return list.get(index - aggregatedSize);
                }
                aggregatedSize += list.size();
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addListener(ListChangeListener listener) {
        listChangeListenerList.add(listener);
        listenerHelper = ListListenerHelper.addListener(listenerHelper, listener);
        for (ObservableList<E> list : lists) {
            list.addListener(listener);
        }
    }

    @Override
    public void removeListener(ListChangeListener listener) {
        listChangeListenerList.remove(listener);
        listenerHelper = ListListenerHelper.removeListener(listenerHelper, listener);
        for (ObservableList<E> list : lists) {
            list.removeListener(listener);
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerList.add(listener);
        listenerHelper = ListListenerHelper.addListener(listenerHelper, listener);
        for (ObservableList<E> list : lists) {
            list.addListener(listener);
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerList.remove(listener);
        listenerHelper = ListListenerHelper.removeListener(listenerHelper, listener);
        for (ObservableList<E> list : lists) {
            list.removeListener(listener);
        }
    }

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        lists.clear();
    }

    @Override
    public boolean contains(Object o) {
        for (ObservableList list : lists) {
            if (list.contains(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Object... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int from, int to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Object... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Object... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Object... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection col) {
        throw new UnsupportedOperationException();
    }

}