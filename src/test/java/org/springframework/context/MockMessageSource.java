package org.springframework.context;

import java.util.Locale;

public class MockMessageSource implements MessageSource {

	public String getMessage(MessageSourceResolvable arg0, Locale arg1)
			throws NoSuchMessageException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessage(String arg0, Object[] arg1, Locale arg2)
			throws NoSuchMessageException {
		if(arg0.equals("core.common.fullDateTimeMask"))
		{
			return "dd/MM/yyyy HH:mm:ss";
		}
		else if(arg0.equals("core.common.fullDateMask"))
		{
			return "dd/MM/yyyy";
		}
		else
		{
			if(arg1 == null)
			{
				return arg0;
			}
			else
			{
				return arg0 + " " + arg1.toString();
			}
		}
	}

	public String getMessage(String arg0, Object[] arg1, String arg2,
			Locale arg3) {
		return arg0 + " " + arg1.toString() + " " + arg2;
	}

}
