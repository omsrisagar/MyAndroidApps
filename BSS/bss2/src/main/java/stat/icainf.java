package stat;

public class icainf{

    public double seps[][];
    public int iter;
    boolean save=false;

    public icainf(double seps[][]){
	this.seps = seps;
	this.save = true;
    }
    public icainf(){
    	this.save = false;
    }

    public double[][] calc(double data[][],int K,int n,int maxiter,
			   double bdwidth, int nb, double source[][]){
 	final double ALF=0.0001,TOLX2=0.0001/n;
	double bin = bdwidth*(2.107683/Math.exp(Math.log(n)/5))/nb;
	int i,j,k;
	double tmp;

	//Compute the initial transformation and the criterion

	for (k=0; k<K; k++)
	    array.center(data[k],n,source[k]);
	// take sep the identity matrix => no multiplication by seps
	double sep[][]=Matrix.eye(K);
	double cova[][]=new double[K][K];
	array.covc(source,K,n,cova);			//for use later
	double psi[][]=new double[K][n], Ent;
	for (Ent=0,k=0; k<K; k++)
	    Ent += kentropy(source[k],n,bin*Math.sqrt(cova[k][k]),nb,psi[k]);

	// Iteration loop

	if (save)
	    for (i=0; i < K; i++) for (j=0; j < K; j++)
		seps[i][j] = sep[i][j];

	double grad[][]=new double[K][K], info[]=new double[K],
	       omegaij, omegaji, newton[][]=new double[K][K];
	//diagonal of newton should be always zero
	for (i=0; i<K; i++) newton[i][i]=0;	//should always be 0
	double u[][]=new double[K][K],cv[][]=new double[K][K],
	       s[][]=new double[K][n],
	       slope,EntN,crit,alamin,alam,alam2=0,
	       tmplam=0,crit2=0,a,b,rhs1,rhs2,disc;

	for (iter=1; iter<=maxiter; iter++) {

	    // compute gradient and the Newton step
	    for (k=0; k<K; k++){
		for (j=0; j<K; j++){
		    for (tmp=0,i=0; i<n; i++)
			tmp += psi[j][i]*source[k][i];
		    grad[j][k] = tmp/n;
		}
		for (tmp=0,i=0; i<n; i++)
		    tmp += psi[k][i];
		for (tmp=-tmp*tmp/n,i=0; i<n; i++)
		    tmp += psi[k][i]*psi[k][i];
		info[k] = tmp/n + (1-grad[k][k]*grad[k][k])/cova[k][k];
	    }
	    //diagonal elements of grad not needed (since diag(grad)=0)
	    for (i=0; i<K; i++) for(j=0; j<i; j++) {
		grad[i][j] += (1-grad[i][i])*cova[i][j]/cova[i][i];
		grad[j][i] += (1-grad[j][j])*cova[j][i]/cova[j][j];
		omegaij = info[i]*cova[j][j];
		omegaji = info[j]*cova[i][i];
		tmp = omegaij*omegaji - 1;
		newton[i][j] = -(omegaji*grad[i][j] - grad[j][i])/tmp;
		newton[j][i] = -(omegaij*grad[j][i] - grad[i][j])/tmp;
	    }
	    for (slope=0,i=0; i<K; i++) for(j=0; j<i; j++)
		slope += grad[i][j]*newton[i][j] + grad[j][i]*newton[j][i];
	    alamin=Math.sqrt(TOLX2*K*(K-1)/(-slope));

	    // Line search and backtrack
	    alam=1;
	    for ( ; ; ) {
		if(alam<alamin)		//convergent or step becomes too small
		    return sep;
		//new source and its entropy
		for(i=0; i<K; i++){
		    for (j=0; j<K; j++)
			u[i][j] = alam*newton[i][j];
		    u[i][i] += 1;
		}
		Matrix.mult(u,K,source,K,s,n);
		for (i=0; i<K; i++) for (j=0; j<K; j++)	{	//cv = cova*u'
		    for (tmp=0,k=0; k<K; k++)
			tmp += cova[i][k]*u[j][k];
		    cv[i][j] = tmp;
		}
		Matrix.mult(u,K,cv,K);			//cv <- u*cv
		for (EntN=0,k=0; k<K; k++)
		    EntN += kentropy(s[k],n,bin*Math.sqrt(cv[k][k]),nb,psi[k]);
		crit = EntN - Math.log(Math.abs(Matrix.det(u,K)));
		if (crit <= Ent+(ALF*alam*slope)) {
		    //sufficient decrease: we jump out of the loop here
		    Ent = EntN;
		    Matrix.mult(u,K,sep,K);		//sep <- u*sep
		    for (j=0; j<K; j++) {		//cova = cv, source = s
			for (i=0; i<K; i++)
			    cova[i][j] = cv[i][j];
			for (i=0; i<n; i++)
			    source[j][i] = s[j][i];
		    }
		    if (save)
			for (j=0; j<K; j++) for (i=0; i<K; i++)
			    seps[i][K*iter+j] = sep[i][j];
		    break;
		}
		else {
		    if (alam==1)				//1st time
			tmplam = -slope/(2*(crit-slope-Ent));
		    else {
			rhs1=crit-Ent-alam*slope;
			rhs2=crit2-Ent-alam2*slope;
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
		}	//end if (crit <= ...) else ...
	    }		//end for ( ; ; )
	}		//end for (iter= ...)
	return sep;
    }

    static double kentropy(double source[],int n,double bin,int nb,
			   double psi[]){
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
}
