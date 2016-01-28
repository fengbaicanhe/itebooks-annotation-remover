package com.poet.ar.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.poet.ar.config.Config;

/**
 * Created by poet on 2016/1/28.
 */
public abstract class ConfigLoader {

	private static Logger logger = Logger.getLogger(ConfigLoader.class);
	
    private static final String CONFIG_FILE = "start.properties";
    private static final String EXT = ".pdf";
    private static final Integer DEFAULT_POOL_SIZE = 5;
    
    private static Collection<String> keywordsList;
    
    public static Config loadConfig() throws Exception {
    	return loadConfig(CONFIG_FILE);
    }
    
    public static Config loadConfig(String configFile) throws Exception {
    	InputStream is = ResourceLoader.loadResource(configFile);
    	return loadConfig(is);
    }
    
    public static Config loadConfig(InputStream is) throws Exception {
    	
    	Properties props = new Properties();
        props.load(is);

        String inputRootDir = props.getProperty("inputRootDir");
        if (inputRootDir == null) {
            throw new NullPointerException("inputRootDir must be set in configFile!");
        }
        String outputRootDir = props.getProperty("outputRootDir");
        if (outputRootDir == null) {
            throw new NullPointerException("outputRootDir must be set in configFile!");
        }

        String outputFileSuffix = props.getProperty("outputFileSuffix");
        outputFileSuffix = outputFileSuffix == null ? "" : outputFileSuffix;

        String poolSizeStr = props.getProperty("threadPoolSize");
        
        int poolSize = DEFAULT_POOL_SIZE;
        if (poolSizeStr != null) {
            poolSize = Integer.valueOf(poolSize);
        }
        
        // load keywords
        String keywords = props.getProperty("keywords","");

        // String to list
        String arr[] = keywords.split(",");

        keywordsList = Collections.unmodifiableCollection(Arrays.asList(arr));

        logger.debug("success loaded " + keywordsList.size() + " keywords");
        
        Config config = new Config(outputRootDir, inputRootDir, outputFileSuffix, poolSize, keywordsList);
        
        return config;
    }
    
    public static Collection<String> getKeywords(){
    	return keywordsList;
    }
    
}
