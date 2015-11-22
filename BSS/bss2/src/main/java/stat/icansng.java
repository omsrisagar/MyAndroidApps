package stat;

public class icansng{
    public double seps[][];
    public int iter;
    boolean save=false;

    public icansng(double seps[][]){
        this.seps = seps;
        this.save = true;
    }

    public icansng(){
        this.save = false;
    }

    public double[][]
        calc(double data[][],int K,int n,int nbloc,int maxiter,
	     double source[][]){
        // Input is data, a K by n matrix, which is unchanged.
	// output is the K by K seperation matrix and if seps is provided
        // it contains the sequence of iter iterated seperation matrix

	double eps = 1e-4*K*(K-1)/n;
	int i,j,k;

	// Compute the initial sources

        // take sep the identity matrix => source=data initially
	double sep[][] = Matrix.eye(2);
        for (k=0; k<K; k++) for (i=0; i<n; i++)
	    source[k][i] = data[k][i];

	// Iteration loop

	if (save)
	    for (j=0; j<K; j++) for (k=0; k<K; k++)
		seps[j][k] = sep[j][k];

	int nb, blocbegin, blocend, bloclen;
	double m0, m2[] = new double[K], m4, m6, tmp, c1, c3;
	double psi[][] = new double[K][n], psip[] = new double[K];
	double G[][] = new double[K][k], H[][] = new double[K][k];
	double u[][] = new double[K][k], decr;

	for (iter=1; iter <= maxiter; iter++) {

	    for (j=0; j<K; j++) for (k=0; k<K; k++)
		H[j][k] = G[j][k] = 0;		//initialize G & H to 0

	    blocbegin = 0;
	    for (nb=1; nb<=nbloc; nb++) {	// Loop over blocks

		blocend = Math.round(nb*n/(float)nbloc);
		m0 = blocend - blocbegin;

		for (k=0; k<K; k++) {		//loop over sources index
		    // Compute the moments of a block
		    m6 = m4 = m2[k] = 0;
		    for (i=blocbegin; i<blocend; i++) {
			tmp = source[k][i];
			tmp *= tmp;		// square it
			m2[k] += tmp;
			m4 += tmp*tmp;
			m6 += tmp*tmp*tmp;
		    }
		    // Compute the coefs of the score function of a block
		    tmp = m6*m2[k] - m4*m4;
		    c1 = (m0*m6 - m4*3*m2[k])/tmp;
		    c3 = (m2[k]*3*m2[k] - m0*m4)/tmp;
		    // Compute the score function
		    for (i=blocbegin; i<blocend; i++) {
			tmp = source[k][i];
			psi[k][i] = (c1 + c3*tmp*tmp)*tmp;
		    }
		    // Compute the expected derivative of the score function
		    psip[k] = c1 + 3*(m2[k]/m0)*c3;
		}

		// Compute the gradient and Hessian
		for (j=0; j<K; j++) for (k=0; k<j; k++) {
		    for (i=blocbegin; i < blocend; i++) {
			G[j][k] += psi[j][i]*source[k][i];
			G[k][j] += psi[k][i]*source[j][i];
		    }
		    H[j][k] += psip[j]*m2[k];
		    H[k][j] += psip[k]*m2[j];
		}
		blocbegin = blocend;		//begining of next block
	    }

	    // Compute the transformed gradient

	    for (decr=0,j=0; j<K; j++) {
		u[j][j] = 1;
		for (k=0; k<j; k++) {
		    tmp = H[j][k]*H[k][j] - n*n;
		    u[j][k] = -(H[k][j]*G[j][k] - G[k][j]*n)/tmp;
		    u[k][j] = -(H[j][k]*G[k][j] - G[j][k]*n)/tmp;
		    decr -= (u[k][j]*G[k][j] + u[j][k]*G[j][k]);
		}
	    }				//decr/n is the real decrease

	    // New separating matrix

	    Matrix.mult(u,K,sep,K);
	    if (save)
		for (j=0; j<K; j++) for (i=0; i<K; i++)
		    seps[i][K*iter+j] = sep[i][j];
	    Matrix.mult(u,K,source,n);
	    if (decr/n < eps) {
		iter++;
		break;
	    }
	}
	return sep;
    }
}
