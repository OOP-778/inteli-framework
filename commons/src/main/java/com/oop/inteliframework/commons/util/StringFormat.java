package com.oop.inteliframework.commons.util;

import lombok.SneakyThrows;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormat {
    public static String format(String message, Object ...args) {
        if (message.contains("{}") && args.length > 0) {
            int currentObjectIndex = 0;
            int currentCharIndex = 0;
            char[] messageArray = message.toCharArray();

            StringBuilder builder = new StringBuilder();

            while (currentCharIndex < message.length()) {
                char currentChar = messageArray[currentCharIndex];
                if (currentChar == '{' && messageArray.length > currentCharIndex + 1) {
                    char nextChar = messageArray[currentCharIndex + 1];
                    if (nextChar == '}') {
                        currentCharIndex += 2;
                        if (args.length > currentObjectIndex) {
                            builder.append(Optional.ofNullable(args[currentObjectIndex]).map(Object::toString).orElse("null"));
                            currentObjectIndex += 1;
                        }
                        continue;
                    }
                }

                builder.append(currentChar);
                currentCharIndex++;
            }
            message = builder.toString();
        }

        return message;
    }

    private static final Pattern HEX = Pattern.compile("#(?:[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");

    @SneakyThrows
    public static String colored(String in) {
        if (InteliVersion.isOrAfter(16)) {
            Matcher matcher = HEX.matcher(in);

            while (matcher.find()) {
                String group = matcher.group();
                Method method = SimpleReflection.getMethod(net.md_5.bungee.api.ChatColor.class, "of", String.class);

                try {
                    in = in.replace(group, method.invoke(null, group).toString());
                } catch (Throwable ignored) {}
            }

        }
        in = ChatColor.translateAlternateColorCodes('&', in);
        return in;
    }
}
