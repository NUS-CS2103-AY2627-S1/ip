---
title: "CS2103/T - Admin: Apdx B: Course Policies - Giving credit for reused work - Giving credit for reused work"
source: "https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixB-policies.html#policy-reuse"
author:
published:
created: 2026-09-18
description:
tags:
  - "clippings"
---

> #### Giving credit for reused work
> 
> Below is how to give credit for things you reuse from elsewhere. These requirements are specific to this course i.e., not applicable outside the course (outside the course, you should follow the rules specified by your employer and the license of the reused work).
> 
> If you **used a third party library**:
> 
> - Individual project (iP): Mention in the `README` file (under the *Acknowledgements* section)
> - Team project (tP):
> 	- Mention in the Developer Guide (under the *Acknowledgements* section)
> 		- Mention in Project Portfolio Page if the library has a significant relevance to the features you implemented.
> 
> If you **reused code snippets found on the Internet** e.g., from StackOverflow answers or  
> **referred to code in another software** or  
> **referred to project code by a current/past student**:
> 
> - If you read the code to understand the approach and implemented it yourself, mention it as a comment  
> 	Example:
> 	```java
> 	//Solution below inspired by https://stackoverflow.com/a/16252290
> 	{Your implementation of the reused solution here ...}
> 	```
> - If you copy-pasted code from elsewhere but modified it significantly, mention it as a comment  
> 	Example:
> 	```java
> 	//Solution below adapted from https://stackoverflow.com/a/16252290
> 	{Your implementation of the reused solution here ...}
> 	```
> - If you copy-pasted a non-trivial code block (possibly with minor modifications e.g., renaming, layout changes, changes to comments, etc.), also mark the code block as reused code (using `@@author` tags with the ==`-reused`== suffix)  
> 	Format:
> 	```java
> 	//@@author {yourGithubUsername}-reused
> 	//{Info about the source...}
> 	{Reused code (possibly with minor modifications) here ...}
> 	//@@author
> 	```
> 	Example of reusing a code snippet (with minor modifications):
> 	```java
> 	persons = getList()
> 	//@@author johndoe-reused
> 	//Reused from https://stackoverflow.com/a/34646172
> 	// with minor modifications
> 	Collections.sort(persons, new Comparator<CustomData>() {
> 	    @Override
> 	    public int compare(CustomData lhs, CustomData rhs) {
> 	        return lhs.customInt > rhs.customInt ? -1 : 0;
> 	    }
> 	});
> 	//@@author
> 	return persons;
> 	```
> 
> **Giving credit for reused images** (and other media assets): Ideally, the source should be credited where the asset appears. For example, if you reused an image in your GUI, you can credit the source where a screenshot of the GUI showing that image appears first in your user guide. In addition, you can also acknowledge the sources in your GitHub project's landing page (e.g., `README.md`).
> 
> **Giving credit to AB3 code**: If your team project code follows a design similar to AB3, that doesn't mean you need to credit AB3 -- this is because a brownfield project is *expected* to follow existing code/design where possible, in the interest of consistency. This type of reuse need not be acknowledged/credited specifically.
> 
> **Giving credit to AB4 code**: If you reused any code from [AB4](https://github.com/se-edu/addressbook-level4/), cite it as you would cite reuse from any other external source.
> 
> **Giving credit for reusing from course materials** (e.g., course textbook, tutorials, instructional resources from se-education.org) is not required, although you are welcome to do so. Reason: Those materials were created by the teaching team for you to use/reuse.
> 
> **Reuse within the team** (e.g., reusing code written by a team member) need not be mentioned explicitly. However, you should factor in such reuse when you estimate effort contributed by each team member.
> 
> **Reuse of documentation** (e.g., reusing a UG/DG section) is no different from code reuse. Such reuse should be credited as well.
> 
> **Citing the use of AI-generated/assisted work** (e.g., using GitHub Copilot for project work):
> 
> - If the use of the tool was localized to a few places (e.g., used it to write a few methods/classes only), cite its use in comments near where you used it.
> - If the use was more widespread (e.g., used it as an auto-complete tool during most of your coding), cite the usage (i.e., which tool, who used it, the extent of use) in the following location instead (i.e., no need to cite in code comments):
> 	- iP: in the README file
> 		- tP: in the DG, under the Acknowledgements section
