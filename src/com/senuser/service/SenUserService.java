package com.senuser.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.senuser.model.common.Coordinate;
import com.senuser.model.user.SenUser;
import com.senuser.model.user.TxtUser;
import com.senuser.utils.ConstsUtils;
import com.senuser.utils.FileUtils;
import com.senuser.utils.RandomUtils;
import com.senuser.utils.TimeUtils;

public class SenUserService {

    /**
     * 生成随机用户
     * 
     * @param userNum
     * @param minUserId
     * @param maxUserId
     * @param minUserTime
     * @param maxUserTime
     * @param minUserBid
     * @param maxUserBid
     * @return
     */
    public static List<SenUser> getRandomUser(int userNum, int minUserId, int maxUserId, int minUserTime,
            int maxUserTime, int minUserBid, int maxUserBid) {
        // 返回值
        List<SenUser> senUserBidList = new ArrayList<SenUser>();
        // 防止生成重复数字
        List<Integer> exitList = new ArrayList<Integer>();
        while (senUserBidList.size() < userNum) {
            int ranUserId = RandomUtils.getRandom(minUserId, maxUserId);
            if (!exitList.contains(ranUserId)) {
                exitList.add(ranUserId);
                // 读取对应id的文件数据
                SenUser senUserBid = readFile(ranUserId);
                // 对应id数据满足条件，则生成感知时间等数据
                if (senUserBid != null) {
                    // 用户id范围是[1，10357]
                    senUserBid.setUserId(ranUserId);
                    // 感知时间范围是[5，10]
                    senUserBid.setOriginSenTime(RandomUtils.getRandom(minUserTime, maxUserTime));
                    // 竞标成本范围是[6，10]
                    senUserBid.setBid(RandomUtils.getRandom(minUserBid, maxUserBid));
                    senUserBidList.add(senUserBid);
                }
            }
        }
        // 按照userId升序
        Collections.sort(senUserBidList);
        return senUserBidList;
    }

    /*
     * 根据userId读取文件
     */
    public static SenUser readFile(int ranUserId) {
        SenUser senUserBid = new SenUser();
        // 读取全部文档数据
        List<TxtUser> txtUserList = str2TxtUser(FileUtils.readTxtFile("/Users/kjy/Downloads/MACBOOK/Paper/taxi/" + ranUserId + ".txt"));
        // 筛选范围内的时间数据
        List<TxtUser> trueUserList = new ArrayList<TxtUser>();
        for (TxtUser txtUser : txtUserList) {
            if (TimeUtils.isEffectiveDate(TimeUtils.string2Date(txtUser.getGpsTime()), ConstsUtils.STARTTIME,
                    ConstsUtils.ENDTIME)) {
                trueUserList.add(txtUser);
            }
        }
        // MCD算法要求
        if (trueUserList.size() >= ConstsUtils.FASTMCD) {
            senUserBid = txt2SenUserBid(trueUserList);
            return senUserBid;
        }
        return null;
    }

    /**
     * 根据userId增加一条异常数据
     * 
     * @param carelessUser
     * @return
     */
    public static SenUser readCarelessFile(SenUser carelessUser) {
        // 读取全部文档数据
        List<TxtUser> txtUserList = str2TxtUser(
                FileUtils.readTxtFile("/Users/kjy/Downloads/MACBOOK/Paper/taxi/" + carelessUser.getUserId() + ".txt"));
        // 筛选范围内的时间数据
        for (TxtUser txtUser : txtUserList) {
            if (TimeUtils.isEffectiveDate(TimeUtils.string2Date(txtUser.getGpsTime()), ConstsUtils.CARELESSSTARTTIME,
                    ConstsUtils.CARELESSENDTIME)) {
                Coordinate cor = txtUser.getCor();
                carelessUser.getCorList().add(cor);
                // 只读取一条数据
                break;
            }
        }
        // 按照GPS时间升序
        Collections.sort(carelessUser.getCorList());
        return carelessUser;
    }

    /*
     * 字符串转换为对象
     */
    private static List<TxtUser> str2TxtUser(List<String> strList) {
        List<TxtUser> txtUserList = new ArrayList<TxtUser>();
        for (int i = 0; i < strList.size(); i++) {
            TxtUser txtUser = new TxtUser();
            String[] userArr = strList.get(i).split("\\,");

            txtUser.setUserId(Integer.valueOf(userArr[0]));
            txtUser.setGpsTime(userArr[1]);

            Coordinate cor = new Coordinate();
            cor.setGpsTime(userArr[1]);
            cor.setLon(userArr[2]);
            cor.setLat(userArr[3]);

            txtUser.setCor(cor);

            txtUserList.add(txtUser);
        }
        return txtUserList;
    }

    /*
     * txt对象转换
     */
    private static SenUser txt2SenUserBid(List<TxtUser> trueUserList) {
        SenUser senUserBid = new SenUser();
        // 合并GPS数据
        List<Coordinate> corList = new ArrayList<Coordinate>();
        for (TxtUser trueUser : trueUserList) {
            corList.add(trueUser.getCor());
        }
        // 按照GPS时间升序
        Collections.sort(corList);
        senUserBid.setCorList(corList);
        return senUserBid;
    }

}
