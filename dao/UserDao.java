package com.ning.nsfw.dao;

import com.ning.nsfw.entity.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wangn on 2017/3/29.
 */
public interface UserDao {
    //显示所有的用户
    public List<User> showUsers();
    //根据id查找用户
    public User findUserById(Serializable id);
    //根据id删除用户
    public void deleteById(Serializable id);
    //增加一个用户
    public void insertUser(User user);
    //修改某用户
    public void editUser(User user);
    //分页查询
    public List<User> showUsersByLimit(Map<String,Integer> map);
    //用户数量
    public int showUsersSize();
    //根据用户名查找用户
    public List<User> findUsersByName(String name);
}
