package edu.ucdenver.domain.products;

import edu.ucdenver.domain.request.Requestable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Book extends Product implements Requestable, Serializable {



    private String author;
    private LocalDate publicationDate;
    private int numPages;
    public Book(){
        super();
        setType("Book");
        author= new String();
        publicationDate = LocalDate.now();
        numPages = 0;
    }
    public Book(
                String productName,
                String brandName,
                String description,
                LocalDate doi, //Date of Incorperation
                String author,
                LocalDate publicationDate,
                int numPages
    ){
        super(productName,brandName,description,doi);
        this.author = author;
        this.publicationDate = publicationDate;
        this.numPages = numPages;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    @Override
    public ArrayList<String> asDisplayable() {
        ArrayList<String> displayable = super.asDisplayable();
        displayable.add(String.format("Author %s",author));
        displayable.add(String.format("Publication Date %s",getPublicationDate().toString()));
        displayable.add(String.format("Number of Pages %d",numPages));
        return displayable;
    }
    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }
    public String getAuthor() {
        return author;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public int getNumPages() {
        return numPages;
    }

    @Override
    public HashMap<String, String> asRequestable() {

        HashMap<String, String> base = super.asRequestable();
        base.put("product-type","Book");


        base.put("author",this.author);
        base.put("num-pages",Integer.toString(this.numPages));
        base.put("pub-date",this.publicationDate.toString());
        Book b = new Book();
        b.fromRequestable(base);
        return base;
    }
    @Override
    public void fromRequestable(HashMap<String, String> requestable) throws IllegalArgumentException {


        super.fromRequestable(requestable);
        setType("Book");

        this.author = new String();
        this.author = argCheck(requestable,"author");
        this.numPages=0;
        String temp = new String();
        temp = argCheck(requestable,"num-pages");
        try {
            this.numPages = Integer.parseInt(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }
        temp = argCheck(requestable,"pub-date");
        this.publicationDate = LocalDate.now();

        try {
            this.publicationDate = LocalDate.parse(temp);
        }
        catch (Exception e){
            throw new IllegalArgumentException();
        }
    }
}
