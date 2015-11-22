package com.vsadhu;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import Jama.Matrix;
import Jama.SingularValueDecomposition;
	
	public class ksvd {
		
		public static Matrix Y;
		public static Matrix X;
		public static Matrix D;
		public static ArrayList<Integer> omega;
		public static Matrix ER;
		
		/*public static void convert(ArrayList<String> listOfImages, int h, int w){
			try
			{
				int height = h;
				int width = w;
				double[][] doubleMatrix = new double[width*height][listOfImages.size()];
				for(int k=0; k<listOfImages.size(); k++){
					BufferedImage img = ImageIO.read(new File(listOfImages.get(k)));
					ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
					ColorConvertOp op = new ColorConvertOp(cs, null);  
					BufferedImage grayscaleImage = op.filter(img, null);
					int index = 0;
					for(int i=0; i<width; i++){
						for(int j=0; j<height; j++){
							int sRbgColor = img.getRGB(i, j);
							Color c = new Color(sRbgColor);
							int red = c.getRed();
							int green = c.getGreen();
							int blue = c.getBlue();
							//System.out.println("Red colour at pixel "+"("+i+","+j+")"+" is "+red);
							//System.out.println("Green colour at pixel "+"("+i+","+j+")"+" is "+green);
							//System.out.println("Blue colour at pixel "+"("+i+","+j+")"+" is "+blue);
							red = green = blue = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
							//System.out.println(red);
							doubleMatrix[index][k] = red;
							index++;
							//System.out.println(index);
						}
					}	
				}
				//printDoubleMatrix(doubleMatrix, listOfImages.size(), width*height);
				Y = new Matrix(doubleMatrix);
				Y.print(2, 2);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			someFunction();
			
		}*/
		
		public static void convert(String fName) throws NumberFormatException, IOException{

			BufferedReader in = new BufferedReader(new FileReader(fName));

			String s;
			double[][] a = new double[256][1100];
			int j=0;
			while((s= in.readLine()) != null){
				String values[]= s.split(",");
				for(int i =0 ; i< values.length; i++)
				{
					a[j][i]= Double.parseDouble(values[i]);
				}
				j++;
			}

			in.close();
			Y = new Matrix(a);
			
			int count =0;
			BufferedImage b= new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
			for(int k=0; k<16; k++){
				for(int l=0; l<16; l++){
					b.setRGB(k, l, (int) a[count][0]);
					count++;
				}
				//temp++;
			}
			
			ImageIO.write(b, "png", new File("new.png"));
			
			someFunction();
			
		}

	public static void someFunction(){

		double A[][] = null;
		int check[]=null;
		double e1=0;
		double e2=0;
		Matrix Z, tempz, p, identity, backupZ, G, D1, r;
		Matrix DICT_NEW;
		int width=0;
		int height=0;
		double[][] arrayOut = null;
		int[][] intArrayOut = null;

		Matrix YN = Y;
		backupZ = new Matrix(Y.getRowDimension(), 1000);

		//------------------------------- Initializing the random dictionary -----------------------------------------
		// Set a double matrix A to random values
		A = new double[Y.getRowDimension()][30];
		for (int rowD = 0; rowD < Y.getRowDimension(); rowD++) {
			for (int colD = 0; colD < 30; colD++) {
				A[rowD][colD] = (double) Math.random();
			}
		}
		// Assign the random double array to Dictionary Matrix of size (Pixels in image * (Pixels in image*2))
		D = new Matrix(A);
		
		G = new Matrix(D.getColumnDimension(), 1);
		
		// Number of columns in the dictionary
		check = new int[D.getColumnDimension()];
		
		// Normalize each column in the dictionary
		for (int normVar = 0; normVar < D.getColumnDimension() - 1; normVar++) {
			D1 = D.getMatrix(0, D.getRowDimension() - 1, normVar, normVar);
			double s = 1 / D1.normF();
			D1 = D1.times(s);
			D.setMatrix(0, D.getRowDimension() - 1, normVar, normVar, D1);

		}
		//----------------------------------------- Initialization of random dictionary over ----------------------
		
		DICT_NEW=new Matrix(D.getRowDimension(),1);
		// Sparse coded matrix X ( (2*Number of pixels) * Number of images)
		X = new Matrix(D.getColumnDimension(),Y.getColumnDimension());
		
		Matrix X2=new Matrix(D.getColumnDimension(),Y.getColumnDimension()); // Why?
		
		r = Y.getMatrix(0, Y.getRowDimension() - 1, 0,Y.getColumnDimension() - 1); // Why?

		// Number of update stages in dictionary learning phase
		for (int iter = 0; iter < 6; iter++) {
			if (iter==5)
				Y=YN.getMatrix(0, YN.getRowDimension()-1,0,YN.getColumnDimension()-1);

			// Sets the matrix X in Y - DX to all zeroes (Initialization)
			for (int rowD=0; rowD<X.getRowDimension(); rowD++)
			{
				for (int colD=0; colD<X.getColumnDimension(); colD++)
				{
					X.set(rowD, colD, 0);
				}
			}
			
			r = Y.getMatrix(0, Y.getRowDimension() - 1, 0,Y.getColumnDimension() - 1); // Why?
			
			//------------------------------------ Beginning of sparse coding stage-----------------------------------------

			// Loop to consider all images
			for (int i1 = 0; i1 < Y.getColumnDimension(); i1++) {	

				//No. of Atoms to be considered is chosen here		
				for (int i = 0; i <25; i++) {
					// Getting the largest contributor from the dictionary
					Matrix m = ((r.getMatrix(0, r.getRowDimension() - 1, i1, i1)).transpose()).times(D);
					int max = 0;
					double a, b;
					for (int j = 1; j < m.getColumnDimension(); j++) {
						a = Math.abs(m.get(0, j));
						b = Math.abs(m.get(0, max));
						if (a > b)
							max = j;
					}


					check[i] = max;
					int q = 0;
					for (int j = 0; j < i; j++) {
						if (check[j] == max) {
							q = 1;
						}
					}
					int i4 = 0;
					for (int i3 = 0; i3 < r.getRowDimension(); i3++) {
						double x = r.get(i3, i1);
						if (x < 0.0001) {
							i4++;
						}
						if (i4 == r.getRowDimension())
							q = 1;
					}
					if (q == 1)
						break;


					tempz = D.getMatrix(0, D.getRowDimension() - 1, max, max);

					backupZ.setMatrix(0, backupZ.getRowDimension() - 1, i, i,tempz);

					Z = backupZ.getMatrix(0, backupZ.getRowDimension() - 1, 0,i);

					p = Z.times(((Z.transpose().times(Z)).inverse())).times(Z.transpose());

					G = ((Z.transpose().times(Z)).inverse()).times(Z.transpose()).times(
							r.getMatrix(0, r.getRowDimension() - 1, i1, i1));

					double coeff;
					for (int k = 0; k <= i; k++) {
						coeff =  X.get(check[k], i1)+G.get(k, 0);
						X.set(check[k], i1, coeff);
					}

					identity = Matrix.identity(Y.getRowDimension(), Y.getRowDimension());
					Matrix res;

					res = (identity.minus(p)).times(Y.getMatrix(0,Y.getRowDimension() - 1, i1, i1));
					r.setMatrix(0, r.getRowDimension() - 1, i1, i1, res);

				}

			}

			System.out.println("X after sparse coding:");
			//X.print(7, 2);

			Matrix err;
			err=Y.minus(D.times(X));
			double sum1=0,sum4=0;
			//r.print(4, 0);
			for(int a3=0;a3<err.getRowDimension();a3++)
			{
				for (int a4=0;a4<err.getColumnDimension();a4++)
				{
					sum1=sum1+Math.abs(err.get(a3, a4));
					sum4=sum4+Math.abs(r.get(a3,a4));

				}
			}
			//System.out.println(sum4);
			e1=sum1/(256*1100);
			sum4=sum4/(256*1100);
			System.out.println("residue");
			System.out.println(sum4);
			System.out.println("Error after sparse coding");
			System.out.println(e1);
			if(iter==1)
			{
				Matrix test=new Matrix(X.getRowDimension(),X.getColumnDimension());
				//X.print(4, 2);
				test=X2.minus(X);
				//test.print(4, 2);
				//System.out.println(e1);
				//System.out.println(sum4);
			}

			///////////////////////////////..................End of sparse coding stage............................/////////////////////////////

			//////////////////////////////...................Beginning of Dictionary Update Stage...................////////////////////////////


			Matrix New_DICT = new Matrix(D.getRowDimension(),
					D.getColumnDimension() - 1);
			Matrix New_Coeff = new Matrix(X.getRowDimension() - 1,
					X.getColumnDimension());
			int columns[] = new int[D.getColumnDimension() - 1];
			System.out.println("D column dimension"+D.getColumnDimension());
			for (int i2 = 0; i2 < D.getColumnDimension(); i2++) {

				int f = 1;
				for (int t = 0; t < D.getColumnDimension() - 1; t++) {
					columns[t] = (f + i2) % D.getColumnDimension();
					f++;
				}
				New_DICT = D.getMatrix(0, Y.getRowDimension() - 1, columns);
				New_Coeff = X.getMatrix(columns, 0, X.getColumnDimension() - 1);
				Matrix E;
				E = Y.minus(New_DICT.times(New_Coeff));
				ArrayList<Integer> omega = new ArrayList<Integer>();

				for (int i = 0; i < X.getColumnDimension(); i++) {
					if (X.get(i2, i) != 0) {omega.add(i);}}
				ER=new Matrix(D.getRowDimension(),omega.size());
				System.out.println(omega.size());
				if (omega.size()!=0){


					Matrix Omega = new Matrix(Y.getColumnDimension(), omega.size());
					for (int var = 0; var < omega.size(); var++) {
						Omega.set(omega.get(var), var, 1);
					}

					ER = E.times(Omega);
					System.out.println("Dimension "+ ER.getRowDimension()+" "+ER.getColumnDimension());
					// Uncomment later
					SingularValueDecomposition svdER = new SingularValueDecomposition(ER);

					Matrix left_eigen = svdER.getU();
					//Matrix DICT_UPDATE = left_eigen.getMatrix(0,left_eigen.getRowDimension() - 1, 0, 0);	
					DICT_NEW = left_eigen.getMatrix(0,left_eigen.getRowDimension() - 1, 0, 0);
					double s1=1/DICT_NEW.normF();
					DICT_NEW=DICT_NEW.times(s1);
				}
				else if (omega.size()==0)
				{
					DICT_NEW=new Matrix(D.getRowDimension(),1);
					DICT_NEW.setMatrix(0, D.getRowDimension()-1,0, 0,D.getMatrix(0,D.getRowDimension()-1,i2,i2));
					System.out.println("Omega size is 0");
				}

				////////////////////...................Consensus to be done at this step....................................../////////////////////			




				D.setMatrix(0, D.getRowDimension() - 1, i2, i2, DICT_NEW);
				Matrix Coeff_UPDATE1;
				if (omega.size()!=0){
					Coeff_UPDATE1=DICT_NEW.transpose().times(ER);
					Matrix Coeff_UPDATE=new Matrix(1,Y.getColumnDimension());
					for (int i9=0;i9<omega.size();i9++)
					{
						Coeff_UPDATE.set(0, omega.get(i9),Coeff_UPDATE1.get(0,i9));
					}
					X.setMatrix(i2,i2,0,X.getColumnDimension()-1, Coeff_UPDATE);
				}
			}
			
			Matrix y1;
			y1=D.times(X);
			Matrix error;
			error=y1.minus(Y);
			double sum=0;
			for(int a1=0;a1<error.getRowDimension();a1++)
			{
				for (int a2=0;a2<error.getColumnDimension();a2++)
				{
					sum=sum+Math.abs(error.get(a1, a2));

				}
			}
			e2=sum/(256*1100);	

			// System.out.println("X after update:");
			// X.print(4, 2);




			System.out.println("Error after Dic Update");
			System.out.println(e2);

			X2=X.getMatrix(0, X.getRowDimension()-1,0,X.getColumnDimension()-1);
			// if (iter==0)
			//{
			//X2.print(4, 2);
			//}
			////////////////////............End of Dictionary Update...............//////////////////////////////////

			//System.out.println("X after dic update:");
			// X.print(7, 2);
			//System.out.println("Reconstruction:");
			//D.times(X).print(4,0);
			// System.out.println("D after update:");
			//D.print(4, 2);


		}
		//Y.print(4,0);


		//arrayOut=y1.getArrayCopy();
		//height =y1.getRowDimension();
		//System.out.println(height);
		//width = y1.getColumnDimension();
		//System.out.println(width);
		intArrayOut = new int[height][width];

		//error.print(4,0);
		//System.out.println(error.getRowDimension());
		//System.out.println(error.getColumnDimension());

		//System.out.println(e1);


		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				//System.out.println(arrayOut[i][j]);
				intArrayOut[j][i] = (int) arrayOut[j][i];
			}
		}
		/*finalImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      //rasterOut = (WritableRaster) finalImage.getData();
      rasterOut= Raster.createWritableRaster(sampleModel, new Point(0,0));
      for(int i=0;i<width;i++)
      {
          for(int j=0;j<height;j++)
          {
          	//System.out.println(arrayOut[i][j]);
              rasterOut.setSample(i,j,0,intArrayOut[j][i]);
          }
      }

      //rasterOut.setPixels(0,0,width,height,arrayOut);
      File imageFile = new File("capturedImage.png");
      try {
			ImageIO.write(finalImage, "png", imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		//System.out.println("Columns: "+D.getColumnDimension()+" Rows: "+D.getRowDimension());
		//System.out.println("Columns: "+X.getColumnDimension()+" Rows: "+X.getRowDimension());
		double[][] an= D.times(X.getMatrix(0, X.getRowDimension()-1, 0, 0)).getArray();
		int count =0;
		BufferedImage b= new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
		for(int k=0; k<16; k++){
			for(int l=0; l<16; l++){
				b.setRGB(k, l, (int) an[count][0]);
				count++;
			}
		}
		
		try {
			ImageIO.write(b, "png", new File("new1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	
	// Print the double matrix
	public static void printDoubleMatrix(double[][] a, int cols, int rows){

		for(int i=0; i<cols; i++){
			for(int j=0; j<rows; j++){
				System.out.print(a[j][i]);
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		ArrayList<String> list = new ArrayList<String>();
		list.add("subject01.gif");
		list.add("subject01.glasses.gif");
		//convert(list,243,320);
		//convert(list,50,50);
		//DictionaryLearning();
		//sparseCoding();*/
		
		convert("nines.txt");
		
		
	}
}

	
	

