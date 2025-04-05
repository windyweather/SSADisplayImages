package net.windyweather.ssadisplayimages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


// See if we can find DirectoryScanner somewhere

//import org.codehaus.plexus.util.AbstractScanner;
import org.codehaus.plexus.util.DirectoryScanner;



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
    /*
        not the real <type> just a placeholder to shut up the compiler
        The list view is never used here
     */
    public ListView<String> lvScreenShotPairs;
    public Button btnMovePairTop;
    public Button btnMovePairDown;
    public Button btnMovePairUp;
    public TextField txtSelectedPairName;
    public CheckBox chkSearchSubFolders;
    public TextField txtSourcePath;
    public Button btnSetSourcePath;
    public TextField txtDestPath;
    public Button btnSetDestPath;
    public ComboBox<String> cbChooseFolderSuffix;
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

    //
    // Put some text in the status line to say what's up
    //
    public void setStatus( String sts ) {

        txtStatus.setText( sts );
    }

    //
    // Do this in one place so we can easily turn it off later
    //
    public static void printSysOut( String str ) {
        System.out.println(str);
    }

    public void SetUpStuff(){

        // initialize combo box choices
        ObservableList<String> sol = FXCollections.observableArrayList("yyyy_MM", "", "yyyy_MM_dd");
        cbChooseFolderSuffix.setItems(sol);
        cbChooseFolderSuffix.getSelectionModel().selectFirst();

        /*
        set some paths for testing
         */
        String sTestImagePath = "D:\\MMO_Pictures\\AlienBlackout";

        txtSourcePath.setText(sTestImagePath);
        txtDestPath.setText(sTestImagePath);

    }
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void OnMenuSavePairs(ActionEvent actionEvent) {
    }

    public void OnMenuCloseApplication(ActionEvent actionEvent) {
    }

    public void onAboutApplication(ActionEvent actionEvent) {
    }

    public void onGoImagesStart(ActionEvent actionEvent) {
    }

    public void onGoImageBack10(ActionEvent actionEvent) {
    }

    public void onGoImageBackOne(ActionEvent actionEvent) {
    }

    public void onGoImageForwardOne(ActionEvent actionEvent) {
    }

    public void onGoImageForward10(ActionEvent actionEvent) {
    }

    public void onGoImagesEnd(ActionEvent actionEvent) {
    }

    public void OnListViewMouseClicked(MouseEvent mouseEvent) {
    }

    public void OnMovePairTop(ActionEvent actionEvent) {
    }

    public void OnMovePairUp(ActionEvent actionEvent) {
    }

    public void OnMovePairDown(ActionEvent actionEvent) {
    }

    /*
          Use directoryChooser dialogs launched from stage.
       */
    public void OnSetDestinPath(ActionEvent actionEvent) {
        /*
            Get a path based on the last path we've seen
         */
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set the Destination Path");
        String lastPath = txtDestPath.getText();
        if (!lastPath.isBlank()) {
            File aFile = new File(lastPath);
            // do we have a valid path to a folder here?
            if ( aFile.isDirectory() ) {
                dirChooser.setInitialDirectory(aFile);
            }
        }
        /*
           We either set the initial path if we had one
           or we'll just go in blind and let the user
           navigate where he/she wants.
           Launch the chooser dialog and then stuff
           the result into the source path
       */
        File selDir = dirChooser.showDialog(( stage ));
        /*
            make sure we got something back otherwise just ignore it
         */
        if ( selDir != null ) {
            txtDestPath.setText(selDir.getAbsolutePath());
            setStatus("Destination Path Set");
        } else {
            setStatus("No path selected");
        }
    }

    public void OnSetSourcePath(ActionEvent actionEvent) {
        /*
            Get a path based on the last path we've seen
         */
        Stage stage = (Stage) txtSelectedPairName.getScene().getWindow();
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Set the Source Path");
        String lastPath = txtSourcePath.getText();
        if (!lastPath.isBlank()) {
            File aFile = new File(lastPath);
            // do we have a valid path to a folder here?
            if ( aFile.isDirectory() ) {
                dirChooser.setInitialDirectory(aFile);
            }
        }
        /*
           We either set the initial path if we had one
           or we'll just go in blind and let the user
           navigate where he/she wants.
           Launch the chooser dialog and then stuff
           the result into the source path
       */
        File selDir = dirChooser.showDialog(( stage ));
        /*
            make sure we got something back otherwise just ignore it
         */
        if ( selDir != null ) {
            txtSourcePath.setText(selDir.getAbsolutePath());
            setStatus("Source Path Set");
        } else {
            setStatus("No path selected");
        }
    }



    public void OnRemovePair(ActionEvent actionEvent) {
    }

    public void btnAddPair(ActionEvent actionEvent) {
    }

    public void OnUpdatePair(ActionEvent actionEvent) {
    }

    /*
        Use apache DirectoryScanner to get a list of images in the specified folder
        with or without the File Prefix. Source does not use prefix, destination does
     */
    private String[] GetImagesInFolder( String sFolder, boolean bUsePfx ){
        /*
            we care about only three image types: *.bmp, *.jpg, *.png
         */
        String sFPfx = "";
        if ( bUsePfx) {
            sFPfx = txtFilePrefix.getText();
        }

        String[] saIncludeImages = new String[]{sFPfx + "*.bmp",sFPfx + "*.jpg", sFPfx+"*.png"  };

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( saIncludeImages );
        scanner.setCaseSensitive( false );
        scanner.setBasedir( new File( sFolder ));
        scanner.scan();

        String[] files = scanner.getIncludedFiles();
        int iHowMany = files.length;

        printSysOut(String.format("GetImagesInFolder found %d files in %s", iHowMany, sFolder));

        return files;

    }

    /*
        Scan the Source for files. Don't use the File Prefix in the Source
     */
    public void OnViewSource(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder( txtSourcePath.getText(), false );
        setStatus(String.format("Source Images Found: %d", sImageFileNames.length));
    }

    public void OnViewDestination(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder( txtDestPath.getText(), true );
        setStatus(String.format("Destination Images Found: %d", sImageFileNames.length));
    }

    public void OnCopySource(ActionEvent actionEvent) {
    }

    public void OnDeleteSource(ActionEvent actionEvent) {
    }

    public void OnMakeTestPairs(ActionEvent actionEvent) {
    }

    public void OnCloseAppButton(ActionEvent actionEvent) {
    }
}