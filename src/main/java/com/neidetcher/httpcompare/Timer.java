package com.neidetcher.httpcompare;

public class Timer {
	private String name;
	private Long start = System.currentTimeMillis();
	private Long duration = null;
	
	public Timer(String nameIn) {
		name = nameIn;
		//start = System.currentTimeMillis();
	}
	
	public String stop() {
		duration = System.currentTimeMillis() - start;
		return name + " took " + duration + "ms";
	}
}
