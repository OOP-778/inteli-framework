package com.oop.inteliframework.commons.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InsertableList<T> implements List<T> {

  private final LinkedList<T> list = new LinkedList<>();

  public int insert(int index, InsertMethod method, T... what) {
    switch (method) {
      case AFTER:
        for (T object : what) add(index += 1, object);
        break;

      case BEFORE:
        for (T object : what) add(index -= 1, object);
        break;

      case REPLACE:
        boolean replaced = false;
        for (T object : what) {
          if (!replaced) {
            replaced = true;
            set(index, object);
            continue;
          }
          add(index += 1, object);
        }
    }
    return index;
  }

  public T element() {
    return list.element();
  }

  public int insert(int index, T... what) {
    return insert(index, InsertMethod.AFTER, what);
  }

  public int insert(T where, InsertMethod method, T... what) {
    return insert(indexOf(where), method, what);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(o);
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }

  @NotNull
  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @NotNull
  @Override
  public <T1> T1[] toArray(@NotNull T1[] a) {
    return list.toArray(a);
  }

  @Override
  public boolean add(T t) {
    return list.add(t);
  }

  @Override
  public boolean remove(Object o) {
    return list.remove(o);
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends T> c) {
    return list.addAll(c);
  }

  @Override
  public boolean addAll(int index, @NotNull Collection<? extends T> c) {
    return list.addAll(index, c);
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    return list.removeAll(c);
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    return list.retainAll(c);
  }

  @Override
  public void clear() {
    list.clear();
  }

  @Override
  public T get(int index) {
    return list.get(index);
  }

  @Override
  public T set(int index, T element) {
    return list.set(index, element);
  }

  @Override
  public void add(int index, T element) {
    list.add(index, element);
  }

  @Override
  public T remove(int index) {
    return list.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  @NotNull
  @Override
  public ListIterator<T> listIterator() {
    return list.listIterator();
  }

  @NotNull
  @Override
  public ListIterator<T> listIterator(int index) {
    return list.listIterator(index);
  }

  @NotNull
  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

  public static enum InsertMethod {
    AFTER,
    BEFORE,
    REPLACE
  }
}
