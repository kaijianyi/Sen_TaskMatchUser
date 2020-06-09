package com.senuser.utils;

import java.util.Date;

/**
 * Description:静态类
 *
 * @author kjy
 * @since Apr 21, 2020 5:09:01 PM
 */
public class ConstsUtils {
    // 总执行次数
    public static final int RUNNUM = 5;
    // BP值
    public static final float BREAKPOINT = 0.25f;
    // 任务数
    public static final int NUMTASK = 40;
    // 默认用户数
    public static final int NUMUSER = 140;
    // 任务最小感知时间
    public static final int MINTIMETASK = 5;
    // 任务最大感知时间
    public static final int MAXTIMETASK = 15;

    // 开始时间
    public static final Date STARTTIME = TimeUtils.string2Date("2008-02-02 15:00:00");
    // 结束时间
    public static final Date ENDTIME = TimeUtils.string2Date("2008-02-02 16:00:00");
    // 异常用户开始时间
    public static final Date CARELESSSTARTTIME = TimeUtils.string2Date("2008-02-03 01:00:00");
    // 异常用户结束时间
    public static final Date CARELESSENDTIME = TimeUtils.string2Date("2008-02-03 23:00:00");
    // MCD算法要求，至少有5组历史数据
    public static final int FASTMCD = 5;

    // 任务最小编号
    public static final int MINIDTASK = 1;
    // 任务最大编号
    public static final int MAXIDTASK = 730;

    // 用户最小编号
    public static final int MINIDUSER = 1;
    // 用户最大编号
    public static final int MAXIDUSER = 10357;
    // 用户最小感知时间
    public static final int MINTIMEUSER = 5;
    // 用户最大感知时间
    public static final int MAXTIMEUSER = 10;
    // 用户最大感知时间
    public static final int MINBIDUSER = 6;
    // 用户最大感知时间
    public static final int MAXBIDUSER = 10;
}
