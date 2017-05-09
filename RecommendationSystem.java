
import java.io.*;
import java.util.*; 
import java.util.Map.Entry;
import java.math.*;
import java.math.BigDecimal; 
import java.math.RoundingMode;

public class RecommendationSystem{
   
	private static BufferedReader ratingsReader;
	private static BufferedReader moviesReader;
	
	//ratings.dat attributes
	static int userId;
    static int ratingsMovieId;
    static Double rating;
    
    //movies.dat attributes
    static int movie_id;
    static String movie_title;
    static String movies_line;
    
    private static HashMap<Integer, String> movieStorage(File movies) throws IOException{
    	
    	HashMap<Integer, String> moviesMap = new HashMap<Integer, String>();

	        FileReader moviesFr = new FileReader(movies); 
	        moviesReader = new BufferedReader(moviesFr); 
	           
	        //Parse movie attributes
	        while((movies_line = moviesReader.readLine()) != null){
				String[] movie_attributes = movies_line.split("\\|"); 
				movie_id = new Integer(movie_attributes[0]);
				movie_title = new String(movie_attributes[1]); 
					   
				//Add key,value to movies HashMap
				moviesMap.put(movie_id, movie_title); 
			}
		return moviesMap;
    }
    
    private static HashMap<Integer, HashMap<Integer, Double>> ratingStorage(File ratings) throws NumberFormatException, IOException{
        
    	HashMap<Integer, HashMap<Integer, Double>> ratingsOuterMap = new HashMap<Integer, HashMap<Integer, Double>>();
    	
    	//The inner map stores a movieId as key and it's associated rating as value
        HashMap<Integer, Double> ratingsInnerMap = new HashMap<Integer, Double>(); 
        String ratings_line; 
        ratingsReader = new BufferedReader(new FileReader(ratings));
        
        FileReader ratingsFr = new FileReader(ratings);
        ratingsReader = new BufferedReader(ratingsFr);
        
        //Parse ratings attributes
        while((ratings_line = ratingsReader.readLine()) != null){
     	   String[] ratings_attributes = ratings_line.split("\\s+");
     	   userId = new Integer(ratings_attributes[0]);
     	   ratingsMovieId = new Integer(ratings_attributes[1]);
     	   rating = new Double(ratings_attributes[2]); 
		   
     	   if(ratingsOuterMap.containsKey(userId)){
     		 
     		 //Add key,value to inner map
         	 ratingsOuterMap.get(userId).put(ratingsMovieId, rating);
     	   } 
     	   else{
     		   ratingsInnerMap = new HashMap<Integer, Double>(); 
     		   ratingsInnerMap.put(ratingsMovieId, rating);
     		   
     		   //Add key,value(inner map) to outer map
         	   ratingsOuterMap.put(userId, ratingsInnerMap);   
     	   } 
        }
		return ratingsOuterMap;
    }
    public static int counter = 1;
    private static double computeSimilarity(int id1, int id2, HashMap<Integer, HashMap<Integer, Double>> map){ 
  	   Double square1 = 0.0;
  	   Double square2 = 0.0;
  	   Double sum1 = 0.0;
  	   Double sum2 = 0.0;
  	   Double numerator = 0.0;
  	   Double denominator = 0.0;
  	   Double similarity = 0.0; 
  	   Double result = 0.0; 
  	   
  	   for(Object i : map.keySet()){
  		   
  		   if(id2 < counter){
  			   
  		   }
  		   
  		    if(((HashMap<Integer, Double>) map.get(i)).containsKey(id1) && ((HashMap<Integer, Double>) map.get(i)).containsKey(id2)){
  			   
  			   Double multiplicand = (((HashMap<Integer, Double>) map.get(i)).get(id1)); 
  			   Double multiplier = (((HashMap<Integer, Double>) map.get(i)).get(id2)); 
  			   Double product = multiplicand * multiplier;
  			   
  			   numerator += product; 
  			   
  			   square1 = Math.pow(((HashMap<Integer, Double>) map.get(i)).get(id1), 2);
  			   square2 = Math.pow(map.get(i).get(id2), 2);
  			   
  			   sum1 += square1;
  			   sum2 += square2;
  		   }
  		  
  		  else if(((HashMap<Integer, Double>) map.get(i)).containsKey(id1) && !((HashMap<Integer, Double>) map.get(i)).containsKey(id2)){
  	  			  square1 = Math.pow(((HashMap<Integer, Double>) map.get(i)).get(id1), 2);
    			  sum1 += square1;
    		   }
    		   
    	  else if(!((HashMap<Integer, Double>) map.get(i)).containsKey(id1) && ((HashMap<Integer, Double>) map.get(i)).containsKey(id2)){
   			  square2 = Math.pow((map.get(i).get(id2)), 2);
  			  sum2 += square2;
  		   	}
    	  
  	      denominator = (Math.sqrt(sum1) * Math.sqrt(sum2));
  	      result = numerator / denominator;
  	      
  	   }
  	  return result;
     }  
    
    public static ArrayList<Integer> unRatedList = new ArrayList<>();

    public static ArrayList<Double> predictRating(int user, HashMap<Integer, HashMap<Integer, Double>> map, double[][] similarityList){
		double numerator = 0.0;
		double denominator = 0.0; 
		double rating = 0.0;

		ArrayList<Double> predictedRatingsList = new ArrayList<>();
		ArrayList<Integer> ratedList = new ArrayList<>();  
		
		for(int j = 1; j <= similarityList.length; j++){
			
			if(map.get(user).containsKey(j)){
				ratedList.add(j); 
			}
			else{
				
			unRatedList.add(j); 
			
			//Gets the ratings the user has given.
			for(Integer i : map.get(user).keySet()){              

					if(!map.get(user).get(i).isNaN()){
						
						//multiplies the rating by the similarity
						double multiplicand = map.get(user).get(i);
						double multiplier = similarityList[j-1][i-1];
						double product = multiplicand * multiplier;
						numerator += product;
						
						//Add the similarites of rated items
						denominator += multiplier;
					}	
				}
			
			//Calculates rating by taking numerator which is sum1 and denominator which is sum2.
			rating = numerator / denominator;
			
			//Adds predicted ratings to ArrayList
		    predictedRatingsList.add(rating);
		    numerator = 0.0;
		    denominator = 0.0;
			}			
		}

		//Returns ArrayList		
		return predictedRatingsList; 
    }
    
    
	public static void main(String[] args) throws NumberFormatException, IOException{
    	
       File ratingsFile = new File("ratings.dat");
       File moviesFile = new File("movies.dat"); 
       
       HashMap<Integer, HashMap<Integer, Double>> rating = ratingStorage(ratingsFile); 
       HashMap<Integer, String> movie = movieStorage(moviesFile);  
       double[][] similarityList = new double[movie.size()][movie.size()];
       ArrayList<Double> predictedRatings = new ArrayList<Double>(); 
       
       Double similarity = 0.0; 
       Double compare = 0.0;
       int count = 0;
       int unrated = 0;
       int renew = 0;
       int i; 
       
       for(int movie_Id1 : movie.keySet()){
    	   for(int movie_Id2 : movie.keySet()){
    		   similarity = computeSimilarity(movie_Id1, movie_Id2, rating);      
    		   similarityList[movie_Id1-1][movie_Id2-1] = similarity;
    	   }
       }
       
       for(int user : rating.keySet()){
    	   System.out.print("User ID : " + user + " Top 5 Recommendations: ");
    	   predictedRatings = predictRating(user, rating, similarityList);
       
    	   for(count = 0; count < 5; count++){														
    		   for(i = 0; i < predictedRatings.size(); i++){
    			   if(compare < predictedRatings.get(i)){
    				   compare = predictedRatings.get(i);
    				   renew = predictedRatings.indexOf(compare); 
    				   unrated = unRatedList.get(i); 
    			   	}
    		   	}   
    	   
    		   predictedRatings.remove(compare); 
    		   predictedRatings.add(renew, 0.0); 
      
    	  for(Entry<Integer, String> movies : movie.entrySet()){
    		  if(compare == 0.0){
    		  
    		  }
    	  	  else if(unrated == movies.getKey()){
    	  		  System.out.print(movies.getValue() + " :: " + compare + " | ");
    	  	  }
      		}
      	 compare = 0.0; 
    	  }
    	unRatedList.clear();
    	System.out.println();
       }
	}
}
