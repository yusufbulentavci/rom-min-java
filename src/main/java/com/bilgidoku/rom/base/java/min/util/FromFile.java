package com.bilgidoku.rom.base.java.min.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.bilgidoku.rom.base.java.min.json.JSONObject;
import com.bilgidoku.rom.base.min.err.KnownError;

public class FromFile {
	
	public static JSONObject loadJsonObject(File file) throws KnownError{
		try {
			
			return new JSONObject(FileUtils.readFileToString(file));
		} catch (KnownError | IOException e) {
			throw new KnownError("Error in json file:"+file+" ",e);
		}
	}

}
