package com.houlu.data.clean.prepare;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.houlu.data.clean.bean.HuichuanEntity;
import com.houlu.data.clean.bean.IdeaRefuseEntity;
import com.houlu.data.clean.utils.GameCredentialUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

/**
 * 将初始数据转换成CSV
 */
public class HuichuanGameCertification {

  private static String[] TITLE = new String[]{"id", "userid", "user_name", "industry1", "title",
      "content", "state", "state_auto", "state_url", "state_manual", "reason_auto", "reason_url",
      "reason_manual"};


  private static Integer readCounter = 0;
  private static Integer passCounter = 0;
  private static Integer refuseCounter = 0;
  private static Integer hasGameCounter = 0;
  private static Long withoutGameCounter = 0L;

  private static Set<String> imageSet = Sets.newHashSet();


  private static Integer lineBuffer = 10000;
  private static Integer allReadCounter = 0;
  private static Integer sampleIntever = 100;


  private static BlockingQueue<List<IdeaRefuseEntity>> readQueue = new LinkedBlockingQueue<>();


  public static void main(String[] args) throws Exception {
    String readFileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/huichuan_game_idea.csv";
    File file = new File(readFileName);
    BufferedReader br = null;
    CSVParser csvFileParser = null;
    GameCredentialUtils.init();
    String s = null;

    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(TITLE);
    br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//解决乱码问题
    csvFileParser = new CSVParser(br, csvFileFormat);
    List<CSVRecord> csvRecords = csvFileParser.getRecords();
    for (CSVRecord csvRecord : csvRecords) {
      try {
        String contentStr = csvRecord.get("content");
        String huichuanWord = "";
        String manualState = csvRecord.get("state_manual");
        if ("0".equals(manualState)) {
          passCounter++;
        }
        if("2".equals(manualState)){
          refuseCounter++;
        }

        boolean hasGame = false;
        huichuanWord = csvRecord.get("title");
        if (StringUtils.isNotEmpty(contentStr)) {
          HuichuanEntity huichuanEntity = JSON.parseObject(contentStr, HuichuanEntity.class);
          imageSet.add(huichuanEntity.getImg_1());
          imageSet.add(huichuanEntity.getImg_2());
          imageSet.add(huichuanEntity.getImg_3());
          huichuanWord = huichuanWord + huichuanEntity.getSource() + huichuanEntity.getTitle();
        }
        for (String game : GameCredentialUtils.gameSet) {
          if (huichuanWord.contains(game)) {
            hasGame = true;
            hasGameCounter++;
            break;
          }
        }
        if(!hasGame){
          withoutGameCounter++;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("has game " + hasGameCounter);
      System.out.println("without game " + withoutGameCounter);
      System.out.println("pass counter " + passCounter);
      System.out.println("refuse counter " + refuseCounter);
      System.out.println("image url " + imageSet.size());

    }


  }
}
