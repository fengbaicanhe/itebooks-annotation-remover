package com.poet.ar;

import com.itextpdf.text.DocumentException;
import com.poet.ar.remover.AnnotationRemover;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by poet on 2016/1/27 0027.
 */
public class TestBatchProcessPdfFile {

     String fileOutDir = "E:\\books\\out\\";
     String fileInRootDir = "E:\\books";
     final String EXT = ".pdf";
     final String outSuffix = "-fixed.pdf";


    private ExecutorService executorService = Executors.newFixedThreadPool(20);


    private static class ProcessThread implements Runnable{

        private final String fileIn;
        private final String fileOut;

        private ProcessThread(String fileIn, String fileOut) {
            this.fileIn = fileIn;
            this.fileOut = fileOut;
        }

        @Override
        public void run() {
            try {
                AnnotationRemover.doRemove(fileIn,fileOut);
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBatchProcess(){

        File rootDir = new File(fileInRootDir);

        File[] pdfs = rootDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(EXT);
            }
        });

        for (File pdf : pdfs) {
            String fileIn = pdf.getAbsolutePath();
            String fileName = pdf.getName().substring(0,pdf.getName().lastIndexOf("."));
            String fileOut = fileOutDir + fileName + outSuffix;
            System.out.println("execute ... " + fileIn + "   " + fileOut);
            executorService.execute(new ProcessThread(fileIn, fileOut));
        }


        executorService.shutdown();

        while( !executorService.isTerminated() ){}

    }



}
