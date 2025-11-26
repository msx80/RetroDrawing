package com.github.msx80.retrodrawing;


import java.util.Stack;

import com.github.msx80.omicron.api.Game;
import com.github.msx80.omicron.api.Pointer;
import com.github.msx80.omicron.api.Sys;
import com.github.msx80.omicron.api.SysConfig;
import com.github.msx80.omicron.api.SysConfig.VirtualScreenMode;
import com.github.msx80.omicron.basicutils.Colors;
import com.github.msx80.omicron.basicutils.ShapeDrawer;
import com.github.msx80.omicron.basicutils.SpriteSheet;
import com.github.msx80.omicron.basicutils.text.TextDrawer;
import com.github.msx80.omicron.basicutils.text.TextDrawerFixed;

public class RetroDrawing implements Game, Ctx {
	
	ToolItem[] tools = {
			new ToolItem(0, SmallPen.class),
			new ToolItem(0, MediumPen.class),
			new ToolItem(0, BigPen.class),
			new ToolItem(0, SlowFill.class),
			new ToolItem(0, Undo.class),
			// new ToolItem(0, CleanAll.class),
			
			
			
	};
	
	public static final int HEIGHT = 144;
	public static final int WIDTH = 256;
	
	public static final int SURFWIDTH = WIDTH-20;
	
	public static final int SURFACE_ICONS = 2;
	public static final SpriteSheet icons = new SpriteSheet(16, 16, 8);
	
	int surface = 0;
	int cur = 4;
	Window w = null;
	Tool tool = null;
	
	Stack<byte[]> undos = new Stack<byte[]>();
	
	TextDrawer td;
	
	boolean cooldownMouse = false;
	boolean showCursor;
	
	
    public void init() 
    {
        surface = Sys.newSurface(SURFWIDTH, HEIGHT);
        Sys.fill(surface, 0,0,SURFWIDTH, HEIGHT, Palette.P[0]);
        
        tool = new MediumPen() ;
        
        
        
        try
        {
	        String platform = (String) Sys.hardware("com.github.msx80.omicron.plugins.builtin.PlatformPlugin", "PLATFORM", "");
	        Sys.trace("Platform: "+platform);
	        showCursor = !platform.equals("ANDROID");
        }
        catch (Exception e) {
			Sys.trace("Error getting platform: "+e.getMessage());
			showCursor = true;
		}
		
    }

    public void render() 
    {
    	Sys.clear(Colors.WHITE);
    	ShapeDrawer.outline(SURFWIDTH, 0, WIDTH-SURFWIDTH, HEIGHT,0, Colors.from(30, 30, 50));
    	
    	
    	Sys.fill(0, SURFWIDTH+2, 2, 16, 16, Palette.P[cur]);
    	
    	ShapeDrawer.outline(SURFWIDTH+2, 2, 16, 16,0, Colors.from(30, 30, 50));
    	
    	// options
    	Sys.color(Colors.from(255, 255, 255,100));
    	Sys.draw(SURFACE_ICONS, SURFWIDTH+2, 122, 5*16, 0, 16,16,0,0);
    	
		
    	for (int i = 0; i < tools.length; i++) {
			ToolItem ti = tools[i];
			if(ti.toolClass.isInstance(tool)) 
	    	{
	    		Sys.color(Colors.WHITE);
	    	}
	    	else
	    	{
	    		Sys.color(Colors.from(255, 255, 255,100));
	    	}
			
	    	Sys.draw(SURFACE_ICONS, SURFWIDTH+2, 20*(i+1), i*16, 0, 16,16, 0,0);
		}
    	
    	Sys.color(Colors.WHITE);
    	Sys.draw(surface, 0,0,0,0, SURFWIDTH, HEIGHT, 0, 0);
    	
    	if(w!=null) w.draw(0);
    	
    	
    	Sys.color(Colors.BLACK);
    	
    	// cursor
    	if(showCursor)
    	{
	    	for (Pointer p : Sys.pointers()) {
	    		Sys.fill(0,p.x(), p.y(), 3,3, Colors.RED);
	    	
				
			}
    	}
		
    }

	public boolean update() 
	{
		Pointer m = Sys.pointers()[0];
		if(tool!=null && tool.isBusy())
		{
			// tool is busy
			if(m.btnp(0))
			{
				Sys.trace("Hurry up");
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
			else if(m.y()>=120)
			{
					w = new OptionsWindow(this);
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
	public void recordUndo() {
		byte[] buf = SurfUtils.surfaceToBuffer(surface, 0, 0, SURFWIDTH, HEIGHT);
		undos.push(buf);
		if(undos.size()>10)
			undos.remove(0);
		
	}

	@Override
	public void undo() {
		if (!undos.isEmpty())
		{
			byte[] buf = undos.pop();
			SurfUtils.bufferToSurface(buf, surface, 0, 0, SURFWIDTH, HEIGHT);
		}
		
	}

	@Override
	public int getSurface() {
		
		return surface;
	}
  
}
