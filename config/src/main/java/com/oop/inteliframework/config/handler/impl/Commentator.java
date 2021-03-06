package com.oop.inteliframework.config.handler.impl;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.InteliTriPair;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

public class Commentator {
    public static Map<String, List<String>> comments(String[] array) {
        array = Arrays
                .stream(array)
                .filter(line -> line.trim().length() != 0)
                .toArray(String[]::new);

        Map<String, List<String>> comments = new LinkedHashMap<>();
        InteliTriPair<String[], Integer, Integer> headerScrap = scrapLinesFromTill(
                array,
                line -> StringUtils.trim(line).contains("<--------------->"),
                line -> StringUtils.trim(line).contains("<--------------->")
        );

        if (headerScrap.getThird() == -1)
            headerScrap.setThird(0);
        else
            headerScrap.setThird(headerScrap.getThird() + 1);

        LinkedList<String> currentPath = new LinkedList<>();
        int currentPathSpace = 0;

        List<String> nextComments = new LinkedList<>();
        for (int i = headerScrap.getThird(); i < array.length; i++) {
            String line = array[i];

            // If line is a comment
            if (isComment(line)) {
                InteliPair<String[], Integer> pair = scrapLinesTill(i, array, l -> !isComment(l));
                nextComments.addAll(Arrays.asList(pair.getKey()));
                i += pair.getValue();
                continue;
            }

            if (startsWithIdentifier(line)) {
                String[] split = line.split(":");

                // Check for list
                boolean isList = array[i + 1].trim().startsWith("-");

                // We got a section
                if ((split.length == 1 || split[1].trim().length() == 0) && !isList) {
                    int spaces = spacesTillChar(split[0]);

                    if (spaces + 2 == currentPathSpace) {
                        currentPath.removeLast();
                        currentPath.offer(split[0].trim());

                    } else {
                        if (currentPathSpace > spaces) {
                            int times = currentPathSpace / 2;
                            for (int i2 = 0; i2 < times; i2++)
                                if (!currentPath.isEmpty())
                                    currentPath.removeLast();
                        }

                        currentPath.offer(split[0].trim());
                        currentPathSpace = spaces + 2;
                    }

                    if (!nextComments.isEmpty()) {
                        comments.put(String.join(".", currentPath), new ArrayList<>(nextComments));
                        nextComments.clear();
                    }
                }

                if (!nextComments.isEmpty()) {
                    comments.put(String.join(".", currentPath) + "." + split[0].trim(), new ArrayList<>(nextComments));
                    nextComments.clear();
                }
            }
        }
        return comments;
    }

    private static int spacesTillChar(String line) {
        int count = 0;
        char[] chars = line.toCharArray();
        for (char aChar : chars) {
            if (aChar == ' ')
                count++;
            else
                break;
        }

        return count;
    }

    private static boolean isComment(String line) {
        return line.trim().startsWith("#");
    }

    private static boolean startsWithIdentifier(String line) {
        return !line.trim().startsWith("#") && StringUtils.contains(line, ":");
    }

    private static InteliTriPair<String[], Integer, Integer> scrapLinesFromTill(String[] array, Predicate<String> startPredicate, Predicate<String> endPredicate) {
        List<String> linesScrapped = new ArrayList<>();
        int start = -1;
        int end = -1;

        for (int i = 0; i < array.length; i++) {
            if (start == -1 && startPredicate.test(array[i])) {
                start = i;
                continue;
            }

            if (start != -1 && endPredicate.test(array[i])) {
                end = i;
                break;
            }

            if (start != -1)
                linesScrapped.add(array[i]);
        }

        return new InteliTriPair<>(linesScrapped.toArray(new String[0]), start, end);
    }

    private static InteliPair<String[], Integer> scrapLinesTill(int start, String[] array, Predicate<String> predicate) {
        List<String> linesScrapped = new ArrayList<>();
        int count = 0;

        for (int i = start; i < array.length; i++) {
            if (predicate.test(array[i]))
                break;

            count++;
            linesScrapped.add(array[i]);
        }

        return new InteliPair<>(linesScrapped.toArray(new String[0]), count - 1);
    }
}
