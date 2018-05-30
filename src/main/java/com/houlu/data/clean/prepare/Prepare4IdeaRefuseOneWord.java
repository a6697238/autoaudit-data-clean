package com.houlu.data.clean.prepare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.houlu.data.clean.bean.HitEntity;
import com.houlu.data.clean.bean.IdeaRefuseEntity;
import com.houlu.data.clean.bean.PartnerRefuseEntity;
import com.houlu.data.clean.utils.AuditRuleHelper;
import com.houlu.data.clean.utils.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

/**
 * 将初始数据转换成CSV
 */
public class Prepare4IdeaRefuseOneWord {

  private static String[] TITLE = new String[]{"hit_word", "word_type", "idea_content"};

  private static Integer readCounter = 0;
  private static Integer writeCounter = 0;
  private static Integer lineBuffer = 100000;
  private static Integer allReadCounter = 0;
  private static Integer sampleIntever = 10000;

  private static BlockingQueue<List<IdeaRefuseEntity>> readQueue = new LinkedBlockingQueue<>();


  public static void main(String[] args) throws IOException, InterruptedException {
    String readFileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/idea_refuse_20180516.csv";
    String writFileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/idea_one_sample_word_20180516.csv";

    Thread readThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          txt2Entity(readFileName);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    Thread writeThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            List<IdeaRefuseEntity> ideaRefuseEntityList = readQueue.take();
            writeCSV(writFileName, ideaRefuseEntityList);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    readThread.start();
    Thread.sleep(2000);
    writeThread.start();
    Thread.sleep(1000000000000l);
  }

  public static void txt2Entity(String filePath) throws IOException {
    File file = new File(filePath);
    List<IdeaRefuseEntity> ideaRefuseEntityList = new ArrayList<>();

    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;

    while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
      readRefuseIdeaRecord(ideaRefuseEntityList, s);
      readCounter++;
      if (readCounter % lineBuffer == 0 && readCounter > 0) {
        readQueue.offer(ideaRefuseEntityList);
        ideaRefuseEntityList = new ArrayList<>();
        readCounter = 0;
      }
    }
    if (ideaRefuseEntityList.size() > 0) {
      readQueue.offer(ideaRefuseEntityList);
    }
    br.close();
  }

  public static void readRefuseIdeaRecord(List<IdeaRefuseEntity> ideaRefuseEntityList, String s) {
    try {
      IdeaRefuseEntity ideaRefuseEntity = new IdeaRefuseEntity();
      int end =  s.indexOf("]");
      String jsonStr = s.substring(0,end+1);
      List<HitEntity> hitEntityList = JSON.parseArray(jsonStr, HitEntity.class);
      for (HitEntity hitEntity : hitEntityList) {
        allReadCounter++;
        if(allReadCounter%sampleIntever==0){
          //记录ID号
          ideaRefuseEntity.setHitWord(hitEntity.getWord());
          ideaRefuseEntity.setWordType(hitEntity.getWordType());
          ideaRefuseEntity.setIdeaContent(s.substring(end+1).trim());
          ideaRefuseEntityList.add(ideaRefuseEntity);
        }
      }

      if (allReadCounter % 1000 == 0) {
        System.out.println("read count is " + allReadCounter);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(s);
    }
  }


  public static void writeCSV(String fileName, List<IdeaRefuseEntity> ideaRefuseEntityList) {
    CSVFormat format = null;
    if (writeCounter > 0) {
      format = CSVFormat.DEFAULT.withHeader(TITLE).withSkipHeaderRecord(true);
    } else {
      format = CSVFormat.DEFAULT.withHeader(TITLE).withSkipHeaderRecord(false);
    }
    try (FileWriter fileWriter = new FileWriter(fileName, true);
        CSVPrinter printer = new CSVPrinter(fileWriter, format)) {
      for (IdeaRefuseEntity ideaRefuseEntity : ideaRefuseEntityList) {
        List<String> records = new ArrayList<>();
        records.add(ideaRefuseEntity.getHitWord());
        records.add(ideaRefuseEntity.getWordType());
        records.add(ideaRefuseEntity.getIdeaContent());
        printer.printRecord(records);
        System.out.println("write count is : " + (writeCounter++));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
