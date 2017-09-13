import pandas
from bs4 import BeautifulSoup
import re
import nltk
nltk.download()
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.ensemble import RandomForestClassifier

#Though I put this together and made more than a few changes, most of this is pulled from someone's Bag of Words tutorial from kaggle.com 
#Data must be in tsv format with collumn labels "sentiment", "review", and "id"
#Rows in training data must equal rows in testing data
#Output is the predicted test data "sentiment" value for each "review" of the test data
#Prediction algoritm uses RandomForest with depth 150, operating on a Bag of Words generated from a cleaned version of the input data

def get_raw(filename):
	data = pandas.read_csv(filename, header=0, \
	delimiter="\t", quoting=3)
	return data

def clean_data(filename):
	train = get_raw(filename)	
	clean_train_reviews = []
	for i in range(0, train["review"].size):
		clean_train_reviews.append(review_to_words(train["review"][i]))
	return clean_train_reviews

def review_to_words(raw_review):		
	review_text = BeautifulSoup(raw_review).get_text()
	letters_only = re.sub("[^a-zA-Z0-9]", " ", review_text)
	letters_only = re.sub("[\\d]+", " NUM ", letters_only)
	words = letters_only.lower().split()                             
	stops = set(stopwords.words("english"))                  
	meaningful_words = [w for w in words if not w in stops]   
	return (" ".join(meaningful_words))

def trainNtest(cleaned_train, cleaned_test, trainfilename, testfilename, outputname):
	vectorizer = CountVectorizer(analyzer = "word", \
	tokenizer = None, \
	preprocessor = None, \
	stop_words = None, \
	max_features = 10000)
	train_data = vectorizer.fit_transform(cleaned_train)
	forest = RandomForestClassifier(n_estimators = 150)
	train_data = train_data.toarray()
	uncouth = get_raw(trainfilename)
	forest = forest.fit(train_data, uncouth["sentiment"])
	test_data = vectorizer.transform(cleaned_test)
	test_data = test_data.toarray()
	result = forest.predict(test_data)
	lout = get_raw(testfilename)
	output = pandas.DataFrame(data={"id":lout["id"], "sentiment":result})
	output.to_csv(outputname, index=False, quoting=3)	