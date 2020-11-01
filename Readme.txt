*******************************************************
*  Group     : 10
*  Members   : Blake Brown, Derek Gunnels, Elie Schooley
*  Class     : CSCI 3920
*  HW#       : PA1
*  Due Date  : November 1, 2020
*******************************************************


                 Read Me


*******************************************************
*  Description of the program
*******************************************************

Server, Admin client, and User client that run and manage
a software system for a department store


*******************************************************
*  Source files
*******************************************************

File:  adminclient
    Name:  Adminclient.java
        Description:  Contains the functionality for the
        admin client user interface.

    Name:  AdminClientMain.java
        Description:  Main call to the admin client.

    Name:  adminClient.fxml
        Description:  Contains the admin client user interface.

File:  Catalogclient
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

File:  edu.ucdenver.domain.category
    Name: Catagory.java
        Description:
File:  edu.ucdenver.domain.client
    Name:  Client.java
        Description:
    Name:  ClientError.java
        Description:
    Name:  ClientErrorType.java
        Description:
    Name:  ClientTest.java
        Description:
File:  edu.ucdenver.domain.order
    Name:  Order.java
        Description:
File:  edu.ucdenver.domain.parser
    Name:  RequestObjectParser.java
        Description:
File:  edu.ucdenver.domain.products
    Name:  Book.java
        Description:
    Name:  Computer.java
        Description:
    Name:  Electronic.java
        Description:
    Name:  Home.java
        Description:
    Name:  Phone.java
        Description:
    Name:  Product.java
        Description:
File:  edu.ucdenver.domain.request
    Name:  Request.java
        Description:
    Name:  Request.java
        Description:
    Name:  Requestable.java
        Description:
    Name:  RequestServerProtocol.java
        Description:
    Name:  RequestClientProtocol.java
        Description:
File:  edu.ucdenver.domain.store
    Name:  ItemStore.java
        Description:
    Name:  UserStore.java
        Description:
File:  edu.ucdenver.domain.user
    Name:  User.java
        Description:
File:  edu.ucdenver.server
    Name:  Main.java
        Description:
    Name:  Server.java
        Description:
    Name:  ServerTask.java
        Description:
File:  sample
    Name:  Controller.java
        Description:
    Name:  Main.java
        Description:
File:  userclient
    Name:  Userclient.java
        Description:
    Name:  userclient.fxml
        Description:
  
*******************************************************
*  Circumstances of programs
*******************************************************




*******************************************************
*  Running The Program
*******************************************************

Run edu.ucdenver.server.Main to start the server

run edu.ucdenver.domain.client.ClientTest to add a default login and a few test products

run adminclient.AdminClientMain to start the admin client

run CatalogClient.CatalogClientMain to start the catalog client
    if ClientTest is ran default login is
    Username: admin
    Email: admin@admin.org
    Password: admin3234
    otherwise you will need to add a user before you can login

