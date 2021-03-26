package com.oop.inteliframework.animation;

import com.oop.inteliframework.animation.provider.AnimationProvider;
import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.plugin.InteliPlatform;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

public class AnimationParser {
  public static AnimatedText parse(String text) {
    // Find all groups in a text that start with < and end with />
    List<InteliPair<Integer, Integer>> animationGroups = getGroups(text);

    int animationIndex = 0;
    String indexedText = text;

    List<ContentAnimation> animations = new LinkedList<>();

    for (InteliPair<Integer, Integer> groupPair : animationGroups) {
      String group = text.substring(groupPair.getKey(), groupPair.getValue());

      // Remove />
      String finalGroupCopy = group;
      group = group.substring(1, group.length() - 2);
      char[] groupCharacters = group.toCharArray();

      // Parse animation name
      InteliPair<String, Integer> animationNamePair =
              matchTill(groupCharacters, 0, character -> !Character.isAlphabetic(character));
      String animationName = animationNamePair.getKey();

      // Check if animation is valid
      AnimationProvider animationProvider = InteliPlatform.getInstance().safeModuleByClass(InteliAnimationModule.class)
              .getProviderTreeMap()
              .get(animationName);
      Preconditions.checkArgument(animationProvider != null, "Failed to find animation provider by name: " + animationName);

      // Get rid of the animation name from the group
      group = group.substring(animationNamePair.getValue());

      // Split the animation group to match where are properties if present and where is text
      String[] split = StringUtils.split(group, "=");

      // Do we have properties?
      Map<String, Object> props = new HashMap<>();
      int textIndex = split.length -1;

      if (split.length == 2) {
        String propsString = StringUtils.replace(split[0], " ", "");
        propsString = propsString.substring(1, propsString.length()-1);

        String[] propsSplit = StringUtils.split(propsString, ",");
        for (String s : propsSplit) {
          String[] propSplit = StringUtils.split(s, ":");
          String key = propSplit[0];

          if (propSplit.length == 2) {
            // TODO: parse value
            props.put(key, propSplit[1]);
            continue;
          }

          props.put(key, true);
        }
      }

      String groupText = split[textIndex];
      ContentAnimation contentAnimation = animationProvider.create(groupText, props);

      animations.add(contentAnimation);
      indexedText = StringUtils.replaceOnce(indexedText, finalGroupCopy, "$" + animationIndex);
      animationIndex++;
    }

    return new AnimatedText(indexedText, animations);
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

  private static InteliPair<String, Integer> matchTill(
          char[] chars, int starting, Predicate<Character> predicate) {
    int step = 0;
    StringBuilder builder = new StringBuilder();
    for (int i = starting; i < chars.length; i++) {
      char character = chars[i];
      if (predicate.test(character)) break;

      builder.append(chars[i]);
      step++;
    }

    return new InteliPair<>(builder.toString(), step);
  }

}
