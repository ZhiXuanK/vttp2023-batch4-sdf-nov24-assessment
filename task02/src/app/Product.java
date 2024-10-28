package app;

public class Product {
    
    int prodId;
    String title;
    Float price;
    Float rating;

    public Product(int id, String t, Float price, Float rating){
        this.prodId = id;
        this.title = t;
        this.price = price;
        this.rating = rating;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    

}
