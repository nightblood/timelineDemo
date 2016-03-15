package com.zlf.testdemo01.domain;

public class ImageFloder
{
	private String dir;

	private String firstImagePath;

	private String name;

	private int count;
	
	private boolean selected = true;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImagePath(){
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath){
		this.firstImagePath = firstImagePath;
	}

	public String getName(){
		return name;
	}
	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	

}
