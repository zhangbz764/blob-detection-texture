package main;

import java.util.ArrayList;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import igeo.ICurve;
import igeo.IG;
import igeo.IVec;
import processing.core.PApplet;
import processing.core.PGraphics;

public class BlobManager {
	private BlobDetection bd;
	private FileLoader fl;
	private ArrayList<Brush> brush;
	private PGraphics img;
	private float blur = 10;

	public ArrayList<ICurve> edgeCurves;
	ArrayList<ArrayList<EdgeVertex>> startAll = new ArrayList<ArrayList<EdgeVertex>>();
	ArrayList<ArrayList<EdgeVertex>> endAll = new ArrayList<ArrayList<EdgeVertex>>();

	private float threshold = 0.3f;

	public BlobManager(PApplet app) {
		fl = new FileLoader(app);

		brush = new ArrayList<Brush>();
		brush.add(new Brush(app, (float) app.width / 2, (float) app.height / 2));

		bd = new BlobDetection(app.width, app.height);
		bd.setPosDiscrimination(false);
		bd.setThreshold(threshold);

		img = app.createGraphics(app.width, app.height);
		createBlobGraphics(app);
		img.filter(PApplet.BLUR, blur);

		bd.computeBlobs(img.pixels);
	}

	////////////////////////////////////////////////////////////////////////// PGraphic
	/* set the PGraphic */
	private void createBlobGraphics(PApplet app) {
		img.beginDraw();

		img.background(255, 0);
		img.fill(0);
		for (Brush b : brush) {
			img.ellipse(b.x, b.y, b.r1, b.r2);
		}

		img.beginShape();
		// Exterior part of shape, clockwise winding
		for (int i = 0; i < fl.currPathOut.get(0).getPoints().size(); i++) {
			img.vertex(fl.currPathOut.get(0).getPoints().get(i).xf(), fl.currPathOut.get(0).getPoints().get(i).yf());
		}
		// Interior part of shape, counter-clockwise winding
		img.beginContour();
		for (int i = 0; i < fl.currPathIn.get(0).getPoints().size(); i++) {
			img.vertex(fl.currPathIn.get(0).getPoints().get(i).xf(), fl.currPathIn.get(0).getPoints().get(i).yf());
		}
		img.endContour();
		img.endShape(PApplet.CLOSE);

		img.endDraw();
	}

	////////////////////////////////////////////////////////////////////////// update
	////////////////////////////////////////////////////////////////////////// control
	/* add new brush at mouse's position */
	public void addNewBrush(PApplet app) {
		brush.add(new Brush(app, app.mouseX, app.mouseY));
		createBlobGraphics(app);
		System.out.println("control point added. TOTAL : " + brush.size());
	}

	/* brush moves when mouse dragged */
	public void dragUpdate(PApplet app) {
		for (Brush b : brush) {
			b.updateLoc(app);
		}
		createBlobGraphics(app);
	}

	/* brush and path enlarged when key pressed */
	public void keyUpdate(PApplet app, float delta) {
		for (Brush b : brush) {
			b.updateRadius(delta);
		}
		fl.updatePath();
		createBlobGraphics(app);
	}

	/* add blur filter, compute new blobs, controlled by key */
	public void computeNewBlobs(PApplet app) {
		System.out.println("computing new blobs......");
		img.filter(PApplet.BLUR, blur);
		bd.computeBlobs(img.pixels);
	}

	/* add blur filter, compute new blobs, and record vertices, controlled by key */
	public void computeRecordNewBlobs(PApplet app) {
		img.filter(PApplet.BLUR, blur);
		bd.computeBlobs(img.pixels);
		System.out.println("current blob number = " + bd.blobNumber);
		recordEdgeVertex(app);
	}

	/* add threshold of BlobDetection, compute new blobs, and record vertices */
	public void thresholdUpdateAndRecordNewBlobs(PApplet app) {
		threshold += 0.07f;
		bd.setThreshold(threshold);
		bd.computeBlobs(img.pixels);
		recordEdgeVertex(app);
		System.out.println("blob number of this layer: " + bd.blobNumber);
	}

	////////////////////////////////////////////////////////////////////////// edges
	////////////////////////////////////////////////////////////////////////// processing
	/*  */
	private void recordEdgeVertex(PApplet app) {
		Blob b;
		EdgeVertex eA, eB;

		for (int n = 0; n < bd.getBlobNb(); n++) {
			b = bd.getBlob(n);
			ArrayList<EdgeVertex> currStart = new ArrayList<EdgeVertex>();
			ArrayList<EdgeVertex> currEnd = new ArrayList<EdgeVertex>();
			for (int m = 0; m < b.getEdgeNb(); m++) {
				eA = b.getEdgeVertexA(m);
				eB = b.getEdgeVertexB(m);
				if (eA != null && eB != null) {
					currStart.add(eA);
					currEnd.add(eB);
				}
			}
			startAll.add(currStart);
			endAll.add(currEnd);
		}
	}

	public void saveEdgesTo3dm(PApplet app) {
		IG.init();
		for (ArrayList<EdgeVertex> currStart : startAll) {
			IVec[] edgeVertexList = new IVec[currStart.size()];
			for (int i = 0; i < currStart.size(); i++) {
				edgeVertexList[i] = new IVec(currStart.get(i).x * app.width, currStart.get(i).y * app.height);
			}
			new ICurve(edgeVertexList, true).layer("edges");
		}
		System.out.println(startAll.size());
		IG.save("./src/main/resources/test.3dm");
	}

	////////////////////////////////////////////////////////////////////////// display
	////////////////////////////////////////////////////////////////////////// control
	public void displayCurrSite(PApplet app, boolean curr) {
		if (curr) {
			app.image(fl.curr_site, 0, 0, app.width, app.height);
		}
	}

	public void displayTargetSite(PApplet app, boolean target) {
		if (target) {
			app.image(fl.target_site, 0, 0, app.width, app.height);
		}
	}

	public void displayAllEdges(PApplet app) {
		app.noFill();
		app.strokeWeight(2);
		app.stroke(255, 50, 50);
		for (int i = 0; i < startAll.size(); i++) {
			for (int j = 0; j < startAll.get(i).size(); j++) {
				app.line(startAll.get(i).get(j).x * app.width, startAll.get(i).get(j).y * app.height,
						endAll.get(i).get(j).x * app.width, endAll.get(i).get(j).y * app.height);
			}

		}
	}

	public void displayCurrentEdges(PApplet app) {
		app.noFill();

		Blob b;
		EdgeVertex eA, eB;
		for (int n = 0; n < bd.getBlobNb(); n++) {
			b = bd.getBlob(n);
			app.strokeWeight(4);
			app.stroke(255, 50, 50);
			for (int m = 0; m < b.getEdgeNb(); m++) {
				eA = b.getEdgeVertexA(m);
				eB = b.getEdgeVertexB(m);
				if (eA != null && eB != null) {
					app.line(eA.x * app.width, eA.y * app.height, eB.x * app.width, eB.y * app.height);
				}
			}
//			app.strokeWeight(1);
//			app.stroke(255, 0, 0);
//			app.rect(b.xMin * app.width, b.yMin * app.height, b.w * app.width, b.h * app.height);
		}
	}

	public void displayBlobs(PApplet app, boolean draw) {
		if (draw) {
			app.image(img, 0, 0, app.width, app.height);
			displayAllEdges(app);
			displayCurrentEdges(app);
		}

	}

}
