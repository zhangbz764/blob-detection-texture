package main;

import processing.core.PApplet;

public class Brush {
	public float x;
	public float y;
	public float r1, r2;

	public Brush(PApplet app, float x, float y) {
		this.x = x;
		this.y = y;
		this.r1 = app.random(20, 40);
		this.r2 = app.random(20, 40);
//		app.filter(PConstants.BLUR, 4);
	}

	public void updateLoc(PApplet app) {
		if (app.mouseX > x - r1 * 0.25f && app.mouseX < x + r1 * 0.25f && app.mouseY > y - r2 * 0.25f
				&& app.mouseY < y + r2 * 0.25f) {
			this.x = app.mouseX;
			this.y = app.mouseY;
		}
	}

	public void updateRadius(float delta) {
		this.r1 += (float) (Math.random() * delta);
		this.r2 += (float) (Math.random() * delta);
	}

	public void display(PApplet app) {

		app.pushStyle();
		app.fill(0);
		app.noStroke();
		app.ellipse(x, y, r1, r2);

		app.popStyle();

	}
}
