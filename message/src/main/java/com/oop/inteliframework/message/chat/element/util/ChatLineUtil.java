package com.oop.inteliframework.message.chat.element.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatLineUtil {
  public int findSpaces(String text, boolean reverse) {
    char[] chars = text.toCharArray();
    if (reverse) reverseArray(chars);

    int found = 0;
    for (char chaz : chars) {
      if (chaz == ' ') found++;
      else return found;
    }
    return found;
  }

  public void reverseArray(char[] a) {
    int n = a.length;
    char i, k, t;
    for (i = 0; i < n / 2; i++) {
      t = a[i];
      a[i] = a[n - i - 1];
      a[n - i - 1] = t;
    }
  }

  public String getNextOrNull(char[] array, int amount) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < array.length; i++) {
      if (i == amount) return builder.toString();

      char character = array[i];
      builder.append(character);
    }

    return null;
  }
}
