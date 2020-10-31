package edu.ucdenver.domain.client;

import edu.ucdenver.domain.order.Order;
import edu.ucdenver.domain.user.User;
import edu.ucdenver.domain.category.Catagory;
import edu.ucdenver.domain.products.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class ClientTest {



    //LOOK AT THIS FUNCTION
    //This function shows how to get an exact product from the product class
    //I.E get a Phone object from a Product object
    public static void printer(Product printable){

        //just some basic printing stuff...
        //accessing fields from base product class...
        System.out.printf("\tname : %s\n\tid : %s\n\tbrand : %s\n\tdescription : %s\n\tdoi : %s\n\ttype : %s\n\t",
                printable.getProductName(),
                printable.getProductId(),
                printable.getBrandName(),
                printable.getDescription(),
                printable.getDoi().toString(),
                printable.getType()
        );
        System.out.printf("catagories : {");
        for(String catagory : printable.getCatagories()){
            System.out.printf(" %s",catagory);
        }
        System.out.printf(" }\n\t");



        //THIS IS HOW YOU GET SPECIFIC OBJECTS
        //SWITCH or IF .getType and match these types
        //The types are always guerented to match the type of the obj
        //Then just cast into the required object
        //I.E Book book= (Book) printable;
        switch (printable.getType()){
            case "Book":
                Book book= (Book) printable;
                System.out.printf("author : %s\n\tnumPages : %s\n\tpublication date\n",
                        book.getAuthor(),
                        book.getNumPages(),
                        book.getPublicationDate().toString());
                break;
            case "Computer":
                Computer computer = (Computer) printable;
                System.out.printf("serial : %s\n\twarrenty : %s\n\t",
                        computer.getSerial(),
                        computer.getWarrenty().toString());
                System.out.printf("specs : {");
                boolean first = true;
                for (String spec : computer.getTechnicalSpecs()){
                    if(first != true){
                        System.out.printf(",");
                    }
                    first = false;
                    System.out.print(spec);
                    System.out.printf(" }\n");
                }
                break;
            case "Electronic":
                Electronic e = (Electronic) printable;
                System.out.printf("serial : %s\n\twarrenty : %s\n\t",
                        e.getSerial(),
                        e.getWarrenty().toString());
                break;
            case "Home":
                Home home = (Home) printable;
                System.out.printf("location : %s\n",home.getLocation());
                break;
            case "Phone":
                Phone phone = (Phone) printable;
                System.out.printf("imei : %s\n\tos : %s\n",
                        phone.getImei(),
                        phone.getOs());
                break;
            default:break;
        }
        System.out.println();
    }

    public static void LookAtThisFunction(){
        //This is the default admin user.
        //In order to add an admin  user one must be an admin user
        //This should not be a problem once admins are added to the server
        User user = new User("admin@admin.org","admin","admin3234");

        //This is how to use the client api
        Client client = null;
        try {
            //connect to host, for this project host will always be 127.0.1.1
            //signup is used to specify the user is new, and should be created.
            //As you are logging in as an admin, signup should be false as the admin
            //user needs to be in the database.
            //Only admins can add admins.
            client = new Client("127.0.1.1",8080,user,false);
            //client has field isAdmin to determine if the user you connected with
            //has admin privileges
            System.out.printf("You are %s user.\n",client.isAdmin()?"a privileged":"an unprivledged");
            //These functions WILL fail if user is not admin.

            //Creates a new admin, must already be an admin to use this function
            client.createAdmin("antoheradmin@admin.org","Admin2","password12");


            //Adding catagories
            client.addCatagory("House");
            client.addCatagory("Book");
            client.addCatagory("Electronic");
            client.addCatagory("Phone");
            client.addCatagory("Computer");
            client.addCatagory("Removeable");
            client.addCatagory("RemoveableOne");
            client.addCatagory("RemoveableTwo");

            //Removing catagories
            client.removeCatagory("RemoveableOne");
            client.removeCatagory("RemoveableTwo");

            //Setting default catagory
            client.setDefaultCatagory("Book");
            client.setDefaultCatagory("House");

            //-- final default catagory would be House


            //adding products to catalog
            //Any product can be added this way
            //This is an example Book object to give to the server
            Book book = new Book("The Great Escape",
                    "yomama",
                    "It is a book.",
                    LocalDate.now(),
                    "Yo Mom",
                    LocalDate.now(),
                    9090);
            //specify product catagory as book, this will add it to the book catagory aswell
            book.getCatagories().add("Book");

            //add the product to the catalog
            client.addProductToCatalog(book);

            //this is an example phone class
            Phone phone = new Phone("IPhone",
                    "CoolPhonesInc",
                    "It is a phone.",
                    LocalDate.now(),
                    "12344",
                    LocalDate.of(2021,3,12),
                    "2141234212",
                    "unix"
                    );
            //specify product catagory as Electronic, this will add it to the Electronic catagory aswell
            phone.getCatagories().add("Electronic");

            //add the product to the catalog
            client.addProductToCatalog(phone);
            
            //this is an example electornic class
            Book fridge = new Book("Cool Fridge",
                    "CoolFridgeInc",
                    "It is a Fridge.",
                    LocalDate.now(),
                    "cool",
                    LocalDate.of(2021,3,12),
                    39
            );

            
            //add the product to the catalog
            client.addProductToCatalog(fridge);
            
            //removing products from the catalog
            client.removeProductFromCatalogByName("The Great Escape");


            //removing catagoies from product
            //--remove the catagory Electronic we gave Cool Phone earlier
            client.removeCatagoryFromProductByName("Electronic","IPhone");

            //adding catagories to product
            //--add the catagory House to the product Cool Phone we made earlier
            client.addCatagoryToProductByName("House","IPhone");



            //Non-Admin required functions
            ArrayList<Catagory> catagories = client.allCatagories();
            System.out.printf("All Catagories:\n\t");
            for( Catagory entry : catagories){
                System.out.printf("%s\n\t",entry.getName());
            }

            System.out.println();
            System.out.println("All products:");
            for (Product product : client.getProductsInDefaultCatagory()) {
                printer(product);
            }
            System.out.println();
            System.out.println("Default catagory products:");
            for (Product product : client.getProductsInDefaultCatagory()) {
                printer(product);
            }
            System.out.println();
            System.out.println("Searching for \"phone\" in products:");
            for (Product product : client.search("phone")) {
                printer(product);
            }
            System.out.println();

            Order current = client.currentOrder();
            System.out.printf("Current order id is %s\n",current.getId());
            client.addProductToOrder(phone);
            client.addProductToOrder(phone);
            client.removeProductFromOrder(phone);
            Order fin = client.finalizeOrder();
            System.out.printf("Final order id is %s\n",current.getId());

            int i =0;
            for(Order fine : client.allFinalizedOrders()){
                System.out.printf("Got order %d\n",i++);
                for(String p : fine.getProducts()){
                    System.out.printf("Got product %s\n",p);
                }
            }


            //ADMIN REQUIRED
            //shuting down server
            client.askToShutdown();

            System.out.println("Success");
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
        }
        finally {
            if(client !=null){
                client.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("127.0.1.1",8080,new User("blakemin@comcast.net","Brownbla","Bornwefw12"),true);
            System.out.println("Good");
            client.shutdown();
        }
        catch (Exception e){
            System.out.printf("Bad %s\n",e.getMessage());
        }
        LookAtThisFunction();
    }
}
