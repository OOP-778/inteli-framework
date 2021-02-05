package com.oop.inteliframework.hologram.animation;

import com.oop.inteliframework.commons.util.InteliPair;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;

public class AnimationHelper {
    public static void init(String text) {
        TreeMap<String, AnimationProvider> providerTreeMap = new TreeMap<>(String::compareToIgnoreCase);
        List<ContentAnimation> animations = new ArrayList<>();

        List<InteliPair<Integer, Integer>> animationGroups = getGroups(text);
        for (InteliPair<Integer, Integer> animationGroup : animationGroups) {
            String match = text.substring(animationGroup.getKey(), animationGroup.getValue());
            match = match.substring(1, match.length() - 2);

            char[] matchChars = match.toCharArray();
            InteliPair<String, Integer> animationNamePair = matchTill(matchChars, 0, character -> !Character.isAlphabetic(character));
            String key = animationNamePair.getKey();

            match = match.substring(animationNamePair.getValue());

            // No options provided
            if (match.startsWith("=")) {
                String content = match.substring(1);

                AnimationProvider animationProvider = providerTreeMap.get(key);
                if (animationProvider != null)
                    animations.add(animationProvider.create(content, new TreeMap<>()));

            } else if (match.startsWith("[")) {
                InteliPair<String, Integer> optionsPair = matchTill(match.substring(1).toCharArray(), 0, character -> character == ']');
                System.out.println(optionsPair.getKey());

                String[] split = optionsPair.getKey().split(",");
                System.out.println(match.substring(3 + optionsPair.getValue()));
            }
        }

        String indexedText = text;
        for (int i = 0; i < animationGroups.size(); i++) {
            InteliPair<Integer, Integer> animationGroup = animationGroups.get(i);
            indexedText = indexedText.replace(text.substring(animationGroup.getKey(), animationGroup.getValue()), "$" + i);
        }
    }

    private static List<InteliPair<Integer, Integer>> getGroups(String text) {
        List<InteliPair<Integer, Integer>> animationGroups = new ArrayList<>();
        char[] chars = text.toCharArray();
        boolean isInGroup = false;
        int groupStart = -1;

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];

            if (character == '<') {
                groupStart = i;
                isInGroup = true;
                continue;
            }

            if (character == '/' && isInGroup && (chars.length > i + 1 && chars[i + 1] == '>'))
                animationGroups.add(new InteliPair<>(groupStart, i + 2));
        }

        return animationGroups;
    }

    private static InteliPair<String, Integer> matchTill(char[] chars, int starting, Predicate<Character> predicate) {
        int step = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = starting; i < chars.length; i++) {
            char character = chars[i];
            if (predicate.test(character))
                break;

            builder.append(chars[i]);
            step++;
        }

        return new InteliPair<>(builder.toString(), step);
    }

    public static void main(String[] args) {
        init("<wave[ interval=10,bigBrain=true]=HELLO/> non animated text <wave=BigBrain/> <wave=SmallBrain/>");
    }
}
