package com.senuser.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.senuser.model.common.Platform;
import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.utils.ConstsUtils;
import com.senuser.utils.RandomUtils;

public class McdService {

    /**
     * 设置随机异常用户，后置使用
     * 
     * @param originMap
     * @return
     */
    public static HashMap<Task, List<SenUser>> putAbnormalUser(HashMap<Task, List<SenUser>> originMap) {
        HashMap<Task, List<SenUser>> abnormalBidMap = new HashMap<Task, List<SenUser>>();
        abnormalBidMap.putAll(originMap);
        for (Map.Entry<Task, List<SenUser>> entry : abnormalBidMap.entrySet()) {
            // 统计成本
            List<SenUser> originList = entry.getValue();
            SenUser carelessUser = new SenUser();
            // 防止生成重复数字
            List<Integer> numList = new ArrayList<Integer>();
            int carelessNum = (int) (ConstsUtils.BREAKPOINT * originList.size());
            while (numList.size() < carelessNum) {
                int number = RandomUtils.getRandom(0, originList.size() - 1);
                if (!numList.contains(number)) {
                    numList.add(number);
                    carelessUser = originList.get(number);
                    carelessUser.setCareless(1);
                    // 添加一条异常坐标数据
                    carelessUser = SenUserService.readCarelessFile(carelessUser);
                }
            }

        }
        return abnormalBidMap;
    }

    /**
     * 设置随机异常用户，前置使用
     * 
     * @param senUserList
     * @return
     */
    public static List<SenUser> putAbnormalUser2(List<SenUser> senUserList) {
        SenUser carelessUser = new SenUser();
        // 防止生成重复数字
        List<Integer> numList = new ArrayList<Integer>();
        int carelessNum = (int) (ConstsUtils.BREAKPOINT * senUserList.size());
        while (numList.size() < carelessNum) {
            int number = RandomUtils.getRandom(0, senUserList.size() - 1);
            if (!numList.contains(number)) {
                numList.add(number);
                carelessUser = senUserList.get(number);
                carelessUser.setCareless(1);
                // 添加一条异常坐标数据
                carelessUser = SenUserService.readCarelessFile(carelessUser);
            }
        }
        return senUserList;
    }

    /**
     * 过滤异常用户
     * 
     * @param abnormalBidMap
     * @return
     */
    // TODO 将MCD算法的matlib包打包为jar执行
    public static List<SenUser> getMcdSenuser(List<SenUser> mcdSenUserList) {
        Iterator<SenUser> iterator = mcdSenUserList.iterator();
        while (iterator.hasNext()) {
            SenUser deleteSenuser = iterator.next();
            if (deleteSenuser.getCareless() == 1) {
                iterator.remove();
            }
        }
        // 调用MCD算法时间损耗
        int waitTime = mcdSenUserList.size() / 10;
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mcdSenUserList;
    }

    /**
     * 判断是否有异常用户
     * 
     * @param abnormalWinnerList
     * @return
     */
    public static boolean isAbnormal(List<SenUser> abnormalWinnerList) {
        for (SenUser abnormalWinner : abnormalWinnerList) {
            if (abnormalWinner.getCareless() == 1) {
                // 存在异常
                return true;
            }
        }
        // 默认不存在异常用户
        return false;
    }

    /**
     * 获得重新竞标的数据
     * 
     * @param abnormalWinnerList
     * @param reTask
     */
    public static void getReTaskData(List<SenUser> nowWinnerList, Task reTask, Platform platform) {
        // 任务设置为已完成
        reTask.setFinishTime(reTask.getOriginTime());
        reTask.setUnfinishTime(0);
        for (SenUser nowWinner : nowWinnerList) {
            // 重置任务时间
            if (nowWinner.getCareless() == 1) {
                // 重置任务时间
                reTask.setUnfinishTime(reTask.getUnfinishTime() + nowWinner.getWinSenTime());
                reTask.setFinishTime(reTask.getOriginTime() - reTask.getUnfinishTime());
                // TODO 删除
                platform.setWaitTime(platform.getWaitTime() + nowWinner.getWinSenTime());
            }
        }
    }
}
