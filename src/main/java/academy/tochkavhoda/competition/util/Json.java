package academy.tochkavhoda.competition.util;

import java.util.*;

/** Small dependency-free JSON reader/writer for the server's JSON protocol. */
public final class Json {
    private Json() {}
    public static Map<String,Object> object(String source) {
        if (source == null) throw new IllegalArgumentException("JSON-запрос не должен быть null");
        Parser p = new Parser(source); Object value = p.value(); p.space();
        if (p.i != source.length() || !(value instanceof Map)) throw new IllegalArgumentException("Ожидался JSON-объект");
        @SuppressWarnings("unchecked") Map<String,Object> result = (Map<String,Object>) value; return result;
    }
    public static String stringify(Object value) { StringBuilder b = new StringBuilder(); write(value,b); return b.toString(); }
    public static String quote(String s) { return stringify(s); }
    private static void write(Object v,StringBuilder b) {
        if(v==null){b.append("null");return;} if(v instanceof String){b.append('"'); for(char c:((String)v).toCharArray()){switch(c){case '"':b.append("\\\"");break;case '\\':b.append("\\\\");break;case '\n':b.append("\\n");break;case '\r':b.append("\\r");break;case '\t':b.append("\\t");break;default:if(c<32)b.append(String.format("\\u%04x",(int)c));else b.append(c);}}b.append('"');return;}
        if(v instanceof Number || v instanceof Boolean){b.append(v);return;} if(v instanceof Map){b.append('{');boolean first=true;for(var e:((Map<?,?>)v).entrySet()){if(!first)b.append(',');first=false;write(String.valueOf(e.getKey()),b);b.append(':');write(e.getValue(),b);}b.append('}');return;}
        if(v instanceof Iterable){b.append('[');boolean first=true;for(Object x:(Iterable<?>)v){if(!first)b.append(',');first=false;write(x,b);}b.append(']');return;} write(v.toString(),b);
    }
    private static final class Parser {
        final String s; int i; Parser(String s){this.s=s;} void space(){while(i<s.length()&&Character.isWhitespace(s.charAt(i)))i++;}
        Object value(){space();if(i>=s.length())throw bad();char c=s.charAt(i);if(c=='{')return obj();if(c=='[')return arr();if(c=='"')return str();if(s.startsWith("true",i)){i+=4;return true;}if(s.startsWith("false",i)){i+=5;return false;}if(s.startsWith("null",i)){i+=4;return null;}return num();}
        Map<String,Object> obj(){i++;Map<String,Object> m=new LinkedHashMap<>();space();if(take('}'))return m;do{space();if(i>=s.length()||s.charAt(i)!='"')throw bad();String k=str();space();need(':');if(m.containsKey(k))throw new IllegalArgumentException("Повторяющееся поле: "+k);m.put(k,value());space();if(take('}'))return m;need(',');}while(true);}
        List<Object> arr(){i++;List<Object> a=new ArrayList<>();space();if(take(']'))return a;do{a.add(value());space();if(take(']'))return a;need(',');}while(true);}
        String str(){need('"');StringBuilder b=new StringBuilder();while(i<s.length()){char c=s.charAt(i++);if(c=='"')return b.toString();if(c=='\\'){if(i>=s.length())throw bad();char e=s.charAt(i++);switch(e){case '"':case '\\':case '/':b.append(e);break;case 'n':b.append('\n');break;case 'r':b.append('\r');break;case 't':b.append('\t');break;case 'b':b.append('\b');break;case 'f':b.append('\f');break;case 'u':if(i+4>s.length())throw bad();try{b.append((char)Integer.parseInt(s.substring(i,i+4),16));}catch(NumberFormatException ex){throw bad();}i+=4;break;default:throw bad();}}else{if(c<32)throw bad();b.append(c);}}throw bad();}
        Number num(){int start=i;if(i<s.length()&&s.charAt(i)=='-')i++;while(i<s.length()&&Character.isDigit(s.charAt(i)))i++;if(i<s.length()&&s.charAt(i)=='.'){i++;while(i<s.length()&&Character.isDigit(s.charAt(i)))i++;}if(start==i)throw bad();try{return Double.valueOf(s.substring(start,i));}catch(NumberFormatException e){throw bad();}}
        boolean take(char c){if(i<s.length()&&s.charAt(i)==c){i++;return true;}return false;}void need(char c){space();if(!take(c))throw bad();}IllegalArgumentException bad(){return new IllegalArgumentException("Некорректный JSON около позиции "+i);}
    }
}
