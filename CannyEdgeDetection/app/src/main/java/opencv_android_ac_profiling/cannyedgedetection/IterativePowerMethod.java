package opencv_android_ac_profiling.cannyedgedetection;

/**
 * Created by Vidyasagar Sadhu on 11/15/2015.
 */
//import java.util.Math;
import java.util.Enumeration;
import java.util.Hashtable;

import Jama.Matrix;

public class IterativePowerMethod {

    public static double sumMatrix(Matrix M){
        double sum = 0;
        for(int i=0;i<M.getRowDimension();i++){
            for(int j=0;j<M.getColumnDimension();j++){
                sum += M.get(i, j);
            }
        }
        return sum;
    }

    public static Matrix getW(int NUM_NET_NODES,double p){
        Matrix G = Matrix.random(NUM_NET_NODES, NUM_NET_NODES);
        for(int i=0;i<G.getRowDimension();i++){
            for(int j=0;j<G.getColumnDimension();j++){
                if(j>i){
                    if(G.get(i, j)<p){
                        G.set(i, j, 1);
                    }
                    else{
                        G.set(i, j, 0);
                    }
                }
                else{
                    G.set(i, j, 0);
                }
            }
        }
        G = G.plus(G.transpose());
        Matrix W = new Matrix(NUM_NET_NODES,NUM_NET_NODES,0);
        for(int i=0;i<W.getRowDimension();i++){
            for(int j=0;j<W.getColumnDimension();j++){
                if(G.get(i, j)==1){
                    double temp = Math.max(sumMatrix(W.getMatrix(i,i,0,W.getColumnDimension()-1)), sumMatrix(W.getMatrix(0,W.getRowDimension()-1,j,j)));
                    W.set(i, j, temp);
                }
            }
            W.set(i, i, sumMatrix(W.getMatrix(i,i,0,W.getColumnDimension()-1)));
        }
        double[][] temp = {{0.25,0.25,0,0.25,0.25},{0.25,0.4167,0.3333,0,0},{0,0.3333,0.6667,0,0},{0.25,0,0,0.75,0},{0.25,0,0,0,0.75}};
        Matrix Wtemp = new Matrix(temp);
        return Wtemp;
    }

    public static Hashtable<Integer,Double> getNeighboursW(Matrix W,int myDeviceNum){
        Hashtable<Integer,Double> neighboursW = new Hashtable<Integer,Double>();
        for(int j=0;j<W.getColumnDimension();j++){
            if(W.get(myDeviceNum, j) != 0){
                neighboursW.put(j, W.get(myDeviceNum, j));
            }
        }
        return neighboursW;
    }

    public static Matrix getZ(int deviceNum){
        // Actually use remote procedure calls to this device using its proxy name.
        double[] Z = {4.5000,    6.1000,    8.4000,   17.9000,   46.5000};
        Matrix myZ = new Matrix(Z,5);
        return myZ;
    }

    public static Hashtable<Integer,Matrix> getNeighboursZ(Hashtable<Integer,Double> neighboursW){
        Hashtable<Integer,Matrix> neighboursZ = new Hashtable<Integer,Matrix>();
        Enumeration<Integer> devices = neighboursW.keys();
        while(devices.hasMoreElements()){
            int device = devices.nextElement();
            if(device == myDeviceNum)
                neighboursZ.put(device, z_old);
            else
                neighboursZ.put(device, getZ(device));
        }
        return neighboursZ;
    }

    public static Matrix updateMyConsensusZ(Hashtable<Integer,Double> neighboursW, Hashtable<Integer,Matrix> neighboursZ){
        //use z_old for my matrix
        Matrix consensusZ = new Matrix(z_old.getRowDimension(),z_old.getColumnDimension(),0);
        Enumeration<Integer> devices = neighboursZ.keys();
        while(devices.hasMoreElements()){
            int device = devices.nextElement();
            consensusZ = consensusZ.plus(neighboursZ.get(device).times(neighboursW.get(device)));
        }
        return consensusZ;
    }

    public static Matrix z_old = new Matrix(1,5,1);
    public static int myDeviceNum = 1;
    public static void main() {
        long startTime = System.currentTimeMillis();

        double[][] data = {{1,1,1,1,1}, {1,2,3,1,1}, {1,3,6,1,1}, {7,2,3,1,6}, {8,9,1,12,13}};
        // double[][] data = {{1,1,1,1,1}, {1,2,3,1,1}, {1,3,6,1,1}, {7,2,3,1,6}, {4,3,6,7,3}};
        Matrix myData = new Matrix(data);

        double[] q = {0.3,0.2,0.7,1.3,2};
        Matrix q_old = new Matrix(q,5);
        Matrix q_new = new Matrix(q,5);
        Matrix z_new = new Matrix(q,5);
        Matrix v = new Matrix(q,5);

        int numIterOut=0, stoppingRuleOut=1;
        int numIterIn=0, stoppingRuleIn=1;
        int NUM_NET_NODES = 5; double p=0.5;


        Matrix W = getW(NUM_NET_NODES,p);
        Hashtable<Integer,Double> neighboursW = getNeighboursW(W,myDeviceNum);
        double myWWeight1 = W.get(0,myDeviceNum);

        while (numIterOut < stoppingRuleOut){
            z_old = myData.times(q_old);
            // z_old = z_new;
            numIterIn = 0;
            while (numIterIn < stoppingRuleIn){
                Hashtable<Integer,Matrix> neighboursZ = getNeighboursZ(neighboursW);
                z_new = updateMyConsensusZ(neighboursW, neighboursZ);
                z_old = z_new;
                numIterIn++;
            }
            v = z_new.times(1/Math.pow(myWWeight1, numIterIn));
            q_new = v.times(1/v.norm2());
            q_old = q_new;
            numIterOut++;
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time in Milliseconds is "+elapsedTime);
        //System.out.println(elapsedTime);
        q_old.print(5,4);
    }
}
