package com.poet.ar;

import com.itextpdf.text.DocumentException;
import com.poet.ar.remover.AnnotationRemover;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Love0 on 2016/1/27 0027.
 */
public class TestProcessSinglePdfFile {

    private String fileIn = "H:\\books-test\\test\\pdf.pdf";

    private String fileOut = "H:\\books-test\\test\\out.pdf";

    @Test
    public void testProcessSingle(){

        long start = System.currentTimeMillis();

        try {
            AnnotationRemover.doRemove(fileIn,fileOut);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();


    }

}
