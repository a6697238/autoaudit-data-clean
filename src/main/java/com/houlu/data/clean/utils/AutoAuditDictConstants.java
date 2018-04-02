package com.houlu.data.clean.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AutoAuditDictConstants {

  public static Character[] RS = {' ', '`', '!', '#', '@', '$', '^', '*', '(', ')', '[', ']', '\\',
      '|', '=', ';', '\'', ',', ':', '\"', '?', '【', '】', '。', '‘', '、', '《', '》', '…', '「', '」',
      '—', '“', '”', 'ˇ', '〔', '〕', '～'};
  public static Set<Character> RS_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(RS)));

  public static Character[] RS_NEED_SPLIT = {'-', '.', '&', '%', '/', '+', '_', '~', '>', '<'};
  public static Set<Character> RS_NEED_SPLIT_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(RS_NEED_SPLIT)));

  public static Set<Character> RS_ALL = new HashSet<>(RS_SET);
  static {RS_ALL.addAll(RS_NEED_SPLIT_SET);}

  public static final String WILDCARD_AMPERSAND = ",";

  public static final String SIMILAR_WORD_SPLIT_MARK = "\n";

  public static final String WORD_LIKE_MARK = " ";
  public static final String WORD_LIKE_TRANS_MARK = "ˇ";

  public static final String PARTNER_REGEX = "\\{.*?\\}";
  public static final String PARTNER_MARK = "{";
  public static final String PARTNER_MARK2 = "}";

  public static final String ALL_INDUSTRY = "0";
}
