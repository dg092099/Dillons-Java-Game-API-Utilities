package dillon.gameAPI.core;

public class ScriptObj {
	public ScriptObj(String id, String cont) {
		this.id = id;
		script = cont;
	}

	private String script;
	private String id;

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script
	 *            the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScriptObj)) {
			return false;
		}
		return id.equals(((ScriptObj) obj).id);
	}
}
