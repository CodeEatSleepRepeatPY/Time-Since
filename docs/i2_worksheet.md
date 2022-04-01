Iteration 2 Worksheet
=====================

Paying off technical debt
-----------------

### Date vs Calendar

[Issue](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/39)

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/c91203ee276ea34823b1e5ed2133cdd9e3796a4f)

In Iteration 1, we were using Date objects to store a date, but when we looked
up the documentation of Date, we found that a lot of it was deprecated, and had
been replaced by the Calendar class.
We found this while trying to update the UI for selecting a date.
As such, before adding more code for working with dates, we paid off the
technical debt we previously incurred by switching all Date objects to Calendar
objects.  This ensures we're using something that's less likely to be changed,
or not work, and also it made the UI calls to selecting a date easier.

The type of this technical debt is Prudent and Inadvertent.  We knew we
didn't want to store the date as a String, so we chose the Date format.
What we didn't realize when we did this is that Date has been deprecated,
and now the class people use is Calendar.

### Splitting the FakeDatabase into three different classes

[Issue](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/28)

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/97dd99872ba524644ae6070a28c57062dbf11ba3)

In Iteration 1, we only had one FakeDatabase class that stored the users, events,
and event labels. We realized that this violated ISP (Interface Segregation Principle), 
so we had to pay off the technical debt by splitting up the one FakeDatabase class and moving/adding
a lot of methods into the three new classes that we created; UserPersistence, EventPersistence, 
and EventLabelPersistence.

This technical debt was Prudent and Inadvertent.  At first we thought that
it would be a good idea to store the users, events, and event labels all
in the same class.  We realized that this was not a good idea, because it's
a SOLID violation, and we split them up into 3 different tables.

SOLID
-----

Single Responsibility Principle

[link to issue](https://code.cs.umanitoba.ca/winter-2022-a02/group-1/cool-sentence-game/-/issues/43)

Retrospective
-------------

Actions we've taken since the last retrospective are:

- We had more code reviews.
- There was more discussion surrounding the implementation of classes.
- We starting merging everything together earlier than the last iteration.
- We committed to fewer features, user stories, and dev tasks this iteration,
  as we saw that we ran out of time last iteration, and had to push things
  to this iteration.
- We gave ourselves more time in the timelines than we did in the previous
  iteration in order to account for unforeseen circumstances / situations. 
- We switched up who was doing what.  For example:
  - Freyja was previously working on UI, and in this iteration worked on the DB.
  - Steven was previously working on DSOs, and in this iteration worked on UI.
  
Things that didn't change:

- We're still using the same workflow for commit changes to GitLab.
- We're still doing the same peer review workflow where we notify everyone of
  a merge request via a message on discord, and people review and add comments
  to the merge request on GitLab.  Those comments are then addressed, and
  either changed or discussed (potentially verbally though discord meeting rooms).
  Once all comments are addressed and everyone's signed off on it, we merge it
  into the main branch.
- How we're writing the tests didn't change (but this iteration now added
  integration tests).

Design patterns
---------------

### Chain of Responsibility

- We use chain of responsibility when logging into the app.
  - The database is prompted to check to see if this username exists.
  - If it does exist, then the password is hashed and checked against the
    stored password hash for this user.
  - If the password hashes match, then the user is logged in.

Example in our code:
[Login Page](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/LoginActivity.java)

### Observer

- The UI layer uses observer a lot.  All of the buttons have listeners on
  them, waiting for them to be clicked.  Once they're clicked, then other
  actions occur.  This is the observer pattern, watching and waiting for
  an action to occur.

Example in our project:
[Home Screen](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/HomeActivity.java)

Iteration 1 Feedback fixes
--------------------------

The grader did not open any issues relating to feedback, so instead we created
our own to address said feedback mentioned in UMLearn's Assignments page.

### Main

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/45517253a1c20eb5c44efacd227c2931cd51436e)

The code was refactored to do the following:

- Added a couple of methods to Main.java

### UserManager

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/45517253a1c20eb5c44efacd227c2931cd51436e)

[Commit - account validation](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/773ad43d9e575f309d71e4d0668ad110c927bb8a)

The code was refactored to do the following:

- Let database handle the data rather than handling all the entities from database and going through all of them in UserManger. So in uniqueName method and accountCheck method there is no more for loop to search.
- Account validation is done in UserDSO, which is moved from UserManager. And the password length check is not  separate from the passwordRequirements(). Both length check and capital letter check are in meetsNewPasswordReq() in UserDSO.

### EventLabelDSO

[Commit - equals method](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/97dd99872ba524644ae6070a28c57062dbf11ba3)

[Commit - color](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/4165b43d551306c9635ef4a8806393f7532a1db8)

The code was refactored to do the following:

- added a equals() method
- removed all references to a color (removed the enum).  Decided it didn't
  make sense to specify a color with a given label, as you could have multiple
  labels on a given event, so how would you decide which color should dominate?

### UserDSO

[UserDSO Feedback Issue](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/38)

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/merge_requests/23/diffs?commit_id=c77d8d7c60086cd8b80c0be3e4d9847865f9899d)

The code was refactored to do the following:

- remove password checking from the UserManager class, and instead put it in
  the UserDSO class.
  - this includes both checking if the entered new password meets the
    requirements for a new password, and also a method to check if the password
    hash entered matches the existing password hash.
- removed the setPasswordHash in the UserDSO, and replaced it with
  setNewPassword, which requires you to enter the existing password hash before
  allowing you to change the stored password hash.
- added an equals() method
- removed the membership type
- added tests relating to the newly added methods, and removed test relating
  to methods we no longer have.

### EventDSO

[Commit](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/commit/45517253a1c20eb5c44efacd227c2931cd51436e)

The code was refactored to do the following:

- added a equals() method

### I_Database

[Commit](97dd99872ba524644ae6070a28c57062dbf11ba3)

The code was refactored to do the following:

- I_Database was separated into three different interfaces (IEventPersistence, IEventLabelPersistence, IUserPersistence)

### FakeDatabase

[Commit](97dd99872ba524644ae6070a28c57062dbf11ba3)

The code was refactored in the following way:

- This class was removed and replaced with three new different classes (UserPersistence, EventPersistence, and EventLabelPersistence)
- The getUserIndex() method was removed; we are making use of the ArrayList.indexOf() method instead.
