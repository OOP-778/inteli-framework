package com.oop.inteliframework.message.api;

import java.util.Map;

/**
 * Main class for handling locale
 */
public interface LocaleController {

    /**
     * Clear locale entries
     */
    void clear();

    /**
     * Create new locale as the key identifier
     *
     * @param identifier the name ex. en-us
     * @param messageMap map containing all the messages
     */
    void newLocale(String identifier, Map<String, InteliMessage> messageMap);

}
