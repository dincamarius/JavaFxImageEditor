package sample;

import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.scene.shape.Rectangle;

import javafx.beans.value.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController
{
    @FXML
    private Button selectImageBtn;

    @FXML
    private Button updateImgBtn;

    @FXML
    private Button saveBtn;

    @FXML
    Accordion menuBar;

    @FXML
    private CheckBox grayFlag;

    private String grayLevelValues[] = {"Gray level white","Gray level normal","Gray level dark"};

    @FXML
    private ChoiceBox grayLevel;

    @FXML
    private ImageView modifiedImg;

    @FXML
    private TextField widthFld;

    @FXML
    private TextField heightFld;

    @FXML
    private Button applyBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Hyperlink browseLink;

    @FXML
    private Label dragDropLbl;

    @FXML
    private HBox hboxImg;

    @FXML
    private CheckBox autoAdjustFlg;

    @FXML
    private ToggleButton cropTgl;

    @FXML
    private Slider opacitySlider  = new Slider(0,1,0);

    @FXML
    private Label opacityValue = new Label(
            Double.toString(opacitySlider.getValue()));

    @FXML
    private Slider sepiaSlider = new Slider(0,1,0);

    @FXML
    private Label sepiaValue = new Label (
            Double.toString(sepiaSlider.getValue()));

    @FXML
    private Slider scalingSlider = new Slider(0,1,0);

    @FXML
    private Label scalingValue = new Label(
            Double.toString(scalingSlider.getValue())
    );

    @FXML
    private Slider brightnessSlider = new Slider (0,1,0.5);

    @FXML
    private Label brightnessValue = new Label (
            Double.toString(brightnessSlider.getValue()));



    private void setInitial() {
        System.out.println("Initial function called");
        InputStream indexImgInputStream=Main.class.getResourceAsStream("index.jpg");
        Image indexImg = new Image(indexImgInputStream);
        modifiedImg.setFitHeight(54);
        modifiedImg.setFitWidth(62);
        modifiedImg.setTranslateX(150);
        modifiedImg.setTranslateY(0);
        browseLink.setVisible(true);
        dragDropLbl.setVisible(true);
        modifiedImg.setImage(indexImg);
        menuBar.setVisible(false);
        updateImgBtn.setVisible(false);
        saveBtn.setVisible(false);


        grayLevel.getItems().clear();
        grayFlag.setSelected(false);

        opacitySlider.adjustValue(100);
        opacityValue.setText("1.00");
        scalingSlider.setValue(1);
        scalingValue.setText("1.00");
        brightnessValue.setText("0.00");
        brightnessSlider.setValue(0);
        sepiaValue.setText("0.00");
        sepiaSlider.adjustValue(0);

        widthFld.clear();
        heightFld.clear();
        autoAdjustFlg.setSelected(false);

    }
    private Stage m_stage;


    public void setStage(Stage stage)
    {
        m_stage = stage;
    }



    @FXML
    private void initialize()  {
        System.out.println("initialize");


        setInitial();

        selectImageBtn.setOnMouseClicked((MouseEvent evt) ->
        {
            setInitial();
            System.out.println("selectImageBtn:: Clicked");
            resetMenu();
            File chooseFile = selectFile();
            processImageFile(chooseFile);
        });


        updateImgBtn.setOnMouseClicked((MouseEvent evt) -> {

            System.out.println("updateImgBtn:: Clicked");
            menuBar.setVisible(true);
            grayLevel.setDisable(true);

        });

        applyBtn.setOnMouseClicked((MouseEvent evt) -> {

            System.out.println("applyBtn:: Clicked");
            resizeImg();
            autoAdjustFlg.setSelected(false);
        });

        resetBtn.setOnMouseClicked((MouseEvent evt) -> {

            System.out.println("resetBtn:: Clicked");

            modifiedImg.setFitHeight(600);
            modifiedImg.setFitWidth(950);
            autoAdjustFlg.setSelected(false);

        });

        browseLink.setOnAction((event) -> {

            System.out.println("Browse hyperlink:: Clicked");
            resetMenu();
            File browseFile = selectFile();
            processImageFile(browseFile);

        });

    }

    @FXML
    private void handleDragOver(DragEvent event){

        if (event.getDragboard().hasFiles()){

            event.acceptTransferModes(TransferMode.ANY);
        }

    };

    @FXML
    private void handleDrop(DragEvent event)  {

        List<File> files = event.getDragboard().getFiles();
        setInitial();

        if (files.size() == 1 ) {
            System.out.println("Size: " + files.size() + " file dropped: " + files.get(0).getAbsolutePath());
            String filePath = files.get(0).getAbsolutePath();
            try {
                String mimetype = Files.probeContentType(files.get(0).toPath());
                System.out.println("Mimetype: " + mimetype);
                if (mimetype.substring(0, 5).equalsIgnoreCase("image")) {
                    System.out.println("Image file selected: " + filePath);
                    resetMenu();
                    processImageFile(files.get(0));
                } else {
                    System.out.println("Not an image file selected: " + filePath);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Not an image file selected. Please drag&drop an image file!", ButtonType.OK);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }
                    setInitial();
                }
            } catch (Exception e) {
                e.getMessage();
                System.out.println("Not an image file selected: " + filePath);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Not an image file selected. Please drag&drop an image file!", ButtonType.OK);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    alert.close();
                }
                setInitial();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a single image file!", ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
            setInitial();
        }

    }


    private File selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image file...");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files(*.png, *.jpg, *.bmp, *.jpeg)", "*.png", "*.jpg", "*.bmp", "*.jpeg")
        );
        // fileChooser.setInitialDirectory(new File("G:\\Learning\\FirstFXApp\\src\\images"));
        File file = fileChooser.showOpenDialog(m_stage);
        return file;

    }

    private void processImageFile(File file) {

        if (file != null )
        {
            System.out.println(file.getAbsolutePath());
            Image imgnew = new Image(file.toURI().toString());
            modifiedImg.setImage(imgnew);


            grayFlag.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                                    Boolean old_val, Boolean new_val)
                {
                    System.out.println("grayFlag: "+new_val );
                    WritableImage grayImage=convertToGrayScale(imgnew, "normal");
                    modifiedImg.setImage(new_val ? grayImage: imgnew);
                    grayLevel.setDisable(new_val ? false: true);

                }
            });

            autoAdjustFlg.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    System.out.println("autoAdjustFlg: "+newValue );
                    modifiedImg.setPreserveRatio(newValue ? true:false);
                }
            });

            cropTgl.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    System.out.println("Crop toggle button: "+newValue);
                    String onTgl = "-fx-background-color: lightskyblue";
                    cropTgl.setStyle(newValue ?  onTgl : null);

                    if (newValue) {
                        Rectangle cropRect = new Rectangle(150,150);
                        Image cropImg = modifiedImg.getImage();


                    }
                }
            });

            for (int i=0; i<3; i++){
                grayLevel.getItems().add(grayLevelValues[i]);
            }

            grayLevel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                    if (grayLevel.getItems().get((Integer) newValue) == grayLevelValues[0]) {
                        WritableImage grayImage=convertToGrayScale(imgnew, "whiter");
                        modifiedImg.setImage(grayImage);
                    }
                    else if (grayLevel.getItems().get((Integer) newValue) == grayLevelValues[2]){
                        WritableImage grayImage=convertToGrayScale(imgnew, "darker");
                        modifiedImg.setImage(grayImage);
                    }
                    else {
                        WritableImage grayImage=convertToGrayScale(imgnew, "normal");
                        modifiedImg.setImage(grayImage);
                    }
                }
            });


            saveBtn.setOnMouseClicked((MouseEvent evt) -> {

                System.out.println("saveBtn:: clicked");
                File outputfile = getImageOutputFile(file);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(modifiedImg.snapshot(null, null), null), "png", outputfile);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "File saved at: "+outputfile, ButtonType.OK);
                    alert.setTitle("Image saved");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error saving file", ButtonType.OK);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }
                }

            });

            opacitySlider.adjustValue(100);
            opacityValue.setText("1.00");
            opacitySlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    modifiedImg.setOpacity(newValue.doubleValue()/100);
                    opacityValue.setText(String.format("%.2f", (double)newValue/100));
                }
            });

            sepiaValue.setText("0.00");
            sepiaSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                    SepiaTone sepiaEffect = new SepiaTone();
                    sepiaEffect.setLevel(newValue.doubleValue()/100);
                    modifiedImg.setEffect(sepiaEffect);
                    sepiaValue.setText(String.format("%.2f", (double)newValue/100));
                }
            });

            scalingSlider.setMax(3);
            scalingSlider.setValue(1);
            scalingValue.setText("1.00");
            scalingSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    modifiedImg.setScaleX((double)newValue);
                    modifiedImg.setScaleY((double)newValue);
                    scalingValue.setText(String.format("%.2f",newValue));
                }
            });

            brightnessSlider.setMax(100);
            brightnessValue.setText("0.00");
            brightnessSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    ColorAdjust brightnessAdjust = new ColorAdjust();
                    brightnessAdjust.setBrightness(newValue.doubleValue()/100);
                    modifiedImg.setEffect(brightnessAdjust);
                    brightnessValue.setText(String.format("%.2f",(double)newValue/100));

                }
            });

            updateImgBtn.setVisible(true);
            saveBtn.setVisible(true);

        }
        else {

            setInitial();

        }
    }


    private File getImageOutputFile(File file)
    {
        String path = file.getAbsolutePath();
        int index = path.lastIndexOf(".");
        String extension = path.substring(index);
        String fileName = path.substring(0, index);
        String outputPath = fileName + "_modified" + extension;

        File outputFile = new File(outputPath);
        return outputFile;
    }

    private WritableImage convertToGrayScale(Image img, String str)
    {

        PixelReader pixelReader = img.getPixelReader();
        WritableImage wImage = new WritableImage(
                (int)img.getWidth(),
                (int)img.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();

        for (int row=0; row<img.getHeight(); row++) {
            for (int column=0; column<img.getWidth(); column++) {
                Color color = pixelReader.getColor(column, row);

                double b = color.getBlue();
                double g = color.getGreen();
                double r = color.getRed();
                int avg = (int)(((b+g+r)/3) * 255);

                switch(str){
                    case "whiter":
                        if (avg < 50 ) {avg = avg + 50;}
                        break;
                    case "normal":
                        break;
                    case "darker":
                        if (avg > 50 ) {avg = avg -50;}
                        break;
                }

                color = Color.rgb(avg, avg, avg);
                pixelWriter.setColor(column, row, color);
            }
        }
        return wImage;
    }

    private void resizeImg(){

        try {
            Integer valueW = Integer.parseInt(widthFld.getText());
            Integer valueH = Integer.parseInt(heightFld.getText());
            modifiedImg.setPreserveRatio(false);
            modifiedImg.setFitWidth(valueW);
            modifiedImg.setFitHeight(valueH);
        } catch(Exception e) {
            System.out.println("Non-numeric character exist");

            Alert alert = new Alert(Alert.AlertType.ERROR, "Non-numeric characters found. Please insert valid values for weight and height", ButtonType.OK);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }

            widthFld.clear();
            heightFld.clear();

        }

    }

    public void resetMenu(){

        menuBar.setVisible(false);
        modifiedImg.setFitHeight(600);
        modifiedImg.setFitWidth(950);
        modifiedImg.setTranslateX(-50);
        modifiedImg.setTranslateY(-50);
        modifiedImg.setImage(null);

        browseLink.setVisible(false);
        dragDropLbl.setVisible(false);

    }


}
