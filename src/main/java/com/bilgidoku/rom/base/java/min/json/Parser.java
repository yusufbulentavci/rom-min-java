package com.bilgidoku.rom.base.java.min.json;


public class Parser {
	private static final int BYTE_TO_MB = 1024*1024;


	public static Double parseLongInMB(String str) {
		if(str==null || str.trim().length()==0)
			return null;
		return ((double)Long.parseLong(str)/BYTE_TO_MB);
	}
	
	public static Long parseLong(String str) {
		if(str==null || str.trim().length()==0)
			return null;
		return Long.parseLong(str);
	}
	
	public static Double parseDouble(String str) {
		if(str==null || str.trim().length()==0)
			return null;
		return Double.parseDouble(str);
	}

}
