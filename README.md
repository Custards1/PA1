*******************************************************
*  Group     : 10
*  Members   : Blake Brown, Elie Schooley
*  Class     : CSCI 3920
*  HW#       : PA1
*  Due Date  : November 1, 2020
*******************************************************

#  Description of the program
Server, Admin client, and User client that run and manage
a software system for a department store

#  Source files


## File:  adminclient
   Name:  Adminclient.java
        Description:  Contains the functionality for the
        admin client user interface.

   Name:  AdminClientMain.java
        Description:  Main call to the admin client.

   Name:  adminClient.fxml
        Description:  Contains the admin client user interface.

## File:  Catalogclient
    Name:  CatalogClientController.java
        Description:  Contains the functionality for the
        catalog client user interface.

   Name:  CatalogClientMain.java
        Description:  Main call to the catalog client.

   Name:  LoginController.java
        Description:  Contains the functionality of the
        login interface.

   Name:  login.fxml
        Description:  Contains the login interface.

   Name:  UserClient.fxml
        Description:  contains the catalog client user
        interface.

## File:  edu.ucdenver.domain.category
   Name: Catagory.java
        Description: Contains a class representing a catagory for the store
## File:  edu.ucdenver.domain.client
   Name:  Client.java
        Description: Contains the client class used to retrieve all information from the
        server
   Name:  ClientError.java
        Description: Provides custom errors used to distinguish protocol errors
   Name:  ClientErrorType.java
        Description: An enumeration definining all error types
   Name:  ClientTest.java
        Description: This is soley a file to test the server, nothing more
## File:  edu.ucdenver.domain.order
   Name:  Order.java
        Description: This file represents a users order from the store
## File:  edu.ucdenver.domain.parser
   Name:  RequestObjectParser.java
        Description: This object helps parse objects from and to a requestable format
## File:  edu.ucdenver.domain.products
   Name:  Book.java
        Description: Defines the book product used in the store
   Name:  Computer.java
        Description: Defines the computer product used in the store
   Name:  Electronic.java
        Description: Defines the electronic product used in the store
   Name:  Home.java
        Description: Defines the home product used in the store
   Name:  Phone.java
        Description: Defines the phone product used in the store
   Name:  Product.java
        Description: Defines any product, base class of all products
## File:  edu.ucdenver.domain.request
   Name:  Request.java
        Description: This class represents a request, used to encode messages between
                     A server and a client
   Name:  Requestable.java
        Description: This file defines the interface used to turn an object to and from a
                     request friendly format.
   Name:  RequestServerProtocol.java
        Description: This file defines the protocol to be used by any class implenting a server protocol
   Name:  RequestClientProtocol.java
        Description: This file defines the protocol to be used by any class implenting a request protocol
## File:  edu.ucdenver.domain.store
   Name:  UserStore.java
        Description: This file defines a collection of users and thier orders for the store,
                     enforces admin authentication for admin required tasks.
   Name:  ItemStore.java
        Description: This class extends the UserStore, adds the ability to store products and catagories
                                          to the store. Enforces admin authentication for admin required tasks.
## File:  edu.ucdenver.domain.user
   Name:  User.java
        Description: This class reprents a user account for the store.
## File:  edu.ucdenver.server
   Name:  Main.java
        Description: This is the main server file, asks for option to load a file or not and
                     starts the server
   Name:  Server.java
        Description: This class implents the server process, awwaits a conncetion and starts a ServerTask
   Name:  ServerTask.java
        Description: This class is respondible for handling a client, replying with data from store,
                     or an error if something went wrong

  
*******************************************************
*  Circumstances of programs
*******************************************************
 It is finished with some minor bugs



*******************************************************
*  Running The Program
*******************************************************

Run edu.ucdenver.server.Main to start the server

Run adminclient.AdminClientMain to start the administrator application

Run CatalogClient.CatalogClientMain to start the catalog application

