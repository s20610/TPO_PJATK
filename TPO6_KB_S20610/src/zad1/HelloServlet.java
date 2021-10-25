package zad1;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class mySevlet
 */
@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {
    private final String databaseURL = "jdbc:derby://localhost:1527/bookdb";
    private Connection connection;
    private String Menu;
    private PrintWriter pW;
    private boolean isByBook;
    private String tableContent;
    private int counter;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            //D:\ForTPO\db-derby-10.13.1.1-lib\lib>java -jar derbyrun.jar server start
            pW = response.getWriter();
            tableContent = "<html> <head> <style> table { font-family: arial, sans-serif; border-collapse: collapse;"
                    + "width: 80%; margin-left:auto;margin-right:auto;} th{background-color: #cfd9e5;} tr:hover{background-color: #cfd9e5;} td, th { border: 1px solid #000000; padding: 4px; height: 10px } tr:nth-child(even) {"
                    + "background-color: #6c7c93; } </style> </head> <body> <table> <tr> <th>AUTOR</th>"
                    + "<th>TYTUL</th> <th>ROK</th> <th>CENA</th> </tr>";

            Menu = " <title>WEBAPP</title> <style>input[type=text]:focus{width:30%;}</style>" +
                    "<body style=\"background-color: #a3cde3;\"> " +
                    "<div style=\"text-align: center;\"> " +
                    "<h1 style=\"font-size: 30px;font-family: Arial,sans-serif\">Baza ksiazek derby</h1><p>Klikniecie  w wyszukaj nie wprowadzajac danych wyswietli wszystkie ksiazki</p> " +
                    "<form  text-align: center;"
                    + " action = hello-servlet>\r\n " +
                    "<input type=\"text\" name =\"name\" style=\"background-color: white;background-image: url('http://assets.stickpng.com/images/585e4ad1cb11b227491c3391.png');background-position: 10px 10px;background-repeat: no-repeat;padding-left: 30px;transition: width 0.4s ease-in-out\">\r\n " +
                    "<select name = \"po\">\r\n"
                    + "<option>Wyszukiwanie po autorze</option>\r\n " +
                    "<option>Wyszukiwanie po nazwie"
                    + "</option>\r\n </select>\r\n " +
                    "<input type=\"submit\" value=\"Wyszukaj\">\r\n \r\n </form> " +
                    "</body> </div>";
            pW.println(Menu);

            String[][] books;

            if (!request.getParameter("name").isEmpty()) {
                isByBook = !request.getParameter("po").equalsIgnoreCase("Wyszukiwanie po autorze");
                pW = response.getWriter();
                books = fetchBooks(request.getParameter("name"));
            } else {
                pW = response.getWriter();
                books = fetchBooks("wzystkieKsiazki");
            }
            write(books);
            pW.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String[][] fetchBooks(String name) {
        counter = 0;
        String[][]books;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection(databaseURL);
            connection.getMetaData();
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(
                    "SELECT AUTOR.NAME, POZYCJE.TYTUL, POZYCJE.ROK, POZYCJE.CENA FROM AUTOR INNER JOIN POZYCJE ON POZYCJE.AUTID = AUTOR.AUTID");
            rs.afterLast();
            while (rs.previous()) {
                counter++;
            }
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        books = new String[counter][4];
        if (name.equals("wzystkieKsiazki")) {
            fetchRow("SELECT AUTOR.NAME, POZYCJE.TYTUL,POZYCJE.ROK, POZYCJE.CENA  FROM autor, pozycje where POZYCJE.AUTID = AUTOR.AUTID ORDER BY AUTOR.NAME DESC", books);
        } else if (isByBook) {
            fetchRow("SELECT AUTOR.NAME, POZYCJE.TYTUL,POZYCJE.ROK, POZYCJE.CENA  FROM autor, pozycje where POZYCJE.AUTID = AUTOR.AUTID AND POZYCJE.TYTUL LIKE '"
                    + name + "%' ORDER BY AUTOR.NAME DESC", books);
        } else {
            fetchRow("SELECT AUTOR.NAME, POZYCJE.TYTUL,POZYCJE.ROK, POZYCJE.CENA  FROM autor, pozycje where POZYCJE.AUTID = AUTOR.AUTID AND AUTOR.NAME LIKE '"
                    + name + "%'", books);
        }
        return books;
    }

    private void fetchRow(String s, String[][] books){
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection(databaseURL);
            connection.getMetaData();
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(s);
            rs.afterLast();
            int rowCounter = 0;
            while (rs.previous()) {
                books[rowCounter][0] = rs.getString(1);
                books[rowCounter][1] = rs.getString(2);
                books[rowCounter][2] = rs.getString(3);
                books[rowCounter][3] = rs.getString(4);
                rowCounter++;
            }
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(String[][] books){
        for (String[] s : books) {
            tableContent += "<tr>";
            for (String ss : s) {
                if (ss != null) {
                    tableContent += "<td>"+ss+"</td>";
                }
            }
            tableContent += "</tr>";
        }
        tableContent += "</table> </body>";
        pW.println(tableContent);
    }

}