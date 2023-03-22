package com.bilgidoku.rom.base.java.min;

import com.bilgidoku.rom.base.min.Sistem;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.gorevli.Ortam;

public class OrtamInit {

	public static void prod() throws KnownError {
		Ortam.prod();
		Sistem.cur=new DefaultSistem();
		Sistem.global=new DefaultGlobal();
		Sistem.log=new DefaultLogger();
		Sistem.millis=new DefaultMillis();
		Sistem.uyum=new UyumImpl();
	}
	
	public static void test() throws KnownError {
		Ortam.test();
		Sistem.cur=new DefaultSistem();
		Sistem.global=new DefaultGlobal();
		Sistem.log=new DefaultLogger();
		Sistem.millis=new DefaultMillis();
		Sistem.uyum=new UyumImpl();
	}

	public static void basla() throws KnownError {
		Ortam.tek().basla();
	}

}
