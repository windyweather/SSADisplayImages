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
            setStatus("OpenImageFromList no images");
            return;
        }
        /*
            Get the image we are interested in
         */
        String imageFilePath = sImageList[intImageIndex];
        printSysOut(String.format( "openImageFromList - Opening %s", imageFilePath ));
        File selectedImageFile = new File( imageFilePath );
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

    }

    public void OnMenuSavePairs(ActionEvent actionEvent) {
    }

    public void OnMenuCloseApplication(ActionEvent actionEvent) {
    }

    public void onAboutApplication(ActionEvent actionEvent) {
    }

    public void onGoImagesStart(ActionEvent actionEvent) {
        intImageIndex = 0;
        OpenImageFromList();
        setStatus("First Image Displayed");
    }

    public void onGoImageBack10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 10 ) {
            intImageIndex = intImageIndex - 10;
            OpenImageFromList();
            setStatus("Back 10 Images Displayed");
        }
    }

    public void onGoImageBackOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex >= 1 ) {
            intImageIndex--;
            OpenImageFromList();
            setStatus("Back 1 Image Displayed");
        }
    }

    public void onGoImageForwardOne(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -1 ) ) {
            intImageIndex++;
            OpenImageFromList();
            setStatus("Forward 1 Image Displayed");
        }
    }

    public void onGoImageForward10(ActionEvent actionEvent) {
        if ( bImagesValid && intImageIndex < (sImageList.length -11 ) ) {
            intImageIndex++;
            OpenImageFromList();
            setStatus("Forward 10 Image Displayed");
        }
    }

    public void onGoImagesEnd(ActionEvent actionEvent) {
        if ( bImagesValid ) {
            intImageIndex = sImageList.length - 1;
        }
        OpenImageFromList();
        setStatus("Last Image Displayed");
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

        String[] sImageFileNames = GetImagesInFolder(txtSourcePath.getText(), false);
        setStatus(String.format("Source Images Found: %d", sImageFileNames.length));
        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
        }
    }

    public void OnViewDestination(ActionEvent actionEvent) {

        String[] sImageFileNames = GetImagesInFolder( txtDestPath.getText(), true );
        setStatus(String.format("Destination Images Found: %d", sImageFileNames.length));
        if (sImageFileNames.length > 0) {
            bImagesValid = true;
            sImageList = sImageFileNames;
            intImageIndex = sImageList.length - 1;
        }
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