package com.poet.ar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 
 * @author poet
 *
 */
public class ResourceLoader {

	
	public static InputStream loadResource(String fileName) throws FileNotFoundException{
		// first load from File System
		InputStream result = null;
		
		File file = new File(fileName);
		if( file.exists() ){
			result = new FileInputStream(file);
			return result;
		}
		
		// second load from user.dir
		String userDir = System.getProperty("user.dir");
		File userDirFile = new File(userDir,fileName);
		if( userDirFile.exists() ){
			result = new FileInputStream(userDirFile);
			return result;
		}
		
		// finally load from classpath
		result = ClassPathResource.getClassPathResource(fileName);
		
		return result;
	}
	
}
