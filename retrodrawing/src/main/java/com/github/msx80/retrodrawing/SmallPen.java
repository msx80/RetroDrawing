package com.github.msx80.retrodrawing;

import com.github.msx80.omicron.api.Sys;
import com.github.msx80.omicron.basicutils.ShapeDrawer;

public class SmallPen extends Pen {
	

	@Override
	public void dotPen(Ctx ctx, int x, int y) {
		Sys.fill(ctx.getSurface(), x, y, 1,1, Palette.P[ctx.currentColor()]);
	}

	@Override
	public void linePen(Ctx ctx,int x, int y, int x2, int y2) {
		ShapeDrawer.line(x, y, x2, y2, ctx.getSurface(), Palette.P[ctx.currentColor()]);
	}

}
