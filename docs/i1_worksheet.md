Iteration 1 Worksheet
=====================

Adding a feature
-----------------

Feature: Fake Database

The app we are making is going to be used for tracking when you last did something and the next time you should it again. 
In the app users can create, store and delete any event they would like. For them to be able to do that, we need to 
build a database that stores and keeps track of all the users and their events. Before building the actual database, we 
decided that it's important to implement the Fake Database feature so we can better understand the data flow of the app 
and the type of objects we are going to need to store in the actual database.

Connected Features:
    https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/28

Connected user stories:
    https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/27

Merge commit that was used complete the feature:
    https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/merge_requests/12

Associated tests:
    [link](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/test/java/comp3350/timeSince/tests/persistence/FakeDBUnitTests.java)
    
    
Exceptional code
----------------

In the userLogin() method an exception is thrown in the else{...}. That happens when a user supplies the 
wrong username or password (either one or both of them don't exist in the database) during the login process.
The whole method is also surrounded with a try-catch in case something goes unexpectedly wrong. 
An exception 
is thrown with the error name and it's description in the case that something does go wrong.

https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/userLogin_logic/app/src/main/java/comp3350/timeSince/presentation/LoginActivity.java




Branching
----------

Here is a description of the branching strategy [[link]](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/docs/Workflow.md)

Here is a screenshot of the Fake Database feature being merged into our main branch (highlighted in red) [[link]](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/iter1-worksheet/docs/featureMerge.png)


SOLID
-----

In their DataBaseHelper, it is better to separate the database creation and modification into independent class, to increase cohesion.\
[[link to DataBaseHelper]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/blob/Development/app/src/main/java/com/group3/movieguide/Persistence/DataBaseHelper.java)
Commit SHA was `8c7d619c411db17b4951362cd4d1207bff421cbe`
[[link to commit]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/commit/8c7d619c411db17b4951362cd4d1207bff421cbe)
[[link to issue]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/issues/27)

In their `MovieModel.java` file, they have a toString() method that violates SOLID.
In particular, it violates the Open/Close section.  They mention in a comment that
if they want to, they should add a for loop to iterate over all of the genres to be
printed.  Currently it's just looking at index 0.  This will have to be refactored
to include a for loop.  It would be better if it started with a for loop and passed
a count for the number of genres they want to print.
[[link to MovieModel.java]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/blob/main/app/src/main/java/com/group3/movieguide/Object/MovieModel.java)
Commit SHA was `8c7d619c411db17b4951362cd4d1207bff421cbe`
[[link to commit]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/commit/8c7d619c411db17b4951362cd4d1207bff421cbe)
[[link to issue]](https://code.cs.umanitoba.ca/winter-2022-a02/group-3/movie-recommendation-app/-/issues/28)

Agile Planning
--------------

We pushed two features from iteration 1 to iteration 2, which are CreateAccount and AddEvent.\
We added a new feature "Fake Database".\
We changed the description for the "Create User Account" user story.\
\
links for pushed features and user stories:\
Add Event: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/1 \
Add Event From Presets: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/2 \
Create Own Events: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/14 \
View All Events: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/15 \
View a Single Event: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/16 \
Mark Event as Done: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/18 \
Login to User Account: https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/22
