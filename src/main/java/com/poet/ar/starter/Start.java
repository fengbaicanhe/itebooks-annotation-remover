package com.poet.ar.starter;


import com.itextpdf.text.DocumentException;
import com.poet.ar.remover.AnnotationRemover;
import com.poet.ar.util.ClassPathResource;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by poet on 2016/1/28.
 */
public final class Start {

    private static Logger logger = Logger.getLogger(Start.class);

    private static final String CONFIG_FILE = "start.properties";
    private static final String EXT = ".pdf";

    private String outputRootDir;
    private String inputRootDir;
    private String outputFileSuffix;
    private int poolSize = 20;

    private ExecutorService executorService;

    private Start() throws Exception {
        this(CONFIG_FILE);
    }

    private Start(String configFile) throws Exception {

        InputStream is = null;

        // try get use new File
        File file = new File(configFile);
        String userDir = System.getProperty("user.dir");
        File userDirFile = new File(userDir,configFile);
        if (file.exists()) {
            // file found
            is = new FileInputStream(file);
        } else if( userDirFile.exists() ){
            is = new FileInputStream(userDirFile);
        } else  {
            // try get from class path
            is = ClassPathResource.getClassPathResource(configFile);
        }

        if (is == null) {
            throw new NullPointerException("can not get file with configFile: " + configFile);
        }

        initProperties(is);
        this.executorService = Executors.newFixedThreadPool(this.poolSize);
    }

    private void initProperties(InputStream is) throws Exception {
        Properties props = new Properties();
        props.load(is);

        this.inputRootDir = props.getProperty("inputRootDir");
        if (inputRootDir == null) {
            throw new NullPointerException("inputRootDir must be set in configFile!");
        }
        this.outputRootDir = props.getProperty("outputRootDir");
        if (outputRootDir == null) {
            throw new NullPointerException("outputRootDir must be set in configFile!");
        }

        this.outputFileSuffix = props.getProperty("outputFileSuffix");
        outputFileSuffix = outputFileSuffix == null ? "" : outputFileSuffix;

        String poolSizeStr = props.getProperty("threadPoolSize");
        if (poolSizeStr != null) {
            this.poolSize = Integer.valueOf(poolSize);
        }

    }

    private File[] getFilesInRootDir() throws Exception {
        File rootDir = new File(inputRootDir);
        if( !rootDir.exists() ) {
            throw new NullPointerException("input root dir does not exists!");
        }

        File[] pdfFiles = rootDir.listFiles(new PdfFileFilter());

        return pdfFiles;
    }

    private void startProcess() throws Exception {
        File[] pdfFiles = getFilesInRootDir();
        logger.debug("found " + pdfFiles.length + " pdf file(s) in input root dir: " + inputRootDir);

        for (File pdf : pdfFiles) {
            File fileIn = pdf;
            String outputFileName = fileIn.getName().substring(0,fileIn.getName().lastIndexOf(".")) + outputFileSuffix + EXT;
            File fileOut = new File(outputRootDir + "\\" + outputFileName);

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
            Runtime.getRuntime().exec("explorer " + outputRootDir);
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
