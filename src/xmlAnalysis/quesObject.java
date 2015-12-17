package xmlAnalysis;

import java.util.ArrayList;
import java.util.List;

public class quesObject {
	private String QID;
	private String QCATEGORY;
	private String QBody;
	private List<commentObj> Comments;
	public String [] quesWords;
	public int quesNums;
	public double [] quesFeatures;

	public String getQID() {
		return QID;
	}
	public void setQID(String qID) {
		QID = qID;
	}
	public String getQCATEGORY() {
		return QCATEGORY;
	}
	public void setQCATEGORY(String qCATEGORY) {
		QCATEGORY = qCATEGORY;
	}
	public String getQBody() {
		return QBody;
	}
	public void setQBody(String qBody) {
		QBody = qBody;
	}
	public List<commentObj> getComments() {
		return Comments;
	}
	public void setComments(List<commentObj> comments) {
		Comments = comments;
	}
}
