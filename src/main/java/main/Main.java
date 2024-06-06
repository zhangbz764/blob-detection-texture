package main;

import processing.core.PApplet;

public class Main extends PApplet {

    BlobManager bm;
    boolean curr, target, blob;// decide site pictrues' visibility

    public static void main(String[] args) {
        PApplet.main(Main.class.getName());
    }

    public void settings() {
        size(1280, 720, P2D);
    }

    public void setup() {
        bm = new BlobManager(this);
    }

    public void draw() {
        background(180);

        bm.displayTargetSite(this, target);
        bm.displayBlobs(this, blob);
        bm.displayCurrSite(this, curr);
    }

    public void keyPressed() {
        if (key == '1') {
            curr = !curr;
        }
        if (key == '2') {
            blob = !blob;
        }
        if (key == '3') {
            target = !target;
        }

        if (key == 'e' || key == 'E') {
            bm.addNewBrush(this);
        }
        if (key == 'w' || key == 'W') {
            bm.keyUpdate(this, 5);
        }
        if (key == 'n' || key == 'N') {
            bm.computeNewBlobs(this);
        }
        if (key == 'r' || key == 'R') {
            bm.computeRecordNewBlobs(this);
        }
        if (key == 't' || key == 'T') {
            bm.thresholdUpdateAndRecordNewBlobs(this);
        }
        if (key == 's' || key == 'S') {
            bm.saveEdgesTo3dm(this);
        }
    }

    public void mouseDragged() {
        bm.dragUpdate(this);
    }

}