package com.example;

import java.awt.*;
import java.lang.*;
import graph.*;
import stat.array;

class Plot {
    static void seri(Graph2D graph,double data[],int np,
		     boolean drawyaxis,String xlabel){
	DataSet dataset;
	double  dat[]=new double[2*np];
	Axis    xaxis,yaxis;
	for (int i=0; i<np; i++) {
	    dat[2*i] = i;
	    dat[2*i+1] = data[i];
	}
	graph.drawzero = false;
	//graph.drawgrid = false;
	graph.detachDataSets();
	graph.detachAxes();
	dataset = graph.loadDataSet(dat, np);
	dataset.linecolor = new Color(0,0,255);
	xaxis = graph.createAxis(Axis.BOTTOM);
	xaxis.attachDataSet(dataset);
	xaxis.setTitleText(xlabel);
	xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,14));
	//xaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,14));
	if (drawyaxis) {
	    yaxis = graph.createAxis(Axis.LEFT);
	    yaxis.attachDataSet(dataset);
	}
	graph.repaint();
    }

    static void mat2(Graph2D graph,double[][] mat,int len,double mix[][]){
	int i,j;
	double dat[][]=new double[2][2*len];
	DataSet dataset;
	Axis xaxis=graph.createAxis(Axis.BOTTOM),
	     yaxis=graph.createAxis(Axis.LEFT);
	graph.drawzero = false;
	//graph.drawgrid = false;
	graph.gridcolor = new Color(180,255,180);
	graph.detachDataSets();
	for (j=0; j<2; j++){
	    for (i=0; i<len; i++){
		dat[j][2*i]=i;
		dat[j][2*i+1]=mat[0][2*i]*mix[0][j]+mat[0][2*i+1]*mix[1][j];
	    }
	    dataset = graph.loadDataSet(dat[j],len);
	    dataset.linecolor   =  new Color(0,0,255);
	    xaxis.attachDataSet(dataset);
	    yaxis.attachDataSet(dataset);
	    for (i=0; i<len; i++){
		dat[j][2*i]=i;
		dat[j][2*i+1] = mat[1][2*i]*mix[0][j]+mat[1][2*i+1]*mix[1][j];
	    }
	    dataset = graph.loadDataSet(dat[j],len);
	    dataset.linecolor   =  new Color(255,0,0);
	    xaxis.attachDataSet(dataset);
	    yaxis.attachDataSet(dataset);
	}
	xaxis.setTitleText("global matrix: row1=blue, row2=red");
	xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,14));
	//xaxis.setTitlaFont(new Font("Helvetica",Font.PLAIN,14));
	graph.repaint();
    }

    static void scatter(Graph2D graph,double x[],double y[],int n,
			double axes[][],String title){
	double dat[]=new double[2*n];
	DataSet dataset;
	Markers markers=new Markers();
	markers.AddMarker(5,new boolean[]{true,true,true,true,true},
			  new int[]{-1,-1,1,1,-1},new int[]{-1,1,1,-1,-1});
	for (int i=0; i<n; i++){
	    dat[2*i]=x[i];
	    dat[2*i+1]=y[i];
	}
	Axis xaxis=graph.createAxis(Axis.BOTTOM),
	     yaxis=graph.createAxis(Axis.LEFT);
	graph.drawzero = false;
	//graph.drawgrid = false;
	graph.setMarkers(markers);
	graph.detachDataSets();
	dataset = graph.loadDataSet(dat,n); 
	dataset.linestyle   =  dataset.NOLINE;
	dataset.marker      = 1;
	dataset.markercolor = new Color(0,0,255);
	xaxis.attachDataSet(dataset);
	yaxis.attachDataSet(dataset);
	if (axes != null) {
	    dataset = graph.loadDataSet(axes[0],2);
	    dataset.linecolor = new Color(255,0,0);
	    xaxis.attachDataSet(dataset);
	    yaxis.attachDataSet(dataset);
	    dataset = graph.loadDataSet(axes[1],2);
	    dataset.linecolor = new Color(255,0,0);
	    xaxis.attachDataSet(dataset);
	    yaxis.attachDataSet(dataset);
	}
	xaxis.setTitleText(title);
	xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,14));
	//xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,16));
	//xaxis.setTitlaFont(new Font("Helvetica",Font.PLAIN,14));
	graph.repaint();
    }

    static void xy(Graph2D graph,double x[],double y[],int n,String title){
	double dat[]=new double[2*n];
	DataSet dataset;
	int i, indexx[] = array.indx(x,n), indexy[] = array.indx(y,n);
	for (i=0; i<n; i++){
	    dat[2*i]=x[indexx[i]];
	    dat[2*i+1]=y[indexy[i]];
	}
	Axis xaxis=graph.createAxis(Axis.BOTTOM),
	     yaxis=graph.createAxis(Axis.LEFT);
	graph.drawzero = false;
	//graph.drawgrid = false;
	graph.detachDataSets();
	dataset = graph.loadDataSet(dat,n); 
	dataset.linecolor   =  new Color(0,0,255);
	xaxis.attachDataSet(dataset);
	yaxis.attachDataSet(dataset);
	xaxis.setTitleText(title);
	xaxis.setTitleFont(new Font("TimesRoman",Font.PLAIN,14));
	//xaxis.setTitlaFont(new Font("Helvetica",Font.PLAIN,14));
	graph.repaint();
    }
}
