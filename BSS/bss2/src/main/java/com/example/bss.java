package com.example;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import graph.*;
import stat.arma;
import stat.sepagaus;
import stat.icainf;
import stat.icansng;

public class bss extends Applet implements ActionListener{
    MenuFrame f;
    Graph2D[] graph = new Graph2D[9];
    double source1[],source2[],obs[][],mixmat[][];
    boolean done[] = {false,false,false};
    String startText;
    public void init(){
	URL abc;
	abc=getCodeBase();
	source1=new double[1000];
	source2=new double[1000];
	obs=new double[2][1000];
	mixmat=new double[2][2];
	f=new MenuFrame("BLIND SOURCE SEPARATION",graph,abc,source1,source2,
			obs,done,mixmat);
	int width=Integer.parseInt(getParameter("width"));
	int height=Integer.parseInt(getParameter("height"));

	setSize(new Dimension(width,height));
	setLayout(new BorderLayout());
	startText = getParameter("STARTTEXT");
	if (startText == null)
	    startText = "To START the Java demostration click here"; 
	Button start=new Button(startText);
	add(start,BorderLayout.CENTER);
	start.addActionListener(this);

	f.setSize(1020,700);
	f.setLayout(new GridLayout(3,3,10,10));
	for (int i=0; i<9; i++) {
	    graph[i] = new Graph2D();
	    graph[i].borderBottom = 5;
	    graph[i].borderLeft = 5;
	    graph[i].drawgrid = false;
	    graph[i].framecolor=new Color(10,0,1);
	    graph[i].setGraphBackground(new Color(255,255,255));
	}
	graph[8].drawgrid = true;
	f.add(graph[0]);
	f.add(graph[2]);
	f.add(graph[4]);
	f.add(graph[1]);
	f.add(graph[3]);
	f.add(graph[5]);
	f.add(graph[8]);
	f.add(graph[6]);
	f.add(graph[7]);
    }

    public void stop(){
	f.setVisible(false);
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals(startText))
	    f.setVisible(true);
    }
}

class MenuFrame extends Frame{

    Graph2D[] graph;
    URL abc;
    double source1[],source2[],obs[][],mixmat[][];
    boolean done[];
    MenuFrame(String title,Graph2D[] graph,URL abc,double source1[],
	      double source2[],double obs[][],boolean done[],
	      double mixmat[][]) {
	super(title);
	this.graph = graph;
	this.abc=abc;
	this.source1=source1;
	this.source2=source2;
	this.obs=obs;
	this.done=done;
	this.mixmat=mixmat;
	setVisible(false);
	//setSize(1020,780);
	MenuBar mbar=new MenuBar();
	setMenuBar(mbar);
	Menu sorce=new Menu("Select Source");
        MenuItem sorce1,sorce2;
	sorce.add(sorce1=new MenuItem("Source 1"));
	sorce.add(sorce2=new MenuItem("Source 2"));
	mbar.add(sorce);
        Menu observe=new Menu("Create Mixture");
	MenuItem setmatrix;
        observe.add(setmatrix=new MenuItem("Create Mixture"));
        mbar.add(observe);
	observe.setEnabled(false);
        Menu separate=new Menu("Select Algorithm");
        MenuItem algo1,algo2,algo3;
        separate.add(algo1=new MenuItem("Gaussian Mutual Info. Criterion"));
        separate.add(algo2=new MenuItem("Marginal Mutual Info. Criterion"));
        separate.add(algo3=new MenuItem("Nonstationary non Gaussian method"));
	mbar.add(separate);
	separate.setEnabled(false);
        Menu help=new Menu("Help");
        MenuItem helpi,about;
        help.add(helpi=new MenuItem("HELP"));
	help.add(about=new MenuItem("About"));
	mbar.add(help);
	Menu exitt=new Menu("Exit");
	MenuItem exit;
        exitt.add(exit=new MenuItem("Exit"));
	mbar.add(exitt);

	MyMenuHandler handler =
	    new MyMenuHandler(this,graph,abc,source1,source2,
			      obs,done,observe,separate,mixmat);
        sorce1.addActionListener(handler);
	sorce2.addActionListener(handler);
	setmatrix.addActionListener(handler);
	algo1.addActionListener(handler);
	algo2.addActionListener(handler);
	algo3.addActionListener(handler);
	helpi.addActionListener(handler);
	about.addActionListener(handler);
	exit.addActionListener(handler);
	addWindowListener(new MyWindowAdapter(this));
    }
}

class MyWindowAdapter extends WindowAdapter{
    Window window;
    public MyWindowAdapter(Window window){
	this.window=window;
    }

    public void windowClosing(WindowEvent we){
	window.setVisible(false);
    }
}

class MyMenuHandler implements ActionListener, ItemListener {
    MenuFrame menuFrame;
    Graph2D[] graph;
    URL abc;
    double source1[],source2[],obs[][],mixmat[][];
    boolean done[];
    Menu observe,separate;
    public MyMenuHandler(MenuFrame menuFrame,Graph2D[] graph,URL abc,
			 double source1[],double source2[],double obs[][],
			 boolean done[],Menu observe,Menu separate,
			 double mixmat[][]) {
	this.menuFrame=menuFrame;
	this.graph=graph;
	this.abc=abc;
	this.source1=source1;
	this.source2=source2;
	this.obs=obs;
	this.done=done;
	this.observe=observe;
	this.separate=separate;
	this.mixmat=mixmat;
    }

    public void actionPerformed(ActionEvent ae){

	String arg=(String)ae.getActionCommand();

	if (arg.equals("Source 1")){
	    SelectSource selsou =
		new SelectSource(menuFrame,"Select the source",graph[0],abc,0,
				 source1,done,observe);
	    selsou.setVisible(true);
	    //selsou.setSize(250,250);
	}
	if (arg.equals("Source 2")){
	    SelectSource selsou =
		new SelectSource(menuFrame,"Select the source",graph[1],abc,1,
				 source2,done,observe);
	    selsou.setVisible(true);
	    //selsou.setSize(250,250);
	}
	if(arg.equals("Create Mixture")) {
	   if (done[0] & done[1]){
		ObsMix obsmix=new ObsMix(menuFrame,"Mixture Matrix",graph[2],
					 graph[3],abc,source1,source2,obs,
					 done,separate,mixmat);
		obsmix.setVisible(true);
		//obsmix.setSize(200,150);
	    }
	}
	if(arg.equals("Gaussian Mutual Info. Criterion") & done[2]){
	    sepain sp=new sepain(menuFrame,"Gaussian Mut...",graph[4],graph[5],
				 graph[6],graph[7],graph[8],obs,mixmat);
	    sp.setVisible(true);
	    //sp.setSize(525,230);
	}

	if(arg.equals("Marginal Mutual Info. Criterion") & done[2]){
	    incain ic=new incain(menuFrame,"Minimizing Mut...",graph[4],
				 graph[5],graph[6],graph[7],graph[8],
				 obs,mixmat);
	    ic.setVisible(true);
	    //ic.setSize(600,200);
	}
	if (arg.equals("Nonstationary non Gaussian method") & done[2]) {
	    nsngin nsg=new nsngin(menuFrame,"Nonstat. Non Gauss",graph[4],
				  graph[5],graph[6],graph[7],graph[8],
				  obs,mixmat);
	    nsg.setVisible(true);
	}
	if(arg.equals("HELP")){
	    help hp=new help(menuFrame,"HELP");
	    hp.setVisible(true);
	    //hp.setSize(850,750);
	}
	if(arg.equals("About")){
	    about hp=new about(menuFrame,"About");
	    hp.setVisible(true);
	    //hp.setSize(850,600);
	}
	if(arg.equals("Exit")){
	    menuFrame.setVisible(false);
	    //menuFrame.dispose();
	}
    }

    public void itemStateChanged(ItemEvent ie){
    }
}

class SelectSource extends Dialog implements ActionListener{
    List sourcelist;
    Graph2D graph;
    URL abc;
    int num;
    double source[];
    boolean done[];
    Frame parent;
    Menu observe;
    SelectSource(Frame parent,String title,Graph2D graph,URL abc,int num,
		 double source[],boolean done[],Menu observe){
	super(parent,title,false);
	this.graph=graph;
	this.abc=abc;
	this.num=num;
	this.source=source;
	this.done=done;
	this.parent=parent;
	this.observe=observe;
	setLayout(new BorderLayout());
	setResizable(false);
	setSize(250,250);
	sourcelist=new List(6,false);
	sourcelist.add("Lin. combination of 2 sine waves");
	sourcelist.add("Aleluya (WAV)");
	sourcelist.add("Clip1 (WAV)");
	sourcelist.add("GoodMorning (WAV)");
	sourcelist.add("Libertad (WAV)");
	sourcelist.add("ARMA(2,1)");
	sourcelist.select(0);
	add(sourcelist);

	Panel p = new Panel(new GridLayout());
	Button ok=new Button("OK");
	Button cancel=new Button("Cancel");
	p.add(ok);
	p.add(cancel);
	add(p,BorderLayout.SOUTH);

	sourcelist.addActionListener(this);
	ok.addActionListener(this);
	cancel.addActionListener(this);

	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	String st=ae.getActionCommand();
	String str=sourcelist.getSelectedItem();
	if(st.equals("Cancel")){
	    dispose();
	}
	else if(str.equals("Lin. combination of 2 sine waves")){
	    sinin s=new sinin(parent,"Parameters for sinusoid",source,graph,
			      done,num,observe);
	    //s.setSize(700,250);
	    s.setVisible(true);
	    dispose();
	}
	else if(str.equals("ARMA(2,1)")){
	    genarmain gai=new genarmain(parent,"ARMA(2,1)",graph,source,done,
					num,observe);
	    gai.setVisible(true);
	    //gai.setSize(580,350);
	    dispose();
  	}
	else {
	    String filepat=null;
	    if (str.equals("Aleluya (WAV)")) filepat="aleluya.wav";
	    if (str.equals("Clip1 (WAV)")) filepat="clip1.wav";
	    if (str.equals("GoodMorning (WAV)")) filepat="goodmorning.wav";
	    if (str.equals("Libertad (WAV)")) filepat="Libertad.wav";
	    /*if (str.equals("Aleluya (WAV)")) filepat="aleluya.dat";
	    if (str.equals("Clip1 (WAV)")) filepat="clip1.dat";
	    if (str.equals("GoodMorning (WAV)")) filepat="goodmorning.dat";
	    if (str.equals("Libertad (WAV)")) filepat="Libertad.dat";*/

	    int len = 0;
	    double[] data = null;
	    try {
		URL sourceurl = new URL(abc,filepat);
		AudioInputStream as =
		    AudioSystem.getAudioInputStream(sourceurl);
		len = (int)(as.getFrameLength()*as.getFormat().getFrameSize());
		byte[] audioBytes = new byte[len];
		if (as.read(audioBytes) != len) {
		    errmsg erm=new errmsg(parent,"ERROR",
					  "Error reading URL",400,200);
		    erm.setVisible(true);
		}
		// ASSUME DATA IS 8 BITS PCM_UNSIGNED.
		data = new double[len];
		for (int i=0; i<len; i++)
		    data[i] = (((audioBytes[i]+256) & 255) - 128)/128.0;
		/*BufferedReader br= new BufferedReader(new InputStreamReader(sourceurl.openStream()));
		len = Integer.valueOf(br.readLine()).intValue();
		data=new double[len];
		for(int i=0; i < len; i++) {
		    data[i] = Double.valueOf(br.readLine()).doubleValue();
		}
		chosereg cr=
		    new chosereg(parent,"Select the portion of the wav",
				 data,len,num,source,graph,done,observe);
		cr.setVisible(true);*/

	    }
            catch(IOException e){
                errmsg erm = new errmsg(parent,"ERROR",
					"Error reading URL or audio file",
                                        450,150);
                erm.setVisible(true);
            }
            catch(UnsupportedAudioFileException e) {
                errmsg erm = new errmsg(parent,"ERROR",
                                        "Unsupported audio file",350,150);
                erm.setVisible(true);
            }
	    dispose();
	}
    }
}

class ObsMix extends Dialog implements ActionListener{
    Graph2D graph3,graph4;
    URL abc;
    TextField f2,f3;
    Button accept,random;
    double source1[],source2[],obs[][],mixmat[][];
    boolean done[];

    Menu separate;
    ObsMix(Frame parent,String title,Graph2D graph3,Graph2D graph4,URL abc,
	   double source1[],double source2[],double obs[][],boolean done[],
	   Menu separate,double mixmat[][]){
	super(parent,title,false);
	this.graph3=graph3;
	this.graph4=graph4;
	this.abc=abc;
	this.source1=source1;
	this.source2=source2;
	this.obs=obs;
	this.done=done;
	this.separate=separate;
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
		obs[0][i]= source1[i] + nf2*source2[i];
		obs[1][i]= source2[i] + nf3*source1[i];
	    }
	    mixmat[0][0] = 1;
	    mixmat[0][1] = nf2;
	    mixmat[1][0] = nf3;
	    mixmat[1][1] = 1;
	    done[2]=true;
	    separate.setEnabled(true);
	    Plot.seri(graph3,obs[0],1000,true,"observation 1");
	    Plot.seri(graph4,obs[1],1000,true,"observation 2");
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

class chosereg extends Dialog{
    final int WIDTH = 710, LEFT=20, RIGHT=35, HEIGHT = 600;
    int mx, len;
    String msg="";

    chosereg(Frame parent,String title,double data[],int len,int num,
	     double source[],Graph2D graph,boolean done[],Menu observe){
	super(parent,title,false);
	this.len=len;
	setSize(WIDTH+LEFT+RIGHT,HEIGHT);
	setResizable(false);
	setLayout(new GridLayout(2,1,0,0));
	Graph2D graph12=new Graph2D();
      	add(graph12);
	graph12.borderBottom = 0;
	graph12.borderLeft = LEFT;
	graph12.borderRight = RIGHT;
        graph12.setGraphBackground(new Color(255,255,230));
	Plot.seri(graph12,data,len,false,"Choose the region of wav");
	addMouseListener(new MyMouseAdapter(this,graph,source,data,num,done,
					    observe));
	addWindowListener(new MyWindowAdapter(this));
	addMouseMotionListener(new mml(this));
	repaint();
    }

    public void paint(Graphics g) {
	g.setFont(new Font("TimesRoman",Font.BOLD,14));
	g.drawString("Move the mouse in the grey region to change selection",
		     10,HEIGHT/2+40);
	g.drawString(msg,10,HEIGHT/2+60);
	g.setColor(Color.red);
	g.drawLine(mx,0,mx,HEIGHT);
	g.drawLine(mx+1000*WIDTH/len,0,mx+1000*WIDTH/len,HEIGHT);
    }
}

class MyMouseAdapter extends MouseAdapter{
    chosereg cr;
    Graph2D graph;
    double[] source, data;
    int num;
    boolean[] done;
    Menu observe;
    public MyMouseAdapter(chosereg cr,Graph2D graph,double[] source,
			  double[] data,int num,boolean done[],Menu observe){
	this.cr=cr;
	this.done=done;
	this.num=num;
	this.source=source;
	this.data=data;
	this.graph=graph;
	this.observe=observe;
    }

    public void mousePressed(MouseEvent me){
	cr.mx = me.getX();
	int start = (cr.mx-cr.LEFT)*cr.len/cr.WIDTH;
	if (start >= 0 & start <= cr.len-1000) {
	    int i;
	    double scale;
	    cr.setVisible(false);
	    done[num]=true;
	    if (done[0] & done[1]) observe.setEnabled(true);
	    for (i=0, scale=0; i<1000; i++) {
		source[i]= data[start+i];
		scale += source[i]*source[i];
	    }
	    scale = Math.sqrt(scale/1000);
	    for (i=0; i<1000; i++)
		source[i] /= scale;
	    Plot.seri(graph,source,1000,true,"source "+(num+1));
	}
	else cr.repaint();
  }
}

class mml extends MouseMotionAdapter{
    chosereg cr;
    public mml(chosereg cr){
	this.cr=cr;
    }

    public void mouseMoved(MouseEvent me){
	cr.mx=me.getX();
	int start = (cr.mx-cr.LEFT)*cr.len/cr.WIDTH;
	if (start >= 0 & start <= cr.len-1000) {
	    cr.msg="Selection from "+start+" to "+(start+999)+
		": click to confirm";
	    cr.repaint();
	}
   }
}

class genarmain extends Dialog implements ActionListener{
    Graph2D graph;
    Button ok,cancel;
    TextField a1=new TextField(10),a2=new TextField(10),b=new TextField(10);
    double source[];
    boolean done[];
    int num;
    Frame parent;
    Menu observe;
    genarmain(Frame parent,String title,Graph2D graph,double source[],
	      boolean done[],int num,Menu observe){
	super(parent,title,false);
	this.graph=graph;
	this.source=source;
	this.done=done;
	this.parent=parent;
	this.num=num;
	this.observe=observe;
	setSize(580,350);
	setResizable(true);
	setLayout(new GridLayout(7,2,3,3));
        ok=new Button("ok");
	cancel=new Button("Cancel");
	add(new Label("The equation to ARMA(2,1) is"));
	add(new Label("x(t)=a1x(t-1)+a2x(t-2)+e(t)+be(t-1)"));
	add(new Label(""));
	add(new Label("where e(t) is a white noise"));
	add(new Label("The conditions on a1 and a2 is"));
	add(new Label("|a2| < 1 and -1 < a1 < 1-|a2|"));
	add(new Label("a1 ="));
	a1.setText("1.4");
	add(a1);
	add(new Label("a2 ="));
	a2.setText("-0.9");
	add(a2);
	add(new Label("b ="));
	b.setText("0.2");
	add(b);

	add(ok);
	add(cancel);
	ok.addActionListener(this);
	cancel.addActionListener(this);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals("ok")){
	    double a[]={1.8,-.9},bt=0.2;
	    a[0]=Double.valueOf(a1.getText()).doubleValue();
	    a[1]=Double.valueOf(a2.getText()).doubleValue();
	    double v,r,ma[]={Double.valueOf(b.getText()).doubleValue()};
	    r = a[0]/(1-a[1]);
	    v=(1-r*r)*(1-a[1]*a[1])/(1+ma[0]*ma[0]+2*r*ma[0]);
	    boolean good=arma.gene(a,2,ma,1,v,source,1000);
	    if (good) {
		done[num]=true;
		if (done[0] & done[1]) observe.setEnabled(true);
		Plot.seri(graph,source,1000,true,"source "+(num+1));
		dispose();
	    } else {
		dispose();
		errmsg erm=new errmsg(parent,"ERROR","ERROR",200,200);
		erm.setVisible(true);
		erm.setSize(200,200);
	    }
	}
	else dispose();
    }
}

class errmsg extends Dialog implements ActionListener{
    errmsg(Frame parent,String title,String msg,int x,int y){
	super(parent,title,true);
	setSize(x,y);
	//setLayout(new FlowLayout ());
	add("Center",new Label(msg,Label.CENTER));
	Button ok=new Button("ok");
	add("South",ok);
	ok.addActionListener(this);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	dispose();
    }
}

class sepain extends Dialog implements ActionListener{
    Frame parent;
    TextField nfreq,bloclen,maxiter;
    Button ok,cancel;
    Graph2D graph5,graph6,graph7,graph8,graph9;
    double obs[][],source1[],source2[],mixmat[][];
    sepain(Frame parent,String title, Graph2D graph5,Graph2D graph6,
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

	setSize(525,230);
	setResizable(true);
	setLayout(new GridLayout(4,2,3,3));
	add(new Label("Number of Frequency channels"));
	nfreq=new TextField(3);
	nfreq.setText("10");
	add(nfreq);
	add(new Label("Block Length"));
	bloclen=new TextField(5);
	bloclen.setText("50");
	add(bloclen);
	add(new Label("Maximum number of iterations"));
	maxiter=new TextField(3);
	maxiter.setText("15");
	add(maxiter);
	ok=new Button("ok");
	ok.addActionListener(this);
	add(ok);
	cancel=new Button("cancel");
	cancel.addActionListener(this);
	add(cancel);
	addWindowListener(new MyWindowAdapter(this));
    }

    public void actionPerformed(ActionEvent ae){
	if(ae.getActionCommand().equals("ok")){
	    int nfr,mxiter,blclen;
	    nfr=(int)Double.valueOf(nfreq.getText()).doubleValue();
	    mxiter=(int)Double.valueOf(maxiter.getText()).doubleValue();
	    blclen=(int)Double.valueOf(bloclen.getText()).doubleValue();
	    if(blclen<=2*nfr){
		dispose();
		errmsg er=new errmsg(parent,"ERROR","Block Length too low (must be greater than twice the number of frequency channels)",750,120);
		er.setVisible(true);
		er.setSize(750,120);
	    }
	    else if(blclen>1000){
		dispose();
		errmsg er=new errmsg(parent,"ERROR","Block length cannot exceed 1000 (DATA LENGTH)",550,120);
		er.setVisible(true);
		er.setSize(550,120);
	    }
	    else{
		sepagaus sp=new sepagaus(new double[2][2*(mxiter+1)]);
		double[][] sep=sp.calc(obs,2,1000,nfr,blclen,mxiter);
		double[] sepsrc1=new double[1000],sepsrc2=new double[1000];
		int i;
		for(i=0;i<1000;i++){
		    sepsrc1[i] = sep[0][0]*obs[0][i]+sep[0][1]*obs[1][i];
		    sepsrc2[i] = sep[1][0]*obs[0][i]+sep[1][1]*obs[1][i];
		}
		Plot.mat2(graph9,sp.seps,sp.iter,mixmat);
		Plot.seri(graph5,sepsrc1,1000,true,"separated source 1");
		Plot.seri(graph6,sepsrc2,1000,true,"separated source 2");
		double[] max={sepsrc1[0],sepsrc2[0]},
		         min={sepsrc1[0],sepsrc2[0]};
		for (i=1; i<1000; i++) {
		    if (max[0] < sepsrc1[i]) max[0] = sepsrc1[i];
		    if (min[0] > sepsrc1[i]) min[0] = sepsrc1[i];
		    if (max[1] < sepsrc2[i]) max[1] = sepsrc2[i];
		    if (min[1] > sepsrc2[i]) min[1] = sepsrc2[i];
		}
		double det=sep[0][0]*sep[1][1] - sep[0][1]*sep[1][0];
		double axes[][]={{sep[1][1]*min[0]/det,-sep[1][0]*min[0]/det,
				  sep[1][1]*max[0]/det,-sep[1][0]*max[0]/det},
				 {-sep[0][1]*min[1]/det,sep[0][0]*min[1]/det,
				  -sep[0][1]*max[1]/det,sep[0][0]*max[1]/det}};
		Plot.scatter(graph7,obs[0],obs[1],1000,axes,
			     "observation 2 vs observation 1");
		Plot.scatter(graph8,sepsrc1,sepsrc2,1000,null,
			     "separated source 2 vs separated source 1");
		dispose();
	    }
	}
	else dispose();
    }
}

class incain extends Dialog implements ActionListener{
    Frame parent;
    TextField bwt,maxstept;
    Button ok;
    Graph2D graph5,graph6,graph7,graph8,graph9;
    double obs[][],mixmat[][];
    incain(Frame parent,String title,Graph2D graph5,Graph2D graph6,
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

	setSize(600,200);
	setResizable(true);
	setLayout(new GridLayout(3,2,3,3));
	add(new Label("Bandwidth/(opt. val. for Gauss. dens.)"));
	bwt=new TextField(10);
	bwt.setText("1");
	add(bwt);
	add(new Label("Maximum number of iterations"));
	maxstept=new TextField(3);
	maxstept.setText("15");
	add(maxstept);
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
	    double bw;
	    int maxstep;
	    bw=Double.valueOf(bwt.getText()).doubleValue();
	    maxstep=(int)Double.valueOf(maxstept.getText()).doubleValue();
	    icainf ic=new icainf(new double[2][2*(maxstep+1)]);
	    double[][] sepsrc=new double[2][1000];
	    double[][] sep=ic.calc(obs,2,1000,maxstep,bw,2,sepsrc);
	    Plot.mat2(graph9,ic.seps,ic.iter,mixmat);
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
	    double tmp=sep[0][0]*sep[1][1] - sep[0][1]*sep[1][0];
	    double axes[][]={{sep[1][1]*min[0]/tmp,-sep[1][0]*min[0]/tmp,
			      sep[1][1]*max[0]/tmp,-sep[1][0]*max[0]/tmp},
			     {-sep[0][1]*min[1]/tmp,sep[0][0]*min[1]/tmp,
			      -sep[0][1]*max[1]/tmp,sep[0][0]*max[1]/tmp}};
	    Plot.scatter(graph7,obs[0],obs[1],1000,axes,
			 "observation 2 vs observation 1");
	    Plot.scatter(graph8,sepsrc[0],sepsrc[1],1000,null,
			 "separated source 2 vs separated source 1");
	}
	dispose();
    }
}

class sinin extends Dialog implements ActionListener{
    Frame parent;
    TextField bt,f1t,f2t;
    Button ok;
    Graph2D graph;
    double source[];
    boolean done[];
    int num;
    Menu observe;
    sinin(Frame parent,String title,double source[],Graph2D graph,
	  boolean done[],int num,Menu observe){
	super(parent,title,false);
	this.graph=graph;
	this.source=source;
	this.num=num;
	this.done=done;
	this.observe=observe;
	setLayout(new GridLayout(4,2,3,3));
	setSize(600,250);
	add(new Label("Frequency of 1st sine (per 1000 samples)"));
	f1t=new TextField(5);
	f1t.setText("9");
	add(f1t);
	add(new Label("Frequency of 2nd sine (per 1000 samples)"));
	f2t=new TextField(5);
	f2t.setText("10");
	add(f2t);
	add(new Label("Amplitude of 2nd sine (relative to the 1st)"));
	bt=new TextField(6);
	bt.setText("1");
	add(bt);
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
	  double b,f1,f2,phase1,phase2, scale;
	  f1=Double.valueOf(f1t.getText()).doubleValue();
	  f2=Double.valueOf(f2t.getText()).doubleValue();
	  b=Double.valueOf(bt.getText()).doubleValue();
	  phase1=Math.random();
	  phase2=Math.random();
	  scale = Math.sqrt((1 + b*b)/2);
	  for(int i=0; i<1000; i++)
	      source[i] = (Math.sin(2*Math.PI*f1*i/1000+phase1) +
			   b*Math.cos(2*Math.PI*f2*i/1000+phase2))/scale;
	  done[num]=true;
	  if (done[0] & done[1]) observe.setEnabled(true);
	  Plot.seri(graph,source,1000,true,"source "+(num+1));
       }
      dispose();
    }
}

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
	add(new Label("Stationary block Length"));
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

class help extends Dialog implements ActionListener{
    Button ok;
    help(Frame parent,String title){
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
	g.drawString("Finally, you choose the algorithm for the separation of the sources and thus the",90,475);
	g.drawString("sources are separated.",50,500);

	g.drawString("The global matrix (product of the separation matrix with the mixing matrix) is",90,550);
	g.drawString("then plotted against the iteration step. The ICA axes are also drawn (in red) in the",50,575);
	g.drawString("plot of the observations.",50,600);

	g.setFont(new Font("TimesRoman",Font.PLAIN,11));
	g.drawString("For any Query, you will find us at:",330,670);
	g.drawString("Dinh-Tuan.Pham@imag.fr",350,685);
	g.drawString("jiteshis@hotmail.com",360,700);
    }

    public void actionPerformed(ActionEvent ae){
	dispose();
    }
}

class about extends Dialog implements ActionListener{
    Button ok;
    about(Frame parent,String title){
	super(parent,title,false);
	setSize(850,600);
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
	g.drawString("This is the demostration of project SASI & BLISS that implements the BLIND",90,175);
	g.drawString("SOURCE SEPARATION. The project was done at the LMC IMAG Laboratory under the",50,200);
	g.drawString("guidance of Prof. Pham Dinh-Tuan.",50,225);
	g.drawString("The project was assisted by Jitesh Shah, Indian Institute of Technology,",90,300);
	g.drawString("Guwahati (INDIA).",50,325);
	g.drawString("The project was completed on 24th July, 2002.",200,350);

	g.setFont(new Font("TimesRoman",Font.PLAIN,11));
	g.drawString("For any Query or feedback contact us at:",300,420);
	g.drawString("IMAG - LMC",390,450);
	g.drawString("B. P. 53",400,470);
	g.drawString("8041 Grenoble Cedex 9",358,490);

	g.drawString("Dinh-Tuan.Pham@imag.fr",350,530);
	g.drawString("jiteshis@hotmail.com",360,550);
    }

    public void actionPerformed(ActionEvent ae){
	dispose();
    }
}
