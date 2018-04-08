package com.houlu.data.clean.utils;

import java.util.List;

public class StrTest {

  public static void main(String[] args) {

    String str = "{自动喷漆}邦天机电设备“好顺”自动喷漆,高效的服务、良好的品质、实惠的价格 ,电话:15687091188";
    String key = "墙面喷漆德";
    String hit = "多盈";

    str = AuditRuleHelper.formatCpcText(str);
    key = AuditRuleHelper.formatCpcText(key);
    str = AuditRuleHelper.replaceAuditPart(str,key);

    List<String> words= Tokenizer.apply(str);

    System.out.println(words);

  }

}
