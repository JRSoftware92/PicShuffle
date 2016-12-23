package application.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author John Riley
 * 
 * Simple Model class containing a list of image files in the playlist
 */
public class ImagePlaylist implements Serializable{
	
	private static final long serialVersionUID = 0;
	
	/* Serialized Image Playlist File Extension */
	public static final String FILE_EXTENSION = ".sip";
	
	protected List<String> imageFiles;
	
	public ImagePlaylist(){
		imageFiles = new ArrayList<String>();
	}
	
	public ImagePlaylist(int initialSize){
		imageFiles = new ArrayList<String>(initialSize);
	}
	
	public boolean isEmpty(){
		return imageFiles.isEmpty();
	}
	
	public int size(){
		return imageFiles.size();
	}
	
	public String get(){
		if(isEmpty())
			return null;
		return imageFiles.get(0);
	}
	
	public String get(int index){
		if(isEmpty() || index < 0 || index >= size())
			return null;
		
		return imageFiles.get(index);
	}
	
	public void clear(){
		imageFiles.clear();
	}
	
	public void remove(int index){
		if(isGoodIndex(index))
			imageFiles.remove(index);
	}
	
	/**
	 * 
	 * @param filePath
	 * @return true if the file can safely be added to the playlist, false otherwise
	 */
	public boolean add(String filePath){
		if(filePath != null && AppUtils.isImageFile(filePath)){
			imageFiles.add(filePath);
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean add(File file){
		if(isGoodFile(file)){
			String str = AppUtils.getImageFileName(file);
			if(str != null){
				imageFiles.add(str);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public boolean add(int index, File file) {
		if(isGoodIndex(index) && isGoodFile(file)){
			String str = AppUtils.getImageFileName(file);
			if(str != null){
				imageFiles.add(index, str);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public boolean addAll(Collection<String> filePaths){
		if(filePaths != null){
			boolean flag = true;
			for(String filePath : filePaths){
				if(filePath != null && AppUtils.isImageFile(filePath))
					imageFiles.add(filePath);	
				else
					flag = false;
			}
			imageFiles.addAll(filePaths);
			
			return flag;
		}
		else{
			return false;
		}
	}
	
	public boolean addAllFiles(File... files) {
		if(files != null){
			String str;
			boolean flag = true;
			for(File file : files){
				if(AppUtils.isImageFile(file)){
					str = AppUtils.getImageFileName(file);
					if(str != null)
						imageFiles.add(str);
					else
						flag = false;
				}
				else{
					flag = false;
				}
			}
			
			return flag;
		}
		else{
			return false;
		}
	}
	
	public boolean addAllFiles(Collection<File> files){
		if(files != null){
			String str;
			boolean flag = true;
			for(File file : files){
				if(AppUtils.isImageFile(file)){
					str = AppUtils.getImageFileName(file);
					if(str != null)
						imageFiles.add(str);
					else
						flag = false;
				}
				else{
					flag = false;
				}
			}
			
			return flag;
		}
		else{
			return false;
		}
	}
	
	protected boolean isGoodFile(File file){
		return file != null && file.isFile() && AppUtils.isImageFile(file);
	}
	
	protected boolean isGoodIndex(int index){
		return index > -1 && index < size();
	}
}
