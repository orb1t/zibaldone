zibaldone
=========

Idea visualisation tool for creative people.

Zibaldone is a great way to:

  * Explore vast collections of your existing digital notes.
  * Automatically discover new connections (even if you've never seen the link).
  * Export collections of notes in a format that allows you to start writing!

  Innovation is about pulling together lots of small ideas in a way they've never been combined before.


[![Zibaldone Screenshot](http://i47.tinypic.com/2jch302.jpg)](http://www.youtube.com/watch?v=Hx7cokXB9hM)

*(click image for a video of an old version of the application)*


Installing
==========

Zibaldone is in *alpha* status and users will likely have to be technically savvy enough to compile and run the application. Ideally we'd like to see patches accompanying bug reports in the issue tracker. Once you've obtained a copy of this repo:

```
mvn compile
mvn exec:java
```

*(or use an IDE to launch)*

**If you don't understand these instructions, it is highly likely that you are *not* a suitable *alpha* tester – please come back when the application is in *beta*.**

For Developers
==============

Zibaldone is a Java Swing application with a JPA connection to a local JavaDB database. The MVC model is strictly adhered to, meaning that the user interface can be swapped, with no changes to the backend, for a web application. Anybody interested in doing this is welcome to contribute.

`Importer` and `Exporter` interfaces exist for developers to really easily implement new file formats. It's all loaded dynamically, so you just need to write your implementation and add it to the relevant `META-INF/services` text file. Likewise for `Relator`s, which determine where notes are to be placed on the screen.

History
=======

Zibaldone was originally written as a writer's assistant, for creating novels and series. It is also useful for anybody who wants to be creative with the written word. 

We call the project [http://en.wikipedia.org/wiki/Commonplace_book#Zibaldone Zibaldone] in honour of the first notebooks, used by the 14th century innovators of Italy.

Donations
=========

Please consider supporting the maintenance of this open source project with a donation:

[![Donate via Paypal](https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=B2HW5ATB8C3QW&lc=GB&item_name=zibaldone&currency_code=GBP&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)


Licence
=======

Copyright (C) 2012 Samuel Halliday

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see http://www.gnu.org/licenses/


Contributing
============

Contributors are encouraged to fork this repository and issue pull
requests. Contributors implicitly agree to assign an unrestricted licence
to Sam Halliday, but retain the copyright of their code (this means
we both have the freedom to update the licence for those contributions).
