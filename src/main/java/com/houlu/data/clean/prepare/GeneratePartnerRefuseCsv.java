package com.houlu.data.clean.prepare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.houlu.data.clean.bean.HitEntity;
import com.houlu.data.clean.bean.PartnerRefuseEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * 将初始数据转换成CSV
 */
public class GeneratePartnerRefuseCsv {

//  private static String[] TITLE = new String[]{"keyWordId", "keyWord", "userId", "ideaId",
//      "ideaTitle", "ideaContent", "isFalse", "hitBlack", "hitBrand", "hitCompete", "hitExclude",
//      "hitSensitive", "hitSimilar","hitWords"};

  private static String[] TITLE = new String[]{"keyWord", "ideaId", "ideaTitle", "ideaContent",
      "ideaTitleFull",
      "ideaContentFull", "hitBlackCount", "hitBlackContainsCount", "hitBlackSplitCount",
      "hitBrandCount", "hitBrandContainsCount", "hitBrandSplitCount",
      "hitCompeteCount", "hitCompeteContainsCount", "hitCompeteSplitCount", "hitWords","ideaContain"};

  private static int lineCount = 0;

  public static void main(String[] args) throws IOException {
    writeCSV(txt2Entity("/apps/IdeaWorkSpace/text-utils/res_all.txt"));
//      System.out.println(Arrays.toString("1:码特:特码;".split(";")));
  }

  public static List<PartnerRefuseEntity> txt2Entity(String filePath) throws IOException {
    File file = new File(filePath);
    List<PartnerRefuseEntity> partnerRefuseEntityList = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;
    String ideaId = "";
    String keyWordId = "";

    while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
      try {
        if (lineCount > 5000000) {
          break;
        }

        PartnerRefuseEntity partnerRefuseEntity = new PartnerRefuseEntity();
        String[] recordArray = s.split("\t");
        //记录ID号
        ideaId = recordArray[5];
        keyWordId = recordArray[1];


        partnerRefuseEntity.setKeyWord(recordArray[2]);
        partnerRefuseEntity.setIdeaId(recordArray[5]);
        partnerRefuseEntity.setIdeaTitle(recordArray[6]);
        partnerRefuseEntity.setIdeaContent(recordArray[7]);
        partnerRefuseEntity.setHitBlackCount("0");
        partnerRefuseEntity.setHitBlackContainsCount("0");
        partnerRefuseEntity.setHitBlackSplitCount("0");
        partnerRefuseEntity.setHitCompeteCount("0");
        partnerRefuseEntity.setHitCompeteContainsCount("0");
        partnerRefuseEntity.setHitCompeteSplitCount("0");
        partnerRefuseEntity.setHitBrandCount("0");
        partnerRefuseEntity.setHitBrandContainsCount("0");
        partnerRefuseEntity.setHitBrandSplitCount("0");
        partnerRefuseEntity = analyzeEntity(recordArray[0], partnerRefuseEntity);
        partnerRefuseEntityList.add(partnerRefuseEntity);
        if (partnerRefuseEntityList.size() % 1000 == 0) {
          System.out.println(partnerRefuseEntityList.size());
        }
        lineCount++;
      } catch (JSONException e) {
        e.printStackTrace();
        System.out.println(String.format("idea id is %s, keyWord id is %s",ideaId,keyWordId));
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(String.format("idea id is %s, keyWord id is %s",ideaId,keyWordId));
      }
    }
    br.close();
    return partnerRefuseEntityList;
  }

  public static PartnerRefuseEntity analyzeEntity(String str,
      PartnerRefuseEntity partnerRefuseEntity) {
    List<HitEntity> hitEntityList = JSON.parseArray(str, HitEntity.class);
    StringBuilder sb = new StringBuilder();
    for (HitEntity hitEntity : hitEntityList) {
      if ("1".equals(hitEntity.getWordType())) {
        hitBlackCountAdd(partnerRefuseEntity);
        if (hitEntity.getWord().contains(" ")) {
          hitBlackContainsCountAdd(partnerRefuseEntity);
        } else {
          hitBlackSplitCountAdd(partnerRefuseEntity);
        }
      } else if ("2".equals(hitEntity.getWordType())) {
        hitBrandCountAdd(partnerRefuseEntity);
        if (hitEntity.getWord().contains(" ")) {
          hitBrandContainsCountAdd(partnerRefuseEntity);
        } else {
          hitBrandSplitCountAdd(partnerRefuseEntity);
        }
      } else if ("3".equals(hitEntity.getWordType())) {
        hitCompeteCountAdd(partnerRefuseEntity);
        if (hitEntity.getWord().contains(" ")) {
          hitCompeteContainsCountAdd(partnerRefuseEntity);
        } else {
          hitCompeteSplitCountAdd(partnerRefuseEntity);
        }
      }
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(hitEntity.getWord());
    }

    if(partnerRefuseEntity.getIdeaTitle().contains(partnerRefuseEntity.getKeyWord())){
      partnerRefuseEntity.setIdeaContain("1");
    }else if(partnerRefuseEntity.getIdeaContent().contains(partnerRefuseEntity.getKeyWord())){
      partnerRefuseEntity.setIdeaContain("1");
    }else {
      partnerRefuseEntity.setIdeaContain("0");
    }

    partnerRefuseEntity.setHitWords(sb.toString());
    return partnerRefuseEntity;
  }


  public static void writeCSV(List<PartnerRefuseEntity> partnerRefuseEntityList) {
    CSVFormat format = CSVFormat.DEFAULT.withHeader(TITLE).withSkipHeaderRecord(false);
    int writeCount = 0;
    try (Writer out = new FileWriter("/apps/IdeaWorkSpace/text-utils/res_all.csv");
        CSVPrinter printer = new CSVPrinter(out, format)) {
      for (PartnerRefuseEntity partnerRefuseEntity : partnerRefuseEntityList) {
        List<String> records = new ArrayList<>();
        records.add(partnerRefuseEntity.getKeyWord());
        records.add(partnerRefuseEntity.getIdeaId());
        records.add(partnerRefuseEntity.getIdeaTitle());
        records.add(partnerRefuseEntity.getIdeaContent());
        records.add(partnerRefuseEntity.getIdeaTitleFull());
        records.add(partnerRefuseEntity.getIdeaContentFull());
        records.add(partnerRefuseEntity.getHitBlackCount());
        records.add(partnerRefuseEntity.getHitBlackContainsCount());
        records.add(partnerRefuseEntity.getHitBlackSplitCount());
        records.add(partnerRefuseEntity.getHitBrandCount());
        records.add(partnerRefuseEntity.getHitBrandContainsCount());
        records.add(partnerRefuseEntity.getHitBrandSplitCount());
        records.add(partnerRefuseEntity.getHitCompeteCount());
        records.add(partnerRefuseEntity.getHitCompeteContainsCount());
        records.add(partnerRefuseEntity.getHitCompeteSplitCount());
        records.add(partnerRefuseEntity.getHitWords());
        records.add(partnerRefuseEntity.getIdeaContain());
        printer.printRecord(records);
        System.out.println("write count is : " + (writeCount++));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void hitBlackCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBlackCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBlackCount()) + 1));
  }

  public static void hitBlackContainsCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBlackContainsCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBlackContainsCount()) + 1));
  }

  public static void hitBlackSplitCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBlackSplitCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBlackSplitCount()) + 1));
  }


  public static void hitBrandCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBrandCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBrandCount()) + 1));
  }

  public static void hitBrandContainsCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBrandContainsCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBrandContainsCount()) + 1));
  }

  public static void hitBrandSplitCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitBrandSplitCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitBrandSplitCount()) + 1));
  }


  public static void hitCompeteCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitCompeteCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitCompeteCount()) + 1));
  }

  public static void hitCompeteContainsCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitCompeteContainsCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitCompeteContainsCount()) + 1));
  }

  public static void hitCompeteSplitCountAdd(PartnerRefuseEntity partnerRefuseEntity) {
    partnerRefuseEntity.setHitCompeteSplitCount(
        String.valueOf(Integer.parseInt(partnerRefuseEntity.getHitCompeteSplitCount()) + 1));
  }

}
