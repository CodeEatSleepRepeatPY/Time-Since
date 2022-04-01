# Architecture 

Directory of classes and their layer locations. 

## Entire Project Architecture

![architecture](Architecture.png)

## Iteration 1

![architectureIteration1](Architecture_Iteration1.png)

### Presentation Layer

[HomeActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/HomeActivity.java)
- The starting screen for the app

[RegisterActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/RegisterActivity.java)
- The view for the user to register an account 

[LoginActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/LoginActivity.java)
- The view for the user to log into their account 

### Business Layer

[UserAccountManager](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/business/UserManager.java)
- The presentation layer will call this to handle the user accounts

### Persistence Layer

[AppPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/I_Database.java)
- Interface for the events and user accounts in the database

### Fake Database 

[FakeAppPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/FakeDatabase.java)
- Current database implementation for events and user accounts

### Domain Specific Objects 

[Event](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/EventDSO.java)
- The event object

[EventLabel](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/EventLabelDSO.java)
- Object for the tags / labels of an event 

[UserAccount](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/UserDSO.java)
- The user / user account object

## Iteration 2

![architectureIteration2](Architecture_Iteration2.png)

### Presentation Layer

[HomeActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/HomeActivity.java)
- The starting screen for the app

[RegisterActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/RegisterActivity.java)
- The view for the user to register an account 

[LoginActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/LoginActivity.java)
- The view for the user to log into their account 

[CreateOwnEventActivity](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/presentation/CreateOwnEventActivity.java)
- The view for creating own event for a specific user

### Business Layer

[UserAccountManager](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/business/UserManager.java)
- The presentation layer will call this to handle the user accounts

### Persistence Layer

[Service](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/application/Services.java)
- Service for the database accessing

[IEventPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/I_Database.java)
- Interface for the event persistance

[EventHsqlDB](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/hsqldb/EventPersistenceHSQLDB.java)
- HsqlDB for the Events

[IEventLabelPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/IEventLabelPersistence.java)
- Interface for the eventLabel persistance

[EventLabelHsqlDB](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/hsqldb/EventLabelPersistenceHSQLDB.java)
- HsqlDB for the EventLabels

[UserPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/IUserPersistence.java)
- Interface for the User persistance

[UserHsqlDB](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/hsqldb/UserPersistenceHSQLDB.java)
- HsqlDB for the User


### Fake Database 

[FakeUserPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/fakes/UserPersistence.java)
- fake database implementation for user accounts

[FakeEventPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/fakes/EventPersistence.java)
- fake database implementation for events 

[FakeEventLabelPersistence](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/persistence/fakes/EventLabelPersistence.java)
- fake database implementation for event labels

### Domain Specific Objects 

[Event](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/EventDSO.java)
- The event object

[EventLabel](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/EventLabelDSO.java)
- Object for the tags / labels of an event 

[UserAccount](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/main/java/comp3350/timeSince/objects/UserDSO.java)
- The user / user account object

