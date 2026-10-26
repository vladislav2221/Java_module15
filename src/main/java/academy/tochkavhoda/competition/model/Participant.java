package academy.tochkavhoda.competition.model;
public final class Participant extends Account {
    private final String companyName;
    public Participant(String id,String fullName,String companyName,String login,String password){super(id,fullName,login,password);this.companyName=companyName;}
    public String getCompanyName(){return companyName;}
}
