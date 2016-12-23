package application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import application.model.AppUtils;
import application.model.ImagePlaylist;
import application.model.IndexedImagePlaylist;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author John Riley
 * Main entry point for the PicShuffle application
 */
public class Main extends Application {
	
	//private static final String RES_STOCK_IMAGE = "res/camera-icon-full.png";
	//private static final String RES_STOCK_THUMBNAIL = "res/camera-icon.png";
	
	private Stage stage;
	private Controller controller;
	
	private IndexedImagePlaylist imagePlaylist, unshuffledPlaylist;
	
	private Random rng;
	private boolean isShuffleOn;
	
	public static void main(String[] args) {
		launch(args);
	}
	
    @Override
    public void start(Stage stage) throws Exception {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainPane.fxml"));
        controller = new Controller();
        loader.setController(controller);
       
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 1000);
        scene.setOnKeyPressed(controller.onKeyPressHandler);
    
        stage.setTitle("PicShuffle");
        stage.setScene(scene);
        stage.show();
       
        this.stage = stage;
        isShuffleOn = false;
        imagePlaylist = unshuffledPlaylist = new IndexedImagePlaylist();
        rng = new Random(System.currentTimeMillis());
    }
    
    /*---------------------Image Display Methods---------------------------*/
    
    protected void updateMainImage(){
    	if(!imagePlaylist.isEmpty())
    		controller.displayMainImage(imagePlaylist.get());
    }
    
    protected void updateThumbnails(){
    	if(!imagePlaylist.isEmpty()){
    		controller.displayCurrentThumbnail(imagePlaylist.get());
    		if(imagePlaylist.currentIndex() > 0){
    			controller.displayPreviousThumbnail(imagePlaylist.getPrevious(1));
    		}
    		
    		if(imagePlaylist.currentIndex() > 1){
    			controller.displayFirstThumbnail(imagePlaylist.getPrevious(2));
    		}
    		
    		if(imagePlaylist.size() > (imagePlaylist.currentIndex() + 1)){
    			controller.displayNextThumbnail(imagePlaylist.getNext(1));
    		}
    		
    		if(imagePlaylist.size() > (imagePlaylist.currentIndex() + 2)){
    			controller.displayLastThumbnail(imagePlaylist.getNext(2));
    		}
    	}
    }
    
    //-----------------------Playlist Update Methods---------------------------------------//
    
    protected void addToPlaylist(List<File> files){
    	try{
    		imagePlaylist.last();
    		
    		boolean flag = imagePlaylist.addAllFiles(files);
    		imagePlaylist.next();
    		
    		if(!flag){
    			AppUtils.showErrorDialog(
    					"At least one file you selected was not " +
    					"a valid file. Please make sure to only select image files."
    			);
    		}
    	}
    	catch(Exception e){
    		String message = e.getMessage();
    		if(message != null)
    			AppUtils.showErrorDialog(message);
    	}
    }
    
    protected void overwritePlaylist(File... files){
    	imagePlaylist.clear();
    	try{
    		boolean flag = imagePlaylist.addAllFiles(files);
    		if(!flag){
    			AppUtils.showErrorDialog(
    					"At least one file you selected was not " +
    					"a valid file. Please make sure to only select image files."
    			);
    		}
    	}
    	catch(Exception e){
    		String message = e.getMessage();
    		if(message != null)
    			AppUtils.showErrorDialog(message);
    	}
    }
    
    protected void overwritePlaylist(List<File> files){
    	imagePlaylist.clear();
    	try{
    		boolean flag = imagePlaylist.addAllFiles(files);
    		if(!flag){
    			AppUtils.showErrorDialog(
    					"At least one file you selected was not " +
    					"a valid file. Please make sure to only select image files."
    			);
    		}
    	}
    	catch(Exception e){
    		String message = e.getMessage();
    		if(message != null)
    			AppUtils.showErrorDialog(message);
    	}
    }
    
    class Controller {
    	
    	@FXML
    	private Button buttonShuffle;
    	
    	@FXML
    	ImageView imageView;
    	@FXML
    	ImageView imageViewFirst;
    	@FXML
    	ImageView imageViewLast;
    	@FXML
    	ImageView imageViewPrevious;
    	@FXML
    	ImageView imageViewNext;
    	@FXML
    	ImageView imageViewCurrent;
    	
    	private double sceneX, sceneY, translateX, translateY;
    	private DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    	
    	@FXML
        public void initialize() {
    		/**
    		 * http://www.java2s.com/Code/Java/JavaFX/JavaFXImageZoomExample.htm
    		 */
    		zoomProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable arg0) {
                    imageView.setFitWidth(zoomProperty.get() * 4);
                    imageView.setFitHeight(zoomProperty.get() * 3);
                }
            });

            imageView.addEventFilter(ScrollEvent.ANY, onImageScrollHandler);
            imageView.setOnMousePressed(imageOnMousePressedHandler);
            imageView.setOnMouseDragged(imageOnMouseDraggedHandler);
    	}
    	
    	/*--------------------ImageView Display Methods--------------------*/

        protected void displayImage(ImageView imageView, String imagePath){
        	boolean isImageFile = AppUtils.isImageFile(imagePath);
        	if(imageView != null && imagePath != null && isImageFile){
        		imageView.setImage(new Image(imagePath));
        	}
        	else{
        		AppUtils.showErrorDialog(
    					"The application has experienced an issue while " +
    					"trying to display one of the selected images."
    			);
        	}
        }
        
        protected void displayMainImage(String imagePath){
        	displayImage(imageView, imagePath);
        }
        
        protected void displayCurrentThumbnail(String imagePath){
        	displayImage(imageViewCurrent, imagePath);
        }
        
        protected void displayPreviousThumbnail(String imagePath){
        	displayImage(imageViewPrevious, imagePath);
        }
        
        protected void displayNextThumbnail(String imagePath){
        	displayImage(imageViewNext, imagePath);
        }
        
        protected void displayFirstThumbnail(String imagePath){
        	displayImage(imageViewFirst, imagePath);
        }
        
        protected void displayLastThumbnail(String imagePath){
        	displayImage(imageViewLast, imagePath);
        }
    	
    	/*-------------------------Button Events-------------------------------*/
        
        @FXML
        protected void handleShuffleButton(ActionEvent event){
        	if(isShuffleOn){
        		isShuffleOn = false;
        		imagePlaylist = unshuffledPlaylist;
        	}
        	else{
        		isShuffleOn = true;
        		unshuffledPlaylist = imagePlaylist;
        		imagePlaylist.shuffle(rng);
        	}
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handlePreviousButton(ActionEvent event){
        	imagePlaylist.prev();
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handleNextButton(ActionEvent event){
        	imagePlaylist.next();
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handleSkipBackButton(ActionEvent event){
        	imagePlaylist.back(5);
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handleSkipForwardButton(ActionEvent event){
        	imagePlaylist.forward(5);
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        /*-------------------------Image Events-------------------------------*/
        
        @FXML
        protected void handleFirstImageClick(ActionEvent event){
        	imagePlaylist.back(2);
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handleLastImageClick(ActionEvent event){
        	imagePlaylist.forward(2);
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handlePrevImageClick(ActionEvent event){
        	imagePlaylist.prev();
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        @FXML
        protected void handleNextImageClick(ActionEvent event){
        	imagePlaylist.next();
        	
        	updateMainImage();
        	updateThumbnails();
        }
        
        
        /*-------------------------Menu Events-------------------------------*/
        
        @FXML
        protected void handleMenuItemClose(ActionEvent event){
        	try{
        		Stage stage = (Stage) imageView.getScene().getWindow();
        		stage.close();
        	}
        	catch(Exception e){
        		String message = e.getMessage();
        		if(message != null)
        			AppUtils.showErrorDialog(message);
        	}
        }
        
        @FXML
        protected void handleMenuItemOpen(ActionEvent event){
        	FileChooser fileDialog = new FileChooser();
        	fileDialog.setTitle("Open Image File");
        	
        	File file = fileDialog.showOpenDialog(stage);        	
        	if(file == null){
        		AppUtils.showErrorDialog("Invalid File Selected. File cannot be displayed.");
        		return;
        	}

        	//Open the file and overwrite the previous playlist
        	try{
            	if(!AppUtils.isImageFile(file)){
            		AppUtils.showErrorDialog("File specified is not of a valid type.\n" +
            								 "Please select a file with one of the following " +
            								 "types: .jpg, .jpeg, .png, .gif");
            		return;
            	}
            	
           		imagePlaylist.clear();
            	boolean flag = imagePlaylist.add(file);
            	if(!flag){
        			AppUtils.showErrorDialog(
        					"The file you selected was not " +
        					"a valid file. Please make sure to only select image files."
        			);
        		}
            	else{
            		updateMainImage();
            		updateThumbnails();
            	}
        	}
        	catch(Exception e){
        		String message = e.getMessage();
        		if(message != null)
        			AppUtils.showErrorDialog(message);
        	}
        }
        
        @FXML
        protected void handleMenuItemOpenImages(ActionEvent event){
        	FileChooser fileDialog = new FileChooser();
        	fileDialog.setTitle("Open Images");
        	
        	//Load the chosen images into the application as a new playlist
        	List<File> files = fileDialog.showOpenMultipleDialog(stage);
        	if (files != null && !files.isEmpty()) {
            	imagePlaylist.clear();
            	overwritePlaylist(files);
          
            	updateMainImage();
            	updateThumbnails();
        	}
        }
        
        @FXML
        protected void handleMenuItemOpenPlaylist(ActionEvent event){
        	FileChooser fileDialog = new FileChooser();
        	fileDialog.setTitle("Open Image Playlist");
        	
        	//Open the specified file as an image playlist
        	File file = fileDialog.showOpenDialog(stage);
        	if (file != null){
        		try{
        			String filePath = file.getAbsolutePath();
        			ImagePlaylist obj = AppUtils.readImagePlaylist(filePath);
        			imagePlaylist = new IndexedImagePlaylist(obj.size());
        			
        			int size = obj.size();
        			boolean tempFlag, flag = true;
        			String path;
        			for(int i = 0; i < size; i++){
        				path = obj.get(i);
        				/*
        				tempFlag = AppUtils.fileExists(path);
        				if(tempFlag)
        					imagePlaylist.add(path);
        				else
        					flag = false;
        					*/
        				imagePlaylist.add(path);
        			}
        			
        			if(!flag){
        				AppUtils.showErrorDialog(
            					"At least one file found in the playlist could " +
        						"not be found in the file system, and will not be displayed."
            			);
        			}
            	
        			updateMainImage();
        			updateThumbnails();
        		}
        		catch(ClassNotFoundException cnfe){
        			String message = cnfe.getMessage();
            		if(message != null)
            			AppUtils.showErrorDialog(String.format("Class Not Found Exception: %s", message));
        		}
        		catch(IOException io){
        			String message = io.getMessage();
            		if(message != null)
            			AppUtils.showErrorDialog(String.format("Error Reading File: %s", message));
        		}
        	}
        }
        
        @FXML
        protected void handleMenuItemSavePlaylist(ActionEvent event){
        	FileChooser fileDialog = new FileChooser();
        	fileDialog.setTitle("Save Image Playlist");
        	
        	File file = fileDialog.showSaveDialog(stage);
        	if(file != null){
        		try{
        			//Save Playlist to the specified Filepath
        			String filePath = file.getAbsolutePath();
        			AppUtils.writeImagePlaylist(filePath, imagePlaylist);
        		}
        		catch(IOException io){
        			String message = io.getMessage();
            		if(message != null)
            			AppUtils.showErrorDialog(String.format("Error Writing File: %s", message));
        		}
        	}
        }
        
        @FXML
        protected void handleMenuItemAddToPlaylist(ActionEvent event){
        	FileChooser fileDialog = new FileChooser();
        	fileDialog.setTitle("Add Images to Playlist");
        	
        	List<File> list = fileDialog.showOpenMultipleDialog(stage);
        	if(list != null && !list.isEmpty()){
        		addToPlaylist(list);
        		
            	updateMainImage();
            	updateThumbnails();
        	}
        }
        
        /*----------------------------------Event Listeners------------------------------------*/
        
        EventHandler<KeyEvent> onKeyPressHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:  
                    	handlePreviousButton(null);
                    	break;
                    case RIGHT: 
                    	handleNextButton(null); 
                    	break;
                    default:
                    	break;
                }
            }
        };
        
		/**
		 * http://www.java2s.com/Code/Java/JavaFX/JavaFXImageZoomExample.htm
		 * TODO - Implement Zoom without hiding controls
		 */
        EventHandler<ScrollEvent> onImageScrollHandler = new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        };
        
        /**
    	 * https://java-buddy.blogspot.com/2013/07/javafx-drag-and-move-something.html
    	 */
    	EventHandler<MouseEvent> imageOnMousePressedHandler = 
    			new EventHandler<MouseEvent>() {

    	        @Override
    	        public void handle(MouseEvent t) {
    	            sceneX = t.getSceneX();
    	            sceneY = t.getSceneY();
    	            translateX = imageView.getTranslateX();
    	            translateY = imageView.getTranslateY();
    	        }
    	};
    	
    	/**
    	 * https://java-buddy.blogspot.com/2013/07/javafx-drag-and-move-something.html
    	 */
    	EventHandler<MouseEvent> imageOnMouseDraggedHandler = 
    	        new EventHandler<MouseEvent>() {

    	        @Override
    	        public void handle(MouseEvent t) {
    	            double offsetX = t.getSceneX() - sceneX;
    	            double offsetY = t.getSceneY() - sceneY;
    	            double newTranslateX = translateX + offsetX;
    	            double newTranslateY = translateY + offsetY;
    	            
    	            imageView.setTranslateX(newTranslateX);
    	            imageView.setTranslateY(newTranslateY);
    	        }
    	};
    }
}