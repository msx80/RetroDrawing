package com.github.msx80.retrodrawing;


import java.util.Stack;

import com.github.msx80.omicron.api.Game;
import com.github.msx80.omicron.api.Pointer;
import com.github.msx80.omicron.api.Sys;
import com.github.msx80.omicron.api.SysConfig;
import com.github.msx80.omicron.api.SysConfig.VirtualScreenMode;
import com.github.msx80.omicron.basicutils.Colors;
import com.github.msx80.omicron.basicutils.ShapeDrawer;
import com.github.msx80.omicron.basicutils.text.TextDrawer;
import com.github.msx80.omicron.basicutils.text.TextDrawerFixed;

public class RetroDrawing implements Game, Ctx {
	
	ToolItem[] tools = {
			new ToolItem(0, SmallPen.class),
			new ToolItem(0, MediumPen.class),
			new ToolItem(0, BigPen.class),
			new ToolItem(0, SlowFill.class),
			new ToolItem(0, CleanAll.class),
			new ToolItem(0, Undo.class),
			
			
	};
	
	public static final int HEIGHT = 144;
	public static final int WIDTH = 256;
	
	public static final int SURFWIDTH = WIDTH-20;
	
	private Sys sys;
	int surface = 0;
	int cur = 4;
	Window w = null;
	Tool tool = null;
	
	Stack<byte[]> undos = new Stack<byte[]>();
	
	TextDrawer td;
	
	boolean cooldownMouse = false;
	boolean showCursor;
	
	
    public void init(final Sys sys) 
    {
        this.sys = sys;
        surface = sys.newSurface(SURFWIDTH, HEIGHT);
        sys.fill(surface, 0,0,SURFWIDTH, HEIGHT, Palette.P[0]);
        
        tool = new MediumPen() ;
        
        td = new TextDrawerFixed(sys, 1, 6, 6, 6);
        
        try
        {
	        String platform = (String) sys.hardware("com.github.msx80.omicron.plugins.builtin.PlatformPlugin", "PLATFORM", "");
	        sys.trace("Platform: "+platform);
	        showCursor = !platform.equals("ANDROID");
        }
        catch (Exception e) {
			sys.trace("Error getting platform: "+e.getMessage());
			showCursor = true;
		}
		
    }

    public void render() 
    {
    	sys.clear(Colors.WHITE);
    	ShapeDrawer.outline(sys,SURFWIDTH, 0, WIDTH-SURFWIDTH, HEIGHT,0, Colors.from(30, 30, 50));
    	
    	
    	sys.fill(0, SURFWIDTH+2, 2, 16, 16, Palette.P[cur]);
    	ShapeDrawer.outline(sys,SURFWIDTH+2, 2, 16, 16,0, Colors.from(30, 30, 50));
    	
		
    	for (int i = 0; i < tools.length; i++) {
			ToolItem ti = tools[i];
			if(ti.toolClass.isInstance(tool)) 
	    	{
	    		sys.color(Colors.WHITE);
	    	}
	    	else
	    	{
	    		sys.color(Colors.from(255, 255, 255,100));
	    	}
	    	sys.draw(2, SURFWIDTH+2, 20*(i+1), i*16, 0, 16,16, 0,0);
		}
    	
    	sys.color(Colors.WHITE);
    	sys.draw(surface, 0,0,0,0, SURFWIDTH, HEIGHT, 0, 0);
    	
    	if(w!=null) w.draw(sys, 0);
    	
    	
    	sys.color(Colors.BLACK);
    	
    	// cursor
    	if(showCursor)
    	{
	    	for (Pointer p : sys.pointers()) {
	    		sys.fill(0,p.x(), p.y(), 3,3, Colors.RED);
	    	
				
			}
    	}
		
    }

	public boolean update() 
	{
		Pointer m = sys.pointers()[0];
		if(tool!=null && tool.isBusy())
		{
			// tool is busy
			if(m.btnp(0))
			{
				sys.trace("Hurry up");
				tool.hurryUp();
			}
			tool.update(this, m);
			return true;
		}
		
		// prevent window click to continue on the canvas
		if(cooldownMouse)
		{
			cooldownMouse = m.btn(0);
			return true;
		}
		
		
		if(m.x()<SURFWIDTH)
		{
			if(w!=null) 
			{
				if (!w.update(m)) 
				{
					w = null;
				}
			}
			else if(tool != null)
			{
				tool.update(this, m);
			}
		}
		else if (w == null && m.btnp(0) )
		{
			if(m.y()<20)
			{
				w = new ColorWindow(i -> {cur = i; cooldownMouse = true;});
			}
			else
			{
				
				int tn = ((m.y()-20) / 20);
				if(tn<tools.length)
				{
					try {
						BaseTool bt = tools[tn].toolClass.newInstance(); 
						if (bt instanceof ClickyTool) {
							((ClickyTool) bt).execute(this);
						}
						else
						{
							tool = (Tool) bt;
						}
				
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			
			}
			cooldownMouse = true;
		}
		
        return false;
    }

	public boolean loop() 
	{
		boolean res = update();
		render();
		
        return res;
    }


	@Override
	public SysConfig sysConfig() 
	{
		return new SysConfig(WIDTH, HEIGHT, VirtualScreenMode.STRETCH_FULL, "RetroDrawing", "retrodrawing");
	}

	@Override
	public int currentColor() {
		return cur;
	}

	@Override
	public Sys getSys() {
		return sys;
	}

	@Override
	public void recordUndo() {
		byte[] buf = SurfUtils.surfaceToBuffer(sys, surface, 0, 0, SURFWIDTH, HEIGHT);
		undos.push(buf);
		if(undos.size()>10)
			undos.remove(0);
		
	}

	@Override
	public void undo() {
		if (!undos.isEmpty())
		{
			byte[] buf = undos.pop();
			SurfUtils.bufferToSurface(buf, sys, surface, 0, 0, SURFWIDTH, HEIGHT);
		}
		
	}

	@Override
	public int getSurface() {
		
		return surface;
	}
  
}
