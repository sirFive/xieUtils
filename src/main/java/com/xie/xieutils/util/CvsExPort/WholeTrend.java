package com.xie.xieutils.util.CvsExPort;

import java.io.Serializable;
import java.util.Date;

/**
 * 整体趋势统计entity
 */
public class WholeTrend implements Serializable {

    private static final long serialVersionUID = -7716076801096744660L;

    /**
     * id
     */
    private Integer trendId;

    /**
     * 今日新增设备
     */
    private Integer devAdd;

    /**
     * 今日新增设备相比昨天百分比
     */
    private Double devAddPercent;

    /**
     * 今日在线设备
     */
    private Integer devLine;

    /**
     * 今日在线设备相比昨天百分比
     */
    private Double devLinePercent;

    /**
     * 累计设备数
     */
    private Integer devCount;

    /**
     * 今日新增用户数
     */
    private Integer userAdd;

    /**
     * 今日新增用户相比昨天百分比
     */
    private Double userAddPercent;

    /**
     * 累计用户数
     */
    private Integer userCount;

    /**
     * 统计日期
     */
    private Date statisticsDate;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getTrendId() {
        return trendId;
    }

    public void setTrendId(Integer trendId) {
        this.trendId = trendId;
    }

    public Integer getDevAdd() {
        return devAdd;
    }

    public void setDevAdd(Integer devAdd) {
        this.devAdd = devAdd;
    }

    public Double getDevAddPercent() {
        return devAddPercent;
    }

    public void setDevAddPercent(Double devAddPercent) {
        this.devAddPercent = devAddPercent;
    }

    public Integer getDevLine() {
        return devLine;
    }

    public void setDevLine(Integer devLine) {
        this.devLine = devLine;
    }

    public Double getDevLinePercent() {
        return devLinePercent;
    }

    public void setDevLinePercent(Double devLinePercent) {
        this.devLinePercent = devLinePercent;
    }

    public Integer getDevCount() {
        return devCount;
    }

    public void setDevCount(Integer devCount) {
        this.devCount = devCount;
    }

    public Integer getUserAdd() {
        return userAdd;
    }

    public void setUserAdd(Integer userAdd) {
        this.userAdd = userAdd;
    }

    public Double getUserAddPercent() {
        return userAddPercent;
    }

    public void setUserAddPercent(Double userAddPercent) {
        this.userAddPercent = userAddPercent;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Date getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(Date statisticsDate) {
        this.statisticsDate = statisticsDate;
    }
}
