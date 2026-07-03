package arcpotcalc;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class APC extends Application {

    public double bpStr;
    private TextField bpField = new TextField();
    private TextField scoreField = new TextField();
    private TextField noteCountField = new TextField();
    private Label bpError = new Label("");
    private Label scoreError = new Label("");
    private Label noteCountError = new Label("");
    private Label resultLabel = new Label("--");

    private Label testLabel = new Label("");

    @Override
    public void start(Stage APCMain) {
        APCMain.setTitle("Arcaea Potential Calculator (ver. 2.0)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("보면상수 :"), 5, 5);
        grid.add(bpField, 5, 6);
        bpField.textProperty().addListener((obs, old, val) -> numInput());

        grid.add(new Label("점수 :"), 5, 8);
        grid.add(scoreField, 5, 9);
        scoreField.textProperty().addListener((obs, old, val) -> numInput());

        grid.add(new Label("노트 수 :"), 5, 11);
        grid.add(noteCountField, 5, 12);
        noteCountField.textProperty().addListener((obs, old, val) -> numInput());

        grid.add(bpError, 5, 7);
        grid.add(scoreError, 5, 10);
        grid.add(noteCountError, 5, 13);
        grid.add(resultLabel, 15, 9);
        grid.add(testLabel, 11, 11);

        APCMain.setScene(new Scene(grid, 640, 360));
        APCMain.setResizable(false);
        APCMain.show();
    }

    private void numInput() {
        double bp = 0;
        double score = 0;
        double noteCount = 0;
        Boolean bpValid = false;
        Boolean scoreValid = false;
        bpError.setText("");
        scoreError.setText("");
        resultLabel.setText("--");

        if (bpField.getText().isEmpty()) {
            bpValid = false;
            bpError.setText("");
        }
        else {
            try {
                bpValid = false;
                bp = Double.parseDouble(bpField.getText());
                if (bp < 1.0 || bp > 12.0) {
                    bpValid = false;
                    bpError.setText("유효하지 않은 보면상수입니다.");
                }
                else {
                    bpValid = true;
                    bpError.setText("");
                }
            }
            catch (NumberFormatException e) {
                bpValid = false;
                bpError.setText("유효하지 않은 보면상수입니다.");
            }
        }

        if (scoreField.getText().isEmpty()) {
            scoreValid = false;
            scoreError.setText("");
        }
        else {
            try {
                scoreValid = false;
                score = Double.parseDouble(scoreField.getText());
                if (score < 0 || score > 10002236) {
                    scoreValid = false;
                    scoreError.setText("유효하지 않은 점수입니다.");
                }
                else {
                    scoreValid = true;
                    scoreError.setText("");
                }
            }
            catch (NumberFormatException e) {
                scoreValid = false;
                scoreError.setText("유효하지 않은 점수입니다.");
            }
        }

        if (noteCountField.getText().isEmpty()) {
            noteCountError.setText("");
        }
        else {
            try {
                noteCount = Double.parseDouble(noteCountField.getText());
                if (noteCount < 0 || noteCount > 2236) {
                    noteCountError.setText("유효하지 않은 노트 개수입니다.");
                }
                else {
                    noteCountError.setText("");
                }
            }
            catch (NumberFormatException e) {
                noteCountError.setText("유효하지 않은 노트 개수입니다.");
            }
        }

        if (bpValid && scoreValid) {
            resultLabel.setText(APCLogic.APCLogicMain(bp, score, noteCount));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}