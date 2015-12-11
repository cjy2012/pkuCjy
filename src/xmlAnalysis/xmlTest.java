package xmlAnalysis;
/**
 * @author chenjiyun  2015/12/10
 */
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
import xmlAnalysis.quesObject;


public class xmlTest {
	Document doc;
	public static List<quesObject> xmlList = new ArrayList<quesObject>();//问题链表
	public static quesObject xoj=new quesObject();
	public static commentObj coj=new commentObj();
	public static List<commentObj> cojList=new ArrayList<commentObj>();//每个问题的答案列表
	public static void readXml(Document doc)  {
		/*获取文档树的根结点*/
		Element root = doc.getDocumentElement();
		/*获取所有的问题结点*/
		//Element node = (Element)root.getElementsByTagName("Question").item(0);
		//Element node = (Element)root.getElementsByTagName("Question");
		NodeList quesNode=root.getElementsByTagName("Question");
		int quesNodeLength=quesNode.getLength();
		Element quesBody;
		for(int j=0;j<quesNodeLength;j++){
			quesBody=(Element)(quesNode.item(j));
			xoj.setQID(quesBody.getAttribute("QID"));
			xoj.setQCATEGORY(quesBody.getAttribute("QCATEGORY"));
			xoj.setQBody(quesBody.getElementsByTagName("QBody").item(0).getTextContent());
			//Element commList = (Element) quesBody.getElementsByTagName("Comment");
			NodeList commentNode=quesBody.getElementsByTagName("Comment");
			int commNodeLength=commentNode.getLength();
			Element commBody;
			//Element customerNode =(Element) quesBody.getElementsByTagName("Comment").item(0);
			for(int k=0;k<commNodeLength;k++){
				commBody=(Element)(commentNode.item(k));
				coj.setCID(commBody.getAttribute("CID"));
				coj.setCBody(commBody.getElementsByTagName("CBody").item(0).getTextContent());
				cojList.add(coj);
			}
			xoj.setComments(cojList);
			xmlList.add(xoj);
		}
		System.out.println(xmlList.get(0).getQBody()+"\n");
//		xoj.setQID(node.getAttribute("QID"));
//		xoj.setQCATEGORY(node.getAttribute("QCATEGORY"));
//		xoj.setQBody(node.getElementsByTagName("QBody").item(0).getTextContent());
//		Element customerNode =(Element) node.getElementsByTagName("Comment").item(0);
//		coj.setCID(customerNode.getAttribute("CID"));
//		coj.setCBody(customerNode.getElementsByTagName("CBody").item(0).getTextContent());
//		cojList.add(coj);
//		xoj.setComments(cojList);
//		xmlList.add(xoj);
		//System.out.println(xmlList.get(0).getQBody()+"\n");
		//System.out.println(xoj.getQID()+" "+xoj.getQCATEGORY()+" "+xoj.getQBody()+"\n"+xoj.getCID()+" "+xoj.getCBody());
	}
	public static void main(String[] args) throws TransformerException {
		try {  
            //得到DOM解析器的工厂实例  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            //从DOM工厂中获得DOM解析器  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            String xmlFile = "./xmlFiles/dev.xml";  
            Document doc = db.parse(xmlFile);  
            readXml(doc);
		 } catch (ParserConfigurationException e) {  
	            e.printStackTrace();  
	        } catch (SAXException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	 }
}

