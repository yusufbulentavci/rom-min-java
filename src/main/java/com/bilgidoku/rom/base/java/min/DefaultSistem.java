package com.bilgidoku.rom.base.java.min;

import java.io.IOException;

import com.bilgidoku.rom.base.java.min.util.FromResource;
import com.bilgidoku.rom.base.min.ISistem;
import com.bilgidoku.rom.base.min.err.KnownError;

public class DefaultSistem implements ISistem {

	private final String user;
	private final String userDir;

	public DefaultSistem() {
		this.user=System.getenv("USER");
		this.userDir = System.getProperty("user.home");
		if(user==null)
			throw new RuntimeException("USER is null; It shouldnt be!");
		if(this.user.equalsIgnoreCase("root"))
			throw new RuntimeException("USER is ROOT; It shouldnt be!");
	}

	@Override
	public String getRomUser() {
		return user;
	}

	@Override
	public String getRomUserDir() {
		return userDir;
	}

	@Override
	public String loadTextResource(String res) throws KnownError {
		try {
			return FromResource.loadString(res);
		} catch (IOException e) {
			throw new KnownError(res, e);
		}
	}
	
	



}
