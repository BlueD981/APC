package arcpotcalc;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;

import javafx.beans.value.ChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import javafx.scene.image.Image;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.scene.text.TextFlow;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class APC extends Application {
    Font NanumGothicExtraBold = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicExtraBold.ttf"), 15);
    Font NanumGothicBold = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicBold.ttf"), 15);
    Font NanumGothic = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothic.ttf"), 15);
    Font NanumGothicLight = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicLight.ttf"), 15);

    public double bpStr;

    private Button BackButton;

    private double BBActiveOpacity = 1;
    private double BBInactiveOpacity = 0.5;

    private javafx.collections.ObservableList<javafx.scene.Node> CA;
    private javafx.collections.ObservableList<javafx.scene.Node> RL;
    
    private Label scoreError = new Label("");
    private Label resultLabel = new Label(String.format("결과 포텐셜 :%n--"));

    private ChangeListener<String> bpListener = (obs, old, val) -> numInput();
    private ChangeListener<String> noteCountListener = (obs, old, val) -> numInput();
    private ChangeListener<String> searchListener = (obs, old, val) -> numInput();
    
    private StackPane rootLayout;
    private StackPane contentArea;

    private String info = "Arcaea Potential Calculator (ver. 4.1)";
    
    private TextField scoreField = new TextField();

    private Label WLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    @Override
    public void start(Stage APCMain) {
        settings.loadSettings();
        /*System.out.println(settings.get("Theme", "ThemeDefault"));*/

        try {
            Image icon = new Image(getClass().getResourceAsStream("/APC_black.jpg"));
            APCMain.getIcons().add(icon);
        }
        catch (Exception e) {
            APCMain.getIcons().add(new Image("src/main/resources/APC_black.jpg"));
        }

        bpError.getStyleClass().add("ErrorText");
        scoreError.getStyleClass().add("ErrorText");
        noteCountError.getStyleClass().add("ErrorText");

        APCMain.setTitle(info);

        rootLayout = new StackPane();
        rootLayout.getStyleClass().add("rootLayout");
        contentArea = new StackPane();

        rootLayout.getStyleClass().add("rootlayout");
        contentArea.getStyleClass().add("contentarea");

        RL = rootLayout.getChildren();
        CA = contentArea.getChildren();

        BackButton = new Button("◀");
        BackButton.setStyle("-fx-font-size: 10px;");
        BackButton.setPrefSize(25, 25);
        BackButton.setFocusTraversable(false);

        BackButton.getStyleClass().add("BackButton");

        StackPane.setAlignment(BackButton, Pos.TOP_LEFT);
        StackPane.setMargin(BackButton, new Insets(15, 0, 0, 15));

        BackButton.setOnAction(back -> {
            go_back();
        });

        CA.add(mainMenu());
        RL.addAll(contentArea, BackButton);

        Scene primaryScene = new Scene(rootLayout, 640, 360);
        try {
            if (settings.get("Theme", "Light").equals("Light")) {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                primaryScene.getStylesheets().add(cssPath);
                /*System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Dark")) {
                String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                primaryScene.getStylesheets().add(cssPath);
                /*System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Custom")) {
                String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                primaryScene.getStylesheets().add(cssPath);
                /*System.out.println("성공");*/
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (settings.get("Theme", "Light").equals("Light")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Dark")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                    /*System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Custom")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    /*System.out.println("성공");*/
                }
            }
            catch (Exception f) {
                f.printStackTrace();
                /*System.out.println("실패");*/
            }
        }
        try {
            if (settings.get("clearCheckBoxDefault", "Checked").equals("Follow Latest Decision")) {
                if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                    isCleared = true;
                    clearCheck.setSelected(true);
                    /*System.out.println("TC성공");*/
                }
                if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                    isCleared = false;
                    clearCheck.setSelected(false);
                    /*System.out.println("TL성공");*/
                }
            }
            else if (settings.get("clearCheckBoxDefault", "Checked").equals("Checked")) {
                isCleared = true;
                clearCheck.setSelected(true);
                /*System.out.println("C성공");*/
            }
            else if (settings.get("clearCheckBoxDefault", "Checked").equals("Not Checked")) {
                isCleared = false;
                clearCheck.setSelected(false);
                /*System.out.println("NC성공");*/
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            /*System.out.println("실패");*/
        }

        APCMain.setScene(primaryScene);
        APCMain.setResizable(false);
        APCMain.show();
    }

    public void go_back() {
        bpField.textProperty().removeListener(bpListener);
        scoreField.textProperty().removeListener(ManualScoreListener);
        scoreField.textProperty().removeListener(AutoScoreListener);
        noteCountField.textProperty().removeListener(noteCountListener);
        searchField.textProperty().removeListener(searchListener);
        searchResultTable.getSelectionModel().selectedItemProperty().removeListener(resultListener);
        searchResultTable.setItems(null);
        clearCheck.selectedProperty().removeListener(clearCheckListener);
        CA.clear();
        CA.add(mainMenu());
    }

    public GridPane mainMenu() {
        BackButton.setOpacity(BBInactiveOpacity);
        BackButton.setDisable(true);

        GridPane menu = new GridPane();
        menu.getStyleClass().add("Panes");

        GridPane menu1 = new GridPane();
        menu1.getStyleClass().add("Panes");
        menu1.setHgap(10);
        menu1.setVgap(10);

        Label versionLabel = new Label(info);
        versionLabel.setFocusTraversable(true);
        /*versionLabel.setAlignment(Pos.CENTER_RIGHT);*/

        Button ManualBtn = new Button("직접 입력");
        Button AutoBtn = new Button("곡 검색 및 자동 입력");
        Button SettingsBtn = new Button("설정");
        Button InfoBtn = new Button("앱 정보");
        ManualBtn.getStyleClass().add("Buttons");
        AutoBtn.getStyleClass().add("Buttons");
        SettingsBtn.getStyleClass().add("Buttons");
        InfoBtn.getStyleClass().add("Buttons");

        ManualBtn.setPrefWidth(160);
        AutoBtn.setPrefWidth(160);
        SettingsBtn.setPrefWidth(75);
        InfoBtn.setPrefWidth(75);

        Text APC_A = new Text("A");
        Text APC_rcaea = new Text(String.format("rcaea"));
        Text APC_P = new Text("P");
        Text APC_otential = new Text(String.format("otential"));
        Text APC_C = new Text("C");
        Text APC_alculator = new Text("alculator");
        Text ENTER1 = new Text(String.format("%n"));
        Text ENTER2 = new Text(String.format("%n"));
        APC_A.getStyleClass().add("APCTitle1");
        APC_P.getStyleClass().add("APCTitle1");
        APC_C.getStyleClass().add("APCTitle1");
        APC_rcaea.getStyleClass().add("APCTitle2");
        APC_otential.getStyleClass().add("APCTitle2");
        APC_alculator.getStyleClass().add("APCTitle2");
        TextFlow TitleLabel = new TextFlow(APC_A, APC_rcaea, ENTER1, APC_P, APC_otential, ENTER2, APC_C, APC_alculator);
        menu1.add(TitleLabel, 5, 5);
        menu1.add(ManualBtn, 5, 7);
        menu1.add(AutoBtn, 5, 9);
        GridPane twoButtons = new GridPane();
        twoButtons.getStyleClass().add("Panes");
        twoButtons.setHgap(10);
        twoButtons.add(SettingsBtn, 0, 0);
        twoButtons.add(InfoBtn, 1, 0);
        menu1.add(twoButtons, 5, 11);

        ManualBtn.setOnAction(Mnclick -> {
            CA.clear();
            CA.add(ManualInput());
        });
        AutoBtn.setOnAction(Atclick -> {
            CA.clear();
            CA.add(AutoInput());
        });
        SettingsBtn.setOnAction(Stclick -> {
            CA.clear();
            CA.add(Settings());
        });
        InfoBtn.setOnAction(Crclick -> {
            CA.clear();
            CA.add(AppInfo());
        });

        menu1.setFocusTraversable(false);

        int MIHgap = 400;

        GridPane menu_info = new GridPane();
        menu_info.getStyleClass().add("Panes");
        Label blankLabel = new Label("");
        menu_info.setHgap(MIHgap);
        menu_info.setVgap(37);
        menu_info.add(blankLabel, 0, 0);
        menu_info.add(versionLabel, 1, 1);

        menu.add(menu1, 0, 0);
        menu.add(menu_info, 0, 1);

        return menu;
    }

    private Label bpError = new Label("");
    private Label noteCountError = new Label("");

    private ChangeListener<Boolean> clearCheckListener = (obs, old, val) -> {
            if (val) {
                isCleared = true;
                settings.set("clearLatest", "Track Complete");
                /*System.out.println(settings.get("clearLatest", "Track Complete"));*/
            }
            else {
                isCleared = false;
                settings.set("clearLatest", "Track Lost");
                /*System.out.println(settings.get("clearLatest", "Track Lost"));*/
            }
            autoNumInput();
        };

    private ChangeListener<String> ManualScoreListener = (obs, old, val) -> numInput();

    private CheckBox clearCheck = new CheckBox("클리어");

    public boolean isCleared = false;

    private TextField bpField = new TextField();
    private TextField noteCountField = new TextField();

    public GridPane ManualInput() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        if (settings.get("clearCheckBoxDefault", "Checked").equals("Checked")) {
            settings.set("clearCheckBoxDefault", "Checked");
            isCleared = true;
            clearCheck.setSelected(true);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Not Checked")) {
            settings.set("clearCheckBoxDefault", "Not Checked");
            isCleared = false;
            clearCheck.setSelected(false);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Follow Latest Decision")) {
            settings.set("clearCheckBoxDefault", "Follow Latest Decision");
            if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                isCleared = true;
                clearCheck.setSelected(true);
            }
            else if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                isCleared = false;
                clearCheck.setSelected(false);
            }
        }

        numInput();

        GridPane mnip = new GridPane();
        mnip.getStyleClass().add("Panes");
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

        mnip.add(clearCheck, 8, 6);
        clearCheck.selectedProperty().addListener(clearCheckListener);

        mnip.setFocusTraversable(false);

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
            if (newSel.getDifficulty().equals("BYD") || newSel.getDifficulty().equals("INS")) {
                song = newSel.getSongInfo();
                diffInt = 4;
            }
            autoNumInput();
        }
    };

    private MenuButton DiffSelect = new MenuButton("전체");
    
    private Songs song;

    private Integer diffInt;

    private Label autoResultLabel = new Label(String.format("결과 포텐셜 :%n--%n%n보면상수 :%n--%n%n총 노트 수 :%n--"));

    private TableColumn<SongInformation, String> titleCol = new TableColumn<>("제목");
    private TableColumn<SongInformation, String> diffCol = new TableColumn<>("난이도");
    private TableColumn<SongInformation, String> levelCol = new TableColumn<>("레벨");

    private TableView<SongInformation> searchResultTable = new TableView<>();

    private TextField searchField = new TextField();

    public GridPane AutoInput() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        if (settings.get("clearCheckBoxDefault", "Checked").equals("Checked")) {
            settings.set("clearCheckBoxDefault", "Checked");
            isCleared = true;
            clearCheck.setSelected(true);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Not Checked")) {
            settings.set("clearCheckBoxDefault", "Not Checked");
            isCleared = false;
            clearCheck.setSelected(false);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Follow Latest Decision")) {
            settings.set("clearCheckBoxDefault", "Follow Latest Decision");
            if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                isCleared = true;
                clearCheck.setSelected(true);
            }
            else if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                isCleared = false;
                clearCheck.setSelected(false);
            }
        }

        autoNumInput();

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(searchResultTable.widthProperty());
        clip.heightProperty().bind(searchResultTable.heightProperty());
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        searchResultTable.setClip(clip);

        GridPane atip = new GridPane();
        atip.getStyleClass().add("Panes");
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

                searchResultTable.setItems(FilteredSongs(DiffList));
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

                searchResultTable.setItems(FilteredSongs(DiffList));
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
        titleCol.setMinWidth(149);
        titleCol.setMaxWidth(149);
        diffCol.setMinWidth(50);
        diffCol.setMaxWidth(50);
        levelCol.setMinWidth(30);
        levelCol.setMaxWidth(30);
        
        searchResultTable.getColumns().clear();
        searchResultTable.getColumns().add(titleCol);
        searchResultTable.getColumns().add(diffCol);
        searchResultTable.getColumns().add(levelCol);
        searchResultTable.setItems(FilteredSongs(DiffList));
        searchResultTable.setMinHeight(50);
        searchResultTable.setMinHeight(50);
        searchResultTable.setMaxWidth(250);
        searchResultTable.setMaxWidth(250);
        /*GridPane TablePane = new GridPane();
        TablePane.add(searchResultTable, 0, 0);
        TablePane.getStyleClass().add("TablePane");
        atip.add(TablePane, 5, 8);*/
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

        scoreField.setMinWidth(200);
        scoreField.setMaxWidth(200);
        atip.add(scoreField, 7, 6);

        atip.add(autoResultLabel, 7, 8);

        atip.add(clearCheck, 8, 6);
        clearCheck.selectedProperty().addListener(clearCheckListener);

        atip.setFocusTraversable(false);

        return atip;
    }

    public ScrollPane AppInfo() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane AppInfoGrid = new GridPane();
        AppInfoGrid.getStyleClass().add("Panes");
        AppInfoGrid.setFocusTraversable(false);

        AppInfoGrid.setHgap(10);
        AppInfoGrid.setVgap(10);

        Text INFO_APC_A = new Text("A");
        Text INFO_APC_rcaea = new Text(String.format("rcaea"));
        Text INFO_APC_P = new Text(" P");
        Text INFO_APC_otential = new Text(String.format("otential"));
        Text INFO_APC_C = new Text(" C");
        Text INFO_APC_alculator = new Text("alculator");
        Text INFO_VersionInfo = new Text(String.format("%n       version 4.1"));
        INFO_APC_A.getStyleClass().add("INFO_APCTitle1");
        INFO_APC_P.getStyleClass().add("INFO_APCTitle1");
        INFO_APC_C.getStyleClass().add("INFO_APCTitle1");
        INFO_APC_rcaea.getStyleClass().add("INFO_APCTitle2");
        INFO_APC_otential.getStyleClass().add("INFO_APCTitle2");
        INFO_APC_alculator.getStyleClass().add("INFO_APCTitle2");
        INFO_VersionInfo.getStyleClass().add("INFO_VersionInfo");

        TextFlow InfoTitle = new TextFlow(INFO_APC_A, INFO_APC_rcaea, INFO_APC_P, INFO_APC_otential, INFO_APC_C, INFO_APC_alculator, INFO_VersionInfo);
        AppInfoGrid.add(InfoTitle, 5, 5);

        Label MakerLabel = new Label("개발");
        MakerLabel.getStyleClass().add("AppInfoCategoryTitle");
        AppInfoGrid.add(MakerLabel, 5, 9);
        AppInfoGrid.add(WLabel(String.format(
            "       BlueD981" /*+ "\n"*/ +
            ""
            )
        ), 5, 10);

        Label ArcaeaVersionLabel = new Label("대응 Arcaea 버전");
        ArcaeaVersionLabel.getStyleClass().add("AppInfoCategoryTitle");
        AppInfoGrid.add(ArcaeaVersionLabel, 5, 13);
        AppInfoGrid.add(WLabel(String.format(
            "       v7.0.255" /*+ "\n"*/ +
            ""
            )
        ), 5, 14);

        Label FontLabel = new Label("폰트");
        FontLabel.getStyleClass().add("AppInfoCategoryTitle");
        AppInfoGrid.add(FontLabel, 5, 17);
        AppInfoGrid.add(WLabel(String.format(
            "       [네이버 주식회사 | 프로그램 전체] 나눔고딕" /*+ "\n"*/ +
            ""
            )
        ), 5, 18);

        AppInfoGrid.add(new Label(""), 5, 21);

        ScrollPane AppInfoScroll = new ScrollPane(AppInfoGrid);
        AppInfoScroll.getStyleClass().add("Panes");
        AppInfoScroll.setFitToWidth(true);
        AppInfoScroll.setFocusTraversable(false);

        return AppInfoScroll;
    }

    public int themeStatus;

    public ScrollPane Settings() {
        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane settingsGrid = new GridPane();
        settingsGrid.getStyleClass().add("Panes");
        settingsGrid.setFocusTraversable(false);

        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);

        ScrollPane settingsScroll = new ScrollPane(settingsGrid);
        settingsScroll.getStyleClass().add("Panes");

        Text SETTINGSTITLE = new Text("환경설정");
        SETTINGSTITLE.setFocusTraversable(false);
        SETTINGSTITLE.getStyleClass().add("SETTINGSTITLE");
        settingsGrid.add(SETTINGSTITLE, 5, 5);


        Label DisplaySettings = new Label("디스플레이 설정");
        DisplaySettings.setFocusTraversable(false);
        DisplaySettings.getStyleClass().add("settingsCategoryTitle");
        settingsGrid.add(DisplaySettings, 5, 9);

        ToggleGroup ThemeSelection = new ToggleGroup();
        RadioButton LightTheme = new RadioButton("라이트 모드");
        LightTheme.getStyleClass().add("RadioButton");
        LightTheme.setToggleGroup(ThemeSelection);
        RadioButton DarkTheme = new RadioButton("다크 모드");
        DarkTheme.getStyleClass().add("RadioButton");
        DarkTheme.setToggleGroup(ThemeSelection);
        /*RadioButton CustomTheme = new RadioButton("커스텀 테마");
        CustomTheme.getStyleClass().add("RadioButton");
        CustomTheme.setToggleGroup(ThemeSelection);*/
        GridPane ThemeGrid = new GridPane();
        ThemeGrid.getStyleClass().add("Panes");
        ThemeGrid.setHgap(50);
        ThemeGrid.add(LightTheme, 0, 0);
        ThemeGrid.add(DarkTheme, 1, 0);
        settingsGrid.add(ThemeGrid, 5, 10);

        if (settings.get("Theme", "Light").equals("Light")) {
            settings.set("Theme", "Light");
            LightTheme.setSelected(true);
            DarkTheme.setSelected(false);
            /*CustomTheme.setSelected(false)*/
        }
        else if (settings.get("Theme", "Light").equals("Dark")) {
            settings.set("Theme", "Dark");
            DarkTheme.setSelected(true);
            LightTheme.setSelected(false);
            /*CustomTheme.setSelected(false);*/
        }
        /*else if (settings.get("Theme", "Light").equals("Custom")) {
            settings.set("Theme", "Custom");
            CustomTheme.setSelected(true);
            LightTheme.setSelected(false);
            DarkTheme.setSelected(false);
        }*/

        ThemeSelection.selectedToggleProperty().addListener((obs, old, val) -> {
            if (LightTheme.isSelected()) {
                settings.set("Theme", "Light");
                try {
                    /*System.out.println(settingsScroll.getScene());*/
                    String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(cssPath);
                    /*System.out.println("Light성공");*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        /*System.out.println(settingsScroll.getScene());*/
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                        /*System.out.println("Light성공");*/
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        /*System.out.println("Light실패");*/
                    }
                }
                /*System.out.println(themeStatus);*/
            }
            else if (DarkTheme.isSelected()) {
                settings.set("Theme", "Dark");
                try {
                    /*System.out.println(settingsScroll.getScene());*/
                    String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(cssPath);
                    /*System.out.println("Dark성공");*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        /*System.out.println(settingsScroll.getScene());*/
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                        /*System.out.println("Dark성공");*/
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        /*System.out.println("Dark실패");*/
                    }
                }
                /*System.out.println(themeStatus);*/
            }
            /*else if (CustomTheme.isSelected()) {
                settings.set("Theme", "Custom");
                try {
                    String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(cssPath);
                    System.out.println("Custom성공");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                        System.out.println("Custom성공");
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        System.out.println("Custom실패");
                    }
                }
                System.out.println(themeStatus);
            }*/
        });

        Label clearCheckBoxDefaultSettings = new Label("클리어 체크박스 기본값 설정");
        clearCheckBoxDefaultSettings.setFocusTraversable(false);
        clearCheckBoxDefaultSettings.getStyleClass().add("settingsCategoryTitle");
        settingsGrid.add(clearCheckBoxDefaultSettings, 5, 14);

        ToggleGroup clearCheckBoxDefaultSelection = new ToggleGroup();
        RadioButton clearChecked = new RadioButton("Track Complete");
        clearChecked.getStyleClass().add("RadioButton");
        clearChecked.setToggleGroup(clearCheckBoxDefaultSelection);
        RadioButton clearUnchecked = new RadioButton("Track Lost");
        clearUnchecked.getStyleClass().add("RadioButton");
        clearUnchecked.setToggleGroup(clearCheckBoxDefaultSelection);
        RadioButton clearFollowLatest = new RadioButton("최근 선택 유지");
        clearFollowLatest.getStyleClass().add("RadioButton");
        clearFollowLatest.setToggleGroup(clearCheckBoxDefaultSelection);
        GridPane clearCheckBoxDefaultGrid = new GridPane();
        clearCheckBoxDefaultGrid.getStyleClass().add("Panes");
        clearCheckBoxDefaultGrid.setHgap(22);
        clearCheckBoxDefaultGrid.setVgap(5);
        clearCheckBoxDefaultGrid.add(clearChecked, 0, 0);
        clearCheckBoxDefaultGrid.add(clearUnchecked, 1, 0);
        clearCheckBoxDefaultGrid.add(clearFollowLatest, 0, 1);
        settingsGrid.add(clearCheckBoxDefaultGrid, 5, 15);

        if (settings.get("clearCheckBoxDefault", "Checked").equals("Checked")) {
            settings.set("clearCheckBoxDefault", "Checked");
            isCleared = true;
            clearCheck.setSelected(true);
            clearChecked.setSelected(true);
            clearUnchecked.setSelected(false);
            clearFollowLatest.setSelected(false);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Not Checked")) {
            settings.set("clearCheckBoxDefault", "Not Checked");
            isCleared = false;
            clearCheck.setSelected(false);
            clearUnchecked.setSelected(true);
            clearChecked.setSelected(false);
            clearFollowLatest.setSelected(false);
        }
        else if (settings.get("clearCheckBoxDefault", "Checked").equals("Follow Latest Decision")) {
            settings.set("clearCheckBoxDefault", "Follow Latest Decision");
            if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                isCleared = true;
                clearCheck.setSelected(true);
            }
            else if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                isCleared = false;
                clearCheck.setSelected(false);
            }
            clearFollowLatest.setSelected(true);
            clearChecked.setSelected(false);
            clearUnchecked.setSelected(false);
        }

        clearCheckBoxDefaultSelection.selectedToggleProperty().addListener((obs, old, val) -> {
            if (clearChecked.isSelected()) {
                settings.set("clearCheckBoxDefault", "Checked");
                isCleared = true;
                clearCheck.setSelected(true);
            }
            else if (clearUnchecked.isSelected()) {
                settings.set("clearCheckBoxDefault", "Not Checked");
                isCleared = false;
                clearCheck.setSelected(false);
            }
            else if (clearFollowLatest.isSelected()) {
                settings.set("clearCheckBoxDefault", "Follow Latest Decision");
                if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                    isCleared = true;
                    clearCheck.setSelected(true);
                }
                else if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                    isCleared = false;
                    clearCheck.setSelected(false);
                }
            }
        });

        Label settingsResetLabel = new Label("설정 초기화");
        settingsResetLabel.setFocusTraversable(false);
        settingsResetLabel.getStyleClass().add("settingsCategoryTitle");
        settingsGrid.add(settingsResetLabel, 5, 19);

        Button SetSettingsToDefault = new Button("설정 초기화");
        SetSettingsToDefault.getStyleClass().add("Buttons");
        settingsGrid.add(SetSettingsToDefault, 5, 20);

        SetSettingsToDefault.setOnAction(SetDefault -> {
            settings.set("Theme", "Light");
            LightTheme.setSelected(true);
            DarkTheme.setSelected(false);
            try {
                /*System.out.println(settingsScroll.getScene());*/
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                settingsScroll.getScene().getStylesheets().clear();
                settingsScroll.getScene().getStylesheets().add(cssPath);
                /*System.out.println("Light성공");*/
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    /*System.out.println(settingsScroll.getScene());*/
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*System.out.println("Light성공");*/
                }
                catch (Exception f) {
                    f.printStackTrace();
                    /*System.out.println("Light실패");*/
                }
            }
            /*System.out.println(themeStatus);*/

            settings.set("clearCheckBoxDefault", "Checked");
            clearChecked.setSelected(true);
            clearUnchecked.setSelected(false);
            clearFollowLatest.setSelected(false);
        });

        settingsGrid.add(new Label(""), 5, 23);
        
        settingsScroll.setFitToWidth(true);
        settingsScroll.setFocusTraversable(false);

        return settingsScroll;
    }

    private void numInput() {
        Boolean bpValid = false;
        Boolean scoreValid = false;

        double bp = 0;
        double score = 0;
        double noteCount = 0;

        bpError.setText("");
        resultLabel.setText(String.format("결과 포텐셜 :%n--"));
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
            resultLabel.setText(String.format("결과 포텐셜 :%n") + APCLogic.APCLogicMain(bp, score, noteCount, isCleared));
        }
    }

    public void autoNumInput() {
        double score = 0;
        boolean scoreValid = true;
        if (scoreField.getText().isEmpty()) {
            scoreValid = false;
            scoreError.setText("");
            if (diffInt == null || song == null) {
                autoResultLabel.setText(String.format("결과 포텐셜 :%n--%n%n보면상수 :%n--%n%n총 노트 수 :%n--"));
            }
            else {
                autoResultLabel.setText(String.format("결과 포텐셜 :%n--%n%n보면상수 :%n%.1f%n%n총 노트 수 :%n%d", song.getConst(diffInt), song.getNotes(diffInt)));
            }
        }
        else {
            try {
                scoreValid = false;
                score = Double.parseDouble(scoreField.getText());
                if (diffInt == null || song == null) {
                    scoreValid = false;
                    scoreError.setText("");
                    if (score < 0 || score > 10002236) {
                        scoreError.setText("유효하지 않은 점수입니다.");
                        autoResultLabel.setText(String.format("결과 포텐셜 :%n--%n%n보면상수 :%n--%n%n총 노트 수 :%n--"));
                    }
                    else {
                        scoreError.setText("");
                    }
                }
                else if (score < 0 || score > 10002236) {
                    scoreValid = false;
                    scoreError.setText("유효하지 않은 점수입니다.");
                    autoResultLabel.setText(String.format("결과 포텐셜 :%n--%n%n보면상수 :%n--%n%n총 노트 수 :%n--"));
                }
                else if (score > song.getNotes(diffInt) + 10000000) {
                    scoreValid = true;
                    scoreError.setText(String.format("범위 초과 | 최대 점수: %d", song.getNotes(diffInt) + 10000000));
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
        if (scoreValid) {
            autoResultLabel.setText(String.valueOf(String.format("결과 포텐셜 :%n") + APCLogic.APCLogicMain(song.getConst(diffInt), score, (double) song.getNotes(diffInt), isCleared)) + String.format("%n%n보면상수 :%n%.1f%n%n총 노트 수 :%n%d", song.getConst(diffInt), song.getNotes(diffInt)));/*System.out.println("0 out");*/
        }
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
        Double searchParsed = -1.0;

        try {
            searchParsed = Double.parseDouble(search);
        }
        catch (Exception e) {
            searchParsed = -1.0;
        }

        for (Songs song : Songs.values()) {

            Boolean TitleMatch = song.getTitle().toLowerCase().replaceAll("\\s+", "").contains(search) || song.getBYDTitle().toLowerCase().replaceAll("\\s+", "").contains(search);

            for (int i = 0; i <= 4; i++) {
                if (DiffList.contains(String.valueOf(i)) && song.getConst(i) > 0) {
                    boolean LevelMatch = song.getLevel(i).equalsIgnoreCase(search);
                    boolean ConstMatch = (searchParsed > 0) && (song.getConst(i) == searchParsed);

                    if (TitleMatch || LevelMatch || ConstMatch) {
                        if (i < 4) {
                            ss.add(new SongInformation(song, song.getTitle(), song.getDiff(i), song.getLevel(i)));
                            continue;
                        }
                        else if (i == 4) {
                            ss.add(new SongInformation(song, song.getBYDTitle(), song.getDiff(i), song.getLevel(i)));
                            continue;
                        }
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
            if (DiffList.contains(String.valueOf("4")) && (song.getDifficulty().equals("BYD") || song.getDifficulty().equals("INS"))) {
                fs.add(song);
            }
        }
        return fs;
    }

    public static void main(String[] args) {
        launch(args);
    }
}