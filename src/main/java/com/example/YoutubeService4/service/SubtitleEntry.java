package com.example.YoutubeService4.service;

//SpeechMArks.java is parent
public class SubtitleEntry {
    private int time; // Time in milliseconds
    private String type; // Type (e.g., "word")
    private String value; // The word or phrase

    // Getters and setters
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Convert time in milliseconds to seconds
    public double getTimeInSeconds() {
        return time / 1000.0;
    }

    @Override
    public String toString() {
        return "SubtitleEntry{" +
               "time=" + time +
               ", type='" + type + '\'' +
               ", value='" + value + '\'' +
               '}';
    }
}