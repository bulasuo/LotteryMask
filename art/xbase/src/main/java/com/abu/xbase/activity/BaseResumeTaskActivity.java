package com.abu.xbase.activity;


import com.abu.xbase.Task.Task;

import java.util.HashMap;

/**
 * 在onResume里执行taskList里的task
 * 当前activity接收到消息时做ui更新,
 * 如果activity在后台已经onSaveInstanceState,但是做ui更新涉及到FragmentManager会
 * 报异常:java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
 * 而且ui更新在onResume里进行,会减少多个页面ui更新的阻塞
 * @author abu
 *         2017/11/22    11:32
 *         bulasuo@foxmail.com
 */

public abstract class BaseResumeTaskActivity extends BaseBackPressedActivity {

    /**
     * 是否处于Resume状态
     */
    private boolean resume = false;
    private HashMap<String, Task> taskMap;
    protected boolean isOnResume(){
        return resume;
    }

    private HashMap<String, Task> getTaskMap(){
        if(taskMap == null)
            taskMap = new HashMap<>();
        return taskMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume = true;
        if(taskMap != null && taskMap.size() > 0){
            for(Task task : taskMap.values()){
                task.apply();
            }
        }
        clearTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        resume = false;
    }

    @Override
    protected void onDestroy() {
        clearTask();
        super.onDestroy();
    }

    public void addTask(String tag, Task task){
        if(resume){
            task.apply();
            getTaskMap().remove(tag);
        }else {
            getTaskMap().put(tag, task);
        }
    }

    public void clearTask(){
        if(taskMap != null){
            taskMap.clear();
            taskMap = null;
        }
    }


}
