package com.chenxin.spider.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.chenxin.core.dao.ConnectionManager;
import com.chenxin.spider.entity.User;


public class ZhiHuDAO {
	private static Logger logger = Logger.getLogger(ZhiHuDAO.class);
	
	/**
	 * 数据库表初始化，创建数据表
	 * 如果存在的话，则不创建
	 * @param connection
	 */
	public static void DBTablesInit(Connection connection){
		ResultSet rs = null;
		Properties p = new Properties();
		
		try {
			//加载配置文件
			p.load(ZhiHuDAO.class.getResourceAsStream("/config.properties"));
			rs = connection.getMetaData().getTables(null, null, "url", null);
			Statement st = connection.createStatement();
			//不存在url表
			if(!rs.next()){
				//创建url
				st.execute(p.getProperty("createUrlTable"));
				logger.info("url表创建成功");
			}else{
				logger.info("url表已存在");
			}
			rs = connection.getMetaData().getTables(null, null, "user", null);
			//不存在user表
			if(!rs.next()){
				//创建user表
				st.execute(p.getProperty("createUserTable"));
				logger.info("user表创建成功");
			}else{
				logger.info("user表已存在");
			}
			rs.close();
			st.close();
			
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断该数据库中是否存在该用户
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public synchronized static boolean isExistRecord(String sql) throws SQLException{
		int num = 0;
		PreparedStatement pstmt;
		pstmt = ConnectionManager.getConnection().prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			num = rs.getInt("count(*)");
		}
		rs.close();
		pstmt.close();
		
		if(num == 0){
			return false;
		}else{
		return false;
		}
	}
	/**
	 * 是否存在该用户
	 * @param userToken
	 * @return
	 */
	public synchronized static boolean isExistUser(String userToken){
		String isContainSql  = "select count(*) from user where user_token="+"'"+userToken+"'";
		
		try {
			if(!isExistRecord(isContainSql)){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	public synchronized static boolean insertUser(User u){
		try {
            if (isExistUser(u.getUserToken())){
                return false;
            }
            String column = "location,business,sex,employment,username,url,agrees,thanks,asks," +
                    "answers,posts,followees,followers,hashId,education,user_token";
            String values = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
            String sql = "insert into user (" + column + ") values(" +values+")";
            PreparedStatement pstmt;
            pstmt = ConnectionManager.getConnection().prepareStatement(sql);
            pstmt.setString(1,u.getLocation());
            pstmt.setString(2,u.getBusiness());
            pstmt.setString(3,u.getSex());
            pstmt.setString(4,u.getEmployment());
            pstmt.setString(5,u.getUsername());
            pstmt.setString(6,u.getUrl());
            pstmt.setInt(7,u.getAgrees());
            pstmt.setInt(8,u.getThanks());
            pstmt.setInt(9,u.getAsks());
            pstmt.setInt(10,u.getAnswers());
            pstmt.setInt(11,u.getPosts());
            pstmt.setInt(12,u.getFollowees());
            pstmt.setInt(13,u.getFollowers());
            pstmt.setString(14,u.getHashId());
            pstmt.setString(15,u.getEducation());
            pstmt.setString(16,u.getUserToken());
            pstmt.executeUpdate();
            pstmt.close();
            logger.info("插入数据库成功---" + u.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            ConnectionManager.close();
        }
        return true;
	}
	

}
