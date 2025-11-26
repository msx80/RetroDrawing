package com.github.msx80.retrodrawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.msx80.omicron.api.Pointer;
import com.github.msx80.omicron.api.Sys;
import com.github.msx80.omicron.basicutils.Colors;
import com.github.msx80.omicron.basicutils.ShapeDrawer;
import com.github.msx80.omicron.basicutils.text.TextDrawerVariable;

public class OptionsWindow extends Window {

	private static final int boxWidth = 200;
	private static final int boxHeight = 80;

	private static final int ax = (RetroDrawing.SURFWIDTH - boxWidth) / 2;
	private static final int ay = (RetroDrawing.HEIGHT - boxHeight) / 2;
	
	private List<Option> options = new ArrayList<>();
	
	public OptionsWindow(Ctx ctx) {
		options.add(new Option(6, "New", () -> new CleanAll().execute(ctx)));
		options.add(new Option(7, "Save", () -> new CleanAll().execute(ctx)));
		options.add(new Option(8, "Load", () -> new CleanAll().execute(ctx)));
	}
	
	public void draw(int sheetNum)
	{
		Sys.fill(sheetNum, ax, ay, boxWidth, boxHeight, Colors.WHITE);
		ShapeDrawer.outline(ax, ay, boxWidth, boxHeight, sheetNum, Colors.BLACK);
		
		int y = ay+2;
		int x = ax+2;
		for (Option option : options) {
			// ShapeDrawer.outline(x, y, 100, 20, 0, Colors.BLACK);
			RetroDrawing.icons.draw(RetroDrawing.SURFACE_ICONS, x+2, y+2, option.icon, 0, 0);
			Sys.color(Colors.BLACK);
			TextDrawerVariable.DEFAULT.print(option.label, x+20, y+2);
			Sys.color(Colors.WHITE);
			y+=20;
		}
		
	}

	public boolean update(Pointer m)
	{
		if (m.btn(0))
		{
			int dx = m.x() - ax;
			int dy = m.y() - ay;
			if(dx > 0 && dy > 0 && dx<boxWidth-1 && dy <boxHeight-1)
			{
				
			}
			
			return false;
		}
		
		return true;
	}
	
}
