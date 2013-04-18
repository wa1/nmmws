package se.kth.erlundin.ninemenmorris;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


/**
 *
 * @author Erik
 */
public class Score implements KvmSerializable{
    
    private String name;
    private int wins, losses;
    
    public Score(){
        name = null;
        wins = -1;
        losses = -1;    
    }
    
    public Score(String name, int wins, int losses){
        this.name = name;
        this.wins = wins;
        this.losses = losses;    
    }

	public Object getProperty(int arg0) {
		switch(arg0)
        {
        case 0:
            return name;
        case 1:
            return wins;
        case 2:
            return losses;
        }
        
		return null;
	}

	public int getPropertyCount() {
		return 3;
	}

	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
		 switch(index)
	        {
	        case 0:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "name";
	            break;
	        case 1:
	            info.type = PropertyInfo.INTEGER_CLASS;
	            info.name = "wins";
	            break;
	        case 2:
	            info.type = PropertyInfo.INTEGER_CLASS;
	            info.name = "losses";
	            break;
	        default:break;
	        }
		
	}

	public void setProperty(int index, Object value) {
		switch(index)
        {
        case 0:
            name = value.toString();
            break;
        case 1:
            wins = Integer.parseInt(value.toString());
            break;
        case 2:
            losses = Integer.parseInt(value.toString());
            break;
        default:
            break;
        }
		
	}
	
	/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * @param wins the wins to set
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * @return the losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * @param losses the losses to set
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }

	
    @Override
	public String toString() {
		return "Score [name=" + name + ", wins=" + wins + ", losses=" + losses + "]";
	}
    
}
