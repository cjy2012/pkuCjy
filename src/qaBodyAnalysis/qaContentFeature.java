package qaBodyAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xmlAnalysis.quesObject;

public class qaContentFeature {
	public qaContentFeature(List<quesObject> ql) {
		int qlLength = ql.size();
		System.out.println("问题的数量：" + qlLength);
		for (int j = 0; j < qlLength; j++) {
			int qsNum = ql.get(j).quesNums;
			String[] quesWords = ql.get(j).quesWords;
			int commListLength = ql.get(j).getComments().size();
			/* 计算问题的词频 */
			Map<String, Integer> idfMap = new HashMap<String, Integer>();
			for (String qesst : quesWords) {
				int wordCount = 0;
				if (idfMap.containsKey(qesst)) {
					idfMap.put(qesst, idfMap.get(qesst) + 1);
				} else {
					wordCount++;
					idfMap.put(qesst, wordCount);
				}
			}
			Iterator quesIt = idfMap.entrySet().iterator();
			int sumQues = 0;
			List<Integer> intQues = new ArrayList<Integer>();
			while (quesIt.hasNext()) {
				Map.Entry entry = (Entry) quesIt.next();// 映射项（键-值对）。
				intQues.add((Integer) entry.getValue());
				sumQues += (Integer) entry.getValue()
						* (Integer) entry.getValue();
			}
			for (int k = 0; k < commListLength; k++) {
				int sameWords = 0;
				/* 第一个特征：词数之比 */
				int cmNum = ql.get(j).getComments().get(k).commNums;
				String[] cmWords = ql.get(j).getComments().get(k).commWords;
				ql.get(j).getComments().get(k).commFeatures[0] = (double) cmNum
						/ (cmNum + qsNum);
				/* 第二个特征：问题与答案共同词占问题次数的比例 */
				for (int x = 0; x < quesWords.length; x++) {
					if (contains(cmWords, quesWords[x])) {
						sameWords++;
					} else {
						continue;
					}
				}
				ql.get(j).getComments().get(k).commFeatures[1] = (double) sameWords
						/ (quesWords.length);
				/* 第三个特征：计算问题与答案的cosine相似度 */
				Map<String, Integer> idfContentMap = new HashMap<String, Integer>();
				for (String content : cmWords) {
					int idfConCount = 0;
					if (idfContentMap.containsKey(content)) {
						idfContentMap.put(content,
								idfContentMap.get(content) + 1);
					} else {
						idfConCount++;
						idfContentMap.put(content, idfConCount);
					}
				}
				/* 计算两个向量的余弦相似度 */
				Iterator conIt = idfContentMap.entrySet().iterator();
				int sumCon = 0;
				List<Integer> intCon = new ArrayList<Integer>();
				while (conIt.hasNext()) {
					Map.Entry entryCon = (Entry) conIt.next();// 映射项（键-值对）。
					intCon.add((Integer) entryCon.getValue());
					sumCon += (Integer) entryCon.getValue()*(Integer) entryCon.getValue();
				}
				int sumCombine = 0;
				int sumaryTotal = Math.min(intQues.size(), intCon.size());
				for (int xyz = 0; xyz < sumaryTotal; xyz++) {
					sumCombine += intQues.get(xyz) * intCon.get(xyz);
				}
				ql.get(j).getComments().get(k).commFeatures[2] = (double) sumCombine
						/ (Math.sqrt(sumQues) * Math.sqrt(sumCon));
			}
		}
	}

	public static boolean contains(String[] stringArray, String source) {
		// 转换为list
		List<String> tempList = Arrays.asList(stringArray);
		if (tempList.contains(source)) {
			return true;
		} else {
			return false;
		}
	}
}
