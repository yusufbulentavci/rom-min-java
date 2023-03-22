package com.bilgidoku.rom.base.java.min;

import com.bilgidoku.rom.base.min.MillisProvider;

public class DefaultMillis implements MillisProvider {

	@Override
	public long millis() {
		return System.currentTimeMillis();
	}

}
