package com.houlu.data.clean.utils;

import java.io.UnsupportedEncodingException;

public class FullCharConvertor {
  // 全角转半角的 转换函数
  public static final String full2HalfChange(String qjStr) throws UnsupportedEncodingException {
    StringBuffer qj = new StringBuffer(qjStr);
    if (full2HalfChangeInPlace(qj))
      return qj.toString();
    return null;
  }

  // 半角转全角
  public static final String half2FullChange(String bjStr) throws UnsupportedEncodingException {
    StringBuffer qj = new StringBuffer(bjStr);
    if (half2FullChangeInPlace(qj))
      return qj.toString();
    return null;
  }

  public static final boolean full2HalfChangeInPlace(StringBuffer qj) {
    final int QJ_MASK = 0xFF00;
    for (int i = 0; i < qj.length(); i++) {
      // 全角空格转换成半角空格
      int v = qj.codePointAt(i);
      if (v == 0x3000) {
        qj.setCharAt(i, ' ');
        continue;
      }

      int judge = (QJ_MASK & v);
      if (judge == QJ_MASK) {
        char[] chars = Character.toChars(v - 0xFEE0);
        if (chars.length != 1) {
          System.out.println("erro qj code: " + qj.toString());
          return false;
        }
        qj.setCharAt(i, chars[0]);
      }
    } // end for.
    return true;
  }

  public static boolean half2FullChangeInPlace(StringBuffer qj) {
    for (int i = 0; i < qj.length(); i++) {
      // ban角空格转换成quan角空格
      int v = qj.codePointAt(i);
      if (v == 0x0020) {
        qj.setCharAt(i, '　');
        continue;
      }
      if (v < 0x007F && v > 0x0020) {
        char[] chars = Character.toChars(v + 0xFEE0);
        if (chars.length != 1) {
          System.out.println("erro qj code: " + qj.toString());
          return false;
        }
        qj.setCharAt(i, chars[0]);
      }
    } // end for.
    return true;
  }
  
  public static void main(String args[]) throws UnsupportedEncodingException{
    String str = "~`!！@#$%^&*()（）[]【】\\|_——+=-;；'’/.。,，:：\"”“?？><《》";
    Long t1 = System.currentTimeMillis();
    for (int i =0 ; i < 100000; i++){
      String str2 = full2HalfChange(str);
      //System.out.println(str2);
    }
    Long t2 = System.currentTimeMillis();
    System.out.println(t2-t1);
  }
}
