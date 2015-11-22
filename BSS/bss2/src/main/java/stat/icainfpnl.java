package stat;

public class icainfpnl{

    public double Bs[][];
    public int iter;
    boolean save=false;

    public icainfpnl(double Bs[][]){
	this.Bs = Bs;
	this.save = true;
    }
    public icainfpnl(){
    	this.save = false;
    }

    public double[][] calc(double x[][],int K,int n,int np, int maxiter,
			   double bdwidth, int nb, double mu_B, double mu_z,
			   double y[][], double z[][]){
        final double ALF=1e-4,TOLX2=1e-6/n;
	// np = 10; maxiter=40;
	// bdwidth = 1; mu_B = .6; mu_z = .3;

        int i,j,k,i0;
        double tmp,bin = bdwidth*(2.107683/Math.exp(Math.log(n)/5))/nb;

	//The interpolation matrix
	
	double interp[][] = new double[np][n];
	for (k=0; k<np; k++) for (i=0; i < n; i++)
	    interp[k][i] = 0;
	i0 = 0;
	for (k = 1; k < np; k++) {
	    i = Math.round((float)k*(n-1)/(np-1));
	    for (j=i0; j<i; j++) {
		interp[k-1][j] = (double)(i-j)/(i-i0);
		interp[k][j+1] = (double)(j+1-i0)/(i-i0);
	    }
	    i0 = i;
	}

	// Initialisation
	int rx[][] = new int[K][];
	for (k=0; k<K; k++)
	    rx[k] = array.rank(x[k],n);

	double[][] p0 = new double[K][np], p = new double[K][n];
	for (i=0; i < np; i++) {
	    i0 = Math.round((float)i*(n-1)/(np-1));
	    tmp = specfun.erfinv((double)(2*i0+1)/n - 1);
	    p0[0][i] = Math.exp(-tmp*tmp)/Math.sqrt(2*Math.PI);
	}
	for (i=0; i < n; i++) {
	    for (tmp=0,j=0; j<np; j++) tmp+= p0[0][j]*interp[j][i];
	    p[0][i] = tmp;
	}
	double[][] lambda = new double[K][n-1];
	double entz = densquanti(p[0],n,z[0],lambda[0]);

	for (k=1; k<K; k++) {	//duplicate les rows of p0, p, z and lambda
	    for (i=0; i<np; i++)
		p0[k][i] = p0[0][i];
	    for (i=0; i<n; i++) {
		p[k][i] = p[0][i];
		z[k][i] = z[0][i];
	    }
	    for (i=0; i<n-1; i++)
		lambda[k][i] = lambda[0][i];
	}

	for (k=0; k<K; k++) for (i=0; i<n; i++)
	    y[k][i] = z[k][rx[k][i]];
        double B[][]= Matrix.eye(K);	//B=I -> no initial transformation

	// Inital estimation of the covariance matrix of Y, of its
	// entropies and score functions and of the criterion
 
	double cv[][] = new double[K][K], psiy[][] = new double[K][n], enty;
	array.covc(y,K,n,cv);
	for (enty=0,k=0; k<K; k++)
            enty += kentropy(y[k],n,bin*Math.sqrt(cv[k][k]),nb,psiy[k]);
	double crit0 = enty - K*entz - Math.log(Math.abs(Matrix.det(B,K)));

System.out.println("cv=["+cv[0][0]+" "+cv[0][1]+";"+cv[1][0]+" "+cv[1][1]+"]");
System.out.println("entz="+entz+" det= "+Matrix.det(B,K));
System.out.println(" 0 Criterion "+crit0);

	// Iteration loop

        if (save) {
            for (i=0; i < K; i++) for (j=0; j < K; j++)
                Bs[i][j] = B[i][j];
        }
        double G[][]=new double[K][K], info[]=new double[K],
               omegaij, omegaji, g[][]=new double[K][K];
	for (i=0; i<K; i++) g[i][i] = 0;	//diagonal of g should always 0
        double Bn[][]=new double[K][K], p0n[][]=new double[K][np],
     	       T[][]=new double[np][n-1], TT[][]=new double[np][np],
	       dp0[][]=new double[K][np], d[]=new double[np], decr, ent,
	       slope,crit,critn,alamin,alam,alam2=0,
               tmplam=0,crit2=0,a,b,rhs1,rhs2,disc;

	for(iter=1; iter<=maxiter; iter++) {

	    // Linear part of the gradient
            for (k=0; k<K; k++){
                for (j=0; j<K; j++){
                    for (tmp=0,i=0; i<n; i++)
                        tmp += psiy[j][i]*y[k][i];
                    G[j][k] = tmp/n;
                }
		array.center(psiy[k],n,psiy[k]);	//correction to psi
		for (i=0; i<n; i++) {
		    psiy[k][i] += y[k][i]*(1-G[k][k])/cv[k][k];
		}
                for (tmp=0,i=0; i<n; i++)
                    tmp += psiy[k][i]*psiy[k][i];
                info[k] = tmp/n;
	    }
	    //diagonal elements of G not needed (since diag(G)=0)
            for (i=0; i<K; i++) for(j=0; j<i; j++) {
                G[i][j] += (1-G[i][i])*cv[i][j]/cv[i][i];
                G[j][i] += (1-G[j][j])*cv[j][i]/cv[j][j];
		omegaij=info[i]*cv[j][j];
		omegaji=info[j]*cv[i][i];
		tmp = omegaij*omegaji - 1;
		g[i][j] = (omegaji*G[i][j] - G[j][i])/tmp;
		g[j][i] = (omegaij*G[j][i] - G[i][j])/tmp;
	    }

	    // Non linear part of the gradient

	    Matrix.mult(B,K,psiy,n);		//change psiy to B*psiy
	    for (k=0; k<K; k++) {	//compute "dz" which overwrites z
		ipermute(psiy[k],n,rx[k]);
		for (tmp=0,i=0; i<n-1; i++) {
		    tmp += psiy[k][i];
		    z[k][i] = tmp*(z[k][i]-z[k][i+1]);
		}
		array.center(z[k],n-1,z[k]);	//centering (z of length n-1)
	    }

	    for (decr=0,k=0; k<K; k++) {
		for (i=0; i<np; i++) for (j=0; j<n-1; j++)
		    T[i][j] = interp[i][j]*lambda[k][j]/p[k][j] +
			      interp[i][j+1]*(1-lambda[k][j])/p[k][j+1];
		array.covc(T,np,n-1,TT);	//extra divison by n-1 here
		for (i=0; i<np; i++) {
		    for (tmp=0,j=0; j<n-1; j++)
			tmp += T[i][j]*z[k][j];
		    dp0[k][i] = tmp/(n-1);	//compensate the above division
		    d[i] = tmp;
		}
		cholsolve(TT,np,dp0[k]);
		for (i=0; i<np; i++)
		    decr += d[i]*dp0[k][i];
	    }

	    for (slope=0,i=0; i<K; i++) for(j=0; j<i; j++)
		slope += G[i][j]*g[i][j] + G[j][i]*g[j][i];
	    slope = -(mu_B*slope + mu_z*decr);

	    System.out.println("decr= "+decr+" slope= "+slope);

	    alamin=Math.sqrt(TOLX2*K*(K-1)/(-slope));

	    // Line search and backtrack
	    alam=1;
	    for ( ; ; ) {
		if (alam<alamin) {	//convergent or step becomes too small
		    for (k=0; k<K; k++)
			permute(z[k],n,rx[k]);
		    return B;
		}
		//new source and the criterion
		for(i=0; i<K; i++) for (j=0; j<K; j++) {
		    for (tmp=0,k=0; k<K; k++)
			tmp += g[i][k]*B[k][j];
		    Bn[i][j] = B[i][j] - (alam*mu_B)*tmp;
		}
		for (tmp=alam*mu_z,k=0; k<K; k++) for (i=0; i<np; i++)
		    p0n[k][i] = p0[k][i]*Math.exp(tmp*dp0[k][i]/p0[k][i]);
		Matrix.mult(p0n,K,interp,np,p,n);
		//compute the quantile z from p, the auxilary array lambda
		//and the entropy, then rescale z
		for (k=0; k<K; k++) {
		    ent = densquanti(p[k],n,z[k],lambda[k]);
		    for (i=0; i<n; i++)
			z[k][i] *= Math.exp(entz-ent);	// rescale z
		    for (i=0; i<n; i++)
			y[k][i] = z[k][rx[k][i]];	//to reconst. sources
		}
		Matrix.mult(Bn,K,y,n);
		array.covc(y,K,n,cv);
		for (enty=0,k=0; k<K; k++)
		    enty+=kentropy(y[k],n,bin*Math.sqrt(cv[k][k]),nb,psiy[k]);
		crit = enty - K*entz - Math.log(Math.abs(Matrix.det(Bn,K)));

		if (crit <= crit0+(ALF*alam*slope)) {
		    //sufficient decrease: JUMP OUT OF LINE SEARCH here

System.out.println(iter+" Criterion "+crit);

		    crit0 = crit;
		    for (k=0; k<K; k++) {
			for (i=0; i<np; i++)
			    p0[k][i] = p0n[k][i];
			for (i=0; i<K; i++)
			    B[k][i] = Bn[k][i];
		    }
		    if (save)
			for (j=0; j<K; j++) for (i=0; i<K; i++)
			    Bs[i][K*iter+j] = B[i][j];
		    break;
		}
		else {
		    if (alam==1)				//1st time
			tmplam = -slope/(2*(crit-crit0-slope));
		    else {
			rhs1=crit-crit0-alam*slope;
			rhs2=crit2-crit0-alam2*slope;
			a=((rhs1/(alam*alam))-(rhs2/(alam2*alam2)))
			    /(alam-alam2);
			b=((-alam2*rhs1/(alam*alam))+(alam*rhs2/(alam2*alam2)))
			    /(alam-alam2);
			if (a==0) tmplam = -slope/(2*b);
			else {
			    disc=b*b-3*a*slope;
			    if (disc<0) {
			        System.out.println("Roundoff problem in lnsrch.");
			        System.exit(1);
			    }
			    tmplam=(-b+Math.sqrt(disc))/(3*a);
			    if (tmplam>.5*alam) tmplam=.5*alam;
			}
		    }
		    alam2=alam;
		    crit2=crit;
		    alam=Math.max(tmplam,0.1*alam);

System.out.println("crit "+crit+" -> alam "+alam);

		}	//end if (crit <= ...) else ...
	    }		//end for ( ; ; )
	}		//end for (iter= ...)
	for (k=0; k<K; k++) {
	    for (i=0; i<n; i++)
		z[k][i] = z[k][rx[k][i]];
	}
	return B;
    }

    static double kentropy(double source[],int n,double bin,int nb,
			   double psi[]) {
	int i,j,m,lenf;
	double r[] = new double[n];
	int index[] = new int[n];
	double ent, d, tmp;
	for (i=0;i<n;i++) {
	    r[i] = source[i]/bin;
	    index[i] = (int)Math.floor(r[i]);
	    r[i] -= index[i];
	}
	m = index[0];
	lenf = m;
	for(i=0; i<n; i++) {
	    if (m > index[i]) m = index[i];
	    if (lenf < index[i]) lenf = index[i];
	}
	lenf += 3*nb-m;
	for (i=0;i<n;i++) index[i] -= m;
	double f[] = new double [lenf];
	for (i=0; i<lenf; i++) f[i]=0;
	for (i=0; i<n; i++) {
	    f[index[i]]   += (1-r[i])*(1-r[i]);
	    f[index[i]+1] += (1 + 2*r[i]*(1-r[i]));
	    f[index[i]+2] += r[i]*r[i];
	}
	for (i=lenf-2*nb+1; i >= nb-1; i--)
	    for (j=i-1; j>i-nb; j--) f[i] += f[j];
	for (; i>0; i--) for (j=i-1; j>=0; j--) f[i] += f[j];
	for (i=lenf-nb; i>=nb-1; i--)
	    for (j=i-1; j>i-nb; j--) f[i] += f[j];
	for (; i>0; i--) for (j=i-1; j>=0; j--) f[i] += f[j];
	for (i=lenf-1; i>nb-1; i--)
	    for (j=i-1; j>i-nb; j--) f[i] += f[j];
	for (; i>0; i--) for (j=i-1; j>=0; j--) f[i] += f[j];
	for (d=n*2*nb*nb*nb,i=0; i<lenf; i++) f[i] /= d;
	for (d=nb*nb*nb*bin,ent=Math.log(bin),i=0; i<lenf; i++)
	    if (f[i] > 0) {
		tmp = Math.log(f[i]);
		ent -= f[i]*tmp;
		f[i] = tmp/d;
	    }
	for (i=0; i<=lenf-nb; i++)
	    for (j=i+1; j<i+nb; j++) f[i] += f[j];
	for (i=0; i<=lenf-2*nb+1; i++)
	    for (j=i+1; j<i+nb; j++) f[i] += f[j];
	for (i=0; i<=lenf-3*nb+2; i++)
	    for (j=i+1; j<i+nb; j++) f[i] += f[j];
	
    	for(i=0; i<n; i++)
	    psi[i] = (1-r[i])*f[index[i]] + (2*r[i]-1)*f[(index[i]+1)] -
	  	     r[i]*f[index[i]+2];
	return ent;
    }

    static double densquanti(double p[],int n,double z[],double lambda[]) {
	/* Contruct from a set of n density-quantile p, the quantile z such
	   that z[i+1]-z[i] = log(p[i+1]/p[i])/(n*(p[i+1]-p[i]))
	   and z being centered. Further, contruct the derivative
	   lambda[i] = 1/log(p[i+1]/p[i]) - 1/(p[i+1]/p[i]-1) */
	final double EULER = .5772156649;
	int i;
	double r, logr, spacing, ent;
	for (ent=0,z[0]=0,i=0; i<n-1; i++) {
	    r = p[i+1]/p[i];
	    if (Math.abs(r-1) < 1e-5) {
		spacing = 1/(n*p[i]);
		lambda[i] = 0.5;
	    }
	    else {
		logr = Math.log(r);
		spacing = logr/(n*(p[i+1]-p[i]));
		lambda[i] = 1/logr - 1/(r-1);
	    }
	    z[i+1] = z[i] + spacing;
	    ent += Math.log(spacing);
	}
	for (r=0,i=1; i<n; i++) r += z[i];
	r /= n;
	for (i=0; i<n; i++) z[i] -= r;
	return ent/(n-1) + Math.log(n-1) + EULER;
    }

    static void cholsolve(double[][] a, int n, double b[]){
	double p[] = new double[n];
	Matrix.choldc(a,n,p);
	Matrix.cholsl(a,n,p,b,b);
    }

    static void permute(double z[], int n, int r[]) {
	double tmp[] = new double[n];
        int i;
	for (i=0; i<n; i++) tmp[i] = z[i];
	for (i=0; i<n; i++) z[i] = tmp[r[i]];
    }

    static void ipermute(double z[], int n, int r[]) {
	double tmp[] = new double[n];
        int i;
	for (i=0; i<n; i++) tmp[i] = z[i];
	for (i=0; i<n; i++) z[r[i]] = tmp[i];
    }
}
