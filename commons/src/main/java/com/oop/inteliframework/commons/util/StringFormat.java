package com.oop.inteliframework.commons.util;

import java.util.Optional;

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
}
