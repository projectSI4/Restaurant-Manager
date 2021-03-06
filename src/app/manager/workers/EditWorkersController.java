package app.manager.workers;

import app.main.Main;
import app.main.StageProperty;
import connectivity.ConnectionClass;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EditWorkersController extends Main
{
    public AnchorPane anchorPane;
    public Button submitButton;
    public TextField newUserName;
    public TextField newPassword;
    public ChoiceBox<String> choiceBox;
    public Hyperlink back;
    public Hyperlink logOut;
    public Label loggedAs;
    static String userNameDB;
    public Label userName;
    public Label password;
    public Label position;

    /**
     * set initial properties
     * disable focusing cursor on TextField
     */
    public void initialize() {
        loggedAs.setText("Logged as: " + Main.loggedAs);

        userName.setText(userNameDB);

        choiceBox.getItems().add("Logistician");
        choiceBox.getItems().add("Waiter");
        choiceBox.getItems().add("Accountant");
        choiceBox.getItems().add("Manager");
        choiceBox.setValue(fillLabels());

        newUserName.setFocusTraversable(false);
        newPassword.setFocusTraversable(false);
    }

    public void backAction() {
        StageProperty.loadView("workers", anchorPane, this.getClass());
    }

    public void logOutAction() {
        StageProperty.loadView("login", anchorPane, this.getClass());
    }

    /**
     * fills labels with information from DB
     * returns position
     *
     * @return
     */
    private String fillLabels() {
        password.setText(getInfo("password"));
        String result = getInfo("position");
        position.setText(result);

        return result;
    }

    /**
     * get given attribute from DB
     *
     * @param attribute
     * @return
     */
    private String getInfo(String attribute) {
        Connection connection = new ConnectionClass().getConnection();
        String result = null;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT " + attribute + " FROM users WHERE name='" + userNameDB + "';";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                result = resultSet.getString(1);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * save new data if were given
     */
    public void submitAction() {
        if (!newUserName.getText().equals("")) {
            Connection connection = new ConnectionClass().getConnection();
            Statement statement;
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT name FROM users;");
                while (resultSet.next()) {
                    if (resultSet.getString(1).equals(newUserName.getText())) {
                        newUserName.clear();
                        newUserName.setPromptText("User name is already taken!");
                        newUserName.setStyle("-fx-prompt-text-fill: #ff0000");
                        newPassword.clear();
                        connection.close();
                        return;
                    }
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            setInfo("name", newUserName.getText());
            userNameDB = newUserName.getText();
        }

        if (!newPassword.getText().equals("")) {
            setInfo("password", newPassword.getText());
        }

        setInfo("position", choiceBox.getValue());

        backAction();
    }

    /**
     * set given attribute with value
     *
     * @param attribute
     * @param value
     */
    private void setInfo(String attribute, String value) {
        Connection connection = new ConnectionClass().getConnection();
        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE users SET " + attribute + "=" + "'" + value + "'" + " WHERE name='" + userNameDB + "';";
            statement.executeUpdate(sql);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void workersClicked()
    {
        StageProperty.loadView("workers", anchorPane, this.getClass());
    }

    public void logOutClicked()
    {
        StageProperty.loadView("login", anchorPane, this.getClass());
    }

    public void dishesClicked()
    {
        StageProperty.loadView("dishes", anchorPane, this.getClass());
    }
}
