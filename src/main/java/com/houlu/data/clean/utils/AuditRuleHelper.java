package com.houlu.data.clean.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditRuleHelper {

  protected static final Logger LOG = LoggerFactory.getLogger(AuditRuleHelper.class);

  /**
   * 格式化物料文本
   *
   * @return created by Luchenguang on 2016年6月14日 上午10:30:45
   */
  public static String formatCpcText(String text) {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    // 1.全角转半角
    String halfString = null;
    try {
      halfString = FullCharConvertor.full2HalfChange(text);
    } catch (UnsupportedEncodingException e) {
      LOG.error("全角半角转换异常", e);
    }
    if (!StringUtils.isBlank(halfString)) {
      text = halfString;
    }
    // 2.去掉特殊符号
    String afterTrans = AutoauditStringUtils.rmSpecialChars(text,
        AutoAuditDictConstants.RS_SET);
    // 3.大写转小写
    afterTrans = afterTrans.toLowerCase();
    // 4.繁体转简体
    StringBuffer sb = new StringBuffer(afterTrans);
    CNSimpleTraditionConvertor.convertToZhs(sb);
    String simpleStr = sb.toString();

    return simpleStr;

  }

  public static String replaceAuditPart(String originalText, String replaceText) {
    String regex = AutoAuditDictConstants.PARTNER_REGEX;
    originalText = formatCpcText(originalText);
    replaceText = formatCpcText(replaceText);
    return AutoauditStringUtils.replaceAllEscapseMark(originalText, regex, replaceText);
  }
}
