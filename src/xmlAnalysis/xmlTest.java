package xmlAnalysis;

/*
 * @author chenjiyun  2015/12/10
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qaBodyAnalysis.Logistic;
import qaBodyAnalysis.qaContentFeature;
import qaBodyAnalysis.trainContent;
import xmlAnalysis.quesObject;

public class xmlTest {
	public static List<quesObject> readXml(Document doc) {
		List<quesObject> xmlList = new ArrayList<quesObject>();
		/* 获取文档树的根结点 */
		Element root = doc.getDocumentElement();
		/* 获取所有Question结点 */
		NodeList quesNode = root.getElementsByTagName("Question");
		int quesNodeLength = quesNode.getLength();
		// System.out.println("ques结点数量："+quesNodeLength);
		for (int j = 0; j < quesNodeLength; j++) {
			quesObject xoj = new quesObject();
			Element quesBody = (Element) (quesNode.item(j));
			String quesType=quesBody.getAttribute("QTYPE");//判断问题的类型
			if(quesType.equals("YES_NO")){
				continue;
			}
			else{ 
			xoj.setQID(quesBody.getAttribute("QID"));
			xoj.setQCATEGORY(quesBody.getAttribute("QCATEGORY"));
			xoj.setQBody(quesBody.getElementsByTagName("QBody").item(0).getTextContent());
			xoj.quesWords = xoj.getQBody().trim().split("[ ,.!?;]");
			xoj.quesNums = xoj.quesWords.length;
			// System.out.println(xoj.getQID()+"\t\t"+xoj.quesNums+"\t\t"+xoj.getQBody());
			/* 获取所有Comment评论 */
			NodeList commentNode = quesBody.getElementsByTagName("Comment");
			int commNodeLength = commentNode.getLength();
			List<commentObj> cojList = new ArrayList<commentObj>();
			for (int k = 0; k < commNodeLength; k++) {
				commentObj coj = new commentObj();
				Element commBody = (Element) (commentNode.item(k));
				coj.setCID(commBody.getAttribute("CID"));
				String flagClass=commBody.getAttribute("CGOLD");
				if(flagClass.equals("Good")){
					coj.setCGOLD(1);
				}else if(flagClass.equals("Bad")){
					coj.setCGOLD(2);
				}else if(flagClass.equals("Potential")){
					coj.setCGOLD(3);
				}else if(flagClass.equals("Dialogue")){
					coj.setCGOLD(4);
				}else if(flagClass.equals("Other")){
					coj.setCGOLD(5);
				}
				coj.setCBody(commBody.getElementsByTagName("CBody").item(0).getTextContent());
				coj.commWords = coj.getCBody().trim().split("[ ,.!?;]");
				coj.commNums = coj.commWords.length;
				cojList.add(coj);//将评论加入本问题的评论列表
			}
			xoj.setComments(cojList);
			xmlList.add(xoj);// 添加一条问题-评论集
		  }
		}
		return xmlList;
	}

	public static void main(String[] args) throws Exception {
		String pathOut = "./etc/out.txt";
		FileWriter file = new FileWriter(pathOut);
		try {
			// 得到DOM解析器的工厂实例
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// 从DOM工厂中获得DOM解析器
			DocumentBuilder db = dbf.newDocumentBuilder();
			String xmlFile = "./xmlFiles/dev.xml";
			Document doc = db.parse(xmlFile);
			/* 读取xml并解析得到内容 */
			List<quesObject> xmlReslut = readXml(doc);
			new qaContentFeature(xmlReslut);
			Logistic lg=new Logistic();
			lg.trainCon(xmlReslut);
			lg.printWeight("./etc/weightResult.txt");
			System.out.println("训练模型得到的数据："+lg.getWeights()[0]+"\t"+lg.getWeights()[1]+"\t"+lg.getWeights()[2]);
//			for (quesObject qjj : xmlReslut) {
//				file.write(qjj.getQID() + "\t\t" + qjj.quesNums + "\t\t"
//						+ qjj.getQBody() + "\r\n");
//				for (commentObj cmoj : qjj.getComments()) {
//					file.write(cmoj.getCID() + "\t\t" + cmoj.commNums + "\t\t"
//							+ cmoj.getCBody() + "\t\t" + cmoj.commFeatures[0]
//							+ "\t\t" + cmoj.commFeatures[1] + "\t\t"
//							+ cmoj.commFeatures[2] + "\r\n");
//				}
//				file.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
//						+ "\r\n");
//				file.flush();
//			}
//			file.close();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
