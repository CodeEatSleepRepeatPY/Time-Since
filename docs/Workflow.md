# Workflow 

## Branching Strategy 

We will utilize a combination of Git Flow, and GitLab Flow. There will be only
one main branch (main). All feature branches will stem off of the main branch.

Feature branches should have the following syntax: `task-name-or-description`.

## Commit Messages

> "A commit message should reflect your intention, not just the contents of the
commit. You can see the changes in a commit, so the commit message should explain
> why you made those changes". 
> 
> — Introduction to GitLab Flow

[This](https://cbea.ms/git-commit/) article on how to write a git commit 
message should guide us: 

1. Capitalize the subject line
2. Limit the subject line to 50 characters
3. Do not end the subject line with a period
4. Use the imperative mood in the subject line 
   - A properly formed Git commit 
   subject line should always be able to complete the following sentence:
   "If applied, this commit will <ins>*your subject line here*</ins>"
5. Separate subject from body with a blank line
6. Wrap the body at 72 characters
7. Use the body to explain *what* and *why* NOT *how*

## Merge Requests 

Taken and modified from [cook-eBook](https://code.cs.umanitoba.ca/comp3350-summer2019/cook-eBook/-/blob/master/docs/Contributing.md):

All merge requests should have an appropriate title, consisting of a brief 
description of what was changed/added. In the description should be a more 
detailed version, including any and all important/relevant technical details.

You should link user story and what developer task it is associated with.

If you want to discuss your code, get feedback, or get suggestions, open a 
merge request and start the tile with `Draft:`. Don't assign anyone to review
the code, but mention people in the description or comment of the request, 
such as: "/cc @Freyja @speters-gitlab". Team members can then comment on 
the merge request in general or on specific lines with line comments.

As an option in your merge request you should check off squash commits and 
delete source branch.

In terms of approvals, this will depend on the scope of the request. If you're 
just adding a few lines of documentation, you don't need any approvals. If 
you're adding any functional code, you need at least 1 approval, from someone 
other than yourself. If you're adding a significant amount of code, then you'll 
need 2 approvals. Significant additions SHOULD and WILL require unit tests to 
ensure that the changes are working.

Additionally, before your merge you should merge develop into your task branch 
and do a final sanity check to make sure everything works as expected. Then, 
assuming you have your approvals, you can merge.

## Unit Tests

Try to practice Test-Driven Development. Write unit tests before writing the 
code. Most code will need to be unit tested, but use common sense to determine
where to spend your time, and what doesn't actually need a test. The tests 
should be run (and pass) before merging. 
