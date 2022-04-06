# NewBank
New bank code base for software engineering 2 assignment

#code execution
1. Ensure a project directory structure with /out and /src on the same directory level. Within /src there should be a newbank directory which contains in turn the data, server, client and testing directories

#code dependencies
The application uses JSON files as simple databases. In order to read/write to these JSON files the JSON-Simple library is used, which is therefore an 
external dependency for the project. To install this library and allow the code to execute first take a copy of json-simple-1.1.jar from this git repository. Next you need to register dependency on this .jar file with your intelliJ project. To do this follow these steps:
1.Start from the "project window";
2.Use menu File | Project Structure;
3.In Project Settings, select Modules,
4. On the window to the right, select "Dependencies” tab
5.press "+” under ‘module SDK’
6. select "Jars or directories" and navigate to the jar file, select it and press ok
7. Press apply in bottom right hand corner

Additionally, the JUnit testing framework for the code is dependent on junit-4.10.jar. This can also be obtained from this git repository also, and dependency on this library can be registered in exactly the same manner as above.

The code will now compile and execute
