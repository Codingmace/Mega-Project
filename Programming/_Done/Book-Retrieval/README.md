# Book Retrieval
 Retrieve a document of a found book online (Potentially for free if it can)

# Steps
1. Clean up the database 
	a. Folder 2, get rid of a lot of fields. Do not need the 1,2,3,4,5 distributions)
	b. Combine the files in folder 2
	c. It seemed that the PublishMonth and PublishDay are mixed up.

2. Cleaned up the first folder
	a. Removed the reviews because for the third book it had 0 reviews but rating of 3.33 so I don't think it was accurate.
	b. Rounded the price to the third decimal place 
	c. Renamed to programming_Books.csv
3. Finished up
	a. [GoodBooks] The cleaned up dataset for books
	b. [programming_Books] 270 Top Programming Books
	c. [clean.py] The code to clean up the original Folder 2 code

# Future Steps
1. Enrich the data looking online
	a. Make sure the ISBN is not missing
2. Find websites of the book
	a. Could do legal but It would be more benifical to find that already can download
	b. Verify the file is correct
	c. Verify right author
	d. Verify ISBN
	e. Verify Corrent Number of pages or close.

# Sources
 - https://www.kaggle.com/thomaskonstantin/top-270-rated-computer-science-programing-books
 - https://www.kaggle.com/bahramjannesarr/goodreads-book-datasets-10m