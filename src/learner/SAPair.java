package learner;

import java.util.List;

/**
 * State-action pair.
 * 
 * @author Alex Braithwaite
 * 
 * @param <T>
 *            Type of variables in the state. Normally Double.
 */
public class SAPair<T> {

	public int action;
	public List<T> state;

	public SAPair(List<T> s, int a) {
		this.action = a;
		this.state = s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		SAPair other = (SAPair) obj;
		if (action != other.action)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
