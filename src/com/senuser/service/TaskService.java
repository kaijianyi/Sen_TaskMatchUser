package com.senuser.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.utils.ConstsUtils;
import com.senuser.utils.RandomUtils;

public class TaskService {

    /**
     * 生成随机任务
     * 
     * @param taskNum
     * @param minTaskId
     * @param maxTaskId
     * @param minTaskTime
     * @param maxTaskTime
     * @return
     */
    public static List<Task> getRandomTask(int taskNum, int minTaskId, int maxTaskId, int minTaskTime,
            int maxTaskTime) {
        // 返回值
        List<Task> taskList = new ArrayList<Task>();
        // 防止生成重复数字
        List<Integer> exitList = new ArrayList<Integer>();
        while (taskList.size() < taskNum) {
            Task task = new Task();
            // 任务id范围是[1，730]
            int taskId = RandomUtils.getRandom(minTaskId, maxTaskId);
            if (!exitList.contains(taskId)) {
                // 防止生成相同的任务
                exitList.add(taskId);
                task.setTaskId(taskId);
                // 任务感知时间[5,12]
                int senTime = RandomUtils.getRandom(minTaskTime, maxTaskTime);
                task.setOriginTime(senTime);
                task.setUnfinishTime(senTime);
                taskList.add(task);
            }
        }
        // 按照taskId升序
        Collections.sort(taskList);
        // 排序后编号
        for (int j = 0; j < taskList.size(); j++) {
            taskList.get(j).setId(j + 1);
        }
        return taskList;
    }

    /**
     * 关联任务和竞拍人员
     * 
     * @param TaskList
     * @param senUserBidList
     * @throws CloneNotSupportedException
     */
    public static HashMap<Task, List<SenUser>> getTaskWithSenUser(List<Task> taskList, List<SenUser> senUserBidList)
            throws CloneNotSupportedException {
        // 返回值
        HashMap<Task, List<SenUser>> bidMap = new HashMap<Task, List<SenUser>>();
        for (Task task : taskList) {
            // 任务深拷贝
            Task bidTask = task.clone();
            // 任务对应的用户列表
            List<SenUser> bidSenUserList = new ArrayList<SenUser>();
            // 防止生成重复数字
            List<Integer> exitList = new ArrayList<Integer>();
            // 任务AOI数据
            int taskAOI = RandomUtils.getRandom(20, 100);
            // 限制每个人参加一个任务
            while (bidSenUserList.size() < taskAOI) {
                // list编号从0开始
                int bidNum = RandomUtils.getRandom(0, ConstsUtils.NUMUSER - 1);
                if (!exitList.contains(bidNum)) {
                    exitList.add(bidNum);
                    // 深拷贝
                    SenUser bidSenUser = senUserBidList.get(bidNum).clone();
                    bidSenUserList.add(bidSenUser);
                }
            }
            // 按照userId排序输出
            Collections.sort(bidSenUserList);
            // 排序后编号
            for (int j = 0; j < bidSenUserList.size(); j++) {
                bidSenUserList.get(j).setId(j + 1);
            }
            // 全部深拷贝
            bidMap.put(bidTask, bidSenUserList);
        }
        return bidMap;
    }
}
