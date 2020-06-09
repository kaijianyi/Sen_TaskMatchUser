package com.senuser.main;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.senuser.model.common.Platform;
import com.senuser.model.common.PlatformTotal;
import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.service.DataService;
import com.senuser.service.McdService;
import com.senuser.service.RandomService;
import com.senuser.service.SenAuctionService;
import com.senuser.utils.ConstsUtils;
import com.senuser.utils.JsonUtils;
import com.senuser.utils.NumberUtils;

public class SenUserStart {

    public static void main(String[] args) throws IOException, CloneNotSupportedException {

        // 汇总数据
        List<Platform> platformList = new ArrayList<Platform>();

        for (int i = 1; i <= ConstsUtils.RUNNUM; i++) {
            // 储存平台数据
            Platform platform = new Platform();

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$$ 开始生成随机数据 $$$$$$$$$$$$$$$$$$$$$");

            Instant ranTimeStart = Instant.now();

            // 原始数据
            HashMap<Task, List<SenUser>> originBidMap = RandomService.getRandomData();
            
            // 随机设置异常用户,包含异常用户
            // originBidMap = McdService.putAbnormalUser(originBidMap);

            Instant ranTimeEnd = Instant.now();
            long ranRunTime = Duration.between(ranTimeStart, ranTimeEnd).toMillis();
            platform.setRandomRunTime(String.valueOf(ranRunTime));

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$$ 结束生成随机数据 $$$$$$$$$$$$$$$$$$$$$");

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$$ 正常拍卖开始 $$$$$$$$$$$$$$$$$$$$$\n");

            HashMap<Task, List<SenUser>> normalResultMap = new HashMap<Task, List<SenUser>>();

            Instant normalTimeStart = Instant.now();

            for (Map.Entry<Task, List<SenUser>> originBidEntry : originBidMap.entrySet()) {
                // 原始数据
                Task originTask = originBidEntry.getKey();
                List<SenUser> originSenUserList = originBidEntry.getValue();
                // 正常数据拍卖，使用原始数据，下层服务使用深拷贝
                List<SenUser> normalWinnerList = SenAuctionService.startAuction(originTask, originSenUserList);
                // 竞拍结果
                normalResultMap.put(originTask, normalWinnerList);
            }

            Instant normalTimeEnd = Instant.now();
            long normalRunTime = Duration.between(normalTimeStart, normalTimeEnd).toMillis();
            // 统计总的支付+时间
            platform.setNormalRunTime(String.valueOf(normalRunTime));
            platform = DataService.getNormalTotal(platform, normalResultMap);

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$$ 正常拍卖结束 $$$$$$$$$$$$$$$$$$$$$");

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$ 异常拍卖开始 $$$$$$$$$$$$$$$$$$$$$");

            // 汇总数据
            HashMap<Task, List<SenUser>> abnormalResultMap = new HashMap<Task, List<SenUser>>();

            Instant abnormalTimeStart = Instant.now();

            for (Map.Entry<Task, List<SenUser>> originBidEntry : originBidMap.entrySet()) {
                // 原始数据
                Task originTask = originBidEntry.getKey();
                List<SenUser> originSenUserList = originBidEntry.getValue();

                // 正常数据拍卖，使用原始数据，下层服务使用深拷贝
                List<SenUser> abnormalWinnerList = SenAuctionService.startAuction(originTask, originSenUserList);
                // 判断是否存在异常
                boolean flag = McdService.isAbnormal(abnormalWinnerList);

                // 备份Json数据
                String originTaskStr = JsonUtils.objToFastjson(originTask);
                String originSenUserListStr = JsonUtils.objToFastjson(originSenUserList);

                List<SenUser> nowWinnerList = abnormalWinnerList;
                // 如果存在异常用户参加竞拍
                while (flag) {
                    // 备份数据
                    Task reTask = JsonUtils.fastjsonToObj(originTaskStr, Task.class);
                    List<SenUser> reSenUserList = JsonUtils.fastjsonToObj(originSenUserListStr,
                            new TypeToken<List<SenUser>>() {
                            }.getType());

                    // 重置任务数据
                    McdService.getReTaskData(nowWinnerList, reTask, platform);
                    // 重置竞拍用户
                    for (SenUser abnormalWinner : abnormalWinnerList) {
                        // 删除获胜者
                        Iterator<SenUser> itReSenUserList = reSenUserList.iterator();
                        while (itReSenUserList.hasNext()) {
                            SenUser deleteSenuser = itReSenUserList.next();
                            if (abnormalWinner.getUserId() == deleteSenuser.getUserId()) {
                                itReSenUserList.remove();
                            }
                        }
                    }
                    nowWinnerList = SenAuctionService.startAuction(reTask, reSenUserList);
                    // 合并winner数据
                    abnormalWinnerList.addAll(nowWinnerList);
                    // 再次判断
                    flag = McdService.isAbnormal(nowWinnerList);
                }
                // 汇总数据
                abnormalResultMap.put(originTask, abnormalWinnerList);
            }

            Instant abnormalTimeEnd = Instant.now();
            long abnormalRunTime = Duration.between(abnormalTimeStart, abnormalTimeEnd).toMillis();
            // 统计总的支付+时间
            platform.setAbnormalRunTime(String.valueOf(abnormalRunTime));
            platform = DataService.getAbnormalTotal(platform, abnormalResultMap);

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$ 异常拍卖结束 $$$$$$$$$$$$$$$$$$$$$");

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$ MCD拍卖开始 $$$$$$$$$$$$$$$$$$$$$");

            // 汇总数据
            HashMap<Task, List<SenUser>> mcdResultMap = new HashMap<Task, List<SenUser>>();

            Instant mcdTimeStart = Instant.now();

            for (Map.Entry<Task, List<SenUser>> originBidMapEntry : originBidMap.entrySet()) {
                // mcd任务
                Task mcdTask = originBidMapEntry.getKey();
                // mcd用户
                List<SenUser> mcdSenUserList = originBidMapEntry.getValue();

                // 备份用户数据
                String mcdSenUserListStr = JsonUtils.objToFastjson(mcdSenUserList);
                List<SenUser> mcdSenUserListBackUp = JsonUtils.fastjsonToObj(mcdSenUserListStr,
                        new TypeToken<List<SenUser>>() {
                        }.getType());

                // 过滤异常用户
                mcdSenUserList = McdService.getMcdSenuser(mcdSenUserListBackUp);

                // 过滤后拍卖流程
                List<SenUser> mcdWinnerList = SenAuctionService.startAuction(mcdTask, mcdSenUserList);
                // 汇总数据
                mcdResultMap.put(mcdTask, mcdWinnerList);
            }

            Instant mcdTimeEnd = Instant.now();
            long mcdRunTime = Duration.between(mcdTimeStart, mcdTimeEnd).toMillis();
            // 统计总的支付+时间
            platform.setMcdRunTime(String.valueOf(mcdRunTime));
            platform = DataService.getMcdTotal(platform, mcdResultMap);

            System.out.println("\n$$$$$$$$$$$$$$$$$$$$ MCD拍卖结束 $$$$$$$$$$$$$$$$$$$$$");

            System.out.println("\n~~~~~~~~~~第" + i + "轮拍卖结束~~~~~~~~~\n");

            // 存入汇总数据
            platformList.add(platform);

        }

        PlatformTotal platformTotal = new PlatformTotal();

        // 数据求和
        for (Platform platform : platformList) {

            // 求和，100次随机数据-运行时间
            platformTotal.setRandomRunTimeTotal(
                    NumberUtils.addStr(platformTotal.getRandomRunTimeTotal(), platform.getRandomRunTime()));

            // 求和，100次标准-感知时间
            platformTotal.setNormalTimeTotal(platformTotal.getNormalTimeTotal() + platform.getNormalTime());
            // 求和，100次标准-支付
            platformTotal
                    .setNormalPayTotal(NumberUtils.addStr(platformTotal.getNormalPayTotal(), platform.getNormalPay()));
            // 求和，100次标准-运行时间
            platformTotal.setNormalRunTimeTotal(
                    NumberUtils.addStr(platformTotal.getNormalRunTimeTotal(), platform.getNormalRunTime()));

            // 求和，100次异常-感知时间
            platformTotal.setAbnormalTimeTotal(platformTotal.getAbnormalTimeTotal() + platform.getAbnormalTime());
            // 求和，100次异常-支付
            platformTotal.setAbnormalPayTotal(
                    NumberUtils.addStr(platformTotal.getAbnormalPayTotal(), platform.getAbnormalPay()));
            // 求和，100次异常-运行时间
            platformTotal.setAbnormalRunTimeTotal(
                    NumberUtils.addStr(platformTotal.getAbnormalRunTimeTotal(), platform.getAbnormalRunTime()));

            // 求和，100次MCD-感知时间
            platformTotal.setMcdTimeTotal(platformTotal.getMcdTimeTotal() + platform.getMcdTime());
            // 求和，100次MCD-支付
            platformTotal.setMcdPayTotal(NumberUtils.addStr(platformTotal.getMcdPayTotal(), platform.getMcdPay()));
            // 求和，100次MCD-运行时间
            platformTotal.setMcdRunTimeTotal(
                    NumberUtils.addStr(platformTotal.getMcdRunTimeTotal(), platform.getMcdRunTime()));

            platformTotal.setWaitTimeTotal(platformTotal.getWaitTimeTotal() + platform.getWaitTime());

        }

        // 平均，100次随机数据-运行时间
        platformTotal.setRandomRunTimeAve(NumberUtils.divisionStr2(platformTotal.getRandomRunTimeTotal(),
                String.valueOf(ConstsUtils.RUNNUM * 1000)));

        // 平均，100次异常-感知时间
        platformTotal.setNormalTimeAve(NumberUtils.divisionStr(String.valueOf(platformTotal.getNormalTimeTotal()),
                String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次异常-支付
        platformTotal.setNormalPayAve(
                NumberUtils.divisionStr(platformTotal.getNormalPayTotal(), String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次标准-运行时间
        platformTotal.setNormalRunTimeAve(NumberUtils.divisionStr2(platformTotal.getNormalRunTimeTotal(),
                String.valueOf(ConstsUtils.RUNNUM * 1000)));

        // 平均，100次异常-感知时间
        platformTotal.setAbnormalTimeAve(NumberUtils.divisionStr(String.valueOf(platformTotal.getAbnormalTimeTotal()),
                String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次异常-支付
        platformTotal.setAbnormalPayAve(
                NumberUtils.divisionStr(platformTotal.getAbnormalPayTotal(), String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次异常-运行时间
        platformTotal.setAbnormalRunTimeAve(NumberUtils.divisionStr2(platformTotal.getAbnormalRunTimeTotal(),
                String.valueOf(ConstsUtils.RUNNUM * 1000)));

        // 平均，100次MCD-感知时间
        platformTotal.setMcdTimeAve(NumberUtils.divisionStr(String.valueOf(platformTotal.getMcdTimeTotal()),
                String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次MCD-支付
        platformTotal.setMcdPayAve(
                NumberUtils.divisionStr(platformTotal.getMcdPayTotal(), String.valueOf(ConstsUtils.RUNNUM)));
        // 平均，100次MCD-运行时间
        platformTotal.setMcdRunTimeAve(NumberUtils.divisionStr2(platformTotal.getMcdRunTimeTotal(),
                String.valueOf(ConstsUtils.RUNNUM * 1000)));

        System.out.println(">>>>>>>随机数据：");
        // System.out.println("随机数据总时间：" + platformTotal.getRandomRunTimeTotal());
        System.out.println("随机数据平均时间：" + platformTotal.getRandomRunTimeAve());

        System.out.println(">>>>>>>标准算法：");
        // System.out.println("全部感知时间：" + platformTotal.getNormalTimeTotal());
        // System.out.println("全部支付成本：" + platformTotal.getNormalPayTotal());
        // System.out.println("全部运行时间：" + platformTotal.getNormalRunTimeTotal());
        System.out.println("平均感知时间：" + platformTotal.getNormalTimeAve());
        System.out.println("平均支付成本：" + platformTotal.getNormalPayAve());
        System.out.println("平均运行时间：" + platformTotal.getNormalRunTimeAve());

        System.out.println(">>>>>>>异常算法：");
        // System.out.println("全部感知时间：" + platformTotal.getAbnormalTimeTotal());
        // System.out.println("全部支付成本：" + platformTotal.getAbnormalPayTotal());
        // System.out.println("全部运行时间：" + platformTotal.getAbnormalRunTimeTotal());
        System.out.println("平均感知时间：" + platformTotal.getAbnormalTimeAve());
        System.out.println("平均支付成本：" + platformTotal.getAbnormalPayAve());
        System.out.println("平均运行时间：" + platformTotal.getAbnormalRunTimeAve());

        System.out.println(">>>>>>>MCD算法：");
        // System.out.println("全部感知时间：" + platformTotal.getMcdTimeTotal());
        // System.out.println("全部支付成本：" + platformTotal.getMcdPayTotal());
        // System.out.println("全部运行时间：" + platformTotal.getMcdRunTimeTotal());
        System.out.println("平均感知时间：" + platformTotal.getMcdTimeAve());
        System.out.println("平均支付成本：" + platformTotal.getMcdPayAve());
        System.out.println("平均运行时间：" + platformTotal.getMcdRunTimeAve());

        // TODO 删除
        System.out.println("平均等待时间：" + platformTotal.getWaitTimeTotal() / ConstsUtils.RUNNUM);

        System.out.println();

    }

}
