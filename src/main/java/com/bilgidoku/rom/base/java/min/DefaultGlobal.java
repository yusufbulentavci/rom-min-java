package com.bilgidoku.rom.base.java.min;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.bilgidoku.rom.base.java.min.util.FromResource;
import com.bilgidoku.rom.base.min.IGlobal;
import com.bilgidoku.rom.base.min.err.KnownError;

public class DefaultGlobal implements IGlobal {

	@Override
	public String getResource(String rn) throws KnownError {
		InputStream f = getRes(rn);
		if (f == null) {
			throw new NullPointerException("Resource file not found:" + rn);
		}

		byte[] buffer = new byte[100000];
		int t;
		try {
			t = IOUtils.read(f, buffer);
			String s = new String(buffer, 0, t, "UTF-8");
			// System.err.println("========="+rn+"============"+t);
			// System.err.println(s);
			// System.err.println("========================");
			return s;
		} catch (IOException e) {
			throw new KnownError(e);
		}
	}

	
	public static InputStream getRes(String rn) {

		if (!rn.startsWith("/")) {
			//rn = rn.substring(1);
			rn = "/"+rn;
		}

		//InputStream u = ClassLoader.getSystemClassLoader().getResourceAsStream(rn);
		InputStream u = FromResource.class.getResourceAsStream(rn);
		if(u != null)
			return u;
		//System.out.println("Failed for:" + rn);

		rn=rn.substring(1);

		//System.out.println("Tried for:" + rn);
		//u = ClassLoader.getSystemClassLoader().getResourceAsStream(rn);
		u = FromResource.class.getResourceAsStream(rn);
		return u;
	}
}
