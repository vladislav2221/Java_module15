package academy.tochkavhoda.competition.model;
import java.util.*;
public final class Expert extends Account {
    private final Set<String> directions;
    public Expert(String id,String fullName,Set<String> directions,String login,String password){super(id,fullName,login,password);this.directions=Collections.unmodifiableSet(new LinkedHashSet<>(directions));}
    public Set<String> getDirections(){return directions;}
}
