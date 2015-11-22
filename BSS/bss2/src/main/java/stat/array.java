package stat;

public class array {

    public static void center(double x[],int n, double xc[]) {
	// Center the data x and store at xc (xc can be the same as x)
        double tmp=0;
	int i;
	for (i=0; i<n; i++) tmp += x[i];
	tmp /= n;
	for (i=0; i<n; i++) xc[i] = x[i]-tmp;
    }

    public static void covc(double x[][], int K, int n, double cova[][]) {
	// Set cova to the covariance matrix of K centered data
	// x[0][.],...,x[K-1][.]
        double tmp;
	int i,j,k;
        for (i=0; i<K; i++) for (j=0; j<K; j++){
            for (tmp=0, k=0; k<n; k++)
                tmp += x[i][k]*x[j][k];
            cova[i][j] = tmp/n;
        }
    }

    /************
    public static void sort(double arr[], int n) {
	// Sort an array[n] into ascending order using Quicksort algorithm.
	// The array is arr is replaced by the sorted array.
	// The code is lifted from nrecipes. The indexes i,j,k,l,jstack
	// (except the i in the 1st innermost loop) are all 1 unit smaller
	// than the original version (since our array index starts at 0).
	final int NSTACK=50, M=7;
	int i,ir=n-1,j,k,l=0;
	int jstack=-1, istack[] = new int[NSTACK];
	double a,temp;

	for (;;) {	//insertion sort when the subarray is small enough
	    if (ir-l < M) {
		for (j=l+1; j<=ir; j++) {
		    a = arr[j];
		    for (i=j; i>=1; i--) {
			if (arr[i-1] <= a) break;
			arr[i]=arr[i-1];
		    }
		    arr[i]=a;
		}
		if (jstack < 1) break;
		ir=istack[jstack--];	//Pop stack and begin a new round
		l=istack[jstack--];	//of partitionning
	    } else {
		k=(l+ir) >> 1;		//Choose median of left, center and
		temp=arr[k];		//right element as partitioning
		arr[k]=arr[l+1];	//element a. Also rearrange so that
		arr[l+1]=temp;		//a[l+1]<=a<=a[ir]
		if (arr[l+1] > arr[ir]) {
		    temp=arr[l+1];
		    arr[l+1]=arr[ir];
		    arr[ir]=temp;
		}
		if (arr[l] > arr[ir]) {
		    temp=arr[l];
		    arr[l]=arr[ir];
		    arr[ir]=temp;
		}
		if (arr[l+1] > arr[l]) {
		    temp=arr[l+1];
		    arr[l+1]=arr[l];
		    arr[l]=temp;
		}
		i=l+1;			//Initialize pointers for partitioning
		j=ir;
		a=arr[l];
		for (;;) {		//beginning of innermost loop
		    do i++; while (arr[i] < a);	//Scan up to find elem >= a
		    do j--; while (arr[j] > a);	//Scan down to find elem <= a
		    if (j < i) break;	//Pointer cross, partinioning complete
		    temp=arr[i];
		    arr[i]=arr[j];
		    arr[j]=temp;
		}
		arr[l]=arr[j];
		arr[j]=a;
		jstack += 2;
		if (jstack > NSTACK) {
		    System.out.println("NSTACK too small in sort.");
		    System.exit(1);
		}
		if (ir-i+1 >= j-l) {
		    istack[jstack]=ir;
		    istack[jstack-1]=i;
		    ir=j-1;
		} else {
		    istack[jstack]=j-1;
		    istack[jstack-1]=l;
		    l=i;
		}
	    }
	}
    }

   ***********/

    public static int[] indx(double arr[], int n) {
	// Index an array[n], i.e. output an index array indx such that
	// the array arr[indx[]] is is in ascending order. The array arr
	// is unchanged. The algorithm is again quicksort.
	// The code is lifted from nrecipes. The indexes i,j,k,l,jstack
	// (except the i in the 1st innermost loop) are all 1 unit smaller
	// than the original version (since our array index starts at 0).
	final int NSTACK=50, M=7;
	int i,indxt,ir=n-1,itemp,j,k,l=0;
	int jstack=-1, istack[] = new int[NSTACK];
	int indx[] = new int[n];
	double a;

	for (j=0;j<n;j++) indx[j]=j;
	for (;;) {
	    if (ir-l < M) {
		for (j=l+1;j<=ir;j++) {
		    indxt=indx[j];
		    a=arr[indxt];
		    for (i=j; i>=1; i--) {
			if (arr[indx[i-1]] <= a) break;
			indx[i]=indx[i-1];
		    }
		    indx[i]=indxt;
		}
		if (jstack < 1) break;
		ir=istack[jstack--];
		l=istack[jstack--];
	    } else {
		k=(l+ir) >> 1;
		itemp=indx[k];
		indx[k]=indx[l+1];
		indx[l+1]=itemp;
		if (arr[indx[l+1]] > arr[indx[ir]]) {
		    itemp=indx[l+1];
		    indx[l+1]=indx[ir];
		    indx[ir]=itemp;
		}
		if (arr[indx[l]] > arr[indx[ir]]) {
		    itemp=indx[l];
		    indx[l]=indx[ir];
		    indx[ir]=itemp;
		}
		if (arr[indx[l+1]] > arr[indx[l]]) {
		    itemp=indx[l+1];
		    indx[l+1]=indx[l];
		    indx[l]=itemp;
		}
		i=l+1;
		j=ir;
		indxt=indx[l];
		a=arr[indxt];
		for (;;) {
		    do i++; while (arr[indx[i]] < a);
		    do j--; while (arr[indx[j]] > a);
		    if (j < i) break;
		    itemp=indx[i];
		    indx[i]=indx[j];
		    indx[j]=itemp;
		}
		indx[l]=indx[j];
		indx[j]=indxt;
		jstack += 2;
		if (jstack > NSTACK) {
		    System.out.println("NSTACK too small in sort.");
		    System.exit(1);
		}
		if (ir-i+1 >= j-l) {
		    istack[jstack]=ir;
		    istack[jstack-1]=i;
		    ir=j-1;
		} else {
		    istack[jstack]=j-1;
		    istack[jstack-1]=l;
		    l=i;
		}
	    }
	}
	return indx;
    }

   public static int[] rank(double arr[], int n) {
       // Index an array[n], i.e. output a integer array yielding the
       // rank of a[0], ..., a[n]
       int index[] = indx(arr,n), rk[] = new int[n];
       for (int i=0; i < n ; i++)
	   rk[index[i]] = i;
       return rk;
   }

}
