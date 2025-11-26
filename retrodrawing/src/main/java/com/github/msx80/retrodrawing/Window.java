package com.github.msx80.retrodrawing;

import com.github.msx80.omicron.api.Pointer;

public abstract class Window {
	
	boolean wasEverDown = false;
	
	public abstract void draw(int sheetNum);

	public abstract boolean update(Pointer m);
	
}
