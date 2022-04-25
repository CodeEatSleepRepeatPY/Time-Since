What technical debt has been cleaned up
========================================

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

What technical debt did you leave?
==================================

What one item would you like to fix, and can't? Anything you write will not
be marked negatively. Classify this debt.

A potential technical debt is that we currently only allow for you to register with your email. That
might limit the number of potential users. It's that in the future, we may need to allow for
alternate registration methods.  Example: using a phone number.

Discuss a Feature or User Story that was cut/re-prioritized
============================================

When did you change the priority of a Feature or User Story? Why was it
re-prioritized? Provide a link to the Feature or User Story. This can be from any
iteration.

We changed the priority of [Add Event From Presets](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/2)
because this features seems not as important as we planned before. And we lower
the priority from high to low.

Acceptance test/end-to-end
==========================
Write a discussion about an end-to-end test that you wrote. What did you test,
how did you set up the test so it was not flaky? Provide a link to that test.

For one of our tests we had to test whether event1, event2 and event3 are visible
on the screen, however the database wouldn't necessarily contain these events in
the beginning. Events are always changing and have a chance of getting deleted.
If at least one of the events out of event1 or event2 or event3 gets deleted then
the test will break. So what we did to make the test not flaky is just always remove
3 events (or less if there are less events in the database) and instead add the 3 new
events which are "event1", "event2" and "event3" so this way, they're always guranteed to
appear in the database/view-all-events list. [Link To Test](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/androidTest/java/comp3350/timeSince/ViewAllEventsSystemTest.java) 
[Link To How We Counter The Flaky Test](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/blob/main/app/src/androidTest/java/comp3350/timeSince/utils/TestUtils.java)

Acceptance test, untestable
===============
What challenges did you face when creating acceptance tests? What was difficult
or impossible to test?

We ran into a lot of issues with the press back button. Espresso didn't really 
work to well with the onSupportNavigateUp() method and it was really difficult
to debug what is going wrong / what the problem is. I just ended up using a different
function because it was pretty much impossible to understand why onSupportNavigateUp()
didn't work.

Velocity/teamwork
=================

Did your estimates get better or worse through the course? Show some
evidence of the estimates/actual times from tasks.

For Iteration 3, our velocity sped up even though we had more tasks done than previous
iterations and less time for doing it. The estimation of dev tasks and actually spent
are more accurate than previous iterations:

In [Create sort events logic and test](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/79)
The estimated time is 2 hours and the actual spent time is 2.5 h.

In [Create the Logic for coming events alert](https://code.cs.umanitoba.ca/winter-2022-a02/group-2/time-since-a02-2/-/issues/81)
The estimated time is 2 hours and the actual spent time is also 2 h.
