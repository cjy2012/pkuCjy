package qaBodyAnalysis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import xmlAnalysis.commentObj;
import xmlAnalysis.quesObject;


public class trainContent {
	Logistic lg;
	
	public trainContent(){
		lg=new Logistic();
	}
	
	public void extractWhen(List<quesObject> qls) {
		/*classify*/
		for(quesObject qoj:qls){
			for(commentObj coj:qoj.getComments()){
			  coj.score=this.lg.classify(coj.commFeatures);
			  System.out.println("·ÖÊý£º"+coj.score);
			}
		}
	}
	
	
	
}
