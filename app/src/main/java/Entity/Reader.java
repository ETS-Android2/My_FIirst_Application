package Entity;

public class Reader {
    private  String rdpassword;
    private   String rdid;
    public String getRdid() {
        return rdid;
    }

    public void setRdid(String rdid) {
        this.rdid = rdid;
    }

    public String getRdpassword() {
        return rdpassword;
    }
    public void setRdpassword(String rdpassword) {
        this.rdpassword = rdpassword;
    }



    public Reader() {
    }

    @Override
    public String toString() {
        return "Reader{" +
                "rdid='" + rdid + '\'' +
                ", rdpassword='" + rdpassword + '\'' +
                '}';
    }
}
