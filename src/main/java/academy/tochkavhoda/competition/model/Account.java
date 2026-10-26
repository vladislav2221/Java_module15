package academy.tochkavhoda.competition.model;

public abstract class Account {
    private final String id, fullName, login, password;
    protected Account(String id,String fullName,String login,String password){this.id=id;this.fullName=fullName;this.login=login;this.password=password;}
    public String getId(){return id;} public String getFullName(){return fullName;} public String getLogin(){return login;} public boolean passwordMatches(String value){return password.equals(value);}
}
