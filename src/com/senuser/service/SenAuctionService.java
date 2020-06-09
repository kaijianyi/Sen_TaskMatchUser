package com.senuser.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.utils.JsonUtils;
import com.senuser.utils.NumberUtils;

public class SenAuctionService {

    /**
     * 开始入口
     * 
     * @param bidMap
     * @return
     */
    public static List<SenUser> startAuction(Task originTask, List<SenUser> originSenUserList) {

        // 任务Json数据
        String originTaskStr = JsonUtils.objToFastjson(originTask);
        // 用户级Json
        String originSenUserListStr = JsonUtils.objToFastjson(originSenUserList);

        // winnerSelection数据
        Task winnerTask = JsonUtils.fastjsonToObj(originTaskStr, Task.class);
        List<SenUser> winnerSenUserList = JsonUtils.fastjsonToObj(originSenUserListStr, new TypeToken<List<SenUser>>() {
        }.getType());

        System.out.println("\n>>>>>>>当前任务ID：" + originTask.getTaskId());

        System.out.println("\n$$$$$$$$$$$$$$$$$ winnerSelection开始 $$$$$$$$$$$$$$$$");
        List<SenUser> winnerUserList = winnerSelection(winnerTask, winnerSenUserList);
        System.out.println("\n$$$$$$$$$$$$$$$$$ winnerSelection结束 $$$$$$$$$$$$$$$$");

        System.out.println("\n$$$$$$$$$$$$$$$$ paymentDetermination开始 $$$$$$$$$$$$$$$$");
        winnerUserList = paymentDetermination(originTask, originSenUserList, winnerUserList);
        System.out.println("\n$$$$$$$$$$$$$$$$ paymentDetermination结束 $$$$$$$$$$$$$$$$");

        return winnerUserList;
    }

    /*
     * 1-0、Winner Selection
     */
    private static List<SenUser> winnerSelection(Task winnerTask, List<SenUser> winnerSenUserList) {
        // 获胜者集合
        List<SenUser> winnerList = new ArrayList<SenUser>();
        // 获取任务感知时间
        while (winnerTask.getUnfinishTime() > 0) {
            // 计算单位成本并取得获胜者
            SenUser winner = getWinner(winnerSenUserList, winnerTask.getUnfinishTime());
            // 加入获胜者集合
            winnerList.add(winner);
            // 从候选者集合剔除获胜者
            winnerSenUserList.remove(winner);
            // 更新任务感知时间
            winnerTask.setFinishTime(winnerTask.getFinishTime() + winner.getWinSenTime());
            winnerTask.setUnfinishTime(winnerTask.getUnfinishTime() - winner.getWinSenTime());
        }
        return winnerList;
    }

    /*
     * 1-1、获胜者算法
     */
    private static SenUser getWinner(List<SenUser> winnerSenUserList, int remainSenTime) {

        System.out.println("\n>>>>>>>>>任务剩余感知时间：" + remainSenTime + " <<<<<<<<<<<<<");

        // 计算所有竞标者的单位竞标成本
        for (SenUser senUser : winnerSenUserList) {
            // 获取最小竞标时间
            int minTime = NumberUtils.getMin(remainSenTime, senUser.getOriginSenTime());
            // 计算每个用户的单位竞标成本
            String aveCostStr = NumberUtils.division(senUser.getBid(), minTime);
            senUser.setAveCost(aveCostStr);
            senUser.setWinSenTime(minTime);

            // System.out.println(
            // "ID：" + senUser.getId() + ", 用户ID：" + senUser.getUserId() + ", 报价：" + senUser.getBid() + ", 初始感知时间:"
            // + senUser.getOriginSenTime() + ", 获胜感知时间:" + minTime + ", 单位成本:" + senUser.getAveCost());
            // System.out.println("########################################################################");
        }
        // 选择获胜者
        SenUser winner = getMinAveCost(winnerSenUserList);
        // System.out.println(">>>>>>获胜者ID：" + winner.getUserId() + ", 报价：" + winner.getBid() + ",初始感知时间:"
        // + winner.getOriginSenTime() + ", 获胜感知时间:" + winner.getWinSenTime() + ", 单位成本:" + winner.getAveCost());
        return winner;
    }

    /*
     * 1-2、选择最小的竞标成本的获胜
     */
    private static SenUser getMinAveCost(List<SenUser> originList) {
        // 假设第一个人竞拍成本最小
        SenUser winner = originList.get(0);
        for (int i = 1; i < originList.size(); i++) {
            if (Float.valueOf(winner.getAveCost()) > Float.valueOf(originList.get(i).getAveCost())) {
                winner = originList.get(i);
            }
        }
        return winner;
    }

    /**
     * 2-0、Payment Determination
     */
    private static List<SenUser> paymentDetermination(Task originTask, List<SenUser> originSenUserList,
            List<SenUser> winnerUserList) {

        String originTaskStr = JsonUtils.objToFastjson(originTask);
        String originSenUserListStr = JsonUtils.objToFastjson(originSenUserList);

        // 去除winner后再重新竞拍
        for (SenUser winner : winnerUserList) {

            // paymentDetermination数据
            Task payTask = JsonUtils.fastjsonToObj(originTaskStr, Task.class);

            List<SenUser> paySenUserList = JsonUtils.fastjsonToObj(originSenUserListStr,
                    new TypeToken<List<SenUser>>() {
                    }.getType());

            System.out.println("\n$$$$$$$$$$$$$$$$ 当前获胜者：" + winner.getUserId() + " $$$$$$$$$$$$$$$$");

            // 去除当前winner
            Iterator<SenUser> itPaySenUserList = paySenUserList.iterator();
            while (itPaySenUserList.hasNext()) {
                SenUser deleteSenuser = itPaySenUserList.next();
                if (deleteSenuser.getUserId() == winner.getUserId()) {
                    itPaySenUserList.remove();
                }
            }

            // 重新竞拍后获得的获胜者集合
            List<SenUser> nextWinnerList = winnerSelection(payTask, paySenUserList);
            for (SenUser nextWinner : nextWinnerList) {
                String payBid = getPay(winner, nextWinner);
                winner.setPay(payBid);
            }

            // System.out.println("\n>>>>>最终获胜者ID：" + winner.getId() + ", 最终获胜者用户ID：" + winner.getUserId() + ", 获胜者竞价："
            // + winner.getBid() + ", 获胜者感知时间：" + winner.getOriginSenTime() + ", 获胜者实际感知时间："
            // + winner.getWinSenTime() + ", 获胜者单位成本：" + winner.getAveCost() + ", 获胜者收益：" + winner.getPay());

        }
        return winnerUserList;
    }

    /*
     * 2-1、支付函数
     */
    private static String getPay(SenUser winner, SenUser nextWinner) {
        String winnerPay = winner.getPay();
        int winnerSenTime = winner.getWinSenTime();
        // 保留2位小数
        String nextPay = getNextPay(winnerSenTime, nextWinner.getWinSenTime(), nextWinner.getBid());
        winnerPay = NumberUtils.getStrMax(winnerPay, nextPay);
        return winnerPay;
    }

    /*
     * 2-2、计算支付价格
     */
    public static String getNextPay(int winnerSenTime, int nextSenTime, int nextBid) {
        float result = (float) winnerSenTime / nextSenTime * nextBid;
        String nextPay = String.format("%.2f", result);
        return nextPay;
    }

}
