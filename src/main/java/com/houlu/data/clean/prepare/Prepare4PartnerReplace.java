package com.houlu.data.clean.prepare;


import com.houlu.data.clean.utils.AuditRuleHelper;
import com.houlu.data.clean.utils.Tokenizer;

public class Prepare4PartnerReplace {

  public static void main(String[] args) {
    String content = "北京{租房网}海量北租房信息-赶集网";
    String keyWord = "平乐园租房信息";
    String replaceStr = AuditRuleHelper.replaceAuditPart(content,keyWord);
    System.out.println(content);
    System.out.println(replaceStr);
    System.out.println(Tokenizer.apply(replaceStr));
    System.out.println("京平");
  }

}
