package com.wvs.shoppercrux.activities;

public class DemoViewModel {

	private String title;
	private int resourceId;
	private int demoViewText;

	public DemoViewModel(String title, int resourceId, int demoViewText) {
		this.title = title;
		this.resourceId = resourceId;
		this.demoViewText = demoViewText;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getDemoViewText() {
		return demoViewText;
	}

	public void setDemoViewText(int demoViewText) {
		this.demoViewText = demoViewText;
	}
}
