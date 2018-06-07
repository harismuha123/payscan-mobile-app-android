package ba.edu.ibu.stu.chern0.payscan;

public class Transaction {
    private String article;
    private String seller;
    private String customer;
    private String amount;
    private String timestamp;

    public Transaction(String article, String seller, String customer, String amount, String timestamp) {
        this.article = article;
        this.seller = seller;
        this.customer = customer;
        this.amount = amount + " KM";
        this.timestamp = timestamp;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
