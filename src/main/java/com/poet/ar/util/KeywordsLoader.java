package com.poet.ar.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Love0 on 2016/1/27 0027.
 */
public class KeywordsLoader {

    // ====
    private static Logger logger = Logger.getLogger(KeywordsLoader.class);

    public static final String CONFIG_FILE = "keywords.properties";
    private static final String KEY = "keywords";

    private static List<String> loadKeywords(InputStream is) {

        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            logger.error("failed load keywords,exception msg: " + e.getMessage());
        }

        String keywords = props.getProperty(KEY,"");

        // String to list
        String arr[] = keywords.split(",");

        List<String> list = Arrays.asList(arr);

        logger.debug("success loaded " + list.size() + " keywords");

        return  list;

    }

    public static List<String> loadKeywords(String configFile){
        return loadKeywords(ClassPathResource.getClassPathResource(configFile));
    }

    public static List<String> loadKeywords(){
        return loadKeywords(CONFIG_FILE);
    }




}
