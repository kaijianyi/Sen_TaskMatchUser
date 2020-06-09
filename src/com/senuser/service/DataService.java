package com.senuser.service;

import java.util.HashMap;
import java.util.List;

import com.senuser.model.common.Platform;
import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.utils.NumberUtils;

public class DataService {

    /**
     * 获得正常汇总数据
     * 
     * @param normalResultMap
     * @return
     */
    public static Platform getNormalTotal(Platform platform, HashMap<Task, List<SenUser>> normalResultMap) {
        for (HashMap.Entry<Task, List<SenUser>> normalResulEntry : normalResultMap.entrySet()) {
            // 统计成本
            List<SenUser> normalList = normalResulEntry.getValue();
            for (SenUser normal : normalList) {
                platform.setNormalTime(platform.getNormalTime() + normal.getWinSenTime());
                platform.setNormalPay(NumberUtils.addStr(platform.getNormalPay(), normal.getPay()));
            }
        }
        return platform;
    }

    /**
     * 获取异常汇总数据
     * 
     * @param platform
     * @param abnormalResultMap
     * @param abnormalAgainResultMap
     * @return
     */
    public static Platform getAbnormalTotal(Platform platform, HashMap<Task, List<SenUser>> abnormalResultMap) {
        for (HashMap.Entry<Task, List<SenUser>> abnormalEntry : abnormalResultMap.entrySet()) {
            List<SenUser> abnormalList = abnormalEntry.getValue();
            for (SenUser abnormal : abnormalList) {
                // 因为异常用户的等待时间
                if (abnormal.getCareless() == 1) {
                    // 设置异常感知时间
                    platform.setAbnormalTime(platform.getAbnormalTime() + abnormal.getWinSenTime());
                }
                if (abnormal.getCareless() == 0) {
                    // 统计感知时间
                    platform.setAbnormalTime(platform.getAbnormalTime() + abnormal.getWinSenTime());
                    // 统计支付
                    platform.setAbnormalPay(NumberUtils.addStr(platform.getAbnormalPay(), abnormal.getPay()));
                }
            }
        }

        return platform;
    }

    /**
     * 获得MCD拍卖数据
     * 
     * @param mcdMap
     * @return
     */
    public static Platform getMcdTotal(Platform platform, HashMap<Task, List<SenUser>> mcdResultMap) {
        for (HashMap.Entry<Task, List<SenUser>> entry : mcdResultMap.entrySet()) {
            List<SenUser> mcdList = entry.getValue();
            for (SenUser mcdWinner : mcdList) {
                platform.setMcdTime(platform.getMcdTime() + mcdWinner.getWinSenTime());
                platform.setMcdPay(NumberUtils.addStr(platform.getMcdPay(), mcdWinner.getPay()));
            }
        }
        return platform;
    }

    /**
     * 合并获胜者数据
     * 
     * @param abnormalResultMap
     * @param abnormalAgainResultMap
     * @return
     */
    public static HashMap<Task, List<SenUser>> mergeWinner(HashMap<Task, List<SenUser>> abnormalResultMap,
            HashMap<Task, List<SenUser>> abnormalAgainResultMap) {
        for (HashMap.Entry<Task, List<SenUser>> againEntry : abnormalAgainResultMap.entrySet()) {
            Task againTask = againEntry.getKey();
            List<SenUser> againList = againEntry.getValue();

            for (HashMap.Entry<Task, List<SenUser>> abnormalResultEntry : abnormalResultMap.entrySet()) {
                Task abnormalTask = abnormalResultEntry.getKey();
                List<SenUser> abnormalList = abnormalResultEntry.getValue();

                if (againTask.getTaskId() == abnormalTask.getTaskId()) {
                    abnormalList.addAll(againList);
                    break;
                }
            }
        }
        return abnormalResultMap;
    }

}
