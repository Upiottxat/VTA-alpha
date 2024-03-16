package com.monster.videotoaudio.vta;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class Convertor {
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
     String ffprobe="ffprobe.exe";
    ScreenController Controller;
    int totalFilesConverted=0;

    int totalFiles=0;
    double percent;
    public Convertor(ScreenController screenController) throws IOException {
        this.Controller=screenController;
    }
    AtomicLong TotalDurationInSec= new AtomicLong(0);
    public  Task<Void> convertFiles(List<File> inputFiles, String OutputDir, String convertInto){
        System.out.println(ffmpeg);
        totalFiles=inputFiles.size();
        Task<Void> task =new Task<>() {
            @Override
            protected Void call() throws Exception {
                for(File file:inputFiles){

                    String outputFile= OutputDir+"/"+file.getName()+"."+convertInto;
                    String command=ffmpeg+" -i \""+file.getAbsolutePath()+"\" \""+outputFile+"\"";
                    GetMediaDetails getMediaDetails=new GetMediaDetails(file.getAbsolutePath());

                    new Thread(()->{
                        TotalDurationInSec.set(getMediaDetails.getDuration());
                    }).start();
                    new Thread(){

                        @Override
                        public void run() {
                            File f1=new File(file.getAbsolutePath());

                            Process process= null;
                            try {
                                process = new ProcessBuilder(command.split(" ")).redirectErrorStream(true).start();
                                Controller.showProgress();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //Read the output of the ffmpeg process
                            try(BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()))){
                                Scanner scanner = new Scanner(process.getErrorStream());
                                String line;
                                while((line=reader.readLine())!=null){

                                    if (line.contains("time=")) {
                                        String[] parts = line.split("time=");
                                        String timePart = parts[1].trim();
                                        String[] timeSplit = timePart.split("\\.");
                                        String[] timeValues = timeSplit[0].split(":");
                                        int hours = Integer.parseInt(timeValues[0]);
                                        int minutes = Integer.parseInt(timeValues[1]);
                                        int seconds = Integer.parseInt(timeValues[2]);
                                        double totalSeconds = hours * 3600 + minutes * 60 + seconds;


                                        double progress = (totalSeconds / TotalDurationInSec.get());
                                        Platform.runLater(() -> {
                                            Controller.getProgressBar().setProgress(progress);
                                            Controller.getProgPercentage().setText(Math.round((float)progress * 100) +"%");
                                        });
                                    }
                                    System.out.println(line);
                                }

                            }catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }

                            int exitCode= 0;
                            try {
                                exitCode = process.waitFor();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(exitCode==0) {
                                System.out.println("Converted Successful !");
                                totalFilesConverted= totalFilesConverted +1;
                                updateProgress(totalFilesConverted + 1, inputFiles.size());

                                Controller.hideProgress();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Controller.getLabelForTotalFilesConverted().setText(String.valueOf(totalFilesConverted));
                                    }
                                });


                            }else {
                                System.out.println("Conversion failed of "+file.getAbsolutePath()+"with exit code "+exitCode);
                            }
                            super.run();
                        }
                    }.start();


                }

                return null;
            }
        };
        task.run();
//        Controller.getProgressBar().progressProperty().bind(task.progressProperty());
        return task;



    }



    private static long convertDurationToSeconds(String duration) {
        String[] parts = duration.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        float seconds = Float.parseFloat(parts[2]);

        return (long) (hours * 3600L + minutes * 60L + seconds);
    }



}



