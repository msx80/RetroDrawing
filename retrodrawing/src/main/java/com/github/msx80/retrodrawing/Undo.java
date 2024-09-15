package com.github.msx80.retrodrawing;

public class Undo implements ClickyTool {

	@Override
	public void execute(Ctx ctx) {
		ctx.undo();
		
	}

}
