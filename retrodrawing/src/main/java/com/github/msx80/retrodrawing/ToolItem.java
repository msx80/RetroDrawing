package com.github.msx80.retrodrawing;

public class ToolItem {

	public int y; //icon y
	public Class<? extends BaseTool> toolClass;
	
	public ToolItem(int y, Class<? extends BaseTool> toolClass) {
		this.y = y;
		this.toolClass = toolClass;
	} 
	
	

}
