package net.windyweather.ssadisplayimages;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SSADisplayImagesController {
    public SplitPane splitPaneOutsideContainer;
    public MenuItem miSavePairs;
    public MenuItem miCloseApplication;
    public MenuItem miAboutApplication;
    public Label lblImageName;
    public ScrollPane spImagePane;
    public Button btnGoImagesStart;
    public Button btnGoImageBack10;
    public Button btnGoImageBackOne;
    public ScrollBar sbImageListScrollBar;
    public Button btnImageForwardOne;
    public Button btnImageForward10;
    public Button btnGoImagesEnd;
    public ListView lvScreenShotPairs;
    public Button btnMovePairTop;
    public Button btnMovePairDown;
    public Button btnMovePairUp;
    public TextField txtSelectedPairName;
    public CheckBox chkSearchSubFolders;
    public TextField txtSourcePath;
    public Button btnSetSourcePath;
    public TextField txtDestPath;
    public Button btnSetDestPath;
    public ComboBox cbChooseFolderSuffix;
    public TextField txtFilePrefix;
    public CheckBox chkPreserveFileNames;
    public Button btnRemovePair;
    public Button btnAddPair;
    public Button btnUpdatePair;
    public Button btnViewSource;
    public Button btnViewDestination;
    public Button btnCopySource;
    public Button btnDeleteSource;
    public CheckBox chkTestLogOnly;
    public Button btnMakeTestPairs;
    public TextField txtStatus;
    public Button btnCloseAppButton;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}