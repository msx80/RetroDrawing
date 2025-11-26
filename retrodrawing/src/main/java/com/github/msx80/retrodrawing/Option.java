package com.github.msx80.retrodrawing;

public class Option 
{
	public int icon;
	public String label;
	public Runnable onClick;
	
	public Option(int icon, String label, Runnable onClick) {
		super();
		this.icon = icon;
		this.label = label;
		this.onClick = onClick;
	}
	
	
}
