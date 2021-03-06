package app.manager.calendar;

import app.main.StageProperty;
import connectivity.ConnectionClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;


public class FullCalendarView
{
    private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
    private VBox view;
    private Text calendarTitle;
    private YearMonth currentYearMonth;
    private String worker;

    /**
     * Create a calendar view
     *
     * @param yearMonth year month to create the calendar of
     */
    public FullCalendarView( YearMonth yearMonth, String worker )
    {
        currentYearMonth = yearMonth;
        this.worker = worker;
        // Create the calendar grid pane
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600, 400);
        calendar.setGridLinesVisible(true);

        // Create rows and columns with anchor panes for the calendar
        for( int i = 0; i < 5; i++ )
        {
            for( int j = 0; j < 7; j++ )
            {
                AnchorPaneNode ap = new AnchorPaneNode();
                ap.setPrefSize(200, 200);
                calendar.add(ap, j, i);
                allCalendarDays.add(ap);

                ap.setOnMouseClicked( e ->
                {
                    DayView dayView = new DayView( ap.date, worker, ap );
                    dayView.draw();
                });
            }
        }

        // Days of the week labels
        Text[] dayNames = new Text[]{ new Text("Sunday"), new Text("Monday"), new Text("Tuesday"),
                new Text("Wednesday"), new Text("Thursday"), new Text("Friday"),
                new Text("Saturday") };
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for( Text txt : dayNames )
        {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 10);
            AnchorPane.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }

        // Create calendarTitle and buttons to change current month
        calendarTitle = new Text();
        Button previousMonth = new Button("<<");
        previousMonth.setOnAction(e -> previousMonth());
        Button nextMonth = new Button(">>");
        nextMonth.setOnAction(e -> nextMonth());
        Button previousYear = new Button("<<");
        previousYear.setOnAction(e -> previousYear());
        Button nextYear = new Button(">>");
        nextYear.setOnAction(e -> nextYear());
        Button back = new Button("Back");
        back.setOnAction(e -> StageProperty.loadView("workers", this.getView(), this.getClass()));
        HBox titleBar = new HBox(previousYear, previousMonth, calendarTitle, nextMonth, nextYear, back);
        titleBar.setSpacing(10);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        // Populate calendar with the appropriate day numbers
        populateCalendar(yearMonth);

        // Create the calendar view
        view = new VBox(titleBar, dayLabels, calendar);
    }

    /**
     * Set the days of the calendar to correspond to the appropriate date
     *
     * @param yearMonth year and month of month to render
     */
    public void populateCalendar( YearMonth yearMonth )
    {
        // Get the date we want to start with on the calendar
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
        while( !calendarDate.getDayOfWeek().toString().equals("SUNDAY") )
        {
            calendarDate = calendarDate.minusDays(1);
        }

        Connection connection = new ConnectionClass().getConnection();
        ResultSet resultSet = null;
        try
        {
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM dailyEvents WHERE worker='"+ worker +"';";
            resultSet = statement.executeQuery(sql);
        } catch( SQLException e )
        {
            e.printStackTrace();
        }

        // Populate the calendar with day numbers
        for( AnchorPaneNode ap : allCalendarDays )
        {
            if( ap.getChildren().size() != 0 )
            {
                ap.getChildren().remove(0);
            }
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);

            try
            {
                if( resultSet != null )
                {
                    while( resultSet.next() )
                    {
                        if( resultSet.getString(2).equals(ap.date.toString()) )
                        {
                            ap.setStyle("-fx-background-color: #ed3838");
                            break;
                        }
                    }
                    resultSet.first();
                }
            } catch( SQLException e )
            {
                e.printStackTrace();
            }

            AnchorPane.setTopAnchor(txt, 5.0);
            AnchorPane.setLeftAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            calendarDate = calendarDate.plusDays(1);
        }

        try
        {
            connection.close();
        } catch( SQLException e )
        {
            e.printStackTrace();
        }

        // Change the title of the calendar
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
    }

    /**
     * Move the month back by one. Repopulate the calendar with the correct dates.
     */
    private void previousMonth()
    {
        currentYearMonth = currentYearMonth.minusMonths(1);
        for( AnchorPaneNode ap : allCalendarDays )
        {
            ap.setStyle(null);
        }
        populateCalendar(currentYearMonth);
    }

    /**
     * Move the month forward by one. Repopulate the calendar with the correct dates.
     */
    private void nextMonth()
    {
        currentYearMonth = currentYearMonth.plusMonths(1);
        for( AnchorPaneNode ap : allCalendarDays )
        {
            ap.setStyle(null);
        }
        populateCalendar(currentYearMonth);
    }

    /**
     * Move the year back by one. Repopulate the calendar with the correct dates.
     */
    private void previousYear()
    {
        currentYearMonth = currentYearMonth.minusMonths(12);
        for( AnchorPaneNode ap : allCalendarDays )
        {
            ap.setStyle(null);
        }
        populateCalendar(currentYearMonth);
    }

    /**
     * Move the year forward by one. Repopulate the calendar with the correct dates.
     */
    private void nextYear()
    {
        currentYearMonth = currentYearMonth.plusMonths(12);
        for( AnchorPaneNode ap : allCalendarDays )
        {
            ap.setStyle(null);
        }
        populateCalendar(currentYearMonth);
    }

    public VBox getView()
    {
        return view;
    }

    public ArrayList<AnchorPaneNode> getAllCalendarDays()
    {
        return allCalendarDays;
    }

    public void setAllCalendarDays( ArrayList<AnchorPaneNode> allCalendarDays )
    {
        this.allCalendarDays = allCalendarDays;
    }
}
