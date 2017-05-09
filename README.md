# RecommendationSystem
Recommendation system based on cosine (pair-wise) similarity.

Item-based recommendation system based on past ratings of items. In this iteration, the items are movies. The input to the recommendation system is a dataset consisting of 100,000 ratings from roughly 1,000 users on about 1,700 movies. 

The two input files are:
 
 1. movies.dat: Each line in the movies.dat represents a unique movie and has the following format:
      movie id | movie title | release date | video release date |etc. 
 
 2. ratings.dat: All ratings are contained in the file ratings.dat. Each line of ratings.dat has the following format (the fields are separated by tab): 
      UserID MovieID Rating Timestamp
For example, the following record means user id 1 gave rating 5 to the item id 1193 (the last entry is the time stamp which is not used in this project) 1 1193 5 978300760

This item based recommender system predicts a rating that userId (u) will give to itemId (i) based on the ratings that the user has previously given to items that are similar to i.  More specifically, to predict rating(u,i), an item-based recommender iterates through all items  that have been previously rated by user u and takes the weighted average of the ratings that the user gave to such items .  The rating of item j is weighted by the similarity of item j to item i. That means the more similar item j is to the target item i, the more its rating weighs in predicting the rating of item i. 

