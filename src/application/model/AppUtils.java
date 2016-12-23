package application.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppUtils {
	
	public static boolean fileExists(String filePath){
		if(filePath == null || filePath.length() < 1)
			return false;
		
		File file = new File(filePath);
		return file.exists();
	}
	
	public static void showErrorDialog(String message){
		showDialog("An Error has Occurred", message);
	}
	
	public static void showDialog(String title, String message){
		Stage dialog = getMessageDialog(title, message);
		dialog.show();
	}
	
	public static Stage getMessageDialog(String title, String message){
		final Stage dialog = new Stage();
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setTitle(title);
     
		//Clicking the button closes the dialog
        Button okButton = new Button("CLOSE");
        okButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent arg0) {
            	dialog.close();
            }
         
        });
        okButton.setAlignment(Pos.CENTER);
        
        //Creates the Dialog Layout
        VBox dialogVbox = new VBox(20);
        Text text = new Text(message);
        text.setTextAlignment(TextAlignment.CENTER);
        dialogVbox.getChildren().add(text);
        dialogVbox.getChildren().add(okButton);
        dialogVbox.setAlignment(Pos.CENTER);
        
        //Initializes the dialog scene
        Scene dialogScene = new Scene(dialogVbox, 600, 200);
        dialog.setScene(dialogScene);
        
        return dialog;
	}
	
	public static ImagePlaylist readImagePlaylist(String filePath) throws ClassNotFoundException, 
																			IOException {
		ImagePlaylist output = null;
		InputStream file = new FileInputStream(filePath);
    	InputStream buffer = new BufferedInputStream(file);
    	ObjectInput input = new ObjectInputStream (buffer);
    	output = (ImagePlaylist)input.readObject();
    	
    	input.close();
    	buffer.close();
    	file.close();
	    
	    return output;
	}
	
	public static void writeImagePlaylist(String filePath, 
										ImagePlaylist imagePlaylist) throws IOException{
		OutputStream newFile = new FileOutputStream(filePath);
    	OutputStream buffer = new BufferedOutputStream(newFile);
    	ObjectOutput output = new ObjectOutputStream(buffer);
    	output.writeObject(imagePlaylist);
    	
    	output.close();
    	buffer.close();
    	newFile.close();
	}
	
	public static String getParentDirectory(File file){
		if(file == null)
			return null;
		
		return file.getParent();
	}
	
	public static String getImageFileName(File file){
		if(file == null)
			return null;
		
		try{
			URL url = file.toURI().toURL();
			return url.toString();
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static boolean isImageFile(File file){
		if(file == null || file.isDirectory())
			return false;
		
		String path = file.getAbsolutePath();
		if(path == null)
			return false;
		
		int i = path.lastIndexOf('.');
		if (i > 0) {
			String extension = path.substring(i + 1);
			return isImageExtension(extension);
		}
		else{
			return false;
		}
	}
	
	public static boolean isImageFile(String filePath){
		if(filePath == null)
			return false;
		
		int i = filePath.lastIndexOf('.');
		if (i > 0) {
			String extension = filePath.substring(i + 1);
			return isImageExtension(extension);
		}
		else{
			return false;
		}
	}
	
	public static boolean isImageExtension(String extension){
		String str = extension.toLowerCase();
		switch(str){
			case "jpg":
			case "jpeg":
			case "png":
			case "gif":
				return true;
			default:
				return false;
		}
	}
}
