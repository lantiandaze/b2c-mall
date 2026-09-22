package com.b2cmall.employee.dao;
import com.b2cmall.employee.dao.po.EmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;
import com.b2cmall.employee.service.event.LoginEvent;
@Mapper
public interface EmployeeMapper {
    int save(EmployeePO employee);
    EmployeePO find(EmployeePO employee);
    @Select("SELECT id,shop_id AS shopId,username,password,avatar_url AS avatarUrl,status,last_login_time AS lastLoginTime,login_count AS loginCount FROM tb_employee WHERE id=#{id}")
    EmployeePO findById(Long id);
    @Update("UPDATE tb_employee SET last_login_time=CURRENT_TIMESTAMP,login_count=COALESCE(login_count,0)+1,updated_at=CURRENT_TIMESTAMP WHERE id=#{id}")
    int updateLoginStatus(Long id);
    @Insert("INSERT INTO tb_login_logs(user_id,login_ip,login_device,login_location,user_agent,status) VALUES(#{userId},#{ip},#{device},#{location},#{device},#{success})")
    int saveLoginLog(LoginEvent event);
}
