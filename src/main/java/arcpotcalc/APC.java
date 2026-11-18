package arcpotcalc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.Image;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.scene.Scene;

import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class APC extends Application {
    Font NanumGothicExtraBold = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicExtraBold.ttf"), 15);
    Font NanumGothicBold = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicBold.ttf"), 15);
    Font NanumGothic = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothic.ttf"), 15);
    Font NanumGothicLight = Font.loadFont(getClass().getResourceAsStream("/NanumFontSetup_TTF_GOTHIC/NanumGothicLight.ttf"), 15);

    private int PageIndex = 0;

    public double bpStr;

    private Button BackButton;

    private double BBActiveOpacity = 1;
    private double BBInactiveOpacity = 0.5;

    private javafx.collections.ObservableList<javafx.scene.Node> CA;
    private javafx.collections.ObservableList<javafx.scene.Node> RL;
    
    private Label scoreError = new Label("");
    private Label resultLabel = new Label(String.format("결과 포텐셜 :%n--"));
    private Label AOInputError = new Label("");

    private ChangeListener<String> bpListener = (obs, old, val) -> numInput();
    private ChangeListener<String> noteCountListener = (obs, old, val) -> numInput();
    private ChangeListener<String> searchListener = (obs, old, val) -> numInput();
    
    private StackPane rootLayout;
    private StackPane contentArea;

    private String version = "5.0";
    private String info = String.format("Arcaea Potential Calculator (ver. %s)", version);

    private String Username;
    
    private TextField scoreField = new TextField();

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                showErrorMessage                                                */
    /* -------------------------------------------------------------------------------------------------------------- */

    Label ErrorMessageLabel = new Label();

    private void showErrorMessage(String title, String message, String buttonMessage, int width, int height) {
        Stage alertStage = new Stage();
        alertStage.setTitle(title);

        ErrorMessageLabel.setText(message);
        
        GridPane alertGrid1 = new GridPane();
        alertGrid1.setHgap(25);
        alertGrid1.setVgap(25);
        
        GridPane alertGrid2 = new GridPane();
        alertGrid2.getStyleClass().add("Panes");
        alertGrid2.setHgap(10);
        alertGrid2.setVgap(10);
        alertGrid2.add(ErrorMessageLabel, 0, 0);
        
        Button ExitBtn = new Button();
        ExitBtn.setText(buttonMessage);
        ExitBtn.getStyleClass().add("Buttons");
        alertGrid2.add(ExitBtn, 1, 1);
        ExitBtn.setOnAction(a -> {
            alertStage.close();
        });
        
        alertGrid1.add(alertGrid2, 1, 1);
        
        Scene alertScene = new Scene(alertGrid1, width, height);
        
        try {
            if (settings.get("Theme", "Light").equals("Light")) {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                alertScene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Dark")) {
                String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                alertScene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Custom")) {
                String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                alertScene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                settings.set("Theme", "Light");
                alertScene.getStylesheets().add(cssPath);
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (settings.get("Theme", "Light").equals("Light")) {
                    alertScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Dark")) {
                    alertScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Custom")) {
                    alertScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else {
                    alertScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    settings.set("Theme", "Light");
                }
            }
            catch (Exception f) {
                f.printStackTrace();
                /*//System.out.println("실패");*/
            }
        }
        
        ExitBtn.setDefaultButton(true);
    
        alertStage.setScene(alertScene);
        alertStage.setResizable(false);
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.showAndWait();
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                   showFullB50                                                   */
    /* --------------------------------------------------------------------------------------------------------------- */
    
    private Label UsernameTextB = new Label();
    private Label B10AVGTextB = new Label();
    private Label B30AVGTextB = new Label();
    private Label B50AVGTextB = new Label();

    private String UserPTTB;

    private Text PTTTextB = new Text();

    private TableView<ScoreData> B30MainTableB = new TableView<>();

    private TableColumn<ScoreData, String> B30RIndexB = new TableColumn<>(String.format("순%n위"));
    private TableColumn<ScoreData, String> B30RDiffColB = new TableColumn<>("난이도");
    private TableColumn<ScoreData, String> B30RLevelColB = new TableColumn<>(String.format("레%n벨"));
    private TableColumn<ScoreData, String> B30RTitleColB = new TableColumn<>("제목");
    private TableColumn<ScoreData, String> B30RGradeColB = new TableColumn<>("등급");
    private TableColumn<ScoreData, String> B30RClearColB = new TableColumn<>("클리어");
    private TableColumn<ScoreData, Integer> B30RScoreColB = new TableColumn<>("점수");
    private TableColumn<ScoreData, Double> B30RPotentialColB = new TableColumn<>(String.format("플레이%n결과"));

    private TableView<ScoreData> B50MainTableB = new TableView<>();

    private TableColumn<ScoreData, String> B50RIndexB = new TableColumn<>(String.format("순%n위"));
    private TableColumn<ScoreData, String> B50RDiffColB = new TableColumn<>("난이도");
    private TableColumn<ScoreData, String> B50RLevelColB = new TableColumn<>(String.format("레%n벨"));
    private TableColumn<ScoreData, String> B50RTitleColB = new TableColumn<>("제목");
    private TableColumn<ScoreData, String> B50RGradeColB = new TableColumn<>("등급");
    private TableColumn<ScoreData, String> B50RClearColB = new TableColumn<>("클리어");
    private TableColumn<ScoreData, Integer> B50RScoreColB = new TableColumn<>("점수");
    private TableColumn<ScoreData, Double> B50RPotentialColB = new TableColumn<>(String.format("플레이%n결과"));

    private void showFullB50(String title, int width, int height) {
        Stage FullB50Stage = new Stage();
        FullB50Stage.setTitle(String.format(info + " | " +title));

        GridPane FullB50Grid = new GridPane();
        FullB50Grid.getStyleClass().add("Panes");
        FullB50Grid.setHgap(10);
        FullB50Grid.setVgap(10);

        UsernameTextB.setText(Username);
        UsernameTextB.setWrapText(true);
        UsernameTextB.setMaxWidth(360);
        UsernameTextB.setMaxHeight(10);
        UsernameTextB.getStyleClass().add("SETTINGSTITLE");
        PTTTextB.setText(UserPTTB);
        PTTTextB.getStyleClass().add("SETTINGSTITLE");
        Label UsernameTitle = new Label("닉네임");
        UsernameTitle.getStyleClass().add("APCTitle1");
        Label BIGSPLITTER1 = new Label("|");
        Label BIGSPLITTER2 = new Label("|");
        BIGSPLITTER1.getStyleClass().add("APCTitle1");
        BIGSPLITTER2.getStyleClass().add("APCTitle1");
        Label PotentialTitle = new Label("포텐셜");
        PotentialTitle.getStyleClass().add("APCTitle1");
        B10AVGTextB.setText(B10AVG);
        B10AVGTextB.getStyleClass().add("APCTitle2");
        Label B10AVGTitle = new Label("B10 평균");
        Label B10AVGSplitter = new Label("|");
        B10AVGTitle.getStyleClass().add("INFO_VersionInfo");
        B10AVGSplitter.getStyleClass().add("INFO_VersionInfo");
        B30AVGTextB.setText(B30AVG);
        B30AVGTextB.getStyleClass().add("INFO_VersionInfo");
        Label B30AVGTitle = new Label("B30 평균");
        Label B30AVGSplitter = new Label("|");
        B30AVGTitle.getStyleClass().add("INFO_VersionInfo");
        B30AVGSplitter.getStyleClass().add("INFO_VersionInfo");
        B50AVGTextB.setText(B50AVG);
        B50AVGTextB.getStyleClass().add("INFO_VersionInfo");
        Label B50AVGTitle = new Label("B50 평균");
        Label B50AVGSplitter = new Label("|");
        B50AVGTitle.getStyleClass().add("INFO_VersionInfo");
        B50AVGSplitter.getStyleClass().add("INFO_VersionInfo");

        Scene FullB50Scene = new Scene(FullB50Grid, width, height);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/APC_black.jpg"));
            FullB50Stage.getIcons().add(icon);
        }
        catch (Exception e) {
            FullB50Stage.getIcons().add(new Image("src/main/resources/APC_black.jpg"));
        }

        try {
            if (settings.get("Theme", "Light").equals("Light")) {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                FullB50Scene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Dark")) {
                String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                FullB50Scene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Custom")) {
                String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                FullB50Scene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                settings.set("Theme", "Light");
                FullB50Scene.getStylesheets().add(cssPath);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (settings.get("Theme", "Light").equals("Light")) {
                    FullB50Scene.getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Dark")) {
                    FullB50Scene.getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Custom")) {
                    FullB50Scene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else {
                    FullB50Scene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    settings.set("Theme", "Light");
                }
            }
            catch (Exception f) {
                f.printStackTrace();
                /*//System.out.println("실패");*/
            }
        }

        List<ScoreData> SDLCopy = new ArrayList<>();
        try (FileReader reader = new FileReader(B50Location)) {
            Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
            List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
            if (SDList != null && !SDList.isEmpty()) {
                SDLCopy = new ArrayList<>(SDList);
                SDLCopy.sort(
                    Comparator.comparingLong((ScoreData a) -> Math.round(a.getPotential() * 1000000.0))
                    .reversed()
                    .thenComparing(b -> {
                        return b.getIndex();
                    })
                    .thenComparing(c -> {
                        return c.getSong();
                    })
                );
            }
            else {
                SDLCopy.clear();
            }
        }
        catch (Exception e) {
            //System.out.println("파일이 존재하지 않음");
            B50jsonStarter(null);
        }
        List<ScoreData> SDL30 = new ArrayList<>();
        List<ScoreData> SDL50 = new ArrayList<>();
        if (SDLCopy != null && SDLCopy.size() >= 0 && SDLCopy.size() > 50) {
            for (int i = 0; i < 30; i++) {
                SDL30.add(SDLCopy.get(i));
            }
            for (int j = 30; j < 50; j++) {
                SDL50.add(SDLCopy.get(j));
            }
        }
        else if (SDLCopy != null && SDLCopy.size() >= 0 && SDLCopy.size() <= 50) {
            if (SDLCopy.size() >= 30) {
                ;
            }
            else if (SDLCopy.size() < 30) {
                SDL30 = SDLCopy;
            }
        }

        ObservableList<ScoreData> SDL30Obs = FXCollections.observableArrayList(SDL30);

        B30RIndexB.setReorderable(false);
        B30RIndexB.setSortable(false);
        B30RDiffColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty()));
        B30RDiffColB.setReorderable(false);
        B30RDiffColB.setSortable(false);
        B30RLevelColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLevel()));
        B30RLevelColB.setReorderable(false);
        B30RLevelColB.setSortable(false);
        B30RTitleColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        B30RTitleColB.setReorderable(false);
        B30RTitleColB.setSortable(false);
        B30RGradeColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        B30RGradeColB.setReorderable(false);
        B30RGradeColB.setSortable(false);
        B30RClearColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShortClearStatus()));
        B30RClearColB.setReorderable(false);
        B30RClearColB.setSortable(false);
        B30RScoreColB.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScore()).asObject());
        B30RScoreColB.setReorderable(false);
        B30RScoreColB.setSortable(false);
        B30RPotentialColB.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPotential()).asObject());
        B30RPotentialColB.setReorderable(false);
        B30RPotentialColB.setSortable(false);

        ObservableList<ScoreData> SDL50Obs = FXCollections.observableArrayList(SDL50);

        B50RIndexB.setReorderable(false);
        B50RIndexB.setSortable(false);
        B50RDiffColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty()));
        B50RDiffColB.setReorderable(false);
        B50RDiffColB.setSortable(false);
        B50RLevelColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLevel()));
        B50RLevelColB.setReorderable(false);
        B50RLevelColB.setSortable(false);
        B50RTitleColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        B50RTitleColB.setReorderable(false);
        B50RTitleColB.setSortable(false);
        B50RGradeColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        B50RGradeColB.setReorderable(false);
        B50RGradeColB.setSortable(false);
        B50RClearColB.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShortClearStatus()));
        B50RClearColB.setReorderable(false);
        B50RClearColB.setSortable(false);
        B50RScoreColB.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScore()).asObject());
        B50RScoreColB.setReorderable(false);
        B50RScoreColB.setSortable(false);
        B50RPotentialColB.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPotential()).asObject());
        B50RPotentialColB.setReorderable(false);
        B50RPotentialColB.setSortable(false);


        B30RTitleColB.setCellFactory(column -> new TableCell<ScoreData, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });

        B30RPotentialColB.setCellFactory(column -> new TableCell<ScoreData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(String.format("%.3f", item));
                }
            }
        });
        
        B30RScoreColB.setCellFactory(column -> new TableCell<ScoreData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String commaScore = String.format("%,d", item);
                    String commaScoreB = commaScore.replace(",", "\'");

                    String zeros = "00\'000\'000";

                    setText(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                }
            }
        });

        B30RIndexB.setCellFactory(column -> new TableCell<ScoreData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        B50RTitleColB.setCellFactory(column -> new TableCell<ScoreData, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });

        B50RPotentialColB.setCellFactory(column -> new TableCell<ScoreData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(String.format("%.3f", item));
                }
            }
        });
        
        B50RScoreColB.setCellFactory(column -> new TableCell<ScoreData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String commaScore = String.format("%,d", item);
                    String commaScoreB = commaScore.replace(",", "\'");

                    String zeros = "00\'000\'000";

                    setText(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                }
            }
        });

        B50RIndexB.setCellFactory(column -> new TableCell<ScoreData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 31));
                }
            }
        });

        int B50RIWidth = 25;
        B30RIndexB.setMinWidth(B50RIWidth);
        B30RIndexB.setMaxWidth(B50RIWidth);
        B50RIndexB.setMinWidth(B50RIWidth);
        B50RIndexB.setMaxWidth(B50RIWidth);

        int B50RDWidth = 35;
        B30RDiffColB.setMinWidth(B50RDWidth);
        B30RDiffColB.setMaxWidth(B50RDWidth);
        B50RDiffColB.setMinWidth(B50RDWidth);
        B50RDiffColB.setMaxWidth(B50RDWidth);

        int B50RLWidth = 35;
        B30RLevelColB.setMinWidth(B50RLWidth);
        B30RLevelColB.setMaxWidth(B50RLWidth);
        B50RLevelColB.setMinWidth(B50RLWidth);
        B50RLevelColB.setMaxWidth(B50RLWidth);

        int B50RTWidth = 180;
        B30RTitleColB.setMinWidth(B50RTWidth);
        B30RTitleColB.setMaxWidth(B50RTWidth);
        B50RTitleColB.setMinWidth(B50RTWidth);
        B50RTitleColB.setMaxWidth(B50RTWidth);

        int B50RGWidth = 35;
        B30RGradeColB.setMinWidth(B50RGWidth);
        B30RGradeColB.setMaxWidth(B50RGWidth);
        B50RGradeColB.setMinWidth(B50RGWidth);
        B50RGradeColB.setMaxWidth(B50RGWidth);

        int B50RCWidth = 35;
        B30RClearColB.setMinWidth(B50RCWidth);
        B30RClearColB.setMaxWidth(B50RCWidth);
        B50RClearColB.setMinWidth(B50RCWidth);
        B50RClearColB.setMaxWidth(B50RCWidth);

        int B50RSWidth = 85;
        B30RScoreColB.setMinWidth(B50RSWidth);
        B30RScoreColB.setMaxWidth(B50RSWidth);
        B50RScoreColB.setMinWidth(B50RSWidth);
        B50RScoreColB.setMaxWidth(B50RSWidth);

        int B50RPWidth = 50;
        B30RPotentialColB.setMinWidth(B50RPWidth);
        B30RPotentialColB.setMaxWidth(B50RPWidth);
        B50RPotentialColB.setMinWidth(B50RPWidth);
        B50RPotentialColB.setMaxWidth(B50RPWidth);

        B30MainTableB.setItems(SDL30Obs);
        B50MainTableB.setItems(SDL50Obs);

        Rectangle clip30 = new Rectangle();
        clip30.widthProperty().bind(B30MainTableB.widthProperty());
        clip30.heightProperty().bind(B30MainTableB.heightProperty());
        clip30.setArcWidth(40);
        clip30.setArcHeight(40);
        B30MainTableB.setClip(clip30);

        Rectangle clip50 = new Rectangle();
        clip50.widthProperty().bind(B50MainTableB.widthProperty());
        clip50.heightProperty().bind(B50MainTableB.heightProperty());
        clip50.setArcWidth(40);
        clip50.setArcHeight(40);
        B50MainTableB.setClip(clip50);

        B30MainTableB.getColumns().clear();
        B30MainTableB.getColumns().add(B30RIndexB);
        B30MainTableB.getColumns().add(B30RDiffColB);
        B30MainTableB.getColumns().add(B30RLevelColB);
        B30MainTableB.getColumns().add(B30RTitleColB);
        B30MainTableB.getColumns().add(B30RGradeColB);
        B30MainTableB.getColumns().add(B30RClearColB);
        B30MainTableB.getColumns().add(B30RScoreColB);
        B30MainTableB.getColumns().add(B30RPotentialColB);

        B50MainTableB.getColumns().clear();
        B50MainTableB.getColumns().add(B50RIndexB);
        B50MainTableB.getColumns().add(B50RDiffColB);
        B50MainTableB.getColumns().add(B50RLevelColB);
        B50MainTableB.getColumns().add(B50RTitleColB);
        B50MainTableB.getColumns().add(B50RGradeColB);
        B50MainTableB.getColumns().add(B50RClearColB);
        B50MainTableB.getColumns().add(B50RScoreColB);
        B50MainTableB.getColumns().add(B50RPotentialColB);

        B30MainTableB.setMinHeight(812);
        B30MainTableB.setMinHeight(812);
        B30MainTableB.setMaxWidth(490);
        B30MainTableB.setMaxWidth(490);

        GridPane B50SubPaneB = new GridPane();
        B50SubPaneB.setHgap(10);
        B50SubPaneB.setVgap(5);
        B50SubPaneB.add(B10AVGTitle, 0, 0);
        B50SubPaneB.add(B10AVGSplitter, 1, 0);
        B50SubPaneB.add(B10AVGTextB, 2, 0);
        B50SubPaneB.add(B30AVGTitle, 0, 1);
        B50SubPaneB.add(B30AVGSplitter, 1, 1);
        B50SubPaneB.add(B30AVGTextB, 2, 1);
        B50SubPaneB.add(B50AVGTitle, 0, 2);
        B50SubPaneB.add(B50AVGSplitter, 1, 2);
        B50SubPaneB.add(B50AVGTextB, 2, 2);

        GridPane FullB50SubGrid1 = new GridPane();
        FullB50SubGrid1.setFocusTraversable(false);
        FullB50SubGrid1.setHgap(10);
        FullB50SubGrid1.setVgap(10);

        GridPane FullB50SubGrid2 = new GridPane();
        FullB50SubGrid2.setFocusTraversable(false);
        FullB50SubGrid2.setHgap(10);
        FullB50SubGrid2.setVgap(10);

        GridPane FullB50SubGrid3 = new GridPane();
        FullB50SubGrid3.setFocusTraversable(false);
        FullB50SubGrid3.setHgap(10);
        FullB50SubGrid3.setVgap(10);

        GridPane FullB50SubGrid4 = new GridPane();
        FullB50SubGrid4.setFocusTraversable(false);
        FullB50SubGrid4.setHgap(13);
        FullB50SubGrid4.setVgap(5);

        B50MainTableB.setMinHeight(552);
        B50MainTableB.setMinHeight(552);
        B50MainTableB.setMaxWidth(490);
        B50MainTableB.setMaxWidth(490);

        FullB50SubGrid1.add(B30MainTableB, 0, 0);
        FullB50SubGrid2.add(B50MainTableB, 0, 0);
        FullB50SubGrid2.add(FullB50SubGrid3, 0, 1);

        FullB50SubGrid4.add(UsernameTitle, 0, 0);
        FullB50SubGrid4.add(BIGSPLITTER1, 1, 0);
        FullB50SubGrid4.add(UsernameTextB, 2, 0);
        FullB50SubGrid4.add(PotentialTitle, 0, 1);
        FullB50SubGrid4.add(BIGSPLITTER2, 1, 1);
        FullB50SubGrid4.add(PTTTextB, 2, 1);
        
        FullB50SubGrid3.add(FullB50SubGrid4, 3, 3);
        FullB50SubGrid3.add(B50SubPaneB, 3, 4);

        FullB50Grid.add(FullB50SubGrid1, 5, 5);
        FullB50Grid.add(FullB50SubGrid2, 6, 5);

        FullB50Stage.setScene(FullB50Scene);
        FullB50Stage.setResizable(false);
        FullB50Stage.initModality(Modality.APPLICATION_MODAL);
        FullB50Stage.showAndWait();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                     WLabel                                                     */
    /* -------------------------------------------------------------------------------------------------------------- */

    private Label WLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                      start                                                      */
    /* --------------------------------------------------------------------------------------------------------------- */

    File B50Location = new File(System.getProperty("user.home"), ".APC/ScoreData.json");

    Gson B50Builder = new GsonBuilder().setPrettyPrinting().create();
    Gson Gson = new Gson();

    @Override
    public void start(Stage APCMain) {
        settings.loadSettings();
        /*//System.out.println(settings.get("Theme", "ThemeDefault"));*/

        if (settings.get("Username", null) == null) {
            settings.set("Username", "Username");
        }
        Username = settings.get("Username", "Username");
        
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
        AOInputError.getStyleClass().add("ErrorText");

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
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Dark")) {
                String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                primaryScene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else if (settings.get("Theme", "Light").equals("Custom")) {
                String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                primaryScene.getStylesheets().add(cssPath);
                /*//System.out.println("성공");*/
            }
            else {
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                settings.set("Theme", "Light");
                primaryScene.getStylesheets().add(cssPath);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (settings.get("Theme", "Light").equals("Light")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Dark")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else if (settings.get("Theme", "Light").equals("Custom")) {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("성공");*/
                }
                else {
                    primaryScene.getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                    settings.set("Theme", "Light");
                }
            }
            catch (Exception f) {
                f.printStackTrace();
                /*//System.out.println("실패");*/
            }
        }
        try {
            if (settings.get("clearCheckBoxDefault", "Checked").equals("Follow Latest Decision")) {
                if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                    isCleared = true;
                    clearCheck.setSelected(true);
                    /*//System.out.println("TC성공");*/
                }
                if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                    isCleared = false;
                    clearCheck.setSelected(false);
                    /*//System.out.println("TL성공");*/
                }
            }
            else if (settings.get("clearCheckBoxDefault", "Checked").equals("Checked")) {
                isCleared = true;
                clearCheck.setSelected(true);
                /*//System.out.println("C성공");*/
            }
            else if (settings.get("clearCheckBoxDefault", "Checked").equals("Not Checked")) {
                isCleared = false;
                clearCheck.setSelected(false);
                /*//System.out.println("NC성공");*/
            }
            else {
                isCleared = true;
                clearCheck.setSelected(true);
                settings.set("clearCheckBoxDefault", "Checked");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            /*//System.out.println("실패");*/
        }
        try {
            if (settings.get("clearLatest", "Track Complete").equals("Track Complete")) {
                isCleared = true;
                clearCheck.setSelected(true);
                settings.set("clearLatest", "Track Complete");
            }
            else if (settings.get("clearLatest", "Track Complete").equals("Track Lost")) {
                isCleared = false;
                clearCheck.setSelected(false);
                settings.set("clearLatest", "Track Lost");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                settings.set("B50InputDifficultyDefault", "PST");
            }
            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                settings.set("B50InputDifficultyDefault", "PRS");
            }
            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                settings.set("B50InputDifficultyDefault", "FTR");
            }
            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                settings.set("B50InputDifficultyDefault", "ETR");
            }
            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                settings.set("B50InputDifficultyDefault", "BYD");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        APCMain.setScene(primaryScene);
        APCMain.setResizable(false);
        APCMain.show();
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                     go_back                                                     */
    /* --------------------------------------------------------------------------------------------------------------- */

    public void go_back() {
        bpField.textProperty().removeListener(bpListener);
        scoreField.textProperty().removeListener(ManualScoreListener);
        scoreField.textProperty().removeListener(AutoScoreListener);
        noteCountField.textProperty().removeListener(noteCountListener);
        searchField.textProperty().removeListener(searchListener);
        searchResultTable.getSelectionModel().selectedItemProperty().removeListener(resultListener);
        searchResultTable.setItems(null);
        clearCheck.selectedProperty().removeListener(clearCheckListener);

        if (PageIndex == 1) {
            CA.clear();
            CA.add(mainMenu());
        }
        else if (PageIndex == 2) {
            CA.clear();
            CA.add(mainMenu());
        }
        else if (PageIndex == 3) {
            CA.clear();
            CA.add(mainMenu());
        }
        else if (PageIndex == 31) {
            CA.clear();
            CA.add(B50());
        }
        else if (PageIndex == 32) {
            CA.clear();
            CA.add(B50());
        }
        else if (PageIndex == 4) {
            CA.clear();
            CA.add(mainMenu());
        }
        else if (PageIndex == 5) {
            CA.clear();
            CA.add(mainMenu());
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                    mainMenu                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    public GridPane mainMenu() {
        PageIndex = 0;

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
        Button B50Btn = new Button("B50 조회 및 관리");
        Button SettingsBtn = new Button("설정");
        Button InfoBtn = new Button("앱 정보");
        ManualBtn.getStyleClass().add("Buttons");
        AutoBtn.getStyleClass().add("Buttons");
        B50Btn.getStyleClass().add("Buttons");
        SettingsBtn.getStyleClass().add("Buttons");
        InfoBtn.getStyleClass().add("Buttons");

        ManualBtn.setPrefWidth(160);
        AutoBtn.setPrefWidth(160);
        B50Btn.setPrefWidth(160);
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
        menu1.add(ManualBtn, 5, 6);
        menu1.add(AutoBtn, 5, 7);
        menu1.add(B50Btn, 5, 8);
        GridPane twoButtons = new GridPane();
        twoButtons.getStyleClass().add("Panes");
        twoButtons.setHgap(10);
        twoButtons.add(SettingsBtn, 0, 0);
        twoButtons.add(InfoBtn, 1, 0);
        menu1.add(twoButtons, 5, 9);

        ManualBtn.setOnAction(Mnclick -> {
            CA.clear();
            CA.add(ManualInput());
        });
        AutoBtn.setOnAction(Atclick -> {
            CA.clear();
            CA.add(AutoInput());
        });
        B50Btn.setOnAction(B50click -> {
            CA.clear();
            CA.add(B50());
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
        menu_info.setVgap(25);
        menu_info.add(blankLabel, 0, 0);
        menu_info.add(versionLabel, 1, 1);

        menu.add(menu1, 0, 0);
        menu.add(menu_info, 0, 1);

        return menu;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                   ManualInput                                                   */
    /* --------------------------------------------------------------------------------------------------------------- */

    private Label bpError = new Label("");
    private Label noteCountError = new Label("");

    private ChangeListener<Boolean> clearCheckListener = (obs, old, val) -> {
            if (val) {
                isCleared = true;
                settings.set("clearLatest", "Track Complete");
                /*//System.out.println(settings.get("clearLatest", "Track Complete"));*/
            }
            else {
                isCleared = false;
                settings.set("clearLatest", "Track Lost");
                /*//System.out.println(settings.get("clearLatest", "Track Lost"));*/
            }
            autoNumInput();
        };

    private ChangeListener<String> ManualScoreListener = (obs, old, val) -> numInput();

    private CheckBox clearCheck = new CheckBox("클리어");

    public boolean isCleared = false;

    private TextField bpField = new TextField();
    private TextField noteCountField = new TextField();

    public GridPane ManualInput() {
        PageIndex = 1;

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

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                    AutoInput                                                    */
    /* --------------------------------------------------------------------------------------------------------------- */

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
        PageIndex = 2;

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

                searchResultTable.setItems(filteredSongs(DiffList, searchField.getText()));
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

                searchResultTable.setItems(filteredSongs(DiffList, searchField.getText()));
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
        titleCol.setMinWidth(144);
        titleCol.setMaxWidth(144);
        diffCol.setMinWidth(50);
        diffCol.setMaxWidth(50);
        levelCol.setMinWidth(40);
        levelCol.setMaxWidth(40);

        titleCol.setCellFactory(column -> new TableCell<SongInformation, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });

        titleCol.setReorderable(false);
        diffCol.setReorderable(false);
        levelCol.setReorderable(false);
        
        searchResultTable.getColumns().clear();
        searchResultTable.getColumns().add(titleCol);
        searchResultTable.getColumns().add(diffCol);
        searchResultTable.getColumns().add(levelCol);
        searchResultTable.setItems(filteredSongs(DiffList, searchField.getText()));
        searchResultTable.setMinHeight(75);
        searchResultTable.setMinHeight(75);
        searchResultTable.setMaxWidth(250);
        searchResultTable.setMaxWidth(250);
        atip.add(searchResultTable, 5, 8);
        atip.add(new Label(""), 5, 9);

        searchResultTable.getSelectionModel().selectedItemProperty().addListener(resultListener);

        searchField.setPromptText("여기에 검색어 입력");
        searchField.textProperty().addListener((obs, old, val) -> {
            searchResultTable.setItems(filteredSongs(DiffList, searchField.getText()));
        });

        GridPane scoreFieldPane = new GridPane();
        scoreFieldPane.setHgap(10);
        scoreField.setPromptText("여기에 점수 입력");
        scoreField.textProperty().addListener(AutoScoreListener);
        scoreField.setMinWidth(200);
        scoreField.setMaxWidth(200);

        scoreFieldPane.add(scoreField, 0, 0);
        scoreFieldPane.add(clearCheck, 1, 0);
        clearCheck.selectedProperty().addListener(clearCheckListener);
        atip.add(scoreFieldPane, 7, 6);

        atip.add(scoreError, 7, 7);

        atip.add(autoResultLabel, 7, 8);

        atip.setFocusTraversable(false);

        return atip;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                       B50                                                       */
    /* --------------------------------------------------------------------------------------------------------------- */

    private TableView<ScoreData> B50MainTable = new TableView<>();

    private TableColumn<ScoreData, String> B50RIndex = new TableColumn<>(String.format("순%n위"));
    private TableColumn<ScoreData, String> B50RDiffCol = new TableColumn<>("난이도");
    private TableColumn<ScoreData, String> B50RLevelCol = new TableColumn<>(String.format("레%n벨"));
    private TableColumn<ScoreData, String> B50RTitleCol = new TableColumn<>("제목");
    private TableColumn<ScoreData, String> B50RGradeCol = new TableColumn<>("등급");
    private TableColumn<ScoreData, Integer> B50RScoreCol = new TableColumn<>("점수");
    private TableColumn<ScoreData, Double> B50RPotentialCol = new TableColumn<>(String.format("플레이%n결과"));
    
    private Label UsernameText = new Label();
    private Label B10AVGText = new Label();
    private Label B30AVGText = new Label();
    private Label B50AVGText = new Label();
    private Text PTTText = new Text();

    private String UserPTT;
    private String B10AVG;
    private String B30AVG;
    private String B50AVG;

    public double PTT;
    
    List<ScoreData> SDLCopy = new ArrayList<>();
    
    public GridPane B50() {
        PageIndex = 3;
        
        try (FileReader reader = new FileReader(B50Location)) {
            Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
            List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
            if (SDList != null && !SDList.isEmpty()) {
                SDLCopy = new ArrayList<>(SDList);
                SDLCopy.sort(
                    Comparator.comparingLong((ScoreData a) -> Math.round(a.getPotential() * 1000000.0))
                    .reversed()
                    .thenComparing(b -> {
                        return b.getIndex();
                    })
                    .thenComparing(c -> {
                        return c.getSong();
                    })
                );
            }
            else {
                B50jsonStarter(null);
                SDLCopy.clear();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("파일이 존재하지 않음");
            B50jsonStarter(null);
        }

        double B10PTTsum = 0;
        double B30PTTsum = 0;
        double B50PTTsum = 0;
        List<ScoreData> SDL50 = new ArrayList<>();
        if (SDLCopy != null && SDLCopy.size() >= 0 && SDLCopy.size() > 50) {
            for (int i = 0; i < 50; i++) {
                B50PTTsum += SDLCopy.get(i).getPotential();
                SDL50.add(SDLCopy.get(i));
            }
            for (int j = 0; j < 30; j++) {
                B30PTTsum += SDLCopy.get(j).getPotential();
            }
            for (int k = 0; k < 10; k++) {
                B10PTTsum += SDLCopy.get(k).getPotential();
            }
        }
        else if (SDLCopy != null && SDLCopy.size() >= 0 && SDLCopy.size() <= 50) {
            SDL50 = SDLCopy;
            if (SDLCopy.size() <= 50 && SDLCopy.size() > 30) {
                for (int l = 0; l < SDL50.size(); l++) {
                    B50PTTsum += SDLCopy.get(l).getPotential();
                }
            }
            else if (SDLCopy.size() <= 30 && SDLCopy.size() > 10) {
                for (int m = 0; m < SDLCopy.size(); m++) {
                    B30PTTsum += SDLCopy.get(m).getPotential();
                }
            }
            else if (SDLCopy.size() <= 10) {
                for (int n = 0; n < SDLCopy.size(); n++) {
                    B10PTTsum += SDLCopy.get(n).getPotential();
                }
            }
        }

        PTT = BigDecimal.valueOf((B10PTTsum + B50PTTsum) / 60).setScale(3, RoundingMode.DOWN).doubleValue();
        if (PTT >= 12.000 && PTT < 12.500) {
            UserPTT = String.format("★ | %.3f", PTT);
            UserPTTB = String.format("[ ★ ] %.3f", PTT);
        }
        else if (PTT >= 12.500 && PTT < 13.000) {
            UserPTT = String.format("★★ | %.3f", PTT);
            UserPTTB = String.format("[ ★★ ] %.3f", PTT);
        }
        else if (PTT >= 13.000 && PTT < 13.500) {
            UserPTT = String.format("★★★ | %.3f", PTT);
            UserPTTB = String.format("[ ★★★ ] %.3f", PTT);
        }
        else {
            UserPTT = String.format("%.3f", PTT);
            UserPTTB = String.format("%.3f", PTT);
        }

        B10AVG = String.format("%.3f", B10PTTsum / 10);
        B30AVG = String.format("%.3f", B30PTTsum / 30);
        B50AVG = String.format("%.3f", B50PTTsum / 50);

        ObservableList<ScoreData> SDL50Obs = FXCollections.observableArrayList(SDL50);

        B50RIndex.setReorderable(false);
        B50RIndex.setSortable(false);
        B50RDiffCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty()));
        B50RDiffCol.setReorderable(false);
        B50RDiffCol.setSortable(false);
        B50RLevelCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLevel()));
        B50RLevelCol.setReorderable(false);
        B50RLevelCol.setSortable(false);
        B50RTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        B50RTitleCol.setReorderable(false);
        B50RTitleCol.setSortable(false);
        B50RGradeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        B50RGradeCol.setReorderable(false);
        B50RGradeCol.setSortable(false);
        B50RScoreCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScore()).asObject());
        B50RScoreCol.setReorderable(false);
        B50RScoreCol.setSortable(false);
        B50RPotentialCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPotential()).asObject());
        B50RPotentialCol.setReorderable(false);
        B50RPotentialCol.setSortable(false);

        B50RTitleCol.setCellFactory(column -> new TableCell<ScoreData, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        });

        B50RPotentialCol.setCellFactory(column -> new TableCell<ScoreData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(String.format("%.3f", item));
                }
            }
        });
        
        B50RScoreCol.setCellFactory(column -> new TableCell<ScoreData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String commaScore = String.format("%,d", item);
                    String commaScoreB = commaScore.replace(",", "\'");

                    String zeros = "00\'000\'000";

                    setText(zeros.substring(0, 10 - commaScoreB.length()) + commaScoreB);
                }
            }
        });

        B50RIndex.setCellFactory(column -> new TableCell<ScoreData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        int B50RIWidth = 25;
        B50RIndex.setMinWidth(B50RIWidth);
        B50RIndex.setMaxWidth(B50RIWidth);

        int B50RDWidth = 35;
        B50RDiffCol.setMinWidth(B50RDWidth);
        B50RDiffCol.setMaxWidth(B50RDWidth);

        int B50RLWidth = 35;
        B50RLevelCol.setMinWidth(B50RLWidth);
        B50RLevelCol.setMaxWidth(B50RLWidth);

        int B50RTWidth = 119;
        B50RTitleCol.setMinWidth(B50RTWidth);
        B50RTitleCol.setMaxWidth(B50RTWidth);

        int B50RGWidth = 35;
        B50RGradeCol.setMinWidth(B50RGWidth);
        B50RGradeCol.setMaxWidth(B50RGWidth);

        int B50RSWidth = 85;
        B50RScoreCol.setMinWidth(B50RSWidth);
        B50RScoreCol.setMaxWidth(B50RSWidth);

        int B50RPWidth = 50;
        B50RPotentialCol.setMinWidth(B50RPWidth);
        B50RPotentialCol.setMaxWidth(B50RPWidth);

        B50MainTable.setItems(SDL50Obs);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(B50MainTable.widthProperty());
        clip.heightProperty().bind(B50MainTable.heightProperty());
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        B50MainTable.setClip(clip);

        B50MainTable.getColumns().clear();
        B50MainTable.getColumns().add(B50RIndex);
        B50MainTable.getColumns().add(B50RDiffCol);
        B50MainTable.getColumns().add(B50RLevelCol);
        B50MainTable.getColumns().add(B50RTitleCol);
        B50MainTable.getColumns().add(B50RGradeCol);
        B50MainTable.getColumns().add(B50RScoreCol);
        B50MainTable.getColumns().add(B50RPotentialCol);

        B50MainTable.setMinHeight(30);
        B50MainTable.setMinHeight(30);
        B50MainTable.setMaxWidth(400);
        B50MainTable.setMaxWidth(400);

        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane B50Main = new GridPane();
        B50Main.setHgap(10);
        B50Main.setVgap(10);

        int B50BtnWidth = 150;
        int B50InputBtnWidth = 70;

        GridPane B50InputButtonPane = new GridPane();
        B50InputButtonPane.setHgap(10);
        B50InputButtonPane.setFocusTraversable(false);
        
        Button B50AutoBtn = new Button("자동");
        B50AutoBtn.getStyleClass().add("Buttons");
        B50AutoBtn.setPrefWidth(B50InputBtnWidth);
        
        Button B50ManualBtn = new Button("수동");
        B50ManualBtn.getStyleClass().add("Buttons");
        B50ManualBtn.setPrefWidth(B50InputBtnWidth);
        
        Button B50SeeBigger = new Button("크게 보기");
        B50SeeBigger.getStyleClass().add("Buttons");
        B50SeeBigger.setPrefWidth(B50BtnWidth);
        
        B50AutoBtn.setOnAction(a -> {
            CA.clear();
            CA.add(B50Manage());
        });
        
        B50ManualBtn.setOnAction(a -> {
            CA.clear();
            CA.add(B50Input());
        });
        
        B50SeeBigger.setOnAction(a -> {
            showFullB50("B50", 1087, 912);
        });
        
        B50InputButtonPane.add(B50AutoBtn, 0, 1);
        B50InputButtonPane.add(B50ManualBtn, 1, 1);
        
        GridPane B50MainControlPane = new GridPane();
        B50MainControlPane.setHgap(10);
        B50MainControlPane.setVgap(10);
        B50MainControlPane.add(new Label("스코어 데이터 입력 :"), 1, 7);
        B50MainControlPane.add(B50InputButtonPane, 1, 8);
        B50MainControlPane.add(B50SeeBigger, 1, 9);
        UsernameText.setText(Username);
        UsernameText.setWrapText(true);
        UsernameText.setMaxWidth(170);
        UsernameText.getStyleClass().add("SETTINGSTITLE");
        PTTText.setText(UserPTT);
        PTTText.getStyleClass().add("APCTitle1");
        B10AVGText.setText(B10AVG);
        B30AVGText.setText(B30AVG);
        B50AVGText.setText(B50AVG);
        B50MainControlPane.add(UsernameText, 1, 1);
        B50MainControlPane.add(PTTText, 1, 2);
        GridPane B50SubPane = new GridPane();
        B50SubPane.setHgap(10);
        B50SubPane.add(new Label("B10 평균"), 0, 0);
        B50SubPane.add(new Label("|"), 1, 0);
        B50SubPane.add(B10AVGText, 2, 0);
        B50SubPane.add(new Label("B30 평균"), 0, 1);
        B50SubPane.add(new Label("|"), 1, 1);
        B50SubPane.add(B30AVGText, 2, 1);
        B50SubPane.add(new Label("B50 평균"), 0, 2);
        B50SubPane.add(new Label("|"), 1, 2);
        B50SubPane.add(B50AVGText, 2, 2);
        B50MainControlPane.add(B50SubPane, 1, 3);
        
        B50Main.add(B50MainControlPane, 6, 5);
        B50Main.add(B50MainTable, 5, 5);
        B50Main.add(new Label(""), 5, 7);
        
        return B50Main;
    }

    /* ------------------------------------------------------------------------------------------------------------- */
    /*                                                    B50Input                                                   */
    /* ------------------------------------------------------------------------------------------------------------- */

    private Songs B50song;

    private String BYDorINS = "Beyond";

    private TableColumn<B50SongInformation, String> B50TitleCol = new TableColumn<>("제목");
    private TableColumn<B50SongInformation, String> B50DiffCol = new TableColumn<>("ETR/BYD");

    private TableView<B50SongInformation> B50SearchResultTable = new TableView<>();

    private TextField B50SearchField = new TextField();

    public class ScoreData {
        private Songs Song;

        private double Index;

        private String Title;
        private String Difficulty; private String Level;

        private String Artist;

        private int Score;
        private String ClearStatus;
        private double Potential;

        public ScoreData() {}

        public ScoreData(Songs Song, double Index, String Title, String Difficulty, String Level, String Artist, int Score, String ClearStatus, double Potential) {
            this.Song = Song;

            this.Index = Index;

            this.Title = Title;
            this.Difficulty = Difficulty; this.Level = Level;

            this.Artist = Artist;

            this.Score = Score;
            this.ClearStatus = ClearStatus;
            this.Potential = Potential;
        }

        Songs getSong() {
            return Song;
        }
        double getIndex() {
            return Index;
        }
        String getTitle() {
            return Title;
        }
        String getDifficulty() {
            return Difficulty;
        }
        String getLevel() {
            return Level;
        }
        String getArtist() {
            return Artist;
        }
        String getGrade() {
            if (getScore() < 8600000) {
                return "D";
            }
            else if (8600000 <= getScore() && getScore() < 8900000) {
                return "C";
            }
            else if (8900000 <= getScore() && getScore() < 9200000) {
                return "B";
            }
            else if (9200000 <= getScore() && getScore() < 9500000) {
                return "A";
            }
            else if (9500000 <= getScore() && getScore() < 9800000) {
                return "AA";
            }
            else if (9800000 <= getScore() && getScore() < 9900000) {
                return "EX";
            }
            else if (9900000 <= getScore()) {
                return "EX+";
            }
            else {
                return "Err";
            }
        }
        int getScore() {
            return Score;
        }
        String getClearStatus() {
            return ClearStatus;
        }
        String getShortClearStatus() {
            if (ClearStatus.equals("Pure Memory")) {
                return "P";
            }
            else if (ClearStatus.equals("Full Recall")) {
                return "F";
            }
            else if (ClearStatus.equals("Track Complete")) {
                return "C";
            }
            else if (ClearStatus.equals("Track Lost")) {
                return "L";
            }
            else {
                return "Err";
            }
        }
        double getPotential() {
            return Potential;
        }

        void setScore(int newScore) {
            this.Score = newScore;
        }
        void setClearStatus(String newClearStatus) {
            this.ClearStatus = newClearStatus;
        }
        void setPotential(double newPotential) {
            this.Potential = newPotential;
        }
    }

    public void B50jsonMaker(File Location) {
        File parentDir = Location.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(Location)) {
            try (FileWriter writer = new FileWriter(Location)) {
                List<ScoreData> EmptySD = new ArrayList<>();
                B50Builder.toJson(EmptySD, writer);
                //System.out.println("파일 생성 성공");
            }
            catch (IOException f) {}
        }
        catch (IOException e) {
            e.printStackTrace();
            //System.out.println("파일 생성 실패");
        }
    }

    public void B50jsonAdder(ScoreData SD, List<ScoreData> List, File Location) {
        List<ScoreData> SDList = List;
    
        SDList.add(SD);
    
        try (FileWriter writer = new FileWriter(Location)) {
            B50Builder.toJson(SDList, writer);
        }
        catch (Exception WriteFail) {
            WriteFail.printStackTrace();
        }
    }

    public void B50jsonSetter(List<ScoreData> List, ScoreData SD, File Location, int DiffIndex) {
        boolean Changed = false;
        if (Location.exists() && Location.length() > 0) {
            try (FileReader reader = new FileReader(Location)) {
                Type B50ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        
                List<ScoreData> SDList = B50Builder.fromJson(reader, B50ListType);
                if (SDList != null) {
                    List = new ArrayList<>(SDList);
                    //System.out.println(List.size());
                }
                for (ScoreData S : SDList) {
                    if (S.getTitle().equals(B50song.getTitle(DiffIndex)) && S.getDifficulty().equals(B50song.getDiff(DiffIndex))) {
                        List.remove(S);
                        Changed = true;
                        B50jsonAdder(SD, List, Location);
                        //System.out.println("수정");
                    }
                    //System.out.println(S.getTitle() + " " + S.getDifficulty() + " " + S.getLevel() + " | " + S.getScore() + " " + S.getPotential());
                }
                if (Changed == false) {
                    B50jsonAdder(SD, List, Location);
                }
            }
            catch (IOException IOE) {}
        }
        else if (Location.exists() && Location.length() == 0) {
            //System.out.println("json 파일이 비어 있음");
            B50jsonStarter(SD);
        }
        else if (!Location.exists()) {
            //System.out.println("파일이 존재하지 않음");
            B50jsonMaker(Location);
            B50jsonStarter(SD);
        }
    }

    public void B50jsonRemover(List<ScoreData> List, File Location, int DiffIndex) {
        boolean Changed = false;
        if (Location.exists() && Location.length() > 0) {
            try (FileReader reader = new FileReader(Location)) {
                Type B50ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        
                List<ScoreData> SDList = B50Builder.fromJson(reader, B50ListType);
                if (SDList != null) {
                    List = new ArrayList<>(SDList);
                    //System.out.println(List.size());
                }
                for (ScoreData S : SDList) {
                    if (S.getTitle().equals(B50song.getTitle(DiffIndex)) && S.getDifficulty().equals(B50song.getDiff(DiffIndex))) {
                        List.remove(S);
                        Changed = true;
                        try (FileWriter writer = new FileWriter(Location)) {
                            B50Builder.toJson(List, writer);
                        }
                        catch (Exception WriteFail) {
                            WriteFail.printStackTrace();
                        }
                    }
                    //System.out.println(S.getTitle() + " " + S.getDifficulty() + " " + S.getLevel() + " | " + S.getScore() + " " + S.getPotential());
                }
                if (!Changed) {
                    showErrorMessage("기록 확인", "삭제할 대상이 존재하지 않습니다.", "확인", 300, 105);
                }
            }
            catch (IOException IOE) {}
        }
        else if (Location.exists() && Location.length() == 0) {
            //System.out.println("json 파일이 비어 있음");
            B50jsonStarter(null);
        }
        else if (!Location.exists()) {
            //System.out.println("파일이 존재하지 않음");
            B50jsonMaker(Location);
            B50jsonStarter(null);
        }
    }

    public void B50jsonStarter(ScoreData SD) {
        try (FileWriter writer = new FileWriter(B50Location)) {
            List<ScoreData> EmptySD = new ArrayList<>();
            if (SD != null) {
                EmptySD.add(SD);
            }
            B50Builder.toJson(EmptySD, writer);
        }
        catch (Exception WriteFail) {
            WriteFail.printStackTrace();
        }
    }

    public GridPane B50Input() {
        PageIndex = 31;

        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        GridPane B50InputPane1 = new GridPane();
        B50InputPane1.setHgap(10);
        B50InputPane1.setVgap(10);

        CheckMenuItem B50SelPMPSTA = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMPRSA = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMFTRA = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMPSTB = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMPRSB = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMFTRB = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMPSTC = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMPRSC = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMFTRC = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMETR = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMBYD = new CheckMenuItem("Pure Memory");
        CheckMenuItem B50SelPMLast = new CheckMenuItem("Pure Memory");

        CheckMenuItem B50SelFRPSTA = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRPRSA = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRFTRA = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRPSTB = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRPRSB = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRFTRB = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRPSTC = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRPRSC = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRFTRC = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRETR = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRBYD = new CheckMenuItem("Full Recall");
        CheckMenuItem B50SelFRLast = new CheckMenuItem("Full Recall");

        CheckMenuItem B50SelTCPSTA = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCPRSA = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCFTRA = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCPSTB = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCPRSB = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCFTRB = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCPSTC = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCPRSC = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCFTRC = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCETR = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCBYD = new CheckMenuItem("Track Complete");
        CheckMenuItem B50SelTCLast = new CheckMenuItem("Track Complete");

        CheckMenuItem B50SelTLPSTA = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLPRSA = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLFTRA = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLPSTB = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLPRSB = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLFTRB = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLPSTC = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLPRSC = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLFTRC = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLETR = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLBYD = new CheckMenuItem("Track Lost");
        CheckMenuItem B50SelTLLast = new CheckMenuItem("Track Lost");
        
        TextField ScoreMatchPST_A = new TextField();
        TextField ScoreMatchPST_B = new TextField();
        TextField ScoreMatchPST_C = new TextField();
        TextField ScoreMatchPRS_A = new TextField();
        TextField ScoreMatchPRS_B = new TextField();
        TextField ScoreMatchPRS_C = new TextField();
        TextField ScoreMatchFTR_A = new TextField();
        TextField ScoreMatchFTR_B = new TextField();
        TextField ScoreMatchFTR_C = new TextField();
        TextField ScoreMatchETR = new TextField();
        TextField ScoreMatchBYD = new TextField();
        TextField ScoreMatchLast = new TextField();
        ScoreMatchPST_A.setPrefWidth(100);
        ScoreMatchPST_B.setPrefWidth(100);
        ScoreMatchPST_C.setPrefWidth(100);
        ScoreMatchPRS_A.setPrefWidth(100);
        ScoreMatchPRS_B.setPrefWidth(100);
        ScoreMatchPRS_C.setPrefWidth(100);
        ScoreMatchFTR_A.setPrefWidth(100);
        ScoreMatchFTR_B.setPrefWidth(100);
        ScoreMatchFTR_C.setPrefWidth(100);
        ScoreMatchETR.setPrefWidth(100);
        ScoreMatchBYD.setPrefWidth(100);
        ScoreMatchLast.setPrefWidth(100);

        int CCWidth = 50;
        
        MenuButton ClearCheckPST_A = new MenuButton();
        ClearCheckPST_A.getItems().clear();
        ClearCheckPST_A.getItems().addAll(B50SelPMPSTA, B50SelFRPSTA, B50SelTCPSTA, B50SelTLPSTA);
        ClearCheckPST_A.setPrefWidth(CCWidth);
        B50SelTCPSTA.setSelected(true);
        MenuButton ClearCheckPST_B = new MenuButton();
        ClearCheckPST_B.getItems().clear();
        ClearCheckPST_B.getItems().addAll(B50SelPMPSTB, B50SelFRPSTB, B50SelTCPSTB, B50SelTLPSTB);
        ClearCheckPST_B.setPrefWidth(CCWidth);
        B50SelTCPSTB.setSelected(true);
        MenuButton ClearCheckPST_C = new MenuButton();
        ClearCheckPST_C.getItems().clear();
        ClearCheckPST_C.getItems().addAll(B50SelPMPSTC, B50SelFRPSTC, B50SelTCPSTC, B50SelTLPSTC);
        ClearCheckPST_C.setPrefWidth(CCWidth);
        B50SelTCPSTC.setSelected(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        MenuButton ClearCheckPRS_A = new MenuButton();
        ClearCheckPRS_A.getItems().clear();
        ClearCheckPRS_A.getItems().addAll(B50SelPMPRSA, B50SelFRPRSA, B50SelTCPRSA, B50SelTLPRSA);
        ClearCheckPRS_A.setPrefWidth(CCWidth);
        B50SelTCPRSA.setSelected(true);
        MenuButton ClearCheckPRS_B = new MenuButton();
        ClearCheckPRS_B.getItems().clear();
        ClearCheckPRS_B.getItems().addAll(B50SelPMPRSB, B50SelFRPRSB, B50SelTCPRSB, B50SelTLPRSB);
        ClearCheckPRS_B.setPrefWidth(CCWidth);
        B50SelTCPRSB.setSelected(true);
        MenuButton ClearCheckPRS_C = new MenuButton();
        ClearCheckPRS_C.getItems().clear();
        ClearCheckPRS_C.getItems().addAll(B50SelPMPRSC, B50SelFRPRSC, B50SelTCPRSC, B50SelTLPRSC);
        ClearCheckPRS_C.setPrefWidth(CCWidth);
        B50SelTCPRSC.setSelected(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        MenuButton ClearCheckFTR_A = new MenuButton();
        ClearCheckFTR_A.getItems().clear();
        ClearCheckFTR_A.getItems().addAll(B50SelPMFTRA, B50SelFRFTRA, B50SelTCFTRA, B50SelTLFTRA);
        ClearCheckFTR_A.setPrefWidth(CCWidth);
        B50SelTCFTRA.setSelected(true);
        MenuButton ClearCheckFTR_B = new MenuButton();
        ClearCheckFTR_B.getItems().clear();
        ClearCheckFTR_B.getItems().addAll(B50SelPMFTRB, B50SelFRFTRB, B50SelTCFTRB, B50SelTLFTRB);
        ClearCheckFTR_B.setPrefWidth(CCWidth);
        B50SelTCFTRB.setSelected(true);
        MenuButton ClearCheckFTR_C = new MenuButton();
        ClearCheckFTR_C.getItems().clear();
        ClearCheckFTR_C.getItems().addAll(B50SelPMFTRC, B50SelFRFTRC, B50SelTCFTRC, B50SelTLFTRC);
        ClearCheckFTR_C.setPrefWidth(CCWidth);
        B50SelTCFTRC.setSelected(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        MenuButton ClearCheckETR = new MenuButton();
        ClearCheckETR.getItems().clear();
        ClearCheckETR.getItems().addAll(B50SelPMETR, B50SelFRETR, B50SelTCETR, B50SelTLETR);
        ClearCheckETR.setPrefWidth(CCWidth);
        B50SelTCETR.setSelected(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        MenuButton ClearCheckBYD = new MenuButton();
        ClearCheckBYD.getItems().clear();
        ClearCheckBYD.getItems().addAll(B50SelPMBYD, B50SelFRBYD, B50SelTCBYD, B50SelTLBYD);
        ClearCheckBYD.setPrefWidth(CCWidth);
        B50SelTCBYD.setSelected(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        MenuButton ClearCheckLast = new MenuButton();
        ClearCheckLast.getItems().clear();
        ClearCheckLast.getItems().addAll(B50SelPMLast, B50SelFRLast, B50SelTCLast, B50SelTLLast);
        ClearCheckLast.setPrefWidth(CCWidth);
        B50SelTCLast.setSelected(true);
        
        if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("PSTClearStatus", "Pure Memory");
            B50SelPMPSTA.setSelected(true);
            B50SelFRPSTA.setSelected(false);
            B50SelTCPSTA.setSelected(false);
            B50SelTLPSTA.setSelected(false);
            ClearCheckPST_A.setText("P");
            B50SelPMPSTB.setSelected(true);
            B50SelFRPSTB.setSelected(false);
            B50SelTCPSTB.setSelected(false);
            B50SelTLPSTB.setSelected(false);
            ClearCheckPST_B.setText("P");
            B50SelPMPSTC.setSelected(true);
            B50SelFRPSTC.setSelected(false);
            B50SelTCPSTC.setSelected(false);
            B50SelTLPSTC.setSelected(false);
            ClearCheckPST_C.setText("P");
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("PSTClearStatus", "Full Recall");
            B50SelPMPSTA.setSelected(false);
            B50SelFRPSTA.setSelected(true);
            B50SelTCPSTA.setSelected(false);
            B50SelTLPSTA.setSelected(false);
            ClearCheckPST_A.setText("F");
            B50SelPMPSTB.setSelected(false);
            B50SelFRPSTB.setSelected(true);
            B50SelTCPSTB.setSelected(false);
            B50SelTLPSTB.setSelected(false);
            ClearCheckPST_B.setText("F");
            B50SelPMPSTC.setSelected(false);
            B50SelFRPSTC.setSelected(true);
            B50SelTCPSTC.setSelected(false);
            B50SelTLPSTC.setSelected(false);
            ClearCheckPST_C.setText("F");
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("PSTClearStatus", "Track Complete");
            B50SelPMPSTA.setSelected(false);
            B50SelFRPSTA.setSelected(false);
            B50SelTCPSTA.setSelected(true);
            B50SelTLPSTA.setSelected(false);
            ClearCheckPST_A.setText("C");
            B50SelPMPSTB.setSelected(false);
            B50SelFRPSTB.setSelected(false);
            B50SelTCPSTB.setSelected(true);
            B50SelTLPSTB.setSelected(false);
            ClearCheckPST_B.setText("C");
            B50SelPMPSTC.setSelected(false);
            B50SelFRPSTC.setSelected(false);
            B50SelTCPSTC.setSelected(true);
            B50SelTLPSTC.setSelected(false);
            ClearCheckPST_C.setText("C");
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("PSTClearStatus", "Track Lost");
            B50SelPMPSTA.setSelected(false);
            B50SelFRPSTA.setSelected(false);
            B50SelTCPSTA.setSelected(false);
            B50SelTLPSTA.setSelected(true);
            ClearCheckPST_A.setText("L");
            B50SelPMPSTB.setSelected(false);
            B50SelFRPSTB.setSelected(false);
            B50SelTCPSTB.setSelected(false);
            B50SelTLPSTB.setSelected(true);
            ClearCheckPST_B.setText("L");
            B50SelPMPSTC.setSelected(false);
            B50SelFRPSTC.setSelected(false);
            B50SelTCPSTC.setSelected(false);
            B50SelTLPSTC.setSelected(true);
            ClearCheckPST_C.setText("L");
        }
        
        if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("PRSClearStatus", "Pure Memory");
            B50SelPMPRSA.setSelected(true);
            B50SelFRPRSA.setSelected(false);
            B50SelTCPRSA.setSelected(false);
            B50SelTLPRSA.setSelected(false);
            ClearCheckPRS_A.setText("P");
            B50SelPMPRSB.setSelected(true);
            B50SelFRPRSB.setSelected(false);
            B50SelTCPRSB.setSelected(false);
            B50SelTLPRSB.setSelected(false);
            ClearCheckPRS_B.setText("P");
            B50SelPMPRSC.setSelected(true);
            B50SelFRPRSC.setSelected(false);
            B50SelTCPRSC.setSelected(false);
            B50SelTLPRSC.setSelected(false);
            ClearCheckPRS_C.setText("P");
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("PRSClearStatus", "Full Recall");
            B50SelPMPRSA.setSelected(false);
            B50SelFRPRSA.setSelected(true);
            B50SelTCPRSA.setSelected(false);
            B50SelTLPRSA.setSelected(false);
            ClearCheckPRS_A.setText("F");
            B50SelPMPRSB.setSelected(false);
            B50SelFRPRSB.setSelected(true);
            B50SelTCPRSB.setSelected(false);
            B50SelTLPRSB.setSelected(false);
            ClearCheckPRS_B.setText("F");
            B50SelPMPRSC.setSelected(false);
            B50SelFRPRSC.setSelected(true);
            B50SelTCPRSC.setSelected(false);
            B50SelTLPRSC.setSelected(false);
            ClearCheckPRS_C.setText("F");
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("PRSClearStatus", "Track Complete");
            B50SelPMPRSA.setSelected(false);
            B50SelFRPRSA.setSelected(false);
            B50SelTCPRSA.setSelected(true);
            B50SelTLPRSA.setSelected(false);
            ClearCheckPRS_A.setText("C");
            B50SelPMPRSB.setSelected(false);
            B50SelFRPRSB.setSelected(false);
            B50SelTCPRSB.setSelected(true);
            B50SelTLPRSB.setSelected(false);
            ClearCheckPRS_B.setText("C");
            B50SelPMPRSC.setSelected(false);
            B50SelFRPRSC.setSelected(false);
            B50SelTCPRSC.setSelected(true);
            B50SelTLPRSC.setSelected(false);
            ClearCheckPRS_C.setText("C");
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("PRSClearStatus", "Track Lost");
            B50SelPMPRSA.setSelected(false);
            B50SelFRPRSA.setSelected(false);
            B50SelTCPRSA.setSelected(false);
            B50SelTLPRSA.setSelected(true);
            ClearCheckPRS_A.setText("L");
            B50SelPMPRSB.setSelected(false);
            B50SelFRPRSB.setSelected(false);
            B50SelTCPRSB.setSelected(false);
            B50SelTLPRSB.setSelected(true);
            ClearCheckPRS_B.setText("L");
            B50SelPMPRSC.setSelected(false);
            B50SelFRPRSC.setSelected(false);
            B50SelTCPRSC.setSelected(false);
            B50SelTLPRSC.setSelected(true);
            ClearCheckPRS_C.setText("L");
        }
        
        if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("FTRClearStatus", "Pure Memory");
            B50SelPMFTRA.setSelected(true);
            B50SelFRFTRA.setSelected(false);
            B50SelTCFTRA.setSelected(false);
            B50SelTLFTRA.setSelected(false);
            ClearCheckFTR_A.setText("P");
            B50SelPMFTRB.setSelected(true);
            B50SelFRFTRB.setSelected(false);
            B50SelTCFTRB.setSelected(false);
            B50SelTLFTRB.setSelected(false);
            ClearCheckFTR_B.setText("P");
            B50SelPMFTRC.setSelected(true);
            B50SelFRFTRC.setSelected(false);
            B50SelTCFTRC.setSelected(false);
            B50SelTLFTRC.setSelected(false);
            ClearCheckFTR_C.setText("P");
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("FTRClearStatus", "Full Recall");
            B50SelPMFTRA.setSelected(false);
            B50SelFRFTRA.setSelected(true);
            B50SelTCFTRA.setSelected(false);
            B50SelTLFTRA.setSelected(false);
            ClearCheckFTR_A.setText("F");
            B50SelPMFTRB.setSelected(false);
            B50SelFRFTRB.setSelected(true);
            B50SelTCFTRB.setSelected(false);
            B50SelTLFTRB.setSelected(false);
            ClearCheckFTR_B.setText("F");
            B50SelPMFTRC.setSelected(false);
            B50SelFRFTRC.setSelected(true);
            B50SelTCFTRC.setSelected(false);
            B50SelTLFTRC.setSelected(false);
            ClearCheckFTR_C.setText("F");
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("FTRClearStatus", "Track Complete");
            B50SelPMFTRA.setSelected(false);
            B50SelFRFTRA.setSelected(false);
            B50SelTCFTRA.setSelected(true);
            B50SelTLFTRA.setSelected(false);
            ClearCheckFTR_A.setText("C");
            B50SelPMFTRB.setSelected(false);
            B50SelFRFTRB.setSelected(false);
            B50SelTCFTRB.setSelected(true);
            B50SelTLFTRB.setSelected(false);
            ClearCheckFTR_B.setText("C");
            B50SelPMFTRC.setSelected(false);
            B50SelFRFTRC.setSelected(false);
            B50SelTCFTRC.setSelected(true);
            B50SelTLFTRC.setSelected(false);
            ClearCheckFTR_C.setText("C");
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("FTRClearStatus", "Track Lost");
            B50SelPMFTRA.setSelected(false);
            B50SelFRFTRA.setSelected(false);
            B50SelTCFTRA.setSelected(false);
            B50SelTLFTRA.setSelected(true);
            ClearCheckFTR_A.setText("L");
            B50SelPMFTRB.setSelected(false);
            B50SelFRFTRB.setSelected(false);
            B50SelTCFTRB.setSelected(false);
            B50SelTLFTRB.setSelected(true);
            ClearCheckFTR_B.setText("L");
            B50SelPMFTRC.setSelected(false);
            B50SelFRFTRC.setSelected(false);
            B50SelTCFTRC.setSelected(false);
            B50SelTLFTRC.setSelected(true);
            ClearCheckFTR_C.setText("L");
        }
        
        if (settings.get("ETRClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("ETRClearStatus", "Pure Memory");
            B50SelPMETR.setSelected(true);
            B50SelFRETR.setSelected(false);
            B50SelTCETR.setSelected(false);
            B50SelTLETR.setSelected(false);
            ClearCheckETR.setText("P");
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("ETRClearStatus", "Full Recall");
            B50SelPMETR.setSelected(false);
            B50SelFRETR.setSelected(true);
            B50SelTCETR.setSelected(false);
            B50SelTLETR.setSelected(false);
            ClearCheckETR.setText("F");
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("ETRClearStatus", "Track Complete");
            B50SelPMETR.setSelected(false);
            B50SelFRETR.setSelected(false);
            B50SelTCETR.setSelected(true);
            B50SelTLETR.setSelected(false);
            ClearCheckETR.setText("C");
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("ETRClearStatus", "Track Lost");
            B50SelPMETR.setSelected(false);
            B50SelFRETR.setSelected(false);
            B50SelTCETR.setSelected(false);
            B50SelTLETR.setSelected(true);
            ClearCheckETR.setText("L");
        }
        
        if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("BYDClearStatus", "Pure Memory");
            B50SelPMBYD.setSelected(true);
            B50SelFRBYD.setSelected(false);
            B50SelTCBYD.setSelected(false);
            B50SelTLBYD.setSelected(false);
            ClearCheckBYD.setText("P");
            B50SelPMLast.setSelected(true);
            B50SelFRLast.setSelected(false);
            B50SelTCLast.setSelected(false);
            B50SelTLLast.setSelected(false);
            ClearCheckLast.setText("P");
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("BYDClearStatus", "Full Recall");
            B50SelPMBYD.setSelected(false);
            B50SelFRBYD.setSelected(true);
            B50SelTCBYD.setSelected(false);
            B50SelTLBYD.setSelected(false);
            ClearCheckBYD.setText("F");
            B50SelPMLast.setSelected(false);
            B50SelFRLast.setSelected(true);
            B50SelTCLast.setSelected(false);
            B50SelTLLast.setSelected(false);
            ClearCheckLast.setText("F");
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("BYDClearStatus", "Track Complete");
            B50SelPMBYD.setSelected(false);
            B50SelFRBYD.setSelected(false);
            B50SelTCBYD.setSelected(true);
            B50SelTLBYD.setSelected(false);
            ClearCheckBYD.setText("C");
            B50SelPMLast.setSelected(false);
            B50SelFRLast.setSelected(false);
            B50SelTCLast.setSelected(true);
            B50SelTLLast.setSelected(false);
            ClearCheckLast.setText("C");
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("BYDClearStatus", "Track Lost");
            B50SelPMBYD.setSelected(false);
            B50SelFRBYD.setSelected(false);
            B50SelTCBYD.setSelected(false);
            B50SelTLBYD.setSelected(true);
            ClearCheckBYD.setText("L");
            B50SelPMLast.setSelected(false);
            B50SelFRLast.setSelected(false);
            B50SelTCLast.setSelected(false);
            B50SelTLLast.setSelected(true);
            ClearCheckLast.setText("L");
        }
        
        B50SelPMPSTA.setOnAction(a -> {
            if (B50SelPMPSTA.isSelected()) {
                B50SelPMPSTA.setSelected(true);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("P");
            }
        });
        B50SelPMPSTB.setOnAction(a -> {
            if (B50SelPMPSTB.isSelected()) {
                B50SelPMPSTB.setSelected(true);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("P");
            }
        });
        B50SelPMPSTC.setOnAction(a -> {
            if (B50SelPMPSTC.isSelected()) {
                B50SelPMPSTC.setSelected(true);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("P");
            }
        });

        B50SelPMPRSA.setOnAction(a -> {
            if (B50SelPMPRSA.isSelected()) {
                B50SelPMPRSA.setSelected(true);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("P");
            }
        });
        B50SelPMPRSB.setOnAction(a -> {
            if (B50SelPMPRSB.isSelected()) {
                B50SelPMPRSB.setSelected(true);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("P");
            }
        });
        B50SelPMPRSC.setOnAction(a -> {
            if (B50SelPMPRSC.isSelected()) {
                B50SelPMPRSC.setSelected(true);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("P");
            }
        });

        B50SelPMFTRA.setOnAction(a -> {
            if (B50SelPMFTRA.isSelected()) {
                B50SelPMFTRA.setSelected(true);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("P");
            }
        });
        B50SelPMFTRB.setOnAction(a -> {
            if (B50SelPMFTRB.isSelected()) {
                B50SelPMFTRB.setSelected(true);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("P");
            }
        });
        B50SelPMFTRC.setOnAction(a -> {
            if (B50SelPMFTRC.isSelected()) {
                B50SelPMFTRC.setSelected(true);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("P");
            }
        });

        B50SelPMETR.setOnAction(a -> {
            if (B50SelPMETR.isSelected()) {
                B50SelPMETR.setSelected(true);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("P");
            }
        });

        B50SelPMBYD.setOnAction(a -> {
            if (B50SelPMBYD.isSelected()) {
                B50SelPMBYD.setSelected(true);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("P");
            }
        });

        B50SelPMLast.setOnAction(a -> {
            if (B50SelPMLast.isSelected()) {
                B50SelPMLast.setSelected(true);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("P");
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        B50SelFRPSTA.setOnAction(a -> {
            if (B50SelFRPSTA.isSelected()) {
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(true);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("F");
            }
        });
        B50SelFRPSTB.setOnAction(a -> {
            if (B50SelFRPSTB.isSelected()) {
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(true);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("F");
            }
        });
        B50SelFRPSTC.setOnAction(a -> {
            if (B50SelFRPSTC.isSelected()) {
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(true);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("F");
            }
        });

        B50SelFRPRSA.setOnAction(a -> {
            if (B50SelFRPRSA.isSelected()) {
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(true);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("F");
            }
        });
        B50SelFRPRSB.setOnAction(a -> {
            if (B50SelFRPRSB.isSelected()) {
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(true);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("F");
            }
        });
        B50SelFRPRSC.setOnAction(a -> {
            if (B50SelFRPRSC.isSelected()) {
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(true);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("F");
            }
        });

        B50SelFRFTRA.setOnAction(a -> {
            if (B50SelFRFTRA.isSelected()) {
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(true);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("F");
            }
        });
        B50SelFRFTRB.setOnAction(a -> {
            if (B50SelFRFTRB.isSelected()) {
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(true);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("F");
            }
        });
        B50SelFRFTRC.setOnAction(a -> {
            if (B50SelFRFTRC.isSelected()) {
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(true);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("F");
            }
        });

        B50SelFRETR.setOnAction(a -> {
            if (B50SelFRETR.isSelected()) {
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(true);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("F");
            }
        });

        B50SelFRBYD.setOnAction(a -> {
            if (B50SelFRBYD.isSelected()) {
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(true);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("F");
            }
        });

        B50SelFRLast.setOnAction(a -> {
            if (B50SelFRLast.isSelected()) {
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(true);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("F");
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        B50SelTCPSTA.setOnAction(a -> {
            if (B50SelTCPSTA.isSelected()) {
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(true);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("C");
            }
        });
        B50SelTCPSTB.setOnAction(a -> {
            if (B50SelTCPSTB.isSelected()) {
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(true);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("C");
            }
        });
        B50SelTCPSTC.setOnAction(a -> {
            if (B50SelTCPSTC.isSelected()) {
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(true);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("C");
            }
        });

        B50SelTCPRSA.setOnAction(a -> {
            if (B50SelTCPRSA.isSelected()) {
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(true);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("C");
            }
        });
        B50SelTCPRSB.setOnAction(a -> {
            if (B50SelTCPRSB.isSelected()) {
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(true);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("C");
            }
        });
        B50SelTCPRSC.setOnAction(a -> {
            if (B50SelTCPRSC.isSelected()) {
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(true);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("C");
            }
        });

        B50SelTCFTRA.setOnAction(a -> {
            if (B50SelTCFTRA.isSelected()) {
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(true);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("C");
            }
        });
        B50SelTCFTRB.setOnAction(a -> {
            if (B50SelTCFTRB.isSelected()) {
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(true);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("C");
            }
        });
        B50SelTCFTRC.setOnAction(a -> {
            if (B50SelTCFTRC.isSelected()) {
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(true);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("C");
            }
        });

        B50SelTCETR.setOnAction(a -> {
            if (B50SelTCETR.isSelected()) {
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(true);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("C");
            }
        });

        B50SelTCBYD.setOnAction(a -> {
            if (B50SelTCBYD.isSelected()) {
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(true);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("C");
            }
        });

        B50SelTCLast.setOnAction(a -> {
            if (B50SelTCLast.isSelected()) {
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(true);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("C");
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        B50SelTLPSTA.setOnAction(a -> {
            if (B50SelTLPSTA.isSelected()) {
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(true);
                ClearCheckPST_A.setText("L");
            }
        });
        B50SelTLPSTB.setOnAction(a -> {
            if (B50SelTLPSTB.isSelected()) {
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(true);
                ClearCheckPST_B.setText("L");
            }
        });
        B50SelTLPSTC.setOnAction(a -> {
            if (B50SelTLPSTC.isSelected()) {
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(true);
                ClearCheckPST_C.setText("L");
            }
        });

        B50SelTLPRSA.setOnAction(a -> {
            if (B50SelTLPRSA.isSelected()) {
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(true);
                ClearCheckPRS_A.setText("L");
            }
        });
        B50SelTLPRSB.setOnAction(a -> {
            if (B50SelTLPRSB.isSelected()) {
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(true);
                ClearCheckPRS_B.setText("L");
            }
        });
        B50SelTLPRSC.setOnAction(a -> {
            if (B50SelTLPRSC.isSelected()) {
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(true);
                ClearCheckPRS_C.setText("L");
            }
        });

        B50SelTLFTRA.setOnAction(a -> {
            if (B50SelTLFTRA.isSelected()) {
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(true);
                ClearCheckFTR_A.setText("L");
            }
        });
        B50SelTLFTRB.setOnAction(a -> {
            if (B50SelTLFTRB.isSelected()) {
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(true);
                ClearCheckFTR_B.setText("L");
            }
        });
        B50SelTLFTRC.setOnAction(a -> {
            if (B50SelTLFTRC.isSelected()) {
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(true);
                ClearCheckFTR_C.setText("L");
            }
        });

        B50SelTLETR.setOnAction(a -> {
            if (B50SelTLETR.isSelected()) {
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(true);
                ClearCheckETR.setText("L");
            }
        });

        B50SelTLBYD.setOnAction(a -> {
            if (B50SelTLBYD.isSelected()) {
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(true);
                ClearCheckBYD.setText("L");
            }
        });

        B50SelTLLast.setOnAction(a -> {
            if (B50SelTLLast.isSelected()) {
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(true);
                ClearCheckLast.setText("L");
            }
        });
        
        Button ApplyButtonPST_A = new Button("저장");
        ApplyButtonPST_A.getStyleClass().add("Buttons");
        ApplyButtonPST_A.setOnAction(PSAA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPSTA.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPSTA.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPSTA.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPSTA.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPST_A.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(0)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(0), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(0), B50song.getTitle(0), B50song.getDiff(0), B50song.getLevel(0), B50song.getArtist(0), Score, ClearStatus, Potential), B50Location, 0);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPST_A = new Button("삭제");
        DeleteButtonPST_A.getStyleClass().add("Buttons");
        DeleteButtonPST_A.setOnAction(PSDA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 0);
            ScoreMatchPST_A.clear();
            if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("PSTClearStatus", "Pure Memory");
                B50SelPMPSTA.setSelected(true);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("P");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PSTClearStatus", "Full Recall");
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(true);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("F");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PSTClearStatus", "Track Complete");
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(true);
                B50SelTLPSTA.setSelected(false);
                ClearCheckPST_A.setText("C");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PSTClearStatus", "Track Lost");
                B50SelPMPSTA.setSelected(false);
                B50SelFRPSTA.setSelected(false);
                B50SelTCPSTA.setSelected(false);
                B50SelTLPSTA.setSelected(true);
                ClearCheckPST_A.setText("L");
            }
        });
        Button ApplyButtonPST_B = new Button("저장");
        ApplyButtonPST_B.getStyleClass().add("Buttons");
        ApplyButtonPST_B.setOnAction(PSAB -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPSTB.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPSTB.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPSTB.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPSTB.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPST_B.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(0)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(0), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(0), B50song.getTitle(0), B50song.getDiff(0), B50song.getLevel(0), B50song.getArtist(0), Score, ClearStatus, Potential), B50Location, 0);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPST_B = new Button("삭제");
        DeleteButtonPST_B.getStyleClass().add("Buttons");
        DeleteButtonPST_B.setOnAction(PSDB -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 0);
            ScoreMatchPST_B.clear();
            if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("PSTClearStatus", "Pure Memory");
                B50SelPMPSTB.setSelected(true);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("P");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PSTClearStatus", "Full Recall");
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(true);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("F");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PSTClearStatus", "Track Complete");
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(true);
                B50SelTLPSTB.setSelected(false);
                ClearCheckPST_B.setText("C");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PSTClearStatus", "Track Lost");
                B50SelPMPSTB.setSelected(false);
                B50SelFRPSTB.setSelected(false);
                B50SelTCPSTB.setSelected(false);
                B50SelTLPSTB.setSelected(true);
                ClearCheckPST_B.setText("L");
            }
        });
        Button ApplyButtonPST_C = new Button("저장");
        ApplyButtonPST_C.getStyleClass().add("Buttons");
        ApplyButtonPST_C.setOnAction(PSAC -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPSTC.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPSTC.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPSTC.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPSTC.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPST_C.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(0)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(0), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(0), B50song.getTitle(0), B50song.getDiff(0), B50song.getLevel(0), B50song.getArtist(0), Score, ClearStatus, Potential), B50Location, 0);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(0)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPST_C = new Button("삭제");
        DeleteButtonPST_C.getStyleClass().add("Buttons");
        DeleteButtonPST_C.setOnAction(PSDC -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 0);
            ScoreMatchPST_C.clear();
            if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("PSTClearStatus", "Pure Memory");
                B50SelPMPSTC.setSelected(true);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("P");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PSTClearStatus", "Full Recall");
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(true);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("F");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PSTClearStatus", "Track Complete");
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(true);
                B50SelTLPSTC.setSelected(false);
                ClearCheckPST_C.setText("C");
            }
            if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PSTClearStatus", "Track Lost");
                B50SelPMPSTC.setSelected(false);
                B50SelFRPSTC.setSelected(false);
                B50SelTCPSTC.setSelected(false);
                B50SelTLPSTC.setSelected(true);
                ClearCheckPST_C.setText("L");
            }
        });

        Button ApplyButtonPRS_A = new Button("저장");
        ApplyButtonPRS_A.getStyleClass().add("Buttons");
        ApplyButtonPRS_A.setOnAction(PRAA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPRSA.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPRSA.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPRSA.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPRSA.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPRS_A.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(1)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(1), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(1), B50song.getTitle(1), B50song.getDiff(1), B50song.getLevel(1), B50song.getArtist(1), Score, ClearStatus, Potential), B50Location, 1);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPRS_A = new Button("삭제");
        DeleteButtonPRS_A.getStyleClass().add("Buttons");
        DeleteButtonPRS_A.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 1);
            ScoreMatchPRS_A.clear();
            if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("PRSClearStatus", "Pure Memory");
                B50SelPMPRSA.setSelected(true);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("P");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PRSClearStatus", "Full Recall");
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(true);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("F");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PRSClearStatus", "Track Complete");
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(true);
                B50SelTLPRSA.setSelected(false);
                ClearCheckPRS_A.setText("C");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PRSClearStatus", "Track Lost");
                B50SelPMPRSA.setSelected(false);
                B50SelFRPRSA.setSelected(false);
                B50SelTCPRSA.setSelected(false);
                B50SelTLPRSA.setSelected(true);
                ClearCheckPRS_A.setText("L");
            }
        });
        Button ApplyButtonPRS_B = new Button("저장");
        ApplyButtonPRS_B.getStyleClass().add("Buttons");
        ApplyButtonPRS_B.setOnAction(PRAB -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPRSB.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPRSB.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPRSB.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPRSB.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPRS_B.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(1)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(1), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(1), B50song.getTitle(1), B50song.getDiff(1), B50song.getLevel(1), B50song.getArtist(1), Score, ClearStatus, Potential), B50Location, 1);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPRS_B = new Button("삭제");
        DeleteButtonPRS_B.getStyleClass().add("Buttons");
        DeleteButtonPRS_B.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 1);
            ScoreMatchPRS_B.clear();
            if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("PRSClearStatus", "Pure Memory");
                B50SelPMPRSB.setSelected(true);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("P");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PRSClearStatus", "Full Recall");
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(true);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("F");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PRSClearStatus", "Track Complete");
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(true);
                B50SelTLPRSB.setSelected(false);
                ClearCheckPRS_B.setText("C");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PRSClearStatus", "Track Lost");
                B50SelPMPRSB.setSelected(false);
                B50SelFRPRSB.setSelected(false);
                B50SelTCPRSB.setSelected(false);
                B50SelTLPRSB.setSelected(true);
                ClearCheckPRS_B.setText("L");
            }
        });
        Button ApplyButtonPRS_C = new Button("저장");
        ApplyButtonPRS_C.getStyleClass().add("Buttons");
        ApplyButtonPRS_C.setOnAction(PRAC -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMPRSC.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRPRSC.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCPRSC.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLPRSC.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchPRS_C.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(1)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(1), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(1), B50song.getTitle(1), B50song.getDiff(1), B50song.getLevel(1), B50song.getArtist(1), Score, ClearStatus, Potential), B50Location, 1);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(1)), "확인", 395, 120);
            }
        });
        Button DeleteButtonPRS_C = new Button("삭제");
        DeleteButtonPRS_C.getStyleClass().add("Buttons");
        DeleteButtonPRS_C.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 1);
            ScoreMatchPRS_C.clear();
            if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("PRSClearStatus", "Pure Memory");
                B50SelPMPRSC.setSelected(true);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("P");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("PRSClearStatus", "Full Recall");
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(true);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("F");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("PRSClearStatus", "Track Complete");
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(true);
                B50SelTLPRSC.setSelected(false);
                ClearCheckPRS_C.setText("C");
            }
            if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("PRSClearStatus", "Track Lost");
                B50SelPMPRSC.setSelected(false);
                B50SelFRPRSC.setSelected(false);
                B50SelTCPRSC.setSelected(false);
                B50SelTLPRSC.setSelected(true);
                ClearCheckPRS_C.setText("L");
            }
        });

        Button ApplyButtonFTR_A = new Button("저장");
        ApplyButtonFTR_A.getStyleClass().add("Buttons");
        ApplyButtonFTR_A.setOnAction(FTAA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMFTRA.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRFTRA.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCFTRA.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLFTRA.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchFTR_A.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(2)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(2), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(2), B50song.getTitle(2), B50song.getDiff(2), B50song.getLevel(2), B50song.getArtist(2), Score, ClearStatus, Potential), B50Location, 2);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
        });
        Button DeleteButtonFTR_A = new Button("삭제");
        DeleteButtonFTR_A.getStyleClass().add("Buttons");
        DeleteButtonFTR_A.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 2);
            ScoreMatchFTR_A.clear();
            if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("FTRClearStatus", "Pure Memory");
                B50SelPMFTRA.setSelected(true);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("P");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("FTRClearStatus", "Full Recall");
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(true);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("F");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("FTRClearStatus", "Track Complete");
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(true);
                B50SelTLFTRA.setSelected(false);
                ClearCheckFTR_A.setText("C");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("FTRClearStatus", "Track Lost");
                B50SelPMFTRA.setSelected(false);
                B50SelFRFTRA.setSelected(false);
                B50SelTCFTRA.setSelected(false);
                B50SelTLFTRA.setSelected(true);
                ClearCheckFTR_A.setText("L");
            }
        });
        Button ApplyButtonFTR_B = new Button("저장");
        ApplyButtonFTR_B.getStyleClass().add("Buttons");
        ApplyButtonFTR_B.setOnAction(FTAB -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMFTRB.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRFTRB.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCFTRB.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLFTRB.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchFTR_B.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(2)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(2), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(2), B50song.getTitle(2), B50song.getDiff(2), B50song.getLevel(2), B50song.getArtist(2), Score, ClearStatus, Potential), B50Location, 2);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
        });
        Button DeleteButtonFTR_B = new Button("삭제");
        DeleteButtonFTR_B.getStyleClass().add("Buttons");
        DeleteButtonFTR_B.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 2);
            ScoreMatchFTR_B.clear();
            if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("FTRClearStatus", "Pure Memory");
                B50SelPMFTRB.setSelected(true);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("P");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("FTRClearStatus", "Full Recall");
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(true);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("F");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("FTRClearStatus", "Track Complete");
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(true);
                B50SelTLFTRB.setSelected(false);
                ClearCheckFTR_B.setText("C");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("FTRClearStatus", "Track Lost");
                B50SelPMFTRB.setSelected(false);
                B50SelFRFTRB.setSelected(false);
                B50SelTCFTRB.setSelected(false);
                B50SelTLFTRB.setSelected(true);
                ClearCheckFTR_B.setText("L");
            }
        });
        Button ApplyButtonFTR_C = new Button("저장");
        ApplyButtonFTR_C.getStyleClass().add("Buttons");
        ApplyButtonFTR_C.setOnAction(FTAC -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMFTRC.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRFTRC.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCFTRC.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLFTRC.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchFTR_C.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(2)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(2), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(2), B50song.getTitle(2), B50song.getDiff(2), B50song.getLevel(2), B50song.getArtist(2), Score, ClearStatus, Potential), B50Location, 2);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(2)), "확인", 395, 120);
            }
        });
        Button DeleteButtonFTR_C = new Button("삭제");
        DeleteButtonFTR_C.getStyleClass().add("Buttons");
        DeleteButtonFTR_C.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 2);
            ScoreMatchFTR_C.clear();
            if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("FTRClearStatus", "Pure Memory");
                B50SelPMFTRC.setSelected(true);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("P");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("FTRClearStatus", "Full Recall");
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(true);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("F");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("FTRClearStatus", "Track Complete");
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(true);
                B50SelTLFTRC.setSelected(false);
                ClearCheckFTR_C.setText("C");
            }
            if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("FTRClearStatus", "Track Lost");
                B50SelPMFTRC.setSelected(false);
                B50SelFRFTRC.setSelected(false);
                B50SelTCFTRC.setSelected(false);
                B50SelTLFTRC.setSelected(true);
                ClearCheckFTR_C.setText("L");
            }
        });

        Button ApplyButtonETR = new Button("저장");
        ApplyButtonETR.getStyleClass().add("Buttons");
        ApplyButtonETR.setOnAction(ETA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMETR.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRETR.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCETR.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLETR.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchETR.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(3)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(3), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(3), B50song.getTitle(3), B50song.getDiff(3), B50song.getLevel(3), B50song.getArtist(3), Score, ClearStatus, Potential), B50Location, 3);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(3)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(3)), "확인", 395, 120);
            }
        });
        Button DeleteButtonETR = new Button("삭제");
        DeleteButtonETR.getStyleClass().add("Buttons");
        DeleteButtonETR.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 3);
            ScoreMatchETR.clear();
            if (settings.get("ETRClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("ETRClearStatus", "Pure Memory");
                B50SelPMETR.setSelected(true);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("P");
            }
            if (settings.get("ETRClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("ETRClearStatus", "Full Recall");
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(true);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("F");
            }
            if (settings.get("ETRClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("ETRClearStatus", "Track Complete");
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(true);
                B50SelTLETR.setSelected(false);
                ClearCheckETR.setText("C");
            }
            if (settings.get("ETRClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("ETRClearStatus", "Track Lost");
                B50SelPMETR.setSelected(false);
                B50SelFRETR.setSelected(false);
                B50SelTCETR.setSelected(false);
                B50SelTLETR.setSelected(true);
                ClearCheckETR.setText("L");
            }
        });

        Button ApplyButtonBYD = new Button("저장");
        ApplyButtonBYD.getStyleClass().add("Buttons");
        ApplyButtonBYD.setOnAction(BYA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMBYD.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRBYD.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCBYD.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLBYD.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchBYD.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(4)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(4), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(4), B50song.getTitle(4), B50song.getDiff(4), B50song.getLevel(4), B50song.getArtist(4), Score, ClearStatus, Potential), B50Location, 4);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(4)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(4)), "확인", 395, 120);
            }
        });
        Button DeleteButtonBYD = new Button("삭제");
        DeleteButtonBYD.getStyleClass().add("Buttons");
        DeleteButtonBYD.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 4);
            ScoreMatchBYD.clear();
            if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("BYDClearStatus", "Pure Memory");
                B50SelPMBYD.setSelected(true);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("P");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("BYDClearStatus", "Full Recall");
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(true);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("F");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("BYDClearStatus", "Track Complete");
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(true);
                B50SelTLBYD.setSelected(false);
                ClearCheckBYD.setText("C");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("BYDClearStatus", "Track Lost");
                B50SelPMBYD.setSelected(false);
                B50SelFRBYD.setSelected(false);
                B50SelTCBYD.setSelected(false);
                B50SelTLBYD.setSelected(true);
                ClearCheckBYD.setText("L");
            }
        });

        Button ApplyButtonLast = new Button("저장");
        ApplyButtonLast.getStyleClass().add("Buttons");
        ApplyButtonLast.setOnAction(BYA -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            if (B50SelPMLast.isSelected()) {
                ClearStatus = "Pure Memory";
                isCleared = true;
            }
            else if (B50SelFRLast.isSelected()) {
                ClearStatus = "Full Recall";
                isCleared = true;
            }
            else if (B50SelTCLast.isSelected()) {
                ClearStatus = "Track Complete";
                isCleared = true;
            }
            else if (B50SelTLLast.isSelected()) {
                ClearStatus = "Track Lost";
                isCleared = false;
            }

            try {
                int Score = Integer.parseInt(ScoreMatchLast.getText());
                if (Score < 0 || Score > 10000000 + B50song.getNotes(4)) {
                    throw new IndexOutOfBoundsException();
                }
                double Potential = APCLogic.APCLogic_B50(B50song.getConst(4), Score, isCleared);
                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(4), B50song.getTitle(4), B50song.getDiff(4), B50song.getLevel(4), B50song.getArtist(4), Score, ClearStatus, Potential), B50Location, 4);
            }
            catch (NumberFormatException NFE) {
                //System.out.println("String to Integer 파싱 실패");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(4)), "확인", 395, 120);
            }
            catch (IndexOutOfBoundsException IOOBE) {
                //System.out.println("점수 범위 초과");
                showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(4)), "확인", 395, 120);
            }
        });
        Button DeleteButtonLast = new Button("삭제");
        DeleteButtonLast.getStyleClass().add("Buttons");
        DeleteButtonLast.setOnAction(PSD -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            B50jsonRemover(B50List, B50Location, 4);
            ScoreMatchLast.clear();
            if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
                settings.set("BYDClearStatus", "Pure Memory");
                B50SelPMLast.setSelected(true);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("P");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
                settings.set("BYDClearStatus", "Full Recall");
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(true);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("F");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
                settings.set("BYDClearStatus", "Track Complete");
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(true);
                B50SelTLLast.setSelected(false);
                ClearCheckLast.setText("C");
            }
            if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
                settings.set("BYDClearStatus", "Track Lost");
                B50SelPMLast.setSelected(false);
                B50SelFRLast.setSelected(false);
                B50SelTCLast.setSelected(false);
                B50SelTLLast.setSelected(true);
                ClearCheckLast.setText("L");
            }
        });

        GridPane B50InputPane2 = new GridPane();
        B50InputPane2.setHgap(10);
        B50InputPane2.setVgap(10);
        
        GridPane forFTR = new GridPane();
        forFTR.setHgap(10);
        forFTR.setVgap(10);
        forFTR.add(new Label("Past 난이도 점수 입력"), 1, 5);
        GridPane PSTGrid1 = new GridPane();
        PSTGrid1.setHgap(10);
        PSTGrid1.add(ScoreMatchPST_A, 0, 0);
        PSTGrid1.add(ClearCheckPST_A, 1, 0);
        PSTGrid1.add(ApplyButtonPST_A, 2, 0);
        PSTGrid1.add(DeleteButtonPST_A, 3, 0);
        forFTR.add(PSTGrid1, 1, 6);
        GridPane PRSGrid1 = new GridPane();
        PRSGrid1.setHgap(10);
        forFTR.add(new Label("Present 난이도 점수 입력"), 1, 7);
        PRSGrid1.add(ScoreMatchPRS_A, 0, 0);
        PRSGrid1.add(ClearCheckPRS_A, 1, 0);
        PRSGrid1.add(ApplyButtonPRS_A, 2, 0);
        PRSGrid1.add(DeleteButtonPRS_A, 3, 0);
        forFTR.add(PRSGrid1, 1, 8);
        GridPane FTRGrid1 = new GridPane();
        FTRGrid1.setHgap(10);
        forFTR.add(new Label("Future 난이도 점수 입력"), 1, 9);
        FTRGrid1.add(ScoreMatchFTR_A, 0, 0);
        FTRGrid1.add(ClearCheckFTR_A, 1, 0);
        FTRGrid1.add(ApplyButtonFTR_A, 2, 0);
        FTRGrid1.add(DeleteButtonFTR_A, 3, 0);
        forFTR.add(FTRGrid1, 1, 10);

        GridPane forETR = new GridPane();
        forETR.setHgap(10);
        forETR.setVgap(10);
        forETR.add(new Label("Past 난이도 점수 입력"), 1, 5);
        GridPane PSTGrid2 = new GridPane();
        PSTGrid2.setHgap(10);
        PSTGrid2.add(ScoreMatchPST_B, 0, 0);
        PSTGrid2.add(ClearCheckPST_B, 1, 0);
        PSTGrid2.add(ApplyButtonPST_B, 2, 0);
        PSTGrid2.add(DeleteButtonPST_B, 3, 0);
        forETR.add(PSTGrid2, 1, 6);
        GridPane PRSGrid2 = new GridPane();
        PRSGrid2.setHgap(10);
        forETR.add(new Label("Present 난이도 점수 입력"), 1, 7);
        PRSGrid2.add(ScoreMatchPRS_B, 0, 0);
        PRSGrid2.add(ClearCheckPRS_B, 1, 0);
        PRSGrid2.add(ApplyButtonPRS_B, 2, 0);
        PRSGrid2.add(DeleteButtonPRS_B, 3, 0);
        forETR.add(PRSGrid2, 1, 8);
        GridPane FTRGrid2 = new GridPane();
        FTRGrid2.setHgap(10);
        forETR.add(new Label("Future 난이도 점수 입력"), 1, 9);
        FTRGrid2.add(ScoreMatchFTR_B, 0, 0);
        FTRGrid2.add(ClearCheckFTR_B, 1, 0);
        FTRGrid2.add(ApplyButtonFTR_B, 2, 0);
        FTRGrid2.add(DeleteButtonFTR_B, 3, 0);
        forETR.add(FTRGrid2, 1, 10);
        GridPane ETRGrid = new GridPane();
        ETRGrid.setHgap(10);
        forETR.add(new Label("Eternal 난이도 점수 입력"), 1, 11);
        ETRGrid.add(ScoreMatchETR, 0, 0);
        ETRGrid.add(ClearCheckETR, 1, 0);
        ETRGrid.add(ApplyButtonETR, 2, 0);
        ETRGrid.add(DeleteButtonETR, 3, 0);
        forETR.add(ETRGrid, 1, 12);
        
        GridPane forBYD = new GridPane();
        forBYD.setHgap(10);
        forBYD.setVgap(10);
        forBYD.add(new Label("Past 난이도 점수 입력"), 1, 5);
        GridPane PSTGrid3 = new GridPane();
        PSTGrid3.setHgap(10);
        PSTGrid3.add(ScoreMatchPST_C, 0, 0);
        PSTGrid3.add(ClearCheckPST_C, 1, 0);
        PSTGrid3.add(ApplyButtonPST_C, 2, 0);
        PSTGrid3.add(DeleteButtonPST_C, 3, 0);
        forBYD.add(PSTGrid3, 1, 6);
        GridPane PRSGrid3 = new GridPane();
        PRSGrid3.setHgap(10);
        forBYD.add(new Label("Present 난이도 점수 입력"), 1, 7);
        PRSGrid3.add(ScoreMatchPRS_C, 0, 0);
        PRSGrid3.add(ClearCheckPRS_C, 1, 0);
        PRSGrid3.add(ApplyButtonPRS_C, 2, 0);
        PRSGrid3.add(DeleteButtonPRS_C, 3, 0);
        forBYD.add(PRSGrid3, 1, 8);
        GridPane FTRGrid3 = new GridPane();
        FTRGrid3.setHgap(10);
        forBYD.add(new Label("Future 난이도 점수 입력"), 1, 9);
        FTRGrid3.add(ScoreMatchFTR_C, 0, 0);
        FTRGrid3.add(ClearCheckFTR_C, 1, 0);
        FTRGrid3.add(ApplyButtonFTR_C, 2, 0);
        FTRGrid3.add(DeleteButtonFTR_C, 3, 0);
        forBYD.add(FTRGrid3, 1, 10);
        GridPane BYDGrid = new GridPane();
        BYDGrid.setHgap(10);
        Label BYDorINSLabel = new Label();
        forBYD.add(BYDorINSLabel, 1, 11);
        BYDGrid.add(ScoreMatchBYD, 0, 0);
        BYDGrid.add(ClearCheckBYD, 1, 0);
        BYDGrid.add(ApplyButtonBYD, 2, 0);
        BYDGrid.add(DeleteButtonBYD, 3, 0);
        forBYD.add(BYDGrid, 1, 12);

        GridPane forLast = new GridPane();
        forLast.setHgap(10);
        forLast.setVgap(10);
        forLast.add(new Label("Beyond 난이도 점수 입력"), 1, 5);
        GridPane LastGrid = new GridPane();
        LastGrid.setHgap(10);
        LastGrid.add(ScoreMatchLast, 0, 0);
        LastGrid.add(ClearCheckLast, 1, 0);
        LastGrid.add(ApplyButtonLast, 2, 0);
        LastGrid.add(DeleteButtonLast, 3, 0);
        forLast.add(LastGrid, 1, 6);
        
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(B50SearchResultTable.widthProperty());
        clip.heightProperty().bind(B50SearchResultTable.heightProperty());
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        B50SearchResultTable.setClip(clip);

        GridPane B50ip = new GridPane();
        B50ip.getStyleClass().add("Panes");
        B50ip.setHgap(10);
        B50ip.setVgap(10);

        B50InputPane1.add(new Label("곡 선택 :"), 5, 5);

        B50SearchField.setPromptText("여기에 검색어 입력");
        B50InputPane1.add(B50SearchField, 5, 6);

        B50SearchResultTable.setPrefHeight(500);
        B50SearchResultTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        B50TitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        B50DiffCol.setCellValueFactory(new PropertyValueFactory<>("diff"));
        B50TitleCol.setMinWidth(169);
        B50TitleCol.setMaxWidth(169);
        B50DiffCol.setMinWidth(50);
        B50DiffCol.setMaxWidth(50);

        B50TitleCol.setReorderable(false);
        B50DiffCol.setReorderable(false);
        
        B50SearchResultTable.getColumns().clear();
        B50SearchResultTable.getColumns().add(B50TitleCol);
        B50SearchResultTable.getColumns().add(B50DiffCol);
        B50SearchResultTable.setItems(B50SearchedSongs(B50SearchField.getText()));
        B50SearchResultTable.setMinHeight(75);
        B50SearchResultTable.setMinHeight(75);
        B50SearchResultTable.setMaxWidth(250);
        B50SearchResultTable.setMaxWidth(250);
        B50InputPane1.add(B50SearchResultTable, 5, 8);
        B50InputPane1.add(new Label(""), 5, 9);

        B50SearchResultTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                B50song = newSel.getSongInfo();
                Boolean isFoundPST = false;
                Boolean isFoundPRS = false;
                Boolean isFoundFTR = false;
                Boolean isFoundETR = false;
                Boolean isFoundBYD = false;
                Boolean isFoundLast = false;
                if (newSel.getSongInfo().getConst(3) > 0 && newSel.getSongInfo().getConst(4) == 0){
                    B50InputPane2.getChildren().clear();
                    B50InputPane2.add(forETR, 0, 0);
                    try (FileReader reader = new FileReader(B50Location)) {
                        Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
                        if (SDList != null && !SDList.isEmpty()) {
                            for (ScoreData S : SDList) {
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(0)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(0))) {
                                    ScoreMatchPST_B.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPST_B.setText("P");
                                        B50SelPMPSTB.setSelected(true);
                                        B50SelFRPSTB.setSelected(false);
                                        B50SelTCPSTB.setSelected(false);
                                        B50SelTLPSTB.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPST_B.setText("F");
                                        B50SelPMPSTB.setSelected(false);
                                        B50SelFRPSTB.setSelected(true);
                                        B50SelTCPSTB.setSelected(false);
                                        B50SelTLPSTB.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPST_B.setText("C");
                                        B50SelPMPSTB.setSelected(false);
                                        B50SelFRPSTB.setSelected(false);
                                        B50SelTCPSTB.setSelected(true);
                                        B50SelTLPSTB.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPST_B.setText("L");
                                        B50SelPMPSTB.setSelected(false);
                                        B50SelFRPSTB.setSelected(false);
                                        B50SelTCPSTB.setSelected(false);
                                        B50SelTLPSTB.setSelected(true);
                                        isFoundPST = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(1)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(1))) {
                                    ScoreMatchPRS_B.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPRS_B.setText("P");
                                        B50SelPMPRSB.setSelected(true);
                                        B50SelFRPRSB.setSelected(false);
                                        B50SelTCPRSB.setSelected(false);
                                        B50SelTLPRSB.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPRS_B.setText("F");
                                        B50SelPMPRSB.setSelected(false);
                                        B50SelFRPRSB.setSelected(true);
                                        B50SelTCPRSB.setSelected(false);
                                        B50SelTLPRSB.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPRS_B.setText("C");
                                        B50SelPMPRSB.setSelected(false);
                                        B50SelFRPRSB.setSelected(false);
                                        B50SelTCPRSB.setSelected(true);
                                        B50SelTLPRSB.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPRS_B.setText("L");
                                        B50SelPMPRSB.setSelected(false);
                                        B50SelFRPRSB.setSelected(false);
                                        B50SelTCPRSB.setSelected(false);
                                        B50SelTLPRSB.setSelected(true);
                                        isFoundPRS = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(2)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(2))) {
                                    ScoreMatchFTR_B.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckFTR_B.setText("P");
                                        B50SelPMFTRB.setSelected(true);
                                        B50SelFRFTRB.setSelected(false);
                                        B50SelTCFTRB.setSelected(false);
                                        B50SelTLFTRB.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckFTR_B.setText("F");
                                        B50SelPMFTRB.setSelected(false);
                                        B50SelFRFTRB.setSelected(true);
                                        B50SelTCFTRB.setSelected(false);
                                        B50SelTLFTRB.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckFTR_B.setText("C");
                                        B50SelPMFTRB.setSelected(false);
                                        B50SelFRFTRB.setSelected(false);
                                        B50SelTCFTRB.setSelected(true);
                                        B50SelTLFTRB.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckFTR_B.setText("L");
                                        B50SelPMFTRB.setSelected(false);
                                        B50SelFRFTRB.setSelected(false);
                                        B50SelTCFTRB.setSelected(false);
                                        B50SelTLFTRB.setSelected(true);
                                        isFoundFTR = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(3)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(3))) {
                                    ScoreMatchETR.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckETR.setText("P");
                                        B50SelPMETR.setSelected(true);
                                        B50SelFRETR.setSelected(false);
                                        B50SelTCETR.setSelected(false);
                                        B50SelTLETR.setSelected(false);
                                        isFoundETR = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckETR.setText("F");
                                        B50SelPMETR.setSelected(false);
                                        B50SelFRETR.setSelected(true);
                                        B50SelTCETR.setSelected(false);
                                        B50SelTLETR.setSelected(false);
                                        isFoundETR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckETR.setText("C");
                                        B50SelPMETR.setSelected(false);
                                        B50SelFRETR.setSelected(false);
                                        B50SelTCETR.setSelected(true);
                                        B50SelTLETR.setSelected(false);
                                        isFoundETR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckETR.setText("L");
                                        B50SelPMETR.setSelected(false);
                                        B50SelFRETR.setSelected(false);
                                        B50SelTCETR.setSelected(false);
                                        B50SelTLETR.setSelected(true);
                                        isFoundETR = true;
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        ScoreMatchPST_B.setText("");
                        ScoreMatchPRS_B.setText("");
                        ScoreMatchFTR_B.setText("");
                        ScoreMatchETR.setText("");
                    }
                }
                else if (newSel.getSongInfo().getConst(4) > 0 && newSel.getSongInfo().getConst(3) == 0 && !(newSel.getSongInfo().getTitle(4).equals("Last | Moment") || newSel.getSongInfo().getTitle(4).equals("Last | Eternity"))){
                    B50InputPane2.getChildren().clear();
                    B50InputPane2.add(forBYD, 0, 0);
                    BYDorINS = (newSel.getDiff().equals("BYD")) ? "Beyond" : "Inscribed";
                    BYDorINSLabel.setText(String.format("%s 난이도 점수 입력", BYDorINS));
                    try (FileReader reader = new FileReader(B50Location)) {
                        Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
                        if (SDList != null && !SDList.isEmpty()) {
                            for (ScoreData S : SDList) {
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(0)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(0))) {
                                    ScoreMatchPST_C.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPST_C.setText("P");
                                        B50SelPMPSTC.setSelected(true);
                                        B50SelFRPSTC.setSelected(false);
                                        B50SelTCPSTC.setSelected(false);
                                        B50SelTLPSTC.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPST_C.setText("F");
                                        B50SelPMPSTC.setSelected(false);
                                        B50SelFRPSTC.setSelected(true);
                                        B50SelTCPSTC.setSelected(false);
                                        B50SelTLPSTC.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPST_C.setText("C");
                                        B50SelPMPSTC.setSelected(false);
                                        B50SelFRPSTC.setSelected(false);
                                        B50SelTCPSTC.setSelected(true);
                                        B50SelTLPSTC.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPST_C.setText("L");
                                        B50SelPMPSTC.setSelected(false);
                                        B50SelFRPSTC.setSelected(false);
                                        B50SelTCPSTC.setSelected(false);
                                        B50SelTLPSTC.setSelected(true);
                                        isFoundPST = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(1)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(1))) {
                                    ScoreMatchPRS_C.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPRS_C.setText("P");
                                        B50SelPMPRSC.setSelected(true);
                                        B50SelFRPRSC.setSelected(false);
                                        B50SelTCPRSC.setSelected(false);
                                        B50SelTLPRSC.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPRS_C.setText("F");
                                        B50SelPMPRSC.setSelected(false);
                                        B50SelFRPRSC.setSelected(true);
                                        B50SelTCPRSC.setSelected(false);
                                        B50SelTLPRSC.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPRS_C.setText("C");
                                        B50SelPMPRSC.setSelected(false);
                                        B50SelFRPRSC.setSelected(false);
                                        B50SelTCPRSC.setSelected(true);
                                        B50SelTLPRSC.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPRS_C.setText("L");
                                        B50SelPMPRSC.setSelected(false);
                                        B50SelFRPRSC.setSelected(false);
                                        B50SelTCPRSC.setSelected(false);
                                        B50SelTLPRSC.setSelected(true);
                                        isFoundPRS = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(2)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(2))) {
                                    ScoreMatchFTR_C.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckFTR_C.setText("P");
                                        B50SelPMFTRC.setSelected(true);
                                        B50SelFRFTRC.setSelected(false);
                                        B50SelTCFTRC.setSelected(false);
                                        B50SelTLFTRC.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckFTR_C.setText("F");
                                        B50SelPMFTRC.setSelected(false);
                                        B50SelFRFTRC.setSelected(true);
                                        B50SelTCFTRC.setSelected(false);
                                        B50SelTLFTRC.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckFTR_C.setText("C");
                                        B50SelPMFTRC.setSelected(false);
                                        B50SelFRFTRC.setSelected(false);
                                        B50SelTCFTRC.setSelected(true);
                                        B50SelTLFTRC.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckFTR_C.setText("L");
                                        B50SelPMFTRC.setSelected(false);
                                        B50SelFRFTRC.setSelected(false);
                                        B50SelTCFTRC.setSelected(false);
                                        B50SelTLFTRC.setSelected(true);
                                        isFoundFTR = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(4)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(4))) {
                                    ScoreMatchBYD.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckBYD.setText("P");
                                        B50SelPMBYD.setSelected(true);
                                        B50SelFRBYD.setSelected(false);
                                        B50SelTCBYD.setSelected(false);
                                        B50SelTLBYD.setSelected(false);
                                        isFoundBYD = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckBYD.setText("F");
                                        B50SelPMBYD.setSelected(false);
                                        B50SelFRBYD.setSelected(true);
                                        B50SelTCBYD.setSelected(false);
                                        B50SelTLBYD.setSelected(false);
                                        isFoundBYD = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckBYD.setText("C");
                                        B50SelPMBYD.setSelected(false);
                                        B50SelFRBYD.setSelected(false);
                                        B50SelTCBYD.setSelected(true);
                                        B50SelTLBYD.setSelected(false);
                                        isFoundBYD = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckBYD.setText("L");
                                        B50SelPMBYD.setSelected(false);
                                        B50SelFRBYD.setSelected(false);
                                        B50SelTCBYD.setSelected(false);
                                        B50SelTLBYD.setSelected(true);
                                        isFoundBYD = true;
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        ScoreMatchPST_C.setText("");
                        ScoreMatchPRS_C.setText("");
                        ScoreMatchFTR_C.setText("");
                        ScoreMatchBYD.setText("");
                    }
                }
                else if (newSel.getSongInfo().getConst(4) > 0 && newSel.getSongInfo().getConst(3) == 0 && (newSel.getSongInfo().getTitle(4).equals("Last | Moment") || newSel.getSongInfo().getTitle(4).equals("Last | Eternity"))) {
                    B50InputPane2.getChildren().clear();
                    B50InputPane2.add(forLast, 0, 0);
                    BYDorINSLabel.setText("Beyond 난이도 점수 입력");
                    try (FileReader reader = new FileReader(B50Location)) {
                        Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
                        if (SDList != null && !SDList.isEmpty()) {
                            for (ScoreData S : SDList) {
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(4)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(4))) {
                                    ScoreMatchLast.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckLast.setText("P");
                                        B50SelPMLast.setSelected(true);
                                        B50SelFRLast.setSelected(false);
                                        B50SelTCLast.setSelected(false);
                                        B50SelTLLast.setSelected(false);
                                        isFoundLast = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckLast.setText("F");
                                        B50SelPMLast.setSelected(false);
                                        B50SelFRLast.setSelected(true);
                                        B50SelTCLast.setSelected(false);
                                        B50SelTLLast.setSelected(false);
                                        isFoundLast = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckLast.setText("C");
                                        B50SelPMLast.setSelected(false);
                                        B50SelFRLast.setSelected(false);
                                        B50SelTCLast.setSelected(true);
                                        B50SelTLLast.setSelected(false);
                                        isFoundLast = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckLast.setText("L");
                                        B50SelPMLast.setSelected(false);
                                        B50SelFRLast.setSelected(false);
                                        B50SelTCLast.setSelected(false);
                                        B50SelTLLast.setSelected(true);
                                        isFoundLast = true;
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        ScoreMatchLast.setText("");
                    }
                }
                else {
                    B50InputPane2.getChildren().clear();
                    B50InputPane2.add(forFTR, 0, 0);
                    try (FileReader reader = new FileReader(B50Location)) {
                        Type ListType = new TypeToken<List<ScoreData>>() {}.getType();
                        List<ScoreData> SDList = B50Builder.fromJson(reader, ListType);
                        if (SDList != null && !SDList.isEmpty()) {
                            for (ScoreData S : SDList) {
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(0)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(0))) {
                                    ScoreMatchPST_A.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPST_A.setText("P");
                                        B50SelPMPSTA.setSelected(true);
                                        B50SelFRPSTA.setSelected(false);
                                        B50SelTCPSTA.setSelected(false);
                                        B50SelTLPSTA.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPST_A.setText("F");
                                        B50SelPMPSTA.setSelected(false);
                                        B50SelFRPSTA.setSelected(true);
                                        B50SelTCPSTA.setSelected(false);
                                        B50SelTLPSTA.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPST_A.setText("C");
                                        B50SelPMPSTA.setSelected(false);
                                        B50SelFRPSTA.setSelected(false);
                                        B50SelTCPSTA.setSelected(true);
                                        B50SelTLPSTA.setSelected(false);
                                        isFoundPST = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPST_A.setText("L");
                                        B50SelPMPSTA.setSelected(false);
                                        B50SelFRPSTA.setSelected(false);
                                        B50SelTCPSTA.setSelected(false);
                                        B50SelTLPSTA.setSelected(true);
                                        isFoundPST = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(1)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(1))) {
                                    ScoreMatchPRS_A.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckPRS_A.setText("P");
                                        B50SelPMPRSA.setSelected(true);
                                        B50SelFRPRSA.setSelected(false);
                                        B50SelTCPRSA.setSelected(false);
                                        B50SelTLPRSA.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckPRS_A.setText("F");
                                        B50SelPMPRSA.setSelected(false);
                                        B50SelFRPRSA.setSelected(true);
                                        B50SelTCPRSA.setSelected(false);
                                        B50SelTLPRSA.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckPRS_A.setText("C");
                                        B50SelPMPRSA.setSelected(false);
                                        B50SelFRPRSA.setSelected(false);
                                        B50SelTCPRSA.setSelected(true);
                                        B50SelTLPRSA.setSelected(false);
                                        isFoundPRS = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckPRS_A.setText("L");
                                        B50SelPMPRSA.setSelected(false);
                                        B50SelFRPRSA.setSelected(false);
                                        B50SelTCPRSA.setSelected(false);
                                        B50SelTLPRSA.setSelected(true);
                                        isFoundPRS = true;
                                    }
                                }
                                if (S.getTitle().equals(newSel.getSongInfo().getTitle(2)) && S.getDifficulty().equals(newSel.getSongInfo().getDiff(2))) {
                                    ScoreMatchFTR_A.setText(String.valueOf(S.getScore()));
                                    if (S.getClearStatus().equals("Pure Memory")) {
                                        ClearCheckFTR_A.setText("P");
                                        B50SelPMFTRA.setSelected(true);
                                        B50SelFRFTRA.setSelected(false);
                                        B50SelTCFTRA.setSelected(false);
                                        B50SelTLFTRA.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Full Recall")) {
                                        ClearCheckFTR_A.setText("F");
                                        B50SelPMFTRA.setSelected(false);
                                        B50SelFRFTRA.setSelected(true);
                                        B50SelTCFTRA.setSelected(false);
                                        B50SelTLFTRA.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Complete")) {
                                        ClearCheckFTR_A.setText("C");
                                        B50SelPMFTRA.setSelected(false);
                                        B50SelFRFTRA.setSelected(false);
                                        B50SelTCFTRA.setSelected(true);
                                        B50SelTLFTRA.setSelected(false);
                                        isFoundFTR = true;
                                    }
                                    else if (S.getClearStatus().equals("Track Lost")) {
                                        ClearCheckFTR_A.setText("L");
                                        B50SelPMFTRA.setSelected(false);
                                        B50SelFRFTRA.setSelected(false);
                                        B50SelTCFTRA.setSelected(false);
                                        B50SelTLFTRA.setSelected(true);
                                        isFoundFTR = true;
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        ScoreMatchPST_A.setText("");
                        ScoreMatchPRS_A.setText("");
                        ScoreMatchFTR_A.setText("");
                    }
                }
                if (isFoundPST == false) {
                    ScoreMatchPST_A.clear();
                    ScoreMatchPST_B.clear();
                    ScoreMatchPST_C.clear();
                    if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMPSTA.setSelected(true);
                        B50SelFRPSTA.setSelected(false);
                        B50SelTCPSTA.setSelected(false);
                        B50SelTLPSTA.setSelected(false);
                        ClearCheckPST_A.setText("P");
                        B50SelPMPSTB.setSelected(true);
                        B50SelFRPSTB.setSelected(false);
                        B50SelTCPSTB.setSelected(false);
                        B50SelTLPSTB.setSelected(false);
                        ClearCheckPST_B.setText("P");
                        B50SelPMPSTC.setSelected(true);
                        B50SelFRPSTC.setSelected(false);
                        B50SelTCPSTC.setSelected(false);
                        B50SelTLPSTC.setSelected(false);
                        ClearCheckPST_C.setText("P");
                    }
                    if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMPSTA.setSelected(false);
                        B50SelFRPSTA.setSelected(true);
                        B50SelTCPSTA.setSelected(false);
                        B50SelTLPSTA.setSelected(false);
                        ClearCheckPST_A.setText("F");
                        B50SelPMPSTB.setSelected(false);
                        B50SelFRPSTB.setSelected(true);
                        B50SelTCPSTB.setSelected(false);
                        B50SelTLPSTB.setSelected(false);
                        ClearCheckPST_B.setText("F");
                        B50SelPMPSTC.setSelected(false);
                        B50SelFRPSTC.setSelected(true);
                        B50SelTCPSTC.setSelected(false);
                        B50SelTLPSTC.setSelected(false);
                        ClearCheckPST_C.setText("F");
                    }
                    if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMPSTA.setSelected(false);
                        B50SelFRPSTA.setSelected(false);
                        B50SelTCPSTA.setSelected(true);
                        B50SelTLPSTA.setSelected(false);
                        ClearCheckPST_A.setText("C");
                        B50SelPMPSTB.setSelected(false);
                        B50SelFRPSTB.setSelected(false);
                        B50SelTCPSTB.setSelected(true);
                        B50SelTLPSTB.setSelected(false);
                        ClearCheckPST_B.setText("C");
                        B50SelPMPSTC.setSelected(false);
                        B50SelFRPSTC.setSelected(false);
                        B50SelTCPSTC.setSelected(true);
                        B50SelTLPSTC.setSelected(false);
                        ClearCheckPST_C.setText("C");
                    }
                    if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMPSTA.setSelected(false);
                        B50SelFRPSTA.setSelected(false);
                        B50SelTCPSTA.setSelected(false);
                        B50SelTLPSTA.setSelected(true);
                        ClearCheckPST_A.setText("L");
                        B50SelPMPSTB.setSelected(false);
                        B50SelFRPSTB.setSelected(false);
                        B50SelTCPSTB.setSelected(false);
                        B50SelTLPSTB.setSelected(true);
                        ClearCheckPST_B.setText("L");
                        B50SelPMPSTC.setSelected(false);
                        B50SelFRPSTC.setSelected(false);
                        B50SelTCPSTC.setSelected(false);
                        B50SelTLPSTC.setSelected(true);
                        ClearCheckPST_C.setText("L");
                    }
                }
                if (isFoundPRS == false) {
                    ScoreMatchPRS_A.clear();
                    ScoreMatchPRS_B.clear();
                    ScoreMatchPRS_C.clear();
                    if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMPRSA.setSelected(true);
                        B50SelFRPRSA.setSelected(false);
                        B50SelTCPRSA.setSelected(false);
                        B50SelTLPRSA.setSelected(false);
                        ClearCheckPRS_A.setText("P");
                        B50SelPMPRSB.setSelected(true);
                        B50SelFRPRSB.setSelected(false);
                        B50SelTCPRSB.setSelected(false);
                        B50SelTLPRSB.setSelected(false);
                        ClearCheckPRS_B.setText("P");
                        B50SelPMPRSC.setSelected(true);
                        B50SelFRPRSC.setSelected(false);
                        B50SelTCPRSC.setSelected(false);
                        B50SelTLPRSC.setSelected(false);
                        ClearCheckPRS_C.setText("P");
                    }
                    if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMPRSA.setSelected(false);
                        B50SelFRPRSA.setSelected(true);
                        B50SelTCPRSA.setSelected(false);
                        B50SelTLPRSA.setSelected(false);
                        ClearCheckPRS_A.setText("F");
                        B50SelPMPRSB.setSelected(false);
                        B50SelFRPRSB.setSelected(true);
                        B50SelTCPRSB.setSelected(false);
                        B50SelTLPRSB.setSelected(false);
                        ClearCheckPRS_B.setText("F");
                        B50SelPMPRSC.setSelected(false);
                        B50SelFRPRSC.setSelected(true);
                        B50SelTCPRSC.setSelected(false);
                        B50SelTLPRSC.setSelected(false);
                        ClearCheckPRS_C.setText("F");
                    }
                    if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMPRSA.setSelected(false);
                        B50SelFRPRSA.setSelected(false);
                        B50SelTCPRSA.setSelected(true);
                        B50SelTLPRSA.setSelected(false);
                        ClearCheckPRS_A.setText("C");
                        B50SelPMPRSB.setSelected(false);
                        B50SelFRPRSB.setSelected(false);
                        B50SelTCPRSB.setSelected(true);
                        B50SelTLPRSB.setSelected(false);
                        ClearCheckPRS_B.setText("C");
                        B50SelPMPRSC.setSelected(false);
                        B50SelFRPRSC.setSelected(false);
                        B50SelTCPRSC.setSelected(true);
                        B50SelTLPRSC.setSelected(false);
                        ClearCheckPRS_C.setText("C");
                    }
                    if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMPRSA.setSelected(false);
                        B50SelFRPRSA.setSelected(false);
                        B50SelTCPRSA.setSelected(false);
                        B50SelTLPRSA.setSelected(true);
                        ClearCheckPRS_A.setText("L");
                        B50SelPMPRSB.setSelected(false);
                        B50SelFRPRSB.setSelected(false);
                        B50SelTCPRSB.setSelected(false);
                        B50SelTLPRSB.setSelected(true);
                        ClearCheckPRS_B.setText("L");
                        B50SelPMPRSC.setSelected(false);
                        B50SelFRPRSC.setSelected(false);
                        B50SelTCPRSC.setSelected(false);
                        B50SelTLPRSC.setSelected(true);
                        ClearCheckPRS_C.setText("L");
                    }
                }
                if (isFoundFTR == false) {
                    ScoreMatchFTR_A.clear();
                    ScoreMatchFTR_B.clear();
                    ScoreMatchFTR_C.clear();
                    if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMFTRA.setSelected(true);
                        B50SelFRFTRA.setSelected(false);
                        B50SelTCFTRA.setSelected(false);
                        B50SelTLFTRA.setSelected(false);
                        ClearCheckFTR_A.setText("P");
                        B50SelPMFTRB.setSelected(true);
                        B50SelFRFTRB.setSelected(false);
                        B50SelTCFTRB.setSelected(false);
                        B50SelTLFTRB.setSelected(false);
                        ClearCheckFTR_B.setText("P");
                        B50SelPMFTRC.setSelected(true);
                        B50SelFRFTRC.setSelected(false);
                        B50SelTCFTRC.setSelected(false);
                        B50SelTLFTRC.setSelected(false);
                        ClearCheckFTR_C.setText("P");
                    }
                    if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMFTRA.setSelected(false);
                        B50SelFRFTRA.setSelected(true);
                        B50SelTCFTRA.setSelected(false);
                        B50SelTLFTRA.setSelected(false);
                        ClearCheckFTR_A.setText("F");
                        B50SelPMFTRB.setSelected(false);
                        B50SelFRFTRB.setSelected(true);
                        B50SelTCFTRB.setSelected(false);
                        B50SelTLFTRB.setSelected(false);
                        ClearCheckFTR_B.setText("F");
                        B50SelPMFTRC.setSelected(false);
                        B50SelFRFTRC.setSelected(true);
                        B50SelTCFTRC.setSelected(false);
                        B50SelTLFTRC.setSelected(false);
                        ClearCheckFTR_C.setText("F");
                    }
                    if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMFTRA.setSelected(false);
                        B50SelFRFTRA.setSelected(false);
                        B50SelTCFTRA.setSelected(true);
                        B50SelTLFTRA.setSelected(false);
                        ClearCheckFTR_A.setText("C");
                        B50SelPMFTRB.setSelected(false);
                        B50SelFRFTRB.setSelected(false);
                        B50SelTCFTRB.setSelected(true);
                        B50SelTLFTRB.setSelected(false);
                        ClearCheckFTR_B.setText("C");
                        B50SelPMFTRC.setSelected(false);
                        B50SelFRFTRC.setSelected(false);
                        B50SelTCFTRC.setSelected(true);
                        B50SelTLFTRC.setSelected(false);
                        ClearCheckFTR_C.setText("C");
                    }
                    if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMFTRA.setSelected(false);
                        B50SelFRFTRA.setSelected(false);
                        B50SelTCFTRA.setSelected(false);
                        B50SelTLFTRA.setSelected(true);
                        ClearCheckFTR_A.setText("L");
                        B50SelPMFTRB.setSelected(false);
                        B50SelFRFTRB.setSelected(false);
                        B50SelTCFTRB.setSelected(false);
                        B50SelTLFTRB.setSelected(true);
                        ClearCheckFTR_B.setText("L");
                        B50SelPMFTRC.setSelected(false);
                        B50SelFRFTRC.setSelected(false);
                        B50SelTCFTRC.setSelected(false);
                        B50SelTLFTRC.setSelected(true);
                        ClearCheckFTR_C.setText("L");
                    }
                }
                if (isFoundETR == false) {
                    ScoreMatchETR.clear();
                    if (settings.get("ETRClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMETR.setSelected(true);
                        B50SelFRETR.setSelected(false);
                        B50SelTCETR.setSelected(false);
                        B50SelTLETR.setSelected(false);
                        ClearCheckETR.setText("P");
                    }
                    if (settings.get("ETRClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMETR.setSelected(false);
                        B50SelFRETR.setSelected(true);
                        B50SelTCETR.setSelected(false);
                        B50SelTLETR.setSelected(false);
                        ClearCheckETR.setText("F");
                    }
                    if (settings.get("ETRClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMETR.setSelected(false);
                        B50SelFRETR.setSelected(false);
                        B50SelTCETR.setSelected(true);
                        B50SelTLETR.setSelected(false);
                        ClearCheckETR.setText("C");
                    }
                    if (settings.get("ETRClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMETR.setSelected(false);
                        B50SelFRETR.setSelected(false);
                        B50SelTCETR.setSelected(false);
                        B50SelTLETR.setSelected(true);
                        ClearCheckETR.setText("L");
                    }
                    
                }
                if (isFoundBYD == false) {
                    ScoreMatchBYD.clear();
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMBYD.setSelected(true);
                        B50SelFRBYD.setSelected(false);
                        B50SelTCBYD.setSelected(false);
                        B50SelTLBYD.setSelected(false);
                        ClearCheckBYD.setText("P");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMBYD.setSelected(false);
                        B50SelFRBYD.setSelected(true);
                        B50SelTCBYD.setSelected(false);
                        B50SelTLBYD.setSelected(false);
                        ClearCheckBYD.setText("F");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMBYD.setSelected(false);
                        B50SelFRBYD.setSelected(false);
                        B50SelTCBYD.setSelected(true);
                        B50SelTLBYD.setSelected(false);
                        ClearCheckBYD.setText("C");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMBYD.setSelected(false);
                        B50SelFRBYD.setSelected(false);
                        B50SelTCBYD.setSelected(false);
                        B50SelTLBYD.setSelected(true);
                        ClearCheckBYD.setText("L");
                    }
                }
                if (isFoundLast == false) {
                    ScoreMatchLast.clear();
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
                        B50SelPMLast.setSelected(true);
                        B50SelFRLast.setSelected(false);
                        B50SelTCLast.setSelected(false);
                        B50SelTLLast.setSelected(false);
                        ClearCheckLast.setText("P");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
                        B50SelPMLast.setSelected(false);
                        B50SelFRLast.setSelected(true);
                        B50SelTCLast.setSelected(false);
                        B50SelTLLast.setSelected(false);
                        ClearCheckLast.setText("F");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
                        B50SelPMLast.setSelected(false);
                        B50SelFRLast.setSelected(false);
                        B50SelTCLast.setSelected(true);
                        B50SelTLLast.setSelected(false);
                        ClearCheckLast.setText("C");
                    }
                    if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
                        B50SelPMLast.setSelected(false);
                        B50SelFRLast.setSelected(false);
                        B50SelTCLast.setSelected(false);
                        B50SelTLLast.setSelected(true);
                        ClearCheckLast.setText("L");
                    }
                }
            }
        });

        B50SearchField.setPromptText("여기에 검색어 입력");
        B50SearchField.textProperty().addListener((obs, old, val) -> {
            B50SearchResultTable.setItems(B50SearchedSongs(B50SearchField.getText()));
        });

        B50ip.add(B50InputPane1, 0, 0);
        B50ip.add(B50InputPane2, 1, 0);

        B50ip.setFocusTraversable(false);
        B50InputPane1.setFocusTraversable(false);
        B50InputPane2.setFocusTraversable(false);
        forFTR.setFocusTraversable(false);
        forETR.setFocusTraversable(false);
        forBYD.setFocusTraversable(false);
        
        B50InputPane2.add(new Label("스코어 데이터를 입력할 악곡을 선택하여 입력 창 표시"), 1, 17);
        
        return B50ip;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                    B50Manage                                                    */
    /* --------------------------------------------------------------------------------------------------------------- */

    private MenuButton B50DiffSelect = new MenuButton("--");

    private List<String> B50DiffList = new ArrayList<String>();

    int B50Diff;

    ToggleGroup B50Sels = new ToggleGroup();
    RadioMenuItem B50SelPst = new RadioMenuItem("PST");
    RadioMenuItem B50SelPrs = new RadioMenuItem("PRS");
    RadioMenuItem B50SelFtr = new RadioMenuItem("FTR");
    RadioMenuItem B50SelEtr = new RadioMenuItem("ETR");
    RadioMenuItem B50SelByd = new RadioMenuItem("BYD");
    
    public ArrayList<String> B50Title = new ArrayList<>();
    public ArrayList<Integer> B50Score = new ArrayList<>();
    public ArrayList<String> B50ScoreStr = new ArrayList<>();
    public ArrayList<String> B50Artist = new ArrayList<>();
    public ArrayList<String> B50Cleared = new ArrayList<>();
    public ArrayList<Boolean> B50ClearedBool = new ArrayList<>();
    
    private Songs RecievedSong;
    private ArrayList<Songs> RecievedSongs = new ArrayList<>();
    
    Boolean SaveAvailable = true;
    
    public GridPane B50Manage() {
        PageIndex = 32;
        B50SelPst.setToggleGroup(B50Sels);
        B50SelPrs.setToggleGroup(B50Sels);
        B50SelFtr.setToggleGroup(B50Sels);
        B50SelEtr.setToggleGroup(B50Sels);
        B50SelByd.setToggleGroup(B50Sels);

        SaveAvailable = true;

        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        AOInputError.setText("");

        B50Title.clear();
        B50Score.clear();
        B50ScoreStr.clear();
        B50Artist.clear();
        B50Cleared.clear();
        B50ClearedBool.clear();
        RecievedSongs.clear();

        GridPane B50MainGrid = new GridPane();
        B50MainGrid.getStyleClass().add("Panes");

        GridPane B50Grid1 = new GridPane();
        B50Grid1.getStyleClass().add("Panes");
        B50Grid1.setHgap(10);
        B50Grid1.setVgap(10);

        GridPane B50Grid2 = new GridPane();
        B50Grid2.getStyleClass().add("Panes");
        B50Grid2.setHgap(10);
        B50Grid2.setVgap(10);

        if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                B50SelPst.setSelected(true);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("0");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("PST");
        }
        else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(true);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("1");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("PRS");
        }
        else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(true);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("2");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("FTR");
        }
        else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(true);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("3");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("ETR");
        }
        else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(true);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("4");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("BYD");
        }

        System.out.println(B50DiffList);

        B50SelPst.setOnAction(a -> {
            if (B50SelPst.isSelected()) {
                B50SelPst.setSelected(true);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("0");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("PST");
                System.out.println(B50Diff);
            }
        });
        B50SelPrs.setOnAction(a -> {
            if (B50SelPrs.isSelected()) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(true);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("1");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("PRS");
                System.out.println(B50Diff);
            }
        });
        B50SelFtr.setOnAction(a -> {
            if (B50SelFtr.isSelected()) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(true);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("2");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("FTR");
                System.out.println(B50Diff);
            }
        });
        B50SelEtr.setOnAction(a -> {
            if (B50SelEtr.isSelected()) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(true);
                B50SelByd.setSelected(false);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("3");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("ETR");
                System.out.println(B50Diff);
            }
        });
        B50SelByd.setOnAction(a -> {
            if (B50SelByd.isSelected()) {
                B50SelPst.setSelected(false);
                B50SelPrs.setSelected(false);
                B50SelFtr.setSelected(false);
                B50SelEtr.setSelected(false);
                B50SelByd.setSelected(true);
                if (B50DiffList != null) {
                    B50DiffList.clear();
                }
                B50DiffList.add("4");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                B50DiffSelect.setText("BYD");
                System.out.println(B50Diff);
            }
        });

        int TitleMatchLabelWidth = 150;

        Label TitleMatchLabel1 = new Label("[난이도] 제목");
        TitleMatchLabel1.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel2 = new Label("[난이도] 제목");
        TitleMatchLabel2.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel3 = new Label("[난이도] 제목");
        TitleMatchLabel3.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel4 = new Label("[난이도] 제목");
        TitleMatchLabel4.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel5 = new Label("[난이도] 제목");
        TitleMatchLabel5.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel6 = new Label("[난이도] 제목");
        TitleMatchLabel6.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel7 = new Label("[난이도] 제목");
        TitleMatchLabel7.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel8 = new Label("[난이도] 제목");
        TitleMatchLabel8.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel9 = new Label("[난이도] 제목");
        TitleMatchLabel9.setPrefWidth(TitleMatchLabelWidth);
        Label TitleMatchLabel10 = new Label("[난이도] 제목");
        TitleMatchLabel10.setPrefWidth(TitleMatchLabelWidth);

        int ScoreMatchLabelWidth = 100;

        Label ScoreMatchLabel1 = new Label("점수");
        ScoreMatchLabel1.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel2 = new Label("점수");
        ScoreMatchLabel2.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel3 = new Label("점수");
        ScoreMatchLabel3.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel4 = new Label("점수");
        ScoreMatchLabel4.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel5 = new Label("점수");
        ScoreMatchLabel5.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel6 = new Label("점수");
        ScoreMatchLabel6.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel7 = new Label("점수");
        ScoreMatchLabel7.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel8 = new Label("점수");
        ScoreMatchLabel8.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel9 = new Label("점수");
        ScoreMatchLabel9.setPrefWidth(ScoreMatchLabelWidth);
        Label ScoreMatchLabel10 = new Label("점수");
        ScoreMatchLabel10.setPrefWidth(ScoreMatchLabelWidth);
        
        int ClearFieldWidth = 30;
        
        Label B50ClearLabel1 = new Label();
        B50ClearLabel1.setPrefWidth(ClearFieldWidth);
        B50ClearLabel1.setText("-");
        Label B50ClearLabel2 = new Label();
        B50ClearLabel2.setPrefWidth(ClearFieldWidth);
        B50ClearLabel2.setText("-");
        Label B50ClearLabel3 = new Label();
        B50ClearLabel3.setPrefWidth(ClearFieldWidth);
        B50ClearLabel3.setText("-");
        Label B50ClearLabel4 = new Label();
        B50ClearLabel4.setPrefWidth(ClearFieldWidth);
        B50ClearLabel4.setText("-");
        Label B50ClearLabel5 = new Label();
        B50ClearLabel5.setPrefWidth(ClearFieldWidth);
        B50ClearLabel5.setText("-");
        Label B50ClearLabel6 = new Label();
        B50ClearLabel6.setPrefWidth(ClearFieldWidth);
        B50ClearLabel6.setText("-");
        Label B50ClearLabel7 = new Label();
        B50ClearLabel7.setPrefWidth(ClearFieldWidth);
        B50ClearLabel7.setText("-");
        Label B50ClearLabel8 = new Label();
        B50ClearLabel8.setPrefWidth(ClearFieldWidth);
        B50ClearLabel8.setText("-");
        Label B50ClearLabel9 = new Label();
        B50ClearLabel9.setPrefWidth(ClearFieldWidth);
        B50ClearLabel9.setText("-");
        Label B50ClearLabel10 = new Label();
        B50ClearLabel10.setPrefWidth(ClearFieldWidth);
        B50ClearLabel10.setText("-");

        int ApplyButtonWidth = 60;


        Button ApplyButton1 = new Button("저장");
        ApplyButton1.setPrefWidth(ApplyButtonWidth);
        ApplyButton1.getStyleClass().add("Buttons");
        ApplyButton1.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(0).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(0).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(0).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(0).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                if (RecievedSongs.size() == 0) {
                    throw new IndexOutOfBoundsException();
                }
                B50song = RecievedSongs.get(0);
                try {
                    int Score = B50Score.get(0);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton2 = new Button("저장");
        ApplyButton2.setPrefWidth(ApplyButtonWidth);
        ApplyButton2.getStyleClass().add("Buttons");
        ApplyButton2.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(1).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(1).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(1).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(1).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(1);
                try {
                    int Score = B50Score.get(1);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton3 = new Button("저장");
        ApplyButton3.setPrefWidth(ApplyButtonWidth);
        ApplyButton3.getStyleClass().add("Buttons");
        ApplyButton3.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(2).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(2).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(2).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(2).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(2);
                try {
                    int Score = B50Score.get(2);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (B50song == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton4 = new Button("저장");
        ApplyButton4.setPrefWidth(ApplyButtonWidth);
        ApplyButton4.getStyleClass().add("Buttons");
        ApplyButton4.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(3).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(3).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(3).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(3).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(3);
                try {
                    int Score = B50Score.get(3);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton5 = new Button("저장");
        ApplyButton5.setPrefWidth(ApplyButtonWidth);
        ApplyButton5.getStyleClass().add("Buttons");
        ApplyButton5.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(4).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(4).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(4).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(4).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(4);
                try {
                    int Score = B50Score.get(4);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton6 = new Button("저장");
        ApplyButton6.setPrefWidth(ApplyButtonWidth);
        ApplyButton6.getStyleClass().add("Buttons");
        ApplyButton6.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(5).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(5).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(5).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(5).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(5);
                try {
                    int Score = B50Score.get(5);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton7 = new Button("저장");
        ApplyButton7.setPrefWidth(ApplyButtonWidth);
        ApplyButton7.getStyleClass().add("Buttons");
        ApplyButton7.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(6).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(6).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(6).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(6).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(6);
                try {
                    int Score = B50Score.get(6);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton8 = new Button("저장");
        ApplyButton8.setPrefWidth(ApplyButtonWidth);
        ApplyButton8.getStyleClass().add("Buttons");
        ApplyButton8.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(7).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(7).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(7).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(7).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(7);
                try {
                    int Score = B50Score.get(7);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton9 = new Button("저장");
        ApplyButton9.setPrefWidth(ApplyButtonWidth);
        ApplyButton9.getStyleClass().add("Buttons");
        ApplyButton9.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(8).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(8).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(8).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(8).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(8);
                try {
                    int Score = B50Score.get(8);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        Button ApplyButton10 = new Button("저장");
        ApplyButton10.setPrefWidth(ApplyButtonWidth);
        ApplyButton10.getStyleClass().add("Buttons");
        ApplyButton10.setOnAction(a -> {
            List<ScoreData> B50List = new ArrayList<ScoreData>();
            String ClearStatus = "Track Complete";
            Boolean isCleared = true;
            
            int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
            try {
                if (B50Cleared.get(9).equals("P")) {
                    ClearStatus = "Pure Memory";
                    isCleared = true;
                }
                else if (B50Cleared.get(9).equals("F")) {
                    ClearStatus = "Full Recall";
                    isCleared = true;
                }
                else if (B50Cleared.get(9).equals("C")) {
                    ClearStatus = "Track Complete";
                    isCleared = true;
                }
                else if (B50Cleared.get(9).equals("L")) {
                    ClearStatus = "Track Lost";
                    isCleared = false;
                }
                B50song = RecievedSongs.get(9);
                try {
                    int Score = B50Score.get(9);
                    if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                        throw new IndexOutOfBoundsException();
                    }
                    double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                    B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                }
                catch (IndexOutOfBoundsException IOOBE_Score) {
                    //System.out.println("점수 범위 초과");
                    if (RecievedSong == null) {
                        showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                    }
                    else {
                        showErrorMessage("점수 확인", String.format("유효하지 않은 점수입니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                    }
                }
                System.out.println(B50DiffList + " " + B50Diff);
            }
            catch (IndexOutOfBoundsException IOOBE_Title) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
        });
        
        int Grid2Widths = 150;
        
        TextField InputField = new TextField();
        InputField.setPrefWidth(Grid2Widths);
        InputField.setPromptText("붙여넣기");
        
        InputField.requestFocus();
        
        AOInputError.setPrefWidth(Grid2Widths);
        
        B50Logic B50_IF = new B50Logic();
        B50Sels.selectedToggleProperty().addListener(a -> {
            if (B50SelPst.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("0");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelPrs.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("1");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelFtr.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("2");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelEtr.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("3");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelByd.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("4");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }

            B50Title.clear();
            B50Score.clear();
            B50ScoreStr.clear();
            B50Artist.clear();
            B50Cleared.clear();
            B50ClearedBool.clear();
            RecievedSongs.clear();

            try {
                if (InputField.getText().isEmpty()) {
                    AOInputError.setText("");
                    B50Title.clear();
                    B50Score.clear();
                    B50ScoreStr.clear();
                    B50Artist.clear();
                    B50Cleared.clear();
                    B50ClearedBool.clear();
                    RecievedSongs.clear();
                    RecievedSong = null;
                    
                    TitleMatchLabel1.setText("[난이도] 제목");
                    TitleMatchLabel2.setText("[난이도] 제목");
                    TitleMatchLabel3.setText("[난이도] 제목");
                    TitleMatchLabel4.setText("[난이도] 제목");
                    TitleMatchLabel5.setText("[난이도] 제목");
                    TitleMatchLabel6.setText("[난이도] 제목");
                    TitleMatchLabel7.setText("[난이도] 제목");
                    TitleMatchLabel8.setText("[난이도] 제목");
                    TitleMatchLabel9.setText("[난이도] 제목");
                    TitleMatchLabel10.setText("[난이도] 제목");
                    ScoreMatchLabel1.setText("점수");
                    ScoreMatchLabel2.setText("점수");
                    ScoreMatchLabel3.setText("점수");
                    ScoreMatchLabel4.setText("점수");
                    ScoreMatchLabel5.setText("점수");
                    ScoreMatchLabel6.setText("점수");
                    ScoreMatchLabel7.setText("점수");
                    ScoreMatchLabel8.setText("점수");
                    ScoreMatchLabel9.setText("점수");
                    ScoreMatchLabel10.setText("점수");
                    B50ClearLabel1.setText("-");
                    B50ClearLabel2.setText("-");
                    B50ClearLabel3.setText("-");
                    B50ClearLabel4.setText("-");
                    B50ClearLabel5.setText("-");
                    B50ClearLabel6.setText("-");
                    B50ClearLabel7.setText("-");
                    B50ClearLabel8.setText("-");
                    B50ClearLabel9.setText("-");
                    B50ClearLabel10.setText("-");
                }
                else if (InputField.getText().substring(0, 8).equals("logo-arc")) {
                    AOInputError.setText("");
                    B50Logic.B50LogicMain(InputField.getText());
                    B50Title = B50_IF.getTitles();
                    B50Score = B50_IF.getScore();
                    B50ScoreStr = B50_IF.getScoreStr();
                    B50Artist = B50_IF.getArtist();
                    B50Cleared = B50_IF.getCleared();
                    B50ClearedBool = B50_IF.getClearedBool();
                    RecievedSongs.clear();

                    Boolean notChecked = true;
                    
                    for (int i = 0; i < 10; i++) {
                        String Title = B50Title.get(i);
                        String ScoreStr = B50ScoreStr.get(i);
                        String Artist = B50Artist.get(i);
                        String Cleared = B50Cleared.get(i);
                        Boolean Found = false;
                        RecievedSong = null;

                        if (Title.equals("Quon") && B50Artist.get(i).contains("DJ Noriken")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Quon (WACCA)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception e) {
                                System.out.println("Quon (WACCA) 검색 실패");
                            }
                        }
                        else if (Title.equals("Quon") && B50Artist.get(i).contains("Feryquitous")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Quon (Lanota)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Quon (Lanota) 검색 실패");
                            }
                        }
                        else if (Title.equals("Genesis") && B50Artist.get(i).contains("Iris")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Genesis (Arcaea)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Genesis (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("Genesis") && B50Artist.get(i).contains("Morrigan feat.Lily")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Genesis (Chunithm)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("Genesis (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("Renegade") && B50Artist.get(i).contains("sky_delta")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Renegade (Arcaea)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Renegade (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("Renegade") && B50Artist.get(i).contains("Jason Walsh, Substantial, X. ARI")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Renegade (Arknights)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("Renegade (Arknights) 검색 실패");
                            }
                        }
                        else if (Title.equals("(II)")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "(II)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("͟͝͞Ⅱ́̕ (II) 검색 실패");
                            }
                        }
                        else if (Title.equals("⊥⊬")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "⊥⊬ (Sacrosanct)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("⊥⊬ (Sacrosanct) 검색 실패");
                            }
                        }
                        else {
                            if (notChecked) {
                                for (Songs S : Songs.getSongsList()) {
                                    if ((S.getTitle(B50Diff).equals(Title))) {
                                        Found = false;
                                        if(S.getConst(B50Diff) > 0) {
                                            RecievedSongs.add(S);
                                            B50Cleared.add(Cleared);
                                            Found = true;
                                        }
                                    }
                                }
                                if (Found == false && RecievedSongs.isEmpty()) {
                                    showErrorMessage("난이도 확인", "7 난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    RecievedSongs.clear();
                                    System.out.println(RecievedSongs);
                                    notChecked = false;
                                    break;
                                }
                            }
                        }
                        System.out.println();
                        System.out.println(B50Diff);
                        System.out.println(Title);
                        System.out.println(ScoreStr);
                        System.out.println(Artist);
                        System.out.println(Cleared);
                        System.out.println();
                        System.out.println(RecievedSongs);
                    }

                    for (Songs S : RecievedSongs) {
                        if (S.getConst(B50Diff) == 0) {
                            showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                            B50DiffList.clear();
                            if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                B50SelPst.setSelected(true);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("0");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("PST");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(true);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("1");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("PRS");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(true);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("2");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("FTR");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(true);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("3");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("ETR");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(true);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("4");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("BYD");
                            }
                            RecievedSongs.clear();
                            System.out.println(RecievedSongs);
                        }
                    }

                    if (0 < RecievedSongs.size()) {
                        if (!B50Title.get(0).equals("APC_NO_TITLE") && !B50Artist.get(0).equals("N")) {
                            TitleMatchLabel1.setText(String.format("[%s %s] %s", RecievedSongs.get(0).getDiff(B50Diff), RecievedSongs.get(0).getLevel(B50Diff), RecievedSongs.get(0).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel1.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(0) > 0) {
                            ScoreMatchLabel1.setText(String.valueOf(B50ScoreStr.get(0)));
                        }
                        else {
                            ScoreMatchLabel1.setText("점수");
                        }
                        if (!B50Cleared.get(0).equals("N")) {
                            B50ClearLabel1.setText(B50Cleared.get(0));
                        }
                        else {
                            B50ClearLabel1.setText("-");
                        }
                    }
                    if (1 < RecievedSongs.size()) {
                        if (!B50Title.get(1).equals("APC_NO_TITLE") && !B50Artist.get(1).equals("N")) {
                            TitleMatchLabel2.setText(String.format("[%s %s] %s", RecievedSongs.get(1).getDiff(B50Diff), RecievedSongs.get(1).getLevel(B50Diff), RecievedSongs.get(1).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel2.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(1) > 0) {
                            ScoreMatchLabel2.setText(String.valueOf(B50ScoreStr.get(1)));
                        }
                        else {
                            ScoreMatchLabel2.setText("점수");
                        }
                        if (!B50Cleared.get(1).equals("N")) {
                            B50ClearLabel2.setText(B50Cleared.get(1));
                        }
                        else {
                            B50ClearLabel2.setText("-");
                        }
                    }
                    if (2 < RecievedSongs.size()) {
                        if (!B50Title.get(2).equals("APC_NO_TITLE") && !B50Artist.get(2).equals("N")) {
                            TitleMatchLabel3.setText(String.format("[%s %s] %s", RecievedSongs.get(2).getDiff(B50Diff), RecievedSongs.get(2).getLevel(B50Diff), RecievedSongs.get(2).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel3.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(2) > 0) {
                            ScoreMatchLabel3.setText(String.valueOf(B50ScoreStr.get(2)));
                        }
                        else {
                            ScoreMatchLabel3.setText("점수");
                        }
                        if (!B50Cleared.get(2).equals("N")) {
                            B50ClearLabel3.setText(B50Cleared.get(2));
                        }
                        else {
                            B50ClearLabel3.setText("-");
                        }
                    }
                    if (3 < RecievedSongs.size()) {
                        if (!B50Title.get(3).equals("APC_NO_TITLE") && !B50Artist.get(3).equals("N")) {
                            TitleMatchLabel4.setText(String.format("[%s %s] %s", RecievedSongs.get(3).getDiff(B50Diff), RecievedSongs.get(3).getLevel(B50Diff), RecievedSongs.get(3).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel4.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(3) > 0) {
                            ScoreMatchLabel4.setText(String.valueOf(B50ScoreStr.get(3)));
                        }
                        else {
                            ScoreMatchLabel4.setText("점수");
                        }
                        if (!B50Cleared.get(3).equals("N")) {
                            B50ClearLabel4.setText(B50Cleared.get(3));
                        }
                        else {
                            B50ClearLabel4.setText("-");
                        }
                    }
                    if (4 < RecievedSongs.size()) {
                        if (!B50Title.get(4).equals("APC_NO_TITLE") && !B50Artist.get(4).equals("N")) {
                            TitleMatchLabel5.setText(String.format("[%s %s] %s", RecievedSongs.get(4).getDiff(B50Diff), RecievedSongs.get(4).getLevel(B50Diff), RecievedSongs.get(4).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel5.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(4) > 0) {
                            ScoreMatchLabel5.setText(String.valueOf(B50ScoreStr.get(4)));
                        }
                        else {
                            ScoreMatchLabel5.setText("점수");
                        }
                        if (!B50Cleared.get(4).equals("N")) {
                            B50ClearLabel5.setText(B50Cleared.get(4));
                        }
                        else {
                            B50ClearLabel5.setText("-");
                        }
                    }
                    if (5 < RecievedSongs.size()) {
                        if (!B50Title.get(5).equals("APC_NO_TITLE") && !B50Artist.get(5).equals("N")) {
                            TitleMatchLabel6.setText(String.format("[%s %s] %s", RecievedSongs.get(5).getDiff(B50Diff), RecievedSongs.get(5).getLevel(B50Diff), RecievedSongs.get(5).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel6.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(5) > 0) {
                            ScoreMatchLabel6.setText(String.valueOf(B50ScoreStr.get(5)));
                        }
                        else {
                            ScoreMatchLabel6.setText("점수");
                        }
                        if (!B50Cleared.get(5).equals("N")) {
                            B50ClearLabel6.setText(B50Cleared.get(5));
                        }
                        else {
                            B50ClearLabel6.setText("-");
                        }
                    }
                    if (6 < RecievedSongs.size()) {
                        if (!B50Title.get(6).equals("APC_NO_TITLE") && !B50Artist.get(6).equals("N")) {
                            TitleMatchLabel7.setText(String.format("[%s %s] %s", RecievedSongs.get(6).getDiff(B50Diff), RecievedSongs.get(6).getLevel(B50Diff), RecievedSongs.get(6).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel7.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(6) > 0) {
                            ScoreMatchLabel7.setText(String.valueOf(B50ScoreStr.get(6)));
                        }
                        else {
                            ScoreMatchLabel7.setText("점수");
                        }
                        if (!B50Cleared.get(6).equals("N")) {
                            B50ClearLabel7.setText(B50Cleared.get(6));
                        }
                        else {
                            B50ClearLabel7.setText("-");
                        }
                    }
                    if (7 < RecievedSongs.size()) {
                        if (!B50Title.get(7).equals("APC_NO_TITLE") && !B50Artist.get(7).equals("N")) {
                            TitleMatchLabel8.setText(String.format("[%s %s] %s", RecievedSongs.get(7).getDiff(B50Diff), RecievedSongs.get(7).getLevel(B50Diff), RecievedSongs.get(7).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel8.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(7) > 0) {
                            ScoreMatchLabel8.setText(String.valueOf(B50ScoreStr.get(7)));
                        }
                        else {
                            ScoreMatchLabel8.setText("점수");
                        }
                        if (!B50Cleared.get(7).equals("N")) {
                            B50ClearLabel8.setText(B50Cleared.get(7));
                        }
                        else {
                            B50ClearLabel8.setText("-");
                        }
                    }
                    if (8 < RecievedSongs.size()) {
                        if (!B50Title.get(8).equals("APC_NO_TITLE") && !B50Artist.get(8).equals("N")) {
                            TitleMatchLabel9.setText(String.format("[%s %s] %s", RecievedSongs.get(8).getDiff(B50Diff), RecievedSongs.get(8).getLevel(B50Diff), RecievedSongs.get(8).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel9.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(8) > 0) {
                            ScoreMatchLabel9.setText(String.valueOf(B50ScoreStr.get(8)));
                        }
                        else {
                            ScoreMatchLabel9.setText("점수");
                        }
                        if (!B50Cleared.get(8).equals("N")) {
                            B50ClearLabel9.setText(B50Cleared.get(8));
                        }
                        else {
                            B50ClearLabel9.setText("-");
                        }
                    }
                    if (9 < RecievedSongs.size()) {
                        if (!RecievedSongs.get(9).getTitle(B50Diff).equals("APC_NO_TITLE") && !B50Artist.get(9).equals("N")) {
                            TitleMatchLabel10.setText(String.format("[%s %s] %s", RecievedSongs.get(9).getDiff(B50Diff), RecievedSongs.get(9).getLevel(B50Diff), RecievedSongs.get(9).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel10.setText("[난이도] 제목");
                        }
                        if (B50Score.get(9) > 0) {
                            ScoreMatchLabel10.setText(String.valueOf(B50ScoreStr.get(9)));
                        }
                        else {
                            ScoreMatchLabel10.setText("점수");
                        }
                        if (!B50Cleared.get(9).equals("N")) {
                            B50ClearLabel10.setText(B50Cleared.get(9));
                        }
                        else {
                            B50ClearLabel10.setText("-");
                        }
                    }
                }
                else {
                    System.out.println("Something is empty");
                    showErrorMessage("입력 확인", String.format("유효하지 않은 정보가 포함되어 있습니다."), "확인", 340, 105);
                    RecievedSongs.clear();
                }
            }
            catch (IndexOutOfBoundsException e) {
                if (InputField.getText().isEmpty()) {
                    AOInputError.setText("");
                }
                else {
                    AOInputError.setText(String.format("유효하지 않은 입력입니다.%n%n입력 문자열은%n\'logo-arc\'로 시작,%n\'© [연도] lowirov.[버전]\'%n으로 완료되어야 합니다."));
                    RecievedSongs.clear();
                    System.out.println("IndexOutOfBoundsException");
                    TitleMatchLabel1.setText("[난이도] 제목");
                    TitleMatchLabel2.setText("[난이도] 제목");
                    TitleMatchLabel3.setText("[난이도] 제목");
                    TitleMatchLabel4.setText("[난이도] 제목");
                    TitleMatchLabel5.setText("[난이도] 제목");
                    TitleMatchLabel6.setText("[난이도] 제목");
                    TitleMatchLabel7.setText("[난이도] 제목");
                    TitleMatchLabel8.setText("[난이도] 제목");
                    TitleMatchLabel9.setText("[난이도] 제목");
                    TitleMatchLabel10.setText("[난이도] 제목");
                    ScoreMatchLabel1.setText("점수");
                    ScoreMatchLabel2.setText("점수");
                    ScoreMatchLabel3.setText("점수");
                    ScoreMatchLabel4.setText("점수");
                    ScoreMatchLabel5.setText("점수");
                    ScoreMatchLabel6.setText("점수");
                    ScoreMatchLabel7.setText("점수");
                    ScoreMatchLabel8.setText("점수");
                    ScoreMatchLabel9.setText("점수");
                    ScoreMatchLabel10.setText("점수");
                    B50ClearLabel1.setText("-");
                    B50ClearLabel2.setText("-");
                    B50ClearLabel3.setText("-");
                    B50ClearLabel4.setText("-");
                    B50ClearLabel5.setText("-");
                    B50ClearLabel6.setText("-");
                    B50ClearLabel7.setText("-");
                    B50ClearLabel8.setText("-");
                    B50ClearLabel9.setText("-");
                    B50ClearLabel10.setText("-");
                }
            }
            catch (Exception f) {
                AOInputError.setText(String.format("유효하지 않은 입력입니다.%n%n입력 문자열은%n\'logo-arc\'로 시작,%n\'© [연도] lowirov.[버전]\'%n으로 완료되어야 합니다."));
                System.out.println("Exception but not IndexOutOfBoundsException");
                RecievedSongs.clear();
            }
        });
        
        InputField.textProperty().addListener((obs, old, val) -> {
            if (B50SelPst.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("0");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelPrs.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("1");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelFtr.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("2");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelEtr.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("3");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }
            else if (B50SelByd.isSelected()) {
                B50DiffList.clear();
                B50DiffList.add("4");
                B50Diff = Integer.parseInt(B50DiffList.get(0));
                System.out.println(B50Diff);
            }

            B50Title.clear();
            B50Score.clear();
            B50ScoreStr.clear();
            B50Artist.clear();
            B50Cleared.clear();
            B50ClearedBool.clear();
            RecievedSongs.clear();

            try {
                if (InputField.getText().isEmpty()) {
                    AOInputError.setText("");
                    B50Title.clear();
                    B50Score.clear();
                    B50ScoreStr.clear();
                    B50Artist.clear();
                    B50Cleared.clear();
                    B50ClearedBool.clear();
                    RecievedSongs.clear();
                    RecievedSong = null;
                    
                    TitleMatchLabel1.setText("[난이도] 제목");
                    TitleMatchLabel2.setText("[난이도] 제목");
                    TitleMatchLabel3.setText("[난이도] 제목");
                    TitleMatchLabel4.setText("[난이도] 제목");
                    TitleMatchLabel5.setText("[난이도] 제목");
                    TitleMatchLabel6.setText("[난이도] 제목");
                    TitleMatchLabel7.setText("[난이도] 제목");
                    TitleMatchLabel8.setText("[난이도] 제목");
                    TitleMatchLabel9.setText("[난이도] 제목");
                    TitleMatchLabel10.setText("[난이도] 제목");
                    ScoreMatchLabel1.setText("점수");
                    ScoreMatchLabel2.setText("점수");
                    ScoreMatchLabel3.setText("점수");
                    ScoreMatchLabel4.setText("점수");
                    ScoreMatchLabel5.setText("점수");
                    ScoreMatchLabel6.setText("점수");
                    ScoreMatchLabel7.setText("점수");
                    ScoreMatchLabel8.setText("점수");
                    ScoreMatchLabel9.setText("점수");
                    ScoreMatchLabel10.setText("점수");
                    B50ClearLabel1.setText("-");
                    B50ClearLabel2.setText("-");
                    B50ClearLabel3.setText("-");
                    B50ClearLabel4.setText("-");
                    B50ClearLabel5.setText("-");
                    B50ClearLabel6.setText("-");
                    B50ClearLabel7.setText("-");
                    B50ClearLabel8.setText("-");
                    B50ClearLabel9.setText("-");
                    B50ClearLabel10.setText("-");
                }
                else if (InputField.getText().substring(0, 8).equals("logo-arc")) {
                    AOInputError.setText("");
                    B50Logic.B50LogicMain(InputField.getText());
                    B50Title = B50_IF.getTitles();
                    B50Score = B50_IF.getScore();
                    B50ScoreStr = B50_IF.getScoreStr();
                    B50Artist = B50_IF.getArtist();
                    B50Cleared = B50_IF.getCleared();
                    B50ClearedBool = B50_IF.getClearedBool();
                    RecievedSongs.clear();

                    Boolean notChecked = true;
                    
                    for (int i = 0; i < 10; i++) {
                        String Title = B50Title.get(i);
                        String ScoreStr = B50ScoreStr.get(i);
                        String Artist = B50Artist.get(i);
                        String Cleared = B50Cleared.get(i);
                        Boolean Found = false;
                        RecievedSong = null;

                        if (Title.equals("Quon") && B50Artist.get(i).contains("DJ Noriken")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Quon (WACCA)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception e) {
                                System.out.println("Quon (WACCA) 검색 실패");
                            }
                        }
                        else if (Title.equals("Quon") && B50Artist.get(i).contains("Feryquitous")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Quon (Lanota)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Quon (Lanota) 검색 실패");
                            }
                        }
                        else if (Title.equals("Genesis") && B50Artist.get(i).contains("Iris")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Genesis (Arcaea)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Genesis (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("Genesis") && B50Artist.get(i).contains("Morrigan feat.Lily")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Genesis (Chunithm)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("Genesis (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("(II)")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "(II)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("͟͝͞Ⅱ́̕ (II) 검색 실패");
                            }
                        }
                        else if (Title.equals("⊥⊬")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "⊥⊬ (Sacrosanct)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("⊥⊬ (Sacrosanct) 검색 실패");
                            }
                        }
                        else if (Title.equals("Renegade") && B50Artist.get(i).contains("sky_delta")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Renegade (Arcaea)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            catch (Exception E) {
                                System.out.println("Renegade (Arcaea) 검색 실패");
                            }
                        }
                        else if (Title.equals("Renegade") && B50Artist.get(i).contains("Jason Walsh, Substantial, X. ARI")) {
                            try {
                                RecievedSong = B50AutoSearchedSongs(B50DiffList, "Renegade (Arknights)").get(0).getSongInfo();
                                if (RecievedSong.getConst(B50Diff) > 0) {
                                    RecievedSongs.add(RecievedSong);
                                    B50Cleared.add(Cleared);
                                }
                                else {
                                    showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    break;
                                }
                            }
                            
                            catch (Exception E) {
                                System.out.println("Renegade (Arknights) 검색 실패");
                            }
                        }
                        else {
                            if (notChecked) {
                                for (Songs S : Songs.getSongsList()) {
                                    if (S.getTitle(B50Diff).equals(Title)) {
                                        Found = false;
                                        if(S.getConst(B50Diff) > 0) {
                                            RecievedSongs.add(S);
                                            B50Cleared.add(Cleared);
                                            Found = true;
                                        }
                                    }
                                }
                                if (Found == false && RecievedSongs.isEmpty()) {
                                    showErrorMessage("난이도 확인", "15 난이도가 일치하지 않습니다.", "확인", 270, 105);
                                    B50DiffList.clear();
                                    if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                        B50SelPst.setSelected(true);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("0");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PST");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(true);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("1");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("PRS");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(true);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("2");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("FTR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(true);
                                        B50SelByd.setSelected(false);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("3");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("ETR");
                                    }
                                    else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                        B50SelPst.setSelected(false);
                                        B50SelPrs.setSelected(false);
                                        B50SelFtr.setSelected(false);
                                        B50SelEtr.setSelected(false);
                                        B50SelByd.setSelected(true);
                                        if (B50DiffList != null) {
                                            B50DiffList.clear();
                                        }
                                        B50DiffList.add("4");
                                        B50Diff = Integer.parseInt(B50DiffList.get(0));
                                        B50DiffSelect.setText("BYD");
                                    }
                                    RecievedSongs.clear();
                                    System.out.println(RecievedSongs);
                                    notChecked = false;
                                    break;
                                }
                            }
                        }
                        System.out.println();
                        System.out.println(B50Diff);
                        System.out.println(Title);
                        System.out.println(ScoreStr);
                        System.out.println(Artist);
                        System.out.println(Cleared);
                        System.out.println();
                        System.out.println(RecievedSongs);
                    }

                    for (Songs S : RecievedSongs) {
                        if (S.getConst(B50Diff) == 0) {
                            showErrorMessage("난이도 확인", "난이도가 일치하지 않습니다.", "확인", 270, 105);
                            B50DiffList.clear();
                            if (settings.get("B50InputDifficultyDefault", "FTR").equals("PST")) {
                                B50SelPst.setSelected(true);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("0");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("PST");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("PRS")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(true);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("1");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("PRS");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("FTR")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(true);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("2");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("FTR");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("ETR")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(true);
                                B50SelByd.setSelected(false);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("3");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("ETR");
                            }
                            else if (settings.get("B50InputDifficultyDefault", "FTR").equals("BYD")) {
                                B50SelPst.setSelected(false);
                                B50SelPrs.setSelected(false);
                                B50SelFtr.setSelected(false);
                                B50SelEtr.setSelected(false);
                                B50SelByd.setSelected(true);
                                if (B50DiffList != null) {
                                    B50DiffList.clear();
                                }
                                B50DiffList.add("4");
                                B50Diff = Integer.parseInt(B50DiffList.get(0));
                                B50DiffSelect.setText("BYD");
                            }
                            RecievedSongs.clear();
                            System.out.println(RecievedSongs);
                        }
                    }

                    if (0 < RecievedSongs.size()) {
                        if (!B50Title.get(0).equals("APC_NO_TITLE") && !B50Artist.get(0).equals("N")) {
                            TitleMatchLabel1.setText(String.format("[%s %s] %s", RecievedSongs.get(0).getDiff(B50Diff), RecievedSongs.get(0).getLevel(B50Diff), RecievedSongs.get(0).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel1.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(0) > 0) {
                            ScoreMatchLabel1.setText(String.valueOf(B50ScoreStr.get(0)));
                        }
                        else {
                            ScoreMatchLabel1.setText("점수");
                        }
                        if (!B50Cleared.get(0).equals("N")) {
                            B50ClearLabel1.setText(B50Cleared.get(0));
                        }
                        else {
                            B50ClearLabel1.setText("-");
                        }
                    }
                    if (1 < RecievedSongs.size()) {
                        if (!B50Title.get(1).equals("APC_NO_TITLE") && !B50Artist.get(1).equals("N")) {
                            TitleMatchLabel2.setText(String.format("[%s %s] %s", RecievedSongs.get(1).getDiff(B50Diff), RecievedSongs.get(1).getLevel(B50Diff), RecievedSongs.get(1).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel2.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(1) > 0) {
                            ScoreMatchLabel2.setText(String.valueOf(B50ScoreStr.get(1)));
                        }
                        else {
                            ScoreMatchLabel2.setText("점수");
                        }
                        if (!B50Cleared.get(1).equals("N")) {
                            B50ClearLabel2.setText(B50Cleared.get(1));
                        }
                        else {
                            B50ClearLabel2.setText("-");
                        }
                    }
                    if (2 < RecievedSongs.size()) {
                        if (!B50Title.get(2).equals("APC_NO_TITLE") && !B50Artist.get(2).equals("N")) {
                            TitleMatchLabel3.setText(String.format("[%s %s] %s", RecievedSongs.get(2).getDiff(B50Diff), RecievedSongs.get(2).getLevel(B50Diff), RecievedSongs.get(2).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel3.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(2) > 0) {
                            ScoreMatchLabel3.setText(String.valueOf(B50ScoreStr.get(2)));
                        }
                        else {
                            ScoreMatchLabel3.setText("점수");
                        }
                        if (!B50Cleared.get(2).equals("N")) {
                            B50ClearLabel3.setText(B50Cleared.get(2));
                        }
                        else {
                            B50ClearLabel3.setText("-");
                        }
                    }
                    if (3 < RecievedSongs.size()) {
                        if (!B50Title.get(3).equals("APC_NO_TITLE") && !B50Artist.get(3).equals("N")) {
                            TitleMatchLabel4.setText(String.format("[%s %s] %s", RecievedSongs.get(3).getDiff(B50Diff), RecievedSongs.get(3).getLevel(B50Diff), RecievedSongs.get(3).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel4.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(3) > 0) {
                            ScoreMatchLabel4.setText(String.valueOf(B50ScoreStr.get(3)));
                        }
                        else {
                            ScoreMatchLabel4.setText("점수");
                        }
                        if (!B50Cleared.get(3).equals("N")) {
                            B50ClearLabel4.setText(B50Cleared.get(3));
                        }
                        else {
                            B50ClearLabel4.setText("-");
                        }
                    }
                    if (4 < RecievedSongs.size()) {
                        if (!B50Title.get(4).equals("APC_NO_TITLE") && !B50Artist.get(4).equals("N")) {
                            TitleMatchLabel5.setText(String.format("[%s %s] %s", RecievedSongs.get(4).getDiff(B50Diff), RecievedSongs.get(4).getLevel(B50Diff), RecievedSongs.get(4).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel5.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(4) > 0) {
                            ScoreMatchLabel5.setText(String.valueOf(B50ScoreStr.get(4)));
                        }
                        else {
                            ScoreMatchLabel5.setText("점수");
                        }
                        if (!B50Cleared.get(4).equals("N")) {
                            B50ClearLabel5.setText(B50Cleared.get(4));
                        }
                        else {
                            B50ClearLabel5.setText("-");
                        }
                    }
                    if (5 < RecievedSongs.size()) {
                        if (!B50Title.get(5).equals("APC_NO_TITLE") && !B50Artist.get(5).equals("N")) {
                            TitleMatchLabel6.setText(String.format("[%s %s] %s", RecievedSongs.get(5).getDiff(B50Diff), RecievedSongs.get(5).getLevel(B50Diff), RecievedSongs.get(5).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel6.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(5) > 0) {
                            ScoreMatchLabel6.setText(String.valueOf(B50ScoreStr.get(5)));
                        }
                        else {
                            ScoreMatchLabel6.setText("점수");
                        }
                        if (!B50Cleared.get(5).equals("N")) {
                            B50ClearLabel6.setText(B50Cleared.get(5));
                        }
                        else {
                            B50ClearLabel6.setText("-");
                        }
                    }
                    if (6 < RecievedSongs.size()) {
                        if (!B50Title.get(6).equals("APC_NO_TITLE") && !B50Artist.get(6).equals("N")) {
                            TitleMatchLabel7.setText(String.format("[%s %s] %s", RecievedSongs.get(6).getDiff(B50Diff), RecievedSongs.get(6).getLevel(B50Diff), RecievedSongs.get(6).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel7.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(6) > 0) {
                            ScoreMatchLabel7.setText(String.valueOf(B50ScoreStr.get(6)));
                        }
                        else {
                            ScoreMatchLabel7.setText("점수");
                        }
                        if (!B50Cleared.get(6).equals("N")) {
                            B50ClearLabel7.setText(B50Cleared.get(6));
                        }
                        else {
                            B50ClearLabel7.setText("-");
                        }
                    }
                    if (7 < RecievedSongs.size()) {
                        if (!B50Title.get(7).equals("APC_NO_TITLE") && !B50Artist.get(7).equals("N")) {
                            TitleMatchLabel8.setText(String.format("[%s %s] %s", RecievedSongs.get(7).getDiff(B50Diff), RecievedSongs.get(7).getLevel(B50Diff), RecievedSongs.get(7).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel8.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(7) > 0) {
                            ScoreMatchLabel8.setText(String.valueOf(B50ScoreStr.get(7)));
                        }
                        else {
                            ScoreMatchLabel8.setText("점수");
                        }
                        if (!B50Cleared.get(7).equals("N")) {
                            B50ClearLabel8.setText(B50Cleared.get(7));
                        }
                        else {
                            B50ClearLabel8.setText("-");
                        }
                    }
                    if (8 < RecievedSongs.size()) {
                        if (!B50Title.get(8).equals("APC_NO_TITLE") && !B50Artist.get(8).equals("N")) {
                            TitleMatchLabel9.setText(String.format("[%s %s] %s", RecievedSongs.get(8).getDiff(B50Diff), RecievedSongs.get(8).getLevel(B50Diff), RecievedSongs.get(8).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel9.setText("[난이도] 제목");
                        }   
                        if (B50Score.get(8) > 0) {
                            ScoreMatchLabel9.setText(String.valueOf(B50ScoreStr.get(8)));
                        }
                        else {
                            ScoreMatchLabel9.setText("점수");
                        }
                        if (!B50Cleared.get(8).equals("N")) {
                            B50ClearLabel9.setText(B50Cleared.get(8));
                        }
                        else {
                            B50ClearLabel9.setText("-");
                        }
                    }
                    if (9 < RecievedSongs.size()) {
                        if (!RecievedSongs.get(9).getTitle(B50Diff).equals("APC_NO_TITLE") && !B50Artist.get(9).equals("N")) {
                            TitleMatchLabel10.setText(String.format("[%s %s] %s", RecievedSongs.get(9).getDiff(B50Diff), RecievedSongs.get(9).getLevel(B50Diff), RecievedSongs.get(9).getTitle(B50Diff)));
                        }
                        else if (B50Title.get(0).equals("APC_NO_TITLE")) {
                            TitleMatchLabel10.setText("[난이도] 제목");
                        }
                        if (B50Score.get(9) > 0) {
                            ScoreMatchLabel10.setText(String.valueOf(B50ScoreStr.get(9)));
                        }
                        else {
                            ScoreMatchLabel10.setText("점수");
                        }
                        if (!B50Cleared.get(9).equals("N")) {
                            B50ClearLabel10.setText(B50Cleared.get(9));
                        }
                        else {
                            B50ClearLabel10.setText("-");
                        }
                    }
                }
                else {
                    System.out.println("Something is empty");
                    showErrorMessage("입력 확인", String.format("유효하지 않은 정보가 포함되어 있습니다."), "확인", 340, 105);
                    RecievedSongs.clear();
                }
            }
            catch (IndexOutOfBoundsException e) {
                if (InputField.getText().isEmpty()) {
                    AOInputError.setText("");
                }
                else {
                    AOInputError.setText(String.format("유효하지 않은 입력입니다.%n%n입력 문자열은%n\'logo-arc\'로 시작,%n\'© [연도] lowirov.[버전]\'%n으로 완료되어야 합니다."));
                    RecievedSongs.clear();
                    System.out.println("IndexOutOfBoundsException");
                    TitleMatchLabel1.setText("[난이도] 제목");
                    TitleMatchLabel2.setText("[난이도] 제목");
                    TitleMatchLabel3.setText("[난이도] 제목");
                    TitleMatchLabel4.setText("[난이도] 제목");
                    TitleMatchLabel5.setText("[난이도] 제목");
                    TitleMatchLabel6.setText("[난이도] 제목");
                    TitleMatchLabel7.setText("[난이도] 제목");
                    TitleMatchLabel8.setText("[난이도] 제목");
                    TitleMatchLabel9.setText("[난이도] 제목");
                    TitleMatchLabel10.setText("[난이도] 제목");
                    ScoreMatchLabel1.setText("점수");
                    ScoreMatchLabel2.setText("점수");
                    ScoreMatchLabel3.setText("점수");
                    ScoreMatchLabel4.setText("점수");
                    ScoreMatchLabel5.setText("점수");
                    ScoreMatchLabel6.setText("점수");
                    ScoreMatchLabel7.setText("점수");
                    ScoreMatchLabel8.setText("점수");
                    ScoreMatchLabel9.setText("점수");
                    ScoreMatchLabel10.setText("점수");
                    B50ClearLabel1.setText("-");
                    B50ClearLabel2.setText("-");
                    B50ClearLabel3.setText("-");
                    B50ClearLabel4.setText("-");
                    B50ClearLabel5.setText("-");
                    B50ClearLabel6.setText("-");
                    B50ClearLabel7.setText("-");
                    B50ClearLabel8.setText("-");
                    B50ClearLabel9.setText("-");
                    B50ClearLabel10.setText("-");
                }
            }
            catch (Exception f) {
                AOInputError.setText(String.format("유효하지 않은 입력입니다.%n%n입력 문자열은%n\'logo-arc\'로 시작,%n\'© [연도] lowirov.[버전]\'%n으로 완료되어야 합니다."));
                f.printStackTrace();
                System.out.println("Exception but not IndexOutOfBoundsException");
                RecievedSongs.clear();
            }
        });

        B50Grid1.add(TitleMatchLabel1, 5, 5);
        B50Grid1.add(TitleMatchLabel2, 5, 7);
        B50Grid1.add(TitleMatchLabel3, 5, 9);
        B50Grid1.add(TitleMatchLabel4, 5, 11);
        B50Grid1.add(TitleMatchLabel5, 5, 13);
        B50Grid1.add(TitleMatchLabel6, 5, 15);
        B50Grid1.add(TitleMatchLabel7, 5, 17);
        B50Grid1.add(TitleMatchLabel8, 5, 19);
        B50Grid1.add(TitleMatchLabel9, 5, 21);
        B50Grid1.add(TitleMatchLabel10, 5, 23);

        B50Grid1.add(ScoreMatchLabel1, 6, 5);
        B50Grid1.add(ScoreMatchLabel2, 6, 7);
        B50Grid1.add(ScoreMatchLabel3, 6, 9);
        B50Grid1.add(ScoreMatchLabel4, 6, 11);
        B50Grid1.add(ScoreMatchLabel5, 6, 13);
        B50Grid1.add(ScoreMatchLabel6, 6, 15);
        B50Grid1.add(ScoreMatchLabel7, 6, 17);
        B50Grid1.add(ScoreMatchLabel8, 6, 19);
        B50Grid1.add(ScoreMatchLabel9, 6, 21);
        B50Grid1.add(ScoreMatchLabel10, 6, 23);

        B50Grid1.add(B50ClearLabel1, 7, 5);
        B50Grid1.add(B50ClearLabel2, 7, 7);
        B50Grid1.add(B50ClearLabel3, 7, 9);
        B50Grid1.add(B50ClearLabel4, 7, 11);
        B50Grid1.add(B50ClearLabel5, 7, 13);
        B50Grid1.add(B50ClearLabel6, 7, 15);
        B50Grid1.add(B50ClearLabel7, 7, 17);
        B50Grid1.add(B50ClearLabel8, 7, 19);
        B50Grid1.add(B50ClearLabel9, 7, 21);
        B50Grid1.add(B50ClearLabel10, 7, 23);

        B50Grid1.add(ApplyButton1, 8, 5);
        B50Grid1.add(ApplyButton2, 8, 7);
        B50Grid1.add(ApplyButton3, 8, 9);
        B50Grid1.add(ApplyButton4, 8, 11);
        B50Grid1.add(ApplyButton5, 8, 13);
        B50Grid1.add(ApplyButton6, 8, 15);
        B50Grid1.add(ApplyButton7, 8, 17);
        B50Grid1.add(ApplyButton8, 8, 19);
        B50Grid1.add(ApplyButton9, 8, 21);
        B50Grid1.add(ApplyButton10, 8, 23);

        Label PlayResultLabel1 = new Label("1번 플레이 결과 :");
        PlayResultLabel1.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel1, 5, 4);
        Label PlayResultLabel2 = new Label("2번 플레이 결과 :");
        PlayResultLabel2.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel2, 5, 6);
        Label PlayResultLabel3 = new Label("3번 플레이 결과 :");
        PlayResultLabel3.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel3, 5, 8);
        Label PlayResultLabel4 = new Label("4번 플레이 결과 :");
        PlayResultLabel4.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel4, 5, 10);
        Label PlayResultLabel5 = new Label("5번 플레이 결과 :");
        PlayResultLabel5.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel5, 5, 12);
        Label PlayResultLabel6 = new Label("6번 플레이 결과 :");
        PlayResultLabel6.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel6, 5, 14);
        Label PlayResultLabel7 = new Label("7번 플레이 결과 :");
        PlayResultLabel7.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel7, 5, 16);
        Label PlayResultLabel8 = new Label("8번 플레이 결과 :");
        PlayResultLabel8.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel8, 5, 18);
        Label PlayResultLabel9 = new Label("9번 플레이 결과 :");
        PlayResultLabel9.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel9, 5, 20);
        Label PlayResultLabel10 = new Label("10번 플레이 결과 :");
        PlayResultLabel10.getStyleClass().add("settingsCategorySubTitle");
        B50Grid1.add(PlayResultLabel10, 5, 22);

        B50Grid1.add(new Label(""), 9, 25);

        Button ApplyAllButton = new Button("일괄 저장");
        ApplyAllButton.getStyleClass().add("Buttons");
        ApplyAllButton.setPrefWidth(Grid2Widths);
        ApplyAllButton.setOnAction(a -> {
            Boolean AlertShowed = false;

            if (RecievedSongs.size() == 0 || RecievedSongs == null) {
                showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
            }
            else {
                for (int i = 0; i < RecievedSongs.size(); i++) {
                    List<ScoreData> B50List = new ArrayList<ScoreData>();
                    String ClearStatus = "Track Complete";
                    Boolean isCleared = true;
                    B50song = null;
                    
                    int B50DiffInt = Integer.parseInt(B50DiffList.get(0));
                    try {
                        if (B50Cleared.get(i).equals("P")) {
                            ClearStatus = "Pure Memory";
                            isCleared = true;
                        }
                        else if (B50Cleared.get(i).equals("F")) {
                            ClearStatus = "Full Recall";
                            isCleared = true;
                        }
                        else if (B50Cleared.get(i).equals("C")) {
                            ClearStatus = "Track Complete";
                            isCleared = true;
                        }
                        else if (B50Cleared.get(i).equals("L")) {
                            ClearStatus = "Track Lost";
                            isCleared = false;
                        }
                        if (RecievedSongs.size() == 0 || RecievedSongs == null) {
                            throw new Exception();
                        }
                        System.out.println(RecievedSongs);
                        B50song = RecievedSongs.get(i);
    
                        for (Songs RS : RecievedSongs) {
                            System.out.println(RS);
                        }
                        try {
                            int Score = B50Score.get(i);
                            if (Score < 0 || Score > 10000000 + B50song.getNotes(B50DiffInt)) {
                                throw new IndexOutOfBoundsException();
                            }
                            double Potential = APCLogic.APCLogic_B50(B50song.getConst(B50DiffInt), Score, isCleared);
                            if (B50song.getConst(B50DiffInt) > 0) {
                                B50jsonSetter(B50List, new ScoreData(B50song, B50song.getIndex(B50DiffInt), B50song.getTitle(B50DiffInt), B50song.getDiff(B50DiffInt), B50song.getLevel(B50DiffInt), B50song.getArtist(B50DiffInt), Score, ClearStatus, Potential), B50Location, B50DiffInt);
                            }
                        }
                        catch (Exception Inner) {
                            //System.out.println("점수 범위 초과");
                            if (RecievedSongs.size() == 0 || RecievedSongs == null) {
                                if (!AlertShowed) {
                                    showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 285, 105);
                                    AlertShowed = true;
                                }
                            }
                            else {
                                showErrorMessage("점수 확인", String.format("%d번 플레이 결과의 점수가 유효하지 않습니다." + "%n" + "점수는 %s 이하, 0 이상의 정수여야 합니다.", i, 10000000 + B50song.getNotes(B50DiffInt)), "확인", 395, 120);
                            }
                        }
                        System.out.println(B50DiffList + " " + B50Diff);
                    }
                    catch (Exception Outer) {
                        if (!AlertShowed) {
                            showErrorMessage("입력 확인", String.format("악곡이 입력되지 않았습니다."), "확인", 270, 105);
                            AlertShowed = true;
                        }
                    }
                }
            }
            if (settings.get("AutoClear", "N").equals("Y")) {
                InputField.clear();
            }
        });

        B50DiffSelect.setPrefWidth(Grid2Widths);

        B50Grid2.add(new Label(""), 0, 0);
        B50Grid2.add(InputField, 2, 5);
        B50Grid2.add(B50DiffSelect, 2, 7);
        B50Grid2.add(ApplyAllButton, 2, 9);
        B50Grid2.add(AOInputError, 2, 11);

        B50DiffSelect.getItems().clear();
        B50DiffSelect.getItems().addAll(B50SelPst, B50SelPrs, B50SelFtr, B50SelEtr, B50SelByd);
        B50DiffSelect.setMaxWidth(300);

        ScrollPane B50Scroll = new ScrollPane(B50Grid1);
        B50Scroll.getStyleClass().add("Panes");
        B50Scroll.setPrefWidth(440);
        B50Grid2.setPrefWidth(200);

        B50MainGrid.add(B50Scroll, 0, 0);
        B50MainGrid.add(B50Grid2, 1, 0);

        B50MainGrid.setFocusTraversable(false);
        B50Grid1.setFocusTraversable(false);
        B50Scroll.setFocusTraversable(false);

        return B50MainGrid;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                    Settings                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    public int themeStatus;

    public ScrollPane Settings() {
        PageIndex = 4;

        BackButton.setOpacity(BBActiveOpacity);
        BackButton.setDisable(false);

        
        ToggleGroup CSPM = new ToggleGroup();
        RadioMenuItem SelPMPST = new RadioMenuItem("Pure Memory");
        SelPMPST.setToggleGroup(CSPM);
        RadioMenuItem SelPMPRS = new RadioMenuItem("Pure Memory");
        SelPMPRS.setToggleGroup(CSPM);
        RadioMenuItem SelPMFTR = new RadioMenuItem("Pure Memory");
        SelPMFTR.setToggleGroup(CSPM);
        RadioMenuItem SelPMETR = new RadioMenuItem("Pure Memory");
        SelPMETR.setToggleGroup(CSPM);
        RadioMenuItem SelPMBYD = new RadioMenuItem("Pure Memory");
        SelPMBYD.setToggleGroup(CSPM);
        
        ToggleGroup CSFR = new ToggleGroup();
        RadioMenuItem SelFRPST = new RadioMenuItem("Full Recall");
        SelFRPST.setToggleGroup(CSFR);
        RadioMenuItem SelFRPRS = new RadioMenuItem("Full Recall");
        SelFRPRS.setToggleGroup(CSFR);
        RadioMenuItem SelFRFTR = new RadioMenuItem("Full Recall");
        SelFRFTR.setToggleGroup(CSFR);
        RadioMenuItem SelFRETR = new RadioMenuItem("Full Recall");
        SelFRETR.setToggleGroup(CSFR);
        RadioMenuItem SelFRBYD = new RadioMenuItem("Full Recall");
        SelFRBYD.setToggleGroup(CSFR);
        
        ToggleGroup CSTC = new ToggleGroup();
        RadioMenuItem SelTCPST = new RadioMenuItem("Track Complete");
        SelTCPST.setToggleGroup(CSTC);
        RadioMenuItem SelTCPRS = new RadioMenuItem("Track Complete");
        SelTCPRS.setToggleGroup(CSTC);
        RadioMenuItem SelTCFTR = new RadioMenuItem("Track Complete");
        SelTCFTR.setToggleGroup(CSTC);
        RadioMenuItem SelTCETR = new RadioMenuItem("Track Complete");
        SelTCETR.setToggleGroup(CSTC);
        RadioMenuItem SelTCBYD = new RadioMenuItem("Track Complete");
        SelTCBYD.setToggleGroup(CSTC);
        
        ToggleGroup CSTL = new ToggleGroup();
        RadioMenuItem SelTLPST = new RadioMenuItem("Track Lost");
        SelTLPST.setToggleGroup(CSTL);
        RadioMenuItem SelTLPRS = new RadioMenuItem("Track Lost");
        SelTLPRS.setToggleGroup(CSTL);
        RadioMenuItem SelTLFTR = new RadioMenuItem("Track Lost");
        SelTLFTR.setToggleGroup(CSTL);
        RadioMenuItem SelTLETR = new RadioMenuItem("Track Lost");
        SelTLETR.setToggleGroup(CSTL);
        RadioMenuItem SelTLBYD = new RadioMenuItem("Track Lost");
        SelTLBYD.setToggleGroup(CSTL);
        
        if (settings.get("PSTClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("PSTClearStatus", "Pure Memory");
            SelPMPST.setSelected(true);
            SelFRPST.setSelected(false);
            SelTCPST.setSelected(false);
            SelTLPST.setSelected(false);
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("PSTClearStatus", "Full Recall");
            SelPMPST.setSelected(false);
            SelFRPST.setSelected(true);
            SelTCPST.setSelected(false);
            SelTLPST.setSelected(false);
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("PSTClearStatus", "Track Complete");
            SelPMPST.setSelected(false);
            SelFRPST.setSelected(false);
            SelTCPST.setSelected(true);
            SelTLPST.setSelected(false);
        }
        if (settings.get("PSTClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("PSTClearStatus", "Track Lost");
            SelPMPST.setSelected(false);
            SelFRPST.setSelected(false);
            SelTCPST.setSelected(false);
            SelTLPST.setSelected(true);
        }
        
        if (settings.get("PRSClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("PRSClearStatus", "Pure Memory");
            SelPMPRS.setSelected(true);
            SelFRPRS.setSelected(false);
            SelTCPRS.setSelected(false);
            SelTLPRS.setSelected(false);
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("PRSClearStatus", "Full Recall");
            SelPMPRS.setSelected(false);
            SelFRPRS.setSelected(true);
            SelTCPRS.setSelected(false);
            SelTLPRS.setSelected(false);
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("PRSClearStatus", "Track Complete");
            SelPMPRS.setSelected(false);
            SelFRPRS.setSelected(false);
            SelTCPRS.setSelected(true);
            SelTLPRS.setSelected(false);
        }
        if (settings.get("PRSClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("PRSClearStatus", "Track Lost");
            SelPMPRS.setSelected(false);
            SelFRPRS.setSelected(false);
            SelTCPRS.setSelected(false);
            SelTLPRS.setSelected(true);
        }
        
        if (settings.get("FTRClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("FTRClearStatus", "Pure Memory");
            SelPMFTR.setSelected(true);
            SelFRFTR.setSelected(false);
            SelTCFTR.setSelected(false);
            SelTLFTR.setSelected(false);
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("FTRClearStatus", "Full Recall");
            SelPMFTR.setSelected(false);
            SelFRFTR.setSelected(true);
            SelTCFTR.setSelected(false);
            SelTLFTR.setSelected(false);
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("FTRClearStatus", "Track Complete");
            SelPMFTR.setSelected(false);
            SelFRFTR.setSelected(false);
            SelTCFTR.setSelected(true);
            SelTLFTR.setSelected(false);
        }
        if (settings.get("FTRClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("FTRClearStatus", "Track Lost");
            SelPMFTR.setSelected(false);
            SelFRFTR.setSelected(false);
            SelTCFTR.setSelected(false);
            SelTLFTR.setSelected(true);
        }
        
        if (settings.get("ETRClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("ETRClearStatus", "Pure Memory");
            SelPMETR.setSelected(true);
            SelFRETR.setSelected(false);
            SelTCETR.setSelected(false);
            SelTLETR.setSelected(false);
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("ETRClearStatus", "Full Recall");
            SelPMETR.setSelected(false);
            SelFRETR.setSelected(true);
            SelTCETR.setSelected(false);
            SelTLETR.setSelected(false);
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("ETRClearStatus", "Track Complete");
            SelPMETR.setSelected(false);
            SelFRETR.setSelected(false);
            SelTCETR.setSelected(true);
            SelTLETR.setSelected(false);
        }
        if (settings.get("ETRClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("ETRClearStatus", "Track Lost");
            SelPMETR.setSelected(false);
            SelFRETR.setSelected(false);
            SelTCETR.setSelected(false);
            SelTLETR.setSelected(true);
        }
        
        if (settings.get("BYDClearStatus", "Track Complete").equals("Pure Memory")) {
            settings.set("BYDClearStatus", "Pure Memory");
            SelPMBYD.setSelected(true);
            SelFRBYD.setSelected(false);
            SelTCBYD.setSelected(false);
            SelTLBYD.setSelected(false);
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Full Recall")) {
            settings.set("BYDClearStatus", "Full Recall");
            SelPMBYD.setSelected(false);
            SelFRBYD.setSelected(true);
            SelTCBYD.setSelected(false);
            SelTLBYD.setSelected(false);
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Track Complete")) {
            settings.set("BYDClearStatus", "Track Complete");
            SelPMBYD.setSelected(false);
            SelFRBYD.setSelected(false);
            SelTCBYD.setSelected(true);
            SelTLBYD.setSelected(false);
        }
        if (settings.get("BYDClearStatus", "Track Complete").equals("Track Lost")) {
            settings.set("BYDClearStatus", "Track Lost");
            SelPMBYD.setSelected(false);
            SelFRBYD.setSelected(false);
            SelTCBYD.setSelected(false);
            SelTLBYD.setSelected(true);
        }

        MenuButton PSTDefaultSelection = new MenuButton(settings.get("PSTClearStatus", "Track Complete"));
        PSTDefaultSelection.getItems().clear();
        PSTDefaultSelection.getItems().addAll(SelPMPST, SelFRPST, SelTCPST, SelTLPST);
        MenuButton PRSDefaultSelection = new MenuButton(settings.get("PRSClearStatus", "Track Complete"));
        PRSDefaultSelection.getItems().clear();
        PRSDefaultSelection.getItems().addAll(SelPMPRS, SelFRPRS, SelTCPRS, SelTLPRS);
        MenuButton FTRDefaultSelection = new MenuButton(settings.get("FTRClearStatus", "Track Complete"));
        FTRDefaultSelection.getItems().clear();
        FTRDefaultSelection.getItems().addAll(SelPMFTR, SelFRFTR, SelTCFTR, SelTLFTR);
        MenuButton ETRDefaultSelection = new MenuButton(settings.get("ETRClearStatus", "Track Complete"));
        ETRDefaultSelection.getItems().clear();
        ETRDefaultSelection.getItems().addAll(SelPMETR, SelFRETR, SelTCETR, SelTLETR);
        MenuButton BYDDefaultSelection = new MenuButton(settings.get("BYDClearStatus", "Track Complete"));
        BYDDefaultSelection.getItems().clear();
        BYDDefaultSelection.getItems().addAll(SelPMBYD, SelFRBYD, SelTCBYD, SelTLBYD);

        SelPMPST.setOnAction(a -> {
            if (SelPMPST.isSelected()) {
                SelPMPST.setSelected(true);
                SelFRPST.setSelected(false);
                SelTCPST.setSelected(false);
                SelTLPST.setSelected(false);
                PSTDefaultSelection.setText("Pure Memory");
                settings.set("PSTClearStatus", "Pure Memory");
            }
        });
        SelFRPST.setOnAction(a -> {
            if (SelFRPST.isSelected()) {
                SelPMPST.setSelected(false);
                SelFRPST.setSelected(true);
                SelTCPST.setSelected(false);
                SelTLPST.setSelected(false);
                PSTDefaultSelection.setText("Full Recall");
                settings.set("PSTClearStatus", "Full Recall");
            }
        });
        SelTCPST.setOnAction(a -> {
            if (SelTCPST.isSelected()) {
                SelPMPST.setSelected(false);
                SelFRPST.setSelected(false);
                SelTCPST.setSelected(true);
                SelTLPST.setSelected(false);
                PSTDefaultSelection.setText("Track Complete");
                settings.set("PSTClearStatus", "Track Complete");
            }
        });
        SelTLPST.setOnAction(a -> {
            if (SelTLPST.isSelected()) {
                SelPMPST.setSelected(false);
                SelFRPST.setSelected(false);
                SelTCPST.setSelected(false);
                SelTLPST.setSelected(true);
                PSTDefaultSelection.setText("Track Lost");
                settings.set("PSTClearStatus", "Track Lost");
            }
        });

        SelPMPRS.setOnAction(a -> {
            if (SelPMPRS.isSelected()) {
                SelPMPRS.setSelected(true);
                SelFRPRS.setSelected(false);
                SelTCPRS.setSelected(false);
                SelTLPRS.setSelected(false);
                PRSDefaultSelection.setText("Pure Memory");
                settings.set("PRSClearStatus", "Pure Memory");
            }
        });
        SelFRPRS.setOnAction(a -> {
            if (SelFRPRS.isSelected()) {
                SelPMPRS.setSelected(false);
                SelFRPRS.setSelected(true);
                SelTCPRS.setSelected(false);
                SelTLPRS.setSelected(false);
                PRSDefaultSelection.setText("Full Recall");
                settings.set("PRSClearStatus", "Full Recall");
            }
        });
        SelTCPRS.setOnAction(a -> {
            if (SelTCPRS.isSelected()) {
                SelPMPRS.setSelected(false);
                SelFRPRS.setSelected(false);
                SelTCPRS.setSelected(true);
                SelTLPRS.setSelected(false);
                PRSDefaultSelection.setText("Track Complete");
                settings.set("PRSClearStatus", "Track Complete");
            }
        });
        SelTLPRS.setOnAction(a -> {
            if (SelTLPRS.isSelected()) {
                SelPMPRS.setSelected(false);
                SelFRPRS.setSelected(false);
                SelTCPRS.setSelected(false);
                SelTLPRS.setSelected(true);
                PRSDefaultSelection.setText("Track Lost");
                settings.set("PRSClearStatus", "Track Lost");
            }
        });

        SelPMFTR.setOnAction(a -> {
            if (SelPMFTR.isSelected()) {
                SelPMFTR.setSelected(true);
                SelFRFTR.setSelected(false);
                SelTCFTR.setSelected(false);
                SelTLFTR.setSelected(false);
                FTRDefaultSelection.setText("Pure Memory");
                settings.set("FTRClearStatus", "Pure Memory");
            }
        });
        SelFRFTR.setOnAction(a -> {
            if (SelFRFTR.isSelected()) {
                SelPMFTR.setSelected(false);
                SelFRFTR.setSelected(true);
                SelTCFTR.setSelected(false);
                SelTLFTR.setSelected(false);
                FTRDefaultSelection.setText("Full Recall");
                settings.set("FTRClearStatus", "Full Recall");
            }
        });
        SelTCFTR.setOnAction(a -> {
            if (SelTCFTR.isSelected()) {
                SelPMFTR.setSelected(false);
                SelFRFTR.setSelected(false);
                SelTCFTR.setSelected(true);
                SelTLFTR.setSelected(false);
                FTRDefaultSelection.setText("Track Complete");
                settings.set("FTRClearStatus", "Track Complete");
            }
        });
        SelTLFTR.setOnAction(a -> {
            if (SelTLFTR.isSelected()) {
                SelPMFTR.setSelected(false);
                SelFRFTR.setSelected(false);
                SelTCFTR.setSelected(false);
                SelTLFTR.setSelected(true);
                FTRDefaultSelection.setText("Track Lost");
                settings.set("FTRClearStatus", "Track Lost");
            }
        });

        SelPMETR.setOnAction(a -> {
            if (SelPMETR.isSelected()) {
                SelPMETR.setSelected(true);
                SelFRETR.setSelected(false);
                SelTCETR.setSelected(false);
                SelTLETR.setSelected(false);
                ETRDefaultSelection.setText("Pure Memory");
                settings.set("ETRClearStatus", "Pure Memory");
            }
        });
        SelFRETR.setOnAction(a -> {
            if (SelFRETR.isSelected()) {
                SelPMETR.setSelected(false);
                SelFRETR.setSelected(true);
                SelTCETR.setSelected(false);
                SelTLETR.setSelected(false);
                ETRDefaultSelection.setText("Full Recall");
                settings.set("ETRClearStatus", "Full Recall");
            }
        });
        SelTCETR.setOnAction(a -> {
            if (SelTCETR.isSelected()) {
                SelPMETR.setSelected(false);
                SelFRETR.setSelected(false);
                SelTCETR.setSelected(true);
                SelTLETR.setSelected(false);
                ETRDefaultSelection.setText("Track Complete");
                settings.set("ETRClearStatus", "Track Complete");
            }
        });
        SelTLETR.setOnAction(a -> {
            if (SelTLETR.isSelected()) {
                SelPMETR.setSelected(false);
                SelFRETR.setSelected(false);
                SelTCETR.setSelected(false);
                SelTLETR.setSelected(true);
                ETRDefaultSelection.setText("Track Lost");
                settings.set("ETRClearStatus", "Track Lost");
            }
        });

        SelPMBYD.setOnAction(a -> {
            if (SelPMBYD.isSelected()) {
                SelPMBYD.setSelected(true);
                SelFRBYD.setSelected(false);
                SelTCBYD.setSelected(false);
                SelTLBYD.setSelected(false);
                BYDDefaultSelection.setText("Pure Memory");
                settings.set("BYDClearStatus", "Pure Memory");
            }
        });
        SelFRBYD.setOnAction(a -> {
            if (SelFRBYD.isSelected()) {
                SelPMBYD.setSelected(false);
                SelFRBYD.setSelected(true);
                SelTCBYD.setSelected(false);
                SelTLBYD.setSelected(false);
                BYDDefaultSelection.setText("Full Recall");
                settings.set("BYDClearStatus", "Full Recall");
            }
        });
        SelTCBYD.setOnAction(a -> {
            if (SelTCBYD.isSelected()) {
                SelPMBYD.setSelected(false);
                SelFRBYD.setSelected(false);
                SelTCBYD.setSelected(true);
                SelTLBYD.setSelected(false);
                BYDDefaultSelection.setText("Track Complete");
                settings.set("BYDClearStatus", "Track Complete");
            }
        });
        SelTLBYD.setOnAction(a -> {
            if (SelTLBYD.isSelected()) {
                SelPMBYD.setSelected(false);
                SelFRBYD.setSelected(false);
                SelTCBYD.setSelected(false);
                SelTLBYD.setSelected(true);
                BYDDefaultSelection.setText("Track Lost");
                settings.set("BYDClearStatus", "Track Lost");
            }
        });

        GridPane settingsGrid = new GridPane();
        settingsGrid.getStyleClass().add("Panes");
        settingsGrid.setFocusTraversable(false);
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(10);

        ScrollPane settingsScroll = new ScrollPane(settingsGrid);
        settingsScroll.getStyleClass().add("Panes");



        GridPane SETTINGSTITLEPANE = new GridPane();
        SETTINGSTITLEPANE.setVgap(10);
        Label SETTINGSTITLE = new Label("환경설정");
        SETTINGSTITLE.setFocusTraversable(false);
        SETTINGSTITLE.getStyleClass().add("SETTINGSTITLE");
        SETTINGSTITLEPANE.add(SETTINGSTITLE, 0, 0);
        SETTINGSTITLEPANE.add(new Label(""), 0, 2);
        
        settingsGrid.add(SETTINGSTITLEPANE, 5, 5);



        GridPane UserSettingsGrid = new GridPane();
        UserSettingsGrid.setHgap(10);
        UserSettingsGrid.setVgap(20);
        Label UserSettings = new Label("내 정보");
        UserSettings.setFocusTraversable(false);
        UserSettings.getStyleClass().add("settingsCategoryTitle");
        UserSettingsGrid.add(UserSettings, 0, 0);
        
        GridPane UserSettingsGrid1 = new GridPane();
        UserSettingsGrid1.setHgap(10);
        UserSettingsGrid1.setVgap(10);
        Label UsernameLabel = new Label("닉네임 설정");
        UsernameLabel.getStyleClass().add("settingsCategorySubTitle");
        UserSettingsGrid1.add(UsernameLabel, 0, 0);
        GridPane UsernameSettingsGrid = new GridPane();
        UsernameSettingsGrid.setHgap(10);
        UsernameSettingsGrid.setVgap(20);
        TextField UsernameField = new TextField();
        UsernameField.setPrefWidth(150);
        UsernameField.setText(Username);
        UsernameField.setFocusTraversable(false);
        Button UsernameApplyButton = new Button("저장");
        UsernameApplyButton.getStyleClass().add("Buttons");
        UsernameApplyButton.setOnAction(a -> {
            Username = UsernameField.getText();
            settings.set("Username", UsernameField.getText());
        });
        UsernameSettingsGrid.add(UsernameField, 0, 0);
        UsernameSettingsGrid.add(UsernameApplyButton, 1, 0);
        UserSettingsGrid1.add(UsernameSettingsGrid, 0, 1);
        UserSettingsGrid.add(UserSettingsGrid1, 0, 1);

        UserSettingsGrid.add(new Label(""), 0, 3);
        settingsGrid.add(UserSettingsGrid, 5, 6);



        GridPane DisplaySettingsGrid = new GridPane();
        DisplaySettingsGrid.setHgap(10);
        DisplaySettingsGrid.setVgap(20);
        Label DisplaySettings = new Label("디스플레이 설정");
        DisplaySettings.setFocusTraversable(false);
        DisplaySettings.getStyleClass().add("settingsCategoryTitle");
        DisplaySettingsGrid.add(DisplaySettings, 0, 0);

        GridPane ThemeSettingsGrid = new GridPane();
        ThemeSettingsGrid.setHgap(10);
        ThemeSettingsGrid.setVgap(10);
        Label ThemeLabel = new Label("테마 설정");
        ThemeLabel.getStyleClass().add("settingsCategorySubTitle");
        ThemeSettingsGrid.add(ThemeLabel, 0, 0);
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
        ThemeGrid.setHgap(75);
        ThemeGrid.setVgap(10);
        ThemeGrid.add(LightTheme, 0, 0);
        ThemeGrid.add(DarkTheme, 1, 0);
        ThemeSettingsGrid.add(ThemeGrid, 0, 1);
        DisplaySettingsGrid.add(ThemeSettingsGrid, 0, 1);
        
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
                   /*//System.out.println(settingsScroll.getScene());*/
                   String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                   settingsScroll.getScene().getStylesheets().clear();
                   settingsScroll.getScene().getStylesheets().add(cssPath);
                   /*//System.out.println("Light성공");*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        /*//System.out.println(settingsScroll.getScene());*/
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                        /*//System.out.println("Light성공");*/
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        /*//System.out.println("Light실패");*/
                    }
                }
                /*//System.out.println(themeStatus);*/
            }
            else if (DarkTheme.isSelected()) {
                settings.set("Theme", "Dark");
                try {
                    /*//System.out.println(settingsScroll.getScene());*/
                    String cssPath = getClass().getResource("/css/APCDark.css").toExternalForm();
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(cssPath);
                    /*//System.out.println("Dark성공");*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        /*//System.out.println(settingsScroll.getScene());*/
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCDark.css").toURI().toURL().toExternalForm());
                        /*//System.out.println("Dark성공");*/
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        /*//System.out.println("Dark실패");*/
                    }
                }
                /*//System.out.println(themeStatus);*/
            }
            /*else if (CustomTheme.isSelected()) {
                settings.set("Theme", "Custom");
                try {
                    String cssPath = getClass().getResource("/css/APCCustom.css").toExternalForm();
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(cssPath);
                    //System.out.println("Custom성공");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    try {
                        settingsScroll.getScene().getStylesheets().clear();
                        settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCCustom.css").toURI().toURL().toExternalForm());
                        //System.out.println("Custom성공");
                    }
                    catch (Exception f) {
                        f.printStackTrace();
                        //System.out.println("Custom실패");
                    }
                }
                //System.out.println(themeStatus);
            }*/
        });
        
        DisplaySettingsGrid.add(new Label(""), 0, 3);
        settingsGrid.add(DisplaySettingsGrid, 5, 7);
        
        
        
        GridPane ResultCalcGrid = new GridPane();
        ResultCalcGrid.setHgap(10);
        ResultCalcGrid.setVgap(20);
        Label ResultCalcSettings = new Label("결과 포텐셜 계산기 설정");
        ResultCalcSettings.getStyleClass().add("settingsCategoryTitle");
        ResultCalcGrid.add(ResultCalcSettings, 0, 0);
        
        GridPane clearCheckBoxDefaultSettingsGrid = new GridPane();
        clearCheckBoxDefaultSettingsGrid.setHgap(10);
        clearCheckBoxDefaultSettingsGrid.setVgap(10);
        Label clearCheckBoxDefaultSettings = new Label("클리어 체크박스 기본값 설정");
        clearCheckBoxDefaultSettings.setFocusTraversable(false);
        clearCheckBoxDefaultSettings.getStyleClass().add("settingsCategorySubTitle");
        clearCheckBoxDefaultSettingsGrid.add(clearCheckBoxDefaultSettings, 0, 0);
        
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
        clearCheckBoxDefaultGrid.setVgap(10);
        clearCheckBoxDefaultGrid.add(clearChecked, 0, 0);
        clearCheckBoxDefaultGrid.add(clearUnchecked, 1, 0);
        clearCheckBoxDefaultGrid.add(clearFollowLatest, 0, 1);
        clearCheckBoxDefaultSettingsGrid.add(clearCheckBoxDefaultGrid, 0, 1);
        ResultCalcGrid.add(clearCheckBoxDefaultSettingsGrid, 0, 1);
        
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
        else {
            settings.set("clearCheckBoxDefault", "Checked");
            settings.set("clearLatest", "Track Complete");
            isCleared = true;
            clearChecked.setSelected(true);
            clearUnchecked.setSelected(false);
            clearFollowLatest.setSelected(false);
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
        
        ResultCalcGrid.add(new Label(""), 0, 3);
        settingsGrid.add(ResultCalcGrid, 5, 8);
        
        
        
        GridPane B50SettingsGrid = new GridPane();
        B50SettingsGrid.setHgap(10);
        B50SettingsGrid.setVgap(20);
        Label B50ClearStatusLabel = new Label("B50 설정");
        B50ClearStatusLabel.setFocusTraversable(false);
        B50ClearStatusLabel.getStyleClass().add("settingsCategoryTitle");
        B50SettingsGrid.add(B50ClearStatusLabel, 0, 0);
        
        GridPane clearStatusGrid = new GridPane();
        clearStatusGrid.setHgap(10);
        clearStatusGrid.setVgap(10);
        Label clearStatusDefault = new Label("클리어 상태 기본값 설정");
        clearStatusDefault.setFocusTraversable(false);
        clearStatusDefault.getStyleClass().add("settingsCategorySubTitle");
        clearStatusGrid.add(clearStatusDefault, 0, 0);
        GridPane clearStatusDefaultGrid = new GridPane();
        clearStatusDefaultGrid.setFocusTraversable(false);
        clearStatusDefaultGrid.setHgap(10);
        clearStatusDefaultGrid.setVgap(10);
        clearStatusGrid.add(clearStatusDefaultGrid, 0, 1);
        clearStatusDefaultGrid.add(new Label("PST"), 0, 0);
        clearStatusDefaultGrid.add(new Label("|"), 1, 0);
        clearStatusDefaultGrid.add(PSTDefaultSelection, 2, 0);
        clearStatusDefaultGrid.add(new Label("PRS"), 0, 1);
        clearStatusDefaultGrid.add(new Label("|"), 1, 1);
        clearStatusDefaultGrid.add(PRSDefaultSelection, 2, 1);
        clearStatusDefaultGrid.add(new Label("FTR"), 0, 2);
        clearStatusDefaultGrid.add(new Label("|"), 1, 2);
        clearStatusDefaultGrid.add(FTRDefaultSelection,2, 2);
        clearStatusDefaultGrid.add(new Label("ETR"), 0, 3);
        clearStatusDefaultGrid.add(new Label("|"), 1, 3);
        clearStatusDefaultGrid.add(ETRDefaultSelection, 2, 3);
        clearStatusDefaultGrid.add(new Label("BYD"), 0, 4);
        clearStatusDefaultGrid.add(new Label("|"), 1, 4);
        clearStatusDefaultGrid.add(BYDDefaultSelection, 2, 4);
        B50SettingsGrid.add(clearStatusGrid, 0, 1);
        
        GridPane autoClearSettingsGrid = new GridPane();
        autoClearSettingsGrid.setHgap(10);
        autoClearSettingsGrid.setVgap(10);
        Label autoClearLabel = new Label("스코어 데이터 붙여넣기 필드 비우기 설정");
        autoClearLabel.getStyleClass().add("settingsCategorySubTitle");
        autoClearSettingsGrid.add(autoClearLabel, 0, 0);
        ToggleGroup autoClearSelection = new ToggleGroup();
        RadioButton AutoClear = new RadioButton("자동 비우기");
        AutoClear.getStyleClass().add("RadioButton");
        AutoClear.setToggleGroup(autoClearSelection);
        RadioButton DoNotClear = new RadioButton("비우지 않기");
        DoNotClear.getStyleClass().add("RadioButton");
        DoNotClear.setToggleGroup(autoClearSelection);
        GridPane autoClearGrid = new GridPane();
        autoClearGrid.getStyleClass().add("Panes");
        autoClearGrid.setHgap(75);
        autoClearGrid.setVgap(10);
        autoClearGrid.add(AutoClear, 0, 0);
        autoClearGrid.add(DoNotClear, 1, 0);
        autoClearSettingsGrid.add(autoClearGrid, 0, 1);
        B50SettingsGrid.add(autoClearSettingsGrid, 0, 3);

        if (settings.get("AutoClear", "N").equals("Y")) {
            settings.set("AutoClear", "Y");
            AutoClear.setSelected(true);
            DoNotClear.setSelected(false);
        }
        else if (settings.get("AutoClear", "N").equals("N")) {
            settings.set("AutoClear", "N");
            DoNotClear.setSelected(true);
            AutoClear.setSelected(false);
        }

        autoClearSelection.selectedToggleProperty().addListener((obs, old, val) -> {
            if (AutoClear.isSelected()) {
                settings.set("AutoClear", "Y");
                AutoClear.setSelected(true);
                DoNotClear.setSelected(false);
            }
            else if (DoNotClear.isSelected()) {
                settings.set("AutoClear", "N");
                DoNotClear.setSelected(true);
                AutoClear.setSelected(false);
            }
        });

        B50SettingsGrid.add(new Label(""), 0, 4);
        settingsGrid.add(B50SettingsGrid, 5, 9);
        
        
        
        GridPane ResetGrid = new GridPane();
        ResetGrid.setHgap(10);
        ResetGrid.setVgap(10);

        Label settingsResetLabel = new Label("설정 초기화");
        settingsResetLabel.setFocusTraversable(false);
        settingsResetLabel.getStyleClass().add("settingsCategoryTitle");
        ResetGrid.add(settingsResetLabel, 0, 0);

        Button SetSettingsToDefault = new Button("설정 초기화");
        ResetGrid.add(SetSettingsToDefault, 0, 1);
        SetSettingsToDefault.getStyleClass().add("Buttons");

        SetSettingsToDefault.setOnAction(SetDefault -> {
            settings.set("Theme", "Light");
            LightTheme.setSelected(true);
            DarkTheme.setSelected(false);
            try {
                /*//System.out.println(settingsScroll.getScene());*/
                String cssPath = getClass().getResource("/css/APCLight.css").toExternalForm();
                settingsScroll.getScene().getStylesheets().clear();
                settingsScroll.getScene().getStylesheets().add(cssPath);
                /*//System.out.println("Light성공");*/
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    /*//System.out.println(settingsScroll.getScene());*/
                    settingsScroll.getScene().getStylesheets().clear();
                    settingsScroll.getScene().getStylesheets().add(new java.io.File("src/main/resources/css/APCLight.css").toURI().toURL().toExternalForm());
                    /*//System.out.println("Light성공");*/
                }
                catch (Exception f) {
                    f.printStackTrace();
                    /*//System.out.println("Light실패");*/
                }
            }
            /*//System.out.println(themeStatus);*/

            settings.set("clearCheckBoxDefault", "Checked");
            clearChecked.setSelected(true);
            clearUnchecked.setSelected(false);
            clearFollowLatest.setSelected(false);

            settings.set("Username", "Username");
            Username = "Username";
            UsernameField.setText(Username);

            settings.set("PSTClearStatus", "Track Complete");
            PSTDefaultSelection.setText("Track Complete");
            SelPMPST.setSelected(false);
            SelFRPST.setSelected(false);
            SelTCPST.setSelected(true);
            SelTCPST.setSelected(false);
            settings.set("PRSClearStatus", "Track Complete");
            PRSDefaultSelection.setText("Track Complete");
            SelPMPRS.setSelected(false);
            SelFRPRS.setSelected(false);
            SelTCPRS.setSelected(true);
            SelTCPRS.setSelected(false);
            settings.set("FTRClearStatus", "Track Complete");
            FTRDefaultSelection.setText("Track Complete");
            SelPMFTR.setSelected(false);
            SelFRFTR.setSelected(false);
            SelTCFTR.setSelected(true);
            SelTCFTR.setSelected(false);
            settings.set("ETRClearStatus", "Track Complete");
            ETRDefaultSelection.setText("Track Complete");
            SelPMETR.setSelected(false);
            SelFRETR.setSelected(false);
            SelTCETR.setSelected(true);
            SelTCETR.setSelected(false);
            settings.set("BYDClearStatus", "Track Complete");
            BYDDefaultSelection.setText("Track Complete");
            SelPMBYD.setSelected(false);
            SelFRBYD.setSelected(false);
            SelTCBYD.setSelected(true);
            SelTCBYD.setSelected(false);

            /*settings.set("B50InputDifficultyDefault", "FTR");
            InputDiffFTR.setSelected(true);*/

            settings.set("AutoClear", "Y");
            AutoClear.setSelected(true);
            DoNotClear.setSelected(false);
        });

        ResetGrid.add(new Label(""), 0, 4);
        settingsGrid.add(ResetGrid, 5, 11);

        
        settingsScroll.setFitToWidth(true);
        settingsScroll.setFocusTraversable(false);

        return settingsScroll;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                     AppInfo                                                     */
    /* --------------------------------------------------------------------------------------------------------------- */
    
    public ScrollPane AppInfo() {
        PageIndex = 5;

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
        Text INFO_VersionInfo = new Text(String.format("%n       version %s", version));
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
            "       v7.3.1" /*+ "\n"*/ +
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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                    numInput                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                  autoNumInput                                                  */
    /* -------------------------------------------------------------------------------------------------------------- */

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
            autoResultLabel.setText(String.valueOf(String.format("결과 포텐셜 :%n") + APCLogic.APCLogicMain(song.getConst(diffInt), score, (double) song.getNotes(diffInt), isCleared)) + String.format("%n%n보면상수 :%n%.1f%n%n총 노트 수 :%n%d", song.getConst(diffInt), song.getNotes(diffInt)));/*//System.out.println("0 out");*/
        }
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                 SongInformation                                                 */
    /* --------------------------------------------------------------------------------------------------------------- */

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

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                  searchedSongs                                                  */
    /* --------------------------------------------------------------------------------------------------------------- */

    public ObservableList<SongInformation> searchedSongs(List<String> DiffList, String Keyword) {
        ObservableList<SongInformation> ss = FXCollections.observableArrayList();
        ss.clear();
        String search = Keyword.toLowerCase().replaceAll("\\s+", "");;
        Double searchParsed = -1.0;

        try {
            searchParsed = Double.parseDouble(search);
        }
        catch (Exception e) {
            searchParsed = -1.0;
        }

        for (Songs song : Songs.values()) {

            for (int i = 0; i <= 4; i++) {
                if (DiffList.contains(String.valueOf(i)) && song.getConst(i) > 0) {
                    Boolean TitleMatch = song.getTitle(i).toLowerCase().replaceAll("\\s+", "").contains(search);
                    boolean LevelMatch = song.getLevel(i).equalsIgnoreCase(search);
                    boolean ConstMatch = (searchParsed > 0) && (song.getConst(i) == searchParsed);

                    if (TitleMatch || LevelMatch || ConstMatch) {
                        ss.add(new SongInformation(song, song.getTitle(i), song.getDiff(i), song.getLevel(i)));
                        continue;
                    }
                }
            }
        }
        return ss;
    }

    /* --------------------------------------------------------------------------------------------------------------- */
    /*                                                  filteredSongs                                                  */
    /* --------------------------------------------------------------------------------------------------------------- */

    public ObservableList<SongInformation> filteredSongs(List<String> DiffList, String Keyword) {
        ObservableList<SongInformation> fs = FXCollections.observableArrayList();
        fs.clear();

        for (SongInformation song : searchedSongs(DiffList, Keyword)) {
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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               B50SongInformation                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    public class B50SongInformation {
        public Songs songInfo;
        public String title;
        public String difficulty;
        public String lvl;

        public B50SongInformation(Songs songInfo, String title, String difficulty) {
            this.songInfo = songInfo;
            this.title = title;
            this.difficulty = difficulty;
        }

        public Songs getSongInfo() {
            return songInfo;
        }
        public String getTitle() {
            return title;
        }
        public String getDiff() {
            return difficulty;
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                              B50AutoSearchedSongs                                              */
    /* -------------------------------------------------------------------------------------------------------------- */

    public ObservableList<SongInformation> B50AutoSearchedSongs(List<String> DiffList, String Keyword) {
        ObservableList<SongInformation> ss = FXCollections.observableArrayList();
        ss.clear();
        String search = Keyword.replaceAll("\\s+", "");;
        Double searchParsed = -1.0;

        try {
            searchParsed = Double.parseDouble(search);
        }
        catch (Exception e) {
            searchParsed = -1.0;
        }

        for (Songs song : Songs.values()) {

            for (int i = 0; i <= 4; i++) {
                if (DiffList.contains(String.valueOf(i)) && song.getConst(i) > 0) {
                    Boolean TitleMatch = song.getTitle(i).replaceAll("\\s+", "").contains(search);
                    boolean LevelMatch = song.getLevel(i).equals(search);
                    boolean ConstMatch = (searchParsed > 0) && (song.getConst(i) == searchParsed);

                    if (TitleMatch || LevelMatch || ConstMatch) {
                        ss.add(new SongInformation(song, song.getTitle(i), song.getDiff(i), song.getLevel(i)));
                        continue;
                    }
                }
            }
        }
        return ss;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                B50SearchedSongs                                                */
    /* -------------------------------------------------------------------------------------------------------------- */

    public ObservableList<B50SongInformation> B50SearchedSongs(String Keyword) {
        ObservableList<B50SongInformation> B50ss = FXCollections.observableArrayList();
        B50ss.clear();
        String search = Keyword.toLowerCase().replaceAll("\\s+", "");;
        Double searchParsed = -1.0;

        try {
            searchParsed = Double.parseDouble(search);
        }
        catch (Exception e) {
            searchParsed = -1.0;
        }

        for (Songs song : Songs.values()) {

            for (int i = 0; i <= 4; i++) {
                if (song.getConst(i) > 0) {
                    Boolean TitleMatch = song.getTitle(i).toLowerCase().replaceAll("\\s+", "").contains(search);
                    boolean LevelMatch = song.getLevel(i).equalsIgnoreCase(search);
                    boolean ConstMatch = (searchParsed > 0) && (song.getConst(i) == searchParsed);
                    Boolean Exist = false;

                    if (TitleMatch || LevelMatch || ConstMatch) {
                        for (B50SongInformation s : B50ss) {
                            if (s.getSongInfo().equals(song)) Exist = true;
                        }
                        if (Exist) continue;
                        else if (!song.getTitle(0).equals(song.getTitle(4))) {
                            if (!song.getTitle(4).equals("") && !song.getTitle(0).equals("")) {
                                B50ss.add(new B50SongInformation(song, String.format(song.getTitle(0) + "%n" + song.getTitle(4)), song.getDiff(4)));
                            }
                            else if (song.getTitle(4).equals("") && song.getConst(4) > 0) {
                                B50ss.add(new B50SongInformation(song, song.getTitle(0), song.getDiff(4)));
                            }
                            else if (song.getTitle(4).equals("") && song.getConst(3) > 0) {
                                B50ss.add(new B50SongInformation(song, song.getTitle(0), song.getDiff(3)));
                            }
                            else if (song.getTitle(0).equals("") && song.getConst(4) > 0) {
                                B50ss.add(new B50SongInformation(song, song.getTitle(4), song.getDiff(4)));
                            }
                            else {
                                B50ss.add(new B50SongInformation(song, song.getTitle(0), "-"));
                            }
                        }
                        else if (song.getTitle(0).equals(song.getTitle(4)) && song.getConst(4) > 0) {
                            B50ss.add(new B50SongInformation(song, song.getTitle(0), song.getDiff(4)));
                        }
                        continue;
                    }
                }
            }
        }
        return B50ss;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                     Launch                                                     */
    /* -------------------------------------------------------------------------------------------------------------- */

    public static void main(String[] args) {
        launch(args);
    }
}