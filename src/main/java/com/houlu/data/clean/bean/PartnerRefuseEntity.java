package com.houlu.data.clean.bean;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartnerRefuseEntity {

  private String keyWord;

  private String keyWordId;

  private String userId;

  private String unitId;

  private String ideaId;

  private String hitWords;

  private String ideaTitle;

  private String ideaTitleFull;

  private String ideaContent;

  private String ideaContentFull;

  private String hitBlackCount;

  private String hitBlackContainsCount;

  private String hitBlackSplitCount;

  private String hitBrandCount;

  private String hitBrandContainsCount;

  private String hitBrandSplitCount;

  private String hitCompeteCount;

  private String hitCompeteContainsCount;

  private String hitCompeteSplitCount;

  private String partnerHitCount;

}
