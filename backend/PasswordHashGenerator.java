import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt 哈希: " + hashedPassword);
        System.out.println("\n请在数据库中执行以下 SQL 更新:");
        System.out.println("UPDATE users SET password = '" + hashedPassword + "' WHERE email = 'admin@syit.edu.cn';");
    }
}
