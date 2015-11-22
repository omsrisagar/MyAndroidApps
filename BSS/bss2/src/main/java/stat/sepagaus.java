package stat;

public class sepagaus{
    public double seps[][];
    public int iter;
    boolean save=false;

    public sepagaus(double seps[][]){
	this.seps = seps;
	this.save = true;
    }

    public sepagaus(){
	this.save = false;
    }

    public double[][]
	calc(double data[][],int K,int n,int nfreq,int bloclen,int maxiter){
	// input is data, a K by n matrix, which is unchanged.
	// output is the K by K seperation matrix and if seps is provided
	// it contains the sequence of iter+1 iterated seperation matrix

	int i,j,k,l,m,nfreq4=4*nfreq;
	double ssquare=((nfreq4*nfreq)-1)*2*nfreq/3;
	double spec[][]=new double[K][nfreq*2*(int)(n/bloclen)];
	double zr,zi,sum;
	double costab[]=new double[nfreq4], sintab[]=new double[nfreq4];
	double xr[][]=new double[K][n+nfreq];
	double xi[][]=new double[K][n+nfreq];
	int counts=0;
	if (nfreq>1) for(i=1;i<=nfreq;i++){
	    zr=Math.cos((i-.5)*Math.PI/nfreq);
	    zi=Math.sin((i-.5)*Math.PI/nfreq);
	    costab[0]=zr;
	    sintab[0]=zi;
	    for(j=1;j<4*nfreq;j++){
		costab[j] = costab[j-1]*zr - sintab[j-1]*zi;
		sintab[j] = costab[j-1]*zi + sintab[j-1]*zr;
	    }
	    for (k=0; k<K; k++){
		for (j=0;j<n;j++){
		    xr[k][j] = costab[j%nfreq4]*data[k][j];
		    xi[k][j] = sintab[j%nfreq4]*data[k][j];
		}
		xr[k][n-1+nfreq]=xr[k][n-1];
		xi[k][n-1+nfreq]=xi[k][n-1];
		for (j=n-2+nfreq;j>=n;j--){
		    xr[k][j] = xr[k][j+1] + xr[k][j-nfreq];
		    xi[k][j] = xi[k][j+1] + xi[k][j-nfreq];
		}
		for (j=n-1;j>=nfreq;j--){
		    xr[k][j] = xr[k][j+1] + xr[k][j-nfreq]-xr[k][j];
		    xi[k][j] = xi[k][j+1] + xi[k][j-nfreq]-xi[k][j];
		}
		for (j=nfreq-1;j>=0;j--){
		    xr[k][j] = xr[k][j+1] - xr[k][j];
		    xi[k][j] = xi[k][j+1] - xi[k][j];
		}
		for (j=1;j<=nfreq;j++){
		    xr[k][0] = xr[k][0] + xr[k][j];
		    xi[k][0] = xi[k][0] + xi[k][j];
		}
		for (j=1;j<n;j++){
		    xr[k][j] = xr[k][j-1] + xr[k][j+nfreq]-xr[k][j];
		    xi[k][j] = xi[k][j-1] + xi[k][j+nfreq]-xi[k][j];
		}
		xr[k][n] = xr[k][n-1]-xr[k][n];
		xi[k][n] = xi[k][n-1]-xi[k][n];
		for(j=0;j<n;j++){
		    xr[k][j] = xr[k][j]+xr[k][j+1];
		    xi[k][j] = xi[k][j]+xi[k][j+1];
		}
	    }
	    for (j=bloclen; j<=n; j+=bloclen){
		for (k=0; k<K; k++) for (l=0; l<K; l++){  
		    for (m=j-bloclen,sum=0; m<j; m++)
			sum += xr[k][m]*xr[l][m] + xi[k][m]*xi[l][m];
		    spec[k][counts+l] = sum/ssquare;
		}
		counts += K;
	    }
	}
	else for (j=bloclen; j<n; j+=bloclen){
	    for (k=0; k<K; k++) for (l=0; l<K; l++){
		for (m=j-bloclen,sum=0; m<j; m++)
		    sum += data[k][m]*data[l][m];
		spec[k][counts+l] = sum/ssquare;
	    }
	    counts += K;
	}

	double decr,eps=K*(K-1)*1e-8,sep[][]=new double[K][K];
	for (i=0; i<K; i++){
	    for (j=0; j<K; j++) sep[i][j] = 0;
	    sep[i][i] = 1;
	}
	if (save)
	    for (i=0; i<K; i++) for (j=0; j<K; j++) {
	    seps[i][j] = sep[i][j];
	    }
	for (iter=1; iter<=maxiter; iter++){
	    decr = jadiag1(spec,K,counts,sep);

	    if (save)
		for (i=0; i<K; i++) for (j=0; j<K; j++)
		    seps[i][iter*K+j]= sep[i][j];
	    if (decr < eps){
		iter++;
		break;
	    }
	}
	return sep;
    }

    public double jadiag1(double c[][],int m,int n,double a[][]){
	int nmat=(int)(n/m),iter,i,j,k,l;
	double one=1.000000000001;
	double decr=0,g12,g21,omega12,omega21,omega,tmp,tmp1,tmp2,h12,h21;

	for (i = 1; i < m; i++) for (j = 0; j < i; j++){
	    omega12 = omega21 = g12 = g21 = 0;
	    for (k=0; k<n; k+=m) {
		tmp1 = c[i][i+k];
		tmp2 = c[j][j+k];
		tmp  = c[i][j+k];
		g12 += tmp/tmp1;
		g21 += tmp/tmp2;
		omega12 += tmp2/tmp1;
		omega21 += tmp1/tmp2;
	    }
	    g12 /= nmat;
	    g21 /= nmat;
	    omega = Math.sqrt(omega12*omega21)/nmat;
	    tmp = Math.sqrt(omega21/omega12);
	    tmp1 = (tmp*g12 + g21)/(omega + 1);
	    if (omega < one) omega = one;
	    tmp2 = (tmp*g12 - g21)/(omega - 1);
	    h12 = tmp1 + tmp2;
	    h21 = (tmp1 - tmp2)/tmp;
	    decr = decr + nmat*(g12*h12 + g21*h21)/2;
	    tmp = 1 + Math.sqrt(1 - h12*h21);
	    h12 /= tmp;
	    h21 /= tmp;
	    for (k = 0; k < n; k++){
		tmp = c[i][k];
		c[i][k] -= h12*c[j][k];
		c[j][k] -= h21*tmp;
	    }
	    for (k = 0; k < n; k += m) for (l = 0; l < m; l++) {
		tmp = c[l][i+k];
		c[l][i+k] -= h12*c[l][j+k];
		c[l][j+k] -= h21*tmp;
	    }
	    for (k = 0; k < m; k++){
		tmp = a[i][k];
		a[i][k] -= h12*a[j][k];
		a[j][k] -= h21*tmp;
	    }
	}
	return decr;
    }
}
