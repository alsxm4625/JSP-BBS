package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
	private Connection conn; //자바와 데이터베이스를 연결
	private ResultSet rs; // 결과값 받아오기
	
	//기본 생성자
	//UserDAO가 실행되면 자동으로 생성되는 부분
	//메소드마다 반복되는 코드를 이곳에 넣으면 코드가 간소화된다
	public BbsDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost:3306/BBS?serverTimezone=UTC";
			String dbID = "root";
			String dbPassword = "rootpw";
			Class.forName("com.mysql.jdbc.Driver"); 
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword); //연결 conn객체안에 접속된 정보 담김
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getDate() {
	  	String SQL = "SELECT NOW()"; // 현재 시간 가져오기
	  	try {
	  		PreparedStatement pstmt = conn.prepareStatement(SQL);
	  		rs = pstmt.executeQuery();
	  		if (rs.next()) {
	  			return rs.getString(1);
	  		}
	  	} catch(Exception e) {
	  		e.printStackTrace();
	  	}
	  	return "";
	  }

	  public int getNext() { // 다음 글 가지고 오기.
	  	String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
	  	try {
	  		PreparedStatement pstmt = conn.prepareStatement(SQL);
	  		rs = pstmt.executeQuery();
	  		if (rs.next()) {
	  			return rs.getInt(1) + 1;
	  		}
	  		return 1; // 첫 번째 게시물인 경우
	  	} catch(Exception e) {
	  		e.printStackTrace();
	  	}
	  	return -1; // 데이터베이스 오류
	  }

	  public int write(String bbsTitle, String userID, String bbsContent) {
	  	String SQL = "INSERT INTO BBS VALUES (?, ?, ?, ?, ?, ?)";
	  	try {
	  		PreparedStatement pstmt = conn.prepareStatement(SQL);
	  		pstmt.setInt(1, getNext());
	  		pstmt.setString(2, bbsTitle);
	  		pstmt.setString(3, userID);
	  		pstmt.setString(4, getDate());
	  		pstmt.setString(5, bbsContent);
	  		pstmt.setInt(6, 1);
	  		return pstmt.executeUpdate();
	  	} catch(Exception e) {
	  		e.printStackTrace();
	  	}
	  	return -1; // 데이터베이스 오류
	  }

public ArrayList<Bbs> getList(int pageNumber) {
	String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10";
	ArrayList<Bbs> list = new ArrayList<Bbs>();
	try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, getNext() - (pageNumber -1) * 10);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			Bbs bbs = new Bbs();
			bbs.setBbsID(rs.getInt(1));
			bbs.setBbsTitle(rs.getString(2));
			bbs.setUserID(rs.getString(3));
			bbs.setBbsDate(rs.getString(4));
			bbs.setBbsContent(rs.getString(5));
			bbs.setBbsAvailable(rs.getInt(1));
			list.add(bbs);
		}			
	} catch(Exception e) {
		e.printStackTrace();
	}
	return list;
}

public boolean nextPage(int pageNumber) {
	String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1";

	try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, getNext() - (pageNumber -1) * 10);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			return true;
		}			
	} catch(Exception e) {
		e.printStackTrace();
	}
	return false;
}

public Bbs getBbs(int bbsID) {
	String SQL = "SELECT * FROM BBS WHERE bbsID = ?";

	try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, bbsID);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			Bbs bbs = new Bbs();
			bbs.setBbsID(rs.getInt(1));
			bbs.setBbsTitle(rs.getString(2));
			bbs.setUserID(rs.getString(3));
			bbs.setBbsDate(rs.getString(4));
			bbs.setBbsContent(rs.getString(5));
			bbs.setBbsAvailable(rs.getInt(1));
			return bbs;
		}			
	} catch(Exception e) {
		e.printStackTrace();
	}
	return null;
}
public int update(int bbsID, String bbsTitle, String bbsContent) {
	String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID =?";
	try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, bbsTitle);
		pstmt.setString(2, bbsContent);
		pstmt.setInt(3, bbsID);

		return pstmt.executeUpdate();
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1; // 데이터베이스 오류
}

public int delete(int bbsID) {
	String SQL = "DELETE FROM bbs WHERE bbsID = ?";
	try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, bbsID);

		return pstmt.executeUpdate();
	} catch(Exception e) {
		e.printStackTrace();
	}
	return -1; // 데이터베이스 오류
}
}
