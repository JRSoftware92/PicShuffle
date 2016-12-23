package application.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author John Riley
 * Subclass of ImagePlaylist that allows built in index tracking
 */
public class IndexedImagePlaylist extends ImagePlaylist{
	private static final long serialVersionUID = 1;
	
	private int currentIndex;
	private boolean isCircular;
	
	public IndexedImagePlaylist(){
		super();
		currentIndex = 0;
		isCircular = false;
	}
	
	public IndexedImagePlaylist(boolean isCircular){
		this();
		this.isCircular = isCircular;
	}
	
	public IndexedImagePlaylist(int initialSize){
		this();
		currentIndex = 0;
	}
	
	public IndexedImagePlaylist(int initialSize, boolean isCircular){
		this(initialSize);
		this.isCircular = isCircular;
	}
	
	public IndexedImagePlaylist(ImagePlaylist playlist){
		this.imageFiles = playlist.imageFiles;
		currentIndex = 0;
		isCircular = false;
	}
	
	public IndexedImagePlaylist(ImagePlaylist playlist, boolean isCircular){
		this.imageFiles = playlist.imageFiles;
		currentIndex = 0;
		this.isCircular = isCircular;
	}
	
	public int currentIndex(){
		return currentIndex;
	}
	
	public void resetIndex(){
		currentIndex = 0;
	}
	
	public String get(){
		return get(currentIndex);
	}
	
	public String getNext(int n){
		int size = size();
		if((currentIndex + n) < size){
			return get(currentIndex + n);
		}
		else{
			return get(currentIndex + n - size);
		}
	}
	
	public String getPrevious(int n){
		int size = size();
		if(currentIndex >= n){
			return get(currentIndex - n);
		}
		else{
			return get(currentIndex - n + size);
		}
	}
	
	public void clear(){
		super.clear();
		currentIndex = 0;
	}
	
	public void remove(int index){
		super.remove(index);
		if(currentIndex == size())
			currentIndex--;
		else if(currentIndex > size())
			currentIndex -= 2;
	}
	
	/**
	 * Decrements the index
	 * @return The item located at currentIndex - 1
	 */
	public String prev(){
		if(--currentIndex < 0){
			if(isCircular)
				currentIndex = size() - 1;
			else
				currentIndex = 0;
		}
		return get(currentIndex);
	}
	
	/**
	 * Increments the index
	 * @return The item located at currentIndex + 1
	 */
	public String next(){
		int size = size();
		if(++currentIndex >= size){
			if(isCircular)
				currentIndex = 0;
			else
				currentIndex = size - 1;
		}
		return get(currentIndex);
	}
	
	/**
	 * TODO - Test
	 * @return
	 */
	public String back(int n){
		if(currentIndex >= n){
			currentIndex -= n;
		}
		else{
			currentIndex -= n;
			currentIndex += size();
		}
		return get();
	}
	
	/**
	 * TODO - Test
	 * @return
	 */
	public String forward(int n){
		int size = size();
		if((currentIndex + n) < size){
			currentIndex += n;
		}
		else{
			currentIndex += n;
			currentIndex -= size;
		}
		return get();
	}
	
	public String first(){
		currentIndex = 0;
		return get();
	}
	
	public String last(){
		currentIndex = size() - 1;
		return get();
	}
	
	public void shuffle(Random rng){
		if(rng == null)
			rng = new Random();
		ArrayList<String> list = new ArrayList<String>(size());
		int index;
		String str;
		while(!isEmpty()){
			index = rng.nextInt(size());
			str = this.imageFiles.get(index);
			
			this.imageFiles.remove(index);
			list.add(str);
		}
		
		this.imageFiles = list;
	}
}
