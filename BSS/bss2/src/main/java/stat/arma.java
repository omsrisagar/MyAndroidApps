package stat;
import java.lang.*;
import java.util.*;

public class arma {

    public static void whitenoise(double x[],int n) {
        Random r=new Random();
        for (int i = 0; i < n; i++) x[i] = r.nextGaussian();
    }

    public static boolean gene(double a[],int na,double b[],int nb,
			       double varinn,double x[],int n) {
/*
  Generate the process x[i] = a[0]x[i-1] + ... + a[na-1]x[i-na] + e[i] +
                              b[0]e[i-1] + ... + b[nb-1]e[i-nb]
  Return false if the length n of the x array < na+nb or if the AR
  polynomial is unstable. Otherwise x contains the generated process.
*/
	if (n < na+nb) return false;
	int    i, k;
	double r, s, v;
	double[] ar = new double[na], y = new double[na+2*nb];

	// Backward Levinson Durbin algorithm, use ar to avoid changing a
	for (i = 0; i < na; i++)
	    ar[i] = a[i];
	v = 1;
	for (k = na-1; k >= 0; k--) {
	    r = ar[k]/v;
	    if (Math.abs(r) >= 1) return false;
	    i = (int)(k/2);
	    if (i == k-1-i)
		ar[i] *= (1 + r);
	    while (i-- > 0) {
		s = ar[k-i-1];
		ar[k-i-1] += r*a[i];
		ar[i] += r*s;
	    }
	    ar[k] = r;
	    v *= (1 - r*r);
	}
	v = Math.sqrt(varinn/v);

	// Generate e[k]+b[0]*e[k-1]+...+b[nb-1]*e[k-nb], na<=k<n (no scale)
	whitenoise(y,nb);
	whitenoise(x,n);
	for (k = 0; k < na+nb; k++)
	    y[nb+k] = x[k];
	for (k = n-2; k >= na+nb-1; k--) {
	    for (s = x[k+1], i = 0; i < nb; i++)
		s += b[i]*x[k-i];
	    x[k+1] = s;
	}
	for (k = na+nb ; k < na+2*nb; k++) {
	    for (s = y[k], i = 0; i < nb; i++)
		s += b[i]*y[k-1-i];
	    x[k-nb] = s;
	}

	//Generate the process y_k = a[0]*y_{k-1}+...+a[na-1]*y_{k-na}+e[k],
	//for k=-nb,...,na-1 and store them at y[0],...,y[na+nb-1]
	for (k = 0; k < na; k++) {
	    for (s = v*y[k], i = 0; i < k; i++)
		s += ar[i]*y[k-1-i];
	    y[k] = s;
	    r = ar[k];
	    v = v*Math.sqrt(1 - r*r);
	    i = (int)(k/2);
	    if (i == k-1-i)
		ar[i] *= (1 - r);
	    while (i-- > 0) {
		s = ar[k-i-1];
		ar[k-i-1] -= r*a[i];
		ar[i] -= r*s;
	    }
	}
	v = Math.sqrt(varinn);
	for (; k < na+nb; k++) {
	    for (s = v*y[k], i = 0; i < na; i++)
		s += a[i]*y[k-1-i];
	    y[k] = s;
	}

	// generate x[k] = y_k+b[0]*y_{k-1}+...+b[nb-1]*y_{k-nb}, 0<=k<na
	for (k = nb; k < na+nb; k++) {
	    for (s = y[k], i = 0; i < nb; i++)
		s += b[i]*y[k-1-i];
	    x[k-nb] = s;
	}

	// Finally generate the process from na to n-1
	for (k = na-1; k < n-1; k++) {
	    for (s = v*x[k+1], i = 0; i < na; i++)
		s += a[i]*x[k-i];
	    x[k+1] = s;
	}
	return true;
    }
}
