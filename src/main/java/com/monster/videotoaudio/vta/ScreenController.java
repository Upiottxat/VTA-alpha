package com.monster.videotoaudio.vta;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ScreenController implements Initializable {
    public HBox addbtn;
    public ScrollPane listItems;
    public Label counted_items;
    public TextField outputDirBox;
    public ComboBox<String> selectFormat;



    public  Label progPercentage;
    public HBox convertbtn;
    public HBox filesConvertedLabelBox;

    public Label getLabelForTotalFilesConverted() {
        return labelForTotalFilesConverted;
    }

    public Label labelForTotalFilesConverted;


    public void filesConvertedLabelBoxHide(){
        new Thread(){
            @Override
            public void run() {
                filesConvertedLabelBox.setVisible(false);
                super.run();
            }
        };
    }
    public void filesConvertedLabelBoxShow(){
        new Thread(){
            @Override
            public void run() {
                filesConvertedLabelBox.setVisible(true);
                super.run();
            }
        };
    }
    public Label getProgPercentage() {
        return progPercentage;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ProgressBar progressBar;
    List<File> fileList=new ArrayList<>();

    String[] outputFormat ={

        // Audio Formats
               "MP3",
                "WAV",
                "AAC",
                "OGG",
                "FLAC",

                // Video Formats
                "MP4",
                "AVI",
                "MKV",
                "WMV",
                "FLV",

                // Image Formats

                "GIF",

    } ;
public void addItemsToScroll(){
    Task<Void> addItemsInScrollPane = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
            for(File file:fileList){
                VBox content=(VBox) listItems.getContent();

                content.getChildren().add(new Label(file.getAbsolutePath()));

            }
            countedItems();
            return null;
        }
    };
    addItemsInScrollPane.run();

    countedItems();
}



    void countedItems(){
        counted_items.setText(String.valueOf(fileList.size()));
    }

    void hideProgress(){
        new Thread(){

            @Override
            public void run() {
                progressBar.setVisible(false);
                progPercentage.setVisible(false);
                super.run();
            }
        }.start();
    }void updateProgress(Task progress){
        new Thread( ){
            @Override
            public void run() {

                super.run();
            }
        };
    }
    public void showProgress(){
        new Thread(){

            @Override
            public void run() {
                progressBar.setVisible(true);
                progPercentage.setVisible(true);
                super.run();
            }
        }.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideProgress();
        filesConvertedLabelBoxHide();
        selectFormat.getItems().addAll(FXCollections.observableArrayList(outputFormat));
        selectFormat.getSelectionModel().select(0);
        addbtn.setOnMouseClicked(e->{
            filesConvertedLabelBoxHide();
            labelForTotalFilesConverted.setText(String.valueOf(0));
            Node node= (Node) e.getSource();
            Stage thisStage=(Stage)node.getScene().getWindow();

            FileChooser fc=new FileChooser();
            fc.setTitle("Select multiple files");


                    List<File> temp= Collections.unmodifiableList(fc.showOpenMultipleDialog(thisStage));

//                    temp.clear();


                    fileList.addAll(temp);




            System.out.println(fileList);
            addItemsToScroll();
//            temp.clear();

        });
        Convertor convertor= null;
        try {
            convertor = new Convertor(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Convertor finalConvertor = convertor;
        convertbtn.setOnMouseClicked(e->{

            if (!(fileList==null)){
                if (!(fileList.size()==0)){
                    if(doesFolderExist(outputDirBox.getText())){
                        filesConvertedLabelBoxShow();
                        Task<Void> progress= finalConvertor.convertFiles(fileList,String.valueOf(outputDirBox.getText()),selectFormat.getValue());
//                      progressBar.progressProperty().bind(progress.progressProperty());

                    }else{
                        JOptionPane.showMessageDialog(null,"please enter output dir");
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"No files found");
                }
            }else{
                JOptionPane.showMessageDialog(null,"No files found");
            }
        });

    }


    public void selectOutputDir(MouseEvent mouseEvent) {
        DirectoryChooser outputDirChooser=new DirectoryChooser();
        outputDirChooser.setTitle("select folder for output files");
        Node node=(Node) mouseEvent.getSource();
        outputDirBox.setText(String.valueOf(outputDirChooser.showDialog(node.getScene().getWindow())));
        outputDirChooser.setInitialDirectory(outputDirChooser.getInitialDirectory());

    }
    public static boolean doesFolderExist(String folderPath) {
        Path path = Paths.get(folderPath);
        return Files.exists(path) && Files.isDirectory(path);
    }
    private static void addFilesToList(List<File> fileList, File newFile) {
        // Check if the file is not already in the list
        if (!fileList.contains(newFile)) {
            // Add the new file to the list
            fileList.add(newFile);
            System.out.println("File added: " + newFile.getName());
        } else {
            System.out.println("File already exists in the list: " + newFile.getName());
        }
    }
}