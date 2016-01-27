package com.poet.ar.remover;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.poet.ar.util.KeywordsLoader;
import com.poet.ar.util.StreamContentExtractor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by poet on 2016/1/27 0027.
 */
public class AnnotationRemover {

    private static Logger logger = Logger.getLogger(AnnotationRemover.class);

    private static final List<String> keywords = KeywordsLoader.loadKeywords();

    /**
     * @param fileIn  pdf filename you want to remove annotation
     * @param fileOut removed annotation pdf save path
     * @throws IOException
     * @throws DocumentException
     */
    public static void doRemove(String fileIn, String fileOut) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(fileIn);

        FileOutputStream fos = new FileOutputStream(fileOut);
        PdfStamper stamper = new PdfStamper(reader, fos);

        int pageNums = reader.getNumberOfPages();

        for (int i = 1; i <= pageNums; i++) {

            PdfDictionary page = reader.getPageNRelease(i);

            doRemoveAnnotation(page);
            doRemoveContent(page);
        }

        stamper.close();
        fos.close();
        reader.close();
    }

    /**
     * remove content that matches keywords
     *
     * @param page
     */
    private static void doRemoveContent(PdfDictionary page) {

        // all contents in page i
        PdfArray contentArray = page.getAsArray(PdfName.CONTENTS);
        PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);

        List<Integer> willRemovedIx = new ArrayList<Integer>();
        if (contentArray != null) {

            PdfStream stream = null;
            for (int i = 0; i < contentArray.size(); i++) {

                stream = contentArray.getAsStream(i);

                PRStream pr = (PRStream)stream;

                
                // TODO // FIXME: 2016/1/27 0027 java.lang.ClassCastException: com.itextpdf.text.pdf.PdfArray cannot be cast to com.itextpdf.text.pdf.PdfLiteral
                // get display text
//                String text = StreamContentExtractor.extractFromPdfStream(stream, resources);
//
//                if (keywords.contains(text)) {
//                    willRemovedIx.add(i);
//                }

                try {
                    String text = StreamContentExtractor.extractFromPdfStream(stream, resources);

                    if (keywords.contains(text)) {
                        willRemovedIx.add(i);
                    }
                } catch (Exception ex){}


            }
        }

        for (Integer ix : willRemovedIx) {
            contentArray.remove(ix);
        }

    }

    /**
     * remove annotation that matches keywords
     *
     * @param page
     */
    private static void doRemoveAnnotation(PdfDictionary page) {

        // all annotations in page i
        PdfArray annoArray = page.getAsArray(PdfName.ANNOTS);

        if (annoArray != null) {

            List<Integer> willRemovedIx = new ArrayList<Integer>();
            PdfDictionary annotation = null;
            PdfDictionary a = null;
            PdfString uri = null;
            for (int i = 0; i < annoArray.size(); i++) {

                annotation = annoArray.getAsDict(i);

                a = annotation.getAsDict(PdfName.A);

                if (a == null) {
                    continue;
                }

                uri = a.getAsString(PdfName.URI);

                if (uri == null) {
                    continue;
                }

                String uriStr = uri.toString().trim();

                if (keywords.contains(uriStr)) {
                    willRemovedIx.add(i);
                }

            }

            for (Integer ix : willRemovedIx) {
                annoArray.remove(ix);
            }
        }

    }

}
