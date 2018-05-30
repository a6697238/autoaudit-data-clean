package com.houlu.data.clean.prepare;

import com.alibaba.fastjson.JSON;
import com.houlu.data.clean.bean.HitEntity;
import com.houlu.data.clean.bean.IdeaRefuseEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * 将初始数据转换成CSV
 */
public class SampleRecallLog {

  private static String[] TITLE = new String[]{"hit_word", "word_type", "idea_content"};

  private static Integer readCounter = 0;
  private static Integer writeCounter = 0;
  private static Integer lineBuffer = 10000;
  private static Integer allReadCounter = 0;
  private static Integer sampleIntever = 100;


  private static BlockingQueue<List<IdeaRefuseEntity>> readQueue = new LinkedBlockingQueue<>();


  public static void main(String[] args) throws Exception {
    String readFileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/recall_increase_20180523.log";
    sampleFile(readFileName);
//    readFileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/recall_instance_20180521.log";
//    sampleFile(readFileName);
  }

  public static void sampleFile(String fileName) throws Exception {
    File file = new File(fileName);
    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;
    Map<String, String> textMap = new HashMap<>();

    while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
      try {
        readCounter++;
        if (readCounter % sampleIntever == 0) {
//          String[] array = s.split("userid:");
//          String unitId = array[1].split(",")[0];
//          textMap.put(unitId, s);
          textMap.put(String.valueOf(writeCounter), s);
          readCounter=0;
          writeCounter++;
          System.out.println(writeCounter);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    br.close();

    file = new File(fileName + ".sample");
    FileWriter fw = new FileWriter(file);
    for (String key : textMap.keySet()) {
      fw.write(textMap.get(key) + "\n");
    }
    fw.flush();
    fw.close();
  }
}
