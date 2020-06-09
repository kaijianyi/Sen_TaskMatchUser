package com.senuser.model.task;

/**
 * Description:任务类
 *
 * @author kjy
 * @since Apr 4, 2020 3:28:55 PM
 */
public class Task implements Comparable<Task>, Cloneable {
    // id
    private int id;

    // 任务id
    private int taskId;

    // 原始感知时间
    private int originTime;

    // 所需感知时间
    private int unfinishTime;

    // 已完成感知时间
    private int finishTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getOriginTime() {
        return originTime;
    }

    public void setOriginTime(int originTime) {
        this.originTime = originTime;
    }

    public int getUnfinishTime() {
        return unfinishTime;
    }

    public void setUnfinishTime(int unfinishTime) {
        this.unfinishTime = unfinishTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public int compareTo(Task task) {
        return this.taskId - task.getTaskId();
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }
}
