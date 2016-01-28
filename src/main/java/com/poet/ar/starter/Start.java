package com.poet.ar.starter;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.poet.ar.config.Config;
import com.poet.ar.remover.AnnotationRemover;
import com.poet.ar.util.ConfigLoader;

/**
 * Created by poet on 2016/1/28.
 */
public final class Start {

    private static Logger logger = Logger.getLogger(Start.class);

    private static final String CONFIG_FILE = "config.properties";
    private static final String EXT = ".pdf";
    
    private Config config;


    private ExecutorService executorService;

    private Start() throws Exception {
        this(CONFIG_FILE);
    }

    private Start(String configFile) throws Exception {
    	config = ConfigLoader.loadConfig(configFile);
    	this.executorService = Executors.newFixedThreadPool(config.getPoolSize());
    }


    private File[] getFilesInRootDir() throws Exception {
        File rootDir = new File(config.getInputRootDir());
        if( !rootDir.exists() ) {
            throw new NullPointerException("input root dir does not exists!");
        }

        File[] pdfFiles = rootDir.listFiles(new PdfFileFilter());

        return pdfFiles;
    }

    private void startProcess() throws Exception {
        File[] pdfFiles = getFilesInRootDir();
        logger.debug("found " + pdfFiles.length + " pdf file(s) in input root dir: " + config.getInputRootDir());

        for (File pdf : pdfFiles) {
            File fileIn = pdf;
            String outputFileName = fileIn.getName().substring(0,fileIn.getName().lastIndexOf(".")) + config.getOutputFileSuffix() + EXT;
            File fileOut = new File(config.getOutputRootDir() + "\\" + outputFileName);

            executorService.execute(new PdfProcessRunner(fileIn,fileOut));
        }

        executorService.shutdown();

        while ( !executorService.isTerminated() ){}

        logger.debug("all process done!");
    }

    /**
     * after process,open the output root dir
     */
    private void showOutputDir() {
        try {
            Runtime.getRuntime().exec("explorer " + config.getOutputRootDir());
        } catch (IOException e) {
            logger.debug("open output root dir failed,exception message: " + e.getMessage());
        }
    }

    static class PdfFileFilter implements FileFilter{

        @Override
        public boolean accept(File pathname) {
            return pathname.getAbsolutePath().toLowerCase().endsWith(Start.EXT);
        }
    }

    static class PdfProcessRunner implements Runnable {

        private final File fileIn;
        private final File fileOut;

        private PdfProcessRunner(File fileIn, File fileOut){
            this.fileIn = fileIn;
            this.fileOut = fileOut;
        }

        @Override
        public void run() {
            try {
                AnnotationRemover.doRemove(fileIn,fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String configFile = CONFIG_FILE;
        if (args.length > 0) {
            configFile = args[0];
        }

        Start start = new Start(configFile);
        start.startProcess();
        start.showOutputDir();
    }

}
