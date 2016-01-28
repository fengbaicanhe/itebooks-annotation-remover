package com.poet.ar.util;

import java.io.InputStream;

/**
 * Created by xu on 2016/1/28.
 */
public abstract class ClassPathResource {

    public static InputStream getClassPathResource(String path){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

}
