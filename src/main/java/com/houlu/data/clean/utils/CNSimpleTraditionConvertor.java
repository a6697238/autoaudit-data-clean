package com.houlu.data.clean.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CNSimpleTraditionConvertor {
  protected static final Logger LOG = LoggerFactory.getLogger(CNSimpleTraditionConvertor.class);

  private static Dict dictSimpleToTradition;
  private static Dict dictTraditionToSimple;
  private static AtomicBoolean isDictLoad = new AtomicBoolean(false);

  private static final String kSimpleToTraditionChar = "simp_to_trad_characters.txt";
  private static final String kSimpleToTraditionPhrase = "simp_to_trad_phrases.txt";
  private static final String kTraditionToSimpleChar = "trad_to_simp_characters.txt";
  private static final String kTraditionToSimplePhrase = "trad_to_simp_phrases.txt";

  private static final String kDictDir = "/apps/IdeaWorkSpace/autoaudit-data-clean/src/main/resources/cn_convert_dict";

  public static synchronized void LoadDict() throws RuntimeException {
    if (isDictLoad.get())
      return;
//    ClassPathResource cpr = null;
    InputStream stream = null;
    BufferedReader charReader = null;
    BufferedReader phraseReader = null;
    try {
//      cpr = new ClassPathResource(kDictDir + "/" + kSimpleToTraditionChar);
//      stream = cpr.getInputStream();
      charReader = new BufferedReader(new InputStreamReader(new FileInputStream(kDictDir + "/" + kSimpleToTraditionChar), "UTF-8"));
//      cpr = new ClassPathResource(kDictDir + "/" + kSimpleToTraditionPhrase);
//      stream = cpr.getInputStream();
      phraseReader = new BufferedReader(new InputStreamReader(new FileInputStream(kDictDir + "/" + kSimpleToTraditionPhrase), "UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException("Load CNSimpleTraditionConvertor dict fail! " + e.getMessage());
    }
    dictSimpleToTradition = new Dict();
    if (!dictSimpleToTradition.LoadDict(charReader, phraseReader)) {
      dictSimpleToTradition = null;
      dictTraditionToSimple = null;
      throw new RuntimeException("Load CNSimpleTraditionConvertor dict fail! ");
    }

    stream = null;
    charReader = null;
    phraseReader = null;
    try {
//      cpr = new ClassPathResource(kDictDir + "/" + kTraditionToSimpleChar);
//      stream = cpr.getInputStream();
      charReader = new BufferedReader(new InputStreamReader(new FileInputStream(kDictDir + "/" + kTraditionToSimpleChar), "UTF-8"));
//      cpr = new ClassPathResource(kDictDir + "/" + kTraditionToSimplePhrase);
//      stream = cpr.getInputStream();
      phraseReader = new BufferedReader(new InputStreamReader(new FileInputStream(kDictDir + "/" + kTraditionToSimplePhrase), "UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException("load CNSimpleTraditionConvertor dict fail! " + e.getMessage());
    }
    dictTraditionToSimple = new Dict();
    if (!dictTraditionToSimple.LoadDict(charReader, phraseReader)) {
      dictSimpleToTradition = null;
      dictTraditionToSimple = null;
      throw new RuntimeException("load CNSimpleTraditionConvertor dict fail! ");
    }

    isDictLoad.set(true);
    LOG.info("CNSimpleTraditionConvertor load success!");
  }
  
  /**
   * @param src
   * @return
   * @throws RuntimeException
   * created by Luchenguang on 2016年6月27日 下午5:34:56
   */
  public static boolean convertToZhs(StringBuffer src) throws RuntimeException {
    if (!isDictLoad.get())
      LoadDict();
    if (src == null || src.length() == 0)
      return false;
    return dictTraditionToSimple.convertByChar(src);
  }
  
  /**
   * 
   * @param src
   * @return
   * @throws RuntimeException
   * created by Luchenguang on 2016年6月27日 下午6:01:14
   */
  public static boolean convertToZht(StringBuffer src) throws RuntimeException {
    if (!isDictLoad.get())
      LoadDict();
    if (src == null || src.length() == 0)
      return false;
    return dictSimpleToTradition.convertByChar(src);
  }
}

/*
 * class FileUtil {
 * 
 * public static LinkedHashMap<String, String> readDict(BufferedReader reader) {
 * LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); String s; String[]
 * array; try { while ((s = reader.readLine()) != null) { array = s.split("\t"); if (array == null
 * || array.length < 2) continue; map.put(array[0], array[1].split(" ")[0]); } } catch (IOException
 * e) { e.printStackTrace(); return null; } return map; } }
 */

class Dict {
  protected static final Logger LOG = LoggerFactory.getLogger(Dict.class);
  private Map<String, String> dictPhrase, dictChar; // static dictionary map

  public Dict() {}

  public boolean LoadDict(BufferedReader charReader, BufferedReader phraseReader) {
    if (charReader == null || phraseReader == null) {
      return false;
    }
    dictChar = readDict(charReader);
    dictPhrase = readDict(phraseReader);
    return (dictChar != null && dictPhrase != null);
  }

  /**
   * Convert the source
   */
  public boolean convert(StringBuffer src) {
    // return if source is empty
    if (src == null || src.length() == 0) {
      System.out.println("missing src");
      return false;
    }

    if (dictPhrase != null && dictPhrase.size() > 0) {
      map(src, dictPhrase);
    } else {
      System.out.println("missing dictPhrase");
      return false;
    }

    if (dictChar != null && dictChar.size() > 0) {
      map(src, dictChar);
    } else {
      System.out.println("missing dictChar");
      return false;
    }
    return true;
  }
  
  public boolean convertByChar(StringBuffer src) {
    if (src == null || src.length() == 0) {
      System.out.println("missing src");
      return false;
    }
    if (dictChar != null && dictChar.size() > 0) {
      mapByStr(src, dictChar);
    } else {
      System.out.println("missing dictChar");
      return false;
    }
    return true;
  }

  /**
   * Map the source with dictionary
   * 
   * @param src
   * @param dict
   */
  private static void map(StringBuffer src, Map<String, String> dict) {
    String key, value;
    int idx, pos, len;
    Iterator<String> it = dict.keySet().iterator();
    while (it.hasNext()) {
      key = it.next();
      pos = 0;
      while ((idx = src.indexOf(key, pos)) > -1) {
        value = dict.get(key);
        len = value.length();
        // log(key + " -> " + value + ", idx: " + idx + ", len: " +
        // len);
        src.replace(idx, idx + len, value);
        pos = idx + len;
      }
    }
    it = null;
  }
  
  /**
   * 将遍历map改为遍历字符串，使算法复杂度降为n
   * @param src
   * @param dict
   * created by Luchenguang on 2016年6月27日 下午5:32:25
   */
  private static void mapByStr(StringBuffer src, Map<String, String> dict) {
    for (int i = 0; i < src.length(); i++) {
      String str = String.valueOf(src.charAt(i));
      String afterStr = dict.get(str);
      if (afterStr != null) {
        src.replace(i, i + 1, afterStr);
      }
    }
  }

  public static LinkedHashMap<String, String> readDict(BufferedReader reader) {
    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    String s;
    String[] array;
    try {
      while ((s = reader.readLine()) != null) {
        array = s.split("\t");
        if (array == null || array.length < 2)
          continue;
        map.put(array[0], array[1].split(" ")[0]);
      }
    } catch (IOException e) {
      LOG.error("caught exception when coping file", e);
      return null;
    }
    return map;
  }
}
