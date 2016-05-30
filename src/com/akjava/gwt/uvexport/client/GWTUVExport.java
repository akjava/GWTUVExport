package com.akjava.gwt.uvexport.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Color;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTUVExport implements EntryPoint {

	private Canvas canvas;
	private double size;
	private VerticalPanel container;
	private Label messageLabel;
	private double strokeSize=1;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel root=new VerticalPanel();
		RootPanel.get().add(root);
		
		HorizontalPanel panel=new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		root.add(panel);
		FileUploadForm fileUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				
				
				loadJson(text);
				
			}
		}).setAccept(".json").insertTo(panel);
		
		panel.add(new Label("size:"));
		ValueListBox<Integer> sizeListBox=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
				if(object!=null){
					return String.valueOf(object);
				}
				return null;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		sizeListBox.setValue(1024);
		sizeListBox.setAcceptableValues(Lists.newArrayList(256,512,1024,2048,4096,8192));
		sizeListBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				size=event.getValue();
				CanvasUtils.setSize(canvas, (int)size, (int)size);
				if(lastGeometry!=null){
					drawUV(lastGeometry,lastMaterials);
				}
			}
		});
		panel.add(sizeListBox);
		
		List<Double> sizes=Lists.newArrayList();
		for(double i=1;i<=32;i+=1){
			sizes.add(i);
		}
		
		panel.add(new Label("stroke-size:"));
		ValueListBox<Double> storokeSizeListBox=new ValueListBox<Double>(new Renderer<Double>() {

			@Override
			public String render(Double object) {
				if(object!=null){
					return String.valueOf(object);
				}
				return null;
			}

			@Override
			public void render(Double object, Appendable appendable) throws IOException {
				
			}
		});
		storokeSizeListBox.setValue(1.0);//extend because remove antialias
		storokeSizeListBox.setAcceptableValues(sizes);
		storokeSizeListBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				strokeSize=event.getValue();
				if(lastGeometry!=null){
					CanvasUtils.clear(canvas);
					drawUV(lastGeometry,lastMaterials);
				}
			}
		});
		panel.add(storokeSizeListBox);
		
		
		
		messageLabel = new Label();
		root.add(messageLabel);
		
		container = new VerticalPanel();
		container.add(createCanvas());
		root.add(container);
		
		//TODO mix option and set that default
		
		/* for test
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				loadJson("white.json");
			}
		});
		*/
	}
	
	private Geometry lastGeometry;
	private JsArray<Material> lastMaterials;
	public void loadJson(String url){
		
		CanvasUtils.clear(canvas);
		
		THREE.JSONLoader().load(url, new JSONLoadHandler() {
			@Override
			public void loaded(Geometry geometry, JsArray<Material> materials) {
				lastGeometry=geometry;
				lastMaterials=materials;
				drawUV(geometry,materials);
			}

			
		});
	}
	
	
	private Widget createCanvas() {
		size = 1024*1;
		canvas = CanvasUtils.createCanvas(size, size);
		return canvas;
	}

	public double toX(double x){
		return size*x;
	}
	public double toY(double y){
		return size*(1.0-y);
	}
	
	private void drawUV(Geometry geometry,final @Nullable JsArray<Material> materials) {
		drawUV(geometry,materials,strokeSize);
		drawUV(geometry,materials,1);
		
		/*
		if(materials!=null){
		Scheduler.get().scheduleEntry(new ScheduledCommand() {
			
			@Override
			public void execute() {
				fixColors(materials);
				LogUtils.log("fix");
			}
		});
		}
		*/
	}
	
	private int[] materialToRgb(Material material){
		int[] result=new int[3];
		Color color=material.gwtGetColor();
		result[0]=(int) (color.getR()*255);
		result[1]=(int) (color.getG()*255);
		result[2]=(int) (color.getB()*255);
		return result;
	}
	
	private int[] findClosest(int r,int g,int b,List<int[]> colors){
		int[] result=colors.get(0);
		double[] luv=LuvUtils.toLab(r, g,b);
		double l=luv[0];
		double u=luv[1];
		double v=luv[2];
		
		int[] rgb=colors.get(0);
		double[] color=toLuv(colors.get(0));
		double minlength=ColorUtils.getColorLength(l, u, v, color[0], color[1], color[2]);
		
		for(int i=1;i<colors.size();i++){
			rgb=colors.get(i);
			color=toLuv(colors.get(i));
			double length=ColorUtils.getColorLength(l, u, v, color[0], color[1], color[2]);
			if(length<minlength){
				minlength=length;
				result=rgb;
			}
		}
		
		return result;
	}
	
	
	Map<String,double[]> luvMap=Maps.newHashMap();
	private double[] toLuv(int[] rgb) {
		String key=rgb[0]+","+rgb[1]+rgb[2];
		double[] result=luvMap.get(key);
		if(result==null){
		result=LuvUtils.toLab(rgb[0], rgb[1], rgb[2]);
		luvMap.put(key, result);
		}
		return result;
		// TODO Auto-generated method stub
		
	}


	private void fixColors(JsArray<Material> materials) {
		
		//convert color
		List<int[]> colors=Lists.newArrayList();
		for(int i=0;i<materials.length();i++){
			colors.add(materialToRgb(materials.get(i)));
		}
		
		ImageData imageData=CanvasUtils.getImageData(canvas);
		for(int x=0;x<imageData.getWidth();x++){
			for(int y=0;y<imageData.getHeight();y++){
				//kill antialiase
				int alpha=imageData.getAlphaAt(x, y);
				if(alpha!=255 && alpha!=0){
					if(alpha<128){
						imageData.setAlphaAt(0, x, y);
						alpha=0;
					}else{
						imageData.setAlphaAt(255, x, y);
					}	
				}
				
				if(alpha==0){
					continue;
				}
				
				/*
				int red=imageData.getRedAt(x, y);
				int green=imageData.getGreenAt(x, y);
				int blue=imageData.getBlueAt(x, y);
				
				int[] rgb=findClosest(red,green,blue,colors);
				if(red!=rgb[0]){
					imageData.setRedAt(rgb[0], x, y);
				}
				if(green!=rgb[1]){
					imageData.setGreenAt(rgb[1], x, y);
					
				}
				if(blue!=rgb[2]){
					imageData.setBlueAt(rgb[2], x, y);
				}
				*/
			}
		}
		
		canvas.getContext2d().putImageData(imageData, 0, 0);
	}


	private void drawUV(Geometry geometry,@Nullable JsArray<Material> materials,double storokeSize) {
		//TODO support null;
		
		
		
		Context2d context=canvas.getContext2d();
		//LogUtils.log(geometry.getFaces().length());
		
		
		canvas.getContext2d().save();
		context.setLineCap(LineCap.ROUND);
		context.setLineJoin(LineJoin.ROUND);
		
		context.setLineWidth(storokeSize);//some edget problem
		
		//LogUtils.log(geometry.getFaceVertexUvs().length());
		for(int uvAt=0;uvAt<geometry.getFaceVertexUvs().length();uvAt++){//usually single
			
			JsArray<JsArray<Vector2>> arrays=geometry.getFaceVertexUvs().get(uvAt);
			LogUtils.log("faces:"+arrays.length());
			for(int faceAt=0;faceAt<arrays.length();faceAt++){//same as face number
				JsArray<Vector2> array=arrays.get(faceAt);
			
			
			Vector2 last=array.get(array.length()-1);
			context.beginPath();
			
			//context.moveTo(toX(last.getX()),toY(last.getY()));
			
			if(array.length()!=3){
				LogUtils.log("uniq face number:"+array.length());
			}
			
			for(int j=0;j<array.length();j++){//usually j is 3 for each face
				Vector2 vec2=array.get(j);
				if(j==0){
					context.moveTo(toX(vec2.getX()),toY(vec2.getY()));
				}else{
					context.lineTo(toX(vec2.getX()),toY(vec2.getY()));
				}
				//LogUtils.log(toX(vec2.getX())+","+toY(vec2.getY()));
				
				}
			
			context.closePath();//line to first one
			
			if(materials!=null){
			int index=geometry.getFaces().get(faceAt).getMaterialIndex();
			MeshPhongMaterial  phong=materials.get(index).cast();
			String hex=phong.getColor().getHexString();
			context.setFillStyle("#"+hex);
			context.setStrokeStyle("#"+hex);
			if(phong.isTransparent()){
				context.setGlobalAlpha(phong.getOpacity());
			}else{
				context.setGlobalAlpha(1);
			}
			context.fill();
			messageLabel.setText("");
			}else{
				messageLabel.setText("Not contain material/Exported without check Face Materials");
				//no material
				context.setStrokeStyle("#"+000);
			}
			
			context.stroke();
			}
			
		}
		context.setGlobalAlpha(1);//can restore?
		canvas.getContext2d().restore();
		LogUtils.log("draw");
	}
}
