# Introduction #

Here is how I have set up guineu project in my Netbeans. I describe the steps very briefly assuming that you either already have experience with Netbeans or at least literate enough with computer programs that you can use any GUI program. For example, I don't tell you the obvious things such as 'press Next button'. If there are some options which I do not mention below, it means that I chose the default options suggested by Netbeans.

# Check out the latest sources from SVN #

I used Netbeans for this task. Following parameters were used:

Repository URL = http://guineu.googlecode.com/svn<br>
User name = My google accout user name.<br>
Password = Password allocated to me by google code!<br>
<br>
Repository folder = trunk<br>
<br>
I unchecked the option "scan for netbeans projects after checkout".<br>
<br>
The local folder to which I checked out the "trunk" is "D:\workspace\guineu\trunk"<br>
<br>
<h1>Create Project</h1>

New Project > Java > Java Project With Existing Sources.<br>
<br>
Project Name = Guineu<br>
Project folder = D:\workspace\NetBeansProjects\Guineu<br>
Build script = build.xml<br>
<br>
Source package folder = D:\workspace\guineu\trunk\src<br>
<br>
<h1>Configure the Project</h1>

To complete the project setup, you need to configure it finally as explained here.<br>
<br>
Right click the Guineu project (in Netbeans project tree), select properties and do the following.<br>
<br>
Libraries > Compile > Add JAR/folder > D:\workspace\guineu\trunk\lib\<code>*</code>.jar (i.e. select all jar files and click open).<br>
<br>
Run > Main class = guineu.main.GuineuClient<br>
Run > Working directory = D:\workspace\guineu\trunk<br>
Run > VM Options = -Djava.util.logging.config.file=conf/logging.properties -Xms256m -Xmx512m<br>
<br>The part "-Xms256m -Xmx512m" is optional, but the rest is mandatory.<br>
<br>
Done!