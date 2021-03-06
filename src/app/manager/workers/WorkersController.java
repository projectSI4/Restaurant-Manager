package app.manager.workers;

import app.main.Main;
import app.main.StageProperty;
import app.manager.calendar.FullCalendarView;
import connectivity.ConnectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;


public class WorkersController
{
    public AnchorPane anchorPane;
    public Hyperlink backButton;
    public Button createAccountButton;
    public Hyperlink logOutButton;
    public Label loggedAs;
    public TableView tableView;
    public TextField search;
    private ArrayList<String> name = getWorkersInfo("name");
    private ArrayList<String> position = getWorkersInfo("position");

    public void initialize()
    {
        loggedAs.setText(Main.loggedAs);
        tableView.setEditable(true);
        listWorkers();
    }

    public void backAction()
    {
        StageProperty.loadView("manager", anchorPane, this.getClass());
    }

    public void logOutAction()
    {
        StageProperty.loadView("login", anchorPane, this.getClass());
    }

    public void createAccountAction()
    {
        StageProperty.loadView("register", anchorPane, this.getClass());
    }

    /**
     * get data about workers
     * and list all workers
     */
    private void listWorkers()
    {
        TableColumn nameCol = new TableColumn("Name");
        TableColumn<Object, Object> positionCol = new TableColumn<>("Position");

        nameCol.setPrefWidth(tableView.getPrefWidth() / 2);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        positionCol.setPrefWidth(tableView.getPrefWidth() / 2);
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        tableView.getColumns().addAll(nameCol, positionCol);

        tableView.setRowFactory(tv ->
        {
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent ->
            {
                if( !row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1 )
                    showContextMenu(row.getIndex(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
            });
            return row;
        });

        ObservableList<Person> data = FXCollections.observableArrayList();

        for( int i = 0; i < name.size(); i++ )
        {
            data.add(new Person(name.get(i), position.get(i)));
        }

        FilteredList<Person> flPerson = new FilteredList(data, p -> true);
        tableView.setItems(flPerson);

        search.setOnKeyReleased(keyEvent ->
                flPerson.setPredicate(p -> p.getPosition().toLowerCase().contains(search.getText().toLowerCase().trim()))
        );

        if( !loggedAs.getText().contains("Manager") )
        {
            createAccountButton.setVisible(false);
        }
    }

    /**
     * create context menu for one row
     *
     * @param index
     * @param X
     * @param Y
     */
    private void showContextMenu( int index, double X, double Y )
    {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        MenuItem schedule = new MenuItem("Schedule");

        edit.setOnAction(actionEvent1 ->
        {
            EditWorkersController.userNameDB = name.get(index);
            StageProperty.loadView("editWorkers", anchorPane, this.getClass());
        });

        schedule.setOnAction(actionEvent1 ->
        {
            Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
            StageProperty.loadStage( new FullCalendarView(YearMonth.now(), name.get(index) ).getView() );
            primaryStage.close();
        });

        if( loggedAs.getText().contains("Manager") )
        {
            contextMenu.getItems().addAll(edit);
        }
        contextMenu.getItems().addAll(schedule);


        contextMenu.show(anchorPane, X, Y);
        anchorPane.setOnMousePressed(mouseEvent -> contextMenu.hide());
        tableView.setOnMousePressed(mouseEvent -> contextMenu.hide());
    }

    /**
     * get data form DB
     *
     * @param attribute
     * @return
     */
    public static ArrayList<String> getWorkersInfo( String attribute )
    {
        ArrayList<String> result = new ArrayList<>();
        Connection connection = new ConnectionClass().getConnection();
        try
        {
            Statement statement = connection.createStatement();
            String sql = "SELECT " + attribute + " FROM users;";
            ResultSet resultSet = statement.executeQuery(sql);
            while( resultSet.next() )
                result.add(resultSet.getString(1));
            connection.close();
        } catch( SQLException e )
        {
            e.printStackTrace();
        }

        return result;
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