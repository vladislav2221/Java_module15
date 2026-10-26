package academy.tochkavhoda.competition.database;

import academy.tochkavhoda.competition.model.*;
import java.util.*;

/** In-memory database. All mutation is synchronized to keep indexes consistent. */
public final class Database {
    private static final Database INSTANCE = new Database();
    private final Map<String,Account> accountsByLogin = new LinkedHashMap<>();
    private final Map<String,Account> accountsById = new LinkedHashMap<>();
    private final Map<String,String> sessions = new HashMap<>();
    private final Map<String,GrantApplication> applications = new LinkedHashMap<>();
    private final Map<String,Rating> ratings = new LinkedHashMap<>();
    private Database() {}
    public static Database getInstance(){return INSTANCE;}
    public synchronized void register(Account account){if(accountsByLogin.containsKey(account.getLogin()))throw new IllegalArgumentException("Логин уже используется");accountsByLogin.put(account.getLogin(),account);accountsById.put(account.getId(),account);}
    public synchronized Account authenticate(String login,String password){Account a=accountsByLogin.get(login);return a!=null&&a.passwordMatches(password)?a:null;}
    public synchronized void createSession(String token,Account account){sessions.put(token,account.getId());}
    public synchronized Account accountByToken(String token){String id=sessions.get(token);return id==null?null:accountsById.get(id);}
    public synchronized boolean logout(String token){return sessions.remove(token)!=null;}
    public synchronized void addApplication(GrantApplication app){applications.put(app.getId(),app);}
    public synchronized GrantApplication application(String id){return applications.get(id);}
    public synchronized List<GrantApplication> applications(){return new ArrayList<>(applications.values());}
    public synchronized boolean removeApplication(String id){if(applications.remove(id)==null)return false;ratings.entrySet().removeIf(e->e.getValue().getApplicationId().equals(id));return true;}
    public synchronized Rating rating(String applicationId,String expertId){return ratings.get(applicationId+":"+expertId);}
    public synchronized void putRating(Rating rating){ratings.put(rating.getApplicationId()+":"+rating.getExpertId(),rating);}
    public synchronized boolean removeRating(String applicationId,String expertId){return ratings.remove(applicationId+":"+expertId)!=null;}
    public synchronized List<Rating> ratings(){return new ArrayList<>(ratings.values());}
    public synchronized List<Rating> ratingsByExpert(String expertId){List<Rating> out=new ArrayList<>();for(Rating r:ratings.values())if(r.getExpertId().equals(expertId))out.add(r);return out;}
    public synchronized boolean removeAccount(String id){Account a=accountsById.remove(id);if(a==null)return false;accountsByLogin.remove(a.getLogin());sessions.entrySet().removeIf(e->e.getValue().equals(id));if(a instanceof Participant){Set<String> owned=new HashSet<>();for(GrantApplication app:applications.values())if(app.getParticipantId().equals(id))owned.add(app.getId());applications.keySet().removeAll(owned);ratings.entrySet().removeIf(e->owned.contains(e.getValue().getApplicationId()));}if(a instanceof Expert)ratings.entrySet().removeIf(e->e.getValue().getExpertId().equals(id));return true;}
    public synchronized void clear(){accountsByLogin.clear();accountsById.clear();sessions.clear();applications.clear();ratings.clear();}
    public synchronized int accountCount(){return accountsById.size();}
}
