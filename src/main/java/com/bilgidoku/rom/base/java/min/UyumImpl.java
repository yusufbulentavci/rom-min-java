package com.bilgidoku.rom.base.java.min;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bilgidoku.rom.base.java.min.json.JSONArray;
import com.bilgidoku.rom.base.java.min.json.JSONObject;
import com.bilgidoku.rom.base.java.min.json.JSONTokener;
import com.bilgidoku.rom.base.min.IGlobal;
import com.bilgidoku.rom.base.min.ILogger;
import com.bilgidoku.rom.base.min.IRun;
import com.bilgidoku.rom.base.min.ISistem;
import com.bilgidoku.rom.base.min.MillisProvider;
import com.bilgidoku.rom.base.min.Portability;
import com.bilgidoku.rom.base.min.Sistem;
import com.bilgidoku.rom.base.min.err.KnownError;
import com.bilgidoku.rom.base.min.json.RenderArray;
import com.bilgidoku.rom.base.min.json.RenderBoolean;
import com.bilgidoku.rom.base.min.json.RenderNull;
import com.bilgidoku.rom.base.min.json.RenderNumber;
import com.bilgidoku.rom.base.min.json.RenderObject;
import com.bilgidoku.rom.base.min.json.RenderString;
import com.bilgidoku.rom.base.min.json.RenderValue;
import com.bilgidoku.rom.base.min.more.ClientMode;
import com.bilgidoku.rom.base.min.more.Knife;

public class UyumImpl implements Portability {

	private IRun run = new DefaultRun();
	private ISistem cur = new DefaultSistem();
	private ILogger log = new DefaultLogger();
	private IGlobal global = new DefaultGlobal();
	private MillisProvider millis = new DefaultMillis();

	public static final int NO = 0;

	private RenderBoolean btrue = new RenderBoolean(Boolean.TRUE);
	private RenderBoolean bfalse = new RenderBoolean(Boolean.FALSE);

//	private RenderNull jsonnull=new RenderNull(null);
	@Override
	public RenderString jsonValueIsString(Object ntv) {
		if (ntv == null)
			return null;
		if (ntv instanceof String)
			return new RenderString((String) ntv);
		return null;
	}

	@Override
	public RenderNumber jsonValueIsNumber(Object ntv) {
		if (ntv == null)
			return null;
		if (ntv instanceof Integer)
			return new RenderNumber((Integer) ntv);

		if (ntv instanceof Double)
			return new RenderNumber((Double) ntv);

		return null;
	}

	@Override
	public RenderObject jsonValueIsObject(Object ntv) {
		if (ntv == null)
			return null;
		if (ntv instanceof JSONObject)
			return new RenderObject(ntv);
		return null;
	}

	@Override
	public RenderNull jsonValueIsNull(Object ntv) {
		if (ntv == null)
			return RenderNull.getInstance();
		return null;
	}

	@Override
	public RenderBoolean jsonValueIsBoolean(Object ntv) {
		if (ntv == null)
			return null;
		if (!(ntv instanceof Boolean))
			return null;
		Boolean b = (Boolean) ntv;
		return jsonBooleanGetInstance(b);
	}

	@Override
	public RenderArray jsonValueIsArray(Object ntv) {
		if (ntv instanceof JSONArray)
			return new RenderArray(ntv);
		return null;
	}

	@Override
	public String jsonStringStringValue(Object ntv) {
		return (String) ntv;
	}

	@Override
	public Object jsonStringConstructor(String str) {
		return str;
	}

	@Override
	public RenderValue jsonParserParseStrict(String text) throws KnownError {
		JSONTokener tk = new JSONTokener(text);
		try {
			char c = tk.nextClean();
			tk.back();
			if (c == '[') {
				return new RenderArray(new JSONArray(text));
			}
			return new RenderObject(new JSONObject(text));
		} catch (KnownError e) {
			throw new KnownError("Json parsing exception", e);
		}
	}

	@Override
	public RenderValue jsonObjectGet(Object ntv, String key) throws KnownError {
		Object val;
		try {
			val = ((JSONObject) ntv).getNullable(key);
		} catch (KnownError e) {
			throw new KnownError("KnownError jsonobjectget", e);
		}
		return selectValue(val);
	}

	@Override
	public RenderValue jsonObjectOpt(Object ntv, String key) {
		Object val = ((JSONObject) ntv).opt(key);
		return selectValue(val);
	}

	private RenderValue selectValue(Object val) {
		if (val == null || val == RenderNull.getInstance()) {
			return RenderNull.getInstance();
		} else if (val instanceof String) {
			return new RenderString((String) val);
		} else if (val instanceof JSONObject) {
			return new RenderObject(val);
		} else if (val instanceof JSONArray) {
			return new RenderArray(val);
		} else if (val instanceof Boolean) {
			return new RenderBoolean(val);
		} else if (val instanceof Integer) {
			return new RenderNumber(val);
		}
		return null;
	}

	@Override
	public Set<String> jsonObjectKeySet(Object ntv) {
		JSONObject obj = (JSONObject) ntv;
		Set<String> s = new HashSet<String>();
		Iterator<String> it = obj.keys();
		while (it.hasNext()) {
			s.add(it.next());
		}
		return s;
	}

	@Override
	public void jsonObjectPut(Object ntv, String key, RenderValue value) {
		JSONObject obj = (JSONObject) ntv;
		try {
			obj.put(key, value.getNative());
		} catch (KnownError e) {
			Sistem.printStackTrace(e);
		}
	}

	@Override
	public int jsonObjectSize(Object ntv) {
		JSONObject obj = (JSONObject) ntv;
		return obj.length();
	}

	@Override
	public double jsonNumberDoubleValue(Object ntv) {
		if (ntv instanceof Integer)
			return ((Integer) ntv).doubleValue();
		return (Double) ntv;
	}

	@Override
	public RenderNull jsonNullGetInstance() {
		return null;
	}

	@Override
	public RenderBoolean jsonBooleanGetInstance(boolean b) {
		if (b)
			return btrue;
		return bfalse;
	}

	@Override
	public boolean jsonBooleanBooleanValue(Object ntv) {
		return (Boolean) ntv;
	}

	@Override
	public int jsonArraySize(Object ntv) {
		JSONArray obj = (JSONArray) ntv;
		return obj.length();
	}

	@Override
	public void jsonArrayAdd(Object ntv, RenderValue val) {
		JSONArray obj = (JSONArray) ntv;
		obj.put(val.getNative());
	}

	@Override
	public RenderValue jsonArrayGet(Object ntv, int i) throws KnownError {
		JSONArray obj = (JSONArray) ntv;
		try {
			if (obj.isNull(i))
				return RenderNull.getInstance();
			return selectValue(obj.get(i));
		} catch (KnownError e) {
			throw new KnownError("KnownError jsonArrayGet", e);
		}
	}

	@Override
	public void jsonArraySet(Object ntv, int i, RenderValue value) throws KnownError {
		JSONArray obj = (JSONArray) ntv;
		try {
			obj.put(i, value.ntv);
		} catch (KnownError e) {
			throw new KnownError("KnownError jsonarrayset failed", e);
		}
	}

	@Override
	public Object jsonObjectConstruct() {
		return new JSONObject();
	}

	@Override
	public Object jsonArrayConstuct() {
		return new JSONArray();
	}

	@Override
	public Object jsonNumberConstruct(Integer o) {
		return o;
	}

	@Override
	public Object jsonNumberConstruct(Double o) {
		return o;
	}

	@Override
	public void redirect(String uri) {
		throw new RuntimeException("Redirect can not be called from server");
	}

	@Override
	public RenderArray jsonArrayConstuctFromJS(Object params) {
		throw new RuntimeException("Not be called from server");
	}

	@Override
	public String[] fieldValidate(RenderObject conf, String fieldName, String value) throws KnownError {
		throw new RuntimeException("Not be called from server");
	}

	@Override
	public RenderObject jsonObjectConstuctFromJS(Object event) {
		throw new RuntimeException("Not be called from server");
	}

//	public abstract void domShow(String item, Boolean inverse)
//		throws KnownError;
//
//	public abstract void domAppend(String htmlId, String htmlStr)
//		throws KnownError;
//
//	public abstract void domSet(String htmlId, String htmlStr)
//		throws KnownError;

	@Override
	public String urlEncode(String str) {
		return Knife.urlEncode(str);
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Override
	public void tick(int millisec, Runnable runnable) {
		throw new RuntimeException("Shouldnt be called(server side)-tick");
	}

	@Override
	public void domShow(String item, Boolean inverse) throws KnownError {
		// TODO Auto-generated method stub

	}

	@Override
	public void domAppend(String htmlId, String htmlStr) throws KnownError {
		// TODO Auto-generated method stub

	}

	@Override
	public void domSet(String htmlId, String htmlStr) throws KnownError {
		// TODO Auto-generated method stub

	}

	@Override
	public void domScroll(String changeHtmlId, String refchange, boolean tobottom) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void printStackTrace(Throwable x, String extra) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public RenderArray select(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientMode codeEditor() {
		// TODO Auto-generated method stub
		return null;
	}

}
