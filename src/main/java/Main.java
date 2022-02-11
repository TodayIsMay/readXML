import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String dbName;
    private static String user;
    private static String password;
    private static Connection con;

    public static void main(String[] args) {
        System.out.println("Database name:");
        dbName = scanner.nextLine();
        System.out.println("Username:");
        user = scanner.nextLine();
        System.out.println("Password:");
        password = scanner.nextLine();
        String url = "jdbc:mysql://localhost:3306/" + dbName;
        try {
            con = DriverManager.getConnection(url, user, password);
            createTable();//создает таблицу в БД, можно сделать выполнение по команде
            insert();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
           try {
               con.close();
            } catch (SQLException se) {
            }
        }
        System.exit(0);
    }

    /* здесь и в следующем методе можно сделать названия столбцов
       считываемыми из консоли */
    public static void createTable() {
        try {
            Statement createTable = con.createStatement();
            String create = "CREATE TABLE books" +
                    "(book_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "author VARCHAR(50)," +
                    "title VARCHAR(50)," +
                    "genre VARCHAR(50)," +
                    "price DECIMAL(8,2)," +
                    "publish_date DATE," +
                    "description VARCHAR(250));";
            Statement setUnique = con.createStatement();
            String set = "ALTER TABLE books ADD UNIQUE uniq(author, title);";
            createTable.execute(create);
            setUnique.execute(set);
            System.out.println("Table created");
        } catch (SQLException e) {
            System.out.println("Table is already exist");
        }
    }

    public static void insert() {
        File file = new File("book.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document xmlDoc = builder.parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            Object res = xpath.evaluate("/catalog/book",
                    xmlDoc,
                    XPathConstants.NODESET);
            String str = "INSERT IGNORE INTO books(\n" +
                    "  author, title, genre, price,\n" +
                    "  publish_date, description)\n" +
                    "VALUES";
            Statement insertInto = con.createStatement();
            StringBuilder insert = new StringBuilder(str);
            StringBuilder buffer = new StringBuilder();
            NodeList nlist = (NodeList) res;
            char bracket = '"';
            for (int i = 0; i < nlist.getLength(); i++) {
                Node node = nlist.item(i);
                List<String> columns = Arrays
                        .asList(getTextContent(node, "author"),
                                getTextContent(node, "title"),
                                getTextContent(node, "genre"),
                                getTextContent(node, "price"),
                                getTextContent(node, "publish_date"),
                                getTextContent(node, "description"));
                buffer.append("(");
                for (int n = 0; n < columns.size(); n++) {
                    try {
                        Integer.parseInt(columns.get(n));
                        buffer.append(columns.get(n)).append(",");
                    } catch (NumberFormatException e) {
                        buffer.append(bracket).append(columns.get(n)).append(bracket).append(",");
                    }
                }
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("),");
                insert.append(buffer);
            }
            insert.deleteCharAt(insert.length() - 1);
            insertInto.execute(insert.toString());
            System.out.println("Inserting completed");
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | SQLException e) {
            e.printStackTrace();
        }
    }

    static private String getTextContent(Node parentNode, String childName) {
        NodeList nlist = parentNode.getChildNodes();
        for (int i = 0; i < nlist.getLength(); i++) {
            Node n = nlist.item(i);
            String name = n.getNodeName();
            if (name != null && name.equals(childName))
                return n.getTextContent();
        }
        return "";
    }
}