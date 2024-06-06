package main;

import java.util.ArrayList;
import java.util.List;

import igeo.ICurve;
import igeo.IG;
import processing.core.PApplet;
import processing.core.PImage;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

public class FileLoader {
	public PImage curr_site, target_site;

	private WB_GeometryFactory gf;
	public WB_Polygon oriPathOut;
	public WB_Polygon oriPathIn;
	public List<WB_Polygon> currPathOut;
	public List<WB_Polygon> currPathIn;
	
	public ArrayList<WB_Point> ptout;
	public ArrayList<WB_Point> ptin;

	public FileLoader(PApplet app) {
		curr_site = app.loadImage("./src/main/resources/curr_site.png");
		target_site = app.loadImage("./src/main/resources/target_site.png");

		loadPath();
	}

	private void loadPath() {
		IG.init();
		IG.open("./src/main/resources/path.3dm");
		ICurve[] poly = IG.layer("zzz").curves();

		gf = new WB_GeometryFactory();
		ptout = new ArrayList<>();
		ptin = new ArrayList<>();
		for (int i = 0; i < poly[0].cps().length - 1; i++) {
			ptout.add(new WB_Point(poly[0].cps()[i].x(), poly[0].cps()[i].y()));
		}
		for (int i = 0; i < poly[1].cps().length - 1; i++) {
			ptin.add(new WB_Point(poly[1].cps()[i].x(), poly[1].cps()[i].y()));
		}
//		oriPath = gf.createPolygonWithHole(ptout, ptin);
		oriPathOut = gf.createSimplePolygon(ptout);
		oriPathIn = gf.createSimplePolygon(ptin);
		currPathOut = gf.createBufferedPolygons(oriPathOut, 0);
		currPathIn = gf.createBufferedPolygons(oriPathIn, 0);
	}

	public void updatePath() {
		currPathOut = gf.createBufferedPolygons(currPathOut, 1);
		currPathIn = gf.createBufferedPolygons(currPathIn, -1);
	}
	

}
