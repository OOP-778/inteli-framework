package com.oop.inteliframework.scoreboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardCache {
    private Map<Integer, String> lines = new ConcurrentHashMap<>();
    private String title;

    public Map<Integer, String> getLines() {
        return lines;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
