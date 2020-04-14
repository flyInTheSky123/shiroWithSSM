package com.person.service.impl;

import com.person.mapper.PermissionMapper;
import com.person.mapper.RolePermissionMapper;
import com.person.pojo.*;
import com.person.service.PermissionService;
import com.person.service.RoleService;
import com.person.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionMapper permissionMapper;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    RolePermissionMapper rolePermissionMapper;

    @Override
    public Set<String> listPermissions(String userName) {
        Set<String> result = new HashSet<>();
        List<Role> roles = roleService.listRoles(userName);

        List<RolePermission> rolePermissions = new ArrayList<>();

        for (Role role : roles) {
            RolePermissionExample example = new RolePermissionExample();
            example.createCriteria().andRidEqualTo(role.getId());
            List<RolePermission> rps = rolePermissionMapper.selectByExample(example);
            rolePermissions.addAll(rps);
        }

        for (RolePermission rolePermission : rolePermissions) {
            Permission p = permissionMapper.selectByPrimaryKey(rolePermission.getPid());
            result.add(p.getName());
        }

        return result;
    }

    @Override
    public void add(Permission u) {
        permissionMapper.insert(u);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Permission u) {
        permissionMapper.updateByPrimaryKeySelective(u);
    }

    @Override
    public Permission get(Long id) {
        return permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Permission> list() {
        PermissionExample example = new PermissionExample();
        example.setOrderByClause("id desc");
        return permissionMapper.selectByExample(example);

    }

    @Override
    public List<Permission> list(Role role) {
        List<Permission> result = new ArrayList<>();
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andRidEqualTo(role.getId());
        List<RolePermission> rps = rolePermissionMapper.selectByExample(example);
        for (RolePermission rolePermission : rps) {
            result.add(permissionMapper.selectByPrimaryKey(rolePermission.getPid()));
        }

        return result;
    }

    //通过username 获取权限。
    @Override
    public Set<String> listPermissionsURLS(String username) {

        //用来装权限名
        Set<String> result = new HashSet<>();
        //通过username ,获取角色role
        List<Role> roles = roleService.listRoles(username);
        //通过角色，获取权限名称
        ArrayList<RolePermission> rps = new ArrayList<>();

        for (Role r : roles) {
            //
            RolePermissionExample rolePermissionExample = new RolePermissionExample();
            rolePermissionExample.createCriteria().andRidEqualTo(r.getId());

            //获取角色权限数据
            List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
            //将权限数据装入集合。
            rps.addAll(rolePermissions);

        }
        for (RolePermission rp : rps) {
            Permission permission = permissionMapper.selectByPrimaryKey(rp.getPid());
            result.add(permission.getName());
        }

        return result;
    }


    //判断当前的requestUrl是否需要拦截。
    @Override
    public boolean needInterceptor(String requestURL) {
        List<Permission> lists = list();
        for (Permission p:lists){
            if (p.getUrl().equals(requestURL)){
                return  true;
            }
        }
        return false;
    }

}