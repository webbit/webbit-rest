## Thoughts on views

Webbit was primarily built as a WebSocket server. But it also knows how to serve plain HTTP, including static files,
embedded in a jar or on the file system.

Generating dynamic content through a template engine is currently a little tricky. Imagine we have templats (in mustache,
stringtemplate or something else) on the file system (or in a jar) and want to serve this. It should be:

* An API to plug in a new template engine
* Easy to switch between FS and Jar
* Ability to cache templates
* Build a context from: URI parameters (uri.xxx), custom parameters added by handler.
