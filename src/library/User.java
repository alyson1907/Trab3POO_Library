package library;

public class User
{
	private String name;
	private String rg;
	private String type;
	private String banned;
	
	public String getBanned()
	{
		return banned;
	}
	public String getName()
	{
		return name;
	}
	
	public String getRg()
	{
		return rg;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setRg(String r)
	{
		rg = r;
	}
	
	public void setType(String t)
	{
		type = t;
	}
	
	public void setBanned(String b)
	{
		banned = b;
	}
}