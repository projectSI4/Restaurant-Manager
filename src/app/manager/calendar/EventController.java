package app.manager.calendar;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class EventController
{
    public AnchorPane anchorPane;
    public Button apply;
    public TextArea textArea;
    public Label desc;
    public Label start;
    public Label end;
    public ChoiceBox<String> choiceBoxS;
    public ChoiceBox<String> choiceBoxE;
    public static AnchorPane parentPane;
    public TextField textField;

    public void initialize()
    {
        ArrayList<String> hours = new ArrayList<>(FXCollections.observableArrayList("0.00", "0.30", "1.00", "1.30", "2.00", "2.30", "3.00",
                "3.30", "4.00", "4.30", "5.00", "5.30", "6.00", "6.30", "7.00",
                "7.30", "8.00", "8.30", "9.00", "9.30", "10.00", "10.30", "11.00",
                "11.30", "12.00", "12.30", "13.00", "13.30", "14.00", "14.30", "15.00",
                "15.30", "16.00", "16.30", "17.00", "17.30", "18.00", "18.30", "19.00",
                "19.30", "20.00", "20.30", "21.00", "21.30", "22.00", "22.30", "23.00", "23.30"));
        choiceBoxS.getItems().addAll( hours );
        choiceBoxE.getItems().addAll( hours );
    }

    public void applyAction()
    {
        Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
        addNewEvent();
        primaryStage.close();
    }

    private void addNewEvent()
    {
        if( desc.getText().equals("") || choiceBoxE.getValue().equals("") || choiceBoxS.getValue().equals("") )
            return;
        double startHour = Double.parseDouble(choiceBoxS.getValue());
        double endHour = Double.parseDouble(choiceBoxE.getValue());
        if( endHour <= startHour )
            return;

        Rectangle rect = new Rectangle(20,startHour*25 + 6, 80, (endHour-startHour)*25);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.BLUE);

        Text text = new Text(textField.getText());
        text.setFill(Color.BLACK);
        text.setStroke(Color.BLACK);
        StackPane pane = new StackPane();
        pane.getChildren().addAll(rect, text);
        pane.setLayoutX(20);
        pane.setLayoutY(startHour*25 + 6);

        Tooltip tooltip = new Tooltip(desc.getText());
        tooltip.setShowDelay(Duration.seconds(0));
        Tooltip.install( pane, tooltip );

        parentPane.getChildren().add(pane);
    }
}