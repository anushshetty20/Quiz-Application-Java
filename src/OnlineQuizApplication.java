package quizApplication;

import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OnlineQuizApplication {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";// Use your own password
    static Connection conn;


    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Scanner scanner = new Scanner(System.in);



            while(true){
                System.out.println("1. admin\n2. User");
                System.out.print("Enter your choice: ");
                int n = scanner.nextInt();
                scanner.nextLine();

                switch(n){
                    case 1: String admin_name = "admin";//admin name for admin login
                            String admin_pass = "admin@123";// admin password for admin login
                            System.out.print("Enter admin name: ");
                            String adminName = scanner.nextLine();
                            System.out.print("Enter admin password: ");
                            String adminPass = scanner.nextLine();

                            if(adminName.equals(admin_name)  && adminPass.equals(admin_pass)) {
                                System.out.println("Admin login successful!..");
                                System.out.println();
                                adminFunction(scanner);
                            }else{
                                System.out.println("Wrong Admin Name or Password!...");
                                return;
                            }



                    case 2: while (true) {

                                System.out.println("1. Register\n2. Login\n3. Exit");
                                System.out.print("Choose an option: ");
                                int choice = scanner.nextInt();
                                scanner.nextLine();  // consume newline

                                switch (choice) {
                                    case 1 -> registerUser(conn, scanner);
                                    case 2 -> loginUser(conn, scanner);
                                    case 3 -> System.exit(0);
                                    default -> System.out.println("Invalid choice. Try again.");
                                }
                            }

                    default : System.out.println("Invalid choice!.");
                                break;
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(Connection conn, Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password_hash) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.executeUpdate();
            System.out.println("User registered successfully!");
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Error registering user. Username might already be taken.");
        }
    }

    private static void loginUser(Connection conn, Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password_hash = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println("Login successful!");
                System.out.println();
                userMenu(conn, scanner, userId);
            } else {
                System.out.println("Invalid username or password.");
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userMenu(Connection conn,Scanner scanner, int userId) {
        while (true) {
            System.out.println("\n1. Take Quiz\n2. View Quiz History\n3. Logout");
            System.out.println();
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // consume newline
            System.out.println();
            switch (choice) {
                case 1 -> takeQuiz(conn, scanner, userId);
                case 2 -> viewQuizHistory(conn, userId);
                case 3 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void adminFunction(Scanner scanner){
        try{
            while(true){
                System.out.println("1. Add New Quiz Category.\n2. Add Quiz Question.\n3. Delete Quiz question.\n4. Exit.");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch(choice){
                    case 1: Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT quiz_id, title FROM quizzes");
                            System.out.println("Available quizzes:");
                            while (rs.next()) {
                                System.out.println(rs.getInt("quiz_id") + ". " + rs.getString("title"));
                            }
                            System.out.println("Enter Quiz Title which needs to be added: ");
                            String QTitle = scanner.nextLine();
                            PreparedStatement stmt5 = conn.prepareStatement("INSERT INTO quizzes (title) VALUES (?)");
                            stmt5.setString(1, QTitle);
                            stmt5.executeUpdate();
                            System.out.println("Quiz category added successfully!");
                            System.out.println();
                            break;

                    case 2: Statement stmt6 = conn.createStatement();
                            ResultSet rs1 = stmt6.executeQuery("SELECT quiz_id, title FROM quizzes");
                            System.out.println("Available quizzes:");
                            while (rs1.next()) {
                                System.out.println(rs1.getInt("quiz_id") + ". " + rs1.getString("title"));
                            }
                            System.out.println();
                            System.out.print("Choose a quiz ID to add question: ");
                            int quizId = scanner.nextInt();
                            System.out.println("Enter quiz question: ");
                            scanner.next();
                            String quizQuestion = scanner.nextLine();
                            System.out.print("Enter option 1: ");
                            String op1 = scanner.nextLine();
                            System.out.print("Enter option 2: ");
                            String op2 = scanner.nextLine();
                            System.out.print("Enter option 3: ");
                            String op3 = scanner.nextLine();
                            System.out.print("Enter option 4: ");
                            String op4 = scanner.nextLine();
                            System.out.print("Enter correct option:(1-4) ");
                            int cop = scanner.nextInt();
                            scanner.nextLine();
                            PreparedStatement stmt7 = conn.prepareStatement("INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            stmt7.setInt(1, quizId);
                            stmt7.setString(2, quizQuestion);
                            stmt7.setString(3, op1);
                            stmt7.setString(4, op2);
                            stmt7.setString(5, op3);
                            stmt7.setString(6, op4);
                            stmt7.setInt(7, cop);
                            stmt7.executeUpdate();
                            System.out.println("questions added successfully!");
                            System.out.println();
                            break;

                    case 3: Statement stmt8 = conn.createStatement();
                            ResultSet rs2 = stmt8.executeQuery("SELECT quiz_id, title FROM quizzes");
                            System.out.println("Available quizzes:");
                            while (rs2.next()) {
                                    System.out.println(rs2.getInt("quiz_id") + ". " + rs2.getString("title"));
                            }
                            System.out.println();
                            System.out.print("Choose a quiz ID to delete question: ");
                            int quizDeleteId = scanner.nextInt();


                            PreparedStatement stmt9 = conn.prepareStatement("SELECT question_id, question_text FROM questions where quiz_id = ?");
                            stmt9.setInt(1, quizDeleteId);
                            ResultSet rs3 = stmt9.executeQuery();
                            while (rs3.next()) {
                                System.out.println(rs3.getInt("question_id") + ". " + rs3.getString("question_text"));
                            }
                            System.out.println("Enter question_id which needs to be deleted: ");
                            int delete_question_id = scanner.nextInt();
                            PreparedStatement stmt10 = conn.prepareStatement(" delete from questions where question_id = ?");
                            stmt10.setInt(1, delete_question_id);
                            stmt10.executeUpdate();
                            System.out.println("Question deleted successfully!.");
                            System.out.println();
                            break;
                    case 4 : System.exit(0);
                    default : System.out.println("Invalid choice!..");
                            System.out.println();
                                break;

                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }


    }

    private static void takeQuiz(Connection conn, Scanner scanner, int userId) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT quiz_id, title FROM quizzes")) {

            System.out.println("Available quizzes:");
            while (rs.next()) {
                System.out.println(rs.getInt("quiz_id") + ". " + rs.getString("title"));
            }
            System.out.println();
            System.out.print("Choose a quiz ID to take: ");
            int quizId = scanner.nextInt();
            scanner.nextLine();  // consume newline
            System.out.println();
            int score = 0, questionCount = 0;
            try (PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?")) {
                stmt2.setInt(1, quizId);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    questionCount++;
                    System.out.println(rs2.getString("question_text"));
                    System.out.println("1. " + rs2.getString("option1"));
                    System.out.println("2. " + rs2.getString("option2"));
                    System.out.println("3. " + rs2.getString("option3"));
                    System.out.println("4. " + rs2.getString("option4"));
                    System.out.print("Enter your answer (1-4): ");
                    int answer = scanner.nextInt();
                    scanner.nextLine();  // consume newline
                    if (answer == rs2.getInt("correct_option")) {
                        score++;
                        System.out.println("Correct!");
                        System.out.println();
                    } else {
                        System.out.println("Incorrect.");
                        System.out.println();
                    }
                }
            }

            int finalScore = (questionCount > 0) ? (score * 100 / questionCount) : 0;
            System.out.println("Quiz Completed! Your score: " + finalScore + "%");

            // Save the result
            try (PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO quiz_results (user_id, quiz_id, score) VALUES (?, ?, ?)")) {
                stmt3.setInt(1, userId);
                stmt3.setInt(2, quizId);
                stmt3.setInt(3, finalScore);
                stmt3.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewQuizHistory(Connection conn, int userId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT quizzes.title, quiz_results.score, quiz_results.attempt_date " +
                "FROM quiz_results JOIN quizzes ON quiz_results.quiz_id = quizzes.quiz_id " +
                "WHERE quiz_results.user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nQuiz History:");
            System.out.println();
            while (rs.next()) {
                System.out.println("Quiz: " + rs.getString("title") + "\nScore: " + rs.getInt("score") + "%\nDate: " + rs.getTimestamp("attempt_date"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] salt = "SALT".getBytes();  // In a real app, use a more secure, random salt
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

