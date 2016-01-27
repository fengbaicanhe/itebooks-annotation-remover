package com.poet.ar.util;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by Love0 on 2016/1/27 0027.
 */
public class StreamContentExtractor {

    private static Logger logger = Logger.getLogger(StreamContentExtractor.class);

    private static final String EMPTY_STRING = "";

    public static String extractFromPdfStream(PdfStream pdfStream, PdfDictionary resources){

        LocationTextExtractionStrategy extractionStrategy= new LocationTextExtractionStrategy();
        PdfContentStreamProcessor contentProcessor =  new PdfContentStreamProcessor(extractionStrategy);

        String result = EMPTY_STRING;
        try {
            byte[] bts = ContentByteUtils.getContentBytesFromContentObject(pdfStream);
            contentProcessor.processContent(bts,resources);

            String text = extractionStrategy.getResultantText();

            result = text == null ? result : text.trim();

        } catch (IOException e) {
            logger.debug("parse PdfStream to String failed,exception message: " + e.getMessage());
        } finally {
            contentProcessor.reset();
        }

        return result;
    }

}
