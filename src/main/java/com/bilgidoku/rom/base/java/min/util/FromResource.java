package com.bilgidoku.rom.base.java.min.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

import com.bilgidoku.rom.base.java.min.json.JSONArray;
import com.bilgidoku.rom.base.java.min.json.JSONObject;
import com.bilgidoku.rom.base.min.err.KnownError;

public class FromResource {
	public static InputStream inputStream(String rn) throws IOException {
		InputStream f = getRes(rn);
		if (f == null) {
			throw new NullPointerException("Resource file not found:" + rn);
		}
		return f;
	}

	public static Reader readerStream(String rn) {
		InputStreamReader f = new InputStreamReader(getRes(rn));
		if (f == null) {
			throw new NullPointerException("Resource file not found:" + rn);
		}
		return f;
	}

	public static InputStream getRes(String rn) {

		if (!rn.startsWith("/")) {
			// rn = rn.substring(1);
			rn = "/" + rn;
		}

		// InputStream u = ClassLoader.getSystemClassLoader().getResourceAsStream(rn);
		InputStream u = FromResource.class.getResourceAsStream(rn);
		if (u != null)
			return u;
		// System.out.println("Failed for:" + rn);

		rn = rn.substring(1);

		// System.out.println("Tried for:" + rn);
		// u = ClassLoader.getSystemClassLoader().getResourceAsStream(rn);
		u = FromResource.class.getResourceAsStream(rn);
		return u;
	}

	public static String loadString(String rn) throws IOException {
		InputStream f = getRes(rn);
		if (f == null) {
			throw new NullPointerException("Resource file not found:" + rn);
		}

		byte[] buffer = new byte[200000];
		int t = IOUtils.read(f, buffer);
		String s = new String(buffer, 0, t, "UTF-8");
		// System.err.println("========="+rn+"============"+t);
		// System.err.println(s);
		// System.err.println("========================");
		return s;
	}

	public static JSONObject loadJsonObject(String rn) throws KnownError {
		try {
			return new JSONObject(loadString(rn));
		} catch (IOException e) {
			throw new KnownError("Failed to load resource:" + rn);
		}

	}

	public static JSONArray loadJsonArray(String rn) throws KnownError {
		try {
			return new JSONArray(loadString(rn));
		} catch (IOException e) {
			throw new KnownError("Failed to load resource:" + rn, e);
		}
	}

	/*
	 * pack=com/bilgidoku
	 * pack=META-INF
	 */
	public static Set<String> listFromClasspath(String pack) throws KnownError {
		try {
			Set<String> ret = new HashSet<>();
			String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
			for (String string : paths) {
				File f = new File(string);
				if (f.isDirectory()) {
					File ff = new File(f, pack);
					String[] ls = ff.list();
					if (ls != null) {
						for (String s : ls) {
							ret.add(s);
						}
					}
				} else if (string.endsWith(".jar")) {
					try(final JarFile jar = new JarFile(f);){
						final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
						File fpack=new File(pack);
						while (entries.hasMoreElements()) {
							final String name = entries.nextElement().getName();
							File h = new File(name);
							File parent=h.getParentFile();
							if (parent!=null && parent.equals(fpack)) { // filter according to the path
								ret.add(h.getName());
							}
						}
					}
				}
			}
			return ret;
		} catch (Exception e) {
			throw new KnownError(e);
		}
	}

	public static void main(String[] args) throws KnownError {
		System.out.println(listFromClasspath("com/bilgidoku"));
	}

}