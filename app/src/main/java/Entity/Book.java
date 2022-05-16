package Entity;

public class Book {
    private String bkID;
    private String bkName;
    private String bkAuthor;
    private String bkPress;
    private String bkPrice;
    private String bkStatus;
    private String bkURL;
    private String bkResume;

    @Override
    public String toString() {
        return "Book{" +
                "bkID='" + bkID + '\'' +
                ", bkName='" + bkName + '\'' +
                ", bkAuthor='" + bkAuthor + '\'' +
                ", bkPress='" + bkPress + '\'' +
                ", bkPrice='" + bkPrice + '\'' +
                ", bkStatus='" + bkStatus + '\'' +
                ", bkURL='" + bkURL + '\'' +
                ", bkResume='" + bkResume + '\'' +
                '}';
    }

    public String getBkID() {
        return bkID;
    }

    public void setBkID(String bkID) {
        this.bkID = bkID;
    }

    public String getBkName() {
        return bkName;
    }

    public void setBkName(String bkName) {
        this.bkName = bkName;
    }

    public String getBkAuthor() {
        return bkAuthor;
    }

    public void setBkAuthor(String bkAuthor) {
        this.bkAuthor = bkAuthor;
    }

    public String getBkPress() {
        return bkPress;
    }

    public void setBkPress(String bkPress) {
        this.bkPress = bkPress;
    }

    public String getBkPrice() {
        return bkPrice;
    }

    public void setBkPrice(String bkPrice) {
        this.bkPrice = bkPrice;
    }

    public String getBkStatus() {
        return bkStatus;
    }

    public void setBkStatus(String bkStatus) {
        this.bkStatus = bkStatus;
    }

    public String getBkURL() {
        return bkURL;
    }

    public void setBkURL(String bkURL) {
        this.bkURL = bkURL;
    }

    public String getBkResume() {
        return bkResume;
    }

    public void setBkResume(String bkResume) {
        this.bkResume = bkResume;
    }
}
