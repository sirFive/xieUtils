package com.xie.xieutils.util.CvsExPort;


import java.util.Date;

/**
 * wholeTrend需要导出的字段
 */
public class WholeTrendExportVo {

    /**
     * 今日新增设备
     */
    @ExportAnnotation(order = 0,method = "getDevAdd",columnTitle = "新增设备")
    private Integer devAdd;

    /**
     * 今日在线设备
     */
    @ExportAnnotation(order = 1,method = "getDevLine",columnTitle = "在线设备")
    private Integer devLine;

    /**
     * 累计设备数
     */
    @ExportAnnotation(order = 2,method = "getDevCount",columnTitle = "累计设备")
    private Integer devCount;

    /**
     * 今日新增用户数
     */
    @ExportAnnotation(order = 3,method = "getUserAdd",columnTitle = "新增用户")
    private Integer userAdd;

    /**
     * 累计用户数
     */
    @ExportAnnotation(order = 4,method = "getUserCount",columnTitle = "累计用户")
    private Integer userCount;

    /**
     * 统计日期
     */
    @ExportAnnotation(order = 5,method = "getStatisticsDate",columnTitle = "日期")
    private Date statisticsDate;

    public WholeTrendExportVo() {
    }

    public WholeTrendExportVo(WholeTrend wholeTrend) {
        this.devAdd = wholeTrend.getDevAdd();
        this.devLine = wholeTrend.getDevLine();
        this.devCount = wholeTrend.getDevCount();
        this.userAdd = wholeTrend.getUserAdd();
        this.userCount = wholeTrend.getUserCount();
        this.statisticsDate = wholeTrend.getStatisticsDate();
    }

    public Integer getDevAdd() {
        return devAdd;
    }

    public void setDevAdd(Integer devAdd) {
        this.devAdd = devAdd;
    }

    public Integer getDevLine() {
        return devLine;
    }

    public void setDevLine(Integer devLine) {
        this.devLine = devLine;
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
