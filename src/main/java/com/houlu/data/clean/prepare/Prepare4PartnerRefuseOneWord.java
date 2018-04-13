package com.houlu.data.clean.prepare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.houlu.data.clean.bean.HitEntity;
import com.houlu.data.clean.bean.PartnerRefuseEntity;
import com.houlu.data.clean.utils.AuditRuleHelper;
import com.houlu.data.clean.utils.Tokenizer;
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
public class Prepare4PartnerRefuseOneWord {

  private static String[] TITLE = new String[]{"keyWord", "keyWordId", "userId", "unitId",
      "ideaId", "hitWords", "ideaTitle", "ideaTitleFull", "ideaContent",
      "ideaContentFull", "hitBlackCount", "hitBlackContainsCount",
      "hitBlackSplitCount", "hitBrandCount", "hitBrandContainsCount", "hitBrandSplitCount",
      "hitCompeteCount", "hitCompeteContainsCount", "hitCompeteSplitCount", "partnerHitCount"};

  private static final int PREPAER_COUNT = 2000000;
  private static final int STEP = 10000;


  private static int readCount = 0;
  private static int analyzeCount = 0;
  private static int writeCount = 0;

  public static void main(String[] args) throws IOException {
    String fileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/res_refuse_one_word.csv";
    writeCSV(fileName, txt2Entity("/apps/IdeaWorkSpace/autoaudit-data-clean/res_refuse_all.txt"));

//    readRecord(new ArrayList<PartnerRefuseEntity>(),"[{\"word\":\"助手\",\"wordType\":1,\"wordId\":-1},{\"word\":\"麻将\",\"wordType\":1,\"wordId\":-1},{\"word\":\"麻将 辅助\",\"wordType\":1,\"wordId\":29922,\"similarWord\":\"麻将 铺助\"}]  1571397 名鹤麻将机      29803322494553534        2708400320      2017{}上牌器安全,{}经典助手更精湛,值得信赖!     【铺助】{}铺助是专门为{}开发的!它可以帮助你轻松嬴!\n");
  }

  public static List<PartnerRefuseEntity> txt2Entity(String filePath) throws IOException {
    File file = new File(filePath);
    List<PartnerRefuseEntity> partnerRefuseEntityList = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;

    while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
      if (analyzeCount > PREPAER_COUNT) {
        break;
      }
      readCount++;
      readRecord(partnerRefuseEntityList, s);
      analyzeCount++;
    }
    br.close();
    return partnerRefuseEntityList;
  }

  public static void readRecord(List<PartnerRefuseEntity> partnerRefuseEntityList, String s) {
    String ideaId = "";
    String keyWordId = "";
    String userId = "";


    try {
      PartnerRefuseEntity partnerRefuseEntity = new PartnerRefuseEntity();
      String[] recordArray = s.split("\t");
      List<HitEntity> hitEntityList = JSON.parseArray(recordArray[0], HitEntity.class);
      for (HitEntity hitEntity : hitEntityList) {
        //记录ID号
        ideaId = recordArray[5];
        keyWordId = recordArray[1];
        userId = recordArray[3];

        partnerRefuseEntity.setKeyWord(recordArray[2]);
        partnerRefuseEntity.setKeyWordId(recordArray[1]);
        partnerRefuseEntity.setIdeaId(recordArray[5]);
        partnerRefuseEntity.setIdeaTitle(recordArray[6]);
        partnerRefuseEntity.setIdeaTitleFull(
            getFullStr(partnerRefuseEntity.getIdeaTitle(), partnerRefuseEntity.getKeyWord()));
        partnerRefuseEntity.setIdeaContent(recordArray[7]);
        partnerRefuseEntity.setIdeaContentFull(
            getFullStr(partnerRefuseEntity.getIdeaContent(), partnerRefuseEntity.getKeyWord()));

        partnerRefuseEntity.setHitBlackCount("0");
        partnerRefuseEntity.setHitBlackContainsCount("0");
        partnerRefuseEntity.setHitBlackSplitCount("0");
        partnerRefuseEntity.setHitCompeteCount("0");
        partnerRefuseEntity.setHitCompeteContainsCount("0");
        partnerRefuseEntity.setHitCompeteSplitCount("0");
        partnerRefuseEntity.setHitBrandCount("0");
        partnerRefuseEntity.setHitBrandContainsCount("0");
        partnerRefuseEntity.setHitBrandSplitCount("0");
        partnerRefuseEntity = setHitCount(hitEntity, partnerRefuseEntity);
        partnerRefuseEntity = setPartnerHitCount(partnerRefuseEntity, hitEntityList);
        partnerRefuseEntityList.add(partnerRefuseEntity);
      }

      if (partnerRefuseEntityList.size() % 1000 == 0) {
        System.out.println("read count is " + partnerRefuseEntityList.size());
      }
    } catch (JSONException e) {
      e.printStackTrace();
      System.out.println(String.format("idea id is %s, keyWord id is %s ", ideaId, keyWordId));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(String.format("idea id is %s, keyWord id is %s", ideaId, keyWordId));
    }
  }



  public static PartnerRefuseEntity setHitCount(HitEntity hitEntity, PartnerRefuseEntity partnerRefuseEntity) {
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
    partnerRefuseEntity.setHitWords(hitEntity.getWord());
    return partnerRefuseEntity;
  }

  public static PartnerRefuseEntity setPartnerHitCount(PartnerRefuseEntity partnerRefuseEntity,
      List<HitEntity> hitWords) {
    String titleStr = "";
    String contentStr = "";
    try {
      int partnerHitCount = 0;
      titleStr = AuditRuleHelper
          .replaceAuditPart(AuditRuleHelper.formatCpcText(partnerRefuseEntity.getIdeaTitle()),
              AuditRuleHelper.formatCpcText(partnerRefuseEntity.getKeyWord()));
      contentStr = AuditRuleHelper
          .replaceAuditPart(AuditRuleHelper.formatCpcText(partnerRefuseEntity.getIdeaContent()),
              AuditRuleHelper.formatCpcText(partnerRefuseEntity.getKeyWord()));

      List<String> titleWords = Tokenizer.apply(AuditRuleHelper.formatCpcText(titleStr));
      List<String> contentWords = Tokenizer.apply(AuditRuleHelper.formatCpcText(contentStr));

      for (HitEntity hitEntity : hitWords) {
        String word = hitEntity.getWord();
        if (word.contains(" ")) {
          continue;
        }
        if (titleWords.contains(word)) {
          partnerHitCount++;
        }
        if (contentWords.contains(word)) {
          partnerHitCount++;
        }
      }
      partnerRefuseEntity.setPartnerHitCount(String.valueOf(partnerHitCount));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return partnerRefuseEntity;
  }


  public static void writeCSV(String fileName, List<PartnerRefuseEntity> partnerRefuseEntityList) {
    CSVFormat format = CSVFormat.DEFAULT.withHeader(TITLE).withSkipHeaderRecord(false);
    int writeCount = 0;
    try (Writer out = new FileWriter(fileName);
        CSVPrinter printer = new CSVPrinter(out, format)) {
      for (PartnerRefuseEntity partnerRefuseEntity : partnerRefuseEntityList) {
        List<String> records = new ArrayList<>();
        records.add(partnerRefuseEntity.getKeyWord());
        records.add(partnerRefuseEntity.getKeyWordId());
        records.add(partnerRefuseEntity.getUserId());
        records.add(partnerRefuseEntity.getUnitId());
        records.add(partnerRefuseEntity.getIdeaId());
        records.add(partnerRefuseEntity.getHitWords());
        records.add(partnerRefuseEntity.getIdeaTitle());
        records.add(partnerRefuseEntity.getIdeaTitleFull());
        records.add(partnerRefuseEntity.getIdeaContent());
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
        records.add(partnerRefuseEntity.getPartnerHitCount());
        printer.printRecord(records);
        System.out.println("write count is : " + (writeCount++));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getFullStr(String str, String keyWord) {
    str = AuditRuleHelper.formatCpcText(str);
    keyWord = AuditRuleHelper.formatCpcText(keyWord);
    return AuditRuleHelper.replaceAuditPart(str, keyWord);
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
