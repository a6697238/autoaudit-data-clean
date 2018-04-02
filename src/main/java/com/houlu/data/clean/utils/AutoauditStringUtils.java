package com.houlu.data.clean.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class AutoauditStringUtils {

	/**
	 * 根据minlen和maxlen，对word进行切词
	 * 
	 * @param word
	 * @param minlen
	 * @param maxlen
	 * @return
	 */
	private static List<String> splitWord(String word, int minlen, int maxlen) {
		List<String> list = new LinkedList<String>();
		if (word == null)
			return list;
		if (word.length() <= minlen) {
			list.add(word);
			return list;
		}
		maxlen = maxlen > word.length() ? word.length() : maxlen;
		for (int i = minlen; i <= maxlen; i++) {
			// List<String> sub = new LinkedList<String>();
			for (int j = 0; j + i <= word.length(); j++) {
				// sub.add(word.substring(j, j + i));
				list.add(new String(word.substring(j, j + i)));
			}
			// list.addAll(sub);
		}
		return list;
	}

	/**
	 * 将specialChars全部替换为""
	 * 
	 * @param word
	 * @param specialChars
	 * @return
	 */
/*	@Deprecated
	public static String rmSpecialChars(String word, Set<Character> specialChars) {
		if (word == null || specialChars == null || specialChars.size() == 0)
			return word;
		String rs = word;
		for (Character sc : specialChars) {
			rs = rs.replace(sc.toString(), "");
		}
		return rs;
	}*/

  /**
   * 将specialChars全部替换为"", 性能优化版
   *
   * @param word
   * @param specialChars
   * @return
   */
	public static String rmSpecialChars(String word, Set<Character> specialChars){
    if (StringUtils.isEmpty(word) || CollectionUtils.isEmpty(specialChars)) {
      return word;
    }
    char[] wordChars = word.toCharArray();
    char[] rmChars = new char[wordChars.length];
    int i = 0;
    for (char aChar : wordChars) {
      if (specialChars.contains(aChar)) {
        continue;
      }
      rmChars[i] = aChar;
      i++;
    }
    return new String(rmChars).trim();
	}

	/**
	 * 判断source是否包含了comparator
	 * 
	 * @author gaoyibo
	 * @param source
	 * @param comparator
	 * @return
	 */
	private static int[] compareWord(
			LinkedHashMap<String, ArrayList<Integer>> source,
			LinkedHashMap<String, Integer> comparator) {
		int[] rev = new int[2];
		ArrayList<Integer> ind1 = null;
		ArrayList<Integer> ind2 = null;
		boolean span = false;
		for (String s1 : comparator.keySet()) {
			String s2 = s1.length() > 1 ? s1.substring(0, 1) : s1;
			if (s2.equals("%")) {
				span = true;
				continue;
			}
			if (ind1 == null) {
				ind1 = source.get(s2);
				if (ind1 == null)
					return null;
				rev[0] = ind1.get(0) - 1;
			} else {
				ind2 = source.get(s2);
				if (ind2 == null)
					return null;
			}
			if (ind1 != null && ind2 != null) {
				ArrayList<Integer> res = match(ind1, ind2, span);
				if (res.size() > 0) {
					ind1 = res;
					ind2 = null;
					span = false;
					continue;
				} else {
					return null;
				}
			}
			span = false;
		}
		rev[1] = ind1.get(0);
		return rev;
	}

	/**
	 * 每个字符对应位置的匹配。
	 * 
	 * @author gaoyibo
	 * @param ind1
	 * @param ind2
	 * @param span
	 * @return
	 */
	private static ArrayList<Integer> match(ArrayList<Integer> ind1,
			ArrayList<Integer> ind2, boolean span) {
		ArrayList<Integer> rev = new ArrayList<Integer>();
		int min = ind1.get(0);
		int max = ind1.get(ind1.size() - 1);
		// 如果有%,差值>=1
		if (span) {
			for (int i = 0; i < ind2.size(); i++) {
				if (i == 0 && ind2.get(i) > max)
					return ind2;
				if (ind2.get(i) > min) {
					rev.add(ind2.get(i));
				}
			}
		} else {// 如果没有%,差值=1
			for (int i = 0; i < ind2.size(); i++) {
				for (int j = 0; j < ind1.size(); j++) {
					if (ind2.get(i) - ind1.get(j) == 1) {
						rev.add(ind2.get(i));
					}
				}
			}
		}
		return rev;
	}

	/**
	 * 将需要匹配的词切分。
	 * 
	 * @author gaoyibo
	 * @param word
	 * @return
	 */
	public static LinkedHashMap<String, ArrayList<Integer>> splitSourceWord(
			String word) {
		if (word == null || word.trim().length() == 0)
			return null;
		LinkedHashMap<String, ArrayList<Integer>> rev = new LinkedHashMap<String, ArrayList<Integer>>();
		String[] sarr = word.split("");
		for (int i = 1; i < sarr.length; i++) {
			ArrayList<Integer> ind = rev.get(sarr[i]);
			if (ind == null) {
				ind = new ArrayList<Integer>();
				rev.put(sarr[i], ind);
			}
			ind.add(i);
		}
		return rev;
	}

	/**
	 * 将匹配的词切分。
	 * 
	 * @author gaoyibo
	 * @param word
	 * @return
	 */
	public static LinkedHashMap<String, Integer> splitRuleWord(String word) {
		if (word == null || word.trim().length() == 0)
			return null;
		LinkedHashMap<String, Integer> rev = new LinkedHashMap<String, Integer>();
		String[] sarr = word.split("");
		for (int i = 1; i < sarr.length; i++) {
			if (rev.get(sarr[i]) != null) {
				rev.put(sarr[i] + i, i);
			} else {
				rev.put(sarr[i], i);
			}
		}
		return rev;
	}

	public static String convertString(String content) {
		if (content == null)
			return null;

		//无需做全转半、繁转简
//		content = KeyContentFmtProcessor.halfWidth2FullWidth(content);
//		content = KeyContentFmtProcessor.traditional2simplified(content);
//		content = KeyContentFmtProcessor.upper2Lower(content);

		return content;
	}
	
	/**
	 * 得到str中c的个数
	 * @param str
	 * @param c
	 * @return
	 * created by Luchenguang on 2016年8月19日 下午8:58:42
	 */
	public static int countChar(String str, char c){
	  int count = 0;
	  char[] chars = str.toCharArray();
	  for(int i=0;i<chars.length;i++){
	    if(chars[i] == c){
	      count++;
	    }
	  }
	  return count;
	}
	
	/**
	 * 去掉replacement中的特殊符号，在进行replaceAll
	 * @param originalStr
	 * @param regex
	 * @param replacement
	 * @return
	 * created by Luchenguang on 2016年9月2日 下午2:33:51
	 */
	public static String replaceAllEscapseMark(String originalStr, String regex, String replacement){
	  //将\转换为\\,将$转换为\$
      String transReplacement = replacement.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
      return originalStr.replaceAll(regex, transReplacement);
	}

  public static void main(String args[]) {
    // String s1 = "法%轮%大%法";
    // String s2 = "法轮大法";
    // String s1 = "③d";
    // String s2 = "123modi";
    
     String s1 = "法%轮%功"; String s2 = "轮法功轮法功轮";
      
     LinkedHashMap<String, Integer> comparator = splitRuleWord(s1); 
     LinkedHashMap<String, ArrayList<Integer>> source = splitSourceWord(s2); int[] sub_ind = compareWord(source,
     comparator); System.out.println(s2.substring(sub_ind[0], sub_ind[1]));
     System.out.println(countChar("dsfa fdsaf fdsaf ", ' '));
  }

}
