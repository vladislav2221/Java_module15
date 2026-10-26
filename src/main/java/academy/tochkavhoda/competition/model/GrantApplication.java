package academy.tochkavhoda.competition.model;
import java.util.*;
public final class GrantApplication {
 private final String id,participantId,title,description; private final Set<String> directions; private final double requestedAmount;
 public GrantApplication(String id,String participantId,String title,String description,Set<String> directions,double requestedAmount){this.id=id;this.participantId=participantId;this.title=title;this.description=description;this.directions=Collections.unmodifiableSet(new LinkedHashSet<>(directions));this.requestedAmount=requestedAmount;}
 public String getId(){return id;} public String getParticipantId(){return participantId;} public String getTitle(){return title;} public String getDescription(){return description;} public Set<String> getDirections(){return directions;} public double getRequestedAmount(){return requestedAmount;}
}
