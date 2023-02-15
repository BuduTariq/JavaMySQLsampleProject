import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

//install XAMPP and run Apache and SQL servers
//add new DB call mystore
//add new table called users
//inside SQL of users, add the following lines of code to add columns:
//CREATE TABLE IF NOT EXISTS users (id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,
// name VARCHAR(200) NOT NULL, email VARCHAR(200) NOT NULL UNIQUE, phone VARCHAR(200),
// address VARCHAR(200), password VARCHAR(200) NOT NULL)
//Then run this application; If DB connection fails, registration fails aswell.
//DB files are stored in: C:\xampp\mysql\data


public class RegistrationForm extends JDialog{
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JPasswordField txtPassword;
    private JPasswordField txtCPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel panelRegister;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(panelRegister);
        setMinimumSize(new Dimension(600, 474));
        setModal(true);
        setLocationRelativeTo(parent);
//        setVisible(true); moved to the end to call dispose() on btnCancel;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); //stop the app when CLOSE is clicked on the dialogue;

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String address = txtAddress.getText();
        String password = String.valueOf(txtPassword.getText());
        String cPassword = String.valueOf(txtCPassword.getText());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try Again",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (!password.matches(cPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try Again",
                    JOptionPane.ERROR_MESSAGE);
        }

        user = addUserToDatabase(name, email, phone, address, password);
        if (user!= null){
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try Again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        //connect to the database:
        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME= "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //If the above statement runs, then we are successfully connected to DB;
            //add SQL statement to add new User in SQL
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, address, password) "+
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            //execute the sql query:
            //insert row into the table:
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            //close the connection
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        return user;
    }


    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        //testing the application; if myForm object is null, then registration has been cancelled;
        User user = myForm.user;
        if (user !=null) {
            System.out.println("Successful registration of: "+user.name);
        }
        else {
            System.out.println("Registration canceled");
        }
    }


}
