package com.poet.ar.config;

import java.util.Collection;
import java.util.List;

public class Config {

	private String outputRootDir;
    private String inputRootDir;
    private String outputFileSuffix;
    private int poolSize = 20;
    
    private Collection<String> keywords;

	public Config(String outputRootDir, String inputRootDir,
			String outputFileSuffix, int poolSize, Collection<String> keywords) {
		this.outputRootDir = outputRootDir;
		this.inputRootDir = inputRootDir;
		this.outputFileSuffix = outputFileSuffix;
		this.poolSize = poolSize;
		this.keywords = keywords;
	}

	public String getOutputRootDir() {
		return outputRootDir;
	}

	public String getInputRootDir() {
		return inputRootDir;
	}

	public String getOutputFileSuffix() {
		return outputFileSuffix;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public Collection<String> getKeywords() {
		return keywords;
	}
}
