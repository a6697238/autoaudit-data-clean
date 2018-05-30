package com.houlu.data.clean.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GameCredentialUtils
 *
 * @author hl162981
 * @date 2018/5/28
 */
public class GameCredentialUtils {

  private static Set<String> gameStrSet = new HashSet<>();
  public static Set<String> gameSet = new HashSet<>();


  public static void main(String[] args) throws Exception {
    init();

  }


  public static boolean p1Reg(String str) {
    Pattern p = Pattern.compile("游戏\\s.*资质全");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        String[] tempArray = matcher.group(0).split("资质全");
        tempArray = tempArray[0].split("游戏");
        gameStr = tempArray[1].trim();
        for (String game : gameStr.split(" ")) {
          gameStrSet.add(game);
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p2Reg(String str) {
    Pattern p = Pattern.compile("^主体资质全，单一游戏类资质全，app类资质全，应用商店游戏仅限ios.*");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        gameStr = str.split("app类资质全，应用商店游戏仅限ios")[1];
        gameStr = gameStr.replaceAll("，", " ");
        gameStr = gameStr.trim();
        if (gameStr.contains("（")) {
          gameStrSet.add(gameStr.substring(gameStr.indexOf("（") + 1, gameStr.indexOf("）")));
        } else {
          gameStrSet.add(gameStr);
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p3Reg(String str) {
    Pattern p = Pattern.compile("^主体资质全，单一游戏类资质全，app类资质全.*");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        gameStr = str.split("主体资质全，单一游戏类资质全，app类资质全")[1];
        gameStr = gameStr.replaceAll("，", " ");
        gameStr = gameStr.replaceAll("（", " ");
        gameStr = gameStr.replaceAll("）", " ");
        gameStr = gameStr.replaceAll("、", " ");
        String[] gameStrArray = gameStr.split(" ");
        for (String game : gameStrArray) {
          gameStrSet.add(game.trim());
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p4Reg(String str) {
    Pattern p = Pattern.compile("单一游戏\\S.*资质全");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        gameStr = str.split("单一游戏")[1];
        gameStr = gameStr.replaceAll("，", " ");
        gameStr = gameStr.replaceAll("（", " ");
        gameStr = gameStr.replaceAll("）", " ");
        gameStr = gameStr.replaceAll("、", " ");
        String[] gameStrArray = gameStr.split(" ");
        for (String game : gameStrArray) {
          gameStrSet.add(game.trim());
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p5Reg(String str) {
    Pattern p = Pattern.compile("游戏\\S.*资质全");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        gameStr = str.split("游戏")[1];
        gameStr = gameStr.replaceAll("、", " ");
        gameStr = gameStr.replaceAll("（", " ");
        gameStr = gameStr.replaceAll("）", " ");
        gameStr = gameStr.replaceAll(":", " ");
        String[] gameStrArray = gameStr.split(" ");
        for (String game : gameStrArray) {
          gameStrSet.add(game.trim());
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p6Reg(String str) {
    Pattern p = Pattern.compile("已备案.*");
    Matcher matcher = p.matcher(str);
    String gameStr = "";
    try {
      if (matcher.find()) {
        gameStr = str.split("已备案")[1];
        gameStr = gameStr.replaceAll("、", " ");
        gameStr = gameStr.replaceAll("（", " ");
        gameStr = gameStr.replaceAll("）", " ");
        gameStr = gameStr.replaceAll(":", " ");
        String[] gameStrArray = gameStr.split(" ");
        for (String game : gameStrArray) {
          gameStrSet.add(game.trim());
        }
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean p7Reg(String str) {
//    System.out.println(str);
    return false;
  }

  public static void init() throws Exception {
    String fileName = "/apps/IdeaWorkSpace/autoaudit-data-clean/youxi_comment_data.csv";
    File file = new File(fileName);
    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;

    while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
      String[] array = s.split(",");
      String credential = array[2];
      credential = credential.toLowerCase();
      boolean hasExtract = false;

      if ("主体资质全，游戏平台类资质全".equals(credential) ||
          "主体资质全，游戏平台类资质全；app类资质全".equals(credential) ||
          "应用商店游戏仅可ios".equals(credential) ||
          "应用商店游戏仅限ios".equals(credential)) {
        hasExtract = true;
      }

      if (!hasExtract) {
        hasExtract = p1Reg(credential);
      }
      if (!hasExtract) {
        hasExtract = p2Reg(credential);
      }

      if (!hasExtract) {
        hasExtract = p3Reg(credential);
      }
      if (!hasExtract) {
        hasExtract = p4Reg(credential);
      }
      if (!hasExtract) {
        hasExtract = p5Reg(credential);
      }
      if (!hasExtract) {
        hasExtract = p6Reg(credential);
      }
      if (!hasExtract) {
        hasExtract = p7Reg(credential);
      }
    }

    for (String game : gameStrSet) {
      if (game.contains("资质")
          || game.contains("特批")) {
      } else {
        game = game.replaceAll("：|“|“|（|）|、|:|=", " ");
        for (String str : game.split(" ")) {
          if (str.trim().length() > 2) {
            gameSet.add(str.trim());
            System.out.println(str.trim());
          }
        }
      }
    }


  }
}
