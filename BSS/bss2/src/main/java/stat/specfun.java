package stat;

class specfun {

    final static int ITMAX=100;
    final static double EPS=3.0e-7, FPMIN=1.0e-30;

    static double gammln(double xx) {	//returns log(Gamma(xx))
	double x,y,tmp,ser;
	final double cof[]= {76.18009172947146,-86.50532032941677,
		             24.01409824083091,-1.231739572450155,
		              0.1208650973866179e-2,-0.5395239384953e-5};
	int j;

	y=x=xx;
	tmp=x+5.5;
	tmp -= (x+0.5)*Math.log(tmp);
	ser=1.000000000190015;
	for (j=0;j<6;j++) ser += cof[j]/++y;
	return -tmp+Math.log(2.5066282746310005*ser/x);
    }

    public static double gcf(double a, double x) {
	/* Returns the incomplete gamma function "integral from x to infinity
	   of e^{-t} t^{a-1} divided by Gamma(a)". The algorithm uses
	   continue fraction */
	int i;
	double an,b,c,d,del,h, gln;

	gln=gammln(a);
	b=x+1.0-a;
	c=1.0/FPMIN;
	d=1.0/b;
	h=d;
	for (i=1;i<=ITMAX;i++) {
	    an = -i*(i-a);
	    b += 2.0;
	    d=an*d+b;
	    if (Math.abs(d) < FPMIN) d=FPMIN;
	    c=b+an/c;
	    if (Math.abs(c) < FPMIN) c=FPMIN;
	    d=1.0/d;
	    del=d*c;
	    h *= del;
	    if (Math.abs(del-1.0) < EPS) break;
	}
	if (i > ITMAX) {
	    System.out.println("a too large or ITMAX too small in gcf");
	    System.exit(1);
	}
	return Math.exp(-x+a*Math.log(x)-gln)*h;
   }

    public static double gser(double a, double x) {
	/* Returns the incomplete gamma function "integral from 0 to x
	   of e^{-t} t^{a-1} divided by Gamma(a)". The algorithm uses
	   series expansion */
	int n;
	double sum,del,ap;

	double gln=gammln(a);
	if (x <= 0.0) {
	    if (x < 0.0) {
		System.out.println("x less than 0 in routine gser");
		return Double.NaN;
	    }
	    return 0.0;
	}
	ap=a;
	del=sum=1.0/a;
	for (n=1;n<=ITMAX;n++) {
	    ++ap;
	    del *= x/ap;
	    sum += del;
	    if (Math.abs(del) < Math.abs(sum)*EPS) {
		return sum*Math.exp(-x+a*Math.log(x)-gln);
	    }
	}
	System.out.println("a too large or ITMAX too small in gser");
	System.exit(1);
	return 0;    //silly, but the compiler requires a return !
    }

    public static double gammp(double a, double x) {
	/* Returns the incomplete gamma function "integral from 0 to x
	   of e^{-t} t^{a-1} divided by Gamma(a)" */
	if (x < 0.0 || a <= 0.0) {
	    System.out.println("Invalid arguments in routine gammp");
	    return Double.NaN;
	}
	if (x < (a+1.0))
	    return gser(a,x);
	else
	    return 1.0 - gcf(a,x);
    }

    public static double erf(double x) {
	/* Return the error function "2/sqrt(pi) times the integral from 0
	   to x of e^{-t^2} */
	return x < 0.0 ? -gammp(0.5,x*x) : gammp(0.5,x*x);
    }

    static double erfinv(double y) {
	/* x = erfinv(y) is the inverse error function. It satisfies
	   y = erf(x), for -1 <= y < 1 and -inf <= x <= inf.
	   This code is inspired from matlab */
	final double[] a={ 0.886226899,-1.645349621, 0.914624893,-0.140543331},
	               b={-2.118377725, 1.442710462,-0.329097515, 0.012229801},
		       c={-1.970840454,-1.624906493, 3.429567803, 1.641345311},
		       d={ 3.543889200, 1.637067800};
	final double SqrtPiOver2=.886226925452758;	//=sqrt(pi)/2
	double y0 = .7, z, x;

	if (Math.abs(y) <= y0) {	// Central range
	    z = y*y;
	    x = y*(((a[3]*z+a[2])*z+a[1])*z+a[0])/
		  ((((b[3]*z+b[2])*z+b[1])*z+b[0])*z+1);
	}
	else {
	    // Exceptional cases.
	    if (Math.abs(y) >= 1) {
		if (y == -1) return Double.NEGATIVE_INFINITY;
		if (y == 1) return Double.POSITIVE_INFINITY;
		return Double.NaN;
	    }
	    else {			// Near end points of range
		if (y0 < y) {
		    z = Math.sqrt(-Math.log((1-y)/2));
		    x = (((c[3]*z+c[2])*z+c[1])*z+c[0])/((d[1]*z+d[0])*z+1);
		}
		else {
		    z = Math.sqrt(-Math.log((1+y)/2));
		    x = -(((c[3]*z+c[2])*z+c[1])*z+c[0])/((d[1]*z+d[0])*z+1);
		}
	    }
	}
	/* Two steps of Newton-Raphson correction to full accuracy.
	   Without these steps, erfinv(y) would be about 3 times
	   faster to compute, but accurate to only about 6 digits. */
	x = x - (erf(x) - y)*SqrtPiOver2/Math.exp(-x*x);
	x = x - (erf(x) - y)*SqrtPiOver2/Math.exp(-x*x);
	return x;
    }
}
