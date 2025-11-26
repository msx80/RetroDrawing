package com.github.msx80.retrodrawing;

import com.github.msx80.omicron.api.Sys;

public class CleanAll implements ClickyTool {


	@Override
	public void execute(Ctx ctx) {
		ctx.recordUndo();
		Sys.fill(ctx.getSurface(), 0,0, RetroDrawing.SURFWIDTH, RetroDrawing.HEIGHT, Palette.P[0]);
		
	}


}
