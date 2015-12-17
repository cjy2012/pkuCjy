package qaBodyAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;
/*
 * Author:Chen Jiyun
 * Date: 2015-12-17  
 **/

import xmlAnalysis.commentObj;
import xmlAnalysis.quesObject;

public class Logistic {
	/* the learning rate */
    private double rate;
    /* the weight to learn */
    private double[] weights;
    /* the number of iterations */
    private int ITERATIONS = 3000;
    public Logistic() {
        this.rate = 0.0001;
        weights = new double[3];//Corpus.weight_dim 
    }
    
    public double[] getWeights(){
    	return this.weights;
    }

    private double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }
 
    public void trainCon(List<quesObject> qls) {
    	double[] t_weights = new double[this.weights.length];
    	int n;
    	this.weights[0]=1;
    	this.weights[1]=0.5;
    	this.weights[2]=0.7;
        for (n=0; n<ITERATIONS; n++) {
        	double[] x;
        	for(int k=0;k<this.weights.length;k++)
            	t_weights[k] = this.weights[k];
        	for(quesObject qoj:qls){
    			for(commentObj coj:qoj.getComments()){
    			  x=coj.commFeatures;
    			  double predicted = classify(x);
    			  int label=coj.getCGOLD();
    			  this.weights[0] += this.rate*(label-predicted);
                  this.weights[1] += this.rate*(label-predicted);
                  this.weights[2] += this.rate*(label-predicted);
    			}
    		}
            
            if(isConverge(t_weights, weights)){
            	System.out.println("train is converge.");
            	break;
            }
        }
           // System.out.println("iteration: " + n);
        this.weights[0]=this.weights[0]-(int)this.weights[0]/1;
        this.weights[1]=this.weights[1]-(int)this.weights[1]/1;
        this.weights[2]=this.weights[2]-(int)this.weights[2]/1;
    }
    
    public boolean isConverge(double[] w1, double[] w2){
    	double result= 0.0;
    	for(int i=0;i<w1.length;i++)
    		result = result+(w1[i]-w2[i])*(w1[i]-w2[i]);
    	if(result<0.0002)
    		return true;
    	else
    		return false;
    }
    
    public double mulDoub(double[] wvector){
    	double re_d = .0;
    	
    	for(int i=0;i<wvector.length;i++){
    		re_d += this.weights[i]*wvector[i];
    	}
    	return re_d;
    }

    public double classify(double[] x) {
        double logit = .0;
        logit = mulDoub(x);//project multiple dimension to one , by SUMMING, x'=W*X
        return sigmoid(logit);
    }
    
    public void printWeight(String path) throws Exception{
    	File proFile = new File(path);
		FileOutputStream fos = new FileOutputStream(proFile);
		Writer out = new OutputStreamWriter(fos,"UTF-8");
		PrintWriter pw = new PrintWriter(out);
		StringBuffer weights = new StringBuffer();
		int i;
		for(i=0;i<this.weights.length-1;i++)
			weights.append(this.weights[i]).append("\t");
		weights.append(this.weights[i]);
		pw.println(weights.toString());
		pw.close();
    }
    
    public void loadWeight(String path) throws Exception{
    	File inf = new File(path);
		FileInputStream fis= new FileInputStream(inf);
		InputStreamReader in= new InputStreamReader(fis,"UTF-8");
		Scanner cin= new Scanner(in);
		String[] line = cin.nextLine().split("\t");
		if(line.length==this.weights.length){
			for(int i=0;i<line.length;i++)
				this.weights[i] = Double.valueOf(line[i]);
		}
		else
		   cin.close();
    }
}


