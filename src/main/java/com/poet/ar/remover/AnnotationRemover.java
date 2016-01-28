package com.poet.ar.remover;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Meta;
import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfString;
import com.poet.ar.util.ConfigLoader;
import com.poet.ar.util.StreamContentExtractor;

/**
 * Created by poet on 2016/1/27 0027.
 */
public class AnnotationRemover {

    private static Logger logger = Logger.getLogger(AnnotationRemover.class);

    private static final Collection<String> keywords = ConfigLoader.getKeywords();

    public static void doRemove(File fileIn, File fileOut) throws IOException, DocumentException {
        doRemove(fileIn.getAbsolutePath(),fileOut.getAbsolutePath());
    }

    /**
     * @param fileIn  pdf filename you want to remove annotation
     * @param fileOut removed annotation pdf save path
     * @throws IOException
     * @throws DocumentException
     */
    public static void doRemove(String fileIn, String fileOut) throws IOException, DocumentException {

        logger.debug("starting process file: " + fileIn );

        PdfReader reader = new PdfReader(fileIn);

        FileOutputStream fos = new FileOutputStream(fileOut);
        PdfStamper stamper = new PdfStamper(reader, fos);

        removeInfo(reader,stamper);

        int pageNums = reader.getNumberOfPages();

        int totalAnnoCount = 0;
        int totalContentCount = 0;
        for (int i = 1; i <= pageNums; i++) {

            PdfDictionary page = reader.getPageNRelease(i);

            int annoCount = doRemoveAnnotation(page);
            int contentCount = doRemoveContent(page);

            totalAnnoCount += annoCount;
            totalContentCount += contentCount;

            logger.debug("removed " + annoCount + " annotation(s) in page " + i + " ,in file: " + fileIn);
            logger.debug("removed " + contentCount + " content(s) in page " + i + " ,in file: " + fileIn);
        }

        stamper.close();
        fos.close();
        reader.close();

        logger.debug("success removed " + totalAnnoCount + " annotation(s), "+ totalContentCount +" content(s), with output file: " + fileOut);
    }

    /**
     * remove Creator,Subject,Producer,Author,Title,Keywords
     * @param reader
     * @param stamper
     */
    private static void removeInfo(PdfReader reader, PdfStamper stamper){
        Map<String,String> infos = reader.getInfo();

        infos.put(Meta.AUTHOR, "");
        infos.put(Meta.KEYWORDS,"");
        infos.put(Meta.TITLE,"");
        infos.put(Meta.SUBJECT, "");

        infos.put("Creator","");
        infos.put("Subject","");
        infos.put("Author", "");
        infos.put("Title", "");
        infos.put("Keywords", "");

        // this will not work ,if you want change producer , you need buy a itext license
        infos.put(Meta.PRODUCER,"");

        stamper.setMoreInfo(infos);
    }

    /**
     * remove content that matches keywords
     *
     * @param page
     * @return count of removed content
     */
    private static int doRemoveContent(PdfDictionary page) {

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

            int i  = 0;
            for (Integer ix : willRemovedIx) {
                contentArray.remove(ix - i++);
            }
        }

        return willRemovedIx.size();
    }

    /**
     * remove annotation that matches keywords
     *
     * @param page
     * @return count of removed annotations
     */
    private static int doRemoveAnnotation(PdfDictionary page) {

        // all annotations in page i
        PdfArray annoArray = page.getAsArray(PdfName.ANNOTS);
        List<Integer> willRemovedIx = new ArrayList<Integer>();

        if (annoArray != null) {

            PdfDictionary annotation = null;
            PdfDictionary a = null;
            PdfString uri = null;
            for (int i = 0; i < annoArray.size(); i++) {

                annotation = annoArray.getAsDict(i);

                if (annotation == null) {
                    continue;
                }

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

            int i = 0;
            for (Integer ix : willRemovedIx) {
                annoArray.remove(ix - i++);
            }

        }

        return willRemovedIx.size();
    }

}
