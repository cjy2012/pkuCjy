package xmlAnalysis;

public class commentObj {
	private String CID;
	private String CBody;
	private int CGOLD;
    public String [] commWords;
    public int commNums;
    public double [] commFeatures; 
    public double score;
    
    public commentObj(){
    	this.commFeatures=new double[3];
    }
	public String getCID() {
		return CID;
	}
	public void setCID(String cID) {
		CID = cID;
	}
	public String getCBody() {
		return CBody;
	}
	public void setCBody(String cBody) {
		CBody = cBody;
	}
	public int getCGOLD() {
		return CGOLD;
	}
	public void setCGOLD(int cGOLD) {
		CGOLD = cGOLD;
	}
	
}
