package com.github.msx80.retrodrawing;

public class CleanAll implements ClickyTool {


	@Override
	public void execute(Ctx ctx) {
		ctx.recordUndo();
		ctx.getSys().fill(ctx.getSurface(), 0,0, RetroDrawing.SURFWIDTH, RetroDrawing.HEIGHT, Palette.P[0]);
		
	}


}
