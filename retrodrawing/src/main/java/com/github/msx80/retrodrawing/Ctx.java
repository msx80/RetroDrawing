package com.github.msx80.retrodrawing;

public interface Ctx {
	int currentColor();
	void recordUndo();
	void undo();
	int getSurface();
}
