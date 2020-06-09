package com.senuser.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;
import com.senuser.utils.ConstsUtils;

public class RandomService {

    /**
     * 入口函数
     * 
     * @throws CloneNotSupportedException
     */
    public static HashMap<Task, List<SenUser>> getRandomData() throws IOException, CloneNotSupportedException {
        // 获取任务
        List<Task> taskList = TaskService.getRandomTask(ConstsUtils.NUMTASK, ConstsUtils.MINIDTASK,
                ConstsUtils.MAXIDTASK, ConstsUtils.MINTIMETASK, ConstsUtils.MAXTIMETASK);
        // 获取用户
        List<SenUser> senUserList = SenUserService.getRandomUser(ConstsUtils.NUMUSER, ConstsUtils.MINIDUSER,
                ConstsUtils.MAXIDUSER, ConstsUtils.MINTIMEUSER, ConstsUtils.MAXTIMEUSER, ConstsUtils.MINBIDUSER,
                ConstsUtils.MAXBIDUSER);

        // 随机设置异常用户,包含异常用户
        senUserList = McdService.putAbnormalUser2(senUserList);

        // 输出Excel文件
        // HashMap<String, String> excelMap = new HashMap<String, String>();
        // excelMap.put("taskList", JsonUtils.objToGson(taskList));
        // excelMap.put("senUserList", JsonUtils.objToGson(senUserList));
        // ExcelUtils.outRandomExcel(excelMap);

        // 关联任务与用户
        HashMap<Task, List<SenUser>> bidMap = TaskService.getTaskWithSenUser(taskList, senUserList);

        return bidMap;
    }

}
