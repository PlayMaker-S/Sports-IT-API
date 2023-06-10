package PlayMakers.SportsIT;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@EnableAsync
@EnableScheduling
@EnableAdminServer
@SpringBootApplication
public class SportsItApplication {

	public static void main(String[] args) {
//		Connection conn = null;
//		String server = "localhost"; // MySQL 서버 주소
//		String database = "sportsit_test"; // MySQL DATABASE 이름
//		String user_name = "root"; //  MySQL 서버 아이디
//		String password = "1234"; // MySQL 서버 비밀번호
//
//		// mysql 드라이버 로딩
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			log.error(" !! <JDBC 오류> Driver load 오류: " + e.getMessage());
//			e.printStackTrace();
//		}
//
//		// mysql 연결
//		try {
//			conn = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
//			log.info("정상적으로 연결되었습니다.");
//		} catch (SQLException e) {
//			log.error("연결 오류:" + e.getMessage());
//		} finally { // 연결 종료
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				log.error(e.getMessage());
//			}
//		}
		SpringApplication.run(SportsItApplication.class, args);
	}

}
