package com.bilgidoku.rom.base.java.min;

import com.bilgidoku.rom.base.min.IRun;

public class DefaultRun implements IRun {
	
	@Override
	public void runInWorker(Runnable work) {
		work.run();
	}

}
