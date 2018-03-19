package com.abu.xbase.greendao;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.abu.xbase.bean.User;
import com.abu.xbase.greendao.UserDao;

public class UserTest extends AbstractDaoTestLongPk<UserDao, User> {

    public UserTest() {
        super(UserDao.class);
    }

    @Override
    protected User createEntity(Long key) {
        User entity = new User();
        entity.setId(key);
        return entity;
    }

}
