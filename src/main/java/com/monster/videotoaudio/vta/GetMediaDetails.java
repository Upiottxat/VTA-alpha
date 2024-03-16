package com.monster.videotoaudio.vta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetMediaDetails {
    private final String filePath;
    Path path=new Path();
    private static final String ffmpeg;

    static {
        try {
            ffmpeg = new java.io.File(".").getCanonicalFile()+"/../ffmpeg/bin/ffmpeg";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
//private static final String ffmpeg="ffmpeg";
    public GetMediaDetails(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        long durationInSeconds = getMediaDuration(filePath);

        if (durationInSeconds != -1) {
            System.out.println("Media Duration: " + durationInSeconds + " seconds");
        } else {
            System.out.println("Error retrieving media duration.");
        }

        return durationInSeconds;
    }

    private static long getMediaDuration(String filePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg, "-i", filePath);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            long duration = -1;

            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    String[] durationParts = line.split(" ")[3].split(":");
                    int hours = Integer.parseInt(durationParts[0]);
                    int minutes = Integer.parseInt(durationParts[1]);

                    // Remove non-numeric characters from seconds part
                    String secondsStr = durationParts[2].replaceAll("[^0-9.]", "");
                    float seconds = Float.parseFloat(secondsStr);

                    duration = hours * 3600L + minutes * 60L + Math.round(seconds);
                    break;
                }
            }

            process.waitFor();
            return duration;
        } catch (IOException | InterruptedException e) {
            // Handle the exception based on your application's requirements
            e.printStackTrace();
            return -1;
        }
    }
}