package net.windyweather.ssadisplayimages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


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
    public ImageView imgImageView;
    //@FXML


    //@FXML

    /*
        List of images from the last View Source / Destination
        Which Image are we pointed to
        and do we have any images at all
     */
    private String[] sImageList;
    private String  sImageBasePath;
    private int intImageIndex;
    private boolean bImagesValid;

    public Image anImage;
    private double dZoomScale;

    /*
   Put some text in the status line to say what's up
    */
    public void setStatus( String sts ) {

        txtStatus.setText( sts );
    }

    //
    // Do this in one place so we can easily turn it off later
    //
    public static void printSysOut( String str ) {
        System.out.println(str);
    }

    /*
        Open an image from the list based on the index
        Just do nothing if we don't have one
     */

    void OpenImageFromList( ) {

        /*
            Check for sanity of the call
         */
        if ( !bImagesValid || intImageIndex >= sImageList.length ) {
            setStatus("OpenImageFromList no images or invalid index");
            lblImageName.setText("");
            return;
        }
        /*
            Time the following process in Milliseconds just to see how long it takes
         */
        long intStartOpen = System.currentTimeMillis();
        /*
            Get the image we are interested in.
            Reconstruct the absolute path from the base path we saved, and
            the file name returned by the scanner
         */
        String sImageFileName = sImageList[intImageIndex];
        String sImageFilePath = sImageBasePath + File.separator + sImageFileName;
        printSysOut(String.format( "openImageFromList - Opening index %d %s", intImageIndex, sImageFilePath ));
        lblImageName.setText( sImageFileName );
        File selectedImageFile = new File( sImageFilePath );
        /*
            we appear to have a file so we will try to load the image with it.
         */
        InputStream imageAsStream;
        try {
            //assert selectedImageFile != null;
            imageAsStream = new FileInputStream(selectedImageFile);
        } catch (FileNotFoundException e) {
            printSysOut("Somehow, image file not found");
            throw new RuntimeException(e);
        }
        /*
            Fire up the image in the GUI
         */
        anImage = new Image( imageAsStream );
        imgImageView.setImage( anImage );

        imgImageView.setPreserveRatio(true);

        /*
            Don't need the following because the ScrollPane is already connected
            to the imageview by fxml
         */
        //spScrollPane = new ScrollPane(imgImageView);
        spImagePane.setPannable(true);
        spImagePane.setHvalue(0.5);
        spImagePane.setVvalue(0.5);

        /*
            Scrollbar shows us where we are in the list
         */
        sbImageListScrollBar.setValue( intImageIndex);
         /*
            Now set up a scroll wheel based zoom
            and set a default zoom scale
         */

        dZoomScale = 1.0;
        printSysOut("setup Wheel to zoom - ImageView Scroll Event");
        imgImageView.setOnScroll(
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        event.consume();

                        //printSysOut("Zoom with wheel");
                        double zoomFactor = 1.05;
                        double deltaY = event.getDeltaY();
                    /*
                        Don't zoom forever. Just ignore it after
                        a while.
                     */
                        double dScale = dZoomScale;
                        if (deltaY > 0.0 && dScale > 10.0) {
                            printSysOut("Don't scale too big");
                            //event.consume();
                            return;
                        } else if (deltaY < 0.0 && dScale < 0.20) {
                            printSysOut("Don't scale too small");
                            //event.consume();
                            return;
                        }

                        if (deltaY < 0) {
                            zoomFactor = 0.95;
                        }

                        var x = spImagePane.getHvalue();
                        var y = spImagePane.getVvalue();
                        /*
                            What happens if we don't restore x and y?
                         */
                        /*
                            Let's zoom image with setFitWidth rather than setScale
                            but save our zoom so we can report it
                         */
                        dZoomScale = dZoomScale * zoomFactor;
                        imgImageView.setFitWidth(anImage.getWidth() * zoomFactor);

                        String scaleReport = String.format("ImageView scale factor %.3f", imgImageView.getScaleX() * dZoomScale);
                        setStatus(String.format("Zoom %.3f", dZoomScale));

                        /*
                            Lets try this here and see if that fixes the pan after zoom
                         */
                        x = spImagePane.getHvalue();
                        y = spImagePane.getVvalue();

                        /*
                         ********************************************************************
                         *************** The following statement appears to have made it work
                         * Now wheel zooming preserves panning to the corners
                         ********************************************************************
                         */
                        imgImageView.setFitWidth(anImage.getWidth() * dZoomScale);
                        /*
                            What happens if we don't restore x and y?
                         */

                        spImagePane.setHvalue(x);
                        spImagePane.setVvalue(y);

                    }
                } );
        long intEndOpen = System.currentTimeMillis();
        printSysOut(String.format("OpenImageFromList %d ms", intEndOpen - intStartOpen));
    }

    /*
        Enable / Disable the function buttons based on whether we have
        paths that are valid.
     */
    void EnableFunctionButtons() {
        boolean bSourceBlank = txtSourcePath.getText().isBlank();
        boolean bDestinationBlank = txtDestPath.getText().isBlank();

        if ( bSourceBlank && bDestinationBlank ) {
            btnViewSource.setDisable( true );
            btnViewDestination.setDisable( true );
            btnCopySource.setDisable( true );
            btnDeleteSource.setDisable( true );
            return;
        }
        if (bSourceBlank) {
            btnViewSource.setDisable( true );
            btnDeleteSource.setDisable( true );
            btnCopySource.setDisable( true );
            return;
        }
        if ( bDestinationBlank ) {
            btnViewDestination.setDisable( true );
            btnCopySource.setDisable( true );
            return;
        }
        /*
            Check to see if we have valid paths and set the buttons accordingly
         */
        File fileSrc = new File( txtSourcePath.getText() );
        File fileDst = new File( txtDestPath.getText() );
        btnViewSource.setDisable( !fileSrc.isDirectory() );
        btnDeleteSource.setDisable( !fileSrc.isDirectory() );
        btnViewDestination.setDisable( !fileDst.isDirectory() );
        btnCopySource.setDisable( !( fileSrc.isDirectory() && fileDst.isDirectory() ) );

    }

    /*
        Called from App to set things up
     */
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

        intImageIndex = 0;
        bImagesValid = false;

        /*
            Set the function buttons and enable listeners for file paths
         */
        EnableFunctionButtons();
        txtSourcePath.textProperty().addListener( ( observable, oldvalue, newvalue) -> {
            EnableFunctionButtons();
        });
        txtDestPath.textProperty().addListener( ( observable, oldvalue, newvalue) -> {
            EnableFunctionButtons();
        });


    }

    public void OnMenuSavePairs(ActionEvent actionEvent) {
    }

    public void OnMenuCloseApplication(ActionEvent actionEvent) {
    }

    public void onAboutApplication(ActionEvent actionEvent) {
    }

    public void onGoImagesStart(ActionEvent actionEvent) {
        if ( bImagesValid ) {
            intImageIndex = 0;
            OpenImageFromList();
            setStatus("First Image Displayed");
        } else {
            setStatus("No images to display");
        }
    }

    public void onGoImageBack10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 10 ) {
            intImageIndex = intImageIndex - 10;
            OpenImageFromList();
            setStatus("Back 10 Images Displayed");
        } else {
            onGoImagesStart( actionEvent );
        }
    }

    public void onGoImageBackOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 1 ) {
            intImageIndex--;
            OpenImageFromList();
            setStatus("Back 1 Image Displayed");
        } else {
            onGoImagesStart( actionEvent );
        }
    }

    public void onGoImageForwardOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -1 ) ) {
            intImageIndex++;
            OpenImageFromList();
            setStatus("Forward 1 Image Displayed");
        } else {
            onGoImagesEnd(actionEvent);
        }
    }

    public void onGoImageForward10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -11 ) ) {
            intImageIndex += 10;
            OpenImageFromList();
            setStatus("Forward 10 Image Displayed");
        } else {
            onGoImagesEnd(actionEvent);
        }
    }

    public void onGoImagesEnd(ActionEvent actionEvent) {
        if ( bImagesValid ) {
            intImageIndex = sImageList.length - 1;
            OpenImageFromList();
            setStatus("Last Image Displayed");
            printSysOut("Last Image Displayed");
        }
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
            EnableFunctionButtons();
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
            EnableFunctionButtons();
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
    private String[] GetImagesInFolder( String sFolder, boolean bUsePfx, boolean bUseSubFolders ){

        long intStartOpen = System.currentTimeMillis();
        /*
            we care about only three image types: *.bmp, *.jpg, *.png
            for Destination, we use the File Prefix. For source, get all images.
         */
        String sFPfx = "";
        if ( bUsePfx) {
            sFPfx = txtFilePrefix.getText();
        }

        /*
            Set things up to search for subfolders if we should
            Should only be used for Source since some games store images
            in subfolders, oddly.
         */
        boolean bSubFolders = false;
        String sSubPfx = "";
        if ( bUseSubFolders ) {
            bSubFolders = chkSearchSubFolders.isSelected();
            sSubPfx = "**\\";
        }
        /*
            Save the base path because we need it later to find
            the images. Only the file names are saved in the
            scanner result list.
         */
        sImageBasePath = sFolder;
        String[] saIncludeImages = new String[]{sSubPfx+sFPfx + "*.bmp",sFPfx + sSubPfx+ "*.jpg", sSubPfx+sFPfx+"*.png"  };

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( saIncludeImages );
        scanner.setCaseSensitive( false );
        scanner.setBasedir( new File( sFolder ));
        scanner.scan();

        String[] files = scanner.getIncludedFiles();
        int iHowMany = files.length;

        /*
            Set the scroll bar limits to show the position in the list as
            we view images.
         */
        sbImageListScrollBar.setMax( iHowMany-1 );
        sbImageListScrollBar.setMin( 0 );

        printSysOut(String.format("GetImagesInFolder found %d files in %s", iHowMany, sFolder));
        long intEndOpen = System.currentTimeMillis();
        printSysOut(String.format("GetImagesInFolder %d ms", intEndOpen - intStartOpen));
        return files;

    }

    /*
        Scan the Source for files. Don't use the File Prefix in the Source
     */
    public void OnViewSource(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder(txtSourcePath.getText(), false, true);

        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
            onGoImagesEnd( actionEvent );
        }
        setStatus(String.format("Source Images Found: %d", sImageFileNames.length));
    }

    public void OnViewDestination(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder( txtDestPath.getText(), true , false);

        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
            onGoImagesEnd( actionEvent );
        }
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

    public void ImgOnMouseClicked(MouseEvent mouseEvent) {
    }

    /*
        The following is called on a drag, and its very existence
        makes the image drag a success
     */
    public void ImgOnMouseDragged(MouseEvent mouseEvent) {
        //printSysOut("ImgOnMouseDragged");
    }
}