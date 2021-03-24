# MQTT-XES_Bachelor_project

MQTT-XES_Bachelor_project is a lightweight library for creating an intermediate event log form Rejseplanen REST APi and publish on a real-time base to HiveMQ broker. 
This program can be used for online process mining purposes.
The structure of the program is based on mqtt-xes project. [mqtt-xes](https://github.com/beamline/mqtt-xes)
## Using the program

In order to allow users to fully benefit from the project, some user specification can be set as arguments for the program. It is important to mention that each specification has a default value in order to allow each individual part, as well as the combined program, to run without any input from the user. 



### Collecting the data

five variables can be chosen when running this part: the number of calls for each endpoint, the maximum number of endpoints in each call, the number of calls to get new endpoints, The time between each call, and the time series file name.

    0.Number of endpoints: 
     this variable allows the user to limit the amount of routes that are
     pulled each time a call is generated for a new journey. 
    
    1. Number of calls for each end point: this is the number of calls that are done
     to each route. In order to get the full journey, this value needs to equal the 
     amount of stops, although an an analyst may not know how many stops there are in 
     a specific journey. In the case this is necessary all the calls are being 
     saved to files, and the relevant information can be found there.  
    
    2. Number of calls to get new endpoints: 
     The number of iterations in the program can be adjusted. Each iteration starts 
     when the last one finishes all its calls and a new journey request is created 
     to Rejseplanen.
    
    3. Time between calls: 
     a waiting time can be set in between the individual calls to the endpoints. 
     This is set to ensure the events later on are not streamed at the exact same time,
     and to allows for some flexibility in the time between calls.   
    
    4. File name: 
     This will be the name of the final time series file name. When running all the
     programs together, it will be the same file name used in the next part.
     
###  Creating process aware JSON
In order to create the JSON file, two variables are needed:
    
    1. Time series file name: 
    is the name of the time series file that is being processed. In case all programs 
    are running combined, it will be the same file name that has been used in the 
    Receiving process information.
    
    2. JSON process aware file name: 
    is the name by which the program will save the file. In case all the programs are 
    running combined, it will be the same file name that is used to create the XES file.
    
###  Constructing the XES file
Again, two file name attributes are needed for this part of the program:

    1. JSON process aware file name: 
    is the name of the JSON process aware file that is being processed. In case 
    all programs are running combined, it will be the same filr name as been created. 
    
    2. XES.gz file name: 
    is the name by which the program will save file. In case all the programs are 
    running combined, it will be the same file name used to publish then events. 

### Publishing the data
 This part of the program uses two variables:
 
    1. XES.gz file name: 
    is the name of the log that will be published, which is saved as a compressed 
    XES file. The file name should include only the name of the file without a file 
    extension. In case all programs are running combined, it will be the same file 
    name as been created.
    
    2. Preferred log time: 
    The users can specify the desired time for the streaming of the event log. 
    The user can input the desired time in minutes. if the time is shortner the dividing
    varibale will be printed to the consule. 
