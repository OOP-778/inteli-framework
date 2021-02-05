package com.oop.inteliframework.config.configuration.handler.impl.yaml;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.InteliTriPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

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

        if (headerScrap.getThird() == -1) {
            headerScrap.setThird(0);
        } else {
            headerScrap.setThird(headerScrap.getThird() + 1);
            comments.put("#", transformDescription(headerScrap.getFirst()));
        }

        LinkedList<String> currentPath = new LinkedList<>();
        int currentPathSpace = 0;

        List<String> nextComments = new LinkedList<>();
        for (int i = headerScrap.getThird(); i < array.length; i++) {
            String line = array[i];

            // If line is a comment
            if (isComment(line)) {
                InteliPair<String[], Integer> pair = scrapLinesTill(i, array, l -> !isComment(l));
                nextComments.addAll(transformDescription(pair.getKey()));

                i += pair.getValue();
                continue;
            }

            if (startsWithIdentifier(line)) {
                String[] split = line.split(":");

                // Check for list
                boolean isList = (array.length - 1) != i && array[i + 1].trim().startsWith("-");

                // We got a section
                int spaces = spacesTillChar(split[0]);
                if ((split.length == 1 || split[1].trim().length() == 0) && !isList) {

                    if (spaces + 2 == currentPathSpace) {
                        if (!currentPath.isEmpty()) {
                            currentPath.removeLast();
                        }
                        currentPath.offer(split[0].trim());

                    } else {
                        if (currentPathSpace > spaces) {
                            int times = currentPathSpace / 2;
                            for (int i2 = 0; i2 < times; i2++) {
                                if (!currentPath.isEmpty()) {
                                    currentPath.removeLast();
                                }
                            }
                        }

                        currentPath.offer(split[0].trim());
                        currentPathSpace = spaces + 2;
                    }

                    if (!nextComments.isEmpty()) {
                        comments.put(String.join(".", currentPath), new ArrayList<>(nextComments));
                        nextComments.clear();
                    }
                } else {
                    if (currentPathSpace - 2 == spaces) {
                        currentPath.removeLast();
                        currentPathSpace -= 2;
                    }

                    if (spaces == 0 && !currentPath.isEmpty()) {
                        currentPath.clear();
                    }
                }

                if (!nextComments.isEmpty()) {
                    List<String> path = new ArrayList<>(currentPath);
                    path.add(split[0].trim());
                    comments.put(String.join(".", path), new ArrayList<>(nextComments));
                    nextComments.clear();
                }
            }
        }
        return comments;
    }

    private static List<String> transformDescription(String[] desc) {
        return Arrays
            .stream(desc)
            .map(d -> d.trim().substring(1).trim())
            .collect(Collectors.toList());
    }

    private static int spacesTillChar(String line) {
        int count = 0;
        char[] chars = line.toCharArray();
        for (char aChar : chars) {
            if (aChar == ' ') {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    private static boolean isComment(String line) {
        return line.trim().startsWith("#");
    }

    private static boolean startsWithIdentifier(String line) {
        return !line.trim().startsWith("#") && StringUtils.contains(line, ":");
    }

    private static InteliTriPair<String[], Integer, Integer> scrapLinesFromTill(String[] array,
        Predicate<String> startPredicate, Predicate<String> endPredicate) {
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

            if (start != -1) {
                linesScrapped.add(array[i]);
            }
        }

        return new InteliTriPair<>(linesScrapped.toArray(new String[0]), start, end);
    }

    private static InteliPair<String[], Integer> scrapLinesTill(int start, String[] array,
        Predicate<String> predicate) {
        List<String> linesScrapped = new ArrayList<>();
        int count = 0;

        for (int i = start; i < array.length; i++) {
            if (predicate.test(array[i])) {
                break;
            }

            count++;
            linesScrapped.add(array[i]);
        }

        return new InteliPair<>(linesScrapped.toArray(new String[0]), count - 1);
    }
}
