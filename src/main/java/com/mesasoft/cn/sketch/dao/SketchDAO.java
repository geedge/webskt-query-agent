package com.mesasoft.cn.sketch.dao;

import com.mesasoft.cn.sketch.dao.sqlprovider.SketchSqlProvider;
import com.mesasoft.cn.sketch.entity.DomainCategory;
import com.mesasoft.cn.dao.sqlprovider.UserSqlProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SketchDAO {

    /**
     * 通过域名查询
     */
    @Select("select * from domain_category_reputation where fqdn=#{fqdn}")
    DomainCategory getDomainCategoryByFqdn(String usernameOrEmail);

    /**
     * 获取列表
     *
     * @return {@link List}
     */
    @SelectProvider(type = SketchSqlProvider.class, method = "getDomainCategoryListBy")
    List<DomainCategory> getDomainCategoryList(@Param("condition") String condition, @Param("offset") int offset);


    /**
     * 添加一个用户
     *
     * @param domainCategory {@link DomainCategory}
     * @return 是否插入成功
     */
    @Insert("insert into domain_category_reputation(username,real_name,email,password,is_downloadable,is_uploadable,is_deletable," +
            "is_updatable,is_visible) values(#{username},#{realName},#{email},sha2(#{password},256)," +
            "#{isDownloadable},#{isUploadable},#{isDeletable},#{isUpdatable},#{isVisible})")
    boolean insertDomainCategory(DomainCategory domainCategory);

    /**
     * 通过ID更新用户基本信息
     *
     * @param id       编号
     * @param avatar   头像
     * @param realName 真实姓名
     * @param email    邮箱
     * @return 是否更新成功
     */
    @Update("update user set avatar=#{avatar},real_name=#{realName},email=#{email} where id=#{id}")
    boolean updateBasicInfo(@Param("id") int id, @Param("avatar") String avatar, @Param("realName") String realName,
                            @Param("email") String email);



    /**
     * 通过id更新用户登录时间
     *
     * @param id 编号
     * @return {@link Boolean}
     */
    @Update("update user set last_login_time=current_timestamp where id=#{id}")
    boolean updateUserLoginTime(int id);

    /**
     * 更新操作用户权限
     *
     * @param id             用户编号
     * @param isDownloadable 下载权限
     * @param isUploadable   上传权限
     * @param isVisible      可查权限
     * @param isDeletable    删除权限
     * @param isUpdatable    更新权限
     * @return {@link Boolean}
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "updateAuthById")
    boolean updateAuthById(@Param("id") int id, @Param("isDownloadable") int isDownloadable,
                           @Param("isUploadable") int isUploadable, @Param("isDeletable") int isDeletable, @Param(
            "isUpdatable") int isUpdatable, @Param("isVisible") int isVisible);


}
