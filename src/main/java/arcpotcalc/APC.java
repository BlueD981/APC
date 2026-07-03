package arcpotcalc;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class APC extends Application {

    public double bpStr;

    private Button BackButton;

    private double BBActiveOpacity = 1;
    private double BBInactiveOpacity = 0.5;

    private javafx.collections.ObservableList<javafx.scene.Node> CA;
    private javafx.collections.ObservableList<javafx.scene.Node> RL;
    
    private Label scoreError = new Label("");
    private Label resultLabel = new Label("--");

    private ChangeListener<String> bpListener = (obs, old, val) -> numInput();
    private ChangeListener<String> noteCountListener = (obs, old, val) -> numInput();
    private ChangeListener<String> searchListener = (obs, old, val) -> numInput();
    
    private StackPane rootLayout;
    private StackPane contentArea;

    private String info = "Arcaea Potential Calculator (ver. 3.0)";
    
    private TextField scoreField = new TextField();

    @Override
    public void start(Stage APCMain) {
        APCMain.setTitle(info);

        rootLayout = new StackPane();
        contentArea = new StackPane();

        RL = rootLayout.getChildren();
        CA = contentArea.getChildren();

        BackButton = new Button("◀");
        BackButton.setStyle("-fx-font-size: 11px;");
        BackButton.setPrefSize(25, 25);
        BackButton.setFocusTraversable(false);

        StackPane.setAlignment(BackButton, Pos.TOP_LEFT);
        StackPane.setMargin(BackButton, new Insets(15, 0, 0, 15));

        BackButton.setOnAction(back -> {
            go_back();
        });

        CA.add(mainMenu());
        RL.addAll(contentArea, BackButton);

        APCMain.setScene(new Scene(rootLayout, 640, 360));
        APCMain.setResizable(false);
        APCMain.show();
    }

    public void go_back() {
        bpField.textProperty().removeListener(bpListener);
        scoreField.textProperty().removeListener(ManualScoreListener);
        scoreField.textProperty().removeListener(AutoScoreListener);
        noteCountField.textProperty().removeListener(noteCountListener);
        searchField.textProperty().removeListener(searchListener);
        searchResultTable.setItems(null);
        CA.clear();
        CA.add(mainMenu());
    }

    public GridPane mainMenu() {
        BackButton.setOpacity(BBInactiveOpacity);
        BackButton.setDisable(true);

        GridPane menu = new GridPane();
        menu.setHgap(10);
        menu.setVgap(10);

        Label versionLabel = new Label(info);
        versionLabel.setFocusTraversable((true));
        versionLabel.setAlignment(Pos.CENTER_RIGHT);

        GridPane.setHalignment(versionLabel, HPos.RIGHT);
        GridPane.setValignment(versionLabel, VPos.BOTTOM);

        menu.add(versionLabel, 27, 27);

        Button ManualBtn = new Button("직접 입력");
        Button AutoBtn = new Button("곡 검색 및 자동 입력");

        ManualBtn.setPrefWidth(160);
        AutoBtn.setPrefWidth(160);

        menu.add(new Label("APC"), 5, 5);
        menu.add(ManualBtn, 5, 7);
        menu.add(AutoBtn, 5, 8);

        ManualBtn.setOnAction(Mnclick -> {
            CA.clear();
            CA.add(ManualInput());
        });
        AutoBtn.setOnAction(Atclick -> {
            CA.clear();
            CA.add(AutoInput());
        });

        return menu;
    }

    private Label bpError = new Label("");
    private Label noteCountError = new Label("");

    private ChangeListener<String> ManualScoreListener = (obs, old, val) -> numInput();

    private TextField bpField = new TextField();
    private TextField noteCountField = new TextField();

    public GridPane ManualInput() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane mnip = new GridPane();
        mnip.setHgap(10);
        mnip.setVgap(10);

        int FieldSize = 250;

        mnip.add(new Label("보면상수 :"), 5, 5);
        bpField.setPromptText("여기에 보면상수 입력");
        bpField.setMinWidth(FieldSize);
        bpField.setMaxWidth(FieldSize);
        mnip.add(bpField, 5, 6);
        bpField.textProperty().addListener(bpListener);

        mnip.add(new Label("점수 :"), 5, 8);
        scoreField.setPromptText("여기에 점수 입력");
        scoreField.setMinWidth(FieldSize);
        scoreField.setMaxWidth(FieldSize);
        mnip.add(scoreField, 5, 9);
        scoreField.textProperty().addListener(ManualScoreListener);

        mnip.add(new Label("노트 수 :"), 5, 11);
        noteCountField.setPromptText("여기에 노트 수 입력");
        noteCountField.setMinWidth(FieldSize);
        noteCountField.setMaxWidth(FieldSize);
        mnip.add(noteCountField, 5, 12);
        noteCountField.textProperty().addListener(noteCountListener);

        mnip.add(bpError, 5, 7);
        mnip.add(scoreError, 5, 10);
        mnip.add(noteCountError, 5, 13);
        mnip.add(resultLabel, 8, 9);

        return mnip;
    }

    private ChangeListener<String> AutoScoreListener = (obs, oldSel, newSel) -> autoNumInput();

    private ChangeListener<SongInformation> resultListener = (obs, oldSel, newSel) -> {
        if (newSel != null) {
            if (newSel.getDifficulty().equals("PST")) {
                song = newSel.getSongInfo();
                diffInt = 0;
            }
            if (newSel.getDifficulty().equals("PRS")) {
                song = newSel.getSongInfo();
                diffInt = 1;
            }
            if (newSel.getDifficulty().equals("FTR")) {
                song = newSel.getSongInfo();
                diffInt = 2;
            }
            if (newSel.getDifficulty().equals("ETR")) {
                song = newSel.getSongInfo();
                diffInt = 3;
            }
            if (newSel.getDifficulty().equals("BYD")) {
                song = newSel.getSongInfo();
                diffInt = 4;
            }
            autoNumInput();
        }
    };

    private MenuButton DiffSelect = new MenuButton("전체");
    
    private Songs song;

    private Integer diffInt;

    private TableColumn<SongInformation, String> titleCol = new TableColumn<>("제목");
    private TableColumn<SongInformation, String> diffCol = new TableColumn<>("난이도");
    private TableColumn<SongInformation, String> levelCol = new TableColumn<>("레벨");

    private TableView<SongInformation> searchResultTable = new TableView<>();

    private TextField searchField = new TextField();

    public GridPane AutoInput() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane atip = new GridPane();
        atip.setHgap(10);
        atip.setVgap(10);

        atip.add(new Label("곡 선택 :"), 5, 5);

        CheckMenuItem SelAll = new CheckMenuItem("전체");
        CheckMenuItem SelPst = new CheckMenuItem("PST");
        CheckMenuItem SelPrs = new CheckMenuItem("PRS");
        CheckMenuItem SelFtr = new CheckMenuItem("FTR");
        CheckMenuItem SelEtr = new CheckMenuItem("ETR");
        CheckMenuItem SelByd = new CheckMenuItem("BYD");

        List<String> DiffList = new ArrayList<>();

        CheckMenuItem[] DiffSelectList = {SelPst, SelPrs, SelFtr, SelEtr, SelByd};

        SelAll.setSelected(true);

        for (int i = 0; i <= 4; i++) {
            DiffList.add(String.valueOf(i));
        }
        DiffSelect.setText("전체");
        /*String DiffListTest = "";
        for (int i = 0; i <= DiffList.size()-1; i++) {
            System.out.println(DiffListTest + String.valueOf(DiffList.get(i)));
        }
        System.out.println("");*/

        for (int i = 0; i <= 4; i++) {
            DiffList.add(String.valueOf(i));
        }

        for (CheckMenuItem Sel : DiffSelectList) {
            Sel.setOnAction(a -> {
                if (Sel.isSelected()) {
                    SelAll.setSelected(false);
                }

                boolean allSelected = true;
                for (CheckMenuItem b : DiffSelectList) {
                    if (!b.isSelected()) {
                        allSelected = false;
                        break;
                    }
                }

                if (allSelected) {
                    for (CheckMenuItem c : DiffSelectList) {
                        c.setSelected(false);
                    }
                    SelAll.setSelected(true);
                    DiffSelect.setText("전체");
                }
                else {
                    StringBuilder sb = new StringBuilder("");
                    int count = 0;
                    for (CheckMenuItem d : DiffSelectList) {
                        if (d.isSelected()) {
                            sb.append(String.format("%s, ", d.getText()));
                            count++;
                        }
                    }
                    if (count > 0) {
                        sb.setLength(sb.length() - 2);
                        DiffSelect.setText(sb.toString());
                    }
                    else {
                        SelAll.setSelected(true);
                        DiffSelect.setText("전체");
                    }
                }

                DiffList.clear();
                if (SelPst.isSelected()) {
                    DiffList.add("0");
                }
                if (SelPrs.isSelected()) {
                    DiffList.add("1");
                }
                if (SelFtr.isSelected()) {
                    DiffList.add("2");
                }
                if (SelEtr.isSelected()) {
                    DiffList.add("3");
                }
                if (SelByd.isSelected()) {
                    DiffList.add("4");
                }
                if (SelAll.isSelected()) {
                    for (int i = 0; i <= 4; i++) {
                        DiffList.add(String.valueOf(i));
                    }
                }

                /*
                String DiffListTest2 = "";
                for (int i = 0; i <= DiffList.size()-1; i++) {
                    System.out.println(DiffListTest2 + String.valueOf(DiffList.get(i)));
                }
                */
                searchResultTable.setItems(FilteredSongs(DiffList));
                //System.out.println("");
            });
        }

        SelAll.setOnAction(e -> {
            if (SelAll.isSelected()) {
                DiffList.clear();
                for (CheckMenuItem f : DiffSelectList) {
                    f.setSelected(false);
                }
                for (int i = 0; i <= 4; i++) {
                    DiffList.add(String.valueOf(i));
                }
                DiffSelect.setText("전체");
                /*String DiffListTest1 = "";
                for (int i = 0; i <= DiffList.size()-1; i++) {
                    System.out.println(DiffListTest1 + String.valueOf(DiffList.get(i)));
                }*/
                searchResultTable.setItems(FilteredSongs(DiffList));
                //System.out.println("");
            }
            else {
                SelAll.setSelected(true);
            }
        });

        searchField.setPromptText("여기에 검색어 입력");
        atip.add(searchField, 5, 6);

        DiffSelect.getItems().clear();
        DiffSelect.getItems().addAll(SelAll, SelPst, SelPrs, SelFtr, SelEtr, SelByd);
        DiffSelect.setMaxWidth(300);
        atip.add(DiffSelect, 5, 7);

        searchResultTable.setPrefHeight(500);
        searchResultTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        diffCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("lvl"));
        titleCol.setMinWidth(146);
        titleCol.setMaxWidth(146);
        diffCol.setMinWidth(51);
        diffCol.setMaxWidth(51);
        levelCol.setMinWidth(35);
        levelCol.setMaxWidth(35);
        
        searchResultTable.getColumns().clear();
        searchResultTable.getColumns().addAll(titleCol, diffCol, levelCol);
        searchResultTable.setItems(FilteredSongs(DiffList));
        searchResultTable.setMinHeight(50);
        searchResultTable.setMinHeight(50);
        searchResultTable.setMaxWidth(250);
        searchResultTable.setMaxWidth(250);
        atip.add(searchResultTable, 5, 8);
        atip.add(new Label(""), 5, 9);

        searchResultTable.getSelectionModel().selectedItemProperty().addListener(resultListener);

        searchField.setPromptText("여기에 검색어 입력");
        searchField.textProperty().addListener((obs, old, val) -> {
            searchResultTable.setItems(FilteredSongs(DiffList));
        });

        scoreField.setPromptText("여기에 점수 입력");
        scoreField.textProperty().addListener(AutoScoreListener);
        atip.add(scoreError, 7, 7);

        scoreField.setMinWidth(250);
        scoreField.setMaxWidth(250);
        atip.add(scoreField, 7, 6);

        atip.add(resultLabel, 7, 8);

        return atip;
    }

    private void numInput() {
        Boolean bpValid = false;
        Boolean scoreValid = false;

        double bp = 0;
        double score = 0;
        double noteCount = 0;

        bpError.setText("");
        resultLabel.setText("--");
        scoreError.setText("");

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

    public void autoNumInput() {
        if (diffInt == null || song == null) {
            return;
        }
        double score = 0;
        boolean scoreValid = true;
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
                    resultLabel.setText("--");
                }
                else if (score > song.getNotes(diffInt) + 10000000) {
                    scoreValid = true;
                    scoreError.setText(String.format("유효하지 않은 점수입니다. 최대 점수: %d", song.getNotes(diffInt) + 10000000));
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
        if (scoreValid) resultLabel.setText(String.valueOf(APCLogic.APCLogicMain(song.getConst(diffInt), score, (double) song.getNotes(diffInt))) + String.format("\n보면상수: %.1f\n총 노트 수: %d", song.getConst(diffInt), song.getNotes(diffInt))); //System.out.println("0 out");
    }

    public class SongInformation {
        public Songs songInfo;
        public String title;
        public String difficulty;
        public String lvl;

        public SongInformation(Songs songInfo, String title, String difficulty, String lvl) {
            this.songInfo = songInfo;
            this.title = title;
            this.difficulty = difficulty;
            this.lvl = lvl;
        }

        public Songs getSongInfo() {
            return songInfo;
        }
        public String getTitle() {
            return title;
        }
        public String getDifficulty() {
            return difficulty;
        }
        public String getLvl() {
            return lvl;
        }
    }

    public ObservableList<SongInformation> searchedSongs(List<String> DiffList) {
        ObservableList<SongInformation> ss = FXCollections.observableArrayList();
        ss.clear();
        String search = searchField.getText().toLowerCase().replaceAll("\\s+", "");

        for (Songs song : Songs.values()) {
            if (!song.getTitle().toLowerCase().replaceAll("\\s+", "").contains(search) && !song.getBYDTitle().toLowerCase().replaceAll("\\s+", "").contains(search)) {
                continue;
            }

            for (int i = 0; i <= 4; i++) {
                if (DiffList.contains(String.valueOf(i)) && song.getConst(i) > 0) {
                    if (i < 4) {
                        ss.add(new SongInformation(song, song.getTitle(), song.getDiff(i), song.getLevel(i)));
                    }
                    else if (i == 4) {
                        ss.add(new SongInformation(song, song.getBYDTitle(), song.getDiff(i), song.getLevel(i)));
                    }
                }
            }
        }
        return ss;
    }

    public ObservableList<SongInformation> FilteredSongs(List<String> DiffList) {
        ObservableList<SongInformation> fs = FXCollections.observableArrayList();
        fs.clear();

        for (SongInformation song : searchedSongs(DiffList)) {
            if (DiffList.contains(String.valueOf("0")) && song.getDifficulty().equals("PST")) {
                fs.add(song);
            }
            if (DiffList.contains(String.valueOf("1")) && song.getDifficulty().equals("PRS")) {
                fs.add(song);
            }
            if (DiffList.contains(String.valueOf("2")) && song.getDifficulty().equals("FTR")) {
                fs.add(song);
            }
            if (DiffList.contains(String.valueOf("3")) && song.getDifficulty().equals("ETR")) {
                fs.add(song);
            }
            if (DiffList.contains(String.valueOf("4")) && song.getDifficulty().equals("BYD")) {
                fs.add(song);
            }
        }
        return fs;
    }

    public static void main(String[] args) {
        launch(args);
    }
}