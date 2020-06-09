package com.senuser.model.user;

import java.util.List;

import com.senuser.model.common.Coordinate;

/**
 * Description:敏感型竞标用户类
 *
 * @author kjy
 * @since Apr 4, 2020 3:24:28 PM
 */
public class SenUser implements Comparable<SenUser>, Cloneable {
    // API格式：纬度lat,39，经度lon,116
    // 数据集格式：经度lon,116，纬度lat,39

    // id
    private int id;
    // 用户id
    private int userId;
    // 用户出价
    private int bid;
    // 原始感知时间
    private int originSenTime;
    // 历史bid数据
    private List<Coordinate> corList;

    // 异常用户标记，0-正常，1异常
    private int careless;

    // 竞拍单位成本
    private String aveCost;
    // 获胜感知时间
    private int winSenTime;
    // 竞拍获胜后的收益
    private String pay = "0";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getOriginSenTime() {
        return originSenTime;
    }

    public void setOriginSenTime(int originSenTime) {
        this.originSenTime = originSenTime;
    }

    public List<Coordinate> getCorList() {
        return corList;
    }

    public void setCorList(List<Coordinate> corList) {
        this.corList = corList;
    }

    public int getCareless() {
        return careless;
    }

    public void setCareless(int careless) {
        this.careless = careless;
    }

    public String getAveCost() {
        return aveCost;
    }

    public void setAveCost(String aveCost) {
        this.aveCost = aveCost;
    }

    public int getWinSenTime() {
        return winSenTime;
    }

    public void setWinSenTime(int winSenTime) {
        this.winSenTime = winSenTime;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    @Override
    public int compareTo(SenUser user) {
        return this.userId - user.getUserId();
    }

    @Override
    public SenUser clone() throws CloneNotSupportedException {
        return (SenUser) super.clone();
    }

}
