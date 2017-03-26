package com.mingyizhudao.qa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; 

public class MysqlConnector {
	public static final String driver = "com.mysql.cj.jdbc.Driver";
    public static String url = "jdbc:mysql://121.41.82.86:3306/myzd-test";
    public static String username = "root";
    public static String password = "myzd_@!@#123456";


    public static Connection conn = null;
    public static PreparedStatement pst = null;
    public static ResultSet ret=null;


	public static void main(String[] args) {
	    Connect();
	    Query("select * from user where id = 999");
	    Close();
    }
//
//	public MysqlConnector(String url, String user, String password) {
//		this.url=url;
//		this.user=user;
//		this.password=password;
//	}
	
	public static void Connect() {
		try {
			Class.forName(driver);//指定连接类型
			conn = DriverManager.getConnection(url, username, password);//获取连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Query(String sql) {
		try {
			pst = conn.prepareStatement(sql);//准备执行语句
			ret = pst.executeQuery();//执行语句，得到结果集
			//显示数据
			while (ret.next()) {
				System.out.println(ret.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void Close() {
		try {
			ret.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
