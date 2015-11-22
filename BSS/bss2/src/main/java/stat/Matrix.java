package stat;

class Matrix {

    public static double[][] eye(int n) {
	//create an identity matrix of size n
        double eye[][]=new double[n][n];
	int i,j;
        for (i=0; i<n; i++){
            for (j=0; j<n; j++) eye[i][j]=0;
            eye[i][i] = 1;
        }
	return eye;
    }

    public static void mult(double a[][], int na, double[][] b, int nb,
			    double c[][], int nc) {
	//Set c=a*b, a, b being a na x nb and nb x nc matrix
	int i,j,k;
	double tmp;
        for (i=0; i<na; i++)
            for (j=0; j<nc; j++) {
		for (tmp=0,k=0; k<nb; k++)
		    tmp += a[i][k]*b[k][j];
		c[i][j] = tmp;
        }
    }

    public static void mult(double a[][], int na, double[][] b, int nb) {
	//transform b to =a*b, a, b being a na x na and na x nb matrix
	int i,j,k;
	double tmp, v[] = new double[na];
        for (j=0; j<nb; j++) {
            for (i=0; i<na; i++) {
		for (tmp=0,k=0; k<na; k++)
		    tmp += a[i][k]*b[k][j];
		v[i] = tmp;
	    }
	    for (i=0; i<na; i++)
		b[i][j] = v[i]; 
	}
    }

    public static int ludcmp(double a[][], int n, int indx[]) {
	// return 0 if the matrix has a row of zero, +/- 1 according to
	// the sign of the permutation indx 
	int i,imax=0,j,k;
	double big,dum,sum,temp;
	double vv[]=new double[n];	//for storage implicit scaling of rows
	final double TINY=1.0e-30;
	int d=1;
	for (i=0;i<n;i++) {
	    big=0.0;
	    for (j=0;j<n;j++)
		if ((temp=Math.abs(a[i][j])) > big) big=temp;
	    if (big == 0.0) return 0;
	    vv[i]=1.0/big;		//save the scaling of rows
	}
	for (j=0;j<n;j++) {	//loop over columns of Crout method
	    for (i=0;i<j;i++) {
		for (sum=a[i][j],k=1;k<i;k++) sum -= a[i][k]*a[k][j];
		a[i][j]=sum;
	    }
	    big=0.0;		//initialize search for largest pivot element
	    for (i=j;i<n;i++) {
		for (sum=a[i][j],k=0;k<j;k++)
		    sum -= a[i][k]*a[k][j];
		a[i][j]=sum;
		if ( (dum=vv[i]*Math.abs(sum)) >= big) {
		    big=dum;
		    imax=i;
		}
	    }
	    if (j != imax) {		//do we need to interchange rows
		for (k=0;k<n;k++) {	//yes, do so
		    dum=a[imax][k];
		    a[imax][k]=a[j][k];
		    a[j][k]=dum;
		}
		d = -d;			//and change sign of d
		vv[imax]=vv[j];
	    }
	    indx[j]=imax;
	    if (a[j][j] == 0.0) a[j][j]=TINY;
	    if (j != n) {		//finally, divide by the pivot element
		dum=1.0/(a[j][j]);
		for (i=j+1;i<n;i++) a[i][j] *= dum;
	    }
	}			//go back for the next column in the reduction
	return d;
    }

    public static double det(double a[][], int n) {
	double d, aa[][] = new double[n][n];
	int indx[] = new int[n];
	int i,j;
	for (i=0; i < n; i++)
	    for (j=0; j < n; j++)
		aa[i][j] = a[i][j];
	d = (double)ludcmp(aa,n,indx);
	for (i=0; i < n; i++)
	    d *= aa[i][i];
	return d;
    }

    /*
    public static void lubksb(double a[][], int n, int indx[], double b[]) {
	int i,ii=-1,ip,j;
	double sum;

	for (i=0;i<n;i++) {
	    ip=indx[i];
	    sum=b[ip];
	    b[ip]=b[i];
	    if (ii >=0)
		for (j=ii;j<i-1;j++) sum -= a[i][j]*b[j];
	    else if (sum !=0) ii=i;
	    b[i]=sum;
	}
	for (i=n-1;i>=0;i--) {
	    sum=b[i];
	    for (j=i+1;j<n;j++) sum -= a[i][j]*b[j];
	    b[i]=sum/a[i][i];
	}
    }
    */

    public static void choldc(double a[][], int n, double p[]) {
	int i,j,k;
	double sum;

	for (i=0;i<n;i++) {
	    for (j=i;j<n;j++) {
		for (sum=a[i][j],k=i-1;k>=0;k--)
		    sum -= a[i][k]*a[j][k];
		if (i == j) {
		    if (sum <= 0.0) {
			System.out.println("Cholesky decomp. failed");
			System.exit(1);
		    }
		    p[i]=Math.sqrt(sum);
		} else a[j][i]=sum/p[i];
	    }
	}
    }

    public static void cholsl(double a[][],int n,double p[],double b[],
			      double x[]){
	//x can be the same as b, then the solution x would overwrite b
	int i,k;
	double sum;

	for (i=0;i<n;i++) {
	    for (sum=b[i],k=i-1;k>=0;k--)
		sum -= a[i][k]*x[k];
	    x[i]=sum/p[i];
	}
	for (i=n-1;i>=0;i--) {
	    for (sum=x[i],k=i+1;k<n;k++)
		sum -= a[k][i]*x[k];
	    x[i]=sum/p[i];
	}
    }
}
