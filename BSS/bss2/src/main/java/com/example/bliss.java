package com.example;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import graph.*;
/*
import stat.arma;
import stat.sepagaus;
import stat.icainf;
import stat.icansng;
*/
import stat.icainfpnl;

public class bliss extends Applet implements ActionListener{
    Frame f=new Frame("BLIND SOURCE SEPARATION");
    Graph2D graph[] = new Graph2D[11];
    double source[][]=new double[2][1000],obs[][]=new double[4][1000],
	   mixmat[][]=new double[2][2];
    boolean done[] = {false,false,false,false};
    String startText;
    URL abc;

    public void init() {
	abc=getCodeBase();
	int width=Integer.parseInt(getParameter("width"));
	int height=Integer.parseInt(getParameter("height"));

	setSize(new Dimension(width,height));
	setLayout(new BorderLayout());
	startText = getParameter("STARTText");
	if (startText == null)
	    startText = "To START the Java demonstration click here"; 
	Button start=new Button(startText);
	add(start,BorderLayout.CENTER);
	start.addActionListener(this);

	//f.setSize(1250,900);
	//f.setSize(1050,800);
	f.setSize(1020,780);
	f.setLayout(new GridLayout(4,3,2,2));
	for (int i=0; i<11; i++) {
	    graph[i] = new Graph2D();
	    graph[i].borderTop = 2;
	    graph[i].borderBottom = -5;
	    graph[i].borderLeft = 5;
	    graph[i].drawgrid = false;
	    graph[i].framecolor=new Color(10,0,1);
	    graph[i].setGraphBackground(new Color(255,255,255));
	}
	graph[9].drawgrid = true;
	f.add(createMainPanel());
	f.add(graph[1]);
	f.add(graph[2]);
	f.add(graph[10]);
	f.add(graph[3]);
	f.add(graph[4]);
	f.add(graph[0]);
	f.add(graph[5]);
	f.add(graph[6]);
	f.add(graph[9]);
	f.add(graph[8]);
	f.add(graph[7]);
    }

    public void stop(){
	f.setVisible(false);
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals(startText))
	    //f.setSize(1250,900);
	    //f.setSize(1050,800);
	    f.setVisible(true);
    }

    Panel createMainPanel() {
	Panel mainPanel = new Panel(), p;

	p = new Panel();
	Choice src=new Choice();
 	src.add("---Select Source---");
	src.add("Source 1");
	src.add("Source 2");
	p.add(src);
	mainPanel.add(p);

	Button mix=new Button("Create Mixture");
	p = new Panel();
        p.add(mix);
	mainPanel.add(p);

	p = new Panel();
        Choice postnonli=new Choice();
	postnonli.add("---Postnonlinear Transform---");
	postnonli.add("Postnonlinear Transform Source 1");
	postnonli.add("Postnonlinear Transform Source 2");
	p.add(postnonli);
	mainPanel.add(p);

	p = new Panel();
        Choice algo=new Choice();
        algo.add("---Select Algorithm---");
        algo.add("Gaussian Mutual Info. Criterion");
        algo.add("Marginal Mutual Info. Criterion");
        algo.add("Nonstationary non Gaussian method");
        algo.add("Mutual Info. for Postnonlin. mixture");
	p.add(algo);
	mainPanel.add(p);

	p = new Panel();
        Button help=new Button("Help");
	p.add(help);
	Button about=new Button("About");
	p.add(about);
	Button exit=new Button("Exit");
 	p.add(exit);
	mainPanel.add(p);

	MyHandler handler= new MyHandler(f,graph,abc,source,obs,done,mixmat);
        src.addItemListener(handler);
	mix.addActionListener(handler);
	postnonli.addItemListener(handler);
	algo.addItemListener(handler);
	help.addActionListener(handler);
	about.addActionListener(handler);
	exit.addActionListener(handler);
	mainPanel.setLayout(new GridLayout(0,1));
	return mainPanel;
    }
}

class MyHandler implements ActionListener, ItemListener {
    Frame frame;
    Graph2D graph[];
    URL abc;
    double source[][],obs[][],mixmat[][];
    boolean done[];
    Menu observe=new Menu("observe");	//bogus menu to accomodate bss.java
    public MyHandler(Frame frame,Graph2D graph[],URL abc,double source[][],
		     double obs[][],boolean done[],double mixmat[][]){
	this.graph=graph;
	this.abc=abc;
	this.source=source;
	this.obs=obs;
	this.done=done;
	this.mixmat=mixmat;
	this.frame=frame;
    }

    public void actionPerformed(ActionEvent ae){
	String arg=(String)ae.getActionCommand();

	if (arg.equals("Create Mixture"))
	   if (done[0] & done[1]) {
	       Mixture mixture=
		   new Mixture(frame,"Create Matrix",graph[3],graph[4],
			       graph[10],graph[0],source[0],source[1],
			       obs,done,mixmat);
		mixture.setVisible(true);
		//obsmix.setSize(250,200);
	   }
	   else {
	       errmsg erm=new errmsg(frame,"ERROR",
				     "Sources all yet selected yet",250,140);
	       erm.setVisible(true);
	   }
	if (arg.equals("Help")){
	    helpbliss hp=new helpbliss(frame,"Help");
	    hp.setVisible(true);
	    //hp.setSize(850,750);
	}
	if (arg.equals("About")){
	    about hp=new about(frame,"About");
	    hp.setVisible(true);
	    //hp.setSize(850,600);
	}

	if(arg.equals("Exit")){
	    //frame.setVisible(false);
	    frame.dispose();
	}
    }

    public void itemStateChanged(ItemEvent ie){

	String arg=(String)ie.getItem();

	if (arg.equals("Source 1")){
	    SelectSource selsou=
		new SelectSource(frame,"Select the source",graph[1],abc,
				 0,source[0],done,observe);
	    selsou.setVisible(true);
	    //selsou.setSize(220,300);
	}
	if (arg.equals("Source 2")){
	    SelectSource selsou=
		new SelectSource(frame,"Select the source",graph[2],abc,
				 1,source[1],done,observe);
	    selsou.setVisible(true);
	    //selsou.setSize(220,300);
	}
	if (arg.equals("Postnonlinear Transform Source 1"))
	    if (done[2]) {
		SelecTransf seltransf=
		    new SelecTransf (frame,"Select the transformation",
				     graph[3],graph[10],0,obs[0],obs[2]);
		seltransf.setVisible(true);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
	if (arg.equals("Postnonlinear Transform Source 2"))
	    if (done[2]) {
		SelecTransf seltransf=
		    new SelecTransf (frame,"Select the transformation",
				     graph[4],graph[0],1,obs[1],obs[3]);
		seltransf.setVisible(true);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
	if (arg.equals("Gaussian Mutual Info. Criterion"))
	    if (done[2]){
		sepain sp=new sepain(frame,"Gaussian Mut. Info.",graph[5],
				     graph[6],graph[7],graph[8],graph[9],
				     obs,mixmat);
		sp.setVisible(true);
		sp.setSize(525,230);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
	if (arg.equals("Marginal Mutual Info. Criterion"))
	    if (done[2]) {
		incain ic=new incain(frame,"Marginal Mut. Info.",graph[5],
				     graph[6],graph[7],graph[8],graph[9],
				     obs,mixmat);
		ic.setVisible(true);
		//ic.setSize(600,200);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
	if (arg.equals("Nonstationary non Gaussian method"))
	    if(done[2]) {
		nsngin nsg=new nsngin(frame,"Nonstat. Non Gauss",graph[5],
				      graph[6],graph[7],graph[8],graph[9],
				      obs,mixmat);
		nsg.setVisible(true);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
	if (arg.equals("Mutual Info. for Postnonlin. mixture"))
	    if (done[2]) {
		nonlin nl=new nonlin(frame,"Postnonlin. Mixture",graph[5],
				     graph[6],graph[7],graph[8],graph[9],
				     graph[10],graph[0],obs,mixmat);
	    nl.setVisible(true);
	    }
	    else {
		errmsg erm=
		    new errmsg(frame,"ERROR",
			       "Mixture not yet created",250,140);
		erm.setVisible(true);
	    }
    }
}

class SelecTransf extends Dialog implements ActionListener{
    List transf;
    Graph2D graph, grapht;
    URL abc;
    int num;
    double obs[],obs0[];
    boolean done[];
    Frame parent;
    SelecTransf(Frame parent,String title,Graph2D graph, Graph2D grapht,
		 int num,double obs[],double obs0[]){
	super(parent,title,false);
	this.graph=graph;
	this.grapht=grapht;
	this.num=num;
	this.obs=obs;
	this.obs0=obs0;
	this.done=done;
	this.parent=parent;
	setLayout(new BorderLayout());
	setResizable(false);
	setSize(220,160);
	transf=new List(2,false);
	//transf.setSize(200,120);		//not working ???
	transf.add("x -> tanh(ax) + bx");
        transf.add("x -> x^3 + ax");
	transf.select(0);
	add(transf);

	Panel p = new Panel(new GridLayout());
	Button ok=new Button("OK");
	Button cancel=new Button("Cancel");
	p.add(ok);
	p.add(cancel);
	add(p,BorderLayout.SOUTH);

	transf.addActionListener(this);
	ok.addActionListener(this);
	cancel.addActionListener(this);

	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae) {
	String st=ae.getActionCommand();
	String str=transf.getSelectedItem();

	if(st.equals("Cancel")){
	    dispose();
	}
	else if(str.equals("x -> tanh(ax) + bx")) {
	    tanhin s=new tanhin(parent,"x -> tanh(ax) + bx",obs,obs0,num,
				graph,grapht);
	    //s.setSize(700,250);
	    s.setVisible(true);
	    dispose();
	}
	else if(str.equals("x -> x^3 + ax")) {
	    cubein c=new cubein(parent,"x -> x^3 + bx",obs,obs0,num,
				graph,grapht);
	    c.setVisible(true);
	    //gai.setSize(580,350);
	    dispose();
  	}
    }
}

/*
class MyWinAdapter extends WindowAdapter{
    Window window;
    public MyWinAdapter(Window window){
	this.window = window;
    }

    public void windowClosing(WindowEvent we){
	window.setVisible(false);
    }
}
*/

class tanhin extends Dialog implements ActionListener{
    Frame parent;
    TextField fa,fb;
    Button ok;
    Graph2D graph, grapht;
    double obs[],obs0[];
    int num;
    tanhin(Frame parent,String title,double obs[],double obs0[],int num,
	   Graph2D graph, Graph2D grapht){ 
	super(parent,title,false);
	this.graph=graph;
	this.grapht=grapht;
	this.obs=obs;
	this.obs0=obs0;
	this.num=num;
	setLayout(new GridLayout(3,2,3,3));
	setSize(300,200);
	add(new Label("Coefficient a"));
	fa=new TextField("4",5);
	add(fa);
	add(new Label("Coefficient b"));
	fb=new TextField("0.1",5);
	add(fb);
	ok=new Button("ok");
	ok.addActionListener(this);
	add(ok);
	Button cancel=new Button("cancel");
	cancel.addActionListener(this);
	add(cancel);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae) {
	if(ae.getActionCommand().equals("ok")){
	    double a,b,x,e;
	    a=Double.valueOf(fa.getText()).doubleValue();
	    b=Double.valueOf(fb.getText()).doubleValue();

	    for(int i=0; i<1000; i++) {
		x = obs[i];
		e = Math.exp(2*a*x);
		obs[i] = (e-1)/(e+1) + b*x;
	    }
	    Plot.seri(graph,obs,1000,true,"observation "+(num+1));
	    Plot.xy(grapht,obs0,obs,1000,
			 "Postnonlinear transform "+(num+1));
	}
	dispose();
    }
}

class cubein extends Dialog implements ActionListener {
    Frame parent;
    TextField fa;
    Button ok;
    Graph2D graph, grapht;
    double obs[],obs0[];
    int num;
    cubein(Frame parent,String title,double obs[],double obs0[],int num,
	   Graph2D graph, Graph2D grapht){ 
	super(parent,title,false);
	this.graph=graph;
	this.grapht=grapht;
	this.obs=obs;
	this.obs0=obs0;
	this.num=num;
	setLayout(new GridLayout(2,2,3,3));
	setSize(300,150);
	add(new Label("Coefficient a"));
	fa=new TextField("0.1",5);
	add(fa);
	ok=new Button("ok");
	ok.addActionListener(this);
	add(ok);
	Button cancel=new Button("cancel");
	cancel.addActionListener(this);
	add(cancel);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae) {
	if(ae.getActionCommand().equals("ok")){
	    double a,x;
	    a=Double.valueOf(fa.getText()).doubleValue();

	    for(int i=0; i<1000; i++) {
		x = obs[i];
		obs[i] = x*x*x + a*x;
	    }
	    Plot.seri(graph,obs,1000,true,"observation "+(num+1));
	    Plot.xy(grapht,obs0,obs,1000,
			 "Postnonlinear transform "+(num+1));
	}
	dispose();
    }
}

class Mixture extends Dialog implements ActionListener{
    Graph2D graph3,graph4,graph10,graph0;
    TextField f2,f3;
    Button accept,random;
    double source1[],source2[],obs[][],mixmat[][];
    boolean done[];

    Mixture(Frame parent,String title,Graph2D graph3,Graph2D graph4,
	    Graph2D graph10,Graph2D graph0,double source1[],double source2[],
	    double obs[][],boolean done[],double mixmat[][]){
	super(parent,title,false);
	this.graph3=graph3;
	this.graph4=graph4;
	this.graph10=graph10;
	this.graph0=graph0;
	this.source1=source1;
	this.source2=source2;
	this.obs=obs;
	this.done=done;
	this.mixmat=mixmat;

	setLayout(new GridLayout(3,2,3,3));
	setSize(200,160);
	setResizable(false);
	Label f1=new Label("1");
	Label f4=new Label("1");
	f2=new TextField("1",10);
	f3=new TextField("-1",10);
	accept=new Button("Accept");
	random=new Button("Random");
	add(f1);
	add(f2);
	add(f3);
	add(f4);
	add(random);
	add(accept);

	accept.addActionListener(this);
	random.addActionListener(this);

	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	String str=ae.getActionCommand();
	if(str.equals("Accept")){
	    double nf2,nf3;
	    nf2=Double.valueOf(f2.getText()).doubleValue();
	    nf3=Double.valueOf(f3.getText()).doubleValue();
	    for(int i=0; i<1000; i++){
		obs[2][i]=obs[0][i]= source1[i] + nf2*source2[i];
		obs[3][i]=obs[1][i]= source2[i] + nf3*source1[i];
	    }
	    mixmat[0][0] = 1;
	    mixmat[0][1] = nf2;
	    mixmat[1][0] = nf3;
	    mixmat[1][1] = 1;
	    done[2]=true;
	    Plot.seri(graph3,obs[0],1000,true,"observation 1");
	    Plot.seri(graph4,obs[1],1000,true,"observation 2");
	    graph10.detachDataSets();
	    graph10.repaint();
	    graph0.detachDataSets();
	    graph0.repaint();
	    dispose();
        }
        else if (str.equals("Random")){
	    double num1,num2;
	    num1 = Math.tan((Math.random()-0.5)*Math.PI);
	    num2 = Math.tan((Math.random()-0.5)*Math.PI);
	    if (Math.abs(num1*num2) > 1){
		num1 = 1/num1;
		num2 = 1/num2;
	    }
	    f2.setText(""+num1);
	    f3.setText(""+num2);
        }
        else dispose();
    }
}
/*
class nsngin extends Dialog implements ActionListener{
    Frame parent;
    TextField bloclen=new TextField("100",4),maxiter=new TextField("15",4);
    Button ok=new Button("ok"),cancel=new Button("Cancel");
    Graph2D graph5,graph6,graph7,graph8,graph9;
    double obs[][],source1[],source2[],mixmat[][];
    nsngin(Frame parent,String title, Graph2D graph5,Graph2D graph6,
	   Graph2D graph7,Graph2D graph8,Graph2D graph9,double obs[][],
	   double mixmat[][]){
	super(parent,title,false);
	this.parent=parent;
	this.graph5=graph5;
	this.graph6=graph6;
	this.graph7=graph7;
	this.graph8=graph8;
	this.graph9=graph9;
	this.obs=obs;
	this.mixmat=mixmat;

	setSize(525,200);
	setResizable(true);
	setLayout(new GridLayout(3,2,3,3));
	add(new Label("Sta	tionary block Length"));
	add(bloclen);
	add(new Label("Maximum number of iterations"));
	add(maxiter);
	ok.addActionListener(this);
	add(ok);
	cancel.addActionListener(this);
	add(cancel);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals("ok")){
	    int mxiter,blclen, nbloc;
	    mxiter=(int)Double.valueOf(maxiter.getText()).doubleValue();
	    blclen=(int)Double.valueOf(bloclen.getText()).doubleValue();
	    if (blclen<=10 && blclen>1000){
		dispose();
		errmsg er=new errmsg(parent,"ERROR","Block Length too low or too high)",750,120);
		er.setVisible(true);
		er.setSize(750,120);
	    }
	    else{
		nbloc = Math.round(1000/(float)blclen);
		double[][] sepsrc=new double[2][1000];
		icansng in=new icansng(new double[2][2*(mxiter+1)]);
		double[][] sep=in.calc(obs,2,1000,nbloc,mxiter,sepsrc);
		Plot.mat2(graph9,in.seps,in.iter,mixmat);
		Plot.seri(graph5,sepsrc[0],1000,true,"separated source 1");
		Plot.seri(graph6,sepsrc[1],1000,true,"separated source 2");
		double[] max={sepsrc[0][0],sepsrc[1][0]},
		         min={sepsrc[0][0],sepsrc[1][0]};
		for (int i=1; i<1000; i++) {
		    if (max[0] < sepsrc[0][i]) max[0] = sepsrc[0][i];
		    if (min[0] > sepsrc[0][i]) min[0] = sepsrc[0][i];
		    if (max[1] < sepsrc[1][i]) max[1] = sepsrc[1][i];
		    if (min[1] > sepsrc[1][i]) min[1] = sepsrc[1][i];
		}
		double det=sep[0][0]*sep[1][1] - sep[0][1]*sep[1][0];
		double axes[][]={{sep[1][1]*min[0]/det,-sep[1][0]*min[0]/det,
				  sep[1][1]*max[0]/det,-sep[1][0]*max[0]/det},
				 {-sep[0][1]*min[1]/det,sep[0][0]*min[1]/det,
				  -sep[0][1]*max[1]/det,sep[0][0]*max[1]/det}};
		Plot.scatter(graph7,obs[0],obs[1],1000,axes,
			     "observation 2 vs observation 1");
		Plot.scatter(graph8,sepsrc[0],sepsrc[1],1000,null,
			     "separated source 2 vs separated source 1");
		dispose();
	    }
	}
	else dispose();
    }
}
*/
class nonlin extends Dialog implements ActionListener {
    Frame parent;
    TextField bwt,maxstept, npara, mu_B, mu_z;
    Button ok;
    Graph2D graph5,graph6,graph7,graph8,graph9,graph10,graph0;
    double obs[][],mixmat[][];
    //boolean done[];
    nonlin(Frame parent,String title,Graph2D graph5,Graph2D graph6,
	   Graph2D graph7,Graph2D graph8,Graph2D graph9,Graph2D graph10,
	   Graph2D graph0,double obs[][],double mixmat[][]){
	super(parent,title,false);
	this.parent=parent;
	this.graph5=graph5;
	this.graph6=graph6;
	this.graph7=graph7;
	this.graph8=graph8;
	this.graph9=graph9;
	this.graph10=graph10;
	this.graph0=graph0;
	this.obs=obs;
	this.mixmat=mixmat;

	setSize(650,300);
	setResizable(true);
	setLayout(new GridLayout(0,2,3,3));
	add(new Label("Number of params for the nonlin. transf.)"));
	npara=new TextField("12",10);
	add(npara);
	add(new Label("Maximum number of iterations"));
	maxstept=new TextField("40",10);
	add(maxstept);
	add(new Label("Bandwidth/(opt. val. for Gauss. dens.)"));
	bwt=new TextField("1",10);
	add(bwt);
	add(new Label("Learning step for the linear part"));
	mu_B=new TextField("0.6",10);
	add(mu_B);
	add(new Label("Learning step for the nonlinear part"));
	mu_z=new TextField("0.3",10);
	add(mu_z);
	ok=new Button("ok");
	ok.addActionListener(this);
	add(ok);
	Button cancel=new Button("cancel");
	cancel.addActionListener(this);
	add(cancel);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals("ok")){
	    double bw,muB,muz;
	    int maxstep,np;
	    bw=Double.valueOf(bwt.getText()).doubleValue();
	    np=(int)Double.valueOf(npara.getText()).doubleValue();
	    maxstep=(int)Double.valueOf(maxstept.getText()).doubleValue();
	    muB=Double.valueOf(mu_B.getText()).doubleValue();
	    muz=Double.valueOf(mu_z.getText()).doubleValue();

	    icainfpnl nl=new icainfpnl(new double[2][2*(maxstep+1)]);
	    double[][] y=new double[2][1000], z=new double[2][1000];
	    double[][] sep=nl.calc(obs,2,1000,np,maxstep,bw,2,muB,muz,y,z);
	    Plot.mat2(graph9,nl.Bs,nl.iter,mixmat);
	    Plot.seri(graph5,y[0],1000,true,"separated source 1");
	    Plot.seri(graph6,y[1],1000,true,"separated source 2");
	    double[] max={y[0][0],y[1][0]}, min={y[0][0],y[1][0]};
	    for (int i=1; i<1000; i++) {
		if (max[0] < y[0][i]) max[0] = y[0][i];
		if (min[0] > y[0][i]) min[0] = y[0][i];
		if (max[1] < y[1][i]) max[1] = y[1][i];
		if (min[1] > y[1][i]) min[1] = y[1][i];
	    }
	    double tmp=sep[0][0]*sep[1][1] - sep[0][1]*sep[1][0];
	    double axes[][]={{sep[1][1]*min[0]/tmp,-sep[1][0]*min[0]/tmp,
			      sep[1][1]*max[0]/tmp,-sep[1][0]*max[0]/tmp},
			     {-sep[0][1]*min[1]/tmp,sep[0][0]*min[1]/tmp,
			      -sep[0][1]*max[1]/tmp,sep[0][0]*max[1]/tmp}};
	    Plot.scatter(graph7,obs[0],obs[1],1000,null,
			 "observation 2 vs observation 1");
	    Plot.scatter(graph8,z[0],z[1],1000,axes,
			 "compensated obs 2 vs compensated obs 1");
	    Plot.xy(graph10,obs[2],y[0],1000,"Compensator 1");
	    Plot.xy(graph0,obs[3],y[1],1000,"Compensator 2");
	    //done[3]=true;
	}
	dispose();
    }
}


class helpbliss extends Dialog implements ActionListener{
    Button ok;
    helpbliss(Frame parent,String title){
	super(parent,title,false);
	setSize(850,750);
	setVisible(false);
	setResizable(false);
	setLayout(new BorderLayout());
	ok=new Button("OK");
	ok.addActionListener(this);
	add(ok,BorderLayout.SOUTH);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void paint(Graphics g){
	Dimension d=getSize();
	g.setColor(Color.white);
	g.drawRect(0,0,d.width,d.height);
	g.fillRect(0,0,d.width,d.height);
	g.setColor(Color.red);
	g.setFont(new Font("TimesRoman",Font.BOLD,35));
	g.drawString("BLIND SOURCE SEPARATION",150,110);
	g.setFont(new Font("TimesRoman",Font.PLAIN,17));
	g.setColor(Color.blue);
	g.drawString("This software implements three methods to separate linear mixtures of",90,175);
	g.drawString("sources. To start with the software, choose both sources from the list by clicking",50,200);
	g.drawString("on the 'Select Source'. You are required to fill in the parameters required for each",50,225);
	g.drawString("of the sources.",50,250);

	g.drawString("In case you choose a wav file, a small window will pop-up with the entire wave",90,300);
	g.drawString("file plotted in it. You are to move your mouse in the grey region below the plot and",50,325);
	g.drawString("select the portion of the source. The selected sources are plotted.",50,350);
	g.drawString("After selecting Source 1 and Source 2, you can create the mixture composition ",90,400);
	g.drawString("by selecting 'Create Mixture' and providing parameters for the mixing matrix.",50,425);
	g.drawString("Finally, you choose the algorithm to separate the sources. The global matrix",90,475);
	g.drawString("(product of the separation matrix with the mixing matrix) is then plotted against the",50,500);
	g.drawString("iteration step. The ICA axes are also drawn (in red) in the plot of the observations.",50,525);

	g.drawString("You can also apply a postnonlinear transform of the data before applying",90,575);
	g.drawString("the algorithm. In this case you should choose the \"Mutual Information for Post-",50,600);
	g.drawString("nonlinear mixtures\" method.",50,625);

	g.setFont(new Font("TimesRoman",Font.PLAIN,11));
	g.drawString("For any Query, you will find us at:",330,670);
	g.drawString("Dinh-Tuan.Pham@imag.fr",350,685);
	g.drawString("jiteshis@hotmail.com",360,700);
    }

    public void actionPerformed(ActionEvent ae){
	dispose();
    }
}
