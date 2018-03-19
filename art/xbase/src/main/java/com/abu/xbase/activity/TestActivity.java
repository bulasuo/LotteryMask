package com.abu.xbase.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abu.xbase.R;
import com.abu.xbase.bean.User;
import com.abu.xbase.greendao.DaoMaster;
import com.abu.xbase.greendao.DaoSession;
import com.abu.xbase.greendao.UserDao;

import java.util.List;

/**
 * @author abu
 *         2018/1/2    15:58
 *         bulasuo@foxmail.com
 */

public class TestActivity extends Activity {

    public static void launch(Context context){
        context.startActivity(new Intent(context, TestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
        super.onCreate(savedInstanceState);
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "lenve.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        findViewById(R.id.btn_0).setOnClickListener(v->{
            DaoSession daoSession = daoMaster.newSession();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteAll();
            User user = new User(null, "zhangsan","张三");
            userDao.insertOrReplace(user);
            user = new User(null, "zhangsanxx","张三xx");
            userDao.insert(user);
            /**清除session缓存,查询才能查到最新*/
            userDao.detachAll();


            List<User> list = userDao.queryBuilder()
                    .where(UserDao.Properties.Id.between(0, 13)).limit(5).build().list();
            if(list != null) {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(":::" + list.get(i).toString());
                    list.get(i).setNickname("www");
                }
            }

            user = new User(null, "zhangsanxx","张三111");
            userDao.insert(user);
            user.setNickname("bulasuo");
            userDao.update(user);
            /**清除session缓存,查询才能查到最新*/
//            userDao.detachAll();

//            daoSession = daoMaster.newSession();
//            userDao = daoSession.getUserDao();

            list = userDao.queryBuilder()
                    .where(UserDao.Properties.Id.between(0, 13)).limit(5).build().list();
            if(list != null) {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(":::" + list.get(i).toString());
                }
            }

        });

    }
}
