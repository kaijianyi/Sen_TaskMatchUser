package com.senuser.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.reflect.TypeToken;
import com.senuser.model.task.Task;
import com.senuser.model.user.SenUser;

/**
 * Description:Excel工具类，暂时废弃该类
 *
 * @author kjy
 * @since Apr 5, 2020 9:49:43 AM
 */
public class ExcelUtils {

    /**
     * 保存随机生成的用户+任务数据
     * 
     * @param map
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static void outRandomExcel(Map<String, String> map) throws IOException {
        // 创建一个Excel文件
        HSSFWorkbook excel = new HSSFWorkbook();
        // 创建行
        HSSFRow row = null;
        // 创建列
        HSSFCell cell = null;

        /**
         * 创建随机任务表
         */
        String ranTaskListStr = map.get("ranTaskList");
        List<Task> ranTaskList = JsonUtils.gsonToObj(ranTaskListStr, new TypeToken<List<Task>>() {
        }.getType());

        // 创建sheet
        HSSFSheet sheetTask = excel.createSheet("随机任务表");
        // 创建第1行
        row = sheetTask.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("任务id");

        cell = row.createCell(1);
        cell.setCellValue("任务感知时间");

        for (int i = 0; i < ranTaskList.size(); i++) {
            // 创建第i+1行
            row = sheetTask.createRow(i + 1);
            // 创建第i+1行第1列
            cell = row.createCell(0);
            // 第i+1行第1列赋值
            cell.setCellValue(ranTaskList.get(i).getTaskId());

            cell = row.createCell(1);
            cell.setCellValue(ranTaskList.get(i).getUnfinishTime());
        }

        /**
         * 创建随机用户表
         */
        String ranUserListStr = map.get("ranUserList");
        List<SenUser> ranUserList = JsonUtils.gsonToObj(ranUserListStr, new TypeToken<List<SenUser>>() {
        }.getType());

        // 创建sheet
        HSSFSheet sheetUser = excel.createSheet("随机用户表");
        // 创建第1行
        row = sheetUser.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("用户id");

        cell = row.createCell(1);
        cell.setCellValue("用户感知时间");

        cell = row.createCell(2);
        cell.setCellValue("用户竞标价格");

        for (int i = 0; i < ranUserList.size(); i++) {
            // 创建第i+1行
            row = sheetUser.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(ranUserList.get(i).getUserId());

            cell = row.createCell(1);
            cell.setCellValue(ranUserList.get(i).getOriginSenTime());

            cell = row.createCell(2);
            cell.setCellValue(ranUserList.get(i).getBid());
        }

        // 设置路径
        File file = new File("/Users/kjy/Desktop/随机数据表.xls");
        if (!file.exists()) {
            file.createNewFile();
        }
        // 保存Excel文件
        FileOutputStream fileOut = new FileOutputStream(file);
        excel.write(fileOut);
        fileOut.close();
    }

}
